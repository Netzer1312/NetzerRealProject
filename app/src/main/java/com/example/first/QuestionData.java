package com.example.first;

public class QuestionData {
    public String cityName;
    public String correctState;
    public String wrongState;

    public QuestionData() { }

    public QuestionData(String cityName, String correctState, String wrongState) {
        this.cityName = cityName;
        this.correctState = correctState;
        this.wrongState = wrongState;
    }
}
