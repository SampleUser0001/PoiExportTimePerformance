package ittimfn.performance.poi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static String getLocalDateTime() {
        return dateFormat.format(LocalDateTime.now());
    }

    

    private Util(){}
}
