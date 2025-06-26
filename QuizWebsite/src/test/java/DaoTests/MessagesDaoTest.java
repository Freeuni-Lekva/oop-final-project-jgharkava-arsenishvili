package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.OtherObjects.*;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class MessagesDaoTest {

    private BasicDataSource basicDataSource;
    private MessageDao dao;
    private UsersDao usersDao;
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

            dao=new MessageDao(basicDataSource);
            finishSetup();
        }
    }
    private void finishSetup() throws SQLException, NoSuchAlgorithmException {
        usersDao=new UsersDao(basicDataSource);
        User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike=new User(2, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza=new User(3, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini=new User(4, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);

        m1=new Message(2,1,2,"hello tornike", null);
        m2=new Message(3,1,3,"hello liza", null);
        m3=new Message(4,2,4,"hello nini", null);
        m4=new Message(5,3,4,"hello administrator", null);

    }
    private Message m1;
    private Message m2;
    private Message m3;
    private Message m4;
    private Message m5;

    @Test
    public void testInsert() {
        dao.insertMessage(m1);
        assertTrue(dao.contains(m1));
        assertFalse(dao.contains(m2));
        dao.insertMessage(m2);
        dao.insertMessage(m3);
        dao.insertMessage(m4);
        assertEquals(4, dao.getCount());
    }

    @Test
    public void testRemove() {
        dao.insertMessage(m1);
        dao.insertMessage(m2);
        dao.insertMessage(m3);
        dao.insertMessage(m4);
        dao.removeMessage(4);
        assertEquals(3, dao.getCount());
        assertFalse(dao.contains(m4));
        dao.removeMessage(4);
        assertEquals(3, dao.getCount());
    }
    @Test
    public void testMessagesForUser(){
        dao.insertMessage(m1);
        dao.insertMessage(m2);
        dao.insertMessage(m3);
        dao.insertMessage(m4);
        ArrayList<Message> arr=dao.getMessagesForUserSorted(4);
        assertEquals(2, arr.size());
        ArrayList<Message> arr2=dao.getMessagesForUserSorted(1);
        assertEquals(0, arr2.size());
        ArrayList<Message> arr3=dao.getMessagesForUserSorted(2);
        assertEquals(1, arr3.size());
        assertTrue(arr3.contains(m1));
    }
    @Test
    public void testMutualMessages(){
        dao.insertMessage(m1);
        dao.insertMessage(m2);
        dao.insertMessage(m3);
        dao.insertMessage(m4);
        m5=new Message(2,2,1,"hello sandro", null);
        dao.insertMessage(m5);
        ArrayList<Message> arr=dao.getMutualMessagesSorted(1, 2);
        assertEquals(2, arr.size());
        ArrayList<Message> arr2=dao.getMutualMessagesSorted(2, 1);
        assertEquals(arr, arr2);

        ArrayList<Message> arr3=dao.getMutualMessagesSorted(2, 3);
        assertEquals(0, arr3.size());
        ArrayList<Message> arr4=dao.getMutualMessagesSorted(2, 4);
        assertEquals(1, arr4.size());

    }
}
