// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.io.PrintStream;
import org.apache.tools.ant.util.PropertyOutputStream;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.Task;

public class Length extends Task implements Condition
{
    private static final String ALL = "all";
    private static final String EACH = "each";
    private static final String STRING = "string";
    private static final String LENGTH_REQUIRED = "Use of the Length condition requires that the length attribute be set.";
    private String property;
    private String string;
    private Boolean trim;
    private String mode;
    private Comparison when;
    private Long length;
    private Resources resources;
    
    public Length() {
        this.mode = "all";
        this.when = Comparison.EQUAL;
    }
    
    public synchronized void setProperty(final String property) {
        this.property = property;
    }
    
    public synchronized void setResource(final Resource resource) {
        this.add(resource);
    }
    
    public synchronized void setFile(final File file) {
        this.add(new FileResource(file));
    }
    
    public synchronized void add(final FileSet fs) {
        this.add((ResourceCollection)fs);
    }
    
    public synchronized void add(final ResourceCollection c) {
        if (c == null) {
            return;
        }
        (this.resources = ((this.resources == null) ? new Resources() : this.resources)).add(c);
    }
    
    public synchronized void setLength(final long ell) {
        this.length = new Long(ell);
    }
    
    public synchronized void setWhen(final When w) {
        this.setWhen((Comparison)w);
    }
    
    public synchronized void setWhen(final Comparison c) {
        this.when = c;
    }
    
    public synchronized void setMode(final FileMode m) {
        this.mode = m.getValue();
    }
    
    public synchronized void setString(final String string) {
        this.string = string;
        this.mode = "string";
    }
    
    public synchronized void setTrim(final boolean trim) {
        this.trim = (trim ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean getTrim() {
        return this.trim != null && this.trim;
    }
    
    @Override
    public void execute() {
        this.validate();
        final PrintStream ps = new PrintStream((this.property != null) ? new PropertyOutputStream(this.getProject(), this.property) : new LogOutputStream(this, 2));
        if ("string".equals(this.mode)) {
            ps.print(getLength(this.string, this.getTrim()));
            ps.close();
        }
        else if ("each".equals(this.mode)) {
            this.handleResources(new EachHandler(ps));
        }
        else if ("all".equals(this.mode)) {
            this.handleResources(new AllHandler(ps));
        }
    }
    
    public boolean eval() {
        this.validate();
        if (this.length == null) {
            throw new BuildException("Use of the Length condition requires that the length attribute be set.");
        }
        Long ell;
        if ("string".equals(this.mode)) {
            ell = new Long(getLength(this.string, this.getTrim()));
        }
        else {
            final AccumHandler h = new AccumHandler();
            this.handleResources(h);
            ell = new Long(h.getAccum());
        }
        return this.when.evaluate(ell.compareTo(this.length));
    }
    
    private void validate() {
        if (this.string != null) {
            if (this.resources != null) {
                throw new BuildException("the string length function is incompatible with the file/resource length function");
            }
            if (!"string".equals(this.mode)) {
                throw new BuildException("the mode attribute is for use with the file/resource length function");
            }
        }
        else {
            if (this.resources == null) {
                throw new BuildException("you must set either the string attribute or specify one or more files using the file attribute or nested resource collections");
            }
            if (!"each".equals(this.mode) && !"all".equals(this.mode)) {
                throw new BuildException("invalid mode setting for file/resource length function: \"" + this.mode + "\"");
            }
            if (this.trim != null) {
                throw new BuildException("the trim attribute is for use with the string length function only");
            }
        }
    }
    
    private void handleResources(final Handler h) {
        for (final Resource r : this.resources) {
            if (!r.isExists()) {
                this.log(r + " does not exist", 1);
            }
            if (r.isDirectory()) {
                this.log(r + " is a directory; length may not be meaningful", 1);
            }
            h.handle(r);
        }
        h.complete();
    }
    
    private static long getLength(final String s, final boolean t) {
        return (t ? s.trim() : s).length();
    }
    
    public static class FileMode extends EnumeratedAttribute
    {
        static final String[] MODES;
        
        @Override
        public String[] getValues() {
            return FileMode.MODES;
        }
        
        static {
            MODES = new String[] { "each", "all" };
        }
    }
    
    public static class When extends Comparison
    {
    }
    
    private abstract class Handler
    {
        private PrintStream ps;
        
        Handler(final PrintStream ps) {
            this.ps = ps;
        }
        
        protected PrintStream getPs() {
            return this.ps;
        }
        
        protected abstract void handle(final Resource p0);
        
        void complete() {
            FileUtils.close(this.ps);
        }
    }
    
    private class EachHandler extends Handler
    {
        EachHandler(final PrintStream ps) {
            super(ps);
        }
        
        @Override
        protected void handle(final Resource r) {
            this.getPs().print(r.toString());
            this.getPs().print(" : ");
            final long size = r.getSize();
            if (size == -1L) {
                this.getPs().println("unknown");
            }
            else {
                this.getPs().println(size);
            }
        }
    }
    
    private class AccumHandler extends Handler
    {
        private long accum;
        
        AccumHandler() {
            super(null);
            this.accum = 0L;
        }
        
        protected AccumHandler(final PrintStream ps) {
            super(ps);
            this.accum = 0L;
        }
        
        protected long getAccum() {
            return this.accum;
        }
        
        @Override
        protected synchronized void handle(final Resource r) {
            final long size = r.getSize();
            if (size == -1L) {
                Length.this.log("Size unknown for " + r.toString(), 1);
            }
            else {
                this.accum += size;
            }
        }
    }
    
    private class AllHandler extends AccumHandler
    {
        AllHandler(final PrintStream ps) {
            super(ps);
        }
        
        @Override
        void complete() {
            this.getPs().print(this.getAccum());
            super.complete();
        }
    }
}
