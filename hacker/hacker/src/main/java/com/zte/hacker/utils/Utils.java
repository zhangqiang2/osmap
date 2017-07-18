package com.zte.hacker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 10069681 on 2017/6/30.
 */
public class Utils {
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }
}
