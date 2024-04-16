package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private Button signUpButton; // for easy access of methods
    private Button signInButton; // for easy access of methods
    private String currentPlayerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);
        setupMusicToggleButton(this);
        setProperties(); // fill in the class properties
        setButtonsListeners(); // set the buttons listeners

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setProperties() {
        signUpButton = findViewById(R.id.signUpBtnId);
        signInButton = findViewById(R.id.signInBtnId);

    }

    private void setButtonsListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userNameInputView =  findViewById(R.id.userNameInputViewId);
                EditText userPasswordInputView = findViewById(R.id.userPasswordInputViewId);
                String userName = userNameInputView.getText().toString();
                String password = userPasswordInputView.getText().toString();
                currentPlayer = new User(userName, password);
                User.UserState userState = currentPlayer.signUp();
                switch (userState) {
                    case SIGNUP_SUCCEDED:
//                        setUserId(currentPlayer.getUserID());
                        goToOptionsActivity(v);
                        break;
                    case USER_EXISTS:
                        Toast.makeText(SignUpSignInActivity.this, "User Exists, go to SIGN IN", Toast.LENGTH_SHORT).show();
                        break;
                    case USER_NAME_EXISTS:
                        Toast.makeText(SignUpSignInActivity.this, "User Name Exists", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_USERNAME:
                        Toast.makeText(SignUpSignInActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_PASSWORD:
                        Toast.makeText(SignUpSignInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignUpSignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userNameInputView =  findViewById(R.id.userNameInputViewId);
                EditText userPasswordInputView = findViewById(R.id.userPasswordInputViewId);
                String userName = userNameInputView.getText().toString();
                String password = userPasswordInputView.getText().toString();
                Log.d("User", "USERNAME: " + userName);
                currentPlayer = new User(userName, password);
                User.UserState userState = currentPlayer.signIn();
                switch (userState) {
                    case SIGNIN_SUCCEDED:
                        goToOptionsActivity(v);
                        break;
                    case SIGNIN_FAILED:
                        Toast.makeText(SignUpSignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignUpSignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, OptionsActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("currPlayerUserId", currentPlayer.getUserID());
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