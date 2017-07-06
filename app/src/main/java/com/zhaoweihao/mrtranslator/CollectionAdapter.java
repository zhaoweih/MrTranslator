package com.zhaoweihao.mrtranslator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoweihao.mrtranslator.Data.Collect;

import java.util.List;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>{

    private List<Collect>  mCollectList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chineseText;
        TextView englishText;

        public ViewHolder(View view){
            super(view);
            chineseText= (TextView) view.findViewById(R.id.collect_chinese);
            englishText= (TextView) view.findViewById(R.id.collect_english);

        }
    }

    public CollectionAdapter(List<Collect> collectList){
        mCollectList=collectList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collect_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Collect collect=mCollectList.get(position);
        holder.englishText.setText(collect.getEnglish());
        holder.chineseText.setText(collect.getChinese());

    }

    @Override
    public int getItemCount() {
        return mCollectList.size();
    }
}
