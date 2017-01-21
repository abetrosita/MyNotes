package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by AbetRosita on 1/20/2017.
 */

public class NoteLabelRecyclerView extends RecyclerView{
    List<String> mLabels;
    RecyclerView mView;
    NoteLabelAdapter mLabelAdapter;

    public NoteLabelRecyclerView(List<String> labels, RecyclerView view, Context context) {
        super(context);
        mView = view;
        mLabels = labels;
        mView.setHasFixedSize(false);
        mLabelAdapter = new NoteLabelAdapter(mLabels);
        mView.setAdapter(mLabelAdapter);

     }

    public NoteLabelRecyclerView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
    public void loadLabels(List<String> labels){
        if(labels ==null) {
            mView.setVisibility(GONE);
        }else {
            mView.setVisibility(VISIBLE);
        }
        mLabelAdapter.loadLabelData(labels);
    }

}
