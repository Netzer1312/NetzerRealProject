package com.example.first;

import android.content.Context;

import com.example.first.LocationState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

    public class LocationDataStates {
        private List<LocationState> locations;


    public LocationDataStates(Context context, String fileName) throws IOException {
        locations = new ArrayList<>();
        loadLocationsFromCSV(context, fileName);
    }

    private void loadLocationsFromCSV(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String cityName = parts[0].trim();
                String correctState = parts[1].trim();
                String wrongState = parts[2].trim();
                locations.add(new LocationState(cityName, correctState, wrongState));
            }
        }

        reader.close();
        inputStream.close();
    }

    public List<LocationState> getLocations() {
        return locations;
    }
    }