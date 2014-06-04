
package de.eresearch.app.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.view.TouchImageView;
import de.eresearch.app.logic.model.GridImage;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Pyramid;

import java.util.ArrayList;

public class QSortView extends View {

    public QSortView(Context context) {
        super(context);
        this.context = context;
    }

    public View getContent() {
        LinearLayout l = new LinearLayout(context);
        l.addView(mRelLayout);
        l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return l;
    }

    private int windowWidth, windowHeight, winXPercent, pictureSourcePosition, maxRowWidth,
            xInd = 0, counter;
    /**
     * layout for ScrollView with pictures
     */
    private LinearLayout mPictureSource;

    /**
     * containerview for big image and later tutorialvideo
     */
    private LinearLayout mLin;
    /**
     * Layout with pyramid and place to drag pictures aound the screen
     */
    private QSortViewRelativeLayout mRelLayout;

    /**
     * attributes for poles of pyramid
     */

    private TextView mAttrRight, mAttrLeft;

    /**
     * imagelist used for the QSortView only
     */
    private ArrayList<GridImage> mImageList;

    /**
     * Arrays for free/used and available fields in grid
     */
    private boolean[][] spots, mPyramidArray;

    /**
     * time for duration of the touch on screen, needed for TouchListener
     */
    private long time;

    /**
     * ScrollView for items
     */
    private HorizontalScrollView mScrollView;
    /**
     * buttons for moving the ScrollView
     */
    private Button butl, butr;
    private boolean reput = false, fingerOnScreen = false, resize = false, bigImg = false,
            drawonly = false, busy = false;
    private Context context;

    /**
     * list with pictures for the study
     */
    private ArrayList<Picture> mPictureList;

    private LoadImage ldImg;

    // Log log;

    public void onCreate(Point size, Pyramid pyramid, int subtractHeight,
            ArrayList<Picture> pictureList) {
        onCreate(size, pyramid, subtractHeight, pictureList, false);
    }

    /**
     * onCreate Method creates the QSortView. If you set variable drawOnly true,
     * then you can use it just for showing the pyramid with pictures, without
     * function to drag them around
     * 
     * @param size: Display display = getWindowManager().getDefaultDisplay();
     *            Point size = new Point(); display.getSize(size);
     * @param pyramid
     * @param subtractHeight = actionbarheight+statusbarheight
     * @param pictureList
     */

