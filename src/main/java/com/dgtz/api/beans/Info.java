package com.dgtz.api.beans;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/19/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Info {

    protected long id;
    protected String title;
    protected String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
