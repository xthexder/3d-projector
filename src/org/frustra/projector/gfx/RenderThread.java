package org.frustra.projector.gfx;

import org.frustra.projector.Projector;

/**
 * Handles frame rendering
 * 
 * @author Jacob Wirth
 *
 */
public class RenderThread implements Runnable {
	private Thread t;
	private boolean running = false;
	public static int fps = 0;
	
	public void start() {
		if (t == null) t = new Thread(this);
		running = true;
		t.start();
	}
	
	public void stop() {
		running = false;
		t.interrupt();
	}
	
	public void run() {
		int frames = 0;
		long lastTimer1 = System.currentTimeMillis();
		long lastTimer2 = System.currentTimeMillis();
		
		while (running) {
			Projector.screen.render();
			frames++;
			
			try {
				Thread.sleep(Math.max(2, 16 - (System.currentTimeMillis() - lastTimer1)));
			} catch (InterruptedException e) {
				break;
			}

			lastTimer1 = System.currentTimeMillis();
			if (lastTimer1 - lastTimer2 > 1000) {
				lastTimer2 = lastTimer1;
				fps = frames;
				if (Projector.frame != null) Projector.frame.setTitle("3D Projector V2 - " + fps + " FPS");
				frames = 0;
			}
		}
	}
}
