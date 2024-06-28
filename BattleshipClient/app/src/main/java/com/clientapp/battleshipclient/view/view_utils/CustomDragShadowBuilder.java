package com.clientapp.battleshipclient.view.view_utils;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import androidx.annotation.NonNull;



/*
*  CustomDragShadowBuilder is a class that extends View.DragShadowBuilder.
*  It is used to create a custom drag shadow for the view that is being dragged.
*  The class overrides the onProvideShadowMetrics and onDrawShadow methods.
*
* */
public class CustomDragShadowBuilder extends View.DragShadowBuilder {


    /*
    *  CustomDragShadowBuilder constructor
    * @param view - the view that is being dragged
    * */
    public CustomDragShadowBuilder(View view) {
        super(view);
    }




    /*
    * onProvideShadowMetrics method is called when the system needs the size of the drag shadow.
    * The method sets the size of the shadow and the touch point of the shadow.
    * @param outShadowSize - the size of the shadow
    * @param outShadowTouchPoint - the touch point of the shadow
    * */
    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        View view = this.getView();
        if (view == null)
            return;

        if (view.getRotation() == 90)
        {
            outShadowSize.set(view.getHeight(), view.getWidth());
            outShadowTouchPoint.set(outShadowSize.x / 2, outShadowSize.y / 2);
        }
        else
        {
            outShadowSize.set(view.getWidth(), view.getHeight());
            outShadowTouchPoint.set(outShadowSize.x / 2, outShadowSize.y / 2);
        }
    }



    /*
    *  onDrawShadow method is called when the system needs to draw the drag shadow.
    *  The method draws the shadow on the canvas.
    *  @param canvas - the canvas on which the shadow is drawn
    * */
    public void onDrawShadow(@NonNull Canvas canvas) {
        View view = this.getView();
        if (view == null)
            return;

        canvas.save(); // save current state
        if (view.getRotation() == 90)
        {
            canvas.translate(view.getHeight(), 0);
            canvas.rotate(90);
        }

        view.draw(canvas);
        canvas.restore(); // restore state (before applying affine transform (rotate,translate))

    }
}
