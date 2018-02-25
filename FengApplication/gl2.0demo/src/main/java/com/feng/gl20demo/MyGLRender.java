package com.feng.gl20demo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.feng.gl20demo.viewmodle.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李超峰 on 2017/9/19.
 */

public class MyGLRender implements GLSurfaceView.Renderer {
    Model mModel;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        mModel = new Triangle();
//        mModel = new Squre();
//        mModel = new Circular();
        mModel = new Cube();
//        mModel = new TriangleColor();
        mModel.onSurfaceCreated(gl10, eglConfig);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mModel.onSurfaceChanged(gl10, width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl10) {
        mModel.onDrawFrame(gl10);
    }
}
