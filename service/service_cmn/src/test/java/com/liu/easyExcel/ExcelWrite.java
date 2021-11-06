package com.liu.easyExcel;

import com.alibaba.excel.EasyExcel;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-10 - 9:12
 */
public class ExcelWrite {
    public static void main(String[] args) {

        // 指定要写入文件的路径信息
        String filename = "C:\\Users\\Administrator\\Desktop\\ghost1\\01.xlsx";

        List<UserData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData userData = new UserData();
            userData.setUid(i);
            userData.setUsername("lucy_" + i);
            list.add(userData);
        }


        // 调用方法实现向Excel中进行写入数据信息
        EasyExcel.write(filename, UserData.class)
                .sheet("用户信息")
                .doWrite(list);

    }
}
