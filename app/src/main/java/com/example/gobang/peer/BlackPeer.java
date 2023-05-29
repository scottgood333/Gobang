package com.example.gobang.peer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gobang.MainActivity;
import com.example.gobang.R;
import com.example.gobang.websocket.WebSocket;

import java.net.ServerSocket;

public class BlackPeer extends PeerActivity {
    BlackPeer self;
    private WebSocket anotherPlayer;
    private ChessBoardSync player;
    private Thread roundHandler;
    private TextView statusView;
    private TextView colorView;
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
        roundHandler=new Thread(new Runnable() {
            @Override
            public void run() {
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
                    attractionIntent = new Intent(self, MainActivity.class);
                    startActivity(attractionIntent);
                    return;
                }
                sd.cancel();
                try{
                    byte[] point;
                    while(true){
                        statusView.setText("輪到你了");
                        Toast.makeText(self,"輪到你了",Toast.LENGTH_LONG).show();
                        point = player.getTouchPointSync();
                        anotherPlayer.send(point);
                        player.placeChess(point[0],point[1]);

                        statusView.setText("等待對手行動");
                        Toast.makeText(self,"等待對手行動",Toast.LENGTH_LONG).show();
                        point = anotherPlayer.receive();
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
