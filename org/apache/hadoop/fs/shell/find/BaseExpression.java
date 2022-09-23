// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.shell.PathData;
import java.util.Deque;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public abstract class BaseExpression implements Expression, Configurable
{
    private String[] usage;
    private String[] help;
    private FindOptions options;
    private Configuration conf;
    private LinkedList<String> arguments;
    private LinkedList<Expression> children;
    
    public BaseExpression() {
        this.usage = new String[] { "Not yet implemented" };
        this.help = new String[] { "Not yet implemented" };
        this.arguments = new LinkedList<String>();
        this.children = new LinkedList<Expression>();
    }
    
    protected void setUsage(final String[] usage) {
        this.usage = usage;
    }
    
    protected void setHelp(final String[] help) {
        this.help = help;
    }
    
    @Override
    public String[] getUsage() {
        return this.usage;
    }
    
    @Override
    public String[] getHelp() {
        return this.help;
    }
    
    @Override
    public void setOptions(final FindOptions options) throws IOException {
        this.options = options;
        for (final Expression child : this.getChildren()) {
            child.setOptions(options);
        }
    }
    
    @Override
    public void prepare() throws IOException {
        for (final Expression child : this.getChildren()) {
            child.prepare();
        }
    }
    
    @Override
    public void finish() throws IOException {
        for (final Expression child : this.getChildren()) {
            child.finish();
        }
    }
    
    protected FindOptions getOptions() {
        return (this.options == null) ? new FindOptions() : this.options;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("(");
        boolean firstArg = true;
        for (final String arg : this.getArguments()) {
            if (!firstArg) {
                sb.append(",");
            }
            else {
                firstArg = false;
            }
            sb.append(arg);
        }
        sb.append(";");
        firstArg = true;
        for (final Expression child : this.getChildren()) {
            if (!firstArg) {
                sb.append(",");
            }
            else {
                firstArg = false;
            }
            sb.append(child.toString());
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean isAction() {
        for (final Expression child : this.getChildren()) {
            if (child.isAction()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isOperator() {
        return false;
    }
    
    protected List<String> getArguments() {
        return this.arguments;
    }
    
    protected String getArgument(final int position) throws IOException {
        if (position > this.arguments.size()) {
            throw new IOException("Missing argument at " + position);
        }
        final String argument = this.arguments.get(position - 1);
        if (argument == null) {
            throw new IOException("Null argument at position " + position);
        }
        return argument;
    }
    
    protected List<Expression> getChildren() {
        return this.children;
    }
    
    @Override
    public int getPrecedence() {
        return 0;
    }
    
    @Override
    public void addChildren(final Deque<Expression> exprs) {
    }
    
    protected void addChildren(final Deque<Expression> exprs, final int count) {
        for (int i = 0; i < count; ++i) {
            this.addChild(exprs.pop());
        }
    }
    
    private void addChild(final Expression expr) {
        this.children.push(expr);
    }
    
    @Override
    public void addArguments(final Deque<String> args) {
    }
    
    protected void addArguments(final Deque<String> args, final int count) {
        for (int i = 0; i < count; ++i) {
            this.addArgument(args.pop());
        }
    }
    
    protected void addArgument(final String arg) {
        this.arguments.add(arg);
    }
    
    protected FileStatus getFileStatus(final PathData item, final int depth) throws IOException {
        FileStatus fileStatus = item.stat;
        if (fileStatus.isSymlink() && (this.options.isFollowLink() || (this.options.isFollowArgLink() && depth == 0))) {
            final Path linkedFile = item.fs.resolvePath(fileStatus.getSymlink());
            fileStatus = this.getFileSystem(item).getFileStatus(linkedFile);
        }
        return fileStatus;
    }
    
    protected Path getPath(final PathData item) throws IOException {
        return item.path;
    }
    
    protected FileSystem getFileSystem(final PathData item) throws IOException {
        return item.fs;
    }
}
