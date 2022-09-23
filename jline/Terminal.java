// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.IOException;
import java.io.InputStream;

public abstract class Terminal implements ConsoleOperations
{
    private static Terminal term;
    
    public static Terminal getTerminal() {
        return setupTerminal();
    }
    
    public static void resetTerminal() {
        Terminal.term = null;
    }
    
    public static synchronized Terminal setupTerminal() {
        if (Terminal.term != null) {
            return Terminal.term;
        }
        final String os = System.getProperty("os.name").toLowerCase();
        final String termProp = System.getProperty("jline.terminal");
        Terminal t = null;
        Label_0098: {
            if (termProp != null && termProp.length() > 0) {
                try {
                    t = (Terminal)Class.forName(termProp).newInstance();
                    break Label_0098;
                }
                catch (Exception e) {
                    throw (IllegalArgumentException)new IllegalArgumentException(e.toString()).fillInStackTrace();
                }
            }
            if (os.indexOf("windows") != -1) {
                t = new WindowsTerminal();
            }
            else {
                t = new UnixTerminal();
            }
            try {
                t.initializeTerminal();
            }
            catch (Exception e) {
                e.printStackTrace();
                return Terminal.term = new UnsupportedTerminal();
            }
        }
        return Terminal.term = t;
    }
    
    public boolean isANSISupported() {
        return true;
    }
    
    public int readCharacter(final InputStream in) throws IOException {
        return in.read();
    }
    
    public int readVirtualKey(final InputStream in) throws IOException {
        return this.readCharacter(in);
    }
    
    public abstract void initializeTerminal() throws Exception;
    
    public abstract int getTerminalWidth();
    
    public abstract int getTerminalHeight();
    
    public abstract boolean isSupported();
    
    public abstract boolean getEcho();
    
    public void beforeReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
    }
    
    public void afterReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
    }
    
    public abstract boolean isEchoEnabled();
    
    public abstract void enableEcho();
    
    public abstract void disableEcho();
    
    public InputStream getDefaultBindings() {
        return Terminal.class.getResourceAsStream("keybindings.properties");
    }
}
