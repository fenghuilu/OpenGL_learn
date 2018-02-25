package com.feng.gl20demo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.feng.gl20demo.imagemodel.GLSView;

public class MainActivity extends AppCompatActivity {
    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        glSurfaceView = new MyGLSurfaceView(this);
//        glSurfaceView.setRenderer(new MyGLRender());
//        setContentView(glSurfaceView);
        glSurfaceView = new GLSView(this);
        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}
