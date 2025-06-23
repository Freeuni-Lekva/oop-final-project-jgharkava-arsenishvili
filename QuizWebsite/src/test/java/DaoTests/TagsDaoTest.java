package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.CategoriesDao;
import org.ja.dao.TagsDao;
import org.ja.dao.UsersDao;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
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

public class TagsDaoTest {
    private TagsDao dao;
    private BasicDataSource basicDataSource;
    @BeforeEach
    public void setUp() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

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
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
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
            dao=new TagsDao(basicDataSource);
        }
    }
    @Test
    public void testInsert() throws Exception {
        assertEquals(0, dao.getCount());
        Tag e=new Tag(12, "easy");
        dao.insertTag(e);
        assertEquals(1, dao.getCount());
        assertEquals(1, e.getTagId());
        Tag m=new Tag(15, "medium");
        dao.insertTag(m);
        assertEquals(2, dao.getCount());
        assertEquals("medium", dao.getTagById(2).getTagName());
        assertEquals(2, dao.getTagByName("medium").getTagId());
        assertTrue(dao.containsTag("medium"));
        assertTrue(dao.containsTag("easy"));
        assertFalse(dao.containsTag("hard"));
    }
    @Test
    public void testRemove() throws Exception {
        testInsert();
        Tag h=new Tag(12, "hard");
        dao.insertTag(h);
        dao.removeTag(h);
        assertEquals(2, dao.getCount());
        assertFalse(dao.containsTag("hard"));
        assertFalse(dao.containsTag(3));
    }



}

