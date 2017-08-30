package com.example.qwexo.reviewinsert;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import info.hoang8f.widget.FButton;

/**
 * Created by qwexo on 2017-08-23.
 */

public class ReviewManagerView extends LinearLayout {

    ImageView imageView,scoreImage1,scoreImage2,scoreImage3,scoreImage4,scoreImage5;
    TextView marketNameText, bodyText, timeText;
    FButton deleteButton,updateButton;

    public ReviewManagerView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.review_manager_list_item,this,true);

        imageView = (ImageView)findViewById(R.id.imageView);
        scoreImage1 = (ImageView)findViewById(R.id.scoreImage1);
        scoreImage2 = (ImageView)findViewById(R.id.scoreImage2);
        scoreImage3 = (ImageView)findViewById(R.id.scoreImage3);
        scoreImage4 = (ImageView)findViewById(R.id.scoreImage4);
        scoreImage5 = (ImageView)findViewById(R.id.scoreImage5);
        marketNameText = (TextView)findViewById(R.id.marketNameText);
        bodyText = (TextView)findViewById(R.id.bodyText);
        timeText = (TextView)findViewById(R.id.timeText);
        deleteButton = (FButton)findViewById(R.id.deleteButton);
        updateButton = (FButton)findViewById(R.id.updateButton);

        deleteButton.setButtonColor(getResources().getColor(R.color.buttonColor));
        deleteButton.setCornerRadius(15);
        updateButton.setButtonColor(getResources().getColor(R.color.buttonColor));
        updateButton.setCornerRadius(15);
        bodyText.setTextColor(Color.rgb(100,100,100));
    }
}
