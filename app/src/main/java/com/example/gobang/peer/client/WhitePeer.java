package com.example.gobang.peer.client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonUiContext;
import androidx.annotation.UiThread;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.peer.ChessBoardASync;
import com.example.gobang.peer.CreationDialog;
import com.example.gobang.peer.PeerActivity;
import com.example.gobang.web.WebSocket;

import java.net.Socket;
import java.util.concurrent.*;

public class WhitePeer extends PeerActivity {
    WhitePeer self;
    private WebSocket anotherPlayer;
    private ChessBoardASync player;
    private TextView colorView;
    private TextView statusView;
    private Handler UIHandler;
    private CreationDialog preDialog;
    @UiThread
    private void init(){
        preDialog=new ClientDialog(self) {
            @Override
            public void onCreate(String address, int port) {
                switch (port){
                    case ClientDialog.THREAD_ERROR:
                        toast("程序因未知原因中斷，請聯絡開發者");
                        exit();
                        return;
                    case ClientDialog.FORMAT_ERROR:
                        toast("位址格式錯誤");
                        exit();
                        return;
                }
                webHandler.submit(()->{
                    try{
                        anotherPlayer=new WebSocket(new Socket(address, port));
                    }catch (Exception e){
                        toast("房間不存在");
                        exit();
                        return;
                    }
                    cancel();
                    UIHandler.post(()->loop());
                });
            }
        };
    }
    @UiThread
    private boolean ifContinue(int win){
        CreationDialog dialog=new CreationDialog(self);
        switch(win){
            case Color.BLACK:
                dialog.alert("遊戲結束","你輸了!","回主畫面",()->{
                    exit();
                });
                break;
            case Color.WHITE:
                dialog.alert("遊戲結束","你贏了!","回主畫面",()->{
                    exit();
                });
                break;
            case -2:
                dialog.alert("遊戲結束","平局!","回主畫面",()->{
                    exit();
                });
                break;
            default:
                return true;
        }
        return false;
    }
    private void loop(){
        statusView.setText("等待對手行動");
        webHandler.submit(()-> {
            byte[] point;
            try{
                point = anotherPlayer.receive();
            }catch (Exception e){
                Log.i("ERROR",e.toString());
                toast("與對手失去連線");
                exit();
                return;
            }
            UIHandler.post(()->{
                int win=player.placeChess(point[0],point[1]);
                if(ifContinue(win)){
                    statusView.setText("輪到你了");
                    player.onChessPlaceOnce((x,y)->{
                        int win1=player.placeChess(x,y);
                        if(ifContinue(win1)){
                            webHandler.submit(()->{
                                try{
                                    anotherPlayer.send(new byte[]{x,y});
                                }catch (Exception e){
                                    Log.i("ERROR",e.toString());
                                    toast("與對手失去連線");
                                    exit();
                                    return;
                                }
                                UIHandler.post(()->loop());
                            });
                        }else{
                            webHandler.submit(()->{
                                try{
                                    anotherPlayer.send(new byte[]{x,y});
                                }catch (Exception e){
                                    Log.i("ERROR",e.toString());
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peer);
        self=this;
        player=findViewById(R.id.imageView);
        colorView =findViewById(R.id.textView);
        colorView.setText("你的顏色:白");
        statusView=findViewById(R.id.textView4);
        statusView.setText("");
        UIHandler=new Handler(Looper.getMainLooper());
        webHandler=Executors.newSingleThreadExecutor();
        webHandler.submit(()->Looper.prepare());
        init();
    }

    @Override
    public void surrender(View view) {
        anotherPlayer.close();
    }
}
