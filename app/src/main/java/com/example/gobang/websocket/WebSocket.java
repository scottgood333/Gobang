package com.example.gobang.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocket {
    public final InputStreamLV in;
    public final OutputStream out;
    public final Socket socket;
    public WebSocket(Socket socket)throws IOException {
        in = new InputStreamLV(socket.getInputStream());
        out = socket.getOutputStream();
        this.socket = socket;
    }
    public void send(byte[] data)throws Exception{
        out.write(data.length>>24);
        out.write(data.length>>16);
        out.write(data.length>>8);
        out.write(data.length);
        out.write(data);
    }
    public byte[] receive()throws Exception{
        byte[] data = in.readNBytes(4);
        int len = (int) data[0] << 24 | (int) data[1] << 16 | (int) data[2] << 8 | (int) data[3];
        return in.readNBytes(len);
    }
    public boolean close(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
