package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AnswersDaoTest {
    private BasicDataSource basicDataSource;
    private AnswersDao dao;
    private QuestionDao questionsDao;
    private CategoriesDao categoriesDao;
    private UsersDao usersDao;
    private QuizzesDao quizzesDao;

    private Question qu11;
    private Question qu12;
    private Question qu13;

    private Answer a1;
    private Answer a2;
    private Answer a3;
    private Answer a4;

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
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/drop.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

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
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("database/schema.sql"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }

            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("use")) {
                    statement.execute(sql.trim());
                }
            }
        }

        questionsDao=new QuestionDao(basicDataSource);
        usersDao=new UsersDao(basicDataSource);
        categoriesDao=new CategoriesDao(basicDataSource);
        quizzesDao=new QuizzesDao(basicDataSource);

        User sandro=new User(1, "Sandro", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(sandro);
        User tornike=new User(12, "Tornike", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(tornike);
        User liza=new User(12, "Liza", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(liza);
        User nini=new User(12, "Nini", "123", "2025-6-14",null, "sth.jpg", "administrator");
        usersDao.insertUser(nini);

        Category h=new Category(1, "history");
        categoriesDao.insertCategory(h);
        Category g=new Category(2, "geography");
        categoriesDao.insertCategory(g);
        Category m=new Category(3, "maths");
        categoriesDao.insertCategory(m);
        Category p=new Category(4, "physics");
        categoriesDao.insertCategory(p);
        Category a=new Category(5, "arts");
        categoriesDao.insertCategory(a);

        Quiz q1 = new Quiz(1, "historyQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 1, 1,
                "randomized", "one-page", "final-correction");
        Quiz q2 = new Quiz(2, "geoQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 2, 1,
                "randomized", "one-page", "final-correction");
        Quiz q3 = new Quiz(3, "mathsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 3, 1,
                "randomized", "one-page", "final-correction");
        Quiz q4 = new Quiz(4, "physicsQuiz","description",
                10, 7.3, 0,
                new Timestamp(123),12, 3, 1,
                "randomized", "one-page", "final-correction");

        quizzesDao.insertQuiz(q1);
        quizzesDao.insertQuiz(q2);
        quizzesDao.insertQuiz(q3);
        quizzesDao.insertQuiz(q4);

        qu11=new Question(1, 1, "Which of these wasn't a president", "sth.jpg",
                "question-response", 50,"ordered");
        qu12=new Question(42, 1, "historyQuizQuestion2", "sth2.jpg",
                "question-response", 2,"ordered");
        qu13=new Question(1, 1, "historyQuizQuestion3", "null",
                "question-response", 50,"ordered");
        Question qu21=new Question(1, 2, "historyQuizQuestion3", "null",
                "question-response", 50,"ordered");

        questionsDao.insertQuestion(qu11);
        questionsDao.insertQuestion(qu12);
        questionsDao.insertQuestion(qu13);
        questionsDao.insertQuestion(qu21);

        a1=new Answer(12, 1, "George Washington", 1, true);
        a2=new Answer(12, 1, "G. Washington", 2, true);
        a3=new Answer(12, 1, "Joe Mama", 3, false);
        a4=new Answer(12, 2, "High Jess", 1, true);

        dao=new AnswersDao(basicDataSource);
    }

    @Test
    public void testInsert() {
        dao.insertAnswer(a1);
        dao.insertAnswer(a2);
        dao.insertAnswer(a3);
        assertEquals(3, dao.getCount());
        assertEquals("Joe Mama", dao.getAnswerById(3).getAnswerText());
        assertTrue(dao.contains(a1));
        assertFalse(dao.contains(a4));
    }

    @Test
    public void testRemove() {
        dao.insertAnswer(a1);
        dao.insertAnswer(a2);
        dao.insertAnswer(a3);
        dao.insertAnswer(a4);
        dao.removeAnswer(4);
        assertEquals(3, dao.getCount());
        assertFalse(dao.contains(a4));
    }

    @Test
    public void testQuestionAnswers(){
        dao.insertAnswer(a1);
        dao.insertAnswer(a2);
        dao.insertAnswer(a3);
        dao.insertAnswer(a4);
        ArrayList<Answer> arr=dao.getQuestionAnswers(1);
        assertEquals(3, arr.size());
        assertTrue(arr.contains(a1));
        assertTrue(arr.contains(a2));
        assertTrue(arr.contains(a3));
        assertFalse(arr.contains(a4));
        ArrayList<Answer> arr2=dao.getQuestionAnswers(2);
        assertEquals(1, arr2.size());
        assertTrue(arr2.contains(a4));
    }
}
