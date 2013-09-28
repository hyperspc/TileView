package com.qozix.tileview.paths;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.qozix.layouts.StaticLayout;
import com.qozix.tileview.detail.DetailManager;

/**
 * Canvas.drawPath takes a lot of resources.  Using Canvas.drawLines is more performant,
 * but looks terrible for anything beyond a 1px plain line.  Creating and managing a bitmap
 * either scales the thickness of the path, or if you manage that, ends up taking as much
 * juice as Canvas.drawPath.  After many iterations, the default implementation just offers
 * path drawing via Canvas.drawPath.  If you are using very large paths, or need more customization,
 * consider a custom implementation (most likely a custom View subclass added as child view of the
 * TileView, with an overriden onDraw method that manages paths in whatever fashion is most
 * appropriate to your need, or possibly a SurfaceView, or even OpenGL.
 * ref (http://stackoverflow.com/a/15208783/429430)
 */
public class PathManager extends StaticLayout {

	private static final int DEFAULT_STROKE_COLOR = 0x883399FF;
	private static final int DEFAULT_STROKE_WIDTH = 8;

	private boolean shouldDraw = true;

	private Paint defaultPaint = new Paint();
	{
		defaultPaint.setStyle( Paint.Style.STROKE );
		defaultPaint.setColor( DEFAULT_STROKE_COLOR );
		defaultPaint.setStrokeWidth( DEFAULT_STROKE_WIDTH );
		defaultPaint.setAntiAlias( true );
	}

	private DetailManager detailManager;

	private Path translatedPath = new Path();
	private Matrix matrix = new Matrix();

	private ArrayList<DrawablePath> paths = new ArrayList<DrawablePath>();

	public PathManager( Context context, DetailManager dm ) {
		super( context );
		setWillNotDraw( false );
		detailManager = dm;
	}

	public Paint getPaint() {
		return defaultPaint;
	}

	public DrawablePath addPath( List<Point> points ) {
		return addPath( points, defaultPaint );
	}

	public Path getPathFromPoints(List<Point> points) {
        Path path = new Path();
        Point start = points.get(0);
        path.moveTo((float) start.x, (float) start.y);
        int l = points.size();
        for (int i = 1; i < l; i++) {
            Point point = points.get(i);
            path.lineTo((float) point.x, (float) point.y);
        }
        return path;
	}
	
	public DrawablePath addPath( List<Point> points, Paint paint ) {
		return addPath( getPathFromPoints(points), paint );
	}

	public DrawablePath addPath( Path path, Paint paint ) {
		DrawablePath drawablePath = new DrawablePath();
		drawablePath.path = path;
		drawablePath.paint = paint;
		return addPath( drawablePath );
	}

	public DrawablePath addPath( Path path ) {
		return addPath( path, defaultPaint );
	}

	public DrawablePath addPath( DrawablePath drawablePath ) {
		paths.add( drawablePath );
		invalidate();
		return drawablePath;
	}

	public void removePath( DrawablePath path ) {
		paths.remove( path );
		invalidate();
	}

	public void clear() {
		paths.clear();
		invalidate();
	}

	public void setShouldDraw( boolean should ) {
		shouldDraw = should;
		invalidate();
	}

	@Override
	public void onDraw( Canvas canvas ) {
		if ( shouldDraw ) {
			float scale = (float) detailManager.getScale();
			matrix.setScale( scale, scale );
			for ( DrawablePath drawablePath : paths ) {
				translatedPath.set( drawablePath.path );
				translatedPath.transform( matrix );
				drawablePath.draw(canvas, translatedPath);
				
//				if ( !canvas.quickReject( drawingPath, Canvas.EdgeType.BW ) ) {
//					canvas.drawPath( drawingPath, drawablePath.paint );
//				}
			}
		}
		super.onDraw( canvas );
	}

}
