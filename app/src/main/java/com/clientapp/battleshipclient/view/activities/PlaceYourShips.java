package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.widget.GridView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.adapters.SquareAdapter;

public class PlaceYourShips extends BaseActivity {

    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ships);
        setupMusicToggleButton(this);
        setupGridView(gridView);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setupGridView(GridView gridNiew) {
        GridView gridView = findViewById(R.id.myBoardGridId);
        gridView.setVerticalSpacing(0);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalScrollBarEnabled(false);
        SquareAdapter adapter = new SquareAdapter(this);
        gridView.setAdapter(adapter);

    }


    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( PlaceYourShips.this); // mute the music
    }
}