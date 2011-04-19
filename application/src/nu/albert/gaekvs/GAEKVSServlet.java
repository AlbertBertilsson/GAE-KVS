package nu.albert.gaekvs;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class GAEKVSServlet extends HttpServlet {
	private static final String servletUrl = "/gaekvs/";

	private String Check(HttpServletRequest req, HttpServletResponse resp, String accessType) throws IOException {
		
		if (!Configuration.get("HttpsRequired").equals("false")) {
			if (!HTTPServletHelp.httpsCheck(req)) {
				HTTPServletHelp.error(resp, 403, "Forbidden.");
				return null;
			}
		}
		
		String right = Configuration.get(accessType);
		if (right == null || right.equals("none")) {
			HTTPServletHelp.error(resp, 403, "Forbidden.");
			return null;
		}

		if (!right.equals("any")) {
			if (!right.equals("admin")) {
				HTTPServletHelp.error(resp, 403, "Forbidden, unknown access right. Valid value is one of: any, admin, none.");
				return null;
			} else {
				String auth = HTTPServletHelp.getBasicAuth(req);
				if (auth == null) {
					resp.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
					resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return null;
				}

				String[] sa = auth.split(":");
				if (sa.length != 2 || !sa[1].equals(Configuration.get("AdminPassword"))) {
					resp.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
					resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return null;
				}
			}
		}

		String entity = req.getRequestURI();
		if (!entity.startsWith(servletUrl)) {
			HTTPServletHelp.error(resp, 400, "Bad request URI.");
			return null;
		}
		
		if (req.getQueryString() != null) {
			HTTPServletHelp.error(resp, 400, "Bad request, query string not allowed.");
			return null;
		}
		
		entity = entity.substring(servletUrl.length());

		return entity;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		try {

			String entity = Check(req, resp, "Read");
			if (entity == null) return;
			
			Resource res = null;
			if ((entity.length() == 0 || entity.endsWith("/"))) {
				if (Configuration.get("WebServerMode").equals("true")) {
					res = Resource.get(entity + "index.html");
					if (res == null) res = Resource.get(entity + "index.htm");

					if (res != null && res.getResource() != null) {
						setContentHeaders(resp, res);
						resp.getOutputStream().write(res.getResource(), 0, res.getLength());
						return;
					}
				}

				Resource[] ra = Resource.getPath(entity);
				
				if (ra != null && ra.length > 0) {
					res = ra[0];
					setContentHeaders(resp, res);

					for (Resource e : ra) {
						if (e.getResource() != null) {
							resp.getOutputStream().write(e.getResource(), 0, e.getLength());
						}
					}
					
					return;
				}
			} else {
				res = Resource.get(entity);
				
				if (res != null && res.getResource() != null) {
					setContentHeaders(resp, res);
					resp.getOutputStream().write(res.getResource(), 0, res.getLength());
					return;
				}
			}

			HTTPServletHelp.error(resp, 404, "Entity not found.");
		} catch (Exception ex) {
			HTTPServletHelp.error(resp, ex);
		}
	}

	private void setContentHeaders(HttpServletResponse resp, Resource res) {
		String charSet = "UTF-8";
		if (res.getEncoding() != null)
			charSet = res.getEncoding();
		
		String contentType = "text/plain";
		if (res.getType() != null)
			contentType = res.getType();

		resp.setContentType(contentType);
		
		resp.setCharacterEncoding(charSet);
		
		resp.setStatus(200);
	}


	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		try {
			String entity = Check(req, resp, "Write");
			if (entity == null) return;

			int code;
			if (entity.endsWith("/")) {
				code = Resource.deletePath(entity);
			} else {
				code = Resource.delete(entity);
			}

			if (code == 200)
				resp.setStatus(code);
			else
				HTTPServletHelp.error(resp, code, "Entity not found.");
			
		} catch (Exception ex) {
			HTTPServletHelp.error(resp, ex);
		}
	}


	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		try {
			String entity = Check(req, resp, "Write");
			if (entity == null) return;

			if (entity.endsWith("/")) {
				HTTPServletHelp.error(resp, 400, "Bad request, no entity specified.");
				return;
			}

			Resource r = new Resource(entity, req.getContentType(), req.getCharacterEncoding(), HTTPServletHelp.getByteInput(req));
			int code = r.put();
			resp.setStatus(code);
		} catch (Exception ex) {
			HTTPServletHelp.error(resp, ex);
		}
	}

}
