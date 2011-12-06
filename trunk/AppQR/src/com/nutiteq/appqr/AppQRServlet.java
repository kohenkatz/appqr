package com.nutiteq.appqr;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

@SuppressWarnings("serial")
public class AppQRServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AppQRServlet.class.getName());

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String andUrl = req.getParameter("androidmarket");
		String appleUrl = req.getParameter("appstore");
		String qr = req.getParameter("qr");
		String ua = req.getHeader("User-Agent");

		if(qr != null && qr.equals("1")){
			// request from mobile - do download, forward to correct url
			if(ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")){
				log.info("forwarding to "+appleUrl);
				resp.sendRedirect(resp.encodeRedirectURL(appleUrl));
			}else if(ua.contains("Android")){
				log.info("forwarding to "+andUrl);
				resp.sendRedirect(resp.encodeRedirectURL(andUrl));
			}else{
				log.info("cannot forward, giving html with "+andUrl+" and "+appleUrl);
				
				resp.setContentType("text/html");
				resp.getWriter().println("Could not detect your phone. Following are links for application download:<BR/><ul>");
				resp.getWriter().println("<li>Apple Appstore, for iPhone/iPad/iPod Touch: <a href=\""+appleUrl+"\">download</a></li>");
				resp.getWriter().println("<li>Google Market, Android devices: <a href=\""+andUrl+"\">download</a></li></ul>");
			}
		}else{
			// request from web - generate UNIVERSAL QR code URL
			resp.setContentType("text/html");
			StringBuffer baseUrl = new StringBuffer("http://chart.apis.google.com/chart?cht=qr&chs=350x350&chl=");
			StringBuffer selectorUrl = new StringBuffer("http://qrappdownload.appspot.com/appqr?qr=1");
			selectorUrl.append("&androidmarket=");
			selectorUrl.append(andUrl);
			selectorUrl.append("&appstore=");
			selectorUrl.append(appleUrl);
			log.info("selectorUrl before encoding ="+selectorUrl.toString());
			baseUrl.append(URLEncoder.encode(selectorUrl.toString(),"UTF-8"));
			resp.getWriter().println("URL = <a href=\""+(baseUrl.toString())+"\">"+baseUrl.toString()+"<br/>");
			resp.getWriter().println("<IMG SRC=\""+baseUrl.toString()+"\">");
			log.info("generated qr code url="+baseUrl.toString());
		}
	}
}
