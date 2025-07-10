package org.ja.utils;

import java.time.format.DateTimeFormatter;

/**
 * Utility class for time-related formatting and conversions used across the application.
 */
public class TimeUtils {

    /**
     * Formatter for consistent date and time display across the site.
     * Example format: "10 Jul 2025, 16:45"
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");


    /**
     * Formats a double representing a duration in minutes into a human-readable string.
     * - If both minutes and seconds are present: "X minutes Y seconds"
     * - If only minutes: "X minutes"
     * - If only seconds: "Y seconds"
     *
     * @param minutesAsDouble the duration in minutes (e.g., 8.5 for 8 min 30 sec)
     * @return the formatted string (e.g., "8 minutes 30 seconds")
     */
    public static String formatDuration(double minutesAsDouble) {
        int totalSeconds = (int) Math.round(minutesAsDouble * 60);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        if (minutes > 0 && seconds > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min", minutes);
        } else {
            return String.format("%d sec", seconds);
        }
    }
}

