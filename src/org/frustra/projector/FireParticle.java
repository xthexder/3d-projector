package org.frustra.projector;

import java.awt.Color;

import org.frustra.projector.linear.Vector3;

/**
 * Stores existing fire particle information and handles individual particle physics.
 * 
 * @author Jacob Wirth
 *
 */
public class FireParticle {
	Vector3 loc;
	Vector3 dir;
	int ttl;
	double spread;

	public FireParticle(Vector3 loc, Vector3 dir) {
		this(loc, dir, (int) (Math.random() * 200), 0.4);
	}

	public FireParticle(Vector3 loc, Vector3 dir, int ttl, double spread) {
		// Randomize particle direction slightly to make it look good.
		double theta = Math.random() * Math.PI * 2;
		double a = Math.random() * 0.2;
		double b = Math.random() * 0.05;
		this.dir = dir.add(new Vector3(Math.sin(theta) * b, Math.random() * 0.1, Math.cos(theta) * b));
		this.loc = loc.add(new Vector3(Math.sin(theta) * a, 0, Math.cos(theta) * a)).add(dir.multiply(3));
		this.ttl = ttl;
		this.spread = spread;
	}
	
	public void draw() {
		// Draw a circle at the location with a color that gets lighter over the particle's life span.
		Projector.screen.drawCircle(loc, 10, new Color(Math.min(255, ttl + 155), Math.max(0, ttl - 155), 0).getRGB());
	}
	
	/**
	 * Called 60 times per second by EngineThread
	 */
	public void tick() {
		// Remove old particles
		if (ttl + 3 > 255) {
			Projector.particles.remove(this);
			return;
		} else ttl += 3;
		// The particle's new location based on movement direction.
		Vector3 loc2 = loc.add(dir.multiply((300 - ttl) / 255.0));
		// Check to see if the particle will collide with an object and remove it.
		if (Math.pow(loc2.x - 15, 2) + Math.pow(loc2.y - 55, 2) + Math.pow(loc2.z - 15, 2) < 100 || (loc2.x > 5 && loc2.y > 5 && loc2.z > 5 && loc2.x < 25 && loc2.y < 25 && loc2.z < 25)) {
			Projector.particles.remove(this);
			// There is a random chance that a particle will catch fire to the object it hits.
			if (Math.random() < spread) Projector.fires.add(loc);
			return;
		} else {
			loc = loc2;
		}
		// Slight upward movement.
		loc.y += 0.2;
	}
}
