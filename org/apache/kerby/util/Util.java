// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.nio.charset.Charset;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TreeSet;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.security.KeyStore;

public class Util
{
    public static final int SIZE_KEY = 0;
    public static final int LAST_READ_KEY = 1;
    
    public static boolean equals(final KeyStore ks1, final KeyStore ks2) throws KeyStoreException {
        if (ks1 == null || ks2 == null) {
            return ks1 == null && ks2 == null;
        }
        final Set<String> aliases1 = aliases(ks1);
        final Set<String> aliases2 = aliases(ks2);
        if (aliases1.equals(aliases2)) {
            for (final String s : aliases1) {
                if (ks1.isCertificateEntry(s) != ks2.isCertificateEntry(s)) {
                    return false;
                }
                if (ks1.isKeyEntry(s) != ks2.isKeyEntry(s)) {
                    return false;
                }
                if (!ks1.isCertificateEntry(s)) {
                    continue;
                }
                final Certificate[] cc1 = ks1.getCertificateChain(s);
                final Certificate[] cc2 = ks2.getCertificateChain(s);
                if (!Arrays.equals(cc1, cc2)) {
                    return false;
                }
                final Certificate c1 = ks1.getCertificate(s);
                final Certificate c2 = ks2.getCertificate(s);
                if (!c1.equals(c2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static Set<String> aliases(final KeyStore ks) throws KeyStoreException {
        final Set<String> aliases = new TreeSet<String>();
        final Enumeration<String> en = ks.aliases();
        while (en.hasMoreElements()) {
            aliases.add(en.nextElement());
        }
        return aliases;
    }
    
    public static boolean isYes(final String yesString) {
        if (yesString == null) {
            return false;
        }
        final String s = yesString.trim().toUpperCase();
        return "1".equals(s) || "YES".equals(s) || "TRUE".equals(s) || "ENABLE".equals(s) || "ENABLED".equals(s) || "Y".equals(s) || "ON".equals(s);
    }
    
    public static String trim(final String s) {
        if (s == null || "".equals(s)) {
            return s;
        }
        int i = 0;
        int j = s.length() - 1;
        while (isWhiteSpace(s.charAt(i))) {
            ++i;
        }
        while (isWhiteSpace(s.charAt(j))) {
            --j;
        }
        return (j >= i) ? s.substring(i, j + 1) : "";
    }
    
    public static boolean isWhiteSpace(final char c) {
        switch (c) {
            case '\0':
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static void pipeStream(final InputStream in, final OutputStream out) throws IOException {
        pipeStream(in, out, true);
    }
    
    public static void pipeStream(final InputStream in, final OutputStream out, final boolean autoClose) throws IOException {
        final byte[] buf = new byte[8192];
        IOException ioe = null;
        try {
            for (int bytesRead = in.read(buf); bytesRead >= 0; bytesRead = in.read(buf)) {
                if (bytesRead > 0) {
                    out.write(buf, 0, bytesRead);
                }
            }
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                ioe = e;
            }
            if (autoClose) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    ioe = e;
                }
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }
    
    public static byte[] fileToBytes(final File f) throws IOException {
        return streamToBytes(Files.newInputStream(f.toPath(), new OpenOption[0]));
    }
    
    public static byte[] streamToBytes(final ByteArrayInputStream in, final int maxLength) {
        byte[] buf = new byte[maxLength];
        final int[] status = fill(buf, 0, in);
        final int size = status[0];
        if (buf.length != size) {
            final byte[] smallerBuf = new byte[size];
            System.arraycopy(buf, 0, smallerBuf, 0, size);
            buf = smallerBuf;
        }
        return buf;
    }
    
    public static byte[] streamToBytes(final InputStream in, final int maxLength) throws IOException {
        byte[] buf = new byte[maxLength];
        final int[] status = fill(buf, 0, in);
        final int size = status[0];
        if (buf.length != size) {
            final byte[] smallerBuf = new byte[size];
            System.arraycopy(buf, 0, smallerBuf, 0, size);
            buf = smallerBuf;
        }
        return buf;
    }
    
    public static byte[] streamToBytes(final InputStream in) throws IOException {
        byte[] buf = new byte[4096];
        try {
            int[] status = fill(buf, 0, in);
            int size = status[0];
            for (int lastRead = status[1]; lastRead != -1; lastRead = status[1]) {
                buf = resizeArray(buf);
                status = fill(buf, size, in);
                size = status[0];
            }
            if (buf.length != size) {
                final byte[] smallerBuf = new byte[size];
                System.arraycopy(buf, 0, smallerBuf, 0, size);
                buf = smallerBuf;
            }
        }
        finally {
            in.close();
        }
        return buf;
    }
    
    public static byte[] streamToBytes(final ByteArrayInputStream in) {
        byte[] buf = new byte[4096];
        int[] status = fill(buf, 0, in);
        int size = status[0];
        for (int lastRead = status[1]; lastRead != -1; lastRead = status[1]) {
            buf = resizeArray(buf);
            status = fill(buf, size, in);
            size = status[0];
        }
        if (buf.length != size) {
            final byte[] smallerBuf = new byte[size];
            System.arraycopy(buf, 0, smallerBuf, 0, size);
            buf = smallerBuf;
        }
        return buf;
    }
    
    public static int[] fill(final byte[] buf, final int offset, final InputStream in) throws IOException {
        int lastRead;
        int read = lastRead = in.read(buf, offset, buf.length - offset);
        if (read == -1) {
            read = 0;
        }
        while (lastRead != -1 && read + offset < buf.length) {
            lastRead = in.read(buf, offset + read, buf.length - read - offset);
            if (lastRead != -1) {
                read += lastRead;
            }
        }
        return new int[] { offset + read, lastRead };
    }
    
    public static int[] fill(final byte[] buf, final int offset, final ByteArrayInputStream in) {
        int lastRead;
        int read = lastRead = in.read(buf, offset, buf.length - offset);
        if (read == -1) {
            read = 0;
        }
        while (lastRead != -1 && read + offset < buf.length) {
            lastRead = in.read(buf, offset + read, buf.length - read - offset);
            if (lastRead != -1) {
                read += lastRead;
            }
        }
        return new int[] { offset + read, lastRead };
    }
    
    public static byte[] resizeArray(final byte[] bytes) {
        final byte[] biggerBytes = new byte[bytes.length * 2];
        System.arraycopy(bytes, 0, biggerBytes, 0, bytes.length);
        return biggerBytes;
    }
    
    public static String pad(String s, final int length, final boolean left) {
        if (s == null) {
            s = "";
        }
        final int diff = length - s.length();
        if (diff == 0) {
            return s;
        }
        if (diff > 0) {
            final StringBuilder sb = new StringBuilder();
            if (left) {
                for (int i = 0; i < diff; ++i) {
                    sb.append(' ');
                }
            }
            sb.append(s);
            if (!left) {
                for (int i = 0; i < diff; ++i) {
                    sb.append(' ');
                }
            }
            return sb.toString();
        }
        return s;
    }
    
    public static HostPort toAddress(final String target, final int defaultPort) throws UnknownHostException {
        String host = target;
        int port = defaultPort;
        final StringTokenizer st = new StringTokenizer(target, ":");
        if (st.hasMoreTokens()) {
            host = st.nextToken().trim();
        }
        if (st.hasMoreTokens()) {
            port = Integer.parseInt(st.nextToken().trim());
        }
        if (st.hasMoreTokens()) {
            throw new IllegalArgumentException("Invalid host: " + target);
        }
        return new HostPort(host, port);
    }
    
    public static String cipherToAuthType(final String cipher) {
        if (cipher == null) {
            return null;
        }
        final StringTokenizer st = new StringTokenizer(cipher.trim(), "_");
        if (st.hasMoreTokens()) {
            st.nextToken();
        }
        if (st.hasMoreTokens()) {
            String tok = st.nextToken();
            final StringBuilder buf = new StringBuilder();
            buf.append(tok);
            if (st.hasMoreTokens()) {
                for (tok = st.nextToken(); !"WITH".equalsIgnoreCase(tok); tok = st.nextToken()) {
                    buf.append('_');
                    buf.append(tok);
                }
            }
            return buf.toString();
        }
        throw new IllegalArgumentException("not a valid cipher: " + cipher);
    }
    
    public static InetAddress toInetAddress(final String s) throws UnknownHostException {
        byte[] ip = IPAddressParser.parseIPv4Literal(s);
        if (ip == null) {
            ip = IPAddressParser.parseIPv6Literal(s);
        }
        if (ip != null) {
            return InetAddress.getByAddress(s, ip);
        }
        return InetAddress.getByName(s);
    }
    
    public static void main(final String[] args) throws Exception {
        String s = "line1\n\rline2\n\rline3";
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));
        ByteArrayReadLine readLine = new ByteArrayReadLine(in);
        for (String line = readLine.next(); line != null; line = readLine.next()) {
            System.out.println(line);
        }
        System.out.println("--------- test 2 ----------");
        s = "line1\n\rline2\n\rline3\n\r\n\r";
        in = new ByteArrayInputStream(s.getBytes());
        readLine = new ByteArrayReadLine(in);
        for (String line = readLine.next(); line != null; line = readLine.next()) {
            System.out.println(line);
        }
    }
}
