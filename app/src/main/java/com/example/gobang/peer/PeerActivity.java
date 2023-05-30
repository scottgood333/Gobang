package com.example.gobang.peer;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gobang.MainActivity;

import java.util.concurrent.ExecutorService;

abstract public class PeerActivity extends AppCompatActivity {
    public abstract void surrender(View view);
    protected void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
    protected void exit(){
        Intent attractionIntent = new Intent(this, MainActivity.class);
        startActivity(attractionIntent);
    }
    protected ExecutorService webHandler;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webHandler.shutdown();
    }
}
