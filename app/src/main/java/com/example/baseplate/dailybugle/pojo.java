package com.example.baseplate.dailybugle;

public class pojo {
    private String mHeadline;
    private String mHead;
    private String mSection;
    private String mDate;
    private String mURL;
    private String mAuthor;
    public pojo(String headlineID, String headID, String sectionID, String dateID, String urlID, String authorID) {
        mHeadline = headlineID;
        mHead = headID;
        mSection = sectionID;
        mDate = dateID;
        mURL = urlID;
        mAuthor = authorID;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getHead() {
        return mHead;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getURL() {
        return mURL;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
