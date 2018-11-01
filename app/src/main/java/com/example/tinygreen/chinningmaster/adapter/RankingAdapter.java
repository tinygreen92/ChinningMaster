package com.example.tinygreen.chinningmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.mTextDate.setText("운동일자 : " + String.valueOf(mDataset.get(position).start_time));
        holder.mTextCount.setText("횟      수 : " + String.valueOf(mDataset.get(position).count));
        holder.mTtextElapsedTime.setText("경과시간 : " + mDataset.get(position).elapsed_time.toString());
        holder.mTextCorrectionRate.setText("교 정 률 : " + mDataset.get(position).correction_rate.toString());
        holder.mRankingUserId.setText(mDataset.get(position).user_id.toString());
        //TODO : 금은동 프로피 로직
        String data = mDataset.get(position).correction_rate.toString();
        String tmp = data.replace("%","");
        if(tmp.contains("test")) {
            tmp = "80";
        }
        int dataInt = Integer.parseInt(tmp);
        if(dataInt > 79){
            holder.mImageView.setImageResource(R.drawable.rank_gold);
        }else if(dataInt > 59){
            holder.mImageView.setImageResource(R.drawable.rank_silver);
        }else holder.mImageView.setImageResource(R.drawable.rank_bronze);
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
        private TextView mTextDate;
        private TextView mTextCount;
        private TextView mTtextElapsedTime;
        private TextView mTextCorrectionRate;
        private TextView mRankingUserId;
        //
        private ImageView mImageView;

        private ViewHolder(View view) {
            super(view);
            mTextDate = view.findViewById(R.id.textDate);
            mTextCount = view.findViewById(R.id.textCount);
            mTtextElapsedTime = view.findViewById(R.id.textElapsedTime);
            mTextCorrectionRate = view.findViewById(R.id.textCorrectionRate);
            mRankingUserId = view.findViewById(R.id.rankingUserId);
            //
            mImageView = view.findViewById(R.id.imageViewIcon);
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
