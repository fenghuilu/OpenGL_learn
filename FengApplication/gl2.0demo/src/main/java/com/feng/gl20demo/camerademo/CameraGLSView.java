package com.feng.gl20demo.camerademo;

import android.content.Context;
import android.graphics.Point;
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

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCameraTexture = new CameraTexture(getContext());
        mCamera = new KitkatCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraTexture.onSurfaceCreated(gl, config);
        Log.d("feng", "onSurfaceCreated");
        mCamera.open(mCameraId);
        Point point = mCamera.getPreviewSize();
        mCameraTexture.setDataSize(point.x, point.y);
        mCamera.setPreviewTexture(mCameraTexture.getSurfaceTexture());
        mCameraTexture.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
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
        Log.d("haha", "onDrawFrame");
        mCameraTexture.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.close();
    }

}
