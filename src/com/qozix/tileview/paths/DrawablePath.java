package com.qozix.tileview.paths;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawablePath
{

    public Path  path;
    public Paint paint;

    /**
     * Draw simple line as path
     * @param translatedPath 
     */
    public void draw(final Canvas canvas, final Path translatedPath)
    {
        canvas.drawPath(translatedPath, this.paint);
    }

}
