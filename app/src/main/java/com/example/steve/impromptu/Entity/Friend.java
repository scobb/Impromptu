package com.example.steve.impromptu.Entity;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class Friend {

    private String name;
    private boolean selected = false;

    public Friend(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {

        return name;
    }

    public boolean isSelected() {
        return selected;
    }
}
