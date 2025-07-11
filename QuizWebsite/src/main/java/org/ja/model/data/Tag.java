package org.ja.model.data;

/**
 * Represents a Tag entity used for categorizing or labeling quizzes or other content.
 * Each Tag has a unique ID and a name.
 */
public class Tag {
    private String tagName;
    private long tagId;

    /**
     * Default constructor for frameworks or manual property setting.
     */
    public Tag(){}

    /**
     * Constructs a Tag with specified ID and name.
     *
     * @param tagId   the unique identifier of the tag
     * @param tagName the name of the tag
     */
    public Tag(long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    /**
     * Constructs a Tag with a specified name.
     * The tag ID will default to 0 (unset).
     *
     * @param tagName the name of the tag
     */
    public Tag(String tagName){
        this.tagId = 0L;
        this.tagName = tagName;
    }

    /**
     * Returns the name of the tag.
     *
     * @return the tag's name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the name of the tag.
     *
     * @param tagName the tag's new name
     */
    public void setTagName(String tagName){
        this.tagName = tagName;
    }

    /**
     * Returns the ID of the tag.
     *
     * @return the tag's unique ID
     */
    public long getTagId() {
        return tagId;
    }

    /**
     * Sets the ID of the tag.
     *
     * @param tagId the unique ID to set
     */
    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    /**
     * Checks equality based on tag ID and tag name.
     *
     * @param o the object to compare
     * @return true if IDs and names are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag other = (Tag) o;
        return tagId == other.getTagId() &&
                tagName.equals(other.getTagName());
    }
}
