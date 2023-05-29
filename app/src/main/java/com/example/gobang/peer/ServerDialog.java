package com.example.gobang.peer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import com.example.gobang.MainActivity;
import com.example.gobang.websocket.IPAddressUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerDialog {
    public final static int NOT_INT_ERROR=-1;
    public final static int SOCKET_ERROR=-2;
    public final static int THREAD_ERROR=-3;
    private AlertDialog alertDialog;
    private CompletableFuture<Integer> serverPort;
    private Handler handler;
    public ServerDialog(Context self){
        serverPort=new CompletableFuture<>();
        EditText portEditText = new EditText(self);
        portEditText.setInputType(InputType.TYPE_CLASS_NUMBER); // Set input type to phone to show numeric keyboard

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle("建立房間");
        dialogBuilder.setMessage("請輸入正整數作為 Port");
        dialogBuilder.setView(portEditText);
        AlertDialog.Builder ok = dialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                int port;
                try {
                    port = Integer.parseInt(portEditText.getText().toString());
                    if(port<=0)
                        throw new Exception();
                }catch (Exception e) {
                    serverPort.complete(NOT_INT_ERROR);
                    return;
                }
                List<String> ips;
                try{
                    ips = IPAddressUtil.getIPAddress();
                    if(ips.size()==0){
                        throw new Exception();
                    }
                }catch (Exception e){
                    serverPort.complete(SOCKET_ERROR);
                    return;
                }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
                dialogBuilder.setTitle("等待對手");
                String msg="請讓對手輸入以下 ip 位址以進入房間\n";
                for (String ip:ips) {
                    msg+=ip+":"+port+"\n";
                }
                dialogBuilder.setMessage(msg);
                dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent attractionIntent = new Intent(self, MainActivity.class);
                        self.startActivity(attractionIntent);
                    }
                });
                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                serverPort.complete(port);
            }
        });

        // Set the negative button and its click listener
        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent attractionIntent = new Intent(self, MainActivity.class);
                self.startActivity(attractionIntent);
            }
        });
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }
    public void cancel(){
        alertDialog.cancel();
    }
    public Integer getPort(){
        try{
            return serverPort.get();
        }catch (Exception e){
            return THREAD_ERROR;
        }
    }
}
