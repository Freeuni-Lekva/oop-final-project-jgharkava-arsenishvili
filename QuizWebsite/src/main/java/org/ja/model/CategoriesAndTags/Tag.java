package org.ja.model.CategoriesAndTags;

public class Tag {
    private String tagName;
    private long tagId;
    public Tag(long tagId,String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }
    public String getTagName() {
        return tagName;
    }
    public long getTagId() {
        return tagId;
    }
    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
