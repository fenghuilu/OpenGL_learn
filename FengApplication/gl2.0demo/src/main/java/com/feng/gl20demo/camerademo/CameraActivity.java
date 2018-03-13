package com.feng.gl20demo.camerademo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by 李超峰 on 2018/1/10.
 */

public class CameraActivity extends Activity {
    CameraGLSView mGLSView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mGLSView = new CameraGLSView(this));
        Log.d("haha","onCreate");
    }
}
