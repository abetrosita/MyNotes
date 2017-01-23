package com.example.abetrosita.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;


public class NotesAdapter  extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{

    private static final String LOG_TAG = NotesAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;

    final private NotesAdapterOnClickHandler mClickHandler;

    public interface NotesAdapterOnClickHandler {
        void onClick(View view);
    }

    public NotesAdapter(Cursor cursor, NotesAdapterOnClickHandler clickHandler) {
        mCursor = cursor;
        mClickHandler = clickHandler;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        mContext = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.note_list_item, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {

        if (mCursor.isClosed() || mCursor ==null) return;
        if(!mCursor.moveToPosition(position)) return;

        String noteLabel = mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.LABEL));
        String noteImagePath = mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.IMAGE_PATH));

        holder.mTextViewTitle.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.TITLE)));
        holder.mTextViewBody.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.BODY)));
        holder.mTextViewDateCreate.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.DATE_CREATED)));
        holder.itemView.setTag(mCursor.getString(
                mCursor.getColumnIndex(BaseColumns._ID)));
        holder.mNoteCardView.setTag(mCursor.getString(
                mCursor.getColumnIndex(BaseColumns._ID)));

        holder.mFlowLayoutLabel.removeAllViews();
        if(noteLabel.length() == 0) {
            holder.mLinearLayoutLabel.setVisibility(View.GONE);
        } else {
            holder.mLinearLayoutLabel.setVisibility(View.VISIBLE);
            holder.mFlowLayoutLabel.addView(new LabelListLayout(mContext, Arrays.asList(noteLabel.split("#,#"))));
        }

        if(noteImagePath.length() == 0) {
            holder.mImageView.setVisibility(View.GONE);
        }else {
            holder.mImageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(Uri.parse(noteImagePath)).into(holder.mImageView);
        }

    }


    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTextViewTitle;
        TextView mTextViewBody;
        TextView mTextViewDateCreate;
        ImageView mImageView;
        CardView mNoteCardView;
        LinearLayout mLinearLayoutLabel;
        FlowLayout mFlowLayoutLabel;

         public NoteViewHolder(View itemView) {
             super(itemView);

             mTextViewTitle = (TextView) itemView.findViewById(R.id.rc_tv_note_title);
             mTextViewBody = (TextView) itemView.findViewById(R.id.rc_tv_note_body);
             mTextViewDateCreate = (TextView) itemView.findViewById(R.id.rc_tv_note_date_edit);
             mNoteCardView = (CardView) itemView.findViewById(R.id.cv_note);
             mImageView = (ImageView) itemView.findViewById(R.id.rc_iv_note_image);
             mNoteCardView.setOnClickListener(this);
             mFlowLayoutLabel = (FlowLayout) itemView.findViewById(R.id.fl_item_labels);
             mLinearLayoutLabel = (LinearLayout) itemView.findViewById(R.id.ll_note_label);
         }


        @Override
        public void onClick(View v) {
            mClickHandler.onClick(v);
        }
    }

    public void reloadNotesData(Cursor cursor){
        if (mCursor != null) mCursor.close();
        mCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
    }

}