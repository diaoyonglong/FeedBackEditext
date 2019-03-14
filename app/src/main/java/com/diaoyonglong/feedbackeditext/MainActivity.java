package com.diaoyonglong.feedbackeditext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.diaoyonglong.editext.FeedBackEditext;

/**
 * Created by diaoyonglong on 2019/1/27
 *
 * @desc FeedBackEditext  用法
 */

public class MainActivity extends AppCompatActivity {

    FeedBackEditext editext;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editext = (FeedBackEditext) findViewById(R.id.editext);
        button = (Button) findViewById(R.id.button);
        editext.setOnTextChangeListener(new FeedBackEditext.OnTextChangeListener() {
            @Override
            public void onTextChange(CharSequence s) {
                Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_LONG).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, editext.getContent(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
