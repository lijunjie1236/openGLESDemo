package com.example.openglesdemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.openglesdemo.util.ShaderHelper;
import com.example.openglesdemo.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private Context context;

    private  FloatBuffer vertexData;
    private int programId;
    private static final String A_POSITION="a_Position";
    private static final String U_COLOR="u_Color";
    private int uColorLocation;
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };
        //内存从java堆复制到本地堆
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //清空屏幕用的颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
       //链接程序
        programId = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        //使用程序
        GLES20.glUseProgram(programId);
        //获取一个Uniform的位置
        uColorLocation=GLES20.glGetUniformLocation(programId,U_COLOR);
        //获取属性的位置
        aPositionLocation=GLES20.glGetAttribLocation(programId,A_POSITION);
        //从缓存区vertextData找到a_Position的数据，关联属性与顶点数据的数组
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GLES20.GL_FLOAT,false,0,vertexData);
        //启用能顶点数组
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        //设置视口
        GLES20.glViewport(0, 0, w, h);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //檫除屏幕上对底所有颜色，并且之前glClearColor()调用定义的颜色填充整个屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //更新着色器代码中的u_color的值
        GLES20.glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        //当采用顶点数组方式,顶点数组中的坐标数据和指定的模式，进行绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);
        //画线
        GLES20.glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES,6,2);
        //画点
        GLES20.glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS,8,1);
        GLES20.glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS,9,1);

    }
}
