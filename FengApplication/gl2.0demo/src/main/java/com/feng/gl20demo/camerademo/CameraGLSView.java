package com.feng.gl20demo.camerademo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李超峰 on 2017/12/18.
 */

public class CameraGLSView extends GLSurfaceView implements GLSurfaceView.Renderer {
    CameraTexture mCameraTexture;
    //    Camera mCamera;
    KitkatCamera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public CameraGLSView(Context context) {
        this(context, null);
    }

    public CameraGLSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    SurfaceTexture surfaceTexture;
    int textureID;

    private void init() {
        setEGLContextClientVersion(2);
        mCameraTexture = new CameraTexture(getContext());
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCamera = new KitkatCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraTexture.onSurfaceCreated(gl, config);
        surfaceTexture = new SurfaceTexture(textureID = createTextureID());
        mCameraTexture = new CameraTexture(getContext());
        mCameraTexture.setTextureID(textureID);
        Log.d("haha", "textureID = " + textureID);
        mCamera.open(mCameraId);
        mCamera.setPreviewTexture(surfaceTexture);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.d("haha", "onFrameAvailable");
                requestRender();
            }
        });
        mCamera.preview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraTexture.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        mCameraTexture.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.close();
    }

    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
