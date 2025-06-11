package org.ja.model.user;

public class User {
    private long id;
    private String userName;
    private String passwordHashed;
    private String registrDate;
    private String photo;
    private String status;
    public User(long id, String userName, String passwordHashed, String registrDate, String photo, String status) {
        this.id = id;
        this.userName = userName;
        this.passwordHashed = passwordHashed;
        this.registrDate = registrDate;
        this.photo = photo;
        this.status = status;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public String getPasswordHashed() {
        return passwordHashed;
    }
    public String getRegistrDate() {
        return registrDate;
    }
    public String getPhoto() {
        return photo;
    }
    public String getStatus() {
        return status;
    }
}
