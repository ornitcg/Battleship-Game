package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.AuthStateEnum;
import com.clientapp.battleshipclient.logic.AuthTypeEnum;
import com.clientapp.battleshipclient.logic.SignLogic;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.NWutils.EndpointResources;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.view_utils.ValidationSignWatch;

import lombok.Getter;
import lombok.Setter;


/*
 * This class is the activity for the Sign Up and Sign In screen
 * */
@Setter
@Getter
public class SignActivity extends BaseActivity {

    private User currentPlayer;
    private SignLogic signLogic;
    private Button signUpButton; // for easy access of methods
    private Button signInButton; // for easy access of methods
    private EditText userNameInputView;
    private EditText userPasswordInputView;
    private EditText inputIPtext;
    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    private boolean isIpValid = false;
    private View settingsView;
    private View mainView;
    private String ip = EndpointResources.DEFAULT_IP;
    TextView authMsg;


    /* This method overrides the onCreate method in the Activity class
      and is used to initialize the activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        signLogic = new SignLogic();
        mainView = findViewById(R.id.mainTag);
        settingsView = findViewById(R.id.settingsViewId);

        authMsg = findViewById(R.id.authFailureMsg);
        setUItouchListener(mainView);
        setUItouchListener(settingsView);

        setSettingsView();
        setSettingsButton();

        setEditTextListeners();
        setButtonViewsProperties(); // fill in the class properties
        setSignUpButton();
        setSignInButton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setSettingsView() {
        if (settingsView != null) {
            settingsView.setVisibility(View.GONE);
            setSettingsXButton();
            setRadioGroup();
            setAcceptButton();
        } else Log.d("DEBUG on SignActivity", "setSettingsView: settingsView is null");

    }


    /*
     *   Sets the accept button on the settings view
     *   When the button is clicked, the settings view will be hidden
     *   and the endpoints will be set to the user's choice
     */
    private void setAcceptButton() {
        Button acceptButton = findViewById(R.id.acceptBtnId);
        if (acceptButton != null) {
            acceptButton.setOnClickListener(v -> {
                AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
                if (ValidationSignWatch.isValidIP(ip)) {
                    ip = getIpFromInput();
                    Log.d("myDEBUG on SignActivity", "setAcceptButton: " + ip);
                } else
                    displayAuthStateMsg(AuthStateEnum.INVALID_IP);
                settingsView.setVisibility(View.GONE);
                hideKeyboard(acceptButton);
                restoreViewClickables();
                Log.d("myDEBUG on SignActivity", "setAcceptButton: " + ip);
                EndpointResources.initializeEndpoints(ip);
            });
        } else Log.d("myDEBUG on SignActivity", "setAcceptButton: acceptButton is null");
    }


    /*
     *  Disables the clickable property of the buttons and the input fields
     * when the settings view is shown
     * */
    private void disableViewClickables() {
        signUpButton.setClickable(false);
        signInButton.setClickable(false);
        userNameInputView.setEnabled(false);
        userPasswordInputView.setEnabled(false);
        getXActivityBtn().setClickable(false);
    }


    /*
     *  Restores the clickable property of the buttons and the input fields
     *  when the settings view is hidden
     * */
    private void restoreViewClickables() {
        signUpButton.setClickable(true);
        signInButton.setClickable(true);
        userNameInputView.setEnabled(true);
        userPasswordInputView.setEnabled(true);
        getXActivityBtn().setClickable(true);
    }


    /*
     *  Closes and Hides the settings view
     * */
    private void setSettingsXButton() {
        ImageButton settingsXButton = settingsView.findViewById(R.id.XsettingsBtnId);
        settingsXButton.setOnClickListener(v -> {
            AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
            restoreViewClickables();
            settingsView.setVisibility(View.GONE);
        });
    }


