
package de.eresearch.app.logic.model;

import android.content.Context;

import de.eresearch.app.R;

public enum Phase {
    QUESTIONS_PRE, Q_SORT, QUESTIONS_POST, INTERVIEW;

    public String toString(Context context) {
        String phase="";
        switch (this) {
            case QUESTIONS_PRE:
                phase = context.getString(R.string.phase_title_presort);
                break;
            case Q_SORT:
                phase =context.getString(R.string.phase_title_qsort);
                break;
            case QUESTIONS_POST:
                phase =context.getString(R.string.phase_title_postsort);
                break;
            case INTERVIEW:
                phase =context.getString(R.string.phase_title_interview);
                break;
            default:
                break;
        }
        return phase;
    }
}
