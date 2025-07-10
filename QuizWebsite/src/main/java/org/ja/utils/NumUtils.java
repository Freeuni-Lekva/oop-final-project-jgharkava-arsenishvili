package org.ja.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Utility class for number formatting operations such as formatting decimal numbers
 * and percentages in a consistent and locale-safe way.
 */
public class NumUtils {


    /**
     * A {@link DecimalFormat} instance configured to format numbers with one decimal place,
     * using a period (".") as the decimal separator (based on {@link Locale#US}).
     * Example output: 3.3 → "3.3", 3 → "3.0"
     */
    private static final DecimalFormat oneDecimalPlace = new DecimalFormat(
            "#0.0",
            DecimalFormatSymbols.getInstance(Locale.US)
    );

    /**
     * Formats a double to 1 decimal place, but omits ".0" if not needed.
     * Examples: 3.0 → "3", 3.3 → "3.3"
     */
    public static String formatOneDecimal(double value) {
        if (value % 1 == 0.0) {
            return String.valueOf((long) value); // remove .0
        } else {
            return oneDecimalPlace.format(value); // keep 1 decimal
        }
    }


    /**
     * Formats a percentage (e.g., 0.865 → "86.5%", 0.8 → "80%").
     */
    public static String formatPercentage(double value) {
        double percentage = value * 100;

        if (percentage % 1 == 0.0) {
            return ((int) percentage) + "%";
        } else {
            // Decimal part exists: use oneDecimalPlace
            return oneDecimalPlace.format(percentage) + "%";
        }
    }
}

