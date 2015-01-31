package com.savajolchauvet.isima.sictomproject.activity.navigation;

/**
 * Created by Maxime on 31/01/2015.
 */
public class DrawerItem {
    private String mTitle;
    private int mImageId;

    public DrawerItem(String title, int imageId) {
        super();
        mTitle = title;
        this.mImageId = imageId;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public int getImageId() {
        return mImageId;
    }
    public void setImageId(int imageId) {
        mImageId = imageId;
    }
}
