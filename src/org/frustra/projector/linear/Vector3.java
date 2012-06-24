package org.frustra.projector.linear;


/**
 * A 3D vector structure that has many available operations.
 * 
 * @author Jacob Wirth
 *
 */
public class Vector3 {
	public double x;
	public double y;
	public double z;
	
	public Vector3() {}

	public Vector3(Vector3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double dot(Vector3 vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z;
	}
	
	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector3 cross(Vector3 vector) {
		return new Vector3(this.y * vector.z - vector.y * this.z,  this.z * vector.x - vector.z * this.x, this.x * vector.y - vector.x * this.y);
	}

	public Vector3 cross(double x, double y, double z) {
		return new Vector3(this.y * z - y * this.z,  this.z * x - z * this.x, this.x * y - x * this.y);
	}

	public static Vector4 cross(Vector4 a, Vector4 b) {
		return new Vector4(b.y * a.z - a.y * b.z,  b.z * a.x - a.z * b.x, b.x * a.y - a.x * b.y, 0);
	}
	
	public Vector3 multiply(double scalar) {
		return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	
	public Vector3 add(Vector3 vector) {
		return new Vector3(this.x + vector.x, this.y + vector.y, this.z + vector.z);
	}
	
	public Vector3 subtract(Vector3 vector) {
		return new Vector3(this.x - vector.x, this.y - vector.y, this.z - vector.z);
	}
	
	public double length() {
		return Math.sqrt(this.dot(this));
	}
	
	public Vector3 normalize() {
		double len = this.length();
		if (len <= 0) return new Vector3(1, 0, 0); // Problem?
		return this.multiply(1.0 / len);
	}
	
	/**
	 * Convert a normalized screen coordinate into an absolute screen coordinate.
	 * @param vec
	 * @param w The screen width
	 * @param h The screen height
	 * @return
	 */
	public static Vector3 toScreen(Vector3 vec, double w, double h) {
		if (vec == null) return null;
		return new Vector3(vec.x * w, vec.y * h, vec.z);
	}
	
	public String toString() {
		return x + ", " + y + ", " + z;
	}
}
