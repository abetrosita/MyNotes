package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by AbetRosita on 1/20/2017.
 */

public class LabelRecyclerView extends RecyclerView{
    List<String> mLabels;
    RecyclerView mView;
    LabelAdapter mLabelAdapter;

    public LabelRecyclerView(List<String> labels, RecyclerView view, Context context) {
        super(context);
        mView = view;
        mLabels = labels;
        mView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mView.setLayoutManager(layoutManager);
        mLabelAdapter = new LabelAdapter(mLabels);
        mView.setAdapter(mLabelAdapter);

     }

    public LabelRecyclerView(Context context) {
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
