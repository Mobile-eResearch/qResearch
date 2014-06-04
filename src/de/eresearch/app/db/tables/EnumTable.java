package de.eresearch.app.db.tables;

import de.eresearch.app.logic.model.Phase;

public class EnumTable {
    
    public static final String TABLE_ENUM ="ENUM";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_VALUE ="value";

    public static final int ENUM_ITEM_TYPE_PICTURE=1;
    //QPHASE
    public static final int ENUM_QPHASE_QUESTIONS_PRE=10;
    public static final int ENUM_QPHASE_QSORT=11;
    public static final int ENUM_QPHASE_QUESTIONS_POST=12;
    public static final int ENUM_QPHASE_INTERVIEW=13;
    //RECORD ENUMS
    public static final int ENUM_RECORD_DATATYPE_AUDIO=20;
    public static final int ENUM_RECORD_DATATYPE_NOTE=21;
    public static final int ENUM_RECORD_TYPE_QUESTION=22;
    public static final int ENUM_RECORD_TYPE_INTERVIEW=23;
    public static final int ENUM_RECORD_TYPE_QSORT=24;
    public static final int ENUM_RECORD_TYPE_AFTER_QSORT=25;
    public static final int ENUM_RECORD_TYPE_AFTER_QSORT_COMPLETION=26;
    public static final int ENUM_RECORD_TYPE_OTHER=27;
    
    //QUESTION TYPE ENUMS
    public static final int ENUM_QUESTION_TYPE_OPEN=30;
    public static final int ENUM_QUESTION_TYPE_CLOSED=31;
    public static final int ENUM_QUESTION_TYPE_SCALE=32;    
    
    public static final String[] ALL_COLUMNS={COLUMN_ID, COLUMN_VALUE};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_ENUM + "(" + COLUMN_ID
            + " integer primary key unique, "
            + COLUMN_VALUE + " text not null );";
    
    public static final String[][] ALL_ENUMS={
        {Integer.toString(EnumTable.ENUM_ITEM_TYPE_PICTURE),"PICTURE"},
        {Integer.toString(EnumTable.ENUM_QPHASE_QUESTIONS_PRE),"PHASE_QUESTIONS_PRE"},
        {Integer.toString(EnumTable.ENUM_QPHASE_QSORT),"PHASE_QSORT"},
        {Integer.toString(EnumTable.ENUM_QPHASE_QUESTIONS_POST),"PHASE_QUESTIONS_POST"},
        {Integer.toString(EnumTable.ENUM_QPHASE_INTERVIEW),"PHASE_INTERVIEW"},
        {Integer.toString(EnumTable.ENUM_RECORD_DATATYPE_AUDIO),"RECORD_DATATYPE_AUDIO"},
        {Integer.toString(EnumTable.ENUM_RECORD_DATATYPE_NOTE),"RECORD_DATATYPE_NOTE"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_QUESTION),"RECORD_TYPE_QUESTION"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_INTERVIEW),"RECORD_TYPE_INTERVIEW"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_QSORT),"RECORD_TYPE_QSORT"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_AFTER_QSORT),"RECORD_TYPE_AFTER_QSORT"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_AFTER_QSORT_COMPLETION),"RECORD_TYPE_AFTER_QSORT_COMPLETION"},
        {Integer.toString(EnumTable.ENUM_RECORD_TYPE_OTHER),"RECORD_TYPE_OTHER"},
        {Integer.toString(EnumTable.ENUM_QUESTION_TYPE_OPEN),"QUESTION_TYPE_OPEN"},
        {Integer.toString(EnumTable.ENUM_QUESTION_TYPE_CLOSED),"QUESTION_TYPE_CLOSED"},
        {Integer.toString(EnumTable.ENUM_QUESTION_TYPE_SCALE),"QUESTION_TYPE_SCALE"},
    };
    
    public static int getQPhaseIntForModelEnum(Phase phase){
        int res=-1;
        if(phase==Phase.INTERVIEW){
           res=EnumTable.ENUM_QPHASE_INTERVIEW;
        }else if(phase==Phase.Q_SORT){
            res=EnumTable.ENUM_QPHASE_QSORT;
        }else if(phase==Phase.QUESTIONS_PRE){
            res=EnumTable.ENUM_QPHASE_QUESTIONS_PRE;
        }else if(phase==Phase.QUESTIONS_POST){
            res=EnumTable.ENUM_QPHASE_QUESTIONS_POST;
        }
        return res;
    }
    
    public static Phase getModelPhaseForQPhaseEnumId(int enumId){
        Phase res=null;
        if(enumId==EnumTable.ENUM_QPHASE_INTERVIEW){
            res=Phase.INTERVIEW;
        }else if(enumId==EnumTable.ENUM_QPHASE_QSORT){
            res=Phase.Q_SORT;
        }else if(enumId==EnumTable.ENUM_QPHASE_QUESTIONS_PRE){
            res=Phase.QUESTIONS_PRE;
        }else if(enumId==EnumTable.ENUM_QPHASE_QUESTIONS_POST){
            res=Phase.QUESTIONS_POST;
        }
        return res;
    }
}
