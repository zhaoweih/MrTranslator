package com.zhaoweihao.mrtranslator.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;


import com.zhaoweihao.mrtranslator.R;
import com.zhaoweihao.mrtranslator.utils.HttpUtil;
import com.zhaoweihao.mrtranslator.utils.Utility;
import com.zhaoweihao.mrtranslator.constant.Constant;
import com.zhaoweihao.mrtranslator.gson.Translate;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhaoWeihao on 2017/8/15.
 */

public class ClipboardService extends Service {

    private ClipboardManager manager;

//    private RequestQueue queue;

    Handler handler;

    private static final String TAG = ClipboardService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler=new Handler();

        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        manager.addPrimaryClipChangedListener(listener);

//        queue = Volley.newRequestQueue(ClipboardService.this.getApplicationContext());

    }

    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            if (manager.hasPrimaryClip()){

                ClipData data = manager.getPrimaryClip();

                handleClipData(data.getItemAt(0).getText().toString());

            }

        }
    };

    private void handleClipData(final  String clipData){


            String word = clipData;

            String url = Constant.YOUDAO_URL;

            HttpUtil.sendOkHttpRequest(url + word, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.translate_fail, Toast.LENGTH_SHORT).show();

                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    final Translate translate = Utility.handleTranslateResponse(responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (translate.getErrorCode() == 0) {
                                showTranslateInfo(translate);
                            } else if (translate.getErrorCode() == 20) {
                                Toast.makeText(getApplicationContext(), R.string.translate_overlong, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.translate_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            });
        }

    private void showTranslateInfo(final Translate translate){

        String result=translate.getTranslation()[0];

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ClipboardService.this)
                                        .setSmallIcon(R.drawable.small_icon)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                                        .setContentTitle(getString(R.string.app_name))
                                        .setContentText(result)
                                        .setWhen(System.currentTimeMillis())
                                        .setPriority(Notification.PRIORITY_DEFAULT)
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(result));

                                Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT,result);

                                PendingIntent sharePi = PendingIntent.getActivity(ClipboardService.this,0,shareIntent,0);

                                mBuilder.addAction(R.drawable.ic_share_white_24dp,getString(R.string.share),sharePi);

                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                manager.notify(0,mBuilder.build());

    }


//    private void handleClipData(final String clipData) {
//
//        StringRequest request = new StringRequest(Request.Method.GET,
//                Constants.BING_BASE + "?Word=" + clipData + "&Samples=false",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//
//                        try {
//                            Gson gson = new Gson();
//                            BingModel model = gson.fromJson(s, BingModel.class);
//                            if (model != null) {
//                                String result = model.getWord() + "\n";
//                                if (model.getPronunciation() != null) {
//                                    BingModel.Pronunciation p = model.getPronunciation();
//                                    result = result + "\nAmE:" + p.getAmE() + "\nBrE:" + p.getBrE() + "\n";
//                                }
//
//                                for (BingModel.Definition def : model.getDefs()) {
//                                    result = result + def.getPos() + "\n" + def.getDef() + "\n";
//                                }
//
//                                result = result.substring(0, result.length() - 1);
//
//                                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ClipboardService.this)
//                                        .setSmallIcon(R.drawable.ic_small_icon)
//                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
//                                        .setContentTitle(getString(R.string.app_name))
//                                        .setContentText(result)
//                                        .setWhen(System.currentTimeMillis())
//                                        .setPriority(Notification.PRIORITY_DEFAULT)
//                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(result));
//
//                                Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
//                                shareIntent.putExtra(Intent.EXTRA_TEXT,result);
//
//                                PendingIntent sharePi = PendingIntent.getActivity(ClipboardService.this,0,shareIntent,0);
//
//                                mBuilder.addAction(R.drawable.ic_share_white_24dp,getString(R.string.share),sharePi);
//
//                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                manager.notify(0,mBuilder.build());
//
//                            }
//                        } catch (JsonSyntaxException ex) {
//                            Toast.makeText(ClipboardService.this, R.string.error,Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(ClipboardService.this, R.string.network_error,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        queue.add(request);
//
//    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null){
            manager.removePrimaryClipChangedListener(listener);
        }
    }

}
