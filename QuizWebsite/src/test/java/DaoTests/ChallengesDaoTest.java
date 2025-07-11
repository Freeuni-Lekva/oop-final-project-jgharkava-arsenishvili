package DaoTests;

import org.ja.dao.*;
import org.ja.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the ChallengesDao class using an in-memory H2 database.
 */
public class ChallengesDaoTest extends BaseDaoTest{
    private ChallengesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new ChallengesDao(basicDataSource);
    }

    @Test
    public void testInsertChallenge() {
        Challenge challenge = new Challenge(0, 7, 6, 4); // Ani (7) -> Gio (6) for quiz_id 4

        boolean inserted = dao.insertChallenge(challenge);
        assertTrue(inserted);
        assertTrue(challenge.getChallengeId() > 0);
    }

    @Test
    public void testRemoveChallenge() {
        List<Challenge> challenges = dao.challengesAsReceiver(5);
        assertFalse(challenges.isEmpty());

        Challenge challenge = challenges.get(0);
        boolean removed = dao.removeChallenge(challenge.getChallengeId());
        assertTrue(removed);

        List<Challenge> updated = dao.challengesAsReceiver(5);
        assertFalse(updated.contains(challenge));
    }

    @Test
    public void testChallengesAsReceiver() {
        List<Challenge> receiverChallenges = dao.challengesAsReceiver(5); // Mariam <- quiz_id 5 from Ani
        assertEquals(1, receiverChallenges.size());
        Challenge challenge = receiverChallenges.get(0);
        assertEquals(5, challenge.getQuizId());
        assertEquals(7, challenge.getSenderUserId());
    }

    @Test
    public void testChallengesForUserWithNoEntries() {
        List<Challenge> receiver = dao.challengesAsReceiver(1225);
        assertTrue(receiver.isEmpty());
    }
}
