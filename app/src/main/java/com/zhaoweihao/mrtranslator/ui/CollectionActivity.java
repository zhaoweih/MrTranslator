package com.zhaoweihao.mrtranslator.ui;

import android.content.DialogInterface;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.zhaoweihao.mrtranslator.adapters.CollectionAdapter;

import com.zhaoweihao.mrtranslator.litepal.Collect;
import com.zhaoweihao.mrtranslator.view.MyItemDecoration;
import com.zhaoweihao.mrtranslator.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton= (FloatingActionButton) findViewById(R.id.fab);

        initViews();

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(CollectionActivity.this).create();
                dialog.setTitle(R.string.add_to_notebook);
                LayoutInflater li = getLayoutInflater();
                final View v = li.inflate(R.layout.add_note,null);
                dialog.setView(v);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        TextInputEditText etInput = (TextInputEditText) v.findViewById(R.id.et_input);
                        TextInputEditText etOutput = (TextInputEditText) v.findViewById(R.id.et_output);

                        String in = etInput.getText().toString();
                        String out = etOutput.getText().toString();

                        if (in.isEmpty() || out.isEmpty()){

                            Snackbar.make(floatingActionButton, R.string.no_input, Snackbar.LENGTH_SHORT).show();

                        } else {

                            Collect collect=new Collect();
                            collect.setChinese(in);
                            collect.setEnglish(out);
                            collect.save();
                            Toast.makeText(getApplicationContext(), R.string.collect_success, Toast.LENGTH_SHORT).show();
                            initViews();
                        }


                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.refresh:
                Toast.makeText(this, R.string.refresh_success, Toast.LENGTH_SHORT).show();
                initViews();
                break;

            default:

        }
        return true;
    }

    public void initViews(){

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
