package org.frustra.projector;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.frustra.projector.gfx.Camera;
import org.frustra.projector.gfx.RenderThread;
import org.frustra.projector.gfx.Screen;
import org.frustra.projector.linear.Vector3;

/**
 * The main 3D Projector class.
 * Stores instances for different classes, and is the UI window itself.
 * 
 * @author Jacob Wirth
 *
 */
public class Projector extends Applet {
	private static final long serialVersionUID = 1L;
	
	public static int WIDTH = 900;
	public static int HEIGHT = 600;
	
	public static boolean isApplet = true;
	public static JFrame frame = null;

	public static Screen screen;
	public static Camera camera;
	
	public static EngineThread engine;
	public static RenderThread render;
	public static InputHandler input;

	/**
	 * Existing fire particles
	 */
	public static ArrayList<FireParticle> particles;
	
	/**
	 * Locations currently burning (particle emitter locations)
	 */
	public static ArrayList<Vector3> fires;
	
	public void init() {
		screen = new Screen(WIDTH, HEIGHT, true);
		camera = new Camera();
		
		input = new InputHandler();
		screen.addKeyListener(input);
		screen.addMouseListener(input);
		screen.addMouseMotionListener(input);

		particles = new ArrayList<FireParticle>();
		fires = new ArrayList<Vector3>();

		setSize(WIDTH, HEIGHT);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(new BorderLayout());
		add(screen, BorderLayout.CENTER);
	}
	
	public void start() {
		engine = new EngineThread();
		engine.start();
		render = new RenderThread();
		render.start();
		input.start();
	}
	
	public void stop() {
		engine.stop();
		render.stop();
	}
	
	public static void main(String[] args) {
		isApplet = false;
		Projector game = new Projector();
		
		// The projector is not running in an applet, make the window bigger because we can.
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = (int) (size.width * 0.9);
		HEIGHT = (int) (size.height * 0.9);
		game.init();
		
		frame = new JFrame("3D Projector V2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(game, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		game.start();
	}
}
