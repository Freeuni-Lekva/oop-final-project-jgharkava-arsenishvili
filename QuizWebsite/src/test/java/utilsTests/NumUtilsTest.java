package utilsTests;

import org.ja.utils.NumUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the {@link NumUtils} class.
 */
public class NumUtilsTest {

    @Test
    public void testFormatOneDecimalWholeNumbers() {
        assertEquals("0", NumUtils.formatOneDecimal(0.0));
        assertEquals("3", NumUtils.formatOneDecimal(3.0));
        assertEquals("-7", NumUtils.formatOneDecimal(-7.0));
    }

    @Test
    public void testFormatOneDecimalWithDecimalPart() {
        assertEquals("3.3", NumUtils.formatOneDecimal(3.3));
        assertEquals("0.5", NumUtils.formatOneDecimal(0.5));
        assertEquals("-2.8", NumUtils.formatOneDecimal(-2.8));
    }

    @Test
    public void testFormatPercentageWholeNumbers() {
        assertEquals("0%", NumUtils.formatPercentage(0.0));
        assertEquals("100%", NumUtils.formatPercentage(1.0));
        assertEquals("80%", NumUtils.formatPercentage(0.8));
        assertEquals("25%", NumUtils.formatPercentage(0.25));
    }

    @Test
    public void testFormatPercentageWithDecimalPart() {
        assertEquals("86.5%", NumUtils.formatPercentage(0.865));
        assertEquals("99.9%", NumUtils.formatPercentage(0.999));
        assertEquals("12.3%", NumUtils.formatPercentage(0.123));
    }
}
