// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.log.Log;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;

public class Main
{
    public static void main(final String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage - java org.mortbay.jetty.Main [<addr>:]<port>");
            System.err.println("Usage - java org.mortbay.jetty.Main [<addr>:]<port> docroot");
            System.err.println("Usage - java org.mortbay.jetty.Main [<addr>:]<port> -webapp myapp.war");
            System.err.println("Usage - java org.mortbay.jetty.Main [<addr>:]<port> -webapps webapps");
            System.err.println("Usage - java -jar jetty-x.x.x-standalone.jar [<addr>:]<port>");
            System.err.println("Usage - java -jar jetty-x.x.x-standalone.jar [<addr>:]<port> docroot");
            System.err.println("Usage - java -jar jetty-x.x.x-standalone.jar [<addr>:]<port> -webapp myapp.war");
            System.err.println("Usage - java -jar jetty-x.x.x-standalone.jar [<addr>:]<port> -webapps webapps");
            System.exit(1);
        }
        try {
            final Server server = new Server();
            final ContextHandlerCollection contexts = new ContextHandlerCollection();
            server.setHandler(contexts);
            final SocketConnector connector = new SocketConnector();
            final String address = args[0];
            final int colon = address.lastIndexOf(58);
            if (colon < 0) {
                connector.setPort(Integer.parseInt(address));
            }
            else {
                connector.setHost(address.substring(0, colon));
                connector.setPort(Integer.parseInt(address.substring(colon + 1)));
            }
            server.setConnectors(new Connector[] { connector });
            if (args.length < 3) {
                final ContextHandler context = new ContextHandler();
                context.setContextPath("/");
                context.setResourceBase((args.length == 1) ? "." : args[1]);
                final ServletHandler servlet = new ServletHandler();
                servlet.addServletWithMapping("org.mortbay.jetty.servlet.DefaultServlet", "/");
                context.setHandler(servlet);
                contexts.addHandler(context);
            }
            else if ("-webapps".equals(args[1])) {
                WebAppContext.addWebApplications(server, args[2], "org/mortbay/jetty/webapp/webdefault.xml", true, true);
            }
            else if ("-webapp".equals(args[1])) {
                final WebAppContext webapp = new WebAppContext();
                webapp.setWar(args[2]);
                webapp.setContextPath("/");
                contexts.addHandler(webapp);
            }
            server.start();
        }
        catch (Exception e) {
            Log.warn("EXCEPTION ", e);
        }
    }
}
