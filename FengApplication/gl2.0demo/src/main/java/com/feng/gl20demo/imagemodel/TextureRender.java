package com.feng.gl20demo.imagemodel;

import android.opengl.GLSurfaceView;
import android.view.View;

import com.feng.gl20demo.Model;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李超峰 on 2017/12/18.
 */

public class TextureRender implements GLSurfaceView.Renderer {
    Model mModel;
    View mGLSView;

    public TextureRender(View view) {
        mGLSView = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mModel = new TextureImage(mGLSView.getContext());
        mModel.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mModel.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mModel.onDrawFrame(gl);
    }
}
