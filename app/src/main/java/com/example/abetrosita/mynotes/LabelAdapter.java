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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private static final String LOG_TAG = LabelAdapter.class.getSimpleName();
    final private LabelOnClickHandler mClickHandler;
    private List<Label> mLabels;
    private int mCaller;

    public LabelAdapter(List<Label> labels, LabelOnClickHandler clickHandler, int caller) {
        mLabels = labels;
        mClickHandler = clickHandler;
        mCaller = caller;
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
        holder.ivDialogDelete.setTag(position);
        holder.chkLabel.setChecked(mLabels.get(position).isChecked());
        holder.chkLabel.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mLabels == null ? 0 : mLabels.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView label;
        ImageView ivDialogDelete;
        ImageView ivDialogEdit;
        ImageView ivDialogAccept;
        CheckBox chkLabel;
        public LabelViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.tv_note_label);
            chkLabel = (CheckBox) itemView.findViewById(R.id.chk_dialog_label);
            ivDialogDelete = (ImageView) itemView.findViewById(R.id.iv_dialog_delete);
            ivDialogAccept = (ImageView) itemView.findViewById(R.id.iv_dialog_accept);
            ivDialogEdit = (ImageView) itemView.findViewById(R.id.iv_dialog_edit);
            ivDialogDelete.setVisibility(GONE);
            ivDialogAccept.setVisibility(GONE);

            if(mCaller == AppConstant.NOTE_CALLER_MAIN){
                chkLabel.setVisibility(GONE);
                ivDialogEdit.setVisibility(VISIBLE);
            }else {
                chkLabel.setVisibility(VISIBLE);
                ivDialogEdit.setVisibility(GONE);
            }

            label.setOnClickListener(this);
            ivDialogDelete.setOnClickListener(this);
            chkLabel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(v, ivDialogDelete);
        }
    }

    public void loadLabelData(List<Label> labels){
        mLabels = labels;
        if (labels != null) {
            this.notifyDataSetChanged();
        }
    }
}
