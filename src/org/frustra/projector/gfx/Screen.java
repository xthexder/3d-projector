package org.frustra.projector.gfx;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.frustra.projector.Projector;
import org.frustra.projector.linear.Vector3;

/**
 * Stores pixel and depth data buffers for the screen.
 * Contains many different custom drawing functions
 * 
 * @author Jacob Wirth
 *
 */
public class Screen extends Canvas {
	private static final long serialVersionUID = 1L;
	
	public final int w, h;
	public BufferedImage image;
	public Graphics2D g2;
	
	/**
	 * Red, Green, Blue pixel data
	 */
	public int[] pixels;
	
	/**
	 * Screen depth buffer (The depth of each pixel on screen)
	 */
	public double[] depth;
	
	public Screen(int w, int h, boolean top) {
		this.w = w;
		this.h = h;
		setSize(w, h);
		
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		this.g2 = this.image.createGraphics();
		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.depth = new double[this.pixels.length];
	}
	
	/**
	 * Called 60 times per second by RenderThread.
	 */
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}

		if (!this.isVisible()) return;
		Graphics g = bs.getDrawGraphics();
		g2.setBackground(Color.BLACK);
		g2.clearRect(0, 0, w, h);
		this.depth = new double[this.pixels.length];

		Projector.camera.x = Projector.input.tx;
		Projector.camera.y = Projector.input.ty;
		Projector.camera.z = Projector.input.tz;
		Projector.camera.setRotation(Projector.input.pitch, Projector.input.yaw);
		
		renderAxes();
		renderScene();
		for (int i = 0; i < Projector.particles.size(); i++) {
			Projector.particles.get(i).draw();
		}
		
		if (Projector.input.depthBufferOverlay) {
			// Make the pixels darker the farther away from the camera they are. (Overlay the depth buffer)
			for (int i = 0; i < pixels.length; i++) {
				if (depth[i] == 0) continue;
				double scale = Math.max(0.05, Math.min(1.0, Math.log10(depth[i] * 100 - 80)));
				pixels[i] = new Color((int) (((pixels[i] >> 16) & 0xFF) * scale), (int) (((pixels[i] >> 8) & 0xFF) * scale), (int) ((pixels[i] & 0xFF) * scale)).getRGB();
			}
		}
		
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		bs.show();
	}
	
	/**
	 * Render the 3 axes. X: Red, Y: Green, Z: Blue
	 */
	public void renderAxes() {
		drawLine(new Vector3(), new Vector3(100, 0, 0), 0xFF0000);
		drawLine(new Vector3(), new Vector3(0, 100, 0), 0x00FF00);
		drawLine(new Vector3(), new Vector3(0, 0, 100), 0x0000FF);
	}
	
	/**
	 * Render the scene. (Sphere and Cube)
	 */
	public void renderScene() {
		// Calculate and draw many points on the sphere.
		for (double a = -Math.PI / 2; a < Math.PI / 2; a += Math.PI / 32) {
			for (double b = 0; b < Math.PI * 2; b += Math.PI / 32) {
				drawPoint(new Vector3(Math.sin(b) * Math.cos(a) * 10 + 15, Math.sin(a) * 10 + 55, Math.cos(b) * Math.cos(a) * 10 + 15), 0xFFFFFF);
			}
		}
		
		// The 8 points on the cube.
		Vector3 vec1 = new Vector3(5, 5, 5);
		Vector3 vec2 = new Vector3(25, 5, 5);
		Vector3 vec3 = new Vector3(5, 25, 5);
		Vector3 vec4 = new Vector3(5, 5, 25);
		Vector3 vec5 = new Vector3(25, 25, 5);
		Vector3 vec6 = new Vector3(5, 25, 25);
		Vector3 vec7 = new Vector3(25, 5, 25);
		Vector3 vec8 = new Vector3(25, 25, 25);
		
		// Top and bottom face lines.
		drawLine(vec3, vec8, 0xFFFFFF);
		drawLine(vec5, vec6, 0xFFFFFF);
		
		drawLine(vec1, vec7, 0xFFFFFF);
		drawLine(vec4, vec2, 0xFFFFFF);

		// The 4 solid faces.
		drawTriangle(vec1, vec4, vec6, 0x550000);
		drawTriangle(vec1, vec3, vec6, 0x550000);

		drawTriangle(vec2, vec7, vec8, 0x555500);
		drawTriangle(vec2, vec5, vec8, 0x555500);

		drawTriangle(vec1, vec2, vec5, 0x000055);
		drawTriangle(vec1, vec3, vec5, 0x000055);

		drawTriangle(vec4, vec7, vec8, 0x005555);
		drawTriangle(vec4, vec6, vec8, 0x005555);

		// A face intersecting another one of the faces to demonstrate and test the depth buffer.
		drawTriangle(new Vector3(15, 5, 15), new Vector3(35, 5, 15), new Vector3(35, 25, 15), 0x005555);
		drawTriangle(new Vector3(15, 5, 15), new Vector3(15, 25, 15), new Vector3(35, 25, 15), 0x005555);
	}
	
	/**
	 * Draw a line from point A to point B in 3D space.
	 * @param a
	 * @param b
	 * @param color
	 */
	public void drawLine(Vector3 a, Vector3 b, int color) {
		Vector3 vec1 = Vector3.toScreen(Projector.camera.project(a), w, h);
		Vector3 vec2 = Vector3.toScreen(Projector.camera.project(b), w, h);
		if (vec1 != null && vec2 != null) fillLine(vec1, vec2, color);
	}

	/**
	 * Fill in the 3D point A with the specified color.
	 * @param a
	 * @param b
	 * @param color
	 */
	public void drawPoint(Vector3 a, int color) {
		Vector3 vec = Vector3.toScreen(Projector.camera.project(a), w, h);
		if (vec != null) fillPixel((int) vec.x, (int) vec.y, vec.z, color);
	}
	
	/**
	 * Draw a circle at the specified 3D point with the specified radius.
	 * @param a
	 * @param radius
	 * @param color
	 */
	public void drawCircle(Vector3 a, double radius, int color) {
		Vector3 vec = Vector3.toScreen(Projector.camera.project(a), w, h);
		if (vec != null) {
			int i = (int) vec.x + (int) vec.y * w;
			if (vec.x < 0 || vec.x >= w || vec.y < 0 || vec.y >= h) return;
			if (depth[i] == 0 || depth[i] < vec.z) {
				fillCircle(vec, Math.max(2, radius * (vec.z - 0.8) * 15), color);
			}
		}
	}
	
	/**
	 * Fill in a triangle in 3D space with the specified color.
	 * @param a
	 * @param b
	 * @param c
	 * @param color
	 */
	public void drawTriangle(Vector3 a, Vector3 b, Vector3 c, int color) {
		Vector3 vec1 = Vector3.toScreen(Projector.camera.project(a), w, h);
		Vector3 vec2 = Vector3.toScreen(Projector.camera.project(b), w, h);
		Vector3 vec3 = Vector3.toScreen(Projector.camera.project(c), w, h);
		if (vec1 != null && vec2 != null && vec3 != null) {
			fillTriangle(vec1, vec2, vec3, color);
		}
	}
	/**
	 * Fill in a circle at point A in 2D space with the specified color.
	 * @param a1
	 * @param b1
	 * @param color
	 */
	private void fillCircle(Vector3 a, double radius, int color) {
		double radiuss = radius * radius;
		for (int y = (int) -radius; y < radius; y++) {
			int hw = (int) Math.sqrt(radiuss - y * y);
			for (int x = -hw; x < hw; x++) {
				fillPixel((int) (a.x + x), (int) (a.y + y), a.z, color);
			}
		}
	}
	
	/**
	 * Fill in a line from A to B in 2D space with the specified color.
	 * @param a1
	 * @param b1
	 * @param color
	 */
	private void fillLine(Vector3 a1, Vector3 b1, int color) {
		Vector3 a = null;
		Vector3 b = null;
		double slope = (b1.y - a1.y) / (b1.x - a1.x);
		if (slope > 1 || slope < -1) {
			if (a1.y < b1.y) {
				a = a1;
				b = b1;
			} else {
				a = b1;
				b = a1;
			}
			slope = (b.x - a.x) / (b.y - a.y);
			double slope2 = (b.z - a.z) / (b.y - a.y);
			double x = a.x;
			double z = a.z;
			for (int y = (int) a.y; y <= b.y; y++) {
				fillPixel((int) (x += slope), (int) y, z += slope2, color);
			}
		} else {
			if (a1.x < b1.x) {
				a = a1;
				b = b1;
			} else {
				a = b1;
				b = a1;
			}
			double slope2 = (b.z - a.z) / (b.x - a.x);
			double y = a.y;
			double z = a.z;
			for (int x = (int) a.x; x <= b.x; x++) {
				fillPixel((int) x, (int) (y += slope), z += slope2, color);
			}
		}
	}
	
	/**
	 * Fill the the specified triangle in 2D space with the specified color.
	 * @param a1
	 * @param b1
	 * @param c1
	 * @param color
	 */
	private void fillTriangle(Vector3 a1, Vector3 b1, Vector3 c1, int color) {
		Vector3 a = null;
		Vector3 b = null;
		Vector3 c = null;
		if (a1.y < b1.y && b1.y < c1.y) {
			a = a1;
			b = b1;
			c = c1;
		} else if (a1.y < b1.y && a1.y < c1.y) {
			a = a1;
			b = c1;
			c = b1;
		} else if (b1.y < a1.y && a1.y < c1.y) {
			a = b1;
			b = a1;
			c = c1;
		} else if (b1.y < a1.y && b1.y < c1.y) {
			a = b1;
			b = c1;
			c = a1;
		} else if (c1.y < a1.y && a1.y < b1.y) {
			a = c1;
			b = a1;
			c = b1;
		} else {
			a = c1;
			b = b1;
			c = a1;
		}

		Vector3 norm = c.subtract(a).cross(b.subtract(a));
		double D = -norm.dot(a);

		double d0 = b.y != a.y ? (b.x - a.x) / (b.y - a.y) : 0;
		double d1 = c.y != b.y ? (c.x - b.x) / (c.y - b.y) : 0;
		double d2 = a.y != c.y ? (a.x - c.x) / (a.y - c.y) : 0;

		int sx, ex;
		int minx = (int) Math.min(a.x, Math.min(b.x, c.x));
		int maxx = (int) Math.max(a.x, Math.max(b.x, c.x));
		
		for (int y = (int) a.y; y < b.y; y++) {
			sx = (int) (Math.max(minx, Math.min(maxx, a.x + (y - a.y) * d2)));
			ex = (int) (Math.max(minx, Math.min(maxx, a.x + (y - a.y) * d0)));

			if (sx < ex) {
				for (int x = (sx < 0 ? 0 : sx); x <= (ex >= w ? w - 1 : ex); x++) {
					fillPixel(x, y, getDepthOnPlane(x, y, norm, D), color);
				}
			} else {
				for (int x = (ex < 0 ? 0 : ex); x <= (sx >= w ? w - 1 : sx); x++) {
					fillPixel(x, y, getDepthOnPlane(x, y, norm, D), color);
				}
			}
		}

		for (int y = (int) b.y; y < c.y; y++) {
			sx = (int) (Math.max(minx, Math.min(maxx, a.x + (y - a.y) * d2)));
			ex = (int) (Math.max(minx, Math.min(maxx, b.x + (y - b.y) * d1)));

			if (sx < ex) {
				for (int x = (sx < 0 ? 0 : sx); x <= (ex >= w ? w - 1 : ex); x++) {
					fillPixel(x, y, getDepthOnPlane(x, y, norm, D), color);
				}
			} else {
				for (int x = (ex < 0 ? 0 : ex); x <= (sx >= w ? w - 1 : sx); x++) {
					fillPixel(x, y, getDepthOnPlane(x, y, norm, D), color);
				}
			}
		}
	}
	
	/**
	 * Calculates the depth of a 2D point using linear interpolation between 3 other points.
	 * @param x
	 * @param y
	 * @param norm The normal of the plane intersecting the 3 reference points.
	 * @param D The D value of the plane intersecting the 3 reference points.
	 * @return
	 */
	private double getDepthOnPlane(double x, double y, Vector3 norm, double D) {
		return (norm.x * x + norm.y * y + D) / -norm.z;
	}
	
	/**
	 * Fill in the specified pixel with the given color if it is in front of any previously drawn pixels at that location.
	 * @param x
	 * @param y
	 * @param z
	 * @param color
	 */
	private void fillPixel(int x, int y, double z, int color) {
		int i = (int) x + (int) y * w;
		if (x < 0 || x >= w || y < 0 || y >= h) return;
		// If the depth is not set or the depth is less than what is being drawn, draw the specified color.
		if (depth[i] == 0 || depth[i] < z || !Projector.input.useDepthBuffer) {
			depth[i] = z;
			pixels[i] = color;
		}
	}
}