    /*
     *  Sets the radio group for the connection type
     * */
    private void setRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.radioGroupConnectionTypeId);
        RadioButton defaultIPbtn = findViewById(R.id.defaultIPbtnId);
        RadioButton inputIPBtn = findViewById(R.id.inputIPbutnId);
        RadioButton customDefaultIPbtn = findViewById(R.id.customDefaultIPbtnId);
        RadioButton customInputIPBtn = findViewById(R.id.customInputIPbutnId);
        inputIPtext = findViewById(R.id.inputIPid);


        if (radioGroup != null || inputIPtext != null || defaultIPbtn != null || inputIPBtn != null) {
            radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
                if (checkedId == defaultIPbtn.getId()) {
                    ip = EndpointResources.DEFAULT_IP;
                    //change radio button style
                    customDefaultIPbtn.setVisibility(View.VISIBLE);
                    inputIPBtn.setVisibility(View.VISIBLE);
                    defaultIPbtn.setVisibility(View.GONE);
                    customInputIPBtn.setVisibility(View.GONE);
                    inputIPtext.setEnabled(false);
                } else if (checkedId == inputIPBtn.getId()) {
                    defaultIPbtn.setVisibility(View.VISIBLE);
                    customDefaultIPbtn.setVisibility(View.GONE);
                    inputIPBtn.setVisibility(View.GONE);
                    customInputIPBtn.setVisibility(View.VISIBLE);
                    inputIPtext.setEnabled(true);
                    inputIPtext.addTextChangedListener(new ValidationSignWatch(this, inputIPtext, ValidationSignWatch.InputType.IP));

                }

            });
        }
    }


    /*
     *   Gets the IP from the input field
     *   and returns it as a string
     * */
    private String getIpFromInput() {
        EditText inputIPtext = findViewById(R.id.inputIPid);
        ip = inputIPtext.getText().toString();
        Log.d("myDEBUG on SignActivity", "getIpFromInput: " + ip);
        return ip;
    }


    /*
     *  Sets the settings button on the sign in screen
     *  When the button is clicked, the settings view will be shown
     *  and the user can change the endpoints
     * */
    private void setSettingsButton() {
        ImageButton settingsButton = findViewById(R.id.settingsButtonId);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
                disableViewClickables();
                settingsView.setVisibility(View.VISIBLE);
            });
        } else Log.d("myDEBUG on SignActivity", "setSettingsButton: settingsButton is null");
    }


    /*
     *  Sets the edit text listeners for the username and password input fields
     *  on attempt to sign up or sign in the input fields will be checked
     *  for validation
     * */
    private void setEditTextListeners() {
        userNameInputView = findViewById(R.id.userNameInputViewId);
        if (userNameInputView != null) {
            userNameInputView.addTextChangedListener(new ValidationSignWatch(this, userNameInputView, ValidationSignWatch.InputType.USERNAME));
        }
        userPasswordInputView = findViewById(R.id.userPasswordInputViewId);
        if (userPasswordInputView != null) {
            userPasswordInputView.addTextChangedListener(new ValidationSignWatch(this, userPasswordInputView, ValidationSignWatch.InputType.PASSWORD));
        }
    }


    /* This method checks if the user has touched the screen outside the edittext views.
     * in that case, the hideKeyboard will be called */
    private void setUItouchListener(View view) {
        if (view != null && !(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
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
    private void setSignUpButton() {
        if (signUpButton != null) {
            Log.d("myDEBUG on SignActivity", "setupSignUpButton: signUpButton is null");
            signUpButton.setOnClickListener(v -> {
                displayAuthTypeMsg(AuthTypeEnum.SIGN_UP);
                setCurrentPlayerFromInput();
                Log.d("myDEBUG signingUp", "signUp: " + currentPlayer.getName());
                AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
                signLogic.signUser(this, AuthTypeEnum.SIGN_UP, currentPlayer);
            });
        } else Log.d("myDEBUG on SignActivity", "setupSignUpButton: signUpButton is null");
    }


    /*
     *  This method displays the authentication type message
     *   on the screen
     * */
    private void displayAuthTypeMsg(AuthTypeEnum msg) {
        authMsg.setVisibility(View.VISIBLE);
        authMsg.setText(msg.getName());
    }

    /* This method is used to set the signin button click listener */
    private void setSignInButton() {
        if (signInButton != null) {
            signInButton.setOnClickListener(v -> {
                displayAuthTypeMsg(AuthTypeEnum.SIGN_IN);
                setCurrentPlayerFromInput();
                Log.d("myDEBUG signingIn", "signIn: " + currentPlayer.getName());
                AudioUtils.makeSound(SignActivity.this, AudioEnum.BUTTON);
                signLogic.signUser(this, AuthTypeEnum.SIGN_IN, currentPlayer);
            });
        } else Log.d("myDEBUG on SignActivity", "setupSignInButton: signInButton is null");

    }

    /* connect to edittext views
     * and create a new user object with the input values */
    private void setCurrentPlayerFromInput() {
        String userName = userNameInputView.getText().toString();
        String password = userPasswordInputView.getText().toString();
        currentPlayer = new User(userName, password);
    }

    /* This method displays the authentication failure message */
    public void displayAuthStateMsg(AuthStateEnum authState) {

        if (authMsg != null) {
            authMsg.setVisibility(View.VISIBLE);
        } else Log.d("myDEBUG on SignActivity", "authFailureMsg: authFailureMsg is null");

        if (authState == AuthStateEnum.CONNECTION_ERROR)
            authMsg.setText(AuthStateEnum.CONNECTION_ERROR.getName() + " on ip: " + ip);
        else
            authMsg.setText(authState.getName());

        //makes the message wait for 2 seconds, before disappearing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                authMsg.setVisibility(View.GONE);
            }
        }, 2000);
    }


    /* This method hides the soft keyboard */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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