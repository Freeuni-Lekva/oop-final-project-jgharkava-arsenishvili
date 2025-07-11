package org.ja.model.data;

/**
 * Represents a category that can be associated with quizzes or other entities.
 * Each category has a unique ID and a name.
 */
public class Category {

    private final String categoryName;
    private long categoryId;

    /**
     * Constructs a Category with the specified name.
     * Typically used when creating a new category before the ID is assigned.
     *
     * @param categoryName the name of the category
     */
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Constructs a Category with both an ID and a name.
     *
     * @param categoryId the unique ID of the category
     * @param categoryName the name of the category
     */
    public Category(long categoryId, String categoryName) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    /**
     * Returns the name of the category.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns the ID of the category.
     *
     * @return the category ID
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the ID of the category.
     *
     * @param categoryId the ID to set
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Compares this category to another object for equality.
     * Two categories are equal if they have the same ID and name.
     *
     * @param o the object to compare with
     * @return true if the categories are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category other = (Category) o;
        return categoryId == other.getCategoryId() &&
                categoryName.equals(other.getCategoryName());
    }
}
