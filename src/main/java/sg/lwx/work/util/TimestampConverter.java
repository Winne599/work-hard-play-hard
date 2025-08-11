package sg.lwx.work.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")
            .withZone(java.time.ZoneOffset.UTC); // 使用标准 UTC 时间

//    public static void main(String[] args) {
//        long unixTimestamp = 1740461703000L; // 示例时间戳
//        String formattedTime = formatTimestamp(unixTimestamp);
//        System.out.println("Formatted Time: " + formattedTime);
//    }
//
//    public static String formatTimestamp(long timestamp) {
//        Instant instant = Instant.ofEpochSecond(timestamp);
//        return FORMATTER.format(instant);
//    }

    public static void main(String[] args) {
        long millis = 1740461703000L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC"));
        String formattedDate = zonedDateTime.format(formatter);
        System.err.println("result: "+formattedDate); // 例如：2024-02-25 12:34:56+0000
    }
}

