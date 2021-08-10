package com.liu.easyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

/**
 * @author lms
 * @date 2021-08-10 - 9:27
 */
public class ReadListener extends AnalysisEventListener<UserData> {

    /**
     * 从第二行开始（不读取表头），一行一行的进行读取
     * @param userData
     * @param analysisContext
     */
    @Override
    public void invoke(UserData userData, AnalysisContext analysisContext) {
        System.out.println("userData = " + userData);
    }

    /**
     * 读取表头的信息，只有一行
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("headMap = " + headMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
