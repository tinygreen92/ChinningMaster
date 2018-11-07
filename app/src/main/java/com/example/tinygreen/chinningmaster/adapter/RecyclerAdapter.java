package com.example.tinygreen.chinningmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.activity.ArticleActivity;
import com.example.tinygreen.chinningmaster.activity.CommunityActivity;
import com.example.tinygreen.chinningmaster.models.Article;

import java.util.ArrayList;

/**
 * Created by tinygreen on 2018-05-13.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements OnListItemClickListener {

    private ArrayList<Article> mDataset;
    private ArrayList reCntArray;
    private OnListItemClickListener mListener;
    private Context mContext;

//    //TODO : 리플
//    public RecyclerAdapter (ArrayList<Article> myDataset, ArrayList reCntArrayData, Context context) {
//        mDataset = myDataset;
//        reCntArray = reCntArrayData;
//        mContext = context;
//    }

    public RecyclerAdapter (ArrayList<Article> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //카드 뷰를 붙여준다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_article, parent, false);

        ViewHolder vh = new ViewHolder(v);
        //뷰홀더에 클릭리스너 붙이고
        vh.setOnListItemClickListener(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        //내용물이랑 스크롤
        holder.mTextTitleView.setText(mDataset.get(position).title.toString());
        holder.mTextContentView.setText(mDataset.get(position).content.toString());
//        holder.mTextArticleIdView.setText(String.valueOf(mDataset.get(position).article_id));
        holder.mTextTimeView.setText(mDataset.get(position).time.toString());
        holder.mTextUserIdView.setText(mDataset.get(position).user_id.toString());

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

        private TextView mTextTitleView;
        private TextView mTextContentView;
        private TextView mTextArticleIdView;
        private TextView mTextTimeView;
        private TextView mTextUserIdView;
        /**
         * TODO : 리플 갯수 얻어오기
         */
        private TextView mTextReplyCntView;

        private ViewHolder(View view) {
            super(view);
            mTextTitleView = view.findViewById(R.id.textTitle);
            mTextContentView = view.findViewById(R.id.textContent);
//            mTextArticleIdView = view.findViewById(R.id.textArticleId);
            mTextTimeView = view.findViewById(R.id.textTime);
//            mTextReplyCntView = view.findViewById(R.id.textReplyCnt);
            mTextUserIdView = view.findViewById(R.id.textUserId);
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
