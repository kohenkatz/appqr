package com.nutiteq.appqr;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AppQRServlet extends HttpServlet {
	private static final String SHORT_SERVICE_URL = "http://is.gd/create.php?format=simple&url=";
	private static final String DOWNLOAD_APP_URL = "http://m.appqr.mobi/appqr?qr=1";
	private static final String CHART_API_URL = "http://chart.apis.google.com/chart?cht=qr&chs=350x350&chl=";

	private static final Logger log = Logger.getLogger(AppQRServlet.class.getName());

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO jaakl organize list of platforms better, with an array perhaps
		// TODO refactor magic strings
		
		String andUrl = req.getParameter("androidmarket");
		String appleUrl = req.getParameter("appstore");
		String wpUrl = req.getParameter("wp");
		String bbUrl = req.getParameter("bb");
		String symUrl = req.getParameter("sym");

		if(andUrl != null && andUrl.equals("")) andUrl = null;
		if(appleUrl != null && appleUrl.equals("")) appleUrl = null;
		if(wpUrl != null && wpUrl.equals("")) wpUrl = null;
		if(bbUrl != null && bbUrl.equals("")) bbUrl = null;
		if(symUrl != null && symUrl.equals("")) symUrl = null;
		
		String qr = req.getParameter("qr");
		String ua = req.getHeader("User-Agent");

		if(qr != null && qr.equals("1")){
			// request from mobile - do download, forward to correct url
			log.info("user agent: "+ua);
			if((ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")) && appleUrl != null){
				log.info("forwarding to "+appleUrl);
				resp.sendRedirect(resp.encodeRedirectURL(appleUrl));
			}else if(ua.contains("Android") && andUrl != null){
				log.info("forwarding to "+andUrl);
				resp.sendRedirect(resp.encodeRedirectURL(andUrl));
			}else if(ua.contains("Windows Phone") && wpUrl != null){
				log.info("forwarding to "+wpUrl);
				resp.sendRedirect(resp.encodeRedirectURL(wpUrl));
			}else if(ua.contains("BlackBerry") && bbUrl != null){
				log.info("forwarding to "+bbUrl);
				resp.sendRedirect(resp.encodeRedirectURL(bbUrl));
			}else if(ua.contains("Symbian") && symUrl != null){
				log.info("forwarding to "+symUrl);
				resp.sendRedirect(resp.encodeRedirectURL(symUrl));
			}else{
				log.info("cannot forward, giving html with "+andUrl+" and "+appleUrl);
				
				resp.setContentType("text/html");
				resp.getWriter().println("Could not detect your phone or your platform is not supported. Following are links for application download:<BR/><ul>");
				if(appleUrl != null) resp.getWriter().println("<li>Apple iPhone/iPad/iPod Touch: <a href=\""+appleUrl+"\">download</a></li>");
				if(andUrl != null) resp.getWriter().println("<li>Android: <a href=\""+andUrl+"\">download</a></li>");
				if(wpUrl != null) resp.getWriter().println("<li>Windows Phone: <a href=\""+wpUrl+"\">download</a></li>");
				if(bbUrl != null) resp.getWriter().println("<li>BlackBerry: <a href=\""+bbUrl+"\">download</a></li>");
				if(symUrl != null) resp.getWriter().println("<li>Symbian: <a href=\""+symUrl+"\">download</a></li>");
				resp.getWriter().println("</ul>");

			}
		}else{
			// request from web - generate UNIVERSAL QR code URL
			resp.setContentType("text/html");
			StringBuffer baseUrl = new StringBuffer(CHART_API_URL);
			StringBuffer selectorUrl = new StringBuffer(DOWNLOAD_APP_URL);
			if(andUrl != null)
				selectorUrl.append("&androidmarket=").append(andUrl);
			if(appleUrl != null)
				selectorUrl.append("&appstore=").append(appleUrl);
			if(wpUrl != null)
				selectorUrl.append("&wp=").append(wpUrl);
			if(bbUrl != null)
				selectorUrl.append("&bb=").append(bbUrl);
			if(symUrl != null)
				selectorUrl.append("&sym=").append(symUrl);
			
			log.info("selectorUrl before encoding ="+selectorUrl.toString());
			baseUrl.append(URLEncoder.encode(selectorUrl.toString(),"UTF-8"));
			resp.getWriter().println("<h1>a) With independent long URL</h1>");
			resp.getWriter().println("<ol><li>QR Code image URL: <a href=\""+(baseUrl.toString())+"\">"+baseUrl.toString()+"</a>");
			resp.getWriter().println("<li>Image: <IMG SRC=\""+baseUrl.toString()+"\">");
			resp.getWriter().println("<li>Download URL (within QR Code): <a href=\""+(selectorUrl.toString())+"\">"+selectorUrl.toString()+"</a></ol>");
			
			// short version
			StringBuffer baseUrlShort = new StringBuffer(CHART_API_URL);
			String shortUrl = shorten(selectorUrl.toString());
			baseUrlShort.append(URLEncoder.encode(shortUrl.toString(),"UTF-8"));
			
			resp.getWriter().println("<h1>b) With short URL and faster image, using is.gd service</h1>");
			
			resp.getWriter().println("<ol><li>QR Code image URL: <a href=\""+(baseUrlShort.toString())+"\">"+baseUrlShort.toString()+"</a>");
			resp.getWriter().println("<li>Image: <IMG SRC=\""+baseUrlShort.toString()+"\">");
			resp.getWriter().println("<li>Download URL (within QR Code): <a href=\""+(shortUrl)+"\">"+shortUrl+"</a></ol>");
			
			log.info("generated qr code url="+baseUrl.toString());
			log.info("generated short qr code url="+baseUrlShort.toString());
			
		}
	}
	public String shorten(final String longUrl){
		String serviceUrl = null;
		try {
			serviceUrl = SHORT_SERVICE_URL+URLEncoder.encode(longUrl,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.info(e.getMessage());
			return null;

		}
		
   	    String shortUrl = null;
		try {
	            URL url = new URL(serviceUrl);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
	            String line;

	            // should never give more than 1 line
	            while ((line = reader.readLine()) != null) {
	                shortUrl = line;
	            }
	            reader.close();

	        } catch (MalformedURLException e) {
	            log.info(e.getMessage());
	        } catch (IOException e) {
	            log.info(e.getMessage());
	        }
		
		return shortUrl;
	}
}