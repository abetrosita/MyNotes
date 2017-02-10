package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class LabelAdapter extends RecyclerView.Adapter<LabelViewHolder>{

    private static final String LOG_TAG = LabelAdapter.class.getSimpleName();
    final private LabelViewHolder.LabelOnClickHandler mClickHandler;
    private List<Label> mLabels;
    private LabelViewHolder mLabelViewHolder;
    private int mCaller;
    private int mIconVisibility;
    private int mEditVisibility;
    private int mHandleVisibility;
    private int displayChild;

    public LabelAdapter(List<Label> labels, int caller, LabelViewHolder.LabelOnClickHandler clickHandler) {
        mLabels = labels;
        mClickHandler = clickHandler;
        mCaller = caller;
        mIconVisibility = View.VISIBLE;
        mEditVisibility = View.GONE;
        mHandleVisibility = View.INVISIBLE;
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(R.layout.dialog_label_item, parent,
                shouldAttachToParentImmediately);

        return new LabelViewHolder(view, mCaller, mClickHandler);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        if (mLabels ==null) return;
        mLabelViewHolder = holder;
        int labelId = mLabels.get(position).getId();
        holder.label.setText(mLabels.get(position).getName());
        holder.editLabel.setText(mLabels.get(position).getName());
        holder.chkLabel.setChecked(mLabels.get(position).isChecked());
        holder.icNoteLabel.setVisibility(mIconVisibility);
        holder.ivDialogEdit.setVisibility(mEditVisibility);
        holder.icHandle.setVisibility(mHandleVisibility);
    }

    @Override
    public int getItemCount() {
        return mLabels == null ? 0 : mLabels.size();
    }


    public void loadLabelData(List<Label> labels){
        mLabels = labels;
        if (labels != null) {
            this.notifyDataSetChanged();
        }
    }

    public void setCaller(int caller){
        switch (caller){
            case AppConstant.NOTE_CALLER_EDIT:
                mIconVisibility = View.GONE;
                mEditVisibility = View.VISIBLE;
                mHandleVisibility = View.VISIBLE;
                break;
            case AppConstant.NOTE_CALLER_BACK:
                mIconVisibility = View.VISIBLE;
                mEditVisibility = View.GONE;
                mHandleVisibility = View.INVISIBLE;

        }
    }
}
