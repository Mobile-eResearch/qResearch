
package de.eresearch.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.eresearch.app.db.exception.DatabaseOnCreateException;
import de.eresearch.app.db.tables.AnswersTable;
import de.eresearch.app.db.tables.AudioRecordsTable;
import de.eresearch.app.db.tables.CQPossibleAnswersTable;
import de.eresearch.app.db.tables.ClosedQuestionsAnswersTable;
import de.eresearch.app.db.tables.ClosedQuestionsTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.FilledPyramidCellsTable;
import de.eresearch.app.db.tables.NotesTable;
import de.eresearch.app.db.tables.OpenQuestionAnswersTable;
import de.eresearch.app.db.tables.OperatorsTable;
import de.eresearch.app.db.tables.PicturesTable;
import de.eresearch.app.db.tables.PyramidTable;
import de.eresearch.app.db.tables.QsortItems;
import de.eresearch.app.db.tables.QsortsTable;
import de.eresearch.app.db.tables.QuestionLoggingTable;
import de.eresearch.app.db.tables.ScaleQuestionsTable;
/*import de.eresearch.app.db.tables.QuestionTypesTable;*/
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.db.tables.RecordsTable;
import de.eresearch.app.db.tables.ScaleQuestionAnswersTable;
import de.eresearch.app.db.tables.ScaleValuesTable;
import de.eresearch.app.db.tables.ScalesTable;
import de.eresearch.app.db.tables.SortLoggingTable;
import de.eresearch.app.db.tables.StudiesTable;

/**
 * @author Jurij Wöhlke
 */
public final class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static DatabaseOpenHelper dbOHInstance;

    public final static String DATABASE_NAME = "QApp.db";
    public final static int DATABASE_VERSION = 1;

    public static final String[] ALL_TABLES_BY_NAME = {
            AnswersTable.TABLE_ANSWERS, AudioRecordsTable.TABLE_AUDIORECORDS,
            ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS,
            ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS,
            CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, EnumTable.TABLE_ENUM,
            FilledPyramidCellsTable.TABLE_FILLED_PYRAMIDE, NotesTable.TABLE_NOTES,
            OpenQuestionAnswersTable.TABLE_OPEN_QUESTION_ANSWERS_TABLE,
            OperatorsTable.TABLE_OPERATORS, PicturesTable.TABLE_PICTURES,
            PyramidTable.TABLE_PYRAMID, QsortItems.TABLE_QSORT_ITEMS, QsortsTable.TABLE_QSORTS,
            QuestionLoggingTable.TABLE_QUESTION_LOGGING, QuestionsTable.TABLE_QUESTIONS,
            ScaleQuestionsTable.TABLE_SCALE_QUESTIONS,
            /*QuestionTypesTable.TABLE_QUESTION_TYPES,*/ RecordsTable.TABLE_RECORDS,
            ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER, ScalesTable.TABLE_SCALES,
            ScaleValuesTable.TABLE_SCALES_VALUES, SortLoggingTable.TABLE_SORT_LOGGING,
            StudiesTable.TABLE_STUDIES
    };

    public static final String[] ALL_TABLES_CREATE = {
            AnswersTable.TABLE_CREATE, AudioRecordsTable.TABLE_CREATE,
            ClosedQuestionsAnswersTable.TABLE_CREATE,
            ClosedQuestionsTable.TABLE_CREATE,
            CQPossibleAnswersTable.TABLE_CREATE, EnumTable.TABLE_CREATE,
            FilledPyramidCellsTable.TABLE_CREATE, NotesTable.TABLE_CREATE,
            OpenQuestionAnswersTable.TABLE_CREATE,
            OperatorsTable.TABLE_CREATE, PicturesTable.TABLE_CREATE,
            PyramidTable.TABLE_CREATE, QsortItems.TABLE_CREATE, QsortsTable.TABLE_CREATE,
            ScaleQuestionsTable.TABLE_CREATE,
            QuestionLoggingTable.TABLE_CREATE, QuestionsTable.TABLE_CREATE,
            /*QuestionTypesTable.TABLE_CREATE,*/ RecordsTable.TABLE_CREATE,
            ScaleQuestionAnswersTable.TABLE_CREATE, ScalesTable.TABLE_CREATE,
            ScaleValuesTable.TABLE_CREATE, SortLoggingTable.TABLE_CREATE,
            StudiesTable.TABLE_CREATE
    };

    /**
     * private Constructor for Singleton
     * 
     * @param context
     */
    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * returns instance of DatabaseOpenHelper
     * 
     * @param context
     * @return DatabaseConnector
     */
    public synchronized static DatabaseOpenHelper getInstance(Context context) {
        if (dbOHInstance == null) {
            dbOHInstance = new DatabaseOpenHelper(context);
        }
        return dbOHInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String table : ALL_TABLES_CREATE) {
            db.execSQL(table);
        }
        for (String[] enumCreate : EnumTable.ALL_ENUMS) {
            ContentValues cValues=new ContentValues();
            cValues.put(EnumTable.COLUMN_ID,enumCreate[0]);
            cValues.put(EnumTable.COLUMN_VALUE,enumCreate[1]);
            long res=db.insert(EnumTable.TABLE_ENUM, null, cValues);
            if(res<0){
                Log.e("ERROR", "Enum "+enumCreate[1]+" not inserted", new DatabaseOnCreateException());
            }
            
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseOpenHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion);
        for (String table : ALL_TABLES_BY_NAME) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);

    }

    /**
     * sets foreign keys=on when called
     */
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable ForeignKey Constraints
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

}
