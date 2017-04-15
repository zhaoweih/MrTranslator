package com.zhaoweihao.mrtranslator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.Util.HttpUtil;
import com.zhaoweihao.mrtranslator.Util.Utility;
import com.zhaoweihao.mrtranslator.gson.Translate;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText editText;

    private Button button;

//    private TextView dataShown;

    private LinearLayout translationLayout;

    private TextView phoneticText;

    private LinearLayout explainsLayout;

    private TextView queryText;

    private LinearLayout webLayout;

//    private LinearLayout valueLayout;

    private ProgressBar progressBar;

    private TextView clearText;

    private ImageView copyImage;

    private ImageView shareImage;

    private TextView translateTitle;

    private TextView explainsTitle;

    private TextView webTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        toolbar= (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        editText= (EditText)findViewById(R.id.word_input);

        button= (Button)findViewById(R.id.translate_btn);

//        dataShown= (TextView)findViewById(R.id.data_shown);

        translationLayout= (LinearLayout) findViewById(R.id.translation_layout);

        phoneticText= (TextView) findViewById(R.id.phonetic_text);

        explainsLayout= (LinearLayout) findViewById(R.id.explains_layout);

        queryText= (TextView) findViewById(R.id.query_text);

        webLayout= (LinearLayout) findViewById(R.id.web_layout);

//        valueLayout= (LinearLayout) findViewById(R.id.value_layout);

        progressBar= (ProgressBar) findViewById(R.id.progress_bar);

        clearText= (TextView) findViewById(R.id.tv_clear);

        clearText.setVisibility(View.INVISIBLE);

        copyImage= (ImageView) findViewById(R.id.copy);

        copyImage.setVisibility(View.INVISIBLE);

        shareImage= (ImageView) findViewById(R.id.share);

        shareImage.setVisibility(View.INVISIBLE);

        translateTitle= (TextView) findViewById(R.id.translate_title);

        explainsTitle= (TextView) findViewById(R.id.explains_title);

        webTitle= (TextView) findViewById(R.id.web_title);


        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText.getEditableText().toString().length() != 0){
                    clearText.setVisibility(View.VISIBLE);
                } else {
                    clearText.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText.getText().toString().isEmpty()){
                    Snackbar.make(button,R.string.input_empty,Snackbar.LENGTH_SHORT)
                            .show();
                }else {

                    progressBar.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
                    }


                    String word = editText.getText().toString();

                    String url = "http://fanyi.youdao.com/openapi.do?keyfrom=yourkeyfrom&key=yourkey&type=data&doctype=json&version=1.1&q=";

                    HttpUtil.sendOkHttpRequest(url + word, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

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
                            String responseData = response.body().string();
                            final Translate translate = Utility.handleTranslateResponse(responseData);
//                        showResponseData(responseData);
//                        parseJSONWithGSON(responseData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (translate.getErrorCode() == 0) {
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

//    private void showResponseData(final String response){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//    }
//
//    private void parseJSONWithGSON(String jsonData){
//        Gson gson=new Gson();
//        Translate translate=gson.fromJson(jsonData,Translate.class);
//        for (int i = 0;i<translate.getTranslation().length;i++)
//        {
//            Log.d("MainActivity",translate.getTranslation()[i]);
//        }
//        Log.d("MainActivity",translate.getBasic().getPhonetic());
//        for (int i = 0;i<translate.getBasic().getExplains().length;i++){
//            Log.d("MainActivity",translate.getBasic().getExplains()[i]);
//        }
//        Log.d("MainActivity",translate.getQuery());
//        Log.d("MainActivity",String.valueOf(translate.getErrorCode()));
//        for (int i = 0;i<translate.getWeb().size();i++){
//            for(int j = 0; j<translate.getWeb().get(i).getValue().length;j++)
//            {
//                Log.d("MainActivity",translate.getWeb().get(i).getValue()[j]);
//            }
//            Log.d("MainActivity",translate.getWeb().get(i).getKey());
//        }
//    }

    private void showTranslateInfo(final Translate translate){
        translationLayout.removeAllViews();
        explainsLayout.removeAllViews();
//        valueLayout.removeAllViews();
        webLayout.removeAllViews();

        translateTitle.setText(R.string.translate_title);

        explainsTitle.setText(R.string.explains_title);

        webTitle.setText(R.string.web_title);

        for (int i = 0;i<translate.getTranslation().length;i++)
        {
            View view= LayoutInflater.from(this).inflate(R.layout.translation_item,translationLayout,false);
            TextView translateText= (TextView) view.findViewById(R.id.translation_text);
            translateText.setText(translate.getTranslation()[i]);
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

        progressBar.setVisibility(View.GONE);

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

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,translate.getTranslation()[0]);
                startActivity(Intent.createChooser(intent,getString(R.string.share_choice)));
            }
        });

        copyImage.setVisibility(View.VISIBLE);

        shareImage.setVisibility(View.VISIBLE);





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

}
