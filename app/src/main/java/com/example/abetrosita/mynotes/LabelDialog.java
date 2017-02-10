package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.abetrosita.mynotes.AppConstant.ON_EDIT_MODE;
import static com.example.abetrosita.mynotes.MainActivity.mContentResolver;


public class LabelDialog implements LabelViewHolder.LabelOnClickHandler{

    private List<Label> mLabels;
    private LabelAdapter mLabelAdapter;
    private Context mContext;
    private View lastView;
    private List<Label> updatedLabel;
    private List<Label> deletedLabel;
    private List<Label> insertedLabel;
    private int lastPosition = -1;
    private LabelViewHolder lastHolder;
    private LinearLayout llEditLabel;
    private LinearLayout llNewLabel;
    private int mCaller;

    private RecyclerView recyclerView;

    View mView;

    public LabelDialog(Context context, List<Label> labels, int caller){
        mContext = context;
        mLabels = labels;
        mCaller = caller;
        mLabelAdapter = new LabelAdapter(mLabels, caller, this);
        updatedLabel = new ArrayList<>();
        deletedLabel = new ArrayList<>();
        insertedLabel = new ArrayList<>();
        lastHolder = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView= inflater.inflate(R.layout.dialog_label, null);

        recyclerView = (RecyclerView) mView.findViewById(R.id.rv_note_dialog_label_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLabelAdapter);

        showDialog();
    }

    public void showDialog(){
        AlertDialog.Builder   dialog = new AlertDialog.Builder(mContext);
        dialog.setView(mView);
        dialog.setTitle("Manage Note Labels");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){
                if(insertedLabel != null){
                    for(Label label : insertedLabel){
                        ContentValues values= label.getContentValues();
                        Uri uri = mContentResolver.insert(LabelContract.URI_TABLE, values);
                        mLabels.get(mLabels.indexOf(label)).setId(Integer.parseInt(uri.getLastPathSegment()));
                    }
                }

                if(deletedLabel != null){
                    for(Label label : deletedLabel){
                        Uri uri = LabelContract.Labels.buildLabelUri(String.valueOf(label.getId()));
                        mContentResolver.delete(uri, null, null);
                        Log.d("LABEL_DELETED", uri.toString());
                    }
                }

                NoteDetailActivity.loadFlowLabel(getCheckedLabelIds());

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int id){
                dia.cancel();
            }
        });
        dialog.show();

        setUpAddLabelAction();
    }

    private void setUpAddLabelAction(){
        final EditText etAddLabel = (EditText) mView.findViewById(R.id.et_add_label);
        final TextView tvEditLabel = (TextView) mView.findViewById(R.id.tv_add_label);
        final ImageView ivAccept = (ImageView) mView.findViewById(R.id.iv_accept_label);
        final ImageView ivEdit = (ImageView) mView.findViewById(R.id.iv_dialog_edit);
        final ImageView ivBack = (ImageView) mView.findViewById(R.id.iv_back);
        final ImageView ivAddLabel = (ImageView) mView.findViewById(R.id.iv_add_label);
        llNewLabel = (LinearLayout) mView.findViewById(R.id.ll_add_label);
        llEditLabel = (LinearLayout) mView.findViewById(R.id.ll_edit_label);

        if(mCaller == AppConstant.NOTE_CALLER_MAIN){
            llEditLabel.setVisibility(VISIBLE);
            llNewLabel.setVisibility(GONE);
            ivAddLabel.setVisibility(GONE);
            ivBack.setVisibility(VISIBLE);
        }else{
            llEditLabel.setVisibility(GONE);
            llNewLabel.setVisibility(VISIBLE);
            ivAddLabel.setVisibility(VISIBLE);
            ivBack.setVisibility(GONE);
        }

        etAddLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    ivAccept.setVisibility(VISIBLE);
                 }else {
                    ivAccept.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etAddLabel.clearFocus();

        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelName = etAddLabel.getText().toString();
                if(isLabelNameExists(labelName)){
                    Toast.makeText(mContext, "Label " + labelName + " already exists...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Label label = new Label(labelName);
                label.setNew(true);
                mLabels.add(0, label);
                mLabelAdapter.notifyDataSetChanged();
                insertedLabel.add(label);
                etAddLabel.setText(null);
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llEditLabel.setVisibility(GONE);
                llNewLabel.setVisibility(VISIBLE);
                ON_EDIT_MODE = true;
                mLabelAdapter.setCaller(AppConstant.NOTE_CALLER_EDIT);
                mLabelAdapter.notifyDataSetChanged();
            }
        });

        tvEditLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llEditLabel.setVisibility(GONE);
                llNewLabel.setVisibility(VISIBLE);
                ON_EDIT_MODE = true;
                mLabelAdapter.setCaller(AppConstant.NOTE_CALLER_EDIT);
                mLabelAdapter.notifyDataSetChanged();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llEditLabel.setVisibility(VISIBLE);
                llNewLabel.setVisibility(View.GONE);
                ON_EDIT_MODE = false;
                if(lastHolder != null) {
                    lastHolder.resetMainLabelViews();
                }
                mLabelAdapter.setCaller(AppConstant.NOTE_CALLER_BACK);
                mLabelAdapter.notifyDataSetChanged();
            }
        });

    }

    private boolean isLabelNameExists(String labelName){
        boolean exists = false;
        for(Label label : mLabels){
            if(label.getName().equals(labelName)){
                exists = true;
            }
        }
        return exists;
    }

    public List<String> getCheckedLabelIds(){
        List<String> checkedLabels = new ArrayList<>();
        for(Label label : mLabels){
            if(label.isChecked()){
                checkedLabels.add(String.valueOf(label.getId()));
            }
        }
        return checkedLabels;
    }

    @Override
    public void onClick(LabelViewHolder holder, String viewTag) {
        int position = holder.getAdapterPosition();
        if(!ON_EDIT_MODE && viewTag.equals("labelName")){
            Log.d("LOG_HOLDER", "Ready to filter by tags");
            return;
        }

        if(position != lastPosition && lastHolder != null){
            lastHolder.resetMainLabelViews();
        }

        switch (viewTag){
            case "labelDelete":
                //TODO: Add prompt to confirm if to proceed deleting tag.
                if(!mLabels.get(position).isNew()){
                    deletedLabel.add(mLabels.get(position));
                }else{
                    for(Label label : insertedLabel){
                        if(label.getName().equals(mLabels.get(position).getName())){
                            insertedLabel.remove(label);
                        }
                    }
                }
                mLabels.remove(position);
                mLabelAdapter.notifyItemRemoved(position);
                mLabelAdapter.notifyItemRangeChanged(position, mLabels.size());
                break;
            case "checkBox":
                CheckBox chk =  holder.chkLabel;
                mLabels.get(position).setChecked(chk.isChecked());
        }

        lastPosition = holder.getAdapterPosition();
        lastHolder = holder;
    }
}
