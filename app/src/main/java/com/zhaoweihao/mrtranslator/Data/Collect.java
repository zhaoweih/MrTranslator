package com.zhaoweihao.mrtranslator.Data;

import org.litepal.crud.DataSupport;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class Collect extends DataSupport{

    private int id;

    private String chinese;

    private String english;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
