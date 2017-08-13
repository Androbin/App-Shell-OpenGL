package de.androbin.shell.gfx;

public interface OpenGLGraphics {
  default void initGL() {
  }
  
  void render();
}