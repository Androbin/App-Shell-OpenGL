package de.androbin.shell.input;

import de.androbin.opengl.util.*;
import org.lwjgl.opengl.*;

public final class MouseTrack implements MouseMotionInput {
  private int x;
  private int y;
  
  public MouseTrack() {
    x = Display.getWidth() >> 1;
    y = Display.getHeight() >> 1;
  }
  
  public int getDX() {
    return x - ( Display.getWidth() >> 1 );
  }
  
  public int getDY() {
    return y - ( Display.getHeight() >> 1 );
  }
  
  @ Override
  public void mouseDragged( final int x, final int y, final int button ) {
    this.x = x;
    this.y = y;
  }
  
  @ Override
  public void mouseMoved( final int x, final int y ) {
    this.x = x;
    this.y = y;
  }
  
  public void reset() {
    MouseUtil.centerMouse();
    
    x = Display.getWidth() >> 1;
    y = Display.getHeight() >> 1;
  }
}