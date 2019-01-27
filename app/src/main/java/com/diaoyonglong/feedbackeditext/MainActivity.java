package com.diaoyonglong.feedbackeditext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.diaoyonglong.editext.FeedBackEditext;

/**
 * Created by diaoyonglong on 2019/1/27
 *
 * @desc FeedBackEditext  用法
 */

public class MainActivity extends AppCompatActivity {

    FeedBackEditext editext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editext = (FeedBackEditext) findViewById(R.id.editext);
    }
}
