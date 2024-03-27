package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.User;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class SignUpSignInActivity extends BaseActivity {

    private User currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);
        setupMusicToggleButton(this);

        String userNameEditText = findViewById(R.id.userNameInputViewId).toString();
        String passwordEditText = findViewById(R.id.userPasswordInputViewId).toString();
        User user = new User(userNameEditText, passwordEditText);
        Button signUpButton = findViewById(R.id.signUpBtnId);
        Button signInButton = findViewById(R.id.signInBtnId);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.UserState userState = user.signUp();
                switch (userState) {
                    case SIGNUP_SUCCEDED:
                        Toast.makeText(SignUpSignInActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        goToOptionsActivity(v);
                        break;
                    case USER_EXISTS:
                        Toast.makeText(SignUpSignInActivity.this, "User exists, go to Sign In", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_USERNAME:
                        Toast.makeText(SignUpSignInActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_PASSWORD:
                        Toast.makeText(SignUpSignInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        break;
                    case USER_NAME_EXISTS:
                        Toast.makeText(SignUpSignInActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignUpSignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpSignInActivity.this, "Sign In Button Clicked", Toast.LENGTH_SHORT).show();
                User.UserState userState = user.signIn();
                switch (userState) {
                    case SIGNIN_SUCCEDED:
                        Toast.makeText(SignUpSignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                        goToOptionsActivity(v);
                        break;
                    case SIGNIN_FAILED:
                        Toast.makeText(SignUpSignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignUpSignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, PlaceYourShips.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("currentUserName", currentPlayer.getUsername());
        startActivity(intent);
    }



    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic( SignUpSignInActivity.this); // mute the music

    }


    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( SignUpSignInActivity.this); // mute the music
    }



}