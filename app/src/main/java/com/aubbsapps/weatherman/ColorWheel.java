package com.aubbsapps.weatherman;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Aubre on 12/20/2015.
 */
public class ColorWheel {
    String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };
    //member variable (properties or things about the object)
    //Method (abilities: things the object can do)
    public int getColor() {


        //The button was click so updated the factlabel with the new one
        String color = "";

        //A random section genarter that sectionsa random fact
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mColors.length);

        color = mColors[randomNumber];
        int colorAsInt = Color.parseColor(color);

        return colorAsInt;
    }
}
