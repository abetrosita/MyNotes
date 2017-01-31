package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private static final String LOG_TAG = LabelAdapter.class.getSimpleName();
    final private LabelOnClickHandler mClickHandler;
    private List<Label> mLabels;

    public LabelAdapter(List<Label> labels, LabelOnClickHandler clickHandler) {
        mLabels = labels;
        mClickHandler = clickHandler;
    }

    public interface LabelOnClickHandler {
        void onClick(View view, View deleteView);
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(R.layout.dialog_label_item, parent,
                shouldAttachToParentImmediately);

        return new LabelAdapter.LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        if (mLabels ==null) return;
        int labelId = mLabels.get(position).getId();
        holder.label.setText(mLabels.get(position).getName());
        holder.label.setTag(labelId);
        holder.ivDeleteLabel.setTag(position);
        //holder.chkLabel.setTag("chk_" + String.valueOf(labelId));
        holder.chkLabel.setChecked(mLabels.get(position).isChecked());
        holder.chkLabel.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mLabels == null ? 0 : mLabels.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView label;
        ImageView ivDeleteLabel;
        CheckBox chkLabel;
        public LabelViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.tv_note_label);
            chkLabel = (CheckBox) itemView.findViewById(R.id.chk_dialog_label);
            ivDeleteLabel = (ImageView) itemView.findViewById(R.id.iv_delete_label);
            ivDeleteLabel.setVisibility(View.GONE);
            label.setOnClickListener(this);
            ivDeleteLabel.setOnClickListener(this);
            chkLabel.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            mClickHandler.onClick(v, ivDeleteLabel);
        }
    }

    public void loadLabelData(List<Label> labels){
        mLabels = labels;
        if (labels != null) {
            this.notifyDataSetChanged();
        }
    }
}
