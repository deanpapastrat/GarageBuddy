package lib;

import play.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * A collection of formatting utilities for views
 *
 * @author Dean Papastrat
 * @version 1.0.0
 */
public class Formatter {
    /**
     * Formats a date to the MMM d, YYYY pattern
     *
     * @param date date to format
     * @return a date in the pattern "MMM d, YYYY"
     */
    public static String date(Date date) {
        String format = "MMM d, yyyy";
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Formats a date to the mm/dd/yy 12:00pm pattern
     *
     * @param time time to format
     * @return a date in the pattern "mm/dd/yy 12:00pm"
     */
    public static String time(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy H:mma");
        return time.format(formatter);
    }

    /**
     * Formats an integer to be a currency string in the format $1.00
     *
     * @param number the number to convert to currency string
     * @return a string in the format $1.00
     */
    public static String currency(Integer number) {
        return currency(number.doubleValue());
    }

    /**
     * Formats a double to be a currency string in the format $1.00
     *
     * @param number the number to convert to currency string
     * @return a string in the format $1.00
     */
    public static String currency(Double number) {
        if (number == null) {
            number = 0.0;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return "$" + df.format(number);
    }

    /**
     * Encodes a given URL into a UTF-8 string so that special characters are
     * escaped.
     *
     * @param url the url to encode
     * @return the escaped url in UTF-8 format
     */
    public static String encodeURL(String url) {
        String result = null;
        try {
            result = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.debug("URL couldn't be encoded: " + url);
        }
        return result;
    }
}
