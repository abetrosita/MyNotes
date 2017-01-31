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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.example.abetrosita.mynotes.MainActivity.mContentResolver;


public class LabelDialog implements LabelAdapter.LabelOnClickHandler{

    private List<Label> mLabels;
    private LabelAdapter mLabelAdapter;
    private Context mContext;
    private View lastView;
    private List<Label> updatedLabel;
    private List<Label> deletedLabel;
    private List<Label> insertedLabel;

    private RecyclerView recyclerView;

    View mView;

    public LabelDialog(Context context, List<Label> labels, int caller) {
        mContext = context;
        mLabels = labels;
        mLabelAdapter = new LabelAdapter(mLabels, this, caller);
        updatedLabel = new ArrayList<>();
        deletedLabel = new ArrayList<>();
        insertedLabel = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView= inflater.inflate(R.layout.dialog_label, null);

        recyclerView = (RecyclerView) mView.findViewById(R.id.rv_note_dialog_label_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mLabelAdapter);

        lastView = null;
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
        final ImageView ivAccept = (ImageView) mView.findViewById(R.id.iv_accept_label);

        etAddLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    ivAccept.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view, View deleteView) {
        String viewName = String.valueOf(mContext.getResources().getResourceEntryName(view.getId()));
        int id = Integer.parseInt(view.getTag().toString());

        if(viewName.equals("tv_note_label")){
            if (deleteView == lastView) {
                if(deleteView.getVisibility() == GONE){
                    deleteView.setVisibility(View.VISIBLE);
                }else {
                    deleteView.setVisibility(GONE);
                }
            } else {
                deleteView.setVisibility(View.VISIBLE);
                if (lastView != null) {
                    lastView.setVisibility(GONE);
                }
                lastView = deleteView;
            }
        }else if(viewName.equals("iv_dialog_delete")){
            int pos = Integer.parseInt(deleteView.getTag().toString());
            if(!mLabels.get(pos).isNew()){
                deletedLabel.add(mLabels.get(pos));
            }else{
                for(Label label : insertedLabel){
                    if(label.getName().equals(mLabels.get(pos).getName())){
                        insertedLabel.remove(label);
                    }
                }
            }
            mLabels.remove(pos);
            mLabelAdapter.notifyItemRemoved(pos);
            mLabelAdapter.notifyItemRangeChanged(pos, mLabels.size());
        }else if(viewName.equals("chk_dialog_label")){
            int pos = Integer.parseInt(view.getTag().toString());
            CheckBox chk = (CheckBox) view;
            mLabels.get(pos).setChecked(chk.isChecked());
        }
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
}
