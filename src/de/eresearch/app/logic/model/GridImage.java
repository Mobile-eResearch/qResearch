package de.eresearch.app.logic.model;

import android.content.Context;

public class GridImage extends android.widget.ImageView{
    //speichert reihe und spalte ({-1,-1} für nicht im grid)
    int row=-1, col=-1, inih=0, iniw=0;
    public GridImage(Context context) {
        super(context);
    }
    public void setRow(int r){
        row = r;
    }
    public int getRow(){
        return row;
    }
    public void setCol(int c){
        col = c;
    }
    public int getCol(){
        return col;
    }
    public int getInitialWidth(){
        return iniw;
    }
    public int getInitialHeight(){
        return inih;
    }
    public void setInitialWidth(int width){
        iniw = width;
    }
    public void setInitialHeight(int height){
        inih = height;
    }
}
