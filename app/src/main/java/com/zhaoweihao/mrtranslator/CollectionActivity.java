package com.zhaoweihao.mrtranslator;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zhaoweihao.mrtranslator.Data.Collect;

import org.litepal.crud.DataSupport;

import java.util.Collection;
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
        List<Collect> collects= DataSupport.findAll(Collect.class);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        CollectionAdapter collectionAdapter=new CollectionAdapter(collects);
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(collectionAdapter);

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

            default:

        }
        return true;
    }


}
