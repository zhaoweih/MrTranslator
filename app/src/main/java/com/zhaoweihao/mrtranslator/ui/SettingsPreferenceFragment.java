package com.zhaoweihao.mrtranslator.ui;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.zhaoweihao.mrtranslator.R;
import com.zhaoweihao.mrtranslator.service.ClipboardService;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * Created by Zhao Weihao on 2017/8/15.
 */

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_screen);

        findPreference("tap_translate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("tap_translate", false)) {
                    getActivity().startService(new Intent(getActivity(), ClipboardService.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(),ClipboardService.class));
                }
                return true;
            }
        });

        findPreference("source").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.github_source_description)));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    showError();
                }
                return true;
            }
        });

        findPreference("feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Uri uri = Uri.parse(getString(R.string.sendto));
                    Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.topic));
                    intent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                                    + getString(R.string.version));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    showError();
                }
                return true;
            }
        });

        findPreference("coffee").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setTitle(R.string.donate);
                dialog.setMessage(getString(R.string.donate_content));
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 将指定账号添加到剪切板
                        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", getString(R.string.donate_account));
                        manager.setPrimaryClip(clipData);

                        showTextCopied();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
            }
        });

        findPreference("rate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException ex){
                    showError();
                }
                return true;
            }
        });


    }

    private void showError() {
        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    private void showTextCopied() {
        Toast.makeText(getActivity(), R.string.copy_done, Toast.LENGTH_SHORT).show();
    }

}
