package geist.hegel.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateRangeUtil {
    public static LocalDateTime monthsAgoStart(int months) {
        LocalDateTime now = LocalDateTime.now();
        return now.minusMonths(months).with(LocalTime.MIN);
    }
}
