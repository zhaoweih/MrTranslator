package com.zhaoweihao.mrtranslator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.litepal.Collect;
import com.zhaoweihao.mrtranslator.R;
import com.zhaoweihao.mrtranslator.utils.HttpUtil;
import com.zhaoweihao.mrtranslator.utils.Utility;
import com.zhaoweihao.mrtranslator.constant.Constant;
import com.zhaoweihao.mrtranslator.gson.Translate;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Zhaoweihao on 17/7/6.
 * 如果对我的项目有任何疑问可以给我发邮件或者提issues
 * Email:zhaoweihaochn@gmail.com
 * 如果觉得我的项目写得好可以给我star和fork
 * 谢谢！
 */

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText;
    private FloatingActionButton button;
    private LinearLayout translationLayout;
    private TextView phoneticText;
    private LinearLayout explainsLayout;
    private TextView queryText;
    private LinearLayout webLayout;
    private ProgressBar progressBar;
    private ImageView clearView;
    private ImageView copyImage;
    private ImageView shareImage;
    private TextView translateTitle;
    private TextView explainsTitle;
    private TextView webTitle;
    private LinearLayout mixLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageView collectImage;
    private ImageView collectDoneImage;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        initViews();

        //navigationView点击事件
        navigationView.setCheckedItem(R.id.nav_collect);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_collect:
                        intent=new Intent(MainActivity.this,CollectionActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent=new Intent(MainActivity.this,SettingsPreferenceActivity.class);
                        startActivity(intent);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //删除按钮点击事件
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        //editText输入监控事件，如果有文字输入显示删除按钮和翻译按钮，如果为空则隐藏按钮
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText.getEditableText().toString().length() != 0){
                    clearView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                } else {
                    clearView.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //翻译按钮监控事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果编辑框输入文字为空则snackbar提示输入文本为空
                if(editText.getText().toString().isEmpty()){
                    Snackbar.make(button,R.string.input_empty,Snackbar.LENGTH_SHORT)
                            .show();
                }else {
                    //显示progressbar
                    progressBar.setVisibility(View.VISIBLE);
                    //收起输入法
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);}
                    //获取输入框的文字内容
                    String word = editText.getText().toString();
                    //获取有道api初始url
                    String url = Constant.YOUDAO_URL;
                    //okhttp发送网络请求,url+word是初始url拼接word组成api
                    HttpUtil.sendOkHttpRequest(url + word, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //网络请求失败，开启子进程，更新页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, R.string.translate_fail, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //网络请求成功，获取json数据
                            String responseData = response.body().string();
                            //利用gson处理json数据，转化为对象
                            final Translate translate = Utility.handleTranslateResponse(responseData);
                            //开启子进程更新界面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //json数据中errorcode为0表示获取数据成功,20代表文本过长,其他就是出现错误，具体可查有道api使用事项
                                    if (translate.getErrorCode() == 0) {
                                        //将数据显示在界面上
                                        showTranslateInfo(translate);
                                    } else if (translate.getErrorCode() == 20) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, R.string.translate_overlong, Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, R.string.translate_fail, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    private void showTranslateInfo(final Translate translate){
        //先清空layout里面的东西
        translationLayout.removeAllViews();
        explainsLayout.removeAllViews();
        webLayout.removeAllViews();
        //收藏成功按钮隐藏
        collectDoneImage.setVisibility(View.INVISIBLE);
        //设置title
        translateTitle.setText(R.string.translate_title);
        explainsTitle.setText(R.string.explains_title);
        webTitle.setText(R.string.web_title);
        //将请求回来的数据显示在textview上
        for (int i = 0;i<translate.getTranslation().length;i++)
        {
            View view= LayoutInflater.from(this).inflate(R.layout.translation_item,translationLayout,false);
            TextView translateText= (TextView) view.findViewById(R.id.translation_text);
            translateText.setText(translate.getTranslation()[i]);
            translateText.setTextColor(getResources().getColor(R.color.white));
            translateText.setTextSize(25);
            translationLayout.addView(view);
        }

        queryText.setText(translate.getQuery());
        if(translate.getBasic()==null){
            phoneticText.setVisibility(View.INVISIBLE);
            explainsLayout.setVisibility(View.INVISIBLE);
        }else {

        phoneticText.setText("["+translate.getBasic().getPhonetic()+"]");
            for (int i = 0;i<translate.getBasic().getExplains().length;i++){
                View view=LayoutInflater.from(this).inflate(R.layout.explains_item,explainsLayout,false);
                TextView explainsText= (TextView) view.findViewById(R.id.expalins_text);
                explainsText.setText(translate.getBasic().getExplains()[i]);
                explainsLayout.addView(view);
                phoneticText.setVisibility(View.VISIBLE);
                explainsLayout.setVisibility(View.VISIBLE);
            }
        }


        if(translate.getWeb()==null){
            webLayout.setVisibility(View.INVISIBLE);
        }else {

        for (int i = 0;i<translate.getWeb().size();i++){
            View view=LayoutInflater.from(this).inflate(R.layout.web_item,webLayout,false);
            TextView keyText= (TextView) view.findViewById(R.id.key_text);
            TextView valueText= (TextView) view.findViewById(R.id.value_text);
            keyText.setText(translate.getWeb().get(i).getKey());
            String values=getFinalValue(translate.getWeb().get(i).getValue());
            valueText.setText(values);
            webLayout.addView(view);
            webLayout.setVisibility(View.VISIBLE);
        }
        }
        //显示好后隐藏progressbar
        progressBar.setVisibility(View.GONE);
        //复制按钮监听事件
        copyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", translate.getTranslation()[0]);
                manager.setPrimaryClip(clipData);
                Snackbar.make(button,R.string.copy_success,Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        //分享按钮监听事件
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,translate.getTranslation()[0]);
                startActivity(Intent.createChooser(intent,getString(R.string.share_choice)));
            }
        });
        //收藏按钮监听事件
        collectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //利用litepal将数据存入数据库
                Collect collect=new Collect();
                collect.setChinese(translate.getTranslation()[0]);
                collect.setEnglish(translate.getQuery());
                collect.save();
                Toast.makeText(MainActivity.this, R.string.collect_success, Toast.LENGTH_SHORT).show();
                //显示收藏成功按钮，隐藏收藏按钮
                collectDoneImage.setVisibility(View.VISIBLE);
                collectImage.setVisibility(View.INVISIBLE);
            }
        });
        //收藏成功按钮监听事件
        collectDoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从数据库删除该数据
                DataSupport.deleteAll(Collect.class,"english=?",translate.getQuery());
                Toast.makeText(MainActivity.this, R.string.deletesuccess, Toast.LENGTH_SHORT).show();
                collectDoneImage.setVisibility(View.INVISIBLE);
                collectImage.setVisibility(View.VISIBLE);
            }
        });

        copyImage.setVisibility(View.VISIBLE);
        shareImage.setVisibility(View.VISIBLE);
        mixLayout.setVisibility(View.VISIBLE);
        collectImage.setVisibility(View.VISIBLE);
    }

    private  String getFinalValue(String[] value) {
        String finalValue="";
        for (int i = 0;i<value.length;i++){
            if(i==value.length-1){
                finalValue=finalValue+value[i];
            }else {
                finalValue = finalValue + value[i] + ",";
            }
        }
        return finalValue;
    }
    //获取控件实例
    private void initViews(){

        toolbar= (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        editText= (EditText)findViewById(R.id.word_input);

        button= (FloatingActionButton)findViewById(R.id.translate_btn);

        button.setVisibility(View.INVISIBLE);

        translationLayout= (LinearLayout) findViewById(R.id.translation_layout);

        phoneticText= (TextView) findViewById(R.id.phonetic_text);

        explainsLayout= (LinearLayout) findViewById(R.id.explains_layout);

        queryText= (TextView) findViewById(R.id.query_text);

        webLayout= (LinearLayout) findViewById(R.id.web_layout);

        progressBar= (ProgressBar) findViewById(R.id.progress_bar);

        clearView= (ImageView) findViewById(R.id.iv_clear);

        clearView.setVisibility(View.INVISIBLE);

        copyImage= (ImageView) findViewById(R.id.copy);

        copyImage.setVisibility(View.INVISIBLE);

        shareImage= (ImageView) findViewById(R.id.share);

        shareImage.setVisibility(View.INVISIBLE);

        translateTitle= (TextView) findViewById(R.id.translate_title);

        explainsTitle= (TextView) findViewById(R.id.explains_title);

        webTitle= (TextView) findViewById(R.id.web_title);

        mixLayout= (LinearLayout) findViewById(R.id.mixLayout);

        mixLayout.setVisibility(View.INVISIBLE);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView= (NavigationView) findViewById(R.id.nav_view);

        collectImage= (ImageView) findViewById(R.id.collect);

        collectImage.setVisibility(View.INVISIBLE);

        collectDoneImage= (ImageView) findViewById(R.id.collect_done);

        collectDoneImage.setVisibility(View.INVISIBLE);

        ActionBar actionBar=getSupportActionBar();

        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

    }

}
