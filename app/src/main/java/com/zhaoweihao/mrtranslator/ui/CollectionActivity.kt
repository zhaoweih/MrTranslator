package com.zhaoweihao.mrtranslator.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.zhaoweihao.mrtranslator.R
import com.zhaoweihao.mrtranslator.adapters.CollectionAdapter
import com.zhaoweihao.mrtranslator.litepal.Collect
import com.zhaoweihao.mrtranslator.view.MyItemDecoration
import org.litepal.crud.DataSupport

/**
 * Created by Zhaoweihao on 17/7/6.
 */
class CollectionActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var floatingActionButton: FloatingActionButton? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        floatingActionButton = findViewById(R.id.fab) as FloatingActionButton
        initViews()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp)
        }
        //浮动按钮监听事件
        floatingActionButton!!.setOnClickListener {
            val dialog = AlertDialog.Builder(this@CollectionActivity).create()
            dialog.setTitle(R.string.add_to_notebook)
            val li = layoutInflater
            val v = li.inflate(R.layout.add_note, null)
            dialog.setView(v)
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK)) { dialogInterface, i ->
                val etInput = v.findViewById(R.id.et_input) as TextInputEditText
                val etOutput = v.findViewById(R.id.et_output) as TextInputEditText
                val `in` = etInput.text.toString()
                val out = etOutput.text.toString()
                if (`in`.isEmpty() || out.isEmpty()) {
                    Snackbar.make(floatingActionButton!!, R.string.no_input, Snackbar.LENGTH_SHORT).show()
                } else {
                    //储存数据
                    val collect = Collect()
                    collect.chinese = `in`
                    collect.english = out
                    collect.save()
                    Toast.makeText(applicationContext, R.string.collect_success, Toast.LENGTH_SHORT).show()
                    //更新数据
                    initViews()
                }
            }
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialogInterface, i -> }
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.refresh -> {
                Toast.makeText(this, R.string.refresh_success, Toast.LENGTH_SHORT).show()
                initViews()
            }
            else -> {
            }
        }
        return true
    }

    //recyclerView关联adapter
    fun initViews() {
        val collects = DataSupport.findAll(Collect::class.java)
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        val collectionAdapter = CollectionAdapter(this, collects)
        recyclerView!!.addItemDecoration(MyItemDecoration())
        recyclerView!!.adapter = collectionAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }
}