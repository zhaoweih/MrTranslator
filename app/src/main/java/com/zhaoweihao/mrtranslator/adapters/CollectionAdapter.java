package com.zhaoweihao.mrtranslator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.litepal.Collect;
import com.zhaoweihao.mrtranslator.R;
import com.zhaoweihao.mrtranslator.ui.CollectionActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>{

    private List<Collect>  mCollectList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chineseText;
        TextView englishText;
        ImageView greyCollectDone;
        ImageView greyCollect;

        public ViewHolder(View view){
            super(view);
            chineseText= (TextView) view.findViewById(R.id.collect_chinese);
            englishText= (TextView) view.findViewById(R.id.collect_english);
            greyCollectDone= (ImageView) view.findViewById(R.id.grey_collect_done);
            greyCollect= (ImageView) view.findViewById(R.id.grey_collect);

            greyCollect.setVisibility(View.INVISIBLE);

        }
    }

    public CollectionAdapter(Context mContext,List<Collect> collectList){
        this.context=mContext;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Collect collect=mCollectList.get(position);
        holder.englishText.setText(collect.getEnglish());
        holder.chineseText.setText(collect.getChinese());

        holder.greyCollectDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int collectId=collect.getId();
                DataSupport.delete(Collect.class,collectId);
                holder.greyCollectDone.setVisibility(View.INVISIBLE);
                holder.greyCollect.setVisibility(View.VISIBLE);
                Toast.makeText(context, R.string.cancel_success, Toast.LENGTH_SHORT).show();

                if(context instanceof CollectionActivity){
                    ((CollectionActivity)context).initViews();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mCollectList.size();
    }
}
