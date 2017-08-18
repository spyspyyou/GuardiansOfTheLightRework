package mobile.data.usage.spyspyyou.openglrendering.ui.activities;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        glSurfaceView = new MyGLS(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new Renderer());

        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


    }

    public static class MyGLS extends GLSurfaceView{
        public MyGLS(Context context) {
            super(context);
        }
    }

    public static class Renderer implements GLSurfaceView.Renderer {

        private Triangle triangle;
        private int matrixPosHandle;
        private float[] viewMatrix = new float[16];
        private float[] projMatrix = new float[16];
        private float[] theMatrix = new float[16];

        private static final String vertexShaderCode =
                "uniform mat4 uMVPMatrix;\n" +
                "attribute vec4 vPosition;\n" +

                 "void main() {\n" +
                 "  gl_Position = uMVPMatrix * vPosition;\n" +
                 "}";

        private static final String fragmentShaderCode =
                "precision mediump float;\n" +
                "uniform vec4 vColor;\n" +

                "void main() {\n" +
                "  gl_FragColor = vColor;\n" +
                "}";

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //load any and all Resources;
            GLES20.glClearColor(0.2f, 0.4f, 0.8f, 1.0f);
            triangle = new Triangle();

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                    vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                    fragmentShaderCode);
            matrixPosHandle = GLES20.glGetUniformLocation(vertexShader, "uMVPMatrix");
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1, 0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            float ratio = (float) width/height;

            Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            Matrix.multiplyMM(theMatrix, 0, projMatrix, 0, viewMatrix, 0);

            GLES20.glUniformMatrix4fv(matrixPosHandle, 1, false, theMatrix, 0);
            triangle.draw();
        }

        public static int loadShader(int type, String shaderCode) {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;

        }

        public static class Triangle {

            private FloatBuffer vertexBuffer;

            // number of coordinates per vertex in this array
            static final int COORDS_PER_VERTEX = 3;
            static float triangleCoords[] = {   // in counterclockwise order:
                    0.0f, 0.622008459f, 0.0f, // top
                    -0.5f, -0.311004243f, 0.0f, // bottom left
                    0.5f, -0.311004243f, 0.0f  // bottom right
            };

            // Set color with red, green, blue and alpha (opacity) values
            float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};
            private final int mProgram;

            public Triangle() {
                // initialize vertex byte buffer for shape coordinates
                ByteBuffer bb = ByteBuffer.allocateDirect(
                        // (number of coordinate values * 4 bytes per float)
                        triangleCoords.length * 4);
                // use the device hardware's native byte order
                bb.order(ByteOrder.nativeOrder());

                // create a floating point buffer from the ByteBuffer
                vertexBuffer = bb.asFloatBuffer();
                // add the coordinates to the FloatBuffer
                vertexBuffer.put(triangleCoords);
                // set the buffer to read the first coordinate
                vertexBuffer.position(0);

                int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                        vertexShaderCode);
                int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                        fragmentShaderCode);

                // create empty OpenGL ES Program
                mProgram = GLES20.glCreateProgram();

                // add the vertex shader to program
                GLES20.glAttachShader(mProgram, vertexShader);

                // add the fragment shader to program
                GLES20.glAttachShader(mProgram, fragmentShader);

                // creates OpenGL ES program executables
                GLES20.glLinkProgram(mProgram);
            }

            private int mPositionHandle;
            private int mColorHandle;

            private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
            private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

            public void draw() {
                // Add program to OpenGL ES environment
                GLES20.glUseProgram(mProgram);

                // get handle to vertex shader's vPosition member
                mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

                // Enable a handle to the triangle vertices
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                // Prepare the triangle coordinate data
                GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                        GLES20.GL_FLOAT, false,
                        vertexStride, vertexBuffer);

                // get handle to fragment shader's vColor member
                mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

                // Set color for drawing the triangle
                GLES20.glUniform4fv(mColorHandle, 1, color, 0);

                // Draw the triangle
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

                // Disable vertex array
                GLES20.glDisableVertexAttribArray(mPositionHandle);
            }
        }
    }
}
