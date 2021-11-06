package com.liu.yygh.controller.api;

import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.HttpRequestHelper;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.common.utils.MD5;
import com.liu.yygh.service.DepartmentService;
import com.liu.yygh.service.HospitalService;
import com.liu.yygh.service.HospitalSetService;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.DepartmentQueryVo;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
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

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleService scheduleService;

    /**
     * 删除排班信息
     * @param request
     * @return
     */
    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        // 1. 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 2. 获取查询科室的医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        // 判断医院的编码是否为空
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 3. 获取相应的签名sign
//        String hospSign = (String) paramMap.get("sign");
//        // 根据hoscode查询数据库中对应医院的签名sign,并进行加密后校验是否是同一个医院
//        String signKey = hospitalSetService.getSignKey(hoscode);
//        String encryptSign = MD5.encrypt(signKey);
//        if (!encryptSign.equals(hospSign)){
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        // 4.执行删除操作
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }


    /**
     * 查询排班信息
     * @param request
     * @return
     */
    @ApiOperation("查询排班信息")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        // 1. 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 2. 获取查询排班的医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        // 判断医院的编码是否为空
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 3. 获取分页信息的页码和每页显示的记录数
        Integer page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        Integer limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));

        // 4. 获取相应的签名sign
        String hospSign = (String) paramMap.get("sign");
        // 根据hoscode查询数据库中对应医院的签名sign,并进行加密后校验是否是同一个医院
        String signKey = hospitalSetService.getSignKey(hoscode);
        String encryptSign = MD5.encrypt(signKey);
        if (!encryptSign.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 5. 构建查询的数据对象，将需要用到的查询信息设置在对象中，方便管理参数
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        // 6. 执行查询的操作(返回Page分页信息对象)
        Page<Schedule> pageModel = scheduleService.selectPage(page, limit, scheduleQueryVo);

        return Result.ok(pageModel);
    }


    /**
     * 上传排班信息
     * @param request
     * @return
     */
    @ApiOperation("上传排班信息")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        // 1. 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 3.进行添加排班的操作
        scheduleService.save(paramMap);
        return Result.ok();
    }


    /**
     * 删除科室信息
     * @param request
     * @return
     */
    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        // 1. 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 2. 获取查询科室的医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        // 判断医院的编码是否为空
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //        // 3. 获取相应的签名sign
        //        String hospSign = (String) paramMap.get("sign");
        //        // 根据hoscode查询数据库中对应医院的签名sign,并进行加密后校验是否是同一个医院
        //        String signKey = hospitalSetService.getSignKey(hoscode);
        //        String encryptSign = MD5.encrypt(signKey);
        //        if (!encryptSign.equals(hospSign)){
        //            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        //        }

        // 4.执行删除操作
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    /**
     * 科室列表查询操作
     * @param request
     * @return
     */
    @ApiOperation("获取科室分页信息")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        // 1. 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 2. 获取查询科室的医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        // 判断医院的编码是否为空
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 3. 获取分页信息的页码和每页显示的记录数
        Integer page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        Integer limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));

        // 4. 获取相应的签名sign
        String hospSign = (String) paramMap.get("sign");
        // 根据hoscode查询数据库中对应医院的签名sign,并进行加密后校验是否是同一个医院
        String signKey = hospitalSetService.getSignKey(hoscode);
        String encryptSign = MD5.encrypt(signKey);
        if (!encryptSign.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 5. 构建查询的数据对象，将需要用到的查询信息设置在对象中，方便管理参数
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);

        // 6. 执行查询的操作(返回Page分页信息对象)
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 上传(添加)科室操作
     * @param request
     * @return
     */
    @ApiOperation("上传科室信息")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        // 获取医院前台传递过来的数据map, 并将其转为Map(String，Object)的形式
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 1. 进行参数校验,获取前台传来的hoscode和签名sign
        String hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");

        // 2. 根据hoscode查询数据库中对应医院的签名sign,并进行加密后校验是否是同一个医院
        String signKey = hospitalSetService.getSignKey(hoscode);
        String encryptSign = MD5.encrypt(signKey);
        if (!encryptSign.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 3.进行添加科室的操作
        departmentService.save(paramMap);
        return Result.ok();
    }


    /**
     * 查询医院操作
     * @param request
     * @return
     */
    @ApiOperation("查询医院信息")
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
    @ApiOperation("上传医院信息")
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