    public void onCreate(Point size, Pyramid pyramid, int subtractHeight,
            ArrayList<Picture> pictureList, boolean drawOnly) {
        ldImg = new LoadImage(context);
        drawonly = drawOnly;
        windowWidth = size.x;
        windowHeight = size.y;
        winXPercent = windowHeight / 6;
        if (drawonly)
            winXPercent = 0;
        pictureSourcePosition = windowHeight - winXPercent - subtractHeight;
        mPictureList = pictureList;

        if (!drawonly) {
            mPictureSource = new LinearLayout(context);
            mPictureSource.setOrientation(LinearLayout.HORIZONTAL);

        }

        RelativeLayout.LayoutParams pictureSourceParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // add the attributes for pyramid
        mAttrRight = new TextView(context);
        mAttrRight.setText(pyramid.getPoleRight());

        int textSize = 35;
        Paint pt = new Paint();
        Rect bounds = new Rect();
        int textwidth = 0;
        pt.setTypeface(mAttrRight.getTypeface());
        pt.setTextSize(textSize);
        pt.setStrokeWidth(2);
        pt.getTextBounds(pyramid.getPoleRight(), 0, pyramid.getPoleRight().length(), bounds);
        textwidth = bounds.width() + 4;
        subtractHeight += (bounds.height() + 4) * 2;
        int textheight = (bounds.height() - 6) * 3;
        mAttrRight.setTextSize(textSize);
        mAttrRight.setX(windowWidth - textwidth);
        mAttrRight.setY(pictureSourcePosition - textheight);
        // RelativeLayout.LayoutParams attrGood = new
        // RelativeLayout.LayoutParams(10, 45);

        mAttrLeft = new TextView(context);
        mAttrLeft.setText(pyramid.getPoleLeft());
        mAttrLeft.setTextSize(textSize);
        mAttrLeft.setY(pictureSourcePosition - textheight);

        /*
         * RelativeLayout.LayoutParams attrBadParams = new
         * RelativeLayout.LayoutParams
         * (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout
         * .LayoutParams.WRAP_CONTENT);
         * attrBadParams.setMargins(windowWidth-35*6, 0, 0, 0);
         * attrBad.setLayoutParams(attrBadParams);
         */

        mImageList = new ArrayList<GridImage>();

        mPyramidArray = pyramid.toBooleanArray();
        spots = new boolean[mPyramidArray.length][mPyramidArray[0].length];

        mRelLayout = new QSortViewRelativeLayout(context, mPyramidArray, windowWidth, windowHeight
                - subtractHeight, winXPercent);
        mRelLayout
                .setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));

        mRelLayout.setWillNotDraw(false);
        maxRowWidth = mRelLayout.getPyramidWidth();

        counter = 0;
        // for f in files:
        for (Picture f : mPictureList)
        {
            final LoadImage ldImage = new LoadImage(context, counter);

            ldImage.execute(f.getFilePath());
            mImageList.add(new GridImage(context));
            if (!drawonly)
                mImageList.get(counter).setOnTouchListener(touchy);
            else
                mImageList.get(counter).setOnClickListener(clickofski);
            // setze id des bildes auf die nummer an der in str der name des
            // bildes steht
            mImageList.get(counter).setId(counter);
            // füge das fertige bild dem linearlayout hinzu
            if (!drawonly)
                mPictureSource.addView(mImageList.get(counter));
            else {
                mImageList.get(counter).setRow(f.getRow());
                mImageList.get(counter).setCol(f.getColumn());
                mRelLayout.addView(mImageList.get(counter));
            }
            counter++;
        }

        if (!drawonly) {
            mScrollView = new HorizontalScrollView(context);
            mScrollView.addView(mPictureSource);
        }
        pictureSourceParams.setMargins(0, pictureSourcePosition, 0, 0);
        // scrollView.setLayoutParams(pictureSourceParams);

        if (!drawonly) {
            butl = new Button(context);
            butl.setId(1001);
            butl.setBackgroundResource(R.drawable.triangle_arrow_left);
            butl.setOnClickListener(clickofski);
            butr = new Button(context);
            butr.setId(1002);
            butr.setBackgroundResource(R.drawable.triangle_arrow_right);
            butr.setOnClickListener(clickofski);

            mScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, winXPercent,
                    0.1f));
            butl.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.95f));
            butr.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.95f));
            mLin = new LinearLayout(context);
            mLin.setOrientation(LinearLayout.HORIZONTAL);
            mLin.setGravity(Gravity.CENTER_VERTICAL);
            mLin.setLayoutParams(pictureSourceParams);
            mScrollView.requestDisallowInterceptTouchEvent(false);
            mLin.addView(butl);
            mLin.addView(mScrollView);
            mLin.addView(butr);
            mRelLayout.addView(mLin);
        }
        // relLayout.addView(scrollView);
        mRelLayout.addView(mAttrLeft);
        mRelLayout.addView(mAttrRight);

    }

    OnClickListener clickofski = new OnClickListener() {
        //XXX
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 1001:
                    mScrollView.post(new Runnable() {
                        public void run() {
                            mScrollView.smoothScrollBy(-mScrollView.getRight(), 0);
                        }
                    });
                    break;
                case 1002:
                    mScrollView.post(new Runnable() {
                        public void run() {
                            mScrollView.smoothScrollBy(mScrollView.getRight(), 0);
                        }
                    });
                    break;
                default:
                    try {
                        showBigImage(mPictureList.get(v.getId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }

    };

    View.OnTouchListener touchy = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int gridWidth = mRelLayout.getGridWidth();
            int gridHeight = mRelLayout.getGridHeight();
            int yOffset = mRelLayout.getYOffset();
            int xOffset = mRelLayout.getXOffset();
            boolean redraw = false, move = false;// , scroll = false;
            // init lpp für den fall dass v noch in lin ist
            RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            // hole x und y koordinaten (Raw gibt den absolutwert von der links
            // oberen ecke des geräts aus)
            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            // berechne den offset für die y koordinate
            int offsety = (windowHeight - mRelLayout.getHeight());
            // korrekturen für den notfall
            if (x_cord > windowWidth - v.getWidth() / 2) {
                x_cord = windowWidth - v.getWidth() / 2;
            }
            if (y_cord > windowHeight - (v.getHeight() / 2)) {
                y_cord = windowHeight - (v.getHeight() / 2);
            }
            if (x_cord < v.getWidth() / 2) {
                x_cord = v.getWidth() / 2;
            }
            if (y_cord < (v.getHeight() / 2 + offsety)) {
                y_cord = (v.getHeight() / 2 + offsety);
            }
            x_cord = x_cord - (v.getWidth() / 2);
            y_cord = y_cord - offsety - (v.getHeight() / 2);
            // hole motionevent action
            switch(event.getActionMasked()) {
            // frame in dem das objekt v angetippt wird
                case MotionEvent.ACTION_DOWN:
                    fingerOnScreen = true;
                    // hole systemzeit
                    time = System.currentTimeMillis();
                    // exception, falls v in lin ist. (LinearLayout.LayoutParams
                    // kann nicht zu RelativeLayout.Layoutparams getypecasted
                    // werden)
                    try {
                        lpp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    } catch (Exception e) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        // wird ausgeführt falls v in lin ist, entferne v aus
                        // lin und packe es in rel
                        // und speicher die position für den fall ds
                        // wiedereinfügens
                        xInd = mPictureSource.indexOfChild(v);
                        mPictureSource.removeView(v);
                        mRelLayout.addView(v);
                        // setzt wiedereinfügen, falls klick kürzer als 0,1s
                        reput = true;
                        // flag, dass bild bewegt wird
                        move = true;
                    }
                    break;
                // solange finger auf display
                case MotionEvent.ACTION_MOVE:
                    move = true;
                    redraw = true;
                    if (!busy && System.currentTimeMillis() - time > 100
                            && System.currentTimeMillis() - time < 200) {
                        if (v.getWidth() != ((GridImage) v).getInitialWidth()) {
                            busy = true;
                            ldImg = new LoadImage(context, v.getId());
                            ldImg.execute(mPictureList.get(v.getId()).getFilePath());
                        }
                    }
                    break;
                    
                // wenn finger vom bildschirm genommen wird

                case MotionEvent.ACTION_UP:
                    ldImg.cancel(true);
                    fingerOnScreen = false;
                    // falls berührung kürzer als 100ms
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    if (System.currentTimeMillis() - time < 100) {
                        showBigImage(mPictureList.get(v.getId()));
                        if (((GridImage) v).getCol() != -1) {
                            int[] out = attachToGrid((GridImage) v, ((GridImage) v).getCol(),
                                    ((GridImage) v).getRow());
                            x_cord = gridWidth
                                    * ((GridImage) v).getCol()
                                    + xOffset +(gridWidth-((GridImage) v).getWidth())/2;
                            y_cord = yOffset + gridHeight * ((GridImage) v).getRow()
                                    + (gridHeight - out[2]) / 2;
                            move = true;
                            break;
                        }
                        // zeige großes bild an
                        /*
                         * bigPictureView.setVisibility(View.VISIBLE);
                         * linLayout.setVisibility(View.VISIBLE); //zeige bild
                         * an bigPictureView.loadUrl("file:///android_asset/"+
                         * imageNames.get(v.getId()));
                         * //bigimg.loadUrl("file:///android_asset/"
                         * +str.get(v.getId()));
                         */
                        if (reput) {
                            mRelLayout.removeView(v);
                            mPictureSource.addView(v, xInd);
                            return false;
                        }
                        return false;
                    }
                    xInd = 0;
                    // hole koordinaten des bildmittelpunkt
                    int xx = (int) event.getRawX();
                    int yy = (int) event.getRawY();
                    // berechne den offset für die y koordinate
                    int oo = (windowHeight - mRelLayout.getHeight());
                    int[] koord = getgrid(xx, yy - oo, false);
                    // falls koordinaten im grid
                    if (koord[0] >= 0 && koord[1] >= 0) {
                        // falls freies feld
                        if (!spots[koord[0]][koord[1]]
                                || (koord[0] == ((GridImage) v).getCol() && koord[1] == ((GridImage) v)
                                        .getRow())) {
                            int[] out = attachToGrid((GridImage) v, koord[0], koord[1]);
                            if (out != null) {
                                if (out[0] != 0) {
                                    move = true;
                                    x_cord = gridWidth
                                            * koord[0]
                                            + xOffset+(gridWidth-out[1])/2;
                                    y_cord = yOffset + gridHeight * koord[1]
                                            + (gridHeight - out[2]) / 2;
                                }
                            }
                            // falls v im grid war, mache platze wieder frei
                            if (((GridImage) v).getCol() != -1) {
                                spots[((GridImage) v).getCol()][((GridImage) v).getRow()] = false;
                            }
                            // sage dem bild in welchem feld es ist
                            ((GridImage) v).setCol(koord[0]);
                            ((GridImage) v).setRow(koord[1]);
                            // feld jetzt belegt
                            spots[koord[0]][koord[1]] = true;
                        }
                        else if (((GridImage) v).getRow() != -1) {
                            int[] out = attachToGrid((GridImage) v, koord[0], koord[1]);
                            if (out != null) {
                                if (out[0] != 0) {
                                    move = true;
                                    x_cord = gridWidth
                                            * koord[0]
                                            + xOffset+(gridWidth-out[1])/2;
                                    y_cord = yOffset + gridHeight * koord[1]
                                            + (gridHeight - out[2]) / 2;
                                }
                            }
                            for (GridImage grdimg : mImageList) {
                                if (grdimg.getCol() == koord[0] && grdimg.getRow() == koord[1]) {
                                    RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) grdimg
                                            .getLayoutParams();
                                    int x_old = gridWidth
                                            * ((GridImage) v).getCol()
                                            + xOffset+2;
                                    int y_old = yOffset + gridHeight * ((GridImage) v).getRow()
                                            + (gridHeight - grdimg.getHeight()) / 2;
                                    llp.setMargins(x_old, y_old, 0, 0);
                                    grdimg.setLayoutParams(llp);
                                    grdimg.setCol(((GridImage) v).getCol());
                                    grdimg.setRow(((GridImage) v).getRow());
                                    mPictureList.get(grdimg.getId()).setColumn(
                                            ((GridImage) v).getCol());
                                    mPictureList.get(grdimg.getId()).setRow(
                                            ((GridImage) v).getRow());
                                }
                            }
                            // sage dem bild in welchem feld es ist
                            ((GridImage) v).setCol(koord[0]);
                            ((GridImage) v).setRow(koord[1]);
                            // feld jetzt belegt
                            spots[koord[0]][koord[1]] = true;
                        }
                        else {
                            // falls v im grid ist, mache platze wieder frei
                            if (((GridImage) v).getCol() != -1) {
                                spots[((GridImage) v).getCol()][((GridImage) v).getRow()] = false;
                            }
                            // falls kein freies feld
                            ((GridImage) v).setCol(-1);
                            ((GridImage) v).setRow(-1);
                        }
                    }
                    else {
                        int x_tmp = ((GridImage) v).getCol();
                        int y_tmp = ((GridImage) v).getRow();
                        if (y_tmp != -1 && x_tmp != -1)
                            spots[x_tmp][y_tmp] = false;
                        ((GridImage) v).setCol(koord[0]);
                        ((GridImage) v).setRow(koord[1]);
                    }
                    reput = false;
                    int oldcol = mPictureList.get(v.getId()).getColumn();
                    int oldrow = mPictureList.get(v.getId()).getRow();
                    mPictureList.get(v.getId()).setColumn(((GridImage) v).getCol());
                    mPictureList.get(v.getId()).setRow(((GridImage) v).getRow());
                    if (oldcol != mPictureList.get(v.getId()).getColumn()
                            || oldrow != mPictureList.get(v.getId()).getRow()) {
                     ((QSortActivity) context).insertLogEntry(oldcol,
                     oldrow,((GridImage) v).getCol(),((GridImage)
                     v).getRow(),CurrentQSort.getTimer().getTime(),mPictureList.get(v.getId()));
                     ((QSortActivity) context).setSortedItems(mPictureList);
                     ((QSortActivity) context).setStateOfPhase(isFinished());

                    }
                    int[] koordnew = getgrid(xx, yy - oo, true);
                    if (koord[0] == -1 && koordnew[1] >= mRelLayout.getPyramidHeight()) {
                        mRelLayout.removeView(v);
                        mPictureSource.addView(v);
                    }
                    break;
                default:
                    return false;
            } // bringe den imageview in den vordergrund und zeichne oberfläche
              // neu

            if (redraw) {
                v.bringToFront();
                mRelLayout.requestLayout();
                mRelLayout.invalidate();
            }
            if (move) {
                // setze neue x und y position des bildes mit koordinaten auf
                // dem relativelayout
                lpp.setMargins(x_cord, y_cord, 0, 0);
                // wende änderungen an
                v.setLayoutParams(lpp);
                move = false;
            }
            return true;
        }
    };

    // berechne koordinaten im grid
    private int[] getgrid(int x, int y, boolean outofbounds) {
        int gridWidth = mRelLayout.getGridWidth();
        int gridHeight = mRelLayout.getGridHeight();
        int xOffset = mRelLayout.getXOffset();
        int yOffset = mRelLayout.getYOffset();
        int startY = mRelLayout.getYstart();
        int startX = mRelLayout.getXStart();
        int row = (y - yOffset) / gridHeight;
        int col = (x - xOffset) / gridWidth;
        if (x < xOffset || col + startX >= mPyramidArray[0].length
                || row + startY >= mPyramidArray.length
                || y < yOffset || !mPyramidArray[col + startX][row + startY]) {
            if (!outofbounds)
                return new int[] {
                        -1, -1
                };
        }
        return new int[] {
                col, row
        };
    }

    /**
     * resizes the picture to the size of a pyramidcell
     * 
     * @param v
     * @param x
     * @param y
     * @return
     */
    private int[] attachToGrid(GridImage v, int x, int y) {
        int gridWidth = mRelLayout.getGridWidth();
        int gridHeight = mRelLayout.getGridHeight();
        int h = v.getInitialHeight(), w = v.getInitialWidth();
        resize = true;
        float hratio = w / ((float) gridWidth), vratio = h / ((float) gridHeight);
        if (hratio >= vratio) {
            int ww = v.getWidth();
            float widthfactor = ww / ((float) gridWidth - 4);
            int hh = (int) (v.getHeight() / widthfactor);
            final LoadImage ldImage = new LoadImage(gridWidth - 5, hh, v.getId());
            ldImage.execute(mPictureList.get(v.getId()).getFilePath());
            return new int[] {
                    1, gridWidth-4, hh
            };
        }
        else {
            int hh = v.getHeight();
            float heightfactor = hh / ((float) gridHeight - 4);
            int ww = (int) (v.getWidth() / heightfactor);
            final LoadImage ldImage = new LoadImage(ww, gridHeight - 5, v.getId());
            ldImage.execute(mPictureList.get(v.getId()).getFilePath());
            return new int[] {
                    1, ww, gridHeight-4
            };
        }
    }

    /*
     * private Drawable resize(Drawable image,int width, int height) { Bitmap b
     * = ((BitmapDrawable)image).getBitmap(); Bitmap bitmapResized =
     * Bitmap.createScaledBitmap(b, width, height, true); return new
     * BitmapDrawable(getResources(),bitmapResized); }
     */
    private Bitmap resize(Bitmap image, int width, int height) {
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * method is used to show the bigger image of item in a dialog with WebView
     * 
     * @param v
     */
    //XXX
    public void showBigImage(Picture v) {
        TouchImageView bigImageView = new TouchImageView(context);

        bigImageView.setImageBitmap(BitmapFactory.decodeFile(v
                .getFilePath()));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(bigImageView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * checks if the QSort can be finished: are all items sorted?
     * 
     * @return true when QSort is done
     */
    public boolean isFinished() {
        if (mPictureSource.getChildCount() > 0)
            return false;
        for (GridImage i : mImageList) {
            if (i.getCol() == -1 || i.getRow() == -1)
                return false;
        }
        return true;
    }

    public ArrayList<Picture> getPictureList() {
        return mPictureList;
    }

    /**
     * 
     * 
     *
     */
    private class LoadImage extends AsyncTask<String, Integer, String> {
        private Bitmap backs;
        private String st;
        private int width = 0, height = 0, id;

        // private Context context;
        public LoadImage(Context context) {
            // context.context = context;
        }

        /**
         * @param context
         * @param id
         */

        public LoadImage(Context context, int id) {
            this.id = id;
        }

        /**
         * @param width
         * @param height
         * @param id
         */

        public LoadImage(int width, int height, int id) {
            // context.context = context;
            this.height = height;
            this.width = width;
            this.id = id;
        }

        /**
         * 
         * 
         */

        @Override
        protected String doInBackground(String... f) {
            Bitmap back = null;
            st = f[0];
            // lade bild aus assets
            back = BitmapFactory.decodeFile(st);
            if (!drawonly) {
                if (height == 0 || width == 0) {
                    int h = back.getHeight();
                    float heightfactor = h / ((float) winXPercent);
                    int w = (int) (back.getWidth() / heightfactor);
                    // ändere größe des bildes auf 20% bildschirmhöhe ohne
                    // das
                    // seitenverhältnis zu ändern
                    width = w;
                    height = winXPercent;
                }
                backs = resize(back, width, height);
            }
            else {
                int gridWidth = mRelLayout.getGridWidth();
                int gridHeight = mRelLayout.getGridHeight();
                int h = back.getHeight(), w = back.getWidth();
                resize = true;
                float hratio = w / ((float) gridWidth), vratio = h / ((float) gridHeight);
                if (hratio >= vratio) {
                    float widthfactor = w / ((float) gridWidth - 4);
                    h = (int) (h / widthfactor);
                    backs = resize(back, gridWidth - 4, h);
                }
                else {
                    float heightfactor = h / ((float) gridHeight - 5);
                    w = (int) (w / heightfactor);
                    backs = resize(back, w, gridHeight - 5);
                }
            }
            return null;
        }

        /**
         * 
         */
        @Override
        protected void onPostExecute(String result) {
            if (!drawonly) {
                if (mImageList.get(id).getHeight() == 0 || fingerOnScreen) {
                    mImageList.get(id).setInitialHeight(height);
                    mImageList.get(id).setInitialWidth(width);
                    mImageList.get(id).setImageBitmap(backs);
                }
                else if (resize) {
                    mImageList.get(id).setImageBitmap(backs);
                    resize = false;
                }
            }
            else {
                int gridWidth = mRelLayout.getGridWidth();
                int gridHeight = mRelLayout.getGridHeight();
                int yOffset = mRelLayout.getYOffset();
                mImageList.get(id).setImageBitmap(backs);
                int x_cord = gridWidth
                        * mImageList.get(id).getCol()
                        + mRelLayout.getXOffset()+(gridWidth-backs.getWidth())/2;
                int y_cord = gridHeight * mImageList.get(id).getRow()
                        + (gridHeight - backs.getHeight()) / 2;
                RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpp.setMargins(x_cord, y_cord + yOffset, 0, 0);
                // wende änderungen an
                mImageList.get(id).setLayoutParams(lpp);
            }
            busy = false;
        }
    }

}
