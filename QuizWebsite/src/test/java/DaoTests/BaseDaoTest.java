package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/**
 * Abstract base class for DAO tests that provides shared setup utilities.
 */
public abstract class BaseDaoTest {

    protected static BasicDataSource basicDataSource;

    protected void setUpDataSource() throws Exception {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        executeSqlFile("database/drop.sql");
        String schema = Files.readString(Path.of("database/schema.sql"));
        schema = schema.replaceAll(" collate utf8mb4_bin", ""); // unfortunately h2 does not support collate
        executeSql(schema);
        executeSqlFile("database/test_schema.sql");
    }

    /**
     * Executes all SQL statements from a given file.
     *
     * @param filePath path to the SQL file
     */
    protected void executeSqlFile(String filePath) throws Exception {
        try (Connection conn = basicDataSource.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

            for (String sql : sqlBuilder.toString().split(";")) {
                if (!sql.trim().isEmpty() && !sql.trim().toLowerCase().startsWith("use")) {
                    stmt.execute(sql.trim());
                }
            }
        }
    }

    /**
     * Executes multiple SQL statements contained within a single SQL string.
     *
     * @param sql the full SQL script containing one or more SQL statements separated by semicolons
     * @throws Exception if a database access error occurs or any SQL statement fails to execute
     */
    protected void executeSql(String sql) throws Exception {
        try (Connection conn = basicDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String part : sql.split(";")) {
                if (!part.trim().isEmpty() && !part.trim().toLowerCase().startsWith("use")) {
                    stmt.execute(part.trim());
                }
            }
        }
    }


    /**
     * Closes the shared BasicDataSource connection pool after all tests have run.

     * This method is annotated with {@code @AfterAll} and must be static,
     * as it cleans up resources that are shared across all test instances.
     * Closing the datasource releases database connections and other resources.
     *
     * @throws Exception if an error occurs while closing the datasource
     */
    @AfterAll
    public static void tearDown() throws Exception {
        if (basicDataSource != null) {
            basicDataSource.close();
        }
    }


    /**
     * Retrieves the current total score of a quiz from the database.
     *
     * <p>This method queries the `quizzes` table to obtain the value of the
     * `quiz_score` column for the quiz with the specified ID. It is designed
     * for unit testing and deliberately avoids using other DAO methods
     * to ensure isolation and independence of tests.</p>
     *
     * @param quizId the unique identifier of the quiz whose score is to be retrieved
     * @return the total score of the quiz as stored in the database
     * @throws RuntimeException if the quiz with the specified ID does not exist
     *                          or if there is any SQL-related error during the query
     */
    protected int getQuizScore(long quizId){
        String sql = "SELECT quiz_score FROM quizzes WHERE quiz_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, quizId);

            try (ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    return rs.getInt(1);
                } else {
                    throw new RuntimeException("quiz id not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting quiz score", e);
        }
    }


    /**
     * Retrieves the number of correct answers (i.e., {@code answer_validity = true})
     * associated with a given question, as stored in the {@code num_answers} column.
     * <p>
     * This reflects the total number of valid (correct) answers for the question and is
     * typically updated automatically by the DAO logic when answers are inserted, removed,
     * or modified.
     *
     * @param questionId the ID of the question
     * @return the number of correct answers for the given question
     * @throws RuntimeException if the question ID does not exist or a database access error occurs
     */
    protected int getQuestionNumAnswers(long questionId) {
        String sql = "SELECT num_answers FROM questions WHERE question_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, questionId);

            try (ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    return rs.getInt(1);
                } else {
                    throw new RuntimeException("question id not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting question's correct answers", e);
        }
    }


}
