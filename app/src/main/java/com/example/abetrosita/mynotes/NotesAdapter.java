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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by AbetRosita on 1/14/2017.
 */

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
        //Log.d(LOG_TAG, "+++ NUMBER OF ITEMS IN DB IS " + String.valueOf(mCursor.getCount()));
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int noteListItemLayout = R.layout.note_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(noteListItemLayout, viewGroup,
                shouldAttachToParentImmediately);
        NoteViewHolder viewHolder = new NoteViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        //Log.d(LOG_TAG, "+++ BIND ENTERED");
        if (mCursor.isClosed() || mCursor ==null) return;
        if(!mCursor.moveToPosition(position)) return;

        String noteLabel = mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.LABEL));
        String noteImagePath = mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.IMAGE_PATH));

        holder.noteTitle.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.TITLE)));
        holder.noteBody.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.BODY)));
        holder.noteLabel.setText(noteLabel);
        holder.noteDateCreate.setText(mCursor.getString(
                mCursor.getColumnIndex(NotesContract.Columns.DATE_CREATED)));
        holder.itemView.setTag(mCursor.getString(
                mCursor.getColumnIndex(BaseColumns._ID)));
        holder.noteCard.setTag(mCursor.getString(
                mCursor.getColumnIndex(BaseColumns._ID)));

        if(noteLabel.length() == 0) {
            holder.noteLabel.setVisibility(View.GONE);
        } else {
            holder.noteLabel.setVisibility(View.VISIBLE);
        }
        if(noteImagePath.length() == 0) {
            holder.noteImage.setVisibility(View.GONE);
        }else {
            holder.noteImage.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(Uri.parse(noteImagePath)).into(holder.noteImage);
        }

    }


    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView noteTitle;
        TextView noteBody;
        TextView noteLabel;
        TextView noteDateCreate;
        ImageView noteImage;
        CardView noteCard;


        public NoteViewHolder(View itemView) {
            super(itemView);

            noteTitle = (TextView) itemView.findViewById(R.id.rc_tv_note_title);
            noteBody = (TextView) itemView.findViewById(R.id.rc_tv_note_body);
            noteLabel = (TextView) itemView.findViewById(R.id.rc_tv_note_label);
            noteDateCreate = (TextView) itemView.findViewById(R.id.rc_tv_note_date_edit);
            noteCard = (CardView) itemView.findViewById(R.id.cv_note);
            noteImage = (ImageView) itemView.findViewById(R.id.rc_iv_note_image);
            noteCard.setOnClickListener(this);

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