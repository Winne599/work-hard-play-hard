package sg.lwx.work.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author lianwenxiu
 */
public class TimestampChecker {

    public static boolean isTimestampInRange(long timestamp) {
        // 转换为Instant对象
        Instant instant = Instant.ofEpochSecond(timestamp);

        // 设置起始和结束时间
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 12, 31, 23, 59, 59);

        // 转换为Instant对象
        Instant startInstant = startDateTime.toInstant(ZoneOffset.UTC);
        Instant endInstant = endDateTime.toInstant(ZoneOffset.UTC);

        // 比较时间戳是否在指定范围内
        return !instant.isBefore(startInstant) && !instant.isAfter(endInstant);
    }

    public static void main(String[] args) {
        // 举例：判断时间戳 1640995200（2023年1月1日0时0分0秒）是否在指定范围内
        long timestampToCheck = 1700535238;

        if (isTimestampInRange(timestampToCheck)) {
            System.out.println("时间戳在指定范围内。aaaaa");
        } else {
            System.out.println("时间戳不在指定范围内。qqqq");
        }
    }
}

