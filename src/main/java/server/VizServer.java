package server;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class VizServer {

	private Server server;
	private static int port;	
	
	static{
		port = 8080;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws Exception {	
		server = new Server(port);	
		
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("vizsummarization.war");
		webAppContext.setParentLoaderPriority(true);
		webAppContext.setServer(server);
		webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
		webAppContext.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webAppContext);	
		server.start();
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		VizServer vizServer = new VizServer();
		vizServer.start();	
	}	

}
