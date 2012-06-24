package org.frustra.projector;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handles all user input
 * 
 * @author Jacob Wirth
 *
 */
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, Runnable {

	// Key-down flags
	public boolean up = false;
	public boolean down = false;
	public boolean left = false;
	public boolean right = false;
	public boolean space = false;
	public boolean ctrl = false;
	public boolean leftClick = false;
	public boolean rightClick = false;
	
	public double pitch = Math.PI / -5;
	public double yaw = Math.PI / -4;
	
	public boolean lockInput = true;
	public boolean depthBufferOverlay = true;
	public boolean useDepthBuffer = true;

	// Mouse movement API
	Robot robot = null;
	
	private Thread t;
	private boolean running = false;
	
	public InputHandler() {
		// Robot cannot be used with an applet
		if (!Projector.isApplet) {
        	try {
				robot = new Robot();
			} catch (AWTException e) {}
		}
	}

	
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
		long lastTimer1 = System.currentTimeMillis();
		
		while (running) {
			tick();
			
			try {
				Thread.sleep(Math.max(2, 16 - (System.currentTimeMillis() - lastTimer1)));
			} catch (InterruptedException e) {
				break;
			}
			
			lastTimer1 = System.currentTimeMillis();
		}
	}
	
	/**
	 * Reset all input key-down states
	 */
	public void resetAll() {
		up = false;
		down = false;
		left = false;
		right = false;
		space = false;
		ctrl = false;
		leftClick = false;
		rightClick = false;
		lockInput = false;
	}
	
	public void keyPressed(KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				up = true;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				down = true;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				left = true;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				right = true;
				break;
			case KeyEvent.VK_SPACE:
				space = true;
				break;
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_SHIFT:
				ctrl = true;
				break;
			case KeyEvent.VK_Z:
				depthBufferOverlay = !depthBufferOverlay;
				break;
			case KeyEvent.VK_X:
				useDepthBuffer = !useDepthBuffer;
				break;
			case KeyEvent.VK_ESCAPE:
				lockInput = !lockInput;
				break;
		}
	}

	public void keyReleased(KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				up = false;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				down = false;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				left = false;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				right = false;
				break;
			case KeyEvent.VK_SPACE:
				space = false;
				break;
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_SHIFT:
				ctrl = false;
				break;
		}
	}

	public void keyTyped(KeyEvent event) {}

	int x = -1;
	int y = -1;
	boolean initialized = false;
	/**
	 * Handle mouse dragging so that it rotates the camera.
	 */
	public void mouseDragged(MouseEvent event) {
		if (lockInput && robot != null) {
			Component comp = event.getComponent();
			pitch -= (event.getY() - (comp.getHeight() / 2)) / 360.0;
			yaw += (event.getX() - (comp.getWidth() / 2)) / 360.0;
			if (pitch < Math.PI / -2) pitch = Math.PI / -2;
			if (pitch > Math.PI / 2) pitch = Math.PI / 2;
			if (yaw < -Math.PI) yaw += Math.PI * 2;
			if (yaw > Math.PI) yaw -= Math.PI * 2;
			Point offset = comp.getLocationOnScreen();
			robot.mouseMove(offset.x + (comp.getWidth() / 2), offset.y + (comp.getHeight() / 2));
		} else {
			pitch -= (event.getY() - y) / 360.0;
			yaw += (event.getX() - x) / 360.0;
			if (pitch < Math.PI / -2) pitch = Math.PI / -2;
			if (pitch > Math.PI / 2) pitch = Math.PI / 2;
			if (yaw < -Math.PI) yaw += Math.PI * 2;
			if (yaw > Math.PI) yaw -= Math.PI * 2;
			x = event.getX();
			y = event.getY();
		}
	}

	/**
	 * Handle mouse movement so that it rotates the camera if input is grabbed.
	 */
	public void mouseMoved(MouseEvent event) {
		if (lockInput && robot != null) {
			Component comp = event.getComponent();
			if (initialized) {
				pitch -= (event.getY() - (comp.getHeight() / 2)) / 360.0;
				yaw += (event.getX() - (comp.getWidth() / 2)) / 360.0;
				if (pitch < Math.PI / -2) pitch = Math.PI / -2;
				if (pitch > Math.PI / 2) pitch = Math.PI / 2;
				if (yaw < -Math.PI) yaw += Math.PI * 2;
				if (yaw > Math.PI) yaw -= Math.PI * 2;
			} else initialized = true;
			Point offset = comp.getLocationOnScreen();
			robot.mouseMove(offset.x + (comp.getWidth() / 2), offset.y + (comp.getHeight() / 2));
		}
	}

	public void mouseClicked(MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}

	public void mousePressed(MouseEvent event) {
		x = event.getX();
		y = event.getY();
		if (event.getButton() == MouseEvent.BUTTON1) {
			leftClick = true;
		} else {
			rightClick = true;
		}
	}

	public void mouseReleased(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) {
			leftClick = false;
		} else {
			rightClick = false;
		}
	}

	// Current camera location coordinates
	public double tx = -50;
	public double ty = -70;
	public double tz = -50;
	
	/**
	 * Called 60 times per second.
	 * - If a key is pressed, move the camera in that direction.
	 */
	public void tick() {
		if (!Projector.screen.hasFocus()) {
			resetAll();
		}
		double pitch = Projector.input.pitch;
		double yaw = Projector.input.yaw;
		if (Projector.input.left) {
			tx += Math.cos(yaw);
			tz += Math.sin(yaw);
		} else if (Projector.input.right) {
			tx -= Math.cos(yaw);
			tz -= Math.sin(yaw);
		}
		if (Projector.input.up) {
			tx -= Math.sin(yaw) * Math.cos(pitch);
			ty -= Math.sin(pitch);
			tz += Math.cos(yaw) * Math.cos(pitch);
		} else if (Projector.input.down) {
			tx += Math.sin(yaw) * Math.cos(pitch);
			ty += Math.sin(pitch);
			tz -= Math.cos(yaw) * Math.cos(pitch);
		}
		if (Projector.input.space) {
			ty -= 1;
		} else if (Projector.input.ctrl) {
			ty += 1;
		}
	}
}
