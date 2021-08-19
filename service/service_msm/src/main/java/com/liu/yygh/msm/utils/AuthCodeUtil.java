package com.liu.yygh.msm.utils;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.apache.http.conn.util.PublicSuffixList;

import java.util.HashMap;
import java.util.Set;

/**
 * @author lms
 * @date 2021-08-19 - 15:41
 */
public class AuthCodeUtil {
//    public static void main(String[] args) {
//        authCode("18811338693");
//    }

    // 将生成的验证码设置在静态变量code中
    public static String code;

    public static void authCode(String phoneNumber) {

        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf07087b52c64e017b5d4e1b9102fe";
        String accountToken = "4b27fe4ed2b144c181f2c9c24fd9fdd0";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf07087b52c64e017b5d4e264d0305";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        //随机生成6位数字验证码
        code = String.valueOf(Math.random()).substring(2, 8);
        System.out.println("随机生成的6位验证码是： " + code);
        String to = phoneNumber;
        String templateId = "1";
        String[] datas = {code, "2"};
//        String subAppend="1234";  //可选 扩展码，四位数字 0~9999
//        String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }

    }
}
