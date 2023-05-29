package com.example.gobang;

import android.graphics.Color;
import android.util.Log;

public class Gobang {
    public Gobang(){
        chessMap=new int[13][13];
        round=0;
        lastMoveX=-1;
        lastMoveY=-1;
    }
    public int getRound(){
        return round;
    }
    public int getChessColor(int x,int y){
        return chessMap[x][y];
    }
    private int round;
    private final int[][] chessMap;

    private  int lastMoveX, lastMoveY;
    /* 根據 round 下一子，回傳勝利方顏色，沒人勝利則傳 0，滿子和局則回傳-2*/
    /*黑色值: -16777216，白色值: -1*/
    public int placeChess(int x, int y) {
        if(chessMap[x][y]==0) {
            if ((round++) % 2 == 0) {
                chessMap[x][y] = Color.BLACK;
            } else {
                chessMap[x][y] = Color.WHITE;
            }
            Log.i("round: ", String.valueOf(round));
            Log.i("color: ", String.valueOf(chessMap[x][y]));
            lastMoveX = x;
            lastMoveY = y;
        }
        if(round==169){
            return -2;
        }
        int l,r;
        for(l=0;x+l>=0&&chessMap[x+l][y]==chessMap[x][y];l--);
        for(r=0;x+r<13&&chessMap[x+r][y]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;y+l>=0&&chessMap[x][y+l]==chessMap[x][y];l--);
        for(r=0;y+r<13&&chessMap[x][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y+l>=0&&chessMap[x+l][y+l]==chessMap[x][y];l--);
        for(r=0;x+r<13&&y+r<13&&chessMap[x+r][y+r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        for(l=0;x+l>=0&&y-l<13&&chessMap[x+l][y-l]==chessMap[x][y];l--);
        for(r=0;x+r<13&&y-r>=0&&chessMap[x+r][y-r]==chessMap[x][y];r++);
        if(r-l>5)
            return chessMap[x][y];
        return 0;
    }
    /* 悔棋，僅有一次悔棋機會*/
    /* -1: 尚未開始對弈*/
    /* -2: 此回合已悔棋過*/
    public int repentChess() {
        if(round==0){return -1;}
        if(lastMoveX==-2||lastMoveY==-2){return -2;}
        chessMap[lastMoveX][lastMoveY] = 0;
        lastMoveX = lastMoveY = -2;
        round--;
        return 0;
    }
    public void reset(){
        for(int i=0;i<13;i++){
            for(int j=0;j<13;j++){
                chessMap[i][j]=0;
            }
        }
        round=0;
    }
}
