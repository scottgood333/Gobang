package com.example.gobang.peer.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import androidx.annotation.UiThread;
import com.example.gobang.MainActivity;
import com.example.gobang.peer.CreationDialog;
import com.example.gobang.peer.PeerActivity;
import com.example.gobang.web.IPAddressUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ServerDialog extends CreationDialog {
    public final static int NOT_INT_ERROR=-1;
    public final static int SOCKET_ERROR=-2;
    public final static int THREAD_ERROR=-3;
    @UiThread
    public abstract void onCreate(int port);
    @UiThread
    public ServerDialog(Context self){
        super(self);
        input("建立房間","請輸入正整數作為 Port",InputType.TYPE_CLASS_NUMBER,"確定",data->{
            int port;
            try {
                port = Integer.parseInt(data);
                if(port<=0)
                    throw new Exception();
            }catch (Exception e) {
                onCreate(NOT_INT_ERROR);
                return;
            }
            List<String> ips;
            try{
                ips = IPAddressUtil.getIPAddress();
                if(ips.size()==0){
                    throw new Exception();
                }
            }catch (Exception e){
                onCreate(SOCKET_ERROR);
                return;
            }
            String msg="請讓對手輸入以下 ip 位址以進入房間\n";
            for (String ip:ips) {
                msg+=ip+":"+port+"\n";
            }
            alert("等待對手",msg,"取消",()->{
                exit();
            });
            onCreate(port);
        },"取消",data->{
            exit();
        });
    }
}
