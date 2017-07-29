package com.zhaoweihao.mrtranslator.Activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import com.zhaoweihao.mrtranslator.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by Zhao Weihao on 2017/7/26.
 */

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relativeLayout= (RelativeLayout) findViewById(R.id.about_page);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_translation_100)
                .setDescription(getString(R.string.about_des))
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup(getString(R.string.about_group))
                .addEmail("zhaoweihaochn@foxmail.com")
                .addWebsite("http://zhaoweihao.me")
                .addPlayStore("com.zhaoweihao.mrtranslator")
                .addGitHub("zhaoweihaoChina")
                .create();

        relativeLayout.addView(aboutPage);


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
