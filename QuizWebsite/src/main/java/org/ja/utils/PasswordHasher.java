package org.ja.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for securely hashing passwords with a salt using SHA-256.
 * <p>
 * Provides methods to generate a new salt, hash a password with the salt,
 * and verify if a given password matches the hashed value.
 * </p>
 */
public class PasswordHasher {

    /**
     * Generates a new random salt encoded in Base64.
     *
     * @return a Base64-encoded random salt string
     */
    public static String getSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes the given password with the provided salt using SHA-256 algorithm.
     * The salt is decoded from Base64 before use.
     *
     * @param password the plaintext password to hash
     * @param salt the Base64-encoded salt to use in hashing
     * @return the Base64-encoded hashed password
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(Base64.getDecoder().decode(salt));
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }


    /**
     * Verifies whether the given plaintext password, when hashed with the given salt,
     * matches the stored hashed password.
     *
     * @param password the plaintext password to verify
     * @param hashed the Base64-encoded hashed password to compare against
     * @param salt the Base64-encoded salt used for hashing
     * @return true if the password matches the hash, false otherwise
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static boolean verifyPassword(String password, String hashed, String salt) throws NoSuchAlgorithmException {
        String pass = hashPassword(password, salt);
        return hashed.equals(pass);
    }
}
