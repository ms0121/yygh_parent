package com.lms.yygh.common.exception;

import com.lms.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lms
 * @date 2021-07-23 - 20:15
 * 这是一个增强的 Controller。使用这个 Controller ，可以实现三个方面的功能：
     * 1.全局异常处理；
     * 2.全局数据绑定
     * 3.全局数据预处理
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler 注解用来指明异常的处理类型，即出现任何类型都会调用这个方法
     * @ResponseBody: 因为这里没有使用restcontroller，所以json的方式返回数据
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }


    /**
     * 自定义异常处理类
     * @param e
     * @return
     */
    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e){
        e.printStackTrace();
        return Result.fail();
    }
}
