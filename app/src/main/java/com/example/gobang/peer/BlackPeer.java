package com.example.gobang.peer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.peer.chessboard.ChessBoardASync;
import com.example.gobang.websocket.WebSocket;

import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackPeer extends PeerActivity {
    BlackPeer self;
    private WebSocket anotherPlayer;
    private ChessBoardASync player;
    private ExecutorService webHandler;
    private TextView statusView;
    private TextView colorView;
    private Handler UIHandler;
    private byte[] point;
    private void init(){
        Looper.prepare();
        Intent attractionIntent;
        ServerDialog sd=new ServerDialog(self);
        int port=sd.getPort();
        switch (port){
            case ServerDialog.NOT_INT_ERROR:
                Toast.makeText(self,"請輸入正整數",Toast.LENGTH_LONG).show();
                attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
                return;
            case ServerDialog.THREAD_ERROR:
                Toast.makeText(self,"程序因未知原因中斷，請聯絡開發者",Toast.LENGTH_LONG).show();
                attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
                return;
            case ServerDialog.SOCKET_ERROR:
                Toast.makeText(self,"無法取得 IP，請檢查你的網路功能",Toast.LENGTH_LONG).show();
                attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
                return;
        }
        ServerSocket server=null;
        try{
            server = new ServerSocket(port);
            anotherPlayer=new WebSocket(server.accept());
            server.close();
        }catch (Exception e){
            Toast.makeText(self,"房間建立失敗，請檢查你的網路功能",Toast.LENGTH_LONG).show();
            exit();
            return;
        }
        sd.cancel();
        UIHandler.post(()->loop());
    }
    private void loop(){
        statusView.setText("輪到你了");
        Toast.makeText(self,"輪到你了",Toast.LENGTH_LONG).show();
        player.onChessPlaceOnce((x,y)->{
            point=new byte[]{x,y};
            player.placeChess(x,y);
            statusView.setText("等待對手行動");
            Toast.makeText(self,"等待對手行動",Toast.LENGTH_LONG).show();
            webHandler.submit(()->{
                try{
                    anotherPlayer.send(point);
                    point = anotherPlayer.receive();
                    player.placeChess(point[0],point[1]);
                }catch (Exception e){
                    Log.i("ERROR",e.toString());
                    exit();
                    return;
                }
                UIHandler.post(()->loop());
            });
        });
    }
    private void exit(){
        Intent attractionIntent = new Intent(self, MainActivity.class);
        startActivity(attractionIntent);
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
        webHandler.submit(()->init());
    }

    @Override
    public void surrender(View view) {
        anotherPlayer.close();
    }
}
