package org.frustra.projector.linear;

/**
 * A 4x4 Matrix structure that can be multiplied.
 * 
 * @author Jacob Wirth
 *
 */
public class Matrix4x4 {
	// Vectors are columns
	public Vector4[] data = new Vector4[4];
	
	public Matrix4x4() {}

	public Matrix4x4(double num) {
		data[0] = new Vector4(num, 0, 0, 0);
		data[1] = new Vector4(0, num, 0, 0);
		data[2] = new Vector4(0, 0, num, 0);
		data[3] = new Vector4(0, 0, 0, num);
	}
	
	public Matrix4x4(Vector4 a, Vector4 b, Vector4 c, Vector4 d) {
		data[0] = a;
		data[1] = b;
		data[2] = c;
		data[3] = d;
	}
	
	public Matrix4x4(
		double a0, double b0, double c0, double d0,
		double a1, double b1, double c1, double d1,
		double a2, double b2, double c2, double d2,
		double a3, double b3, double c3, double d3
	) {
		data[0] = new Vector4(a0, a1, a2, a3);
		data[1] = new Vector4(b0, b1, b2, b3);
		data[2] = new Vector4(c0, c1, c2, c3);
		data[3] = new Vector4(d0, d1, d2, d3);
	}
	
	public Matrix4x4 multiply(double scalar) {
		return new Matrix4x4(data[0].multiply(scalar), data[1].multiply(scalar), data[2].multiply(scalar), data[3].multiply(scalar));
	}
	
	public Matrix4x4 multiply(Matrix4x4 matrix) {
		return new Matrix4x4(
			matrix.data[0].dot(data[0].x, data[1].x, data[2].x, data[3].x),
			matrix.data[0].dot(data[0].y, data[1].y, data[2].y, data[3].y),
			matrix.data[0].dot(data[0].z, data[1].z, data[2].z, data[3].z),
			matrix.data[0].dot(data[0].w, data[1].w, data[2].w, data[3].w),

			matrix.data[1].dot(data[0].x, data[1].x, data[2].x, data[3].x),
			matrix.data[1].dot(data[0].y, data[1].y, data[2].y, data[3].y),
			matrix.data[1].dot(data[0].z, data[1].z, data[2].z, data[3].z),
			matrix.data[1].dot(data[0].w, data[1].w, data[2].w, data[3].w),

			matrix.data[2].dot(data[0].x, data[1].x, data[2].x, data[3].x),
			matrix.data[2].dot(data[0].y, data[1].y, data[2].y, data[3].y),
			matrix.data[2].dot(data[0].z, data[1].z, data[2].z, data[3].z),
			matrix.data[2].dot(data[0].w, data[1].w, data[2].w, data[3].w),

			matrix.data[3].dot(data[0].x, data[1].x, data[2].x, data[3].x),
			matrix.data[3].dot(data[0].y, data[1].y, data[2].y, data[3].y),
			matrix.data[3].dot(data[0].z, data[1].z, data[2].z, data[3].z),
			matrix.data[3].dot(data[0].w, data[1].w, data[2].w, data[3].w)
		);
	}
	
	public Vector4 multiply(Vector4 vec) {
		return new Vector4(
			data[0].x * vec.x + data[1].x * vec.y + data[2].x * vec.z + data[3].x * vec.w,
			data[0].y * vec.x + data[1].y * vec.y + data[2].y * vec.z + data[3].y * vec.w,
			data[0].z * vec.x + data[1].z * vec.y + data[2].z * vec.z + data[3].z * vec.w,
			data[0].w * vec.x + data[1].w * vec.y + data[2].w * vec.z + data[3].w * vec.w
		);
	}
}
