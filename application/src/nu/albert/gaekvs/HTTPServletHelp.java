package nu.albert.gaekvs;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.appengine.repackaged.com.google.common.util.Base64DecoderException;

public class HTTPServletHelp {

	public static boolean httpsCheck(HttpServletRequest req) throws IOException {
		//https is required... (or localhost during testing)
		if (!(req.getRequestURL().toString().startsWith("https://")
				|| req.getRequestURL().toString().startsWith("http://localhost:8888/")
				|| req.getRequestURL().toString().startsWith("http://10.0.2.2:8888/")
				)) {
			return false;
		}
		
		return true;
	}
	
	public static void error(HttpServletResponse resp, Exception ex) throws IOException {
		resp.sendError(500, "Internal server error: " + ex.getMessage());
	}


	public static void error(HttpServletResponse resp, int code, String msg) throws IOException {
		resp.sendError(code, msg);
	}

	public static String getInput(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
		StringBuffer sb = new StringBuffer();
		
        int i;
        while ((i = br.read()) != -1) {
            sb.append((char)i);
        }
		
		return sb.toString();
	}

	public static String getBasicAuth(HttpServletRequest req) {
		try {
			String auth = req.getHeader("Authorization");
			
			if (auth == null)
				return null; // no auth
	
			if (!auth.toUpperCase().startsWith("BASIC "))
				return null; // we only do BASIC
	
			// Get encoded user and password, comes after "BASIC "
			String userpassEncoded = auth.substring(6);
	
			// Decode it, using any base 64 decoder
			String userpassDecoded;
			try {
				userpassDecoded = new String(Base64.decode(userpassEncoded));
			} catch (Base64DecoderException e) {
				return null;
			}
	
			return userpassDecoded;
		} catch (Exception ex) {
			return null;
		}
	}
}
