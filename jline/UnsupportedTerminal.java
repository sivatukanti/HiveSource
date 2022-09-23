// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.IOException;

public class UnsupportedTerminal extends Terminal
{
    private Thread maskThread;
    
    public UnsupportedTerminal() {
        this.maskThread = null;
    }
    
    public void initializeTerminal() {
    }
    
    public boolean getEcho() {
        return true;
    }
    
    public boolean isEchoEnabled() {
        return true;
    }
    
    public void enableEcho() {
    }
    
    public void disableEcho() {
    }
    
    public int getTerminalWidth() {
        return 80;
    }
    
    public int getTerminalHeight() {
        return 80;
    }
    
    public boolean isSupported() {
        return false;
    }
    
    public void beforeReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
        if (mask != null && this.maskThread == null) {
            final String fullPrompt = "\r" + prompt + "                 " + "                 " + "                 " + "\r" + prompt;
            (this.maskThread = new Thread("JLine Mask Thread") {
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            reader.out.write(fullPrompt);
                            reader.out.flush();
                            Thread.sleep(3L);
                            continue;
                        }
                        catch (IOException ioe) {
                            return;
                        }
                        catch (InterruptedException ie) {
                            return;
                        }
                        break;
                    }
                }
            }).setPriority(10);
            this.maskThread.setDaemon(true);
            this.maskThread.start();
        }
    }
    
    public void afterReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
        if (this.maskThread != null && this.maskThread.isAlive()) {
            this.maskThread.interrupt();
        }
        this.maskThread = null;
    }
}
