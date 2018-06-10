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
 * Created by tinygreen on 2018-05-28.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> implements OnListItemClickListener {

    private ArrayList<Article> mDataset;
    private OnListItemClickListener mListener;
    private Context mContext;

    public ReplyAdapter(ArrayList<Article> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //카드 뷰를 붙여준다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_reply, parent, false);

        ViewHolder vh = new ViewHolder(v);
        //뷰홀더에 클릭리스너 붙이고
        vh.setOnListItemClickListener(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, int position) {
        //내용물이랑 스크롤
        holder.mTextReplyUserId.setText(mDataset.get(position).user_id.toString());
        holder.mTextReplyContent.setText(mDataset.get(position).content.toString());
        holder.mTextReplyTime.setText(mDataset.get(position).time.toString());
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

        private TextView mTextReplyUserId;
        private TextView mTextReplyContent;
        private TextView mTextReplyTime;

        private ViewHolder(View view) {
            super(view);
            mTextReplyUserId = view.findViewById(R.id.textReplyUserId);
            mTextReplyContent = view.findViewById(R.id.textReplyContent);
            mTextReplyTime = view.findViewById(R.id.textReplyTime);
            //
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
