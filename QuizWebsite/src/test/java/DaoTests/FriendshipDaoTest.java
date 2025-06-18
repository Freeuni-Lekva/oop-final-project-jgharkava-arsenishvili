package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Friendship;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;


public class FriendshipDaoTest {

    private BasicDataSource basicDataSource;
    private FriendShipsDao dao;
    private QuestionDao questionsDao;
    private CategoriesDao categoriesDao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;
    Question qu11;
    Question qu12;
    Question qu13;
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
            usersDao=new UsersDao(basicDataSource);
            finishSetUp();
            dao=new FriendShipsDao(basicDataSource);
        }
    }
    private void finishSetUp() throws SQLException, NoSuchAlgorithmException {
        User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike=new User(12, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza=new User(12, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini=new User(12, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);
    }
    private Friendship f1;
    private Friendship f2;
    private Friendship f3;
    private Friendship f4;
    private Friendship f5;
    @Test
    public void testInsert() {
        f1=new Friendship(1,2, null,"pending");
        f2=new Friendship(2,3, null,"pending");
        f3=new Friendship(1,4, null,"pending");
        dao.insertFriendShip(f1);
        dao.insertFriendShip(f2);
        dao.insertFriendShip(f3);
        assertEquals(3, dao.getCount());
        assertTrue(dao.contains(f1));
        dao.insertFriendShip(f1);
        assertEquals(3, dao.getCount());
    }
    @Test
    public void testRemove() {
        testInsert();
        assertTrue(dao.contains(1,4));
        assertEquals(3, dao.getCount());
        dao.removeFriendShip(1, 4);
        assertEquals(2, dao.getCount());
    }
    @Test
    public void testUpdate(){
        testInsert();
        f4=new Friendship(1,3, null,"friends");
        f5=new Friendship(2,4, null,"friends");
        dao.insertFriendRequest(f4);
        dao.insertFriendRequest(f5);

        assertEquals("pending", f4.getFriendshipStatus());
        dao.acceptFriendRequest(f1);
        assertEquals("friends", f1.getFriendshipStatus());
    }
    @Test
    public void testDelete(){
        testInsert();
        dao.removeFriendShip(f1);
        assertEquals(2, dao.getCount());
        dao.removeFriendShip(1, 4);
        assertEquals(1, dao.getCount());
    }
    @Test
    public void testGetFriends(){
        f1=new Friendship(1,2, null,"pending");
        f2=new Friendship(2,3, null,"friends");
        f3=new Friendship(1,4, null,"friends");
        dao.insertFriendShip(f1);
        dao.insertFriendShip(f2);
        dao.insertFriendShip(f3);
        f4=new Friendship(1,3, null,"friends");
        f5=new Friendship(2,4, null,"friends");
        dao.insertFriendShip(f4);
        dao.insertFriendShip(f5);
        dao.acceptFriendRequest(f1);
        ArrayList<Friendship> arr=dao.getFriends(2);
        assertEquals(3, arr.size());
        ArrayList<Friendship> arr2=dao.getFriends(1);
        assertEquals(3, arr2.size());
    }
    @Test
    public void testGetFriendRequests(){
        testInsert();

        f4=new Friendship(1,3, null,"pending");
        f5=new Friendship(2,4, null,"pending");
        dao.insertFriendShip(f4);
        dao.insertFriendShip(f5);
        ArrayList<Friendship> arr=dao.getFriendRequests(2);
        assertEquals(1, arr.size());
        ArrayList<Friendship> arr2=dao.getFriendRequests(3);
        assertEquals(2, arr2.size());
    }
}
