package nu.albert.gaekvs;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class INITServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		try {
			if (Configuration.get("Read") == null)
				Configuration.set("Read", "any");
			
			if (Configuration.get("Write") == null)
				Configuration.set("Write", "admin");

			if (Configuration.get("HttpsRequired") == null)
				Configuration.set("HttpsRequired", "true");

			if (Configuration.get("AdminPassword") == null)
				Configuration.set("AdminPassword", MD5.Hash(String.format("%s%s", System.currentTimeMillis(), Math.random())));
			
			if (Resource.get("test/test") == null)
				new Resource("test/test", "text/plain", "UTF-8", "Hello!").put();
			
			if (Resource.get("test/test2") == null)
				new Resource("test/test2", "text/plain", "UTF-8", "Welcome to GAE-KVS!").put();
			
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println("Please alter your configuration options in the admin GUI at:\nhttps://appengine.google.com/\n\nChoose menu: Datastore Viewer and edit the Configuration entities.\n\nRead: any|admin|none\nWrite: any|admin|none\nAdminPassword: <password>\nHttpsRequired: true|false");
			
		} catch (Exception ex) {
			HTTPServletHelp.error(resp, ex);
		}
	}
}
