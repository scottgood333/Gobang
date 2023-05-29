package com.example.gobang;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class ChessBoardView extends View {

    Context context;
    private final Paint mBitmapPaint;
    private final Bitmap background;
    //畫筆
    private final Paint circlePaint;
    private float gridSize;
    private final Gobang game;
    private PlayingActivity parent;
    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // chess
        gridSize =getHeight()/13;
        game=new Gobang();
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

    public void setParent(PlayingActivity parent) {
        this.parent = parent;

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
                circlePaint.setColor(game.getChessColor(x,y));
                canvas.drawCircle(x*gridSize+gridSize/2, y*gridSize+gridSize/2, 30, circlePaint);
            }
        }
    }

    /**覆寫:偵測使用者觸碰螢幕的事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() / gridSize);
        int y = (int) (event.getY() / gridSize);

        // 確保x, y 不會out of bound
        if (x == 13){ x = 12;}
        if (y == 13){ y = 12;}

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //下一子並且看有沒有人贏
                parent.showMessageIfWin(game.placeChess(x,y));
                parent.setRepentMsgText("");
                //現在是誰
                if(game.getRound()%2==0){
                    parent.setTurnText("現在輪到:黑");
                }else{
                    parent.setTurnText("現在輪到:白");
                }
                break;
        }

        invalidate();
        return true;
    }

    public void reset(){
        game.reset();
        parent.setTurnText("現在輪到:黑");
        parent.setRepentMsgText("");
        invalidate();
    }

    // 悔棋
    public void repentChess(){
        int ret = game.repentChess();
        if(game.getRound()%2==0){
            parent.setTurnText("現在輪到:黑");
        }else{
            parent.setTurnText("現在輪到:白");
        }
        if (ret==-1){
            parent.setRepentMsgText("你還沒有下棋！");
        }
        else if (ret==-2){
            parent.setRepentMsgText("你只能悔棋一次！");
        }
        invalidate();
    }
}