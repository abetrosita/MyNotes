package com.example.abetrosita.mynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class LabelDialog extends AppCompatActivity {

    private int mDialogAction;
    private Context mContext;
    private List<String> mLabels;
    private String mTitle;
    private String mMessage;
    private boolean mAction;

    public LabelDialog(Context context) {
        mContext = context;
        mTitle = "CUSTOM DIALOG TITLE";
        mMessage = "Custom dialog message";
        mLabels = new ArrayList<>();
        generateTempLabelData();
        showDialog();
    }

    public void showDialog(){
        AlertDialog.Builder   alertdialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflaterr = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view= inflaterr.inflate(R.layout.note_label_dialog, null);
        //Log.d("DIALOG_TEST", "LABELS: " +tempLabelData().toString());
        final LabelRecyclerView labelRecyclerView = new  LabelRecyclerView(mLabels,
                (RecyclerView) view.findViewById(R.id.rv_note_dialog_label_list),mContext);

        alertdialog.setView(view);
        alertdialog.setTitle("Manage Note Labels");

        alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){
                Toast.makeText(mContext, "OK Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });

        alertdialog.show();

        ImageView ivAddLabel = (ImageView) view.findViewById(R.id.iv_add_label);
        ivAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLabels.add(String.valueOf(((EditText) view.findViewById(R.id.et_add_label)).getText()));
                labelRecyclerView.loadLabels(mLabels);
            }
        });

    }


    public void showTempDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        //dialogBuilder.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Hardik");
        arrayAdapter.add("Archit");
        arrayAdapter.add("Jignesh");
        arrayAdapter.add("Umang");
        arrayAdapter.add("Gatti");

        dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        dialogBuilder.show();
    }

    private void actionDialog(){
        Toast.makeText(mContext, "OK is Clicked", Toast.LENGTH_SHORT).show();
    }

    private void generateTempLabelData(){
        mLabels.add("Hardik");
        mLabels.add("Archit");
        mLabels.add("Jignesh");
        mLabels.add("Umang");
        mLabels.add("Gatti");
    }

}
