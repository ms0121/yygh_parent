package com.liu.easyExcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author lms
 * @date 2021-08-10 - 9:10
 */

@Data
public class UserData {

    // value: 用于设置excel表中当前这一列的信息
    // index：指定excel表中的哪一列
    @ExcelProperty(value = "用户编号", index = 0)
    private Integer uid;

    @ExcelProperty(value = "用户名称", index = 1)
    private String username;
}
