// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.mail;

import java.net.InetAddress;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.Enumeration;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class MailMessage
{
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 25;
    private String host;
    private int port;
    private String from;
    private Vector replyto;
    private Vector to;
    private Vector cc;
    private Vector headersKeys;
    private Vector headersValues;
    private MailPrintStream out;
    private SmtpResponseReader in;
    private Socket socket;
    private static final int OK_READY = 220;
    private static final int OK_HELO = 250;
    private static final int OK_FROM = 250;
    private static final int OK_RCPT_1 = 250;
    private static final int OK_RCPT_2 = 251;
    private static final int OK_DATA = 354;
    private static final int OK_DOT = 250;
    private static final int OK_QUIT = 221;
    
    public MailMessage() throws IOException {
        this("localhost", 25);
    }
    
    public MailMessage(final String host) throws IOException {
        this(host, 25);
    }
    
    public MailMessage(final String host, final int port) throws IOException {
        this.port = 25;
        this.port = port;
        this.host = host;
        this.replyto = new Vector();
        this.to = new Vector();
        this.cc = new Vector();
        this.headersKeys = new Vector();
        this.headersValues = new Vector();
        this.connect();
        this.sendHelo();
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void from(final String from) throws IOException {
        this.sendFrom(from);
        this.from = from;
    }
    
    public void replyto(final String rto) {
        this.replyto.addElement(rto);
    }
    
    public void to(final String to) throws IOException {
        this.sendRcpt(to);
        this.to.addElement(to);
    }
    
    public void cc(final String cc) throws IOException {
        this.sendRcpt(cc);
        this.cc.addElement(cc);
    }
    
    public void bcc(final String bcc) throws IOException {
        this.sendRcpt(bcc);
    }
    
    public void setSubject(final String subj) {
        this.setHeader("Subject", subj);
    }
    
    public void setHeader(final String name, final String value) {
        this.headersKeys.add(name);
        this.headersValues.add(value);
    }
    
    public PrintStream getPrintStream() throws IOException {
        this.setFromHeader();
        this.setReplyToHeader();
        this.setToHeader();
        this.setCcHeader();
        this.setHeader("X-Mailer", "org.apache.tools.mail.MailMessage (ant.apache.org)");
        this.sendData();
        this.flushHeaders();
        return this.out;
    }
    
    void setFromHeader() {
        this.setHeader("From", this.from);
    }
    
    void setReplyToHeader() {
        if (!this.replyto.isEmpty()) {
            this.setHeader("Reply-To", this.vectorToList(this.replyto));
        }
    }
    
    void setToHeader() {
        if (!this.to.isEmpty()) {
            this.setHeader("To", this.vectorToList(this.to));
        }
    }
    
    void setCcHeader() {
        if (!this.cc.isEmpty()) {
            this.setHeader("Cc", this.vectorToList(this.cc));
        }
    }
    
    String vectorToList(final Vector v) {
        final StringBuffer buf = new StringBuffer();
        final Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            buf.append(e.nextElement());
            if (e.hasMoreElements()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
    
    void flushHeaders() throws IOException {
        for (int size = this.headersKeys.size(), i = 0; i < size; ++i) {
            final String name = this.headersKeys.elementAt(i);
            final String value = this.headersValues.elementAt(i);
            this.out.println(name + ": " + value);
        }
        this.out.println();
        this.out.flush();
    }
    
    public void sendAndClose() throws IOException {
        try {
            this.sendDot();
            this.sendQuit();
        }
        finally {
            this.disconnect();
        }
    }
    
    static String sanitizeAddress(final String s) {
        int paramDepth = 0;
        int start = 0;
        int end = 0;
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if (c == '(') {
                ++paramDepth;
                if (start == 0) {
                    end = i;
                }
            }
            else if (c == ')') {
                --paramDepth;
                if (end == 0) {
                    start = i + 1;
                }
            }
            else if (paramDepth == 0 && c == '<') {
                start = i + 1;
            }
            else if (paramDepth == 0 && c == '>') {
                end = i;
            }
        }
        if (end == 0) {
            end = len;
        }
        return s.substring(start, end);
    }
    
    void connect() throws IOException {
        this.socket = new Socket(this.host, this.port);
        this.out = new MailPrintStream(new BufferedOutputStream(this.socket.getOutputStream()));
        this.in = new SmtpResponseReader(this.socket.getInputStream());
        this.getReady();
    }
    
    void getReady() throws IOException {
        final String response = this.in.getResponse();
        final int[] ok = { 220 };
        if (!this.isResponseOK(response, ok)) {
            throw new IOException("Didn't get introduction from server: " + response);
        }
    }
    
    void sendHelo() throws IOException {
        final String local = InetAddress.getLocalHost().getHostName();
        final int[] ok = { 250 };
        this.send("HELO " + local, ok);
    }
    
    void sendFrom(final String from) throws IOException {
        final int[] ok = { 250 };
        this.send("MAIL FROM: <" + sanitizeAddress(from) + ">", ok);
    }
    
    void sendRcpt(final String rcpt) throws IOException {
        final int[] ok = { 250, 251 };
        this.send("RCPT TO: <" + sanitizeAddress(rcpt) + ">", ok);
    }
    
    void sendData() throws IOException {
        final int[] ok = { 354 };
        this.send("DATA", ok);
    }
    
    void sendDot() throws IOException {
        final int[] ok = { 250 };
        this.send("\r\n.", ok);
    }
    
    void sendQuit() throws IOException {
        final int[] ok = { 221 };
        try {
            this.send("QUIT", ok);
        }
        catch (IOException e) {
            throw new ErrorInQuitException(e);
        }
    }
    
    void send(final String msg, final int[] ok) throws IOException {
        this.out.rawPrint(msg + "\r\n");
        final String response = this.in.getResponse();
        if (!this.isResponseOK(response, ok)) {
            throw new IOException("Unexpected reply to command: " + msg + ": " + response);
        }
    }
    
    boolean isResponseOK(final String response, final int[] ok) {
        for (int i = 0; i < ok.length; ++i) {
            if (response.startsWith("" + ok[i])) {
                return true;
            }
        }
        return false;
    }
    
    void disconnect() throws IOException {
        if (this.out != null) {
            this.out.close();
        }
        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException ex) {}
        }
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException ex2) {}
        }
    }
}
