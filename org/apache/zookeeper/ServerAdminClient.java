// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ServerAdminClient
{
    private static final Logger LOG;
    
    private static long getMask(final String mask) {
        long retv = 0L;
        if (mask.equalsIgnoreCase("CLIENT_REQUEST_TRACE_MASK")) {
            retv = 2L;
        }
        else if (mask.equalsIgnoreCase("CLIENT_DATA_PACKET_TRACE_MASK")) {
            retv = 4L;
        }
        else if (mask.equalsIgnoreCase("CLIENT_PING_TRACE_MASK")) {
            retv = 8L;
        }
        else if (mask.equalsIgnoreCase("SERVER_PACKET_TRACE_MASK")) {
            retv = 16L;
        }
        else if (mask.equalsIgnoreCase("SESSION_TRACE_MASK")) {
            retv = 32L;
        }
        else if (mask.equalsIgnoreCase("EVENT_DELIVERY_TRACE_MASK")) {
            retv = 64L;
        }
        else if (mask.equalsIgnoreCase("SERVER_PING_TRACE_MASK")) {
            retv = 128L;
        }
        else if (mask.equalsIgnoreCase("WARNING_TRACE_MASK")) {
            retv = 256L;
        }
        return retv;
    }
    
    private static long getMasks(final String masks) {
        long retv = 0L;
        final StringTokenizer st = new StringTokenizer(masks, "|");
        while (st.hasMoreTokens()) {
            final String mask = st.nextToken().trim();
            retv |= getMask(mask);
        }
        return retv;
    }
    
    public static void ruok(final String host, final int port) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[4];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            req.putInt(ByteBuffer.wrap("ruok".getBytes()).getInt());
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[4];
            final int rc = is.read(resBytes);
            final String retv = new String(resBytes);
            System.out.println("rc=" + rc + " retv=" + retv);
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    public static void dump(final String host, final int port) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[4];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            req.putInt(ByteBuffer.wrap("dump".getBytes()).getInt());
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[1024];
            final int rc = is.read(resBytes);
            final String retv = new String(resBytes);
            System.out.println("rc=" + rc + " retv=" + retv);
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    public static void stat(final String host, final int port) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[4];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            req.putInt(ByteBuffer.wrap("stat".getBytes()).getInt());
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[1024];
            final int rc = is.read(resBytes);
            final String retv = new String(resBytes);
            System.out.println("rc=" + rc + " retv=" + retv);
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    public static void kill(final String host, final int port) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[4];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            req.putInt(ByteBuffer.wrap("kill".getBytes()).getInt());
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[4];
            final int rc = is.read(resBytes);
            final String retv = new String(resBytes);
            System.out.println("rc=" + rc + " retv=" + retv);
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    public static void setTraceMask(final String host, final int port, final String traceMaskStr) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[12];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            final long traceMask = Long.parseLong(traceMaskStr, 8);
            req.putInt(ByteBuffer.wrap("stmk".getBytes()).getInt());
            req.putLong(traceMask);
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[8];
            final int rc = is.read(resBytes);
            final ByteBuffer res = ByteBuffer.wrap(resBytes);
            final long retv = res.getLong();
            System.out.println("rc=" + rc + " retv=0" + Long.toOctalString(retv) + " masks=0" + Long.toOctalString(traceMask));
            assert retv == traceMask;
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    public static void getTraceMask(final String host, final int port) {
        Socket s = null;
        try {
            final byte[] reqBytes = new byte[12];
            final ByteBuffer req = ByteBuffer.wrap(reqBytes);
            req.putInt(ByteBuffer.wrap("gtmk".getBytes()).getInt());
            s = new Socket();
            s.setSoLinger(false, 10);
            s.setSoTimeout(20000);
            s.connect(new InetSocketAddress(host, port));
            final InputStream is = s.getInputStream();
            final OutputStream os = s.getOutputStream();
            os.write(reqBytes);
            final byte[] resBytes = new byte[8];
            final int rc = is.read(resBytes);
            final ByteBuffer res = ByteBuffer.wrap(resBytes);
            final long retv = res.getLong();
            System.out.println("rc=" + rc + " retv=0" + Long.toOctalString(retv));
        }
        catch (IOException e) {
            ServerAdminClient.LOG.warn("Unexpected exception", e);
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e);
                }
            }
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e2) {
                    ServerAdminClient.LOG.warn("Unexpected exception", e2);
                }
            }
        }
    }
    
    private static void usage() {
        System.out.println("usage: java [-cp CLASSPATH] org.apache.zookeeper.ServerAdminClient host port op (ruok|stat|dump|kill|gettracemask|settracemask) [arguments]");
    }
    
    public static void main(final String[] args) {
        if (args.length < 3) {
            usage();
            return;
        }
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String op = args[2];
        if (op.equalsIgnoreCase("gettracemask")) {
            getTraceMask(host, port);
        }
        else if (op.equalsIgnoreCase("settracemask")) {
            setTraceMask(host, port, args[3]);
        }
        else if (op.equalsIgnoreCase("ruok")) {
            ruok(host, port);
        }
        else if (op.equalsIgnoreCase("kill")) {
            kill(host, port);
        }
        else if (op.equalsIgnoreCase("stat")) {
            stat(host, port);
        }
        else if (op.equalsIgnoreCase("dump")) {
            dump(host, port);
        }
        else {
            System.out.println("Unrecognized op: " + op);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ServerAdminClient.class);
    }
}
