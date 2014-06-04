
package de.eresearch.app.logic.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import de.eresearch.app.util.Util;

public class Pyramid {

    /** The maximum width of a pyramid */
    public static final int GRID_WIDTH = 13;

    /** The maximum height of a pyramid */
    public static final int GRID_HEIGHT = 13;

    /** Color, when a cell of the grid is checked */
    private static final int COLOR_GRID_CHECKED = Color.rgb(0, 102, 150);

    /** Color, when a cell of the grid is unchecked */
    private static final int COLOR_GRID_UNCHECKED = Color.LTGRAY;

    /** The pyramid's id */
    private int mId;

    /** The pole on the left side */
    private String mPoleLeft;

    /** The pole on the right side */
    private String mPoleRight;

    /** The view to manipulate the pyramid */
    private View mEditView;

    /**
     * An {@link OnClickListener} that may be attached to the {@link View}
     * returned by {@link Pyramid#getEditView(Context)} by the user
     */
    private OnClickListener mEditViewUserListener;

    /**
     * The grid that represents the shape of the pyramid: filled cells are
     * <code>true</code>, blank cells are <code>false</code>. The cell at the
     * top left of the grid is <code>mGrid[0][0]</code>.
     */
    private boolean[][] mGrid;

    /**
     * Creates a new pyramid with the given id. The id must be the same as the
     * id of the corresponding {@link Study}.
     * 
     * @param id The id of this pyramid
     */
    public Pyramid(int id) {
        mId = id;

        mGrid = new boolean[GRID_WIDTH][GRID_HEIGHT];

        mEditView = null;
        mEditViewUserListener = null;
    }

    /**
     * The id must be the same as the id of the corresponding {@link Study}.
     * 
     * @param id The id of this pyramid
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return The id of this pyramid
     */
    public int getId() {
        return mId;
    }

    /**
     * @param pole The left pole of this pyramid as a string
     */
    public void setPoleLeft(String pole) {
        mPoleLeft = pole;
    }

    /**
     * @return The left pole of this pyramid as a string
     */
    public String getPoleLeft() {
        return mPoleLeft;
    }

    /**
     * @param pole The right pole of this pyramid as a string
     */
    public void setPoleRight(String pole) {
        mPoleRight = pole;
    }

    /**
     * @return The right pole of this pyramid as a string
     */
    public String getPoleRight() {
        return mPoleRight;
    }

    /**
     * Creates a pyramid from a given string that represents the pyramid conform
     * to pq method. An existing grid may be overridden.
     * 
     * @param pqString The pq-string representation of the pyramid
     */
    public void fromPQString(String pqString) {
        mGrid = new boolean[GRID_WIDTH][GRID_HEIGHT];

        String[] pqs = pqString.split(" ");

        for (int i = 0; i < pqs.length; i++) {
            int sum = Integer.parseInt(pqs[i]);

            for (int j = GRID_HEIGHT - 1; sum > 0; sum--, j--) {
                mGrid[i][j] = true;
            }
        }
    }

    /**
     * <P>
     * Translates this pyramid grid into a string representation that is conform
     * to pq method. Note that this representation might not be unique and also
     * a pyramid with an invalid configuration (as determined by
     * {@link Pyramid#isValid()}) can produce a valid pq-string:
     * <LI>As a number in the pq-String only provides information about the
     * total amount of cells in a certain column, there is no information about
     * the horizontal alignment of the column</LI>
     * <LI>Also blanks between different cells of a column are ignored</LI>
     * </P>
     * <P>
     * However a pyramid created from a pq-string via
     * {@link Pyramid#fromPQString(String)} should return the same string as it
     * was created from.
     * </P>
     * 
     * @return The pq-string representation of the pyramid
     */
    public String toPQString() {
        if (getSize() < 1) {
            return "";
        }

        StringBuilder pq = new StringBuilder();

        for (int i = 0; i < GRID_WIDTH; i++) {
            int sum = 0;
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (mGrid[i][j]) {
                    sum++;
                }
            }

            pq.append(sum);
            pq.append(' ');
        }

        while (pq.charAt(0) == ' ' || pq.charAt(0) == '0') {
            pq.deleteCharAt(0);
        }

        int last;

        while (pq.charAt(last = pq.length() - 1) == ' '
                || pq.charAt(last) == '0') {
            pq.deleteCharAt(last);
        }

