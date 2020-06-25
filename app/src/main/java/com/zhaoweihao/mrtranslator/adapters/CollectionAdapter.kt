package com.zhaoweihao.mrtranslator.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.zhaoweihao.mrtranslator.R
import com.zhaoweihao.mrtranslator.litepal.Collect
import com.zhaoweihao.mrtranslator.ui.CollectionActivity
import org.litepal.crud.DataSupport

/**
 * Created by Zhaoweihao on 17/7/6.
 */
class CollectionAdapter(private val context: Context, private val mCollectList: List<Collect>) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chineseText: TextView
        var englishText: TextView
        var greyCollectDone: ImageView
        var greyCollect: ImageView

        init {
            chineseText = view.findViewById(R.id.collect_chinese) as TextView
            englishText = view.findViewById(R.id.collect_english) as TextView
            greyCollectDone = view.findViewById(R.id.grey_collect_done) as ImageView
            greyCollect = view.findViewById(R.id.grey_collect) as ImageView
            greyCollect.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.collect_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collect = mCollectList[position]
        holder.englishText.text = collect.english
        holder.chineseText.text = collect.chinese
        holder.greyCollectDone.setOnClickListener {
            val collectId = collect.id
            DataSupport.delete(Collect::class.java, collectId.toLong())
            holder.greyCollectDone.visibility = View.INVISIBLE
            holder.greyCollect.visibility = View.VISIBLE
            Toast.makeText(context, R.string.cancel_success, Toast.LENGTH_SHORT).show()
            if (context is CollectionActivity) {
                //调用activity的方法initViews()更新数据
                context.initViews()
            }
        }
    }

    override fun getItemCount(): Int {
        return mCollectList.size
    }


}