package com.example.tinygreen.chinningmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.Article;

import java.util.ArrayList;

/**
 * Created by tinygreen on 2018-05-13.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements OnListItemClickListener {

    private ArrayList<Article> mDataset;
    private OnListItemClickListener mListener;
    private Context mContext;

    public RecyclerAdapter (ArrayList<Article> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //카드 뷰를 붙여준다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        //뷰홀더에 클릭리스너 붙이고
        vh.setOnListItemClickListener(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        //내용물이랑 스크롤
        holder.mTextContentView.setText(mDataset.get(position).title.toString());
        holder.mTextAlramView.setText(mDataset.get(position).content.toString());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onListItemClick(int position) {

    }

    /**
     * Inner Class ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextContentView;
        private TextView mTextAlramView;

        private ViewHolder(View view) {
            super(view);
            mTextContentView = view.findViewById(R.id.textTitle);
            mTextAlramView = view.findViewById(R.id.textContent);
            //뷰에 리스너 연결
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListItemClick(getAdapterPosition());

                }
            });

        }
        //리스너 구현
        private void setOnListItemClickListener(OnListItemClickListener onListItemClickListener){
            mListener = onListItemClickListener;
        }

    }//뷰홀더 클래스 end



}