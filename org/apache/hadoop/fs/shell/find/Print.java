// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;

final class Print extends BaseExpression
{
    private static final String[] USAGE;
    private static final String[] HELP;
    private final String suffix;
    
    public static void registerExpression(final ExpressionFactory factory) throws IOException {
        factory.addClass(Print.class, "-print");
        factory.addClass(Print0.class, "-print0");
    }
    
    public Print() {
        this("\n");
    }
    
    private Print(final String suffix) {
        this.setUsage(Print.USAGE);
        this.setHelp(Print.HELP);
        this.suffix = suffix;
    }
    
    @Override
    public Result apply(final PathData item, final int depth) throws IOException {
        this.getOptions().getOut().print(item.toString() + this.suffix);
        return Result.PASS;
    }
    
    @Override
    public boolean isAction() {
        return true;
    }
    
    static {
        USAGE = new String[] { "-print", "-print0" };
        HELP = new String[] { "Always evaluates to true. Causes the current pathname to be", "written to standard output followed by a newline. If the -print0", "expression is used then an ASCII NULL character is appended rather", "than a newline." };
    }
    
    static final class Print0 extends FilterExpression
    {
        public Print0() {
            super(new Print("\u0000", null));
        }
    }
}
