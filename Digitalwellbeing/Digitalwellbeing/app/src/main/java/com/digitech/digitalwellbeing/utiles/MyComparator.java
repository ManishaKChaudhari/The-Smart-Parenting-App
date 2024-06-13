package com.digitech.digitalwellbeing.utiles;

import java.util.Comparator;

public class MyComparator implements Comparator<DigitalWelbeingDataObject> {
    @Override
    public int compare(DigitalWelbeingDataObject o1, DigitalWelbeingDataObject o2) {
        // Implement comparison logic based on 'value' attribute or any other criteria
        return Integer.compare(Integer.parseInt(o1.getTime()),Integer.parseInt(o2.getTime()));
    }
}