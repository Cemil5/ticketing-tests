package com.cydeo.utils;

public enum Status {

    OPEN("Open"),
    IN_PROGRES("In Progress"),
    UAT_TEST("UAT Testing"),
    COMPLETE("Completed");

    private final String value;


    Status(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}