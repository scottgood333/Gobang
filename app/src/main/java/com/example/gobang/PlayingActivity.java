package com.example.gobang;

import android.app.Activity;
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
    private TextView turnView, repentMsgView;
    /* Called by chessBoard when it is clicked
    * This method display this turn is whose turn */
    public void setTurnText(String text){
        turnView.setText(text);
    }
    public void setRepentMsgText(String text){repentMsgView.setText(text);}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        chessBoard = findViewById(R.id.imageView);
        turnView=findViewById(R.id.textView);
        repentMsgView=findViewById(R.id.textView2);
        self=this;
        chessBoard.setParent(this);
    }
    public void restart_listener(View view){
        chessBoard.reset();
    }

    public  void repent_listener(View view){chessBoard.repentChess();}

    public  void back_to_home_listener(View view){
        Intent attractionIntent = new Intent(this, MainActivity.class);
        startActivity(attractionIntent);
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
        }else if(color==Color.WHITE) {
            builder.setMessage("白色贏了!");
        }
        else if(color==-2){
            builder.setMessage("和局!");
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
