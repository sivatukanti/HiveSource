// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public abstract class FileLineFormatter
{
    private static FileLineFormatter formatter;
    
    public static FileLineFormatter getFormatter() {
        return FileLineFormatter.formatter;
    }
    
    public static void setFormatter(final FileLineFormatter formatter) {
        FileLineFormatter.formatter = formatter;
    }
    
    public abstract String getFormatString(final String p0, final int p1, final int p2);
    
    static {
        FileLineFormatter.formatter = new DefaultFileLineFormatter();
    }
}
