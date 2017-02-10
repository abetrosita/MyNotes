package com.example.abetrosita.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class LabelRecyclerClickListener implements RecyclerView.OnItemTouchListener {
    public static interface OnItemClickListener{
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    final OnItemClickListener itemClickListener;
    private GestureDetector gestureDetector;

    public LabelRecyclerClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener clickListener){
        this.itemClickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            public boolean onSingleTapUp(MotionEvent e){
                Log.d("GESTURE","CLICKED");
                return true;
            }

            public void onLongPress(MotionEvent e){
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && itemClickListener != null){
                    itemClickListener.onItemLongClick(childView, recyclerView.getChildLayoutPosition(childView));
                }
            }

        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if(childView != null && itemClickListener != null && gestureDetector.onTouchEvent(e)){
            itemClickListener.onItemClick(childView, rv.getChildLayoutPosition(childView));
            //Log.d("GESTURE",childView.getTag().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
