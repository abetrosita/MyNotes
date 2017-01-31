package com.example.abetrosita.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LabelListLayout extends FlowLayout {
    private Context mContext;
    public static List<String> mLabelIds;
    private List<Label> labelList;
    private LayoutInflater mInflater;

    public LabelListLayout(Context context, List<String> labels) {
        super(context);

        mContext = context;
        mLabelIds = labels;
        mInflater = LayoutInflater.from(mContext);
        labelList = MainActivity.mLabels;

        removeAllViews();
        loadLabelListItems();

    }

    public LabelListLayout(Context context, final List<String> labels, boolean labelClickable) {
        this(context, labels);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(labels != null){
                    for(Label label : labelList){
                        boolean inList = labels.contains(String.valueOf(label.getId()));
                        label.setChecked(inList);
                    }
                }

                new LabelDialog(mContext, labelList, AppConstant.NOTE_CALLER_DETAIL);
            }
        });
    }


    public void loadLabelListItems(){
        View mContainer;
        ImageView imageLabelIcon = new ImageView(mContext);
        imageLabelIcon.setImageResource(R.drawable.ic_note_label);
        addView(imageLabelIcon);

        if(mLabelIds == null){
            TextView addLabelTextView = new TextView(mContext);
            addLabelTextView.setText(R.string.add_label_text);
            addView(addLabelTextView);
            return;
        }

        for(Label label : labelList){
            if(mLabelIds.contains(String.valueOf(label.getId()))){
                mContainer = mInflater.inflate(R.layout.note_label_item, null, false);
                TextView mTextView = (TextView) mContainer.findViewById(R.id.tv_note_label_item);
                mTextView.setText(label.getName());
                addView(mContainer);
            }
        }
    }
}
