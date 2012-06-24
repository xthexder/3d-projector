package org.frustra.projector.linear;

/**
 * A 2D vector structure that has many available operations.
 * 
 * @author Jacob Wirth
 *
 */
public class Vector4 {
	public double x;
	public double y;
	public double z;
	public double w;
	
	public Vector4() {}
	
	public Vector4(Vector4 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.w = vec.w;
	}
	
	public Vector4(Vector3 vec, double w) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.w = w;
	}
	
	public Vector4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public double dot(Vector4 vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z + this.w * vector.w;
	}

	
	public double dot(double x, double y, double z, double w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}
	
	public Vector4 multiply(double scalar) {
		return new Vector4(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
	}
	
	public Vector4 multiply(Vector4 vector) {
		return new Vector4(
			this.w * vector.w - this.x * vector.x - this.y * vector.y - this.z * vector.z,
	        this.w * vector.x + this.x * vector.w + this.y * vector.z - this.z * vector.y,
	        this.w * vector.y + this.y * vector.w + this.z * vector.x - this.x * vector.z,
	        this.w * vector.z + this.z * vector.w + this.x * vector.y - this.y * vector.x
		);
	}
	
	public Vector4 add(Vector4 vector) {
		return new Vector4(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.w + vector.w);
	}
	
	public Vector4 add(double scalar) {
		return new Vector4(this.x + scalar, this.y + scalar, this.z + scalar, this.w + scalar);
	}
	
	public double length() {
		return Math.sqrt(this.dot(this));
	}
	
	public Vector4 normalize() {
		double len = this.length();
		if (len <= 0) return new Vector4(1, 0, 0, 0); // Problem?
		return this.multiply(1.0 / len);
	}
	
	/**
	 * Used for converting a rotation vector into a transformation matrix.
	 * @return The corresponding transformation matrix
	 */
	public Matrix4x4 toMatrix() {
		Matrix4x4 result = new Matrix4x4(1);
		result.data[0].x = 1 - 2 * this.y * this.y - 2 * this.z * this.z;
		result.data[0].y = 2 * this.x * this.y + 2 * this.w * this.z;
		result.data[0].z = 2 * this.x * this.z - 2 * this.w * this.y;

		result.data[1].x = 2 * this.x * this.y - 2 * this.w * this.z;
		result.data[1].y = 1 - 2 * this.x * this.x - 2 * this.z * this.z;
		result.data[1].z = 2 * this.y * this.z + 2 * this.w * this.x;

		result.data[2].x = 2 * this.x * this.z + 2 * this.w * this.y;
        result.data[2].y = 2 * this.y * this.z - 2 * this.w * this.x;
        result.data[2].z = 1 - 2 * this.x * this.x - 2 * this.y * this.y;
        return result;
	}
	
	public String toString() {
		return x + ", " + y + ", " + z + ", " + w;
	}
}
