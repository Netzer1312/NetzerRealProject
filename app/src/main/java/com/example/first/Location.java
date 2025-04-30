package com.example.first;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Location {
    private String name;
    private boolean isCity;


    // Constructor
    public Location(String name, boolean isCity) {
        this.name = name;
        this.isCity = isCity;
    }


    // Getters
    public String getName() {
        return name;
    }

    public boolean isCity() {
        return isCity;
    }

    public boolean isState() {
        return !isCity; // A location is a state if it's not a city
    }


}