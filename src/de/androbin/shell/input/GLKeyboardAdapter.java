package de.androbin.shell.input;

import static org.lwjgl.input.Keyboard.*;

public final class GLKeyboardAdapter {
  private final KeyInput input;
  
  public GLKeyboardAdapter( final KeyInput input ) {
    this.input = input;
  }
  
  public void processAll() {
    while ( next() ) {
      processNext();
    }
  }
  
  private void processNext() {
    if ( input == null ) {
      return;
    }
    
    final int key = getEventKey();
    final boolean validKey = key != KEY_NONE;
    
    if ( getEventKeyState() ) {
      if ( validKey ) {
        input.keyPressed( key );
      }
      
      final char character = getEventCharacter();
      final boolean validChar = character != CHAR_NONE;
      
      if ( validChar ) {
        input.keyTyped( character );
      }
    } else {
      if ( validKey ) {
        input.keyReleased( key );
      }
    }
  }
}