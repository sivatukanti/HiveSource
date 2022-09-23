// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class UnixTerminal extends Terminal
{
    public static final short ARROW_START = 27;
    public static final short ARROW_PREFIX = 91;
    public static final short ARROW_LEFT = 68;
    public static final short ARROW_RIGHT = 67;
    public static final short ARROW_UP = 65;
    public static final short ARROW_DOWN = 66;
    public static final short O_PREFIX = 79;
    public static final short HOME_CODE = 72;
    public static final short END_CODE = 70;
    public static final short DEL_THIRD = 51;
    public static final short DEL_SECOND = 126;
    private Map terminfo;
    private boolean echoEnabled;
    private String ttyConfig;
    private boolean backspaceDeleteSwitched;
    private static String sttyCommand;
    String encoding;
    ReplayPrefixOneCharInputStream replayStream;
    InputStreamReader replayReader;
    
    public UnixTerminal() {
        this.backspaceDeleteSwitched = false;
        this.encoding = System.getProperty("input.encoding", "UTF-8");
        this.replayStream = new ReplayPrefixOneCharInputStream(this.encoding);
        try {
            this.replayReader = new InputStreamReader(this.replayStream, this.encoding);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void checkBackspace() {
        final String[] ttyConfigSplit = this.ttyConfig.split(":|=");
        if (ttyConfigSplit.length < 7) {
            return;
        }
        if (ttyConfigSplit[6] == null) {
            return;
        }
        this.backspaceDeleteSwitched = ttyConfigSplit[6].equals("7f");
    }
    
    public void initializeTerminal() throws IOException, InterruptedException {
        this.ttyConfig = stty("-g");
        if (this.ttyConfig.length() == 0 || (this.ttyConfig.indexOf("=") == -1 && this.ttyConfig.indexOf(":") == -1)) {
            throw new IOException("Unrecognized stty code: " + this.ttyConfig);
        }
        this.checkBackspace();
        stty("-icanon min 1");
        stty("-echo");
        this.echoEnabled = false;
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void start() {
                    try {
                        UnixTerminal.this.restoreTerminal();
                    }
                    catch (Exception e) {
                        UnixTerminal.this.consumeException(e);
                    }
                }
            });
        }
        catch (AbstractMethodError ame) {
            this.consumeException(ame);
        }
    }
    
    public void restoreTerminal() throws Exception {
        if (this.ttyConfig != null) {
            stty(this.ttyConfig);
            this.ttyConfig = null;
        }
        resetTerminal();
    }
    
    public int readVirtualKey(final InputStream in) throws IOException {
        int c = this.readCharacter(in);
        if (this.backspaceDeleteSwitched) {
            if (c == 127) {
                c = 8;
            }
            else if (c == 8) {
                c = 127;
            }
        }
        if (c == 27) {
            while (c == 27) {
                c = this.readCharacter(in);
            }
            if (c == 91 || c == 79) {
                c = this.readCharacter(in);
                if (c == 65) {
                    return 16;
                }
                if (c == 66) {
                    return 14;
                }
                if (c == 68) {
                    return 2;
                }
                if (c == 67) {
                    return 6;
                }
                if (c == 72) {
                    return 1;
                }
                if (c == 70) {
                    return 5;
                }
                if (c == 51) {
                    c = this.readCharacter(in);
                    return 127;
                }
            }
        }
        if (c > 128) {
            this.replayStream.setInput(c, in);
            c = this.replayReader.read();
        }
        return c;
    }
    
    private void consumeException(final Throwable e) {
    }
    
    public boolean isSupported() {
        return true;
    }
    
    public boolean getEcho() {
        return false;
    }
    
    public int getTerminalWidth() {
        int val = -1;
        try {
            val = getTerminalProperty("columns");
        }
        catch (Exception ex) {}
        if (val == -1) {
            val = 80;
        }
        return val;
    }
    
    public int getTerminalHeight() {
        int val = -1;
        try {
            val = getTerminalProperty("rows");
        }
        catch (Exception ex) {}
        if (val == -1) {
            val = 24;
        }
        return val;
    }
    
    private static int getTerminalProperty(final String prop) throws IOException, InterruptedException {
        final String props = stty("-a");
        final StringTokenizer tok = new StringTokenizer(props, ";\n");
        while (tok.hasMoreTokens()) {
            final String str = tok.nextToken().trim();
            if (str.startsWith(prop)) {
                final int index = str.lastIndexOf(" ");
                return Integer.parseInt(str.substring(index).trim());
            }
            if (str.endsWith(prop)) {
                final int index = str.indexOf(" ");
                return Integer.parseInt(str.substring(0, index).trim());
            }
        }
        return -1;
    }
    
    private static String stty(final String args) throws IOException, InterruptedException {
        return exec("stty " + args + " < /dev/tty").trim();
    }
    
    private static String exec(final String cmd) throws IOException, InterruptedException {
        return exec(new String[] { "sh", "-c", cmd });
    }
    
    private static String exec(final String[] cmd) throws IOException, InterruptedException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final Process p = Runtime.getRuntime().exec(cmd);
        InputStream in = p.getInputStream();
        int c;
        while ((c = in.read()) != -1) {
            bout.write(c);
        }
        in = p.getErrorStream();
        while ((c = in.read()) != -1) {
            bout.write(c);
        }
        p.waitFor();
        final String result = new String(bout.toByteArray());
        return result;
    }
    
    public static void setSttyCommand(final String cmd) {
        UnixTerminal.sttyCommand = cmd;
    }
    
    public static String getSttyCommand() {
        return UnixTerminal.sttyCommand;
    }
    
    public synchronized boolean isEchoEnabled() {
        return this.echoEnabled;
    }
    
    public synchronized void enableEcho() {
        try {
            stty("echo");
            this.echoEnabled = true;
        }
        catch (Exception e) {
            this.consumeException(e);
        }
    }
    
    public synchronized void disableEcho() {
        try {
            stty("-echo");
            this.echoEnabled = false;
        }
        catch (Exception e) {
            this.consumeException(e);
        }
    }
    
    static {
        UnixTerminal.sttyCommand = System.getProperty("jline.sttyCommand", "stty");
    }
    
    static class ReplayPrefixOneCharInputStream extends InputStream
    {
        byte firstByte;
        int byteLength;
        InputStream wrappedStream;
        int byteRead;
        final String encoding;
        
        public ReplayPrefixOneCharInputStream(final String encoding) {
            this.encoding = encoding;
        }
        
        public void setInput(final int recorded, final InputStream wrapped) throws IOException {
            this.byteRead = 0;
            this.firstByte = (byte)recorded;
            this.wrappedStream = wrapped;
            this.byteLength = 1;
            if (this.encoding.equalsIgnoreCase("UTF-8")) {
                this.setInputUTF8(recorded, wrapped);
            }
            else if (this.encoding.equalsIgnoreCase("UTF-16")) {
                this.byteLength = 2;
            }
            else if (this.encoding.equalsIgnoreCase("UTF-32")) {
                this.byteLength = 4;
            }
        }
        
        public void setInputUTF8(final int recorded, final InputStream wrapped) throws IOException {
            if ((this.firstByte & 0xFFFFFFE0) == 0xFFFFFFC0) {
                this.byteLength = 2;
            }
            else if ((this.firstByte & 0xFFFFFFF0) == 0xFFFFFFE0) {
                this.byteLength = 3;
            }
            else {
                if ((this.firstByte & 0xFFFFFFF8) != 0xFFFFFFF0) {
                    throw new IOException("invalid UTF-8 first byte: " + this.firstByte);
                }
                this.byteLength = 4;
            }
        }
        
        public int read() throws IOException {
            if (this.available() == 0) {
                return -1;
            }
            ++this.byteRead;
            if (this.byteRead == 1) {
                return this.firstByte;
            }
            return this.wrappedStream.read();
        }
        
        public int available() {
            return this.byteLength - this.byteRead;
        }
    }
}
