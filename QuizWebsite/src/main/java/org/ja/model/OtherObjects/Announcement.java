package org.ja.model.OtherObjects;

import java.sql.Timestamp;

public class Announcement {
    private long announcementId;
    private long administratorId;
    private String announcementText;
    private Timestamp creationDate;

    public Announcement(){};
    public Announcement(long announcementId, long administratorId, String announcementText, Timestamp creationDate){
        this.announcementId = announcementId;
        this.administratorId = administratorId;
        this.announcementText = announcementText;
        this.creationDate = creationDate;
    }

    public long getAnnouncementId(){return announcementId;};
    public void setAnnouncementId(long announcementId){this.announcementId = announcementId;};
    public long getAdministratorId(){return administratorId;};
    public void setAdministratorId(long administratorId){this.administratorId = administratorId;};
    public String getAnnouncementText(){return announcementText;};
    public void setAnnouncementText(String announcementText){this.announcementText = announcementText;};
    public Timestamp getCreationDate(){return creationDate;};
    public void setCreationDate(Timestamp creationDate){this.creationDate = creationDate;};
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (!(o instanceof  Announcement)) return false;

        return announcementId == ((Announcement) o).getAnnouncementId() &&
                administratorId == ((Announcement) o).getAdministratorId() &&
                announcementText.equals(((Announcement) o).getAnnouncementText()) &&
                creationDate.equals(((Announcement) o).getCreationDate());
    }
}
