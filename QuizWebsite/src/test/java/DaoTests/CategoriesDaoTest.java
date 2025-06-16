package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.CategoriesDao;
import org.ja.dao.UsersDao;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;


public class CategoriesDaoTest {
    private CategoriesDao dao;
    private BasicDataSource basicDataSource;
    @BeforeEach
    public void setUp() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa"); // h2 username
        basicDataSource.setPassword(""); // h2 password

        try (
                Connection connection = basicDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Read SQL file
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/drop.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            // Split and execute SQL commands (if there are multiple)
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    statement.execute(sql.trim());
                }
            }
        }
        try (
                Connection connection = basicDataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Read SQL file
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/schema.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            // Split and execute SQL commands (if there are multiple)
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
            dao=new CategoriesDao(basicDataSource);
        }
    }
    @Test
    public void testInsert() throws SQLException {
        assertEquals(0, dao.getCount());
        Category h=new Category(42, "history");
        dao.insertCategory(h);
        assertEquals(1, dao.getCount());
        assertEquals(1, h.getCategoryId());
        Category same=new Category(15, "history");
        assertTrue(dao.containsCategory("history"));
        dao.insertCategory(same);
        assertEquals(1, dao.getCount());
        Category g=new Category(42, "geography");
        dao.insertCategory(g);
        assertEquals(2, dao.getCount());
        assertEquals(2, g.getCategoryId());
    }
    @Test
    public void testRemove() throws SQLException {
        testInsert();
        assertEquals("history", dao.getCategoryById(1).getCategoryName());
        assertEquals("geography", dao.getCategoryById(2).getCategoryName());
        dao.removeCategory(new Category(1, "history"));
        assertEquals(1, dao.getCount());
        assertEquals(null, dao.getCategoryById(1));
        Category m=new Category(42, "Maths");
        dao.insertCategory(m);
        assertEquals(2, dao.getCount());
        assertEquals(3, m.getCategoryId());
        assertEquals("Maths", dao.getCategoryById(3).getCategoryName());
        assertEquals(3, dao.getCategoryByName("Maths").getCategoryId());
    }




}
