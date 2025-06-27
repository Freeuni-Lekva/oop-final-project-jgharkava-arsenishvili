package org.ja.model.user;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Objects;

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

    public String getSalt() {return salt;}
    public void setSalt(String salt) {this.salt = salt;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        return id == ((User) o).getId() &&
                username.equals(((User) o).getUsername()) &&
                passwordHashed.equals(((User) o).getPasswordHashed()) &&
                registrationDate.equals(((User) o).getRegistrationDate()) &&
                Objects.equals(photo, ((User) o).getPhoto()) &&
                status.equals(((User) o).getStatus()) &&
                salt.equals(((User) o).getSalt());
    }

}
