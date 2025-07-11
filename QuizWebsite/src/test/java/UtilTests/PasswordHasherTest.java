package UtilTests;

import org.ja.utils.PasswordHasher;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link PasswordHasher} class.
 */
public class PasswordHasherTest {

    @Test
    public void testGetSaltCorrect(){
        String salt = PasswordHasher.getSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
    }

    @Test
    public void testHashPasswordSame() throws NoSuchAlgorithmException {
        String password = "password123";
        String salt = PasswordHasher.getSalt();
        String hash1 = PasswordHasher.hashPassword(password, salt);
        String hash2 = PasswordHasher.hashPassword(password, salt);
        assertEquals(hash1, hash2);
    }

    @Test
    public void testHashPasswordDifferent() throws NoSuchAlgorithmException {
        String password = "password123";
        String salt1 = PasswordHasher.getSalt();
        String salt2 = PasswordHasher.getSalt();
        String hash1 = PasswordHasher.hashPassword(password, salt1);
        String hash2 = PasswordHasher.hashPassword(password, salt2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testVerifyPassword() throws NoSuchAlgorithmException {
        String password = "password123";
        String salt = PasswordHasher.getSalt();
        String hash = PasswordHasher.hashPassword(password, salt);
        assertTrue(PasswordHasher.verifyPassword(password, hash, salt));
        assertFalse(PasswordHasher.verifyPassword("password", hash, salt));
    }
}
