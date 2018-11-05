package edu.uiuc.viz.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;



import edu.uiuc.viz.server.Servlet;

public class VizServer {

	public static void main(String[] args) throws Exception {

		Server server = new Server(6060);
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		handler.addServlet(Servlet.class, "/");
		server.start();

	}

}