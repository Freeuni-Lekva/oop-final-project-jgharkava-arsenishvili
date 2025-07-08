package DaoTests;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Abstract base class for DAO tests that provides shared setup utilities.
 */
public abstract class BaseDaoTest {

    protected BasicDataSource basicDataSource;

    protected void setUpDataSource() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");
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
}
