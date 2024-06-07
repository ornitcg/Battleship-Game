package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.logic.SignLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.UI_utils.ValidationSignWatch;

import lombok.Getter;
import lombok.Setter;


/*
 * This class is the activity for the Sign Up and Sign In screen
 * */
@Setter @Getter
public class SignActivity extends BaseActivity {

    private User currentPlayer;
    private SignLogic signLogic;
    private Button signUpButton; // for easy access of methods
    private Button signInButton; // for easy access of methods
    private EditText userNameInputView;
    private EditText userPasswordInputView;
    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;


    /* This method overrides the onCreate method in the Activity class
      and is used to initialize the activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        signLogic = new SignLogic();

        setUItouchListener(findViewById(R.id.mainTag));
        setEditTextListeners();
        setButtonViewsProperties(); // fill in the class properties
        setupSignUpButton();
        setupSignInButton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setEditTextListeners() {
        userNameInputView = findViewById(R.id.userNameInputViewId);
        userPasswordInputView = findViewById(R.id.userPasswordInputViewId);

        userNameInputView.addTextChangedListener(new ValidationSignWatch(this, userNameInputView, ValidationSignWatch.InputType.USERNAME));
        userPasswordInputView.addTextChangedListener(new ValidationSignWatch(this, userPasswordInputView, ValidationSignWatch.InputType.PASSWORD));
    }


    /* This method checks if the user has touched the screen outside the edittext views.
     * in that case, the hideKeyboard will be called */
    private void setUItouchListener(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }
    }


    /* This method is used to set the buttons variables to the the button views properties */
    private void setButtonViewsProperties() {
        signUpButton = findViewById(R.id.signUpBtnId);
        signInButton = findViewById(R.id.signInBtnId);
    }


    /* This method is used to set the signup button click listener */
    private void setupSignUpButton() {
        signUpButton.setOnClickListener(v -> {
            setCurrentPlayerFromInput();
            Log.d("DEBUG signingUp", "signUp: " + currentPlayer.getName());
            AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
            signLogic.signUp(this, currentPlayer);
        });
    }

    /* This method is used to set the signin button click listener */
    private void setupSignInButton() {
        signInButton.setOnClickListener(v -> {
            setCurrentPlayerFromInput();
            Log.d("DEBUG signingIn", "signIn: " + currentPlayer.getName());
            AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
            signLogic.signIn(this, currentPlayer);
        });
    }

    /* connect to edittext views
     * and create a new user object with the input values */
    private void setCurrentPlayerFromInput() {
        String userName = userNameInputView.getText().toString();
        String password = userPasswordInputView.getText().toString();
        currentPlayer = new User(userName, password);
    }

    /* This method displays the authentication failure message */
    public void authFailureMsg(SignLogic.AuthState authState) {
        TextView authFailureMsg = findViewById(R.id.authFailureMsg);
        authFailureMsg.setVisibility(View.VISIBLE);

        switch (authState) {
            case USER_EXISTS:
                authFailureMsg.setText("User Exists");
                break;
            case USER_DOESNT_EXIST:
                authFailureMsg.setText("User Doesn't Exist");
                break;
            case INVALID_USERNAME:
                authFailureMsg.setText("Invalid Username");
                break;
            case INVALID_PASSWORD:
                authFailureMsg.setText("Invalid Password");
                break;
            case WRONG_PASSWORD:
                authFailureMsg.setText("Wrong Password");
                break;
            case CONNECTION_ERROR:
                authFailureMsg.setText("Connection Error");
                break;
            default:
                break;
        }

        //makes the message wait for 2 seconds, before disappearing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                authFailureMsg.setVisibility(View.GONE);
            }
        }, 2000);
    }


    /* This method hides the soft keyboard */
    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /* This is a activity lifecycle callback method  that is called when the activity is paused */
    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(SignActivity.this); // mute the music
    }

    /* This is a activity lifecycle callback method  that is called when the activity is resumed */
    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(SignActivity.this); // mute the music
    }

}