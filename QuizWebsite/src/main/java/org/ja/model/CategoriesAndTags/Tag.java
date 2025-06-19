package org.ja.model.CategoriesAndTags;

public class Tag {
    private String tagName;
    private long tagId;

    public Tag(){}

    public Tag(long tagId,String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName){this.tagName = tagName;}
    public long getTagId() {
        return tagId;
    }
    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        return tagName.equals(((Tag) o).getTagName()) &&
                tagId == ((Tag) o).getTagId();
    }
}
