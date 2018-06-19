package com.example.tinygreen.chinningmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.Record;

import java.util.ArrayList;

/**
 * Created by tinygreen on 2018-05-28.
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> implements OnListItemClickListener {

    private ArrayList<Record> mDataset;
    private OnListItemClickListener mListener;
    private Context mContext;

    public RankingAdapter(ArrayList<Record> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //카드 뷰를 붙여준다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_rank, parent, false);

        ViewHolder vh = new ViewHolder(v);
        //뷰홀더에 클릭리스너 붙이고
        vh.setOnListItemClickListener(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        //내용물이랑 스크롤
        holder.mTextCount.setText("횟    수 : " + String.valueOf(mDataset.get(position).count));
        holder.mTtextElapsedTime.setText("경과 시간 : " + mDataset.get(position).elapsed_time.toString());
        holder.mTextCorrectionRate.setText("교 정 률 : " + mDataset.get(position).correction_rate.toString());
        holder.mRankingUserId.setText(mDataset.get(position).user_id.toString());
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

        private TextView mTextCount;
        private TextView mTtextElapsedTime;
        private TextView mTextCorrectionRate;
        private TextView mRankingUserId;

        private ViewHolder(View view) {
            super(view);
            mTextCount = view.findViewById(R.id.textCount);
            mTtextElapsedTime = view.findViewById(R.id.textElapsedTime);
            mTextCorrectionRate = view.findViewById(R.id.textCorrectionRate);
            mRankingUserId = view.findViewById(R.id.rankingUserId);
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
