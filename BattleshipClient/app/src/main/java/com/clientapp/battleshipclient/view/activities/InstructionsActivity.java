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

public class InstructionsActivity extends BaseActivity {

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

    @Override
    protected void setXbutton() {
        ImageButton xbutton = (ImageButton) findViewById(R.id.XbuttonId);
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
    protected void setYesNoLayout(){
        //do nothing
    }


    @Override //onResume
    protected void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(InstructionsActivity.this); // mute the music
    }




}