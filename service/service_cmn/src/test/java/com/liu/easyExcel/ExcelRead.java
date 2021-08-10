package com.liu.easyExcel;

import com.alibaba.excel.EasyExcel;

/**
 * @author lms
 * @date 2021-08-10 - 9:26
 * 使用easyexcel实现读取的操作: 必须继承easyExcel的监听器:AnalysisEventListener
 */
public class ExcelRead {
    public static void main(String[] args) {
        String filename = "C:\\Users\\Administrator\\Desktop\\ghost1\\01.xlsx";

        // 实现读取的操作,文件路径，类，以及监听器
        EasyExcel.read(filename, UserData.class, new ReadListener()).sheet().doRead();
    }
}
