package edu.hope.cs.yardsale.Model;

public class Course {

    public Course(String course, int number){
        this.course = course;
        this.number = number;
    }

    private int number;
    private String course;

    public int getNumber() {
        return number;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
