package com.example.realliferpg;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class GLRendererEx implements Renderer{

	public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
		// TODO Auto-generated method stub
		gl.glClearColor(.8f, 0f, .2f, 1f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		
	}
}
