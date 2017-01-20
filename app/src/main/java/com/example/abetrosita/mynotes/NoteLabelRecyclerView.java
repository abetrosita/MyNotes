package com.example.abetrosita.mynotes;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by AbetRosita on 1/20/2017.
 */

public class NoteLabelRecyclerView{
    List<String> mLabels;
    Activity mView;
    NoteLabelAdapter mLabelAdapter;
    RecyclerView mRecyclerView;

    public NoteLabelRecyclerView(List<String> labels, Activity view) {
        mLabels = labels;
        mView = view;
        //Context context = getApplicationContext;
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_labels);
        //mRecyclerViewLabel.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mLabelAdapter = new NoteLabelAdapter(mLabels);
        mRecyclerView.setAdapter(mLabelAdapter);
    }


    public void loadLabels(List<String> labels){
        mLabelAdapter.loadLabelData(labels);
    }

}
