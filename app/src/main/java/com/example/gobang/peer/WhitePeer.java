package com.example.gobang.peer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.websocket.WebSocket;

import java.net.ServerSocket;
import java.net.Socket;

public class WhitePeer extends PeerActivity {
    WhitePeer self;
    private WebSocket anotherPlayer;
    private ChessBoardSync player;
    private TextView colorView;
    private TextView statusView;
    private Thread roundHandler;
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
        roundHandler=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                ClientDialog cd=new ClientDialog(self);
                ClientDialog.HostInfo hostInfo=cd.getHostInfo();
                Intent attractionIntent;
                switch (hostInfo.port){
                    case ClientDialog.THREAD_ERROR:
                        Toast.makeText(self,"程序因未知原因中斷，請聯絡開發者",Toast.LENGTH_LONG).show();
                        attractionIntent = new Intent(self, MainActivity.class);
                        startActivity(attractionIntent);
                        return;
                    case ClientDialog.FORMAT_ERROR:
                        Toast.makeText(self,"位址格式錯誤",Toast.LENGTH_LONG).show();
                        attractionIntent = new Intent(self, MainActivity.class);
                        startActivity(attractionIntent);
                        return;
                }
                try{
                    anotherPlayer=new WebSocket(new Socket(hostInfo.address, hostInfo.port));
                }catch (Exception e){
                    Toast.makeText(self,"房間不存在",Toast.LENGTH_LONG).show();
                    attractionIntent = new Intent(self, MainActivity.class);
                    startActivity(attractionIntent);
                    return;
                }
                cd.cancel();
                try{
                    byte[] point;
                    while(true){
                        statusView.setText("等待對手行動");
                        Toast.makeText(self,"等待對手行動",Toast.LENGTH_LONG).show();
                        point = anotherPlayer.receive();
                        player.placeChess(point[0],point[1]);

                        statusView.setText("輪到你了");
                        Toast.makeText(self,"輪到你了",Toast.LENGTH_LONG).show();
                        point = player.getTouchPointSync();
                        anotherPlayer.send(point);
                        player.placeChess(point[0],point[1]);
                    }
                }catch (Exception e) {
                    Toast.makeText(self,"失去連線",Toast.LENGTH_LONG).show();
                }
                attractionIntent = new Intent(self, MainActivity.class);
                startActivity(attractionIntent);
            }
        });
        roundHandler.start();
    }

    @Override
    public void surrender(View view) {
        anotherPlayer.close();
    }
}
