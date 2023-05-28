package com.example.gobang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.w3c.dom.Text;

public class PlayingActivity extends AppCompatActivity {
    private ChessBoardView chessBoard;
    private TextView turnView;
    /* Called by chessBoard when it is clicked
    * This method display this turn is whose turn */
    public void setTurnText(String text){
        turnView.setText(text);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        chessBoard = findViewById(R.id.imageView);
        turnView=findViewById(R.id.textView);
        self=this;
        chessBoard.setParent(this);
    }
    public void restart_listener(View view){
        chessBoard.reset();
    }

    private AppCompatActivity self;
    /* Called by chessBoard when it is clicked
     * if color is 0 means no winner, ignore
     * else show the winner and switch to main menu */
    public void showMessageIfWin(int color) {
        if(color==0)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("遊戲結束");
        if(color== Color.BLACK){
            builder.setMessage("黑色贏了!");
        }else{
            builder.setMessage("白色贏了!");
        }
        builder.setPositiveButton("回主畫面", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }
}
