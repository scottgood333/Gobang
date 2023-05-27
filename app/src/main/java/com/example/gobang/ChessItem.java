package com.example.gobang;

import android.graphics.Color;
public class ChessItem {
    public static int chessCount;
    private int chessX, chessY;
    private int chessColor;

    static {
        chessCount = 0;
    }

    public ChessItem(int x, int y, int color){
        this.chessX = x;
        this.chessY = y;
        this.chessColor = color;
        chessCount++;
    }

    public float getChessX() {
        return chessX;
    }

    public float getChessY() {
        return chessY;
    }

    public int getChessColor() {
        return chessColor;
    }
}
