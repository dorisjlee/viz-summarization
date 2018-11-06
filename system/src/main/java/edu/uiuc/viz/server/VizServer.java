package edu.uiuc.viz.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import edu.uiuc.viz.server.Servlet;

public class VizServer {

	public static void main(String[] args) throws Exception {

		Server server = new Server(6060);
		//ServletContextHandler handler = new ServletContextHandler(server, "/");
		//handler.addServlet(Servlet.class, "/");
		WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("viz.war");
        webapp.setParentLoaderPriority(true);
        webapp.setServer(server);
        webapp.setClassLoader(ClassLoader.getSystemClassLoader());
        webapp.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webapp);	
        
		server.start();
		server.join();
	}

}