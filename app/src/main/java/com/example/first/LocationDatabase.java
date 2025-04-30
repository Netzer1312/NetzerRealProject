package com.example.first;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LocationDatabase {
    private List<Location> locations = new ArrayList<>();

    // Constructor to load locations from a CSV file in assets
    public LocationDatabase(Context context, String csvFileName) throws IOException {
        loadLocationsFromCSV(context, csvFileName);
    }

    private void loadLocationsFromCSV(Context context, String csvFileName) throws IOException {
        try (InputStream is = context.getAssets().open(csvFileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean isFirstLine = true; // Skip the header row
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] fields = line.split(",");
                if (fields.length == 2) {
                    String name = fields[0].trim();
                    boolean isCity = Boolean.parseBoolean(fields[1].trim());
                    locations.add(new Location(name, isCity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Location> getLocations() {
        return locations;
    }
}
