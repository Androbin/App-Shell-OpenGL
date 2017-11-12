package de.androbin.shell.gfx;

public interface GLGraphics {
  default void initGL() {
  }
  
  void render();
}