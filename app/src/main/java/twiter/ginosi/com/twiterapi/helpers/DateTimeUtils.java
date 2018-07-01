package twiter.ginosi.com.twiterapi.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

/**
 * Created by Davit Galstyan on 7/1/18.
 */
public class DateTimeUtils {
    private static Date parseTwitterUTC(String date) throws java.text.ParseException {

        String twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";

        // Important note. Only ENGLISH Locale works.
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        return sf.parse(date);
    }

    public static String getDisplayDate(String createdAt) {
        try {
            Date date = parseTwitterUTC(createdAt);
            Date currentDate = new Date();
            int diffInMinutes = (int) ((currentDate.getTime() - date.getTime()) / (1000 * 60));
            if (diffInMinutes > 59) {
                return getPrettyFullDate(date);
            } else {
                return getPrettyDate(date);
            }
        } catch (ParseException e) {
            return createdAt;
        }
    }

    private static String getPrettyFullDate(Date date) {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        int diffInDays = sdf.format(currentDate).compareTo(sdf.format(date));

        if (diffInDays > 0) {
            if (diffInDays > 300) {
                return getShortDateYear(date);
            } else {
                return getShortDate(date);
            }
        } else {
            return getTimeOnly(date);
        }
    }

    private static String getShortDateYear(Date date) {
        SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yy, hh:mm aa");
        return formatted.format(date);
    }

    private static String getShortDate(Date date) {
        SimpleDateFormat formatted = new SimpleDateFormat("dd MMM, hh:mm aa");
        return formatted.format(date);
    }

    private static String getTimeOnly(Date date) {
        SimpleDateFormat formatted = new SimpleDateFormat("hh:mm aa");
        return formatted.format(date);
    }

    private static String getPrettyDate(Date createdAt) {
        return getPrettyDate(createdAt, new Date());
    }

    private static String getPrettyDate(Date olderDate, Date newerDate) {

        String result;

        int diffInDays = (int) ((newerDate.getTime() - olderDate.getTime()) / (1000 * 60 * 60 * 24));
        if (diffInDays > 365) {
            SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yy");
            result = formatted.format(olderDate);
        } else if (diffInDays > 0) {
            if (diffInDays == 1) {
                result = "1d";
            } else if (diffInDays < 8) {
                result = diffInDays + "d";
            } else {
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM");
                result = formatted.format(olderDate);
            }
        } else {
            int diffInHours = (int) ((newerDate.getTime() - olderDate.getTime()) / (1000 * 60 * 60));
            if (diffInHours > 0) {
                if (diffInHours == 1) {
                    result = "1h";
                } else {
                    result = diffInHours + "h";
                }
            } else {
                int diffInMinutes = (int) ((newerDate.getTime() - olderDate
                        .getTime()) / (1000 * 60));
                if (diffInMinutes > 0) {
                    if (diffInMinutes == 1) {
                        result = "1m";
                    } else {
                        result = diffInMinutes + "m";
                    }
                } else {
                    int diffInSeconds = (int) ((newerDate.getTime() - olderDate
                            .getTime()) / (1000));
                    if (diffInSeconds < 5) {
                        result = "now";
                    } else {
                        result = diffInSeconds + "s";
                    }
                }
            }
        }

        return result;
    }
}
