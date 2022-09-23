// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ANSIBuffer
{
    private boolean ansiEnabled;
    private final StringBuffer ansiBuffer;
    private final StringBuffer plainBuffer;
    
    public ANSIBuffer() {
        this.ansiEnabled = true;
        this.ansiBuffer = new StringBuffer();
        this.plainBuffer = new StringBuffer();
    }
    
    public ANSIBuffer(final String str) {
        this.ansiEnabled = true;
        this.ansiBuffer = new StringBuffer();
        this.plainBuffer = new StringBuffer();
        this.append(str);
    }
    
    public void setAnsiEnabled(final boolean ansi) {
        this.ansiEnabled = ansi;
    }
    
    public boolean getAnsiEnabled() {
        return this.ansiEnabled;
    }
    
    public String getAnsiBuffer() {
        return this.ansiBuffer.toString();
    }
    
    public String getPlainBuffer() {
        return this.plainBuffer.toString();
    }
    
    public String toString(final boolean ansi) {
        return ansi ? this.getAnsiBuffer() : this.getPlainBuffer();
    }
    
    public String toString() {
        return this.toString(this.ansiEnabled);
    }
    
    public ANSIBuffer append(final String str) {
        this.ansiBuffer.append(str);
        this.plainBuffer.append(str);
        return this;
    }
    
    public ANSIBuffer attrib(final String str, final int code) {
        this.ansiBuffer.append(ANSICodes.attrib(code)).append(str).append(ANSICodes.attrib(0));
        this.plainBuffer.append(str);
        return this;
    }
    
    public ANSIBuffer red(final String str) {
        return this.attrib(str, 31);
    }
    
    public ANSIBuffer blue(final String str) {
        return this.attrib(str, 34);
    }
    
    public ANSIBuffer green(final String str) {
        return this.attrib(str, 32);
    }
    
    public ANSIBuffer black(final String str) {
        return this.attrib(str, 30);
    }
    
    public ANSIBuffer yellow(final String str) {
        return this.attrib(str, 33);
    }
    
    public ANSIBuffer magenta(final String str) {
        return this.attrib(str, 35);
    }
    
    public ANSIBuffer cyan(final String str) {
        return this.attrib(str, 36);
    }
    
    public ANSIBuffer bold(final String str) {
        return this.attrib(str, 1);
    }
    
    public ANSIBuffer underscore(final String str) {
        return this.attrib(str, 4);
    }
    
    public ANSIBuffer blink(final String str) {
        return this.attrib(str, 5);
    }
    
    public ANSIBuffer reverse(final String str) {
        return this.attrib(str, 7);
    }
    
    public static void main(final String[] args) throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(ANSICodes.setkey("97", "97;98;99;13") + ANSICodes.attrib(0));
        System.out.flush();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("GOT: " + line);
        }
    }
    
    public static class ANSICodes
    {
        static final int OFF = 0;
        static final int BOLD = 1;
        static final int UNDERSCORE = 4;
        static final int BLINK = 5;
        static final int REVERSE = 7;
        static final int CONCEALED = 8;
        static final int FG_BLACK = 30;
        static final int FG_RED = 31;
        static final int FG_GREEN = 32;
        static final int FG_YELLOW = 33;
        static final int FG_BLUE = 34;
        static final int FG_MAGENTA = 35;
        static final int FG_CYAN = 36;
        static final int FG_WHITE = 37;
        static final char ESC = '\u001b';
        
        private ANSICodes() {
        }
        
        public static String setmode(final int mode) {
            return "\u001b[=" + mode + "h";
        }
        
        public static String resetmode(final int mode) {
            return "\u001b[=" + mode + "l";
        }
        
        public static String clrscr() {
            return "\u001b[2J";
        }
        
        public static String clreol() {
            return "\u001b[K";
        }
        
        public static String left(final int n) {
            return "\u001b[" + n + "D";
        }
        
        public static String right(final int n) {
            return "\u001b[" + n + "C";
        }
        
        public static String up(final int n) {
            return "\u001b[" + n + "A";
        }
        
        public static String down(final int n) {
            return "\u001b[" + n + "B";
        }
        
        public static String gotoxy(final int row, final int column) {
            return "\u001b[" + row + ";" + column + "H";
        }
        
        public static String save() {
            return "\u001b[s";
        }
        
        public static String restore() {
            return "\u001b[u";
        }
        
        public static String attrib(final int attr) {
            return "\u001b[" + attr + "m";
        }
        
        public static String setkey(final String code, final String value) {
            return "\u001b[" + code + ";" + value + "p";
        }
    }
}
