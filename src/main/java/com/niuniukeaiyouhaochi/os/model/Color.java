package com.niuniukeaiyouhaochi.os.model;

public class Color {
    private String color;
    private Boolean isUsed;

    public Color(String color, Boolean isUsed) {
        this.color = color;
        this.isUsed = isUsed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}
