package com.clientapp.battleshipclient.view.view_utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.clientapp.battleshipclient.logic.SignLogic;
import com.clientapp.battleshipclient.view.activities.SignActivity;

/*
 *  ValidationSignWatch is a class that implements the TextWatcher interface.
 *  It is used to validate the input in the sign up form.
 *  The class overrides the afterTextChanged, beforeTextChanged and onTextChanged methods. *
 * */

public class ValidationSignWatch implements TextWatcher {
    private EditText editText;
    private InputType textType;
    private Context context;

    public enum InputType {
        USERNAME, PASSWORD, IP
    }


    /*
     *  Constructor
     * */
    public ValidationSignWatch(Context context, EditText editText, InputType textType) {
        this.editText = editText;
        this.textType = textType;
        this.context = context;
    }


    @Override
    public void afterTextChanged(Editable s) {
        validateInput(editText, s.toString());
    }


    private void validateInput(EditText editText, String text) {

        boolean isValid = isValidInput(text);
        if (isValid) {
            editText.setTextColor(Color.BLACK); // Valid input
        } else {
            editText.setTextColor(Color.RED); // Invalid input
        }


        switch (textType) {
            case USERNAME:
                ((SignActivity) context).setUsernameValid(isValid);
                break;
            case PASSWORD:
                ((SignActivity) context).setPasswordValid(isValid);
                break;
            case IP:
                ((SignActivity) context).setIpValid(isValid);
                break;
        }

    }


    private boolean isValidInput(String text) {
        switch (textType) {
            case USERNAME:
                return isValidUsername(text);
            case PASSWORD:
                return isValidPassword(text);
            case IP:
                return isValidIP(text);
            default:
                return false;
        }
    }

    public static boolean isValidIP(String text) {
        boolean isMatch = text.matches(SignLogic.IPV4_REGEX);
        Log.d("myDEBUG", "isValidIP: " + isMatch);
        return isMatch;
    }


    private boolean isValidPassword(String text) {
        if (text.length() < SignLogic.MIN_PASSWORD_LENGTH || text.length() > SignLogic.MAX_PASSWORD_LENGTH)
            return false;
        //check if text contains only letters and numbers
        return text.matches("^[a-zA-Z0-9]*$");
    }

    private boolean isValidUsername(String text) {
        if (text.length() < SignLogic.MIN_NAME_LENGTH || text.length() > SignLogic.MAX_NAME_LENGTH)
            return false;
        //check if text contains only letters and numbers
        return text.matches("^[a-zA-Z0-9]*$");
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // This method is intentionally left blank
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // This method is intentionally left blank
    }
}
