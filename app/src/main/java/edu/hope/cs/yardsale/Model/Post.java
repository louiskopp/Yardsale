package edu.hope.cs.yardsale.Model;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

import edu.hope.cs.yardsale.Model.User;

public class Post implements Serializable {

    private int id;
    private String title;
    private String description;
    private String condition;
    private boolean active;
    private double price;
    private int boardId;
    private int userId;
    private Date createdAt;
    private Date updatedAt;
    private User user;


    // private Image image;
    private String[] images;


    public Post(String title, String description, Double price){
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public Post(String title, String description, Double price, String[] images){
        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int getBoardId() {
        return boardId;
    }


    public int getUserId() {
        return userId;
    }

    public User getUser() {
      return user;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() { return this.images; }

    public void setImage(String[] images) { this.images = images; }



    public void setTitle(String title) {
        this.title = title;
    }
}
