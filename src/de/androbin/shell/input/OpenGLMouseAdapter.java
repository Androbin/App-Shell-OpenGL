package de.androbin.shell.input;

import static org.lwjgl.input.Mouse.*;

public final class OpenGLMouseAdapter {
  private final MouseInput mouseInput;
  private final MouseMotionInput mouseMotionInput;
  private final MouseWheelInput mouseWheelInput;
  
  private float mclicks;
  
  public OpenGLMouseAdapter( final MouseInput mouseInput,
      final MouseMotionInput mouseMotionInput,
      final MouseWheelInput mouseWheelInput ) {
    this.mouseInput = mouseInput;
    this.mouseMotionInput = mouseMotionInput;
    this.mouseWheelInput = mouseWheelInput;
  }
  
  public void processAll() {
    while ( next() ) {
      processNext();
    }
  }
  
  private void processNext() {
    final int x = getEventX();
    final int y = getEventY();
    
    final int dx = getEventDX();
    final int dy = getEventDY();
    
    final boolean moved = ( dx | dy ) != 0;
    
    final int button = getEventButton();
    final boolean buttonState = getEventButtonState();
    
    if ( mouseInput != null && button != -1 ) {
      if ( buttonState ) {
        mouseInput.mousePressed( x, y, button );
      } else {
        mouseInput.mouseReleased( x, y, button );
        
        if ( !moved ) {
          mouseInput.mouseClicked( x, y, button );
        }
      }
    }
    
    if ( mouseMotionInput != null && moved ) {
      if ( button == -1 ) {
        mouseMotionInput.mouseMoved( x, y );
      } else if ( buttonState ) {
        mouseMotionInput.mouseDragged( x, y, button );
      }
    }
    
    if ( mouseWheelInput != null ) {
      final float fclicks = -getEventDWheel() / 120f;
      mclicks += fclicks;
      final int iclicks = (int) mclicks;
      mclicks %= 1f;
      
      mouseWheelInput.mouseWheelMoved( x, y, iclicks, fclicks );
    }
  }
}