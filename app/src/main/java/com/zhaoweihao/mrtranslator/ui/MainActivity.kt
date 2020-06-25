package com.zhaoweihao.mrtranslator.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.zhaoweihao.mrtranslator.R
import com.zhaoweihao.mrtranslator.constant.Constant
import com.zhaoweihao.mrtranslator.gson.Translate
import com.zhaoweihao.mrtranslator.litepal.Collect
import com.zhaoweihao.mrtranslator.utils.HttpUtil.sendOkHttpRequest
import com.zhaoweihao.mrtranslator.utils.Utility.handleTranslateResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

/**
 * Created by Zhaoweihao on 17/7/6.
 * 如果对我的项目有任何疑问可以给我发邮件或者提issues
 * Email:zhaoweihaochn@gmail.com
 * 如果觉得我的项目写得好可以给我star和fork
 * 谢谢！
 */
class MainActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var editText: EditText? = null
    private var button: FloatingActionButton? = null
    private var translationLayout: LinearLayout? = null
    private var phoneticText: TextView? = null
    private var explainsLayout: LinearLayout? = null
    private var queryText: TextView? = null
    private var webLayout: LinearLayout? = null
    private var progressBar: ProgressBar? = null
    private var clearView: ImageView? = null
    private var copyImage: ImageView? = null
    private var shareImage: ImageView? = null
    private var translateTitle: TextView? = null
    private var explainsTitle: TextView? = null
    private var webTitle: TextView? = null
    private var mixLayout: LinearLayout? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var collectImage: ImageView? = null
    private var collectDoneImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        initViews()

        //navigationView点击事件
        navigationView!!.setCheckedItem(R.id.nav_collect)
        navigationView!!.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_collect -> {
                    intent = Intent(this@MainActivity, CollectionActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_about -> {
                    intent = Intent(this@MainActivity, SettingsPreferenceActivity::class.java)
                    startActivity(intent)
                }
            }
            mDrawerLayout!!.closeDrawers()
            true
        }

        //删除按钮点击事件
        clearView!!.setOnClickListener { editText!!.setText("") }
        //editText输入监控事件，如果有文字输入显示删除按钮和翻译按钮，如果为空则隐藏按钮
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editText!!.editableText.toString().length != 0) {
                    clearView!!.visibility = View.VISIBLE
                    button!!.visibility = View.VISIBLE
                } else {
                    clearView!!.visibility = View.INVISIBLE
                    button!!.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        //翻译按钮监控事件
        button!!.setOnClickListener {
            //如果编辑框输入文字为空则snackbar提示输入文本为空
            if (editText!!.text.toString().isEmpty()) {
                Snackbar.make(button!!, R.string.input_empty, Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                //显示progressbar
                progressBar!!.visibility = View.VISIBLE
                //收起输入法
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(button!!.windowToken, 0)
                }
                //获取输入框的文字内容
                val word = editText!!.text.toString()
                //获取有道api初始url
                val url = Constant.YOUDAO_URL
                //okhttp发送网络请求,url+word是初始url拼接word组成api
                sendOkHttpRequest(url + word, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        //网络请求失败，开启子进程，更新页面
                        runOnUiThread {
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(this@MainActivity, R.string.translate_fail, Toast.LENGTH_SHORT).show()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        //网络请求成功，获取json数据
                        val responseData = response.body().string()
                        //利用gson处理json数据，转化为对象
                        try {
                            val translate = handleTranslateResponse(responseData)
                            //开启子进程更新界面
                            runOnUiThread {
                                //json数据中errorcode为0表示获取数据成功,20代表文本过长,其他就是出现错误，具体可查有道api使用事项
                                if (translate.errorCode == 0) {
                                    //将数据显示在界面上
                                    showTranslateInfo(translate)
                                } else if (translate.errorCode == 20) {
                                    progressBar!!.visibility = View.GONE
                                    Toast.makeText(this@MainActivity, R.string.translate_overlong, Toast.LENGTH_SHORT).show()
                                } else {
                                    progressBar!!.visibility = View.GONE
                                    Toast.makeText(this@MainActivity, R.string.translate_fail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            progressBar!!.visibility = View.INVISIBLE
                        }
                    }
                })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mDrawerLayout!!.openDrawer(GravityCompat.START)
            else -> {
            }
        }
        return true
    }

    private fun showTranslateInfo(translate: Translate) {
        //先清空layout里面的东西
        translationLayout!!.removeAllViews()
        explainsLayout!!.removeAllViews()
        webLayout!!.removeAllViews()
        //收藏成功按钮隐藏
        collectDoneImage!!.visibility = View.INVISIBLE
        //设置title
        translateTitle!!.setText(R.string.translate_title)
        explainsTitle!!.setText(R.string.explains_title)
        webTitle!!.setText(R.string.web_title)
        //将请求回来的数据显示在textview上
        val translationList = translate.translation
        if (translationList != null) {
            for (element in translationList) {
                val view = LayoutInflater.from(this).inflate(R.layout.translation_item, translationLayout, false)
                val translateText = view.findViewById(R.id.translation_text) as TextView
                translateText.text = element
                translateText.setTextColor(resources.getColor(R.color.white))
                translateText.textSize = 25f
                translationLayout!!.addView(view)
            }
        }

        queryText!!.text = translate.query
        val basic = translate.basic
        if (basic == null) {
            phoneticText!!.visibility = View.INVISIBLE
            explainsLayout!!.visibility = View.INVISIBLE
        } else {
            phoneticText!!.text = "[" + translate.basic!!.phonetic + "]"
            val explainsList = basic.explains
            if (explainsList != null) {
                for (element in explainsList) {
                    val view = LayoutInflater.from(this).inflate(R.layout.explains_item, explainsLayout, false)
                    val explainsText = view.findViewById(R.id.expalins_text) as TextView
                    explainsText.text = element
                    explainsLayout!!.addView(view)
                    phoneticText!!.visibility = View.VISIBLE
                    explainsLayout!!.visibility = View.VISIBLE
                }
            }
        }
        if (translate.web == null) {
            webLayout!!.visibility = View.INVISIBLE
        } else {
            for (i in translate.web!!.indices) {
                val view = LayoutInflater.from(this).inflate(R.layout.web_item, webLayout, false)
                val keyText = view.findViewById(R.id.key_text) as TextView
                val valueText = view.findViewById(R.id.value_text) as TextView
                keyText.text = translate.web!![i].key
                val values = getFinalValue(translate.web!![i].value)
                valueText.text = values
                webLayout!!.addView(view)
                webLayout!!.visibility = View.VISIBLE
            }
        }
        //显示好后隐藏progressbar
        progressBar!!.visibility = View.GONE
        //复制按钮监听事件
        copyImage!!.setOnClickListener {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", translate.translation!![0])
            manager.primaryClip = clipData
            Snackbar.make(button!!, R.string.copy_success, Snackbar.LENGTH_SHORT)
                    .show()
        }
        //分享按钮监听事件
        shareImage!!.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND).type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, translate.translation!![0])
            startActivity(Intent.createChooser(intent, getString(R.string.share_choice)))
        }
        //收藏按钮监听事件
        collectImage!!.setOnClickListener { //利用litepal将数据存入数据库
            val collect = Collect()
            collect.chinese = translate.translation!![0]
            collect.english = translate.query
            collect.save()
            Toast.makeText(this@MainActivity, R.string.collect_success, Toast.LENGTH_SHORT).show()
            //显示收藏成功按钮，隐藏收藏按钮
            collectDoneImage!!.visibility = View.VISIBLE
            collectImage!!.visibility = View.INVISIBLE
        }
        //收藏成功按钮监听事件
        collectDoneImage!!.setOnClickListener { //从数据库删除该数据
            DataSupport.deleteAll(Collect::class.java, "english=?", translate.query)
            Toast.makeText(this@MainActivity, R.string.deletesuccess, Toast.LENGTH_SHORT).show()
            collectDoneImage!!.visibility = View.INVISIBLE
            collectImage!!.visibility = View.VISIBLE
        }
        copyImage!!.visibility = View.VISIBLE
        shareImage!!.visibility = View.VISIBLE
        mixLayout!!.visibility = View.VISIBLE
        collectImage!!.visibility = View.VISIBLE
    }

    private fun getFinalValue(value: Array<String>?): String {
        var finalValue = ""
        for (i in value!!.indices) {
            finalValue = if (i == value.size - 1) {
                finalValue + value[i]
            } else {
                finalValue + value[i] + ","
            }
        }
        return finalValue
    }

    //获取控件实例
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        editText = findViewById(R.id.word_input) as EditText
        button = findViewById(R.id.translate_btn) as FloatingActionButton
        button!!.visibility = View.INVISIBLE
        translationLayout = findViewById(R.id.translation_layout) as LinearLayout
        phoneticText = findViewById(R.id.phonetic_text) as TextView
        explainsLayout = findViewById(R.id.explains_layout) as LinearLayout
        queryText = findViewById(R.id.query_text) as TextView
        webLayout = findViewById(R.id.web_layout) as LinearLayout
        progressBar = findViewById(R.id.progress_bar) as ProgressBar
        clearView = findViewById(R.id.iv_clear) as ImageView
        clearView!!.visibility = View.INVISIBLE
        copyImage = findViewById(R.id.copy) as ImageView
        copyImage!!.visibility = View.INVISIBLE
        shareImage = findViewById(R.id.share) as ImageView
        shareImage!!.visibility = View.INVISIBLE
        translateTitle = findViewById(R.id.translate_title) as TextView
        explainsTitle = findViewById(R.id.explains_title) as TextView
        webTitle = findViewById(R.id.web_title) as TextView
        mixLayout = findViewById(R.id.mixLayout) as LinearLayout
        mixLayout!!.visibility = View.INVISIBLE
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        navigationView = findViewById(R.id.nav_view) as NavigationView
        collectImage = findViewById(R.id.collect) as ImageView
        collectImage!!.visibility = View.INVISIBLE
        collectDoneImage = findViewById(R.id.collect_done) as ImageView
        collectDoneImage!!.visibility = View.INVISIBLE
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }
}