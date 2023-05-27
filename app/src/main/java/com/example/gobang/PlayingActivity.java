package com.example.gobang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
//
//
//        Button button_restart;
//        button_restart = findViewById(R.id.button);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i("TAG", "touch: (" + x + ", " + y + ")");
                break;
        }
        return true;
    }

    public void restart_listener(){
//        ChessBoardView chessBoardView = (ChessBoardView) findViewById(R.id.imageView4);
//        chessBoardView.reset();
    }
}
