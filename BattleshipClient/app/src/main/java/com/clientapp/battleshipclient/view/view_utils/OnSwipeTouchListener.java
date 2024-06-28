package com.clientapp.battleshipclient.view.view_utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public  class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private  SwipeGestureListener listener = null;

    public OnSwipeTouchListener(Context context) {
        if (context instanceof SwipeGestureListener) {
            this.listener = (SwipeGestureListener) context;
        } else {
            Log.e("OnSwipeTouchListener", "Context must implement SwipeGestureListener");
        }
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD) { //swipe to the sides
                if (diffX > 0) {
                    if (listener!= null) listener.onSwipeRight();
                } else {
                    if (listener!= null) listener.onSwipeLeft();
                }
            }
            return false;
        }
    }
}




