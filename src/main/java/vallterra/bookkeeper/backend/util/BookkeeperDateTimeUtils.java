package vallterra.bookkeeper.backend.util;

import jakarta.annotation.Nullable;
import org.jooq.types.YearToSecond;
import org.springframework.validation.annotation.Validated;

@Validated
public class BookkeeperDateTimeUtils {

    public static String formatDuration(@Nullable YearToSecond duration) {
        if (duration == null) {
            return "0Y 0M 0D 0h 0m 0s";
        }

        StringBuilder sb = new StringBuilder();

        boolean first = true;
        if (duration.getSign() < 0) {
            sb.append("-");
        }
        if (duration.getYears() > 0) {
            sb.append(duration.getYears()).append("Y ");
            first = false;
        }
        if (duration.getMonths() > 0 || !first) {
            sb.append(duration.getMonths()).append("M ");
            first = false;
        }
        if (duration.getDays() > 0 || !first) {
            sb.append(duration.getDays()).append("D ");
            first = false;
        }
        if (duration.getHours() > 0 || !first) {
            sb.append(duration.getHours()).append("h ");
            first = false;
        }
        if (duration.getMinutes() > 0 || !first) {
            sb.append(duration.getMinutes()).append("m ");
            first = false;
        }
        if (duration.getSeconds() > 0 || !first) {
            sb.append(duration.getSeconds()).append("s");
        }

        return sb.toString();
    }

}
