package org.frustra.projector;

import org.frustra.projector.gfx.RenderThread;
import org.frustra.projector.linear.Vector3;

/**
 * Handles fire particle calculations
 * 
 * @author Jacob Wirth
 *
 */
public class EngineThread implements Runnable {
	private Thread t;
	private boolean running = false;
	
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
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis();
		long lastTimer2 = System.currentTimeMillis();
		
		while (running) {
			tick();
			ticks++;
			
			try {
				Thread.sleep(Math.max(2, 16 - (System.currentTimeMillis() - lastTimer1)));
			} catch (InterruptedException e) {
				break;
			}
			
			lastTimer1 = System.currentTimeMillis();
			if (lastTimer1 - lastTimer2 > 1000) {
				lastTimer2 = lastTimer1;
				System.out.println(ticks + " ticks, " + RenderThread.fps + " fps");
				ticks = 0;
			}
		}
	}
	
	/**
	 * Called 60 times per second
	 */
	public void tick() {
		double pitch = Projector.input.pitch;
		double yaw = Projector.input.yaw;
		/*
		 * Add 10 particles per tick, originating from the camera (600 particles per second)
		 */
		if (Projector.input.leftClick) {
			Vector3 forward = new Vector3(Math.sin(yaw) * Math.cos(pitch), Math.sin(pitch), -Math.cos(yaw) * Math.cos(pitch));
			for (int i = 0; i < 10; i++) {
				Projector.particles.add(new FireParticle(new Vector3(-Projector.input.tx, -Projector.input.ty - 1, -Projector.input.tz), forward));
			}
		}
		/*
		 * Iterate through burning locations and emit particles.
		 * - Don't use fires inside objects, and have a random chance of burning out.
		 * - Only emit a particle half of the time.
		 */
		for (int j = 0; j < Projector.fires.size(); j++) { // Iterate through burning locations and emit particles
			Vector3 loc2 = Projector.fires.get(j);
			if (Math.random() < 0.005 || Math.pow(loc2.x - 15, 2) + Math.pow(loc2.y - 55, 2) + Math.pow(loc2.z - 15, 2) < 100 || (loc2.x > 5 && loc2.y > 5 && loc2.z > 5 && loc2.x < 25 && loc2.y < 25 && loc2.z < 25)) {
				Projector.fires.remove(j);
			} else if (Math.random() < 0.5) {
				Projector.particles.add(new FireParticle(loc2, new Vector3(), 200, 0.01));
			}
		}
		/*
		 * Call individual tick() functions for each existing particle.
		 * - Synchronize with the rendering so that particles aren't moved mid-frame.
		 */
		synchronized (Projector.screen) {
			for (int i = 0; i < Projector.particles.size(); i++) {
				Projector.particles.get(i).tick();
			}
		}
	}
}
