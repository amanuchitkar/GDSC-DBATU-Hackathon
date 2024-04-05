package com.example.psp;

import java.io.Serializable;
import java.util.Date;

public class Course implements Serializable {
    private final int courseId;
    private final Date creationDate;
    private String courseTitle;
    private String courseDescription;
    private int courseSemester;

    public Course(int courseId, Date creationDate, String courseTitle, String courseDescription, int courseSemester) {
        this.courseId = courseId;
        this.creationDate = creationDate;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.courseSemester = courseSemester;
    }

    public int getCourseId() {
        return courseId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public int getCourseSemester() {
        return courseSemester;
    }

    public void setCourseSemester(int courseSemester) {
        this.courseSemester = courseSemester;
    }

}
