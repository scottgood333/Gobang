package com.example.gobang.peer.server;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.UiThread;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.peer.ChessBoardASync;
import com.example.gobang.peer.CreationDialog;
import com.example.gobang.peer.PeerActivity;
import com.example.gobang.web.WebSocket;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackPeer extends PeerActivity {
    BlackPeer self;
    private WebSocket anotherPlayer;
    private ChessBoardASync player;
    private TextView statusView;
    private TextView colorView;
    private Handler UIHandler;
    private CreationDialog preDialog;
    @UiThread
    private void init(){
        preDialog=new ServerDialog(self){
            @Override
            public void onCreate(int port) {
                switch (port){
                    case ServerDialog.NOT_INT_ERROR:
                        toast("請輸入正整數");
                        exit();
                        return;
                    case ServerDialog.THREAD_ERROR:
                        toast("程序因未知原因中斷，請聯絡開發者");
                        exit();
                        return;
                    case ServerDialog.SOCKET_ERROR:
                        toast("無法取得 IP，請檢查你的網路功能");
                        exit();
                        return;
                }
                webHandler.submit(()->{
                    try{
                        ServerSocket server = new ServerSocket(port);
                        anotherPlayer=new WebSocket(server.accept());
                        server.close();
                    }catch (Exception e){
                        toast("房間建立失敗，請檢查你的網路功能");
                        exit();
                        return;
                    }
                    cancel();
                    UIHandler.post(()->loop());
                });
            }
        };
    }

    private boolean ifContinue(int win){
        CreationDialog dialog=new CreationDialog(self);
        switch(win){
            case Color.BLACK:
                dialog.alert("遊戲結束","你贏了!","回主畫面",()->{
                    exit();
                });
                break;
            case Color.WHITE:
                dialog.alert("遊戲結束","你輸了!","回主畫面",()->{
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
        statusView.setText("輪到你了");
        player.onChessPlaceOnce((x,y)->{
            int win=player.placeChess(x,y);
            if(ifContinue(win)){
                statusView.setText("等待對手行動");
                webHandler.submit(()->{
                    byte[] point;
                    try{
                        anotherPlayer.send(new byte[]{x,y});
                        point = anotherPlayer.receive();
                    }catch (Exception e){
                        Log.i("ERROR",e.toString());
                        toast("與對手失去連線");
                        exit();
                        return;
                    }
                    UIHandler.post(()->{
                        int win1=player.placeChess(point[0],point[1]);
                        if(ifContinue(win1))
                            loop();
                    });
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peer);
        self=this;
        player=findViewById(R.id.imageView);
        colorView =findViewById(R.id.textView);
        colorView.setText("你的顏色:黑");
        statusView=findViewById(R.id.textView4);
        statusView.setText("");
        UIHandler=new Handler(Looper.getMainLooper());
        webHandler= Executors.newSingleThreadExecutor();
        webHandler.submit(()->Looper.prepare());
        init();
    }

    @Override
    public void surrender(View view) {
        anotherPlayer.close();
    }
}
