package DaoTests;

import org.ja.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnswersDaoTest extends BaseDaoTest{
    private AnswersDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        executeSqlFile("database/drop.sql");
        executeSqlFile("database/schema.sql");

        //TODO: insert information into db

        dao = new AnswersDao(basicDataSource);
    }

}
