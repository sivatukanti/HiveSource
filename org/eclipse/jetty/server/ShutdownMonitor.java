// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.util.component.Destroyable;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.net.Socket;
import java.io.Closeable;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.ShutdownThread;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.eclipse.jetty.util.component.LifeCycle;
import java.util.Set;

public class ShutdownMonitor
{
    private final Set<LifeCycle> _lifeCycles;
    private boolean debug;
    private final String host;
    private int port;
    private String key;
    private boolean exitVm;
    private boolean alive;
    
    public static ShutdownMonitor getInstance() {
        return Holder.instance;
    }
    
    protected static void reset() {
        Holder.instance = new ShutdownMonitor();
    }
    
    public static void register(final LifeCycle... lifeCycles) {
        getInstance().addLifeCycles(lifeCycles);
    }
    
    public static void deregister(final LifeCycle lifeCycle) {
        getInstance().removeLifeCycle(lifeCycle);
    }
    
    public static boolean isRegistered(final LifeCycle lifeCycle) {
        return getInstance().containsLifeCycle(lifeCycle);
    }
    
    private ShutdownMonitor() {
        this._lifeCycles = new LinkedHashSet<LifeCycle>();
        this.debug = (System.getProperty("DEBUG") != null);
        this.host = System.getProperty("STOP.HOST", "127.0.0.1");
        this.port = Integer.parseInt(System.getProperty("STOP.PORT", "-1"));
        this.key = System.getProperty("STOP.KEY", null);
        this.exitVm = true;
    }
    
    private void addLifeCycles(final LifeCycle... lifeCycles) {
        synchronized (this) {
            this._lifeCycles.addAll(Arrays.asList(lifeCycles));
        }
    }
    
    private void removeLifeCycle(final LifeCycle lifeCycle) {
        synchronized (this) {
            this._lifeCycles.remove(lifeCycle);
        }
    }
    
    private boolean containsLifeCycle(final LifeCycle lifeCycle) {
        synchronized (this) {
            return this._lifeCycles.contains(lifeCycle);
        }
    }
    
    private void debug(final String format, final Object... args) {
        if (this.debug) {
            System.err.printf("[ShutdownMonitor] " + format + "%n", args);
        }
    }
    
    private void debug(final Throwable t) {
        if (this.debug) {
            t.printStackTrace(System.err);
        }
    }
    
    public String getKey() {
        synchronized (this) {
            return this.key;
        }
    }
    
    public int getPort() {
        synchronized (this) {
            return this.port;
        }
    }
    
    public boolean isExitVm() {
        synchronized (this) {
            return this.exitVm;
        }
    }
    
    public void setDebug(final boolean flag) {
        this.debug = flag;
    }
    
    public void setExitVm(final boolean exitVm) {
        synchronized (this) {
            if (this.alive) {
                throw new IllegalStateException("ShutdownMonitor already started");
            }
            this.exitVm = exitVm;
        }
    }
    
    public void setKey(final String key) {
        synchronized (this) {
            if (this.alive) {
                throw new IllegalStateException("ShutdownMonitor already started");
            }
            this.key = key;
        }
    }
    
    public void setPort(final int port) {
        synchronized (this) {
            if (this.alive) {
                throw new IllegalStateException("ShutdownMonitor already started");
            }
            this.port = port;
        }
    }
    
    protected void start() throws Exception {
        synchronized (this) {
            if (this.alive) {
                this.debug("Already started", new Object[0]);
                return;
            }
            final ServerSocket serverSocket = this.listen();
            if (serverSocket != null) {
                this.alive = true;
                final Thread thread = new Thread(new ShutdownMonitorRunnable(serverSocket));
                thread.setDaemon(true);
                thread.setName("ShutdownMonitor");
                thread.start();
            }
        }
    }
    
    private void stop() {
        synchronized (this) {
            this.alive = false;
            this.notifyAll();
        }
    }
    
    void await() throws InterruptedException {
        synchronized (this) {
            while (this.alive) {
                this.wait();
            }
        }
    }
    
    protected boolean isAlive() {
        synchronized (this) {
            return this.alive;
        }
    }
    
