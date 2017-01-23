package com.example.abetrosita.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LabelListLayout extends FlowLayout {
    private Context mContext;
    private List<String> mLabels;
    private LayoutInflater mInflater;

    public LabelListLayout(Context context, List<String> labels) {
        super(context);

        mContext = context;
        mLabels = labels;
        mInflater = LayoutInflater.from(mContext);

        removeAllViews();
        loadListItems();

    }

    public LabelListLayout(Context context, List<String> labels, boolean labelClickable) {
        this(context, labels);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "ADD/EDIT LABEL HERE", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void loadListItems(){
        View mContainer;
        ImageView imageLabelIcon = new ImageView(mContext);
        imageLabelIcon.setImageResource(R.drawable.ic_note_label);
        addView(imageLabelIcon);

        if(mLabels == null){
            TextView addLabelTextView = new TextView(mContext);
            addLabelTextView.setText(R.string.add_label_text);
            addView(addLabelTextView);
            return;
        }
        for(String label : mLabels) {
            mContainer = mInflater.inflate(R.layout.note_label_item, null, false);
            final TextView mTextView = (TextView) mContainer.findViewById(R.id.tv_note_label_item);
            mTextView.setText(label);
            addView(mContainer);
        }
    }
    
    
}