        return pq.toString();
    }

    /**
     * Creates a pyramid from the given unique string representation. See
     * {@link Pyramid#toUniqueString()} for information about the string format.
     * 
     * @param string The string representation
     */
    public void fromUniqueString(String uString) {
        mGrid = new boolean[GRID_WIDTH][GRID_HEIGHT];

        String[] coords = uString.split(":");

        for (String coord : coords) {
            String[] s = coord.split("-");

            if (s.length < 2) {
                break;
            }

            int x = Integer.parseInt(s[0]);
            int y = Integer.parseInt(s[1]);

            mGrid[x][y] = true;
        }
    }

    /**
     * Returns a unique string representation of this pyramid shape. The string
     * contains the coordinates of the activiated cells of the grid, seperated
     * by '-' and ':'. E.g. a pyramid with activated cells at 4,5 and 5,5 will
     * have the following representation: "4-5:5-5:"
     * 
     * @return The string representation
     */
    public String toUniqueString() {
        StringBuilder uString = new StringBuilder();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (mGrid[i][j]) {
                    uString.append(i).append('-').append(j).append(':');
                }
            }
        }

        return uString.toString();
    }

    /**
     * Creates a pyramid from the given two dimensional boolean array. The array
     * must be a grid of the size [{@link Pyramid#GRID_WIDTH}][
     * {@link Pyramid#GRID_HEIGHT}] or an {@link IllegalArgumentException} is
     * thrown. The pyramid shape is formed by cells of the grid set to
     * <code>true</code>.
     * 
     * @param grid The array
     * @throws IllegalArgumentException when the array is not of the form
     *             described above.
     */
    public void fromBooleanArray(boolean[][] grid) {
        if (grid.length != GRID_WIDTH) {
            throw new IllegalArgumentException("Array hasn't got the right width!");
        }

        for (boolean[] bs : grid) {
            if (bs.length != GRID_HEIGHT) {
                throw new IllegalArgumentException("Array hasn't got the right height!");
            }
        }

        mGrid = grid;
    }

    /**
     * Returns a unique representation of this pyramid shape as a two
     * dimensional boolean array. The returned array has got the size [
     * {@link Pyramid#GRID_WIDTH}][{@link Pyramid#GRID_HEIGHT}].
     * 
     * @return The array
     */
    public boolean[][] toBooleanArray() {
        return mGrid;
    }

    /**
     * <P>
     * Checks whether the pyramid is valid or not. The result is only affected
     * by the shape of the pyramid, not regarding aspects like item
     * configuration. A pyramid is valid when it meets the following conditions:
     * <LI>it's vertically symmetric</LI>
     * <LI>there are no blank cells between filled cells</LI>
     * <LI>each row (except the lowermost) must not provide more filled cells
     * than the underlying row</LI>
     * </P>
     * 
     * @return <code>true</code> when this pyramid is valid, <code>false</code>
     *         else
     */
    public boolean isValid() {
        if (getSize() < 1) {
            return false;
        }
        
        if (getWidth() % 2 == 0) {
            return false;
        }

        // Calculate how many cells are filled per row and which column the
        // first filled cell is. When there are blanks between filled cells,
        // return false.
        int[] numCells = new int[GRID_HEIGHT]; // array index is row
        int[] firstCell = new int[GRID_HEIGHT]; // array index is row

        int bottomRow = 0; // the lowermost row

        for (int j = 0; j < GRID_HEIGHT; j++) {
            // j is index of the current row
            boolean filled = false; // has there been a filled cell yet?
            boolean blank = false; // blank after filled cell yet?

            for (int i = 0; i < GRID_WIDTH; i++) {
                if (mGrid[i][j]) {
                    // the cell i,j is filled

                    // when there has already been a blank field after a filled
                    // cell, it's invalid
                    if (blank) {
                        return false;
                    }

                    // when there has not been a filled cell yet, this is the
                    // position of the first one
                    if (!filled) {
                        firstCell[j] = i;
                    }

                    // increment the number of cells in this row because we just
                    // found a filled cell
                    numCells[j]++;
                    filled = true;
                    bottomRow = j;
                }
                else {
                    // the cell i,j is not filled

                    // when there already was a filled cell, this is a blank
                    // after a filled cell
                    blank = filled;
                }
            }
        }

        // When one row has more filled cells than the underlying, return false.
        // If the difference between rows is an odd number, the shape can't be
        // symmetric => return false.
        // there must not be empty rows between the lowermost and the uppermost,
        // return false else
        // Go through rows from bottom to top:
        for (int j = bottomRow - 1; j >= 0; j--) {
            // j is the index of the current row

            if (numCells[j] > numCells[j + 1]) {
                return false;
            }

            if (numCells[j] > 0) {
                int diffNum = numCells[j + 1] - numCells[j];
                // odd number check
                if (diffNum % 2 == 1) {
                    return false;
                }
                // Check the vertical alignment of the rows:
                int diffFirst = firstCell[j] - firstCell[j + 1];
                if (diffFirst != (diffNum / 2)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Says whether the edit view that can be obtained from the
     * {@link Pyramid#getEditView(Context)} method is already cached in the
     * instance of this class. When the view <B>is not cached</B>, the call of
     * {@link Pyramid#getEditView(Context)} might take a bit longer, because the
     * view needs to be build.
     * 
     * @return <code>true</code> if the view is cached, false else
     */
    public boolean isEditViewCached() {
        return (mEditView != null);
    }

    /**
     * <P>
     * Provides a {@link View} object to manipulate this pyramid. As soon as the
     * view is embedded in the user interface, the user is able to shape the
     * pyramid by hitting certain cells in a grid. The user actions will be
     * performed on this {@link Pyramid} object immediately, so the validity of
     * the pyramid can be checked using {@link Pyramid#isValid()} at any time.
     * The {@link View} object will be created on the first call of this method
     * ({@link Pyramid#isEditViewCached()} can say if this has alreqady
     * happened).
     * </P>
     * 
     * @param context The context in which the view should be displayed
     * @return The {@link View} object containing the grid
     */
    public View getEditView(Context context) {
        if (mEditView != null) {
            return mEditView;
        }

        GridLayout gl = new GridLayout(context);

        gl.setColumnCount(GRID_WIDTH);
        gl.setRowCount(GRID_HEIGHT);

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                GridButton g = (GridButton) v;
                g.invert();

                // activate whole grid clicker
                if (mEditViewUserListener != null) {
                    mEditViewUserListener.onClick(mEditView);
                }
            }
        };

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                GridButton g = new GridButton(context, j, i);
                g.setOnClickListener(clicker);
                gl.addView(g);
            }
        }

        gl.setUseDefaultMargins(true);

        HorizontalScrollView hsv = new HorizontalScrollView(context);
        hsv.addView(gl);
        hsv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        ScrollView sv = new ScrollView(context) {
            @Override
            public void setOnClickListener(OnClickListener l) {
                mEditViewUserListener = l;
            }
        };
        sv.addView(hsv);
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mEditView = sv;

        return mEditView;
    }

    /**
     * Helper class for {@link Pyramid#getEditView(Context)}.
     */
    private class GridButton extends Button {
        private int mX, mY;

        public GridButton(Context context, int x, int y) {
            super(context);
            mX = x;
            mY = y;

            setWidth(30);
            setHeight(30);
        }

        public void invert() {
            mGrid[mX][mY] = !mGrid[mX][mY];
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            setBackgroundColor(mGrid[mX][mY] ? COLOR_GRID_CHECKED : COLOR_GRID_UNCHECKED);
            super.onDraw(canvas);
        }
    }

    /**
     * Returns the height of the pyramid at a specific column.
     * 
     * @param column The column
     * @return The height at this column
     */
    public int getHeightAt(int column) {
        int x = -1;

        int sum = 0;
        
        for (int i = 0; i < GRID_WIDTH; i++){
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (mGrid[i][j]) {
                    x = i + column;
                    break;
                }
            }
            if (x != -1)
                break;
        }
        
            
        for (int j = 0; j < GRID_HEIGHT; j++) {
            if (mGrid[x][j]) {
                sum++;
            }
        }

        return sum;
    }

    /**
     * Returns the width of this pyramid. The width is the number of cells in
     * the lowermost row of the pyramid. Note: Only the widest row will be
     * considered to calculate the width. The result might not be significant
     * when the pyramid is invalid!
     * 
     * @return The width of this pyramid
     * @see {@link Pyramid#isValid()}
     */
    public int getWidth() {
        if (getSize() == 0) {
            return 0;
        }

        int sums[] = new int[GRID_HEIGHT];

        for (int j = 0; j < GRID_HEIGHT; j++) {
            for (int i = 0; i < GRID_WIDTH; i++) {
                if (mGrid[i][j]) {
                    sums[j]++;
                }
            }
        }

        int width = 0;

        for (int sum : sums) {
            if (sum > width) {
                width = sum;
            }
        }

        return width;
    }

    /**
     * @return The size (total number of cells) of this pyramid
     */
    public int getSize() {
        int size = 0;

        for (boolean[] bs : mGrid) {
            for (boolean b : bs) {
                if (b) {
                    size++;
                }
            }
        }

        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Pyramid)) {
            return false;
        }

        Pyramid other = (Pyramid) o;

        if (mId != other.mId) {
            return false;
        }

        if (!Util.nullSafeEquals(mPoleLeft, other.mPoleLeft)) {
            return false;
        }

        if (!Util.nullSafeEquals(mPoleRight, other.mPoleRight)) {
            return false;
        }

        if (!Util.nullSafeEquals(toUniqueString(), other.toUniqueString())) {
            return false;
        }

        return true;
    }

}
