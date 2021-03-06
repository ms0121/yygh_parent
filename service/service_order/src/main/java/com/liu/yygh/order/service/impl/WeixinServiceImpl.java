package com.liu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.liu.yygh.order.service.OrderService;
import com.liu.yygh.order.service.PaymentInfoService;
import com.liu.yygh.order.service.RefundInfoService;
import com.liu.yygh.order.service.WeixinService;
import com.liu.yygh.order.utils.ConstantPropertiesUtils;
import com.liu.yygh.order.utils.HttpClient;
import com.lms.yygh.enums.PaymentTypeEnum;
import com.lms.yygh.enums.RefundStatusEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.order.PaymentInfo;
import com.lms.yygh.model.order.RefundInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lms
 * @date 2021-09-02 - 9:32
 */

@Service
public class WeixinServiceImpl implements WeixinService {

    @Resource
    private OrderService orderService;

    @Resource
    private PaymentInfoService paymentService;

    // 用于实现订单支付在两小时内有效
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RefundInfoService refundInfoService;


    // 生成微信支付二维码
    @Override
    public Map createNative(Long orderId) {
        try {
            // 查询当前redis中是否存在该订单数据信息
            Map redisMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if (redisMap != null) {
                return redisMap;
            }

            // 1.根据orderId获取订单信息
            OrderInfo order = orderService.getById(orderId);
            // 2.向支付记录表添加记录信息
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
            // 3.设置参数，
            // 把参数转换为xml格式，使用商户的key进行加密设置
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = order.getReserveDate() + "就诊" + order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1"); // 测试方便所以设置金额为0.01元
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");  // 微信扫一扫支付

            // 4. 调用微信生成二维码接口
            // 使用HTTPClient来根据传入进来的url访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // client设置相关的参数，首先传入的数据为xml，所以将map转为xml，因为微信接口使用的是https,
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 5.发送请求之后，微信返回相关的数据信息，格式为xml
            String xml = client.getContent();
            // 将xml转为map集合数据，然后传到前台
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            // 6.封装返回的数据信息
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));

            // 此时resultMap.get("result_code")不为空，将其放入到redis中
            if(null != resultMap.get("result_code")) {
                // 设置键值对和过期时间为2小时
                redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    // 调用微信接口实现支付状态的查询
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            // 1.根据orderId查询订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            // 2.封装提交的参数
            HashMap paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            // 订单编号
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            // 微信接口随机生成的字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            // 3.向微信地址发送请求，设置请求内容信息
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            // 将map集合转为xml格式数据，并使用商户的key进行加密设置
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 4.得到微信接口返回的xml数据信息，并将其转为map集合
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            // 5.把接口数据进行返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行退款的操作
     * @param orderId
     * @return
     */
    @Override
    public Boolean refund(Long orderId) {
        try {
            // 1.调用支付订单接口查询订单信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
            // 2.调用退款记录接口进行保存的操作
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            // 3.判断是否已经完成了退款的操作
            if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()){
                return true;
            }
            // 否则封装退款信息
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("appid",ConstantPropertiesUtils.APPID);       //公众账号ID
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER);   //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
//           paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
//           paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
            // 将封装的map信息转为xml的格式
            String paramXml = WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY);
            // 设置调用的微信退款订单接口
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);
            // 设置证书信息
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.CERT);
            // 执行调用
            client.post();

            // 接收调用返回的数据信息,并将其转为map数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))){
                // 修改退款记录的信息
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                // 更新为已退款
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}







