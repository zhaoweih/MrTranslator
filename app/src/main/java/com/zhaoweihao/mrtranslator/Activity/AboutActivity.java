package com.zhaoweihao.mrtranslator.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.R;

/**
 * Created by Zhao Weihao on 2017/7/26.
 */

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView githubText;

    private TextView blogText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        githubText= (TextView) findViewById(R.id.github_text);
        blogText= (TextView) findViewById(R.id.blog_text);

        githubText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.githubURL)));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    showError();
                }

            }
        });

        blogText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.blogURL)));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    showError();
                }

            }
        });

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
        }
    }

    private void showError() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
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
