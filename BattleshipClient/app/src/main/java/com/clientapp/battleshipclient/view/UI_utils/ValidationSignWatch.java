package com.clientapp.battleshipclient.view.UI_utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.clientapp.battleshipclient.logic.SignLogic;
import com.clientapp.battleshipclient.view.activities.SignActivity;

public class ValidationSignWatch implements TextWatcher {
    private EditText editText;
    private InputType textType;
    private Context context;

    public enum InputType{
        USERNAME, PASSWORD
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

        if (textType == InputType.USERNAME) {
            ((SignActivity) context).setUsernameValid(isValid);
        } else { //PASSWORD
            ((SignActivity) context).setPasswordValid(isValid);
        }
    }



    private boolean isValidInput(String text) {
        switch (textType) {
            case USERNAME:
                return isValidUsername(text);
            case PASSWORD:
                return isValidPassword(text);
            default:
                return false;
        }
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
