package com.georgemcarlson.sianameservice.servlet;

import com.dosse.upnp.UPnP;
import com.georgemcarlson.sianameservice.servlet.api.SnsProxy;
import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

//https://www.baeldung.com/jetty-embedded
public class SiaNameServiceServer {
    private static final Logger LOGGER = Logger.getInstance();
    private final int port;
    private Server server=null;

    private SiaNameServiceServer(int port){
        this.port = port;
    }
    
    public static SiaNameServiceServer getInstance(int port){
        return new SiaNameServiceServer(port);
    }

    public void terminate() {
        try {
            LOGGER.info("Portal server is shutting down.");
            if(server!=null){
                server.stop();
                server = null;
            }
            if (Settings.PUBLIC && Settings.ENABLED_UPNP) {
                UPnP.closePortTCP(port);
            }
            LOGGER.info("Portal server has been shut down.");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        LOGGER.info("Portal server is starting.");

        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;

        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(getPort());
        server.setConnectors(new Connector[] { connector });

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(SnsProxy.class, "/*");

        try {
            server.start();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }

        if (Settings.PUBLIC && Settings.ENABLED_UPNP) {
            UPnP.openPortTCP(port);
        }
    }

    public int getPort() {
        return port;
    }

}