    private ServerSocket listen() {
        int port = this.getPort();
        if (port < 0) {
            this.debug("Not enabled (port < 0): %d", port);
            return null;
        }
        String key = this.getKey();
        try {
            final ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(this.host), port));
            if (port == 0) {
                port = serverSocket.getLocalPort();
                System.out.printf("STOP.PORT=%d%n", port);
                this.setPort(port);
            }
            if (key == null) {
                key = Long.toString((long)(9.223372036854776E18 * Math.random() + this.hashCode() + System.currentTimeMillis()), 36);
                System.out.printf("STOP.KEY=%s%n", key);
                this.setKey(key);
            }
            return serverSocket;
        }
        catch (Throwable x) {
            this.debug(x);
            System.err.println("Error binding ShutdownMonitor to port " + port + ": " + x.toString());
            return null;
        }
        finally {
            this.debug("STOP.PORT=%d", port);
            this.debug("STOP.KEY=%s", key);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s[port=%d,alive=%b]", this.getClass().getName(), this.getPort(), this.isAlive());
    }
    
    private static class Holder
    {
        static ShutdownMonitor instance;
        
        static {
            Holder.instance = new ShutdownMonitor(null);
        }
    }
    
    private class ShutdownMonitorRunnable implements Runnable
    {
        private final ServerSocket serverSocket;
        
        private ShutdownMonitorRunnable(final ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        
        @Override
        public void run() {
            ShutdownMonitor.this.debug("Started", new Object[0]);
            try {
                final String key = ShutdownMonitor.this.getKey();
            Label_0021_Outer:
                while (true) {
                    while (true) {
                        try {
                            while (true) {
                                try (final Socket socket = this.serverSocket.accept()) {
                                    final LineNumberReader reader = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                                    final String receivedKey = reader.readLine();
                                    if (!key.equals(receivedKey)) {
                                        ShutdownMonitor.this.debug("Ignoring command with incorrect key: %s", receivedKey);
                                    }
                                    else {
                                        final String cmd = reader.readLine();
                                        ShutdownMonitor.this.debug("command=%s", cmd);
                                        final OutputStream out = socket.getOutputStream();
                                        final boolean exitVm = ShutdownMonitor.this.isExitVm();
                                        if ("stop".equalsIgnoreCase(cmd)) {
                                            ShutdownMonitor.this.debug("Performing stop command", new Object[0]);
                                            this.stopLifeCycles(ShutdownThread::isRegistered, exitVm);
                                            ShutdownMonitor.this.debug("Informing client that we are stopped", new Object[0]);
                                            this.informClient(out, "Stopped\r\n");
                                            if (!exitVm) {
                                                break Label_0021_Outer;
                                            }
                                            ShutdownMonitor.this.debug("Killing JVM", new Object[0]);
                                            System.exit(0);
                                        }
                                        else if ("forcestop".equalsIgnoreCase(cmd)) {
                                            ShutdownMonitor.this.debug("Performing forced stop command", new Object[0]);
                                            this.stopLifeCycles(l -> true, exitVm);
                                            ShutdownMonitor.this.debug("Informing client that we are stopped", new Object[0]);
                                            this.informClient(out, "Stopped\r\n");
                                            if (!exitVm) {
                                                break Label_0021_Outer;
                                            }
                                            ShutdownMonitor.this.debug("Killing JVM", new Object[0]);
                                            System.exit(0);
                                        }
                                        else if ("stopexit".equalsIgnoreCase(cmd)) {
                                            ShutdownMonitor.this.debug("Performing stop and exit commands", new Object[0]);
                                            this.stopLifeCycles(ShutdownThread::isRegistered, true);
                                            ShutdownMonitor.this.debug("Informing client that we are stopped", new Object[0]);
                                            this.informClient(out, "Stopped\r\n");
                                            ShutdownMonitor.this.debug("Killing JVM", new Object[0]);
                                            System.exit(0);
                                        }
                                        else if ("exit".equalsIgnoreCase(cmd)) {
                                            ShutdownMonitor.this.debug("Killing JVM", new Object[0]);
                                            System.exit(0);
                                        }
                                        else if ("status".equalsIgnoreCase(cmd)) {
                                            this.informClient(out, "OK\r\n");
                                        }
                                    }
                                }
                            }
                        }
                        catch (Throwable x) {
                            ShutdownMonitor.this.debug(x);
                            continue Label_0021_Outer;
                        }
                        continue;
                    }
                }
            }
            catch (Throwable x2) {
                ShutdownMonitor.this.debug(x2);
            }
            finally {
                IO.close(this.serverSocket);
                ShutdownMonitor.this.stop();
                ShutdownMonitor.this.debug("Stopped", new Object[0]);
            }
        }
        
        private void informClient(final OutputStream out, final String message) throws IOException {
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
        
        private void stopLifeCycles(final Predicate<LifeCycle> predicate, final boolean destroy) {
            final List<LifeCycle> lifeCycles = new ArrayList<LifeCycle>();
            synchronized (this) {
                lifeCycles.addAll(ShutdownMonitor.this._lifeCycles);
            }
            for (final LifeCycle l : lifeCycles) {
                try {
                    if (l.isStarted() && predicate.test(l)) {
                        l.stop();
                    }
                    if (!(l instanceof Destroyable) || !destroy) {
                        continue;
                    }
                    ((Destroyable)l).destroy();
                }
                catch (Throwable x) {
                    ShutdownMonitor.this.debug(x);
                }
            }
        }
    }
}
