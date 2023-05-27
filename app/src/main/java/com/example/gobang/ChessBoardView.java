package com.example.gobang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardView extends View {

    Context context;
    private Paint mBitmapPaint;
    private Bitmap mBitmap, background;
    private Canvas mCanvas;
    //畫筆
    private Paint circlePaint, mPaint;
    //暫存使用者手指的X,Y座標
    private int curChessX, curChessY;
    private int curRound;

    private ArrayList<ChessItem> chessItems;
    private static final float TOUCH_TOLERANCE = 4;

    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // chess
        curChessY = 0;
        curChessX = 0;
        curRound = 0;
        chessItems = new ArrayList<ChessItem>();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.chess_board);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
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
        for(ChessItem chess: chessItems){
            circlePaint.setColor(chess.getChessColor());
            canvas.drawCircle(chess.getChessX(), chess.getChessY(), 30, circlePaint);
        }
    }

    /**覆寫:偵測使用者觸碰螢幕的事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
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
        curChessX = x;
        curChessY = y;
        curRound = ChessItem.chessCount;
        if(curRound%2==0){
            ChessItem newChess = new ChessItem(x,y,Color.BLACK);
            chessItems.add(newChess);
        }
        else {
            ChessItem newChess = new ChessItem(x,y,Color.WHITE);
            chessItems.add(newChess);
        }
        Log.i("TAG", "chess count: (" + ChessItem.chessCount + ")");
        Log.i("TAG", "moving: (" + x + ", " + y + ")");
    }

    public void reset(){
        this.curChessY = 0;
        this.curChessX = 0;
        this.curRound = 0;
        this.chessItems = null;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.chess_board);
        invalidate();
    }
}