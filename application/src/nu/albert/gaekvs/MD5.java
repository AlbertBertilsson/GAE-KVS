package nu.albert.gaekvs;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {
	public static String Hash(String s) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			BigInteger i = new BigInteger(1, m.digest());
			
			return String.format("%1$032X", i).toLowerCase();
		} catch (Exception ex) {
			return "";
		}
	}
}
