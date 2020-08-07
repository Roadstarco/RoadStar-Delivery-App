package com.shrinkcom.pojo;

import java.io.Serializable;

public class EmergencyPojoNew implements Serializable {
    private String title;
    private String number;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
