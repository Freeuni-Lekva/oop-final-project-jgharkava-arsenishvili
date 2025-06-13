package FilterTests;

import org.ja.model.Filters.UserNameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserNameFilterTest {
    private UserNameFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new UserNameFilter("Liza");
    }

    @Test
    public void testBuildWhereClause() {
        assertEquals("username like ?", filter.buildWhereClause());
    }

    @Test
    public void testGetOrderByClause() {
        assertEquals("true", filter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("Liza%", parameters.get(0));
    }

}
