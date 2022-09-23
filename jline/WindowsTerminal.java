// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WindowsTerminal extends Terminal
{
    private static final int ENABLE_LINE_INPUT = 2;
    private static final int ENABLE_ECHO_INPUT = 4;
    private static final int ENABLE_PROCESSED_INPUT = 1;
    private static final int ENABLE_WINDOW_INPUT = 8;
    private static final int ENABLE_MOUSE_INPUT = 16;
    private static final int ENABLE_PROCESSED_OUTPUT = 1;
    private static final int ENABLE_WRAP_AT_EOL_OUTPUT = 2;
    public static final int SPECIAL_KEY_INDICATOR = 224;
    public static final int NUMPAD_KEY_INDICATOR = 0;
    public static final int LEFT_ARROW_KEY = 75;
    public static final int RIGHT_ARROW_KEY = 77;
    public static final int UP_ARROW_KEY = 72;
    public static final int DOWN_ARROW_KEY = 80;
    public static final int DELETE_KEY = 83;
    public static final int HOME_KEY = 71;
    public static final char END_KEY = 'O';
    public static final char PAGE_UP_KEY = 'I';
    public static final char PAGE_DOWN_KEY = 'Q';
    public static final char INSERT_KEY = 'R';
    public static final char ESCAPE_KEY = '\0';
    private Boolean directConsole;
    private boolean echoEnabled;
    String encoding;
    ReplayPrefixOneCharInputStream replayStream;
    InputStreamReader replayReader;
    
    public WindowsTerminal() {
        this.encoding = System.getProperty("jline.WindowsTerminal.input.encoding", System.getProperty("file.encoding"));
        this.replayStream = new ReplayPrefixOneCharInputStream(this.encoding);
        final String dir = System.getProperty("jline.WindowsTerminal.directConsole");
        if ("true".equals(dir)) {
            this.directConsole = Boolean.TRUE;
        }
        else if ("false".equals(dir)) {
            this.directConsole = Boolean.FALSE;
        }
        try {
            this.replayReader = new InputStreamReader(this.replayStream, this.encoding);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private native int getConsoleMode();
    
    private native void setConsoleMode(final int p0);
    
    private native int readByte();
    
    private native int getWindowsTerminalWidth();
    
    private native int getWindowsTerminalHeight();
    
    public int readCharacter(final InputStream in) throws IOException {
        if (this.directConsole == Boolean.FALSE) {
            return super.readCharacter(in);
        }
        if (this.directConsole == Boolean.TRUE || in == System.in || (in instanceof FileInputStream && ((FileInputStream)in).getFD() == FileDescriptor.in)) {
            return this.readByte();
        }
        return super.readCharacter(in);
    }
    
    public void initializeTerminal() throws Exception {
        this.loadLibrary("jline");
        final int originalMode = this.getConsoleMode();
        this.setConsoleMode(originalMode & 0xFFFFFFFB);
        final int newMode = originalMode & 0xFFFFFFF0;
        this.echoEnabled = false;
        this.setConsoleMode(newMode);
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void start() {
                    WindowsTerminal.this.setConsoleMode(originalMode);
                }
            });
        }
        catch (AbstractMethodError ame) {
            this.consumeException(ame);
        }
    }
    
    private void loadLibrary(final String name) throws IOException {
        String version = this.getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "";
        }
        version = version.replace('.', '_');
        final File f = new File(System.getProperty("java.io.tmpdir"), name + "_" + version + ".dll");
        final boolean exists = f.isFile();
        int bits = 32;
        if (System.getProperty("os.arch").indexOf("64") != -1) {
            bits = 64;
        }
        final InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(name + bits + ".dll"));
        try {
            final OutputStream fout = new BufferedOutputStream(new FileOutputStream(f));
            final byte[] bytes = new byte[10240];
            for (int n = 0; n != -1; n = in.read(bytes)) {
                fout.write(bytes, 0, n);
            }
            fout.close();
        }
        catch (IOException ioe) {
            if (!exists) {
                throw ioe;
            }
        }
        f.deleteOnExit();
        System.load(f.getAbsolutePath());
    }
    
    public int readVirtualKey(final InputStream in) throws IOException {
        int indicator = this.readCharacter(in);
        if (indicator != 224 && indicator != 0) {
            if (indicator > 128) {
                this.replayStream.setInput(indicator, in);
                indicator = this.replayReader.read();
            }
            return indicator;
        }
        final int key = this.readCharacter(in);
        switch (key) {
            case 72: {
                return 16;
            }
            case 75: {
                return 2;
            }
            case 77: {
                return 6;
            }
            case 80: {
                return 14;
            }
            case 83: {
                return 127;
            }
            case 71: {
                return 1;
            }
            case 79: {
                return 5;
            }
            case 73: {
                return 11;
            }
            case 81: {
                return 12;
            }
            case 0: {
                return 27;
            }
            case 82: {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }
    
    public boolean isSupported() {
        return true;
    }
    
    public boolean isANSISupported() {
        return false;
    }
    
    public boolean getEcho() {
        return false;
    }
    
    public int getTerminalWidth() {
        return this.getWindowsTerminalWidth();
    }
    
    public int getTerminalHeight() {
        return this.getWindowsTerminalHeight();
    }
    
    private void consumeException(final Throwable e) {
    }
    
    public void setDirectConsole(final Boolean directConsole) {
        this.directConsole = directConsole;
    }
    
    public Boolean getDirectConsole() {
        return this.directConsole;
    }
    
    public synchronized boolean isEchoEnabled() {
        return this.echoEnabled;
    }
    
    public synchronized void enableEcho() {
        this.setConsoleMode(this.getConsoleMode() | 0x4 | 0x2 | 0x1 | 0x8);
        this.echoEnabled = true;
    }
    
    public synchronized void disableEcho() {
        this.setConsoleMode(this.getConsoleMode() & 0xFFFFFFF0);
        this.echoEnabled = true;
    }
    
    public InputStream getDefaultBindings() {
        return this.getClass().getResourceAsStream("windowsbindings.properties");
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
