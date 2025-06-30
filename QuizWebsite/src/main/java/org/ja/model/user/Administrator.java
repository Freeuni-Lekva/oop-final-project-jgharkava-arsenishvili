package org.ja.model.user;

public class Administrator {
    private long id;

    public Administrator(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (!(o instanceof Administrator)) return false;
        return id == ((Administrator) o).getId();
    }
}
