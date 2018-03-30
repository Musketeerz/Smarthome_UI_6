package com.example.musketeers.realm;

public class Word {

    private String aName;
    private Integer aThumb;
    private Boolean aSwitch, aEco;

    public Word(Integer thumb, String name, Boolean swit, Boolean eco) {
        aName = name;
        aThumb = thumb;
        aSwitch = swit;
        aEco = eco;
    }

    public Integer getaThumb() {
        return aThumb;
    }

    public String getaName() {
        return aName;
    }

    public Boolean getaSwitch() {
        return aSwitch;
    }

    public Boolean getaEco() {
        return aEco;
    }

}