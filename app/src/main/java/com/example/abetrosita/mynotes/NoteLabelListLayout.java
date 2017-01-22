package com.example.abetrosita.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbetRosita on 1/21/2017.
 */

public class NoteLabelListLayout extends FlowLayout {
    private Context mContext;
    private List<String> mLabels;
    private FlowLayout mLabelLayout;
    private List<EditText> mTextBoxes = new ArrayList<>();
    private LayoutInflater mInflater;

    public NoteLabelListLayout(Context context, List<String> labels) {
        super(context);
        //if(labels == null) return;
        mContext = context;
        mLabels = labels;
        mInflater = LayoutInflater.from(mContext);
        //ViewGroup.LayoutParams(10, 10);
        removeAllViews();
        loadListItems();

    }

    public NoteLabelListLayout(Context context, List<String> labels, boolean labelClickable) {
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
        ImageView imageLabel = new ImageView(mContext);
        imageLabel.setImageResource(R.drawable.ic_note_label);
        addView(imageLabel);

        //Log.d("FLOW_LAYOUT", "LABEL COUNT" + String.valueOf(mLabels.size()));
        if(mLabels == null){
            TextView addLabel = new TextView(mContext);
            addLabel.setText("Add a note label...");
            addView(addLabel);
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
