package ru.rsue.borisov;

import java.util.Date;
import java.util.UUID;

public class Book {
    private UUID mId;
    private String mTitle;
    private Date mDate;

    private boolean mReader;

    public Book() {
        this(UUID.randomUUID());
    }
    public Book(UUID id){
        mId=id;
        mDate=new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isReaded() {
        return mReader;
    }

    public void setReaded(boolean reader) {
        mReader = reader;
    }

    public void setTime(Date time) {
        mDate.setHours(time.getHours());
        mDate.setMinutes(time.getMinutes());
    }
    public String getPhotoFilename(){
        return "IMG_"+getId().toString()+".jpg";
    }
}
