package edu.hope.cs.yardsale.Model;

public class Board {
    private int id;
    private String title;
    private String description;

    public Board(int id, String title, String description){
        this.id = id;
        this.description = description;
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
