package com.zhaoweihao.mrtranslator.ui

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.widget.Toast
import com.zhaoweihao.mrtranslator.R
import com.zhaoweihao.mrtranslator.service.ClipboardService

/**
 * Created by Zhao Weihao on 2017/8/15.
 */
class SettingsPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)
        findPreference("tap_translate").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("tap_translate", false)) {
                activity.startService(Intent(activity, ClipboardService::class.java))
            } else {
                activity.stopService(Intent(activity, ClipboardService::class.java))
            }
            true
        }
        findPreference("source").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.github_source_description))
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                showError()
            }
            true
        }
        findPreference("feedback").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                val uri = Uri.parse(getString(R.string.sendto))
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.topic))
                intent.putExtra(Intent.EXTRA_TEXT,
                        """
                            ${getString(R.string.sdk_version)}${Build.VERSION.RELEASE}
                            ${getString(R.string.version)}
                            """.trimIndent())
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                showError()
            }
            true
        }
        findPreference("coffee").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = AlertDialog.Builder(activity).create()
            dialog.setTitle(R.string.donate)
            dialog.setMessage(getString(R.string.donate_content))
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK)) { dialogInterface, i -> // 将指定账号添加到剪切板
                val manager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", getString(R.string.donate_account))
                manager.primaryClip = clipData
                showTextCopied()
            }
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialogInterface, i -> dialog.dismiss() }
            dialog.show()
            true
        }
        findPreference("rate").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                val uri = Uri.parse("market://details?id=" + activity.packageName)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                showError()
            }
            true
        }
    }

    private fun showError() {
        Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
    }

    private fun showTextCopied() {
        Toast.makeText(activity, R.string.copy_done, Toast.LENGTH_SHORT).show()
    }
}