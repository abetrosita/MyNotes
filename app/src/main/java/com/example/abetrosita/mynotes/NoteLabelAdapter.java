package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by AbetRosita on 1/20/2017.
 */

public class NoteLabelAdapter extends RecyclerView.Adapter<NoteLabelAdapter.LabelViewHolder> {

    private static final String LOG_TAG = NoteLabelAdapter.class.getSimpleName();
    private List<String> mLabels;

    public NoteLabelAdapter(List<String> labels) {
        mLabels = labels;
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int labelItemLayout = R.layout.note_label_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(labelItemLayout, parent,
                shouldAttachToParentImmediately);
        //view.setClickable(true);
        NoteLabelAdapter.LabelViewHolder viewHolder = new NoteLabelAdapter.LabelViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        if (mLabels ==null) return;
        holder.label.setText(mLabels.get(position));
    }

    @Override
    public int getItemCount() {
        return mLabels == null ? 0 : mLabels.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        public LabelViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.tv_note_label_item);
        }
    }

    public void loadLabelData(List<String> labels){
        mLabels = labels;
        if (labels != null) {
            this.notifyDataSetChanged();
        }
    }
}
