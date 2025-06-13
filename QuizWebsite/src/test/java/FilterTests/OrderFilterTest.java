package FilterTests;

import org.ja.model.Filters.OrderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderFilterTest {
    private OrderFilter filterAsc;
    private OrderFilter filterDesc;

    @BeforeEach
    public void setUp() {
        this.filterAsc = new OrderFilter("price", OrderFilter.INCREASING);
        this.filterDesc = new OrderFilter("price", OrderFilter.DECREASING);
    }

    @Test
    public void testBuildOrderByClause() {
        assertEquals("price desc", filterDesc.buildOrderByClause());
        assertEquals("price asc", filterAsc.buildOrderByClause());
    }

    @Test
    public void testBuildWhereClause() {
        assertEquals("true", filterAsc.buildWhereClause());
        assertEquals("true", filterDesc.buildWhereClause());
    }

    @Test
    public void testGetParameters() {
        assertTrue(filterAsc.getParameters().isEmpty());
        assertTrue(filterDesc.getParameters().isEmpty());
    }

    @Test
    public void testIllegalMonotonicity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderFilter("price", 25));
    }
}
