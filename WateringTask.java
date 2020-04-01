package com.example.wateringtask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class WateringTask extends AppCompatActivity {
    ArrayList<String> plants = new ArrayList<>();
    ImageView[] imgs;
    String[] wDate;
    @Override
    //on create make a list of plants using the data in the plants.txt file
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watering_task);

        String filename = "plants.txt";
        FileOutputStream outputStream;  //Allow a file to be opened for writing
        //set the main action bar to be the toolbar i created
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        myToolbar.setTitle("Watering Schedule");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        //open stream and read data from plants.txt and add each plant as a instance in the plants arraylist
        try {
            FileInputStream fis = openFileInput("plants.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while((line = br.readLine()) != null){
                plants.add(line);
            }
            br.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //grab layout of task
        LinearLayout layout = findViewById(R.id.main);

        //arrays for each of the elements for each plant each index is one plant
        //wDate and imgs are global as they need to be used in methods but cannot be passed as input
        wDate = new String[plants.size()];
        imgs = new ImageView[plants.size()];
        CardView[] cards = new CardView[plants.size()];
        TextView[] texts = new TextView[plants.size()];
        Button[] buttons = new Button[plants.size()];

        //loop over the array list and build the base structure of the task
        for(int i = 0; i < plants.size(); i++){
            //scan over each plant in the list
            Scanner input = new Scanner(plants.get(i));
            input.useDelimiter(",");

            //grab all useful variables within the list
            String type = input.next();
            String name = input.next();
            input.next();
            wDate[i] = input.next();
            input.next();input.next();input.next();
            input.close();

            //add a new element to each array one for each plant
            texts[i] = new TextView(this);
            cards[i] = new CardView(this);
            imgs[i] = new ImageView(this);
            //set the water level image to the calculated level
            imgs[i].setImageResource(getLevel(wDate[i]));
            buttons[i] = new Button(this);
            buttons[i].setText("Water");

            //set the same of the plant to show
            texts[i].setText(name + " - " + type);

            //layout of the cards
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
            lp.setMargins(0, 15, 0, 15);

            //layout of the images
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp2.leftMargin = 1000;

            //layout of the text
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp3.setMargins(20, 130, 0, 0);

            //layout of the buttons
            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 150);
            lp4.setMargins(700, 130, 0, 0);

            //set layouts
            texts[i].setTextSize(28);
            texts[i].setLayoutParams(lp3);
            imgs[i].setLayoutParams(lp2);
            cards[i].setLayoutParams(lp);
            buttons[i].setLayoutParams(lp4);

            //add all the view elements to show on the task layout
            cards[i].addView(buttons[i]);
            cards[i].addView(texts[i]);
            cards[i].addView(imgs[i]);
            layout.addView(cards[i]);
            //give each button an id so that they can be easily retrieved later
            buttons[i].setId(i);
            //on button click call setWater() giving the button pressed as input
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setWater(v);
                }
            });
        }

    }
    //get level method calculates how long it has been since being watered and then returns the proper image
    public int getLevel(String date){
        //retrieve the useful date data and parse int integers
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7));
        int day = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));

        //create a date for now
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date2 = new Date();
        String dat = formatter.format(date2);
        //retrieve the integers for the current time
        int year2 = Integer.parseInt(dat.substring(0,4));
        int month2 = Integer.parseInt(dat.substring(5,7));
        int day2 = Integer.parseInt(dat.substring(8, 10));
        int hour2 = Integer.parseInt(dat.substring(11, 13));

        //Full drop 12 - 24 || 2/3 drop 12 - 6 hours || 1/3 drop 6 - 2 hours || Danger drop < 2 hours
        //          0- 12               12-18                    18-22                        22-24
        //assuming watering every 24 hours
        if(hour2 - hour < 12 && year2 - year == 0 && month2 - month == 0 && day2 - day == 0)
            return R.drawable.image4;
        if(hour2 - hour > 12 && hour2 - hour < 18 && year2 - year == 0 && month2 - month == 0 && day2 - day == 0)
            return R.drawable.image3;
        if(hour2 - hour > 18 && hour2 - hour < 22 && year2 - year == 0 && month2 - month == 0 && day2 - day == 0)
            return R.drawable.image2;
        return R.drawable.image1;
    }
    //updates the last water date on button press
    //setWater() replaces the prior data inside of plants.txt with the new updated lastWatered date
    public void setWater(View bt){
        //set button
        Button but = (Button) bt;
        //get index of the plant
        int index = bt.getId();
        //get last water date
        String lastWater = wDate[index];
        //get plant
        String plant = plants.get(index);
        //get current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date2 = new Date();
        String dat = formatter.format(date2);
        //replace old water date with current time
        plant = plant.replace(lastWater, dat);
        String output = "";
        //append all other plants as they were and append the updated plant
        for(int j = 0; j < plants.size(); j++){
            if(j != index)
                output += plants.get(j) + "\n";
            else
                output += plant + "\n";
        }
        String filename = "plants.txt";
        FileOutputStream outputStream;
        //reprint the new data into plants.txt
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(output.getBytes());    //FileOutputStream is meant for writing streams of raw bytes.
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //update the layout
        update();

    }
    //update updates the plants water level image after the plant has been watered
    public void update(){
        //clear list of plants so they can be replaced
        plants.clear();
        //get list of plants
        try {
            FileInputStream fis = openFileInput("plants.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while((line = br.readLine()) != null) {
                plants.add(line);
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //update the images with the new levels
        for(int i = 0; i < plants.size(); i++){
            Scanner input = new Scanner(plants.get(i));
            input.useDelimiter(",");
            input.next();input.next();input.next();
            wDate[i] = input.next();
            input.next();input.next();input.next();
            imgs[i].setImageResource(getLevel(wDate[i]));
        }
    }

}
