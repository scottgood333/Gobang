package com.example.gobang.peer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import com.example.gobang.MainActivity;

import java.util.concurrent.CompletableFuture;

public class ClientDialog {
    public static class HostInfo{
        public final String address;
        public final int port;
        public HostInfo(String addressPort) throws Exception{
            String[] temp=addressPort.split(":");
            if(temp.length==2){
                this.address=temp[0];
                this.port=Integer.parseInt(temp[1]);
                if(this.port<=0)
                    throw new Exception();
            }else{
                throw new Exception();
            }
        }
        public HostInfo(int error){
            this.address="";
            this.port=error;
        }
    }
    public final static int FORMAT_ERROR =-1;
    public final static int THREAD_ERROR=-3;
    private AlertDialog alertDialog;
    private CompletableFuture<HostInfo> hostInfo;
    private Handler handler;
    public ClientDialog(Context self){
        hostInfo=new CompletableFuture<>();
        EditText portEditText = new EditText(self);
        portEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle("進入房間");
        dialogBuilder.setMessage("請輸入房間位址 (包含 port)");
        dialogBuilder.setView(portEditText);
        AlertDialog.Builder ok = dialogBuilder.setPositiveButton("確定", (dialog, which) -> {
            dialog.cancel();
            HostInfo temp;
            try {
                temp=new HostInfo(portEditText.getText().toString());
            }catch (Exception e) {
                hostInfo.complete(new HostInfo(FORMAT_ERROR));
                return;
            }
            AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(self);
            dialogBuilder1.setTitle("進入房間");
            dialogBuilder1.setMessage("正在嘗試連線");
            AlertDialog.Builder ok1 = dialogBuilder1.setPositiveButton("取消連線", (dialog1, which1) -> {
                dialog1.cancel();
                Intent attractionIntent = new Intent(self, MainActivity.class);
                self.startActivity(attractionIntent);
            });
            alertDialog = dialogBuilder1.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            hostInfo.complete(temp);
        });
        dialogBuilder.setNegativeButton("取消", (dialog, which) -> {
            dialog.cancel();
            Intent attractionIntent = new Intent(self, MainActivity.class);
            self.startActivity(attractionIntent);
        });
        handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            alertDialog = dialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });
    }
    public void cancel(){
        alertDialog.cancel();
    }
    public HostInfo getHostInfo(){
        try{
            return hostInfo.get();
        }catch (Exception e){
            return new HostInfo(THREAD_ERROR);
        }
    }
}
