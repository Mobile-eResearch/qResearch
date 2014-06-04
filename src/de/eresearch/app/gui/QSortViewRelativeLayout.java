package de.eresearch.app.gui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.RelativeLayout;

public class QSortViewRelativeLayout extends RelativeLayout {

    private int mWindowWidth, mPyramidHeight, mPyramidWidth, mItemScrollViewHeight, mViewWidth, mViewHeight, mGridWidth, mGridHeight, mXOffset, mYOffset, startX, startY;
    private Paint mColor;
    private boolean [][] mPyramidArray;

    public QSortViewRelativeLayout(Context context) {
        super(context);
    }
    public QSortViewRelativeLayout(Context context,boolean[][] pyramid, int relLayoutWidth, int relLayoutHeight, int pictureSourceHeight) {
        super(context);
        
        mPyramidArray = pyramid;
        int[] widthHeight = getMaxPyramidWidthHeight();
        mPyramidHeight = widthHeight[1];
        mPyramidWidth = widthHeight[0];
        startX = widthHeight[2];
        startY = widthHeight[3];
        mColor = new Paint();
        mViewWidth = relLayoutWidth;
        mViewHeight = relLayoutHeight;
        mItemScrollViewHeight = pictureSourceHeight;
        mXOffset =(int) (mViewWidth*.125f);
        mYOffset =(int) (mViewHeight*.02f);
        mGridHeight = ((mViewHeight-mItemScrollViewHeight)-2*mYOffset)/mPyramidHeight;
        mGridWidth = (mViewWidth-2*mXOffset)/mPyramidWidth;

    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mColor.setStrokeWidth(3);
        //kinda not so dark blue
        /*mColor.setColor(0xFFddeafd);
        //draw the pyramid
        //filled rectangles
        mColor.setStyle(Paint.Style.FILL);
        for(int i=0;i<mPyramidHeight;i++)
            for(int k=0;k<mPyramidWidth;k++)
                if(this.mPyramidArray[startX+k][startY+i])
                    canvas.drawRect(mXOffset+k*mGridWidth, mYOffset+i*mGridHeight, mXOffset+(k+1)*mGridWidth, mYOffset+(i+1)*mGridHeight, mColor);
        */
        mColor.setStyle(Paint.Style.STROKE);
      //very dark blue
        mColor.setColor(0xFF2645a6);
      //bordering of rectangels
        
        for(int i=0;i<mPyramidHeight;i++)
            for(int k=0;k<mPyramidWidth;k++)
                if(this.mPyramidArray[startX+k][startY+i])
                    canvas.drawRect(mXOffset+k*mGridWidth, mYOffset+i*mGridHeight, mXOffset+(k+1)*mGridWidth, mYOffset+(i+1)*mGridHeight, mColor);
        /*
         * code for line-shit
        mColor.setColor(0xFFddeafd);
        mColor.setStrokeWidth(1);
          
          for(int i=0;i<mPyramidHeight;i++)
              for(int k=0;k<mPyramidWidth;k++)
                  if(this.mPyramidArray[startX+k][startY+i])
                      canvas.drawRect(mXOffset+k*mGridWidth, mYOffset+i*mGridHeight, mXOffset+(k+1)*mGridWidth, mYOffset+(i+1)*mGridHeight, mColor);
        */
        
        //draw line on top of linear layout
        mColor.setStrokeWidth(3);
        //same colour as very dark blue
        mColor.setColor(0xFF2645a6);
        canvas.drawLine(0, (int) (1.5*mYOffset)+mPyramidHeight*mGridHeight, mWindowWidth, (int) (1.5*mYOffset)+mPyramidHeight*mGridHeight, mColor);
        //canvas.drawRect(left, top, right, bottom, paint)
    }
    
    public int[] getMaxPyramidWidthHeight(){
        int startx=13, starty = 13, endx = 0, endy = 0;
        for(int i=0;i<mPyramidArray.length;i++){
            for(int j=0;j<mPyramidArray[0].length;j++){
                if(mPyramidArray[i][j]){
                    startx = (startx>i)? i:startx;
                    starty = (starty>j)? j:starty;
                    endx = (endx<i)? i:endx;
                    endy = (endy<j)? j:endy;
                }
            }
        }
        return new int[] {(endx-startx)+1,(endy-starty)+1,startx,starty};
    }
    
    /**
     * gets the y-offset
     * @return yOffset
     */
    public int getYOffset(){
    	return mYOffset;
    }
    
    /**
     * gets the x-offset
     * @return xOffset
     */
    public int getXOffset(){
    	return mXOffset;
    }
    
    /**
     * gets width of pyramid's grid
     * @return gridWidth
     */
    public int getGridWidth(){
    	return mGridWidth;
    }
    
    /**
     * gets height of pyramid's grid
     * @return gridHeight
     */
    public int getGridHeight(){
    	return mGridHeight;
    }
    public int getXStart(){
    	return startX;
    }
    public int getYstart(){
    	return startY;
    }
    public int getPyramidWidth(){
        return mPyramidWidth;
    }
    public int getPyramidHeight(){
        return mPyramidHeight;
    }
}
