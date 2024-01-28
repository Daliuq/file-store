package im.zhaojun.zfile.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static String historyServiceFormatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(date);
    }

    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.parse(dateString);
    }
}
