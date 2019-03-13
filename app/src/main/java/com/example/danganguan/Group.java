package com.example.danganguan;

/**
 * Created by dell on 2018/8/2.
 */

public class Group {
    private String gName;

    public Group() {
    }
    public Group(Group group){
        this.gName=group.gName;
    }

    public Group(String gName) {
        this.gName = gName;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }
}
