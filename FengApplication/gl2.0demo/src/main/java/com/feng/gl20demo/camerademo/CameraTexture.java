package com.feng.gl20demo.camerademo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.feng.gl20demo.Model;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李超峰 on 2017/12/13.
 */

public class CameraTexture extends Model {
    static Boolean DEBUG = true;
    static String TAG = CameraTexture.class.getSimpleName();
    String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec2 vCoordinate;" +
                    "attribute vec2 aCoordinate;" +
                    "void main() {" +
                    "     gl_Position = vMatrix * vPosition;" +
                    "     vCoordinate = aCoordinate;" +
                    "     vCoordinate = (vCoordMatrix*vec4(vCoord,0,1)).xy;" +
                    " }";
    String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require" +
                    "precision mediump float;" +
//                    "uniform sampler2D vTexture;" +
                    "uniform samplerExternalOES vTexture;" +
                    "varying vec2 vCoordinate;" +
                    "uniform vec3 vChangeColor;" +

                    "void modifyColor(vec4 color){" +
                    "color.r=max(min(color.r,1.0),0.0);" +
                    "color.g=max(min(color.g,1.0),0.0);" +
                    "color.b=max(min(color.b,1.0),0.0);" +
                    "color.a=max(min(color.a,1.0),0.0);" +
                    "}" +

                    "void main() {" +
//               "     vec4 nColor=texture2D(vTexture,vCoordinate);"+
//               "     float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;"+
//               "     gl_FragColor=vec4(c,c,c,nColor.a);"+
//               "     vec4 deltaColor=nColor+vec4(vChangeColor,0.0);"+
//               "     modifyColor(deltaColor);"+
//               "     gl_FragColor = deltaColor;"+
                    "     gl_FragColor = texture2D(vTexture,vCoordinate);" +
                    " }";

    float triangleCoords[] = {
            -1.0f, 1.0f,  // 左上角
            -1.0f, -1.0f,  // 左下角
            1.0f, 1.0f,  // 右上角
            1.0f, -1.0f,   // 右下角
    };
    float sCoord[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };
    FloatBuffer vertexBuffer;
    FloatBuffer sCoordBuffer;
    int mProgram;
    int mPositionHandle;
    int mCoordinateHandle;
    int mMatrixHandle;
    int mTextureHandle;
    int mTextureID;
    int textureType = 0;
    int mColorChangeHandle;
    /**
     * mMVPMatrix是"Model View Projection Matrix"的缩写
     */
    private final float[] mMVPMatrix = new float[16];
    /**
     * 定义投影矩阵变量
     */
    private final float[] mProjectionMatrix = new float[16];
    /**
     * 定义相机视图矩阵变量
     */
    private final float[] mViewMatrix = new float[16];
    private Bitmap mBitmap;
    Context mContext;

    public CameraTexture(Context context) {
        mContext = context;
        //申请底层空间
        //将坐标数据转换为FloatBuffer，才能传入OpenGL ES程序
        vertexBuffer = ByteBuffer
                .allocateDirect(triangleCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        sCoordBuffer = ByteBuffer
                .allocateDirect(sCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        sCoordBuffer.put(sCoord);
        sCoordBuffer.position(0);
    }

    SurfaceTexture surfaceTexture;
    int textureID;

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
//        mProgram = initProgram(vertexShaderCode, fragmentShaderCode);
        createProgram("shader/oes_base_vertex.sh", "shader/oes_base_fragment.sh");
        //获取变换矩阵vMatrix句柄
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix");
        //获取顶点着色器的vPosition句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");
        mColorChangeHandle = GLES20.glGetUniformLocation(mProgram, "vChangeColor");
//        surfaceTexture = new SurfaceTexture(textureID = createTextureID());
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
        surfaceTexture.getTransformMatrix(mMVPMatrix);
    }

    public int initProgram(String vertexShaderCode, String fragmentShaderCode) {
        //加载shader
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        //创建一个空的OpenGLES程序
        int program = GLES20.glCreateProgram();
        //将顶点着色器加入程序
        GLES20.glAttachShader(program, vertexShader);
        //将片源着色器加入程序
        GLES20.glAttachShader(program, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(program);
        return program;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //使用已经初始化好的mProgram
        GLES20.glUseProgram(mProgram);
        onBindTexture();
        onDraw();
    }

    protected void onDraw() {
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        //启用三角形定顶点的句柄
//        GLES20.glUniform3fv(mColorChangeHandle, 1, new float[]{0.299f, 0.587f, 0.114f}, 0);
        //准备顶点坐标数据
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, sCoordBuffer);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordinateHandle);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture() {
        Log.d("haha", "textureID = " + textureID);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        GLES20.glUniform1i(mTextureHandle, textureType);
    }

    //    private int createTextureID() {
//        int[] texture = new int[1];
//        GLES20.glGenTextures(1, texture, 0);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
//        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//        return texture[0];
//    }
    protected final void createProgramByAssetsFile(String vertex, String fragment) {
        createProgram(uRes(mContext.getResources(), vertex), uRes(mContext.getResources(), fragment));
    }

    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = mRes.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }

    protected final void createProgram(String vertex, String fragment) {
        mProgram = uCreateGlProgram(vertex, fragment);
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource) {
        int vertex = uLoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = uLoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                glError(1, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    //加载shader
    public static int uLoadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                glError(1, "Could not compile shader:" + shaderType);
                glError(1, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }
}
