// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.fs.shell.PathData;
import org.apache.hadoop.util.StringUtils;
import java.util.Deque;
import java.io.IOException;
import org.apache.hadoop.fs.GlobPattern;

final class Name extends BaseExpression
{
    private static final String[] USAGE;
    private static final String[] HELP;
    private GlobPattern globPattern;
    private boolean caseSensitive;
    
    public static void registerExpression(final ExpressionFactory factory) throws IOException {
        factory.addClass(Name.class, "-name");
        factory.addClass(Iname.class, "-iname");
    }
    
    public Name() {
        this(true);
    }
    
    private Name(final boolean caseSensitive) {
        this.caseSensitive = true;
        this.setUsage(Name.USAGE);
        this.setHelp(Name.HELP);
        this.setCaseSensitive(caseSensitive);
    }
    
    private void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public void addArguments(final Deque<String> args) {
        this.addArguments(args, 1);
    }
    
    @Override
    public void prepare() throws IOException {
        String argPattern = this.getArgument(1);
        if (!this.caseSensitive) {
            argPattern = StringUtils.toLowerCase(argPattern);
        }
        this.globPattern = new GlobPattern(argPattern);
    }
    
    @Override
    public Result apply(final PathData item, final int depth) throws IOException {
        String name = this.getPath(item).getName();
        if (!this.caseSensitive) {
            name = StringUtils.toLowerCase(name);
        }
        if (this.globPattern.matches(name)) {
            return Result.PASS;
        }
        return Result.FAIL;
    }
    
    static {
        USAGE = new String[] { "-name pattern", "-iname pattern" };
        HELP = new String[] { "Evaluates as true if the basename of the file matches the", "pattern using standard file system globbing.", "If -iname is used then the match is case insensitive." };
    }
    
    static class Iname extends FilterExpression
    {
        public Iname() {
            super(new Name(false, null));
        }
    }
}
