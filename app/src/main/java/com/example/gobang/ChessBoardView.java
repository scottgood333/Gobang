package com.example.gobang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ChessBoardView extends View {

    Context context;
    private final Paint mBitmapPaint;
    private final Bitmap background;
    //畫筆
    private final Paint circlePaint;
    private int[][] chessMap;
    private static final float TOUCH_TOLERANCE = 4;
    private float gridSize;
    private int round=0;
    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // chess
        gridSize =getHeight()/13;
        chessMap=new int[13][13];
        round=0;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        gridSize =getHeight()/13;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //不要背景圖的話，請從這邊刪
        @SuppressLint("DrawAllocation")
        Bitmap res = Bitmap.createScaledBitmap(background
                ,getWidth(),getHeight(),true);
        canvas.drawBitmap(res,0,0,mBitmapPaint);
        //到這邊

        //畫圓圈圈
        for(int x=0;x<13;x++){
            for(int y=0;y<13;y++){
                if(chessMap[x][y]!=0){
                    circlePaint.setColor(chessMap[x][y]);
                    canvas.drawCircle(x*gridSize+gridSize/2, y*gridSize+gridSize/2, 30, circlePaint);
                }
            }
        }
    }

    /**覆寫:偵測使用者觸碰螢幕的事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() / gridSize);
        int y = (int) (event.getY() / gridSize);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                set_chess_point(x, y);
                invalidate();
                break;
        }
        invalidate();
        return true;

    }

    private void set_chess_point(int x, int y) {
        if(chessMap[x][y]==0) {
            if ((round++) % 2 == 0) {
                chessMap[x][y] = Color.BLACK;
            } else {
                chessMap[x][y] = Color.WHITE;
            }
            Log.i("TAG", "chess count: (" + round + ")");
            Log.i("TAG", "moving: (" + x + ", " + y + ")");
        }
    }

    public void reset(){
        round=0;
        chessMap=new int[13][13];
        invalidate();
    }
}