package com.liu.yygh.controller.api;

import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.HttpRequestHelper;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.common.utils.MD5;
import com.liu.yygh.service.HospitalService;
import com.liu.yygh.service.HospitalSetService;
import com.lms.yygh.model.hosp.Hospital;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-11 - 15:20
 */

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院操作
     * @param request
     * @return
     */
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        // 获取医院管理传递的参数信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 获取医院系统传递过来的签名，签名已经进行了MD5加密
        String hospSign = (String) paramMap.get("sign");

        // 根据传递过来的医院编码，查询数据库，查看相应的签名信息
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 对数据库查询签名进行 MD5 加密处理
        String encryptSign = MD5.encrypt(signKey);

        // 判断是否是同一个签名的sign值
        if (!encryptSign.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 根据编号进行查询医院信息
        Hospital hospital = hospitalService.getByHoscode(hoscode);
//        System.out.println("hospital = " + hospital.toString());
        return Result.ok(hospital);
    }


    /**
     * 上传医院的接口设置
     * @param request 因为参数是通过post提交过来的数据信息，所以通过
     *                Request来获取参数信息集合
     * @return
     */
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        // 1.获取前台传过来的数据信息
        Map<String, String[]> requestMap = request.getParameterMap();
        // 将Map<String, String[]>转为Map<String, Object>
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(requestMap);

        // 参数校验
        // 获取医院系统传递过来的签名，签名已经进行了MD5加密
        String hospSign = request.getParameter("sign");

        // 根据传递过来的医院编码，查询数据库，查看相应的签名信息
        String hoscode = (String) resultMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 将从数据库中查询得到的sign值进行MD5加密处理
        String encryptSign = MD5.encrypt(signKey);

        // 判断是否是同一个签名的sign值
        if (!encryptSign.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 图片乱码的处理：因为图片在传输的过程中，
        // " + " 转换成为了 " ", 因此我们要转换回来
        String logoData = (String) resultMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        //
        resultMap.put("logoData", logoData);

        // 2.调用service的方法进行保存
        hospitalService.save(resultMap);
        return Result.ok();
    }









}
