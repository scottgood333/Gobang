package com.example.gobang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.gobang.peer.server.BlackPeer;
import com.example.gobang.peer.client.WhitePeer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playing_listener(View view)
    {
        Intent attractionIntent = new Intent(this, PlayingActivity.class);
        startActivity(attractionIntent);
    }
    public void black(View view){
        Intent attractionIntent = new Intent(this, BlackPeer.class);
        startActivity(attractionIntent);
    }
    public void white(View view){
        Intent attractionIntent = new Intent(this, WhitePeer.class);
        startActivity(attractionIntent);
    }
}