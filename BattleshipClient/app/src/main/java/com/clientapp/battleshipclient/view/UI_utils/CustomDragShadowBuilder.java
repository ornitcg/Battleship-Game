package com.clientapp.battleshipclient.view.UI_utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class CustomDragShadowBuilder extends View.DragShadowBuilder {



    private Bitmap shipBitmap;

    public CustomDragShadowBuilder(View view) {
        super(view);

        // get the src of the imageview
        Drawable drawable = ((ImageView) view).getDrawable();
// use drawable to decode the image
        int width = view.getWidth();
        int height = view.getHeight();
        shipBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shipBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.set(shipBitmap.getWidth(), shipBitmap.getHeight());
        shadowTouchPoint.set(shipBitmap.getWidth() , shipBitmap.getHeight() );
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        // Draw the bitmap of the ship onto the canvas
        canvas.drawBitmap(shipBitmap, 0, 0, null);
    }


}
