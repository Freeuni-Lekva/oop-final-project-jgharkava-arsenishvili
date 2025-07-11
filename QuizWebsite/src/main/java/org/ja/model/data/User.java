package org.ja.model.data;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Represents a user in the system with authentication and profile details.
 */
public class User {
    private long id;
    private String salt;
    private String username;
    private String passwordHashed;
    private Timestamp registrationDate;
    private String photo;
    private String status;

    /**
     * Constructs a User with all details.
     *
     * @param id               the unique user ID
     * @param username         the username chosen by the user
     * @param passwordHashed   the hashed password for authentication
     * @param salt             the salt used in password hashing
     * @param registrationDate the timestamp when the user registered
     * @param photo            URL or path to the user's photo
     * @param status           the user's current status (e.g., active, banned)
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available (can be removed if not used inside constructor)
     */
    public User(long id, String username, String passwordHashed, String salt, Timestamp registrationDate, String photo, String status) throws NoSuchAlgorithmException {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.passwordHashed = passwordHashed;
        this.registrationDate = registrationDate;
        this.photo = photo;
        this.status = status;
    }

    /** Returns the unique user ID. */
    public long getId() {
        return id;
    }

    /** Sets the unique user ID. */
    public void setId(long id) {
        this.id = id;
    }

    /** Returns the username. */
    public String getUsername() {
        return username;
    }

    /** Sets the username. */
    public void setUsername(String username){
        this.username = username;
    }

    /** Returns the hashed password. */
    public String getPasswordHashed() {
        return passwordHashed;
    }

    /** Sets the hashed password. */
    public void setPasswordHashed(String passwordHashed){
        this.passwordHashed = passwordHashed;
    }

    /** Returns the registration date of the user. */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    /** Sets the registration date of the user. */
    public void setRegistrationDate(Timestamp registrationDate){
        this.registrationDate = registrationDate;
    }

    /** Returns the user's photo URL or path. */
    public String getPhoto() {
        return photo;
    }

    /** Sets the user's photo URL or path. */
    public void setPhoto(String photo){
        this.photo = photo;
    }

    /** Returns the user's status. */
    public String getStatus() {
        return status;
    }

    /** Sets the user's status. */
    public void setStatus(String status){
        this.status = status;
    }

    /** Returns the salt used for hashing the password. */
    public String getSalt() {
        return salt;
    }

    /** Sets the salt used for hashing the password. */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Checks equality based on all user fields.
     *
     * @param o the object to compare
     * @return true if all fields are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User other = (User) o;
        return id == other.getId() &&
                username.equals(other.getUsername()) &&
                passwordHashed.equals(other.getPasswordHashed()) &&
                registrationDate.equals(other.getRegistrationDate()) &&
                Objects.equals(photo, other.getPhoto()) &&
                status.equals(other.getStatus()) &&
                salt.equals(other.getSalt());
    }

    /**
     * Generates hash code based on username.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
