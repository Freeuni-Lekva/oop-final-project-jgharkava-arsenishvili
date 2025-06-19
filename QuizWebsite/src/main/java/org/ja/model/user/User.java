package org.ja.model.user;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import org.ja.utils.PasswordHasher;

public class User {
    private long id;
    private String salt;
    private String username;
    private String passwordHashed;
    private Timestamp registrationDate;
    private String photo;
    private String status;

    public User(long id, String username, String passwordHashed, String salt, Timestamp registrationDate, String photo, String status) throws NoSuchAlgorithmException {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.passwordHashed = passwordHashed;
        this.registrationDate = registrationDate;
        this.photo = photo;
        this.status = status;
    }

//    public boolean checkPassword(String password, String salt) throws NoSuchAlgorithmException {
//        return PasswordHasher.verifyPassword(password, passwordHashed, salt);
//    }  //to check the password when the user logs in

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username){this.username = username;}

    public String getPasswordHashed() {return passwordHashed;}
    public void setPasswordHashed(String passwordHashed){this.passwordHashed = passwordHashed;}

    public String getSalt(){return salt;}
    public void setSalt(String salt){this.salt = salt;}

    public Timestamp getRegistrationDate() {return registrationDate;}
    public void setRegistrationDate(Timestamp registrationDate){this.registrationDate = registrationDate;}

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo){this.photo = photo;}

    public String getStatus() {
        return status;
    }
    public void setStatus(String status){this.status = status;}
}
