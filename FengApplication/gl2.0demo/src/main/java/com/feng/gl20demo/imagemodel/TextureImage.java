package com.feng.gl20demo.imagemodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.feng.gl20demo.Model;
import com.feng.gl20demo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 李超峰 on 2017/12/13.
 */

public class TextureImage extends Model {
    String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec2 vCoordinate;" +
                    "attribute vec2 aCoordinate;" +
                    "void main() {" +
                    "     gl_Position = vMatrix * vPosition;" +
                    "     vCoordinate = aCoordinate;" +
                    " }";
    String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D vTexture;" +
                    "varying vec2 vCoordinate;" +
                    "uniform vec3 vChangeColor;"+

            "void modifyColor(vec4 color){"+
                "color.r=max(min(color.r,1.0),0.0);"+
                "color.g=max(min(color.g,1.0),0.0);"+
                "color.b=max(min(color.b,1.0),0.0);"+
                "color.a=max(min(color.a,1.0),0.0);"+
            "}"+

             "void main() {" +
               "     vec4 nColor=texture2D(vTexture,vCoordinate);"+
               "     float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;"+
               "     gl_FragColor=vec4(c,c,c,nColor.a);"+
//               "     vec4 deltaColor=nColor+vec4(vChangeColor,0.0);"+
//               "     modifyColor(deltaColor);"+
//               "     gl_FragColor = deltaColor;"+
//               "     gl_FragColor = texture2D(vTexture,vCoordinate);" +
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

    public TextureImage(Context context) {
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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        mProgram = initProgram(vertexShaderCode, fragmentShaderCode);
        //获取变换矩阵vMatrix句柄
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取顶点着色器的vPosition句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");
        mColorChangeHandle = GLES20.glGetUniformLocation(mProgram, "vChangeColor");
        mBitmap = ((BitmapDrawable) mContext.getDrawable(R.drawable.haha)).getBitmap();
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
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0,
                        -sWidthHeight * sWH, sWidthHeight * sWH,
                        -1, 1,
                        3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0,
                        -sWidthHeight / sWH, sWidthHeight / sWH,
                        -1, 1,
                        3, 5);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0,
                        -1, 1,
                        -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH,
                        3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0,
                        -1, 1,
                        -sWidthHeight / sWH, sWidthHeight / sWH,
                        3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, 5.0f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0,
                mProjectionMatrix, 0,
                mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //使用已经初始化好的mProgram
        GLES20.glUseProgram(mProgram);
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        //启用三角形定顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mCoordinateHandle);
        GLES20.glUniform3fv(mColorChangeHandle,1,new float[]{0.299f,0.587f,0.114f},0);
        GLES20.glUniform1i(mTextureHandle, 0);
        mTextureID = creatTexture(mBitmap);

        //准备顶点坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 2,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(mCoordinateHandle, 2,
                GLES20.GL_FLOAT, false,
                0, sCoordBuffer);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //禁止顶点数组的句柄
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private int creatTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
