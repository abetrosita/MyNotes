package com.example.abetrosita.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by AbetRosita on 1/20/2017.
 */

public class LabelRecyclerView extends RecyclerView{
    Cursor mLabels;
    RecyclerView mView;
    LabelAdapter mLabelAdapter;

    public LabelRecyclerView(Cursor labels, RecyclerView view, LabelAdapter adapter, Context context) {
        super(context);
        mView = view;
        mLabels = labels;
        mView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mView.setLayoutManager(layoutManager);
        mView.setAdapter(mLabelAdapter);

     }

    public LabelRecyclerView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }



}
