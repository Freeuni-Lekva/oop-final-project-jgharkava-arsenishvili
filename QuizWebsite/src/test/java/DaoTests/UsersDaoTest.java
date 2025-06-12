package DaoTests;

//import junit.framework.TestCase;
import junit.framework.TestCase;
import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.UsersDao;
import org.ja.model.user.User;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class UsersDaoTest extends TestCase {
    private UsersDao dao;
    private BasicDataSource dataSource;
    public void setUp() throws Exception {
        super.setUp();
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/b1");
        dataSource.setUsername("root");
        dataSource.setPassword("Georgia!@43211234");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        runSqlScript("database/drop.sql");
        dao = new UsersDao(dataSource);
    }
    public void testInsert() throws SQLException {
        User user=new User(21, "Liz", "123456", "2025-05-11", "sth.jpg", "administrator");
        dao.insertUser(user);
        System.out.println(user.getId());
        User retUser=dao.getUserById(1);
        System.out.println(retUser.getUserName());

        User user2=new User(21, "Sandro", "1234567", "2025-05-11", "sth.jpg", "administrator");
        dao.insertUser(user2);
        System.out.println(user2.getId());
        User retUser2=dao.getUserById(2);
        if(retUser2!=null){
            System.out.println(retUser2.getUserName());
        }
        System.out.println(dao.getCount());
        dao.removeUserById(1);
        System.out.println(dao.getCount());

    }

    private void runSqlScript(String filePath) throws Exception {
        String content = Files.lines(Paths.get(filePath))
                .collect(Collectors.joining("\n"));

        // Remove /* ... */ style comments
        content = content.replaceAll("/\\*.*?\\*/", "");  // Regex removes multiline comments

        // Optionally also strip -- style comments
        content = content.replaceAll("(?m)^--.*?$", "");

        String[] statements = content.split(";");
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String raw : statements) {
                String sql = raw.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }



}
