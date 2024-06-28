package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;


/*
*  InstructionsActivity class
*  This class is used to display the instructions of the game
*  The instructions are displayed in the activity_instructions.xml layout
* */
public class InstructionsActivity extends BaseActivity {



    /*
    *  Overrides the onCreate method from the BaseActivity class
    *  This method sets the content view to the activity_instructions.xml layout
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);
        Log.d("DEBUG on Instructions", "onCreate: ");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInstructions), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /*
    *  Overrides the setActivityXbutton method from the BaseActivity class
    *  This method sets the X button for the InstructionsActivity
    *  The X button is used to exit the current activity
    * */
    @Override
    protected void setActivityXbutton() {
        ImageButton xbutton = (ImageButton) findViewById(R.id.XActivityButtonId);
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: exitBtn");
                AudioUtils.makeSound(InstructionsActivity.this, AudioEnum.BUTTON);
                //finish current activity
                finish();
            }
        });
    }


    /* Overrides the setYesNoLayout method from the BaseActivity class
     * This method sets nothing for the layout of the YesNoLayout that does not exist in the InstructionsActivity
     * */
    @Override
    protected void setYesNoLayout() {
        //do nothing
    }

    /*
     *  Overrides the onResume method from the BaseActivity class
     * */
    @Override //onResume
    protected void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(InstructionsActivity.this); // mute the music
    }


}