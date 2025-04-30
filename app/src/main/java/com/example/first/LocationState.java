package com.example.first;

public class LocationState {
    private String cityName;
    private String correctState;
    private String wrongState;

    public LocationState(String cityName, String correctState, String wrongState) {
        this.cityName = cityName;
        this.correctState = correctState;
        this.wrongState = wrongState;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCorrectState() {
        return correctState;
    }

    public String getWrongState() {
        return wrongState;
    }
}

