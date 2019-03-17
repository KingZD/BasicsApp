package com.project.jaijite.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd,HHmmss");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }
}
