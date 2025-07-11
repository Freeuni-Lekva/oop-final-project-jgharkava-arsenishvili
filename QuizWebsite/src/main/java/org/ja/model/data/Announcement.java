package org.ja.model.data;

import java.sql.Timestamp;

/**
 * Represents an announcement created by an administrator.
 * Each announcement contains a unique ID, the administrator who created it,
 * a text message, and a timestamp indicating when it was created.
 */
public class Announcement {

    private long announcementId;
    private long administratorId;
    private String announcementText;
    private Timestamp creationDate;

    /**
     * Default no-argument constructor.
     */
    public Announcement() {}

    /**
     * Constructs a full announcement object.
     *
     * @param announcementId the unique ID of the announcement
     * @param administratorId the ID of the administrator who created the announcement
     * @param announcementText the content of the announcement
     * @param creationDate the timestamp when the announcement was created
     */
    public Announcement(long announcementId, long administratorId, String announcementText, Timestamp creationDate) {
        this.announcementId = announcementId;
        this.administratorId = administratorId;
        this.announcementText = announcementText;
        this.creationDate = creationDate;
    }

    /**
     * Constructs an announcement with only administrator ID and text.
     * Typically used before the announcement ID and creation date are assigned.
     *
     * @param administratorId the ID of the administrator creating the announcement
     * @param announcementText the content of the announcement
     */
    public Announcement(long administratorId, String announcementText) {
        this.administratorId = administratorId;
        this.announcementText = announcementText;
    }

    /**
     * Returns the unique ID of the announcement.
     *
     * @return the announcement ID
     */
    public long getAnnouncementId() {
        return announcementId;
    }

    /**
     * Sets the unique ID of the announcement.
     *
     * @param announcementId the ID to set
     */
    public void setAnnouncementId(long announcementId) {
        this.announcementId = announcementId;
    }

    /**
     * Returns the ID of the administrator who created the announcement.
     *
     * @return the administrator ID
     */
    public long getAdministratorId() {
        return administratorId;
    }

    /**
     * Sets the ID of the administrator who created the announcement.
     *
     * @param administratorId the ID to set
     */
    public void setAdministratorId(long administratorId) {
        this.administratorId = administratorId;
    }

    /**
     * Returns the content of the announcement.
     *
     * @return the announcement text
     */
    public String getAnnouncementText() {
        return announcementText;
    }

    /**
     * Sets the content of the announcement.
     *
     * @param announcementText the text to set
     */
    public void setAnnouncementText(String announcementText) {
        this.announcementText = announcementText;
    }

    /**
     * Returns the creation timestamp of the announcement.
     *
     * @return the creation date
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation timestamp of the announcement.
     *
     * @param creationDate the timestamp to set
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Compares this announcement with another for equality based on
     * ID, administrator ID, text content, and creation date.
     *
     * @param o the object to compare with
     * @return {@code true} if equal; otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement)) return false;

        Announcement other = (Announcement) o;
        return announcementId == other.getAnnouncementId() &&
                administratorId == other.getAdministratorId() &&
                announcementText.equals(other.getAnnouncementText()) &&
                creationDate.equals(other.getCreationDate());
    }

    /**
     * Returns a hash code for the announcement based on its text content.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return announcementText.hashCode();
    }
}
