package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;
import com.clientapp.battleshipclient.view.adapters.SquareAdapter;

public class PlaceYourShips extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupMusicToggleButton(this);
        AudioUtils.keepMusicState(this); // keep the music state

        setContentView(R.layout.activity_place_ships);



        GridView gridView = findViewById(R.id.myBoard);
        SquareAdapter adapter = new SquareAdapter(this);
        gridView.setAdapter(adapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void onDestroy() {
        super.onDestroy();
        AudioUtils.pauseMusic(this);
    }

    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.keepMusicState( PlaceYourShips.this); // mute the music
    }
}