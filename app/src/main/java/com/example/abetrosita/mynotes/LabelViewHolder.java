package com.example.abetrosita.mynotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.GONE;

/**
 * Created by AbetRosita on 1/31/2017.
 */

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

//        if(mCaller == AppConstant.NOTE_CALLER_MAIN){
//            chkLabel.setVisibility(GONE);
//            ivDialogEdit.setVisibility(VISIBLE);
//        }else {
//            chkLabel.setVisibility(VISIBLE);
//            ivDialogEdit.setVisibility(GONE);
//        }

        label.setOnClickListener(this);
        ivDialogDelete.setOnClickListener(this);
        chkLabel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //mClickHandler.onClick(v, ivDialogDelete);
    }
}