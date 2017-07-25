package com.zhaoweihao.mrtranslator.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.Adapter.CollectionAdapter;
import com.zhaoweihao.mrtranslator.Data.Collect;
import com.zhaoweihao.mrtranslator.Other.MyItemDecoration;
import com.zhaoweihao.mrtranslator.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refresh();

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.refresh:
                refresh();
                Toast.makeText(this, R.string.refresh_success, Toast.LENGTH_SHORT).show();
                break;

            default:

        }
        return true;
    }

    private void refresh(){

        List<Collect> collects= DataSupport.findAll(Collect.class);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        CollectionAdapter collectionAdapter=new CollectionAdapter(this,collects);
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(collectionAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

}
