package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.UsersDao;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UsersDaoTest{
    private UsersDao dao;
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
            dao=new UsersDao(basicDataSource);
        }
    }
    @Test
    public void testInsert() throws SQLException, NoSuchAlgorithmException {
        assertEquals(0,dao.getCount());
        User sandro=new User(12, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        dao.insertUser(sandro);
        assertEquals(1,dao.getCount());
        assertEquals(1,sandro.getId());
        User tornike=new User(12, "Tornike", "123", "2025-6-14", null,"sth.jpg", "administrator");
        dao.insertUser(tornike);
        assertEquals(2,dao.getCount());
        assertEquals(2,tornike.getId());

    }
    @Test
    public void testGetUser() throws SQLException, NoSuchAlgorithmException {
        testInsert();
        assertEquals("Sandro", dao.getUserById(1).getUsername());
        assertEquals("Tornike", dao.getUserById(2).getUsername());
        assertTrue(dao.containsUser("Sandro"));
        assertFalse(dao.containsUser("Nini"));
        assertTrue(dao.containsUser("Tornike"));
    }
    @Test
    public void testRemoveUser() throws SQLException, NoSuchAlgorithmException {
        testGetUser();
        dao.removeUserById(1);
        dao.removeUserById(4);
        dao.removeUserByName("Liza");
        assertEquals("Tornike",dao.getUserById(2).getUsername());
        assertEquals(1,dao.getCount());
        dao.removeUserById(4);
        assertEquals(1,dao.getCount());
        assertEquals(2, dao.getUserByUsername("Tornike").getId());
    }



}
