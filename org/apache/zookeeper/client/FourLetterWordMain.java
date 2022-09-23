// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.client;

import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class FourLetterWordMain
{
    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;
    protected static final Logger LOG;
    
    public static String send4LetterWord(final String host, final int port, final String cmd) throws IOException {
        return send4LetterWord(host, port, cmd, 5000);
    }
    
    public static String send4LetterWord(final String host, final int port, final String cmd, final int timeout) throws IOException {
        FourLetterWordMain.LOG.info("connecting to " + host + " " + port);
        final Socket sock = new Socket();
        final InetSocketAddress hostaddress = (host != null) ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port);
        BufferedReader reader = null;
        try {
            sock.setSoTimeout(timeout);
            sock.connect(hostaddress, timeout);
            final OutputStream outstream = sock.getOutputStream();
            outstream.write(cmd.getBytes());
            outstream.flush();
            sock.shutdownOutput();
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }
        catch (SocketTimeoutException e) {
            throw new IOException("Exception while executing four letter word: " + cmd, e);
        }
        finally {
            sock.close();
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: FourLetterWordMain <host> <port> <cmd>");
        }
        else {
            System.out.println(send4LetterWord(args[0], Integer.parseInt(args[1]), args[2]));
        }
    }
    
    static {
        LOG = Logger.getLogger(FourLetterWordMain.class);
    }
}
