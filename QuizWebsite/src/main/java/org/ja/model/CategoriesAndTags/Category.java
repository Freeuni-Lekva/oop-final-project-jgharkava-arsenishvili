package org.ja.model.CategoriesAndTags;

public class Category {
    private final String categoryName;

    private long categoryId;

    public Category(long categoryId,String categoryName) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        return categoryId == ((Category) o).getCategoryId() &&
                categoryName.equals(((Category) o).getCategoryName());
    }
}
