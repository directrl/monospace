package org.black_matter.monospace.util;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class QuaternionUtils {
	
	public static Vector3f getRotationXYZ(Quaternionfc q) {
		Vector3f xyz = new Vector3f();
		
		var x = q.x();
		var y = q.y();
		var z = q.z();
		var w = q.w();
		
		// x-axis (roll)
		float sinr_cosp = 2 * ((w * x) + (y * z));
		float cosr_cosp = 1 - 2 * ((x * x) + (y * y));
		xyz.x = (float) Math.atan2(sinr_cosp, cosr_cosp);
		
		// y-axis (pitch)
		float sinp = 2 * ((w * y) - (z * x));
		
		if(Math.abs(sinp) >= 1) {
			xyz.y = (float) Math.copySign(Math.PI / 2, sinp); // Use 90 degrees if out of range
		} else {
			xyz.y = (float) Math.asin(sinp);
		}
		
		// z-axis (yaw)
		float siny_cosp = 2 * ((w * z) + (x * y));
		float cosy_cosp = 1 - 2 * ((y * y) + (z * z));
		xyz.z = (float) Math.atan2(siny_cosp, cosy_cosp);
		
		return xyz;
	}
}
