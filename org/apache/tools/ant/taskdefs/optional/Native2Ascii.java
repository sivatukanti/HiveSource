// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapterFactory;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.ant.types.Mapper;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class Native2Ascii extends MatchingTask
{
    private boolean reverse;
    private String encoding;
    private File srcDir;
    private File destDir;
    private String extension;
    private Mapper mapper;
    private FacadeTaskHelper facade;
    private Native2AsciiAdapter nestedAdapter;
    
    public Native2Ascii() {
        this.reverse = false;
        this.encoding = null;
        this.srcDir = null;
        this.destDir = null;
        this.extension = null;
        this.facade = null;
        this.nestedAdapter = null;
        this.facade = new FacadeTaskHelper(Native2AsciiAdapterFactory.getDefault());
    }
    
    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }
    
    public boolean getReverse() {
        return this.reverse;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setSrc(final File srcDir) {
        this.srcDir = srcDir;
    }
    
    public void setDest(final File destDir) {
        this.destDir = destDir;
    }
    
    public void setExt(final String ext) {
        this.extension = ext;
    }
    
    public void setImplementation(final String impl) {
        if ("default".equals(impl)) {
            this.facade.setImplementation(Native2AsciiAdapterFactory.getDefault());
        }
        else {
            this.facade.setImplementation(impl);
        }
    }
    
    public Mapper createMapper() throws BuildException {
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        return this.mapper = new Mapper(this.getProject());
    }
    
    public void add(final FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }
    
    public ImplementationSpecificArgument createArg() {
        final ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }
    
    public Path createImplementationClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }
    
    public void add(final Native2AsciiAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one native2ascii adapter");
        }
        this.nestedAdapter = adapter;
    }
    
    @Override
    public void execute() throws BuildException {
        DirectoryScanner scanner = null;
        if (this.srcDir == null) {
            this.srcDir = this.getProject().resolveFile(".");
        }
        if (this.destDir == null) {
            throw new BuildException("The dest attribute must be set.");
        }
        if (this.srcDir.equals(this.destDir) && this.extension == null && this.mapper == null) {
            throw new BuildException("The ext attribute or a mapper must be set if src and dest dirs are the same.");
        }
        FileNameMapper m = null;
        if (this.mapper == null) {
            if (this.extension == null) {
                m = new IdentityMapper();
            }
            else {
                m = new ExtMapper();
            }
        }
        else {
            m = this.mapper.getImplementation();
        }
        scanner = this.getDirectoryScanner(this.srcDir);
        String[] files = scanner.getIncludedFiles();
        final SourceFileScanner sfs = new SourceFileScanner(this);
        files = sfs.restrict(files, this.srcDir, this.destDir, m);
        final int count = files.length;
        if (count == 0) {
            return;
        }
        final String message = "Converting " + count + " file" + ((count != 1) ? "s" : "") + " from ";
        this.log(message + this.srcDir + " to " + this.destDir);
        for (int i = 0; i < files.length; ++i) {
            this.convert(files[i], m.mapFileName(files[i])[0]);
        }
    }
    
    private void convert(final String srcName, final String destName) throws BuildException {
        final File srcFile = new File(this.srcDir, srcName);
        final File destFile = new File(this.destDir, destName);
        if (srcFile.equals(destFile)) {
            throw new BuildException("file " + srcFile + " would overwrite its self");
        }
        final String parentName = destFile.getParent();
        if (parentName != null) {
            final File parentFile = new File(parentName);
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new BuildException("cannot create parent directory " + parentName);
            }
        }
        this.log("converting " + srcName, 3);
        final Native2AsciiAdapter ad = (this.nestedAdapter != null) ? this.nestedAdapter : Native2AsciiAdapterFactory.getAdapter(this.facade.getImplementation(), this, this.createImplementationClasspath());
        if (!ad.convert(this, srcFile, destFile)) {
            throw new BuildException("conversion failed");
        }
    }
    
    public String[] getCurrentArgs() {
        return this.facade.getArgs();
    }
    
    private class ExtMapper implements FileNameMapper
    {
        public void setFrom(final String s) {
        }
        
        public void setTo(final String s) {
        }
        
        public String[] mapFileName(final String fileName) {
            final int lastDot = fileName.lastIndexOf(46);
            if (lastDot >= 0) {
                return new String[] { fileName.substring(0, lastDot) + Native2Ascii.this.extension };
            }
            return new String[] { fileName + Native2Ascii.this.extension };
        }
    }
}
