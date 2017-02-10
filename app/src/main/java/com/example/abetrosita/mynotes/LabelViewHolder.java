package com.example.abetrosita.mynotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.abetrosita.mynotes.AppConstant.ON_EDIT_MODE;

/**
 * Created by AbetRosita on 1/31/2017.
 */

public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    protected TextView label;
    protected EditText editLabel;
    protected ImageView ivDialogDelete;
    protected ImageView ivDialogEdit;
    protected ImageView ivDialogAccept;
    protected CheckBox chkLabel;
    protected ViewSwitcher switcher;
    protected ImageView icHandle;
    protected ImageView icNoteLabel;
    private int mCaller;
    final private LabelOnClickHandler mClickHandler;

    public LabelViewHolder(View itemView, int caller, LabelOnClickHandler clickHandler) {
        super(itemView);
        mCaller = caller;
        mClickHandler = clickHandler;
        label = (TextView) itemView.findViewById(R.id.tv_note_label);
        editLabel = (EditText) itemView.findViewById(R.id.hidden_edit_view);
        chkLabel = (CheckBox) itemView.findViewById(R.id.chk_dialog_label);
        icHandle = (ImageView) itemView.findViewById(R.id.label_move_handle);
        ivDialogDelete = (ImageView) itemView.findViewById(R.id.iv_dialog_delete);
        ivDialogAccept = (ImageView) itemView.findViewById(R.id.iv_dialog_accept);
        ivDialogEdit = (ImageView) itemView.findViewById(R.id.iv_dialog_edit);
        switcher = (ViewSwitcher) itemView.findViewById(R.id.label_switcher);
        icNoteLabel = (ImageView) itemView.findViewById(R.id.ic_note_label);

        ivDialogDelete.setVisibility(GONE);
        ivDialogAccept.setVisibility(GONE);


        if(mCaller == AppConstant.NOTE_CALLER_MAIN){
            ON_EDIT_MODE = false;
            chkLabel.setVisibility(GONE);
        }else {
            ON_EDIT_MODE =false;
            chkLabel.setVisibility(VISIBLE);
            ivDialogEdit.setVisibility(GONE);
            icHandle.setVisibility(View.GONE);
        }

        ivDialogEdit.setOnClickListener(this);
        ivDialogAccept.setOnClickListener(this);
        ivDialogDelete.setOnClickListener(this);
        chkLabel.setOnClickListener(this);
        label.setOnClickListener(this);

    }

    public interface LabelOnClickHandler {
        void onClick(LabelViewHolder holder, String viewTag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()){
            case "labelEdit":
                editMainLabelViews();
                break;
            case "labelAccept":
                resetMainLabelViews();
                break;
            case "editLabel":
                editMainLabelViews();
                break;
            case "labelName":
                if(ON_EDIT_MODE) {
                    editMainLabelViews();
                }else if(mCaller == AppConstant.NOTE_CALLER_DETAIL){
                    chkLabel.setChecked(!chkLabel.isChecked());
                    v = chkLabel;
                }
        }

        mClickHandler.onClick(this, v.getTag().toString());
    }

    public void editMainLabelViews(){
        switcher.showNext();
        editLabel.requestFocusFromTouch();
        ivDialogEdit.setVisibility(GONE);
        icNoteLabel.setVisibility(GONE);
        ivDialogAccept.setVisibility(VISIBLE);
        ivDialogDelete.setVisibility(VISIBLE);
    }

    public void resetMainLabelViews(){
        if(switcher.getCurrentView() != label){
            switcher.showNext();
        }
        icNoteLabel.setVisibility(GONE);
        ivDialogEdit.setVisibility(VISIBLE);
        ivDialogAccept.setVisibility(GONE);
        ivDialogDelete.setVisibility(GONE);
    }

}