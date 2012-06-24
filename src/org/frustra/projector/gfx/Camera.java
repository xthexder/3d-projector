package org.frustra.projector.gfx;

import org.frustra.projector.Projector;
import org.frustra.projector.linear.Matrix4x4;
import org.frustra.projector.linear.Vector3;
import org.frustra.projector.linear.Vector4;

/**
 * Handles all camera rotations and translations.
 * Does the actual projection math.
 * 
 * @author Jacob Wirth
 *
 */
public class Camera {
	Matrix4x4 viewMatrix;
	Matrix4x4 projMatrix;
	Vector4 rotation;
	
	double x;
	double y;
	double z;
	double pitch;
	double yaw;
	
	public Camera() {
		viewMatrix = new Matrix4x4(1);
		projMatrix = new Matrix4x4(1);
		// Initialize the viewport with the correct aspect ratio.
		Vector3 viewport = new Vector3(Projector.WIDTH, Projector.HEIGHT, 0).normalize();
		setViewport(viewport.x, viewport.y, 1, 10);
	}
	
	/**
	 * Reset the rotation vector, apply the yaw rotation, and then apply the pitch rotation (Standard first person camera)
	 * @param pitch Up / down rotation
	 * @param yaw Left / right rotation
	 */
	public void setRotation(double pitch, double yaw) {
		this.pitch = pitch / 2.0; // Up / down
		this.yaw = yaw / 2.0; // Left / right
		viewMatrix = new Matrix4x4(1);
		rotation = new Vector4(0, 0, 0, 1);
		offsetRotation(new Vector3(1, 0, 0), this.pitch);
		offsetRotation(new Vector3(0, 1, 0), this.yaw);
		viewMatrix = viewMatrix.multiply(rotation.toMatrix());
		setTranslation(new Vector3(x, y, z));
	}
	
	/**
	 * Apply a rotation to the existing rotation vector.
	 * @param axis A unit vector in the direction of the axis
	 * @param angle The angle to rotate around the given axis
	 */
	private void offsetRotation(Vector3 axis, double angle) {
		axis = axis.multiply(Math.sin(angle));
		double scalar = Math.cos(angle);
		
		Vector4 offset = new Vector4(axis, scalar);
		
		rotation = rotation.multiply(offset);
		rotation = rotation.normalize();
	}
	
	/**
	 * Apply a translation to the View Matrix
	 * @param vec
	 */
	private void setTranslation(Vector3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		Vector4 a = viewMatrix.data[0].multiply(vec.x);
		Vector4 b = viewMatrix.data[1].multiply(vec.y);
		Vector4 c = viewMatrix.data[2].multiply(vec.z);
		viewMatrix.data[3] = a.add(b).add(c).add(viewMatrix.data[3]);
	}
	
	/**
	 * Move the camera to the given x, y, z coordinates (Regenerate the View Matrix in the process)
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTranslation(double x, double y, double z) {
		viewMatrix = new Matrix4x4(1);
		rotation = new Vector4(0, 0, 0, 1);
		offsetRotation(new Vector3(1, 0, 0), pitch);
		offsetRotation(new Vector3(0, 1, 0), yaw);
		viewMatrix = viewMatrix.multiply(rotation.toMatrix());
		setTranslation(new Vector3(x, y, z));
	}
	
	/**
	 * Set the view port information and generate the Projection Matrix.
	 * @param right Location of the right side of the screen
	 * @param top Location of the top of the screen
	 * @param near Distance of the near plane
	 * @param far Distance of the far plane
	 */
	public void setViewport(double right, double top, double near, double far) {
		projMatrix.data[0].x = near / right;
		projMatrix.data[1].y = near / top;
		projMatrix.data[2].z = -(far + near) / (far - near);
		projMatrix.data[3].z = -2.0 * near * far / (far - near);
		projMatrix.data[2].w = -2 * near;
	}
	
	/**
	 * Project a 3D world coordinate on the screen space (with a depth value)
	 * @param vec The 3D coordinate to project
	 * @return A normalized 2D screen coordinate (This value is between -0.5 and 0.5, 0 being the center of the screen)
	 */
	public Vector3 project(Vector3 vec) {
		/*
		 * Relative 3D Coordinate = View Matrix * vec
		 * Result = (Projection * Relative 3D Coordinate) / w value
		 */
		
		Vector4 tmp = new Vector4(vec, 1);
		tmp = viewMatrix.multiply(tmp);
		// Ignore points behind the near plane, they can't be projected.
		if (tmp.z < 1) return null;
		tmp = projMatrix.multiply(tmp);

		tmp = tmp.multiply(1.0 / tmp.w);
		tmp = tmp.multiply(0.5).add(0.5);

		return new Vector3(tmp.x, tmp.y, tmp.z);
	}
}
