package com.feng.gl20demo.imagemodel;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by 李超峰 on 2017/12/18.
 */

public class GLSView extends GLSurfaceView {
    TextureRender mTextureRender;

    public GLSView(Context context) {
        this(context, null);
    }

    public GLSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        mTextureRender = new TextureRender(this);
        setRenderer(mTextureRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }
}
