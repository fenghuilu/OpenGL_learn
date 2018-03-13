package com.feng.camerademo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by 李超峰 on 2017/12/18.
 */

public class GLSView extends GLSurfaceView {
    TextureRender mTextureRender;
    Camera mCamera;
    int mCameraId;
    SurfaceTexture mSurfaceTexture;

    public GLSView(Context context) {
        this(context, null);
    }

    public GLSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = Camera.open(mCameraId);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("orientation", "portrait");
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewSize(1280, 720);
        mCamera.setDisplayOrientation(90);
//        setCameraDisplayOrientation(mActivity, mCameraId, mCamera);
        mCamera.setParameters(parameters);
        mTextureRender = new TextureRender();
        mTextureRender.init(this, mCamera);
        setRenderer(mTextureRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
