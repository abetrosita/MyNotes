package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.example.abetrosita.mynotes.MainActivity.mContentResolver;


public class LabelDialog implements LabelAdapter.LabelOnClickHandler{

    private static final int LOADER_ID = 100;

    private Cursor mCursor;
    private List<Label> mLabels;
    private LabelAdapter mLabelAdapter;
    private LoaderManager.LoaderCallbacks mCallbacks;
    LabelRecyclerView mLabelRecyclerView;
    private Context mContext;
    private View lastView;
    private List<Label> updatedLabel;
    private List<Label> deletedLabel;
    private List<Label> insertedLabel;

    View mView;

    public LabelDialog(Context context, List<Label> labels) {
        mContext = context;
        mLabels = labels;
        mLabelAdapter = new LabelAdapter(mLabels, this);
        updatedLabel = new ArrayList<>();
        deletedLabel = new ArrayList<>();
        insertedLabel = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView= inflater.inflate(R.layout.note_label_dialog, null);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.rv_note_dialog_label_list);
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
                Toast.makeText(mContext, "OK Clicked", Toast.LENGTH_SHORT).show();
                if(deletedLabel != null){
                    for(Label label : deletedLabel){
                        Uri uri = LabelContract.Labels.buildLabelUri(String.valueOf(label.getId()));
                        mContentResolver.delete(uri, null, null);
                        Log.d("URI_ALBEL", uri.toString());
                    }
                }

                //mLoaderManager.restartLoader(NOTE_LOADER_ID, null, noteLoader);

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int id){
                dia.cancel();
            }
        });

        dialog.show();

        ((EditText) mView.findViewById(R.id.et_add_label)).clearFocus();
        ImageView ivAddLabel = (ImageView) mView.findViewById(R.id.iv_add_label);
        ivAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Label label = new Label(String.valueOf(((EditText) mView.findViewById(R.id.et_add_label)).getText()));
                ContentValues values= label.getContentValues();
                mLabels.add(label);
                mLabelAdapter.notifyItemInserted(mLabels.size());
                mContentResolver.insert(LabelContract.URI_TABLE, values);

                //mCursor = mContentResolver.query(LabelContract.URI_TABLE, null, null, null, null);
                //mLabelAdapter.loadLabelData(mCursor);
            }
        });

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
        }else if(viewName.equals("iv_delete_label")){
            int pos = Integer.parseInt(deleteView.getTag().toString());
            deletedLabel.add(mLabels.get(pos));
            mLabels.remove(pos);

            mLabelAdapter.notifyItemRemoved(pos);
            mLabelAdapter.notifyItemRangeChanged(pos, mLabels.size());
        }

    }
}
