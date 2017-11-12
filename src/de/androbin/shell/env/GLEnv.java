package de.androbin.shell.env;

import static de.androbin.opengl.util.MouseUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import de.androbin.shell.*;
import de.androbin.shell.gfx.*;
import de.androbin.shell.input.*;
import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public final class GLEnv extends AbstractEnv {
  private final GLKeyboardAdapter keyboardAdapter;
  private final GLMouseAdapter mouseAdapter;
  
  private int fps;
  
  public GLEnv( final Shell shell, final int fps ) {
    super( shell, fps );
    
    final Inputs inputs = shell.getInputs();
    
    keyboardAdapter = new GLKeyboardAdapter( inputs.keyboard );
    mouseAdapter = new GLMouseAdapter( inputs.mouse, inputs.mouseMotion, inputs.mouseWheel );
  }
  
  public static void init2D() {
    glMatrixMode( GL_PROJECTION );
    glLoadIdentity();
    
    glOrtho( 0, Display.getWidth(), 0, Display.getHeight(), 1, -1 );
    glMatrixMode( GL_MODELVIEW );
  }
  
  public static void init3D() {
    init3D( 60f, 0.125f, 1024f );
  }
  
  public static void init3D( final float fovy, final float zNear, final float zFar ) {
    glEnable( GL_DEPTH_TEST );
    glMatrixMode( GL_PROJECTION );
    glLoadIdentity();
    
    Mouse.setGrabbed( true );
    centerMouse();
    
    gluPerspective( fovy, (float) Display.getWidth() / Display.getHeight(), zNear, zFar );
    glMatrixMode( GL_MODELVIEW );
    glLoadIdentity();
  }
  
  public static void initDisplay( final String title, final DisplayMode mode,
      final boolean vsync ) {
    try {
      if ( mode == null ) {
        Display.setFullscreen( true );
      } else {
        Display.setDisplayMode( mode );
      }
      
      Display.setResizable( true );
      Display.setTitle( title );
      Display.setVSyncEnabled( vsync );
      Display.create();
    } catch ( final LWJGLException e ) {
      e.printStackTrace();
    }
  }
  
  public static void initGeneral() {
    glEnable( GL_CULL_FACE );
    glFrontFace( GL_CW );
    glCullFace( GL_BACK );
    
    glEnable( GL_TEXTURE_2D );
    glTexEnvi( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE );
    
    glClearColor( 0f, 0f, 0f, 1f );
    
    glEnable( GL_BLEND );
    glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
  }
  
  @ Override
  public void run() {
    shell.setRunning( true );
    
    final GLGraphics graphics = (GLGraphics) shell;
    graphics.initGL();
    
    long lastFrame = Sys.getTime();
    
    while ( shell.isRunning() ) {
      Display.sync( fps );
      
      if ( Display.wasResized() ) {
        glViewport( 0, 0, Display.getWidth(), Display.getHeight() );
      }
      
      keyboardAdapter.processAll();
      mouseAdapter.processAll();
      
      if ( shell.isActive() ) {
        final long thisFrame = Sys.getTime();
        final float delta = (float) ( thisFrame - lastFrame ) / Sys.getTimerResolution();
        lastFrame = thisFrame;
        
        shell.update( delta );
        shell.updateUI( delta );
        graphics.render();
      }
      
      Display.update();
      
      if ( Display.isCloseRequested() ) {
        shell.exit();
      }
    }
    
    shell.destroy();
    Display.destroy();
    
    if ( shell.isDeadly() ) {
      System.exit( 0 );
    }
  }
  
  @ Override
  public void runParallel() {
    shell.setRunning( true );
    
    final Thread updateDaemon = new Thread( () -> {
      runTimed( shell::update );
    }, "OpenGL Update Daemon" );
    updateDaemon.setDaemon( true );
    updateDaemon.start();
    
    final GLGraphics graphics = (GLGraphics) shell;
    graphics.initGL();
    
    long lastFrame = Sys.getTime();
    
    while ( shell.isRunning() ) {
      Display.sync( fps );
      
      if ( Display.wasResized() ) {
        glViewport( 0, 0, Display.getWidth(), Display.getHeight() );
      }
      
      keyboardAdapter.processAll();
      mouseAdapter.processAll();
      
      if ( shell.isActive() ) {
        final long thisFrame = Sys.getTime();
        final float delta = (float) ( thisFrame - lastFrame ) / Sys.getTimerResolution();
        lastFrame = thisFrame;
        
        shell.updateUI( delta );
        graphics.render();
      }
      
      Display.update();
      
      if ( Display.isCloseRequested() ) {
        shell.exit();
      }
    }
    
    updateDaemon.interrupt();
    
    try {
      updateDaemon.join();
    } catch ( final InterruptedException ignore ) {
    }
    
    shell.destroy();
    Display.destroy();
    
    if ( shell.isDeadly() ) {
      System.exit( 0 );
    }
  }
  
  @ Override
  public void setFPS( final int fps ) {
    this.fps = fps;
  }
}