package nu.albert.gaekvs;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TESTServlet extends HttpServlet {

	private static final String row = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
	private static String domain = "";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String t = req.getRequestURL().toString();
		
		t = t.substring(0, t.length() - req.getRequestURI().length()) + "/";
		domain = t;
		
		try {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println("<html><body>");

			resp.getWriter().println("Read access tests...<br>");
			resp.getWriter().println(String.format("Current read access: %s<br>", Configuration.get("Read")));
			resp.getWriter().println("<table border=\"1\"><tr><th>Test</th><th>Expected</th><th>Result</th></tr>");

			if (Configuration.get("Read").equals("any")) {
				testGetCode(resp, "gaekvs/", 400);
				testGet(resp, "gaekvs/test/test", 200, "Hello!");
				testGet(resp, "gaekvs/test/test2", 200, "Welcome to GAE-KVS!");
				testGetCode(resp, "gaekvs/test/testing", 404);
				testGetCode(resp, "gaekvs/?", 400);
				testGetCode(resp, "gaekvs/test?test", 400);
				testGet(resp, "gaekvs/test/", 200, "Welcome to GAE-KVS!\r\nHello!\r\n");
			} else if (Configuration.get("Read").equals("admin")) {
				testGetCode(resp, "gaekvs/", 401);
				testGetCode(resp, "gaekvs/test/test", 401);
				testGetCode(resp, "gaekvs/test/test2", 401);
				testGetCode(resp, "gaekvs/test/testing", 401);
				testGetCode(resp, "gaekvs/?", 401);
				testGetCode(resp, "gaekvs/test?test", 401);
				testGet(resp, "gaekvs/test/test", "admin", Configuration.get("AdminPassword"), 200, "Hello!");
				testGet(resp, "gaekvs/test/test2", "admin", Configuration.get("AdminPassword"), 200, "Welcome to GAE-KVS!");
				testGet(resp, "gaekvs/test/testing", "admin", Configuration.get("AdminPassword"), 404, null);
				testGet(resp, "gaekvs/test/", "admin", Configuration.get("AdminPassword"), 200, "Welcome to GAE-KVS!\r\nHello!\r\n");
				testGet(resp, "gaekvs/test/test", "admin", "badpassword", 401, null);
				testGet(resp, "gaekvs/test/test2", "admin", "badpassword", 401, null);
				testGet(resp, "gaekvs/test/testing", "admin", "badpassword", 401, null);
				testGet(resp, "gaekvs/test/", "admin", "badpassword", 401, null);
			} else if (Configuration.get("Read").equals("none")) {
				testGetCode(resp, "gaekvs/", 403);
				testGetCode(resp, "gaekvs/test/test", 403);
				testGetCode(resp, "gaekvs/test/test2", 403);
				testGetCode(resp, "gaekvs/test/testing", 403);
				testGetCode(resp, "gaekvs/?", 403);
				testGetCode(resp, "gaekvs/test?test", 403);
			} else {
				HTTPServletHelp.error(resp, 403, "Unknown read access right. Valid value is one of: any, admin, none.");
			}
			
			resp.getWriter().println("</table>");
			

			resp.getWriter().println("<br><br>Write access tests...<br>");
			resp.getWriter().println(String.format("Current write access: %s<br>", Configuration.get("Write")));
			resp.getWriter().println("If read access is any or admin then writes will be verified<br>");
			resp.getWriter().println("<table border=\"1\"><tr><th>Test</th><th>Expected</th><th>Result</th></tr>");

			if (Configuration.get("Write").equals("any")) {
				testPut(resp, "gaekvs/?", 400, "test");
				testDelete(resp, "gaekvs/?", 400);
				testDelete(resp, "gaekvs/testar2", 404);
				testPut(resp, "gaekvs/test/test/test1", 201, "data");
				if (!Configuration.get("Read").equals("none"))
					testGet(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 200, "data");
				testPut(resp, "gaekvs/test/test/test1", 200, "data!");
				if (!Configuration.get("Read").equals("none"))
					testGet(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 200, "data!");
				testPut(resp, "gaekvs/test/test/test2", 201, "data2");
				testDelete(resp, "gaekvs/test/test/test2", 200);
				testDelete(resp, "gaekvs/test/test/", 200);
			} else if (Configuration.get("Write").equals("admin")) {
				testPut(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 201, "data");
				if (!Configuration.get("Read").equals("none"))
					testGet(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 200, "data");
				testPut(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 200, "data!");
				if (!Configuration.get("Read").equals("none"))
					testGet(resp, "gaekvs/test/test/test1", "admin", Configuration.get("AdminPassword"), 200, "data!");
				testPut(resp, "gaekvs/test/test/test2", "admin", Configuration.get("AdminPassword"), 201, "data2");
				testDelete(resp, "gaekvs/test/test/test2", "admin", "badpassword", 401);
				testDelete(resp, "gaekvs/test/test/test2", 401);
				testDelete(resp, "gaekvs/test/test/test2", "admin", Configuration.get("AdminPassword"), 200);
				testDelete(resp, "gaekvs/test/test/", "admin", "badpassword", 401);
				testDelete(resp, "gaekvs/test/test/", 401);
				testDelete(resp, "gaekvs/test/test/", "admin", Configuration.get("AdminPassword"), 200);
				testPut(resp, "gaekvs/test/test/test2", "admin", "badpassword", 401, "data2");
				testPut(resp, "gaekvs/testar2", 401, "data2");
				testDelete(resp, "gaekvs/testar2", 401);
			} else if (Configuration.get("Write").equals("none")) {
				testPut(resp, "gaekvs/?", 403, "test");
				testDelete(resp, "gaekvs/?", 403);
				testDelete(resp, "gaekvs/testar2", 403);
				testPut(resp, "gaekvs/test/test/test1", 403, "data");
				testPut(resp, "gaekvs/test/test/test1", 403, "data!");
				testPut(resp, "gaekvs/test/test/test2", 403, "data2");
				testDelete(resp, "gaekvs/test/test/test2", 403);
				testDelete(resp, "gaekvs/test/test/", 403);
			} else {
				HTTPServletHelp.error(resp, 403, "Unknown write access right. Valid value is one of: any, admin, none.");
			}
			
			resp.getWriter().println("</table>");			

			
			resp.getWriter().println("</body><html>");
		} catch (Exception ex) {
			HTTPServletHelp.error(resp, ex);
		}
	}
	
	private static boolean testGetCode(HttpServletResponse resp, String path, int expectedCode) throws IOException {
		return testGet(resp, path, expectedCode, null);
	}
	
	private static boolean testGet(HttpServletResponse resp, String path, int expectedCode, String expectedResponse) throws IOException {
		String test = "Get " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		if (expectedResponse != null && exp.length() > 0)
			exp += ", ";
		
		if (expectedResponse != null)
			exp += String.format("\"%s\"", expectedResponse);
		
		try {
			StringBuilder sb = new StringBuilder();
			int rc = HTTPHelp.get(domain + path, sb);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
					return false;
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
				}
			}

			if (expectedResponse != null) {
				if (!sb.toString().equals(expectedResponse)) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got \"%s\".", sb.toString())));
					return false;
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
				}
			}

			return true;

		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

	
	private static boolean testDelete(HttpServletResponse resp, String path, int expectedCode) throws IOException {
		String test = "Delete " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		try {
			int rc = HTTPHelp.delete(domain + path);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}
			
		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

	
	private static boolean testDelete(HttpServletResponse resp, String path, String user, String password, int expectedCode) throws IOException {
		String test = "Delete " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		try {
			int rc = HTTPHelp.delete(domain + path, user, password);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}
			
		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

	
	private static boolean testPut(HttpServletResponse resp, String path, int expectedCode, String data) throws IOException {
		String test = "Put " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		try {
			int rc = HTTPHelp.put(domain + path, data);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}
			
		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

	
	private static boolean testPut(HttpServletResponse resp, String path, String user, String password, int expectedCode, String data) throws IOException {
		String test = "Put " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		try {
			int rc = HTTPHelp.put(domain + path, user, password, data);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}
			
		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

	
	private static boolean testGet(HttpServletResponse resp, String path, String user, String password, int expectedCode, String expectedResponse) throws IOException {
		String test = "Get " + path;
		String exp = "";
		
		if (expectedCode > 0)
			exp += String.format("Code = %s", expectedCode);
		
		if (expectedResponse != null && exp.length() > 0)
			exp += ", ";
		
		if (expectedResponse != null)
			exp += String.format("\"%s\"", expectedResponse);
		
		try {
			StringBuilder sb = new StringBuilder();
			int rc = HTTPHelp.get(domain + path, sb, user, password);
			
			if (expectedCode > 0) {
				if (rc != expectedCode) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got %s.", rc)));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}

			if (expectedResponse != null) {
				if (!sb.toString().equals(expectedResponse)) {
					resp.getWriter().println(String.format(row, test, exp, String.format("Error! Got \"%s\".", sb.toString())));
				} else {
					resp.getWriter().println(String.format(row, test, exp, "Ok"));
					return true;
				}
			}
			
		} catch (Exception ex) {
			resp.getWriter().println(String.format("<tr><td>%s</td><td></td><td>Exception: %s</td></tr>", test, ex.getMessage()));
		}
		
		return false;
	}

}
