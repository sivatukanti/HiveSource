// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.io.IOException;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import java.text.ParseException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Mapper;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.types.resources.Union;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Touch extends Task
{
    public static final DateFormatFactory DEFAULT_DF_FACTORY;
    private static final FileUtils FILE_UTILS;
    private File file;
    private long millis;
    private String dateTime;
    private Vector filesets;
    private Union resources;
    private boolean dateTimeConfigured;
    private boolean mkdirs;
    private boolean verbose;
    private FileNameMapper fileNameMapper;
    private DateFormatFactory dfFactory;
    
    public Touch() {
        this.millis = -1L;
        this.filesets = new Vector();
        this.verbose = true;
        this.fileNameMapper = null;
        this.dfFactory = Touch.DEFAULT_DF_FACTORY;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setMillis(final long millis) {
        this.millis = millis;
    }
    
    public void setDatetime(final String dateTime) {
        if (this.dateTime != null) {
            this.log("Resetting datetime attribute to " + dateTime, 3);
        }
        this.dateTime = dateTime;
        this.dateTimeConfigured = false;
    }
    
    public void setMkdirs(final boolean mkdirs) {
        this.mkdirs = mkdirs;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setPattern(final String pattern) {
        this.dfFactory = new DateFormatFactory() {
            public DateFormat getPrimaryFormat() {
                return new SimpleDateFormat(pattern);
            }
            
            public DateFormat getFallbackFormat() {
                return null;
            }
        };
    }
    
    public void addConfiguredMapper(final Mapper mapper) {
        this.add(mapper.getImplementation());
    }
    
    public void add(final FileNameMapper fileNameMapper) throws BuildException {
        if (this.fileNameMapper != null) {
            throw new BuildException("Only one mapper may be added to the " + this.getTaskName() + " task.");
        }
        this.fileNameMapper = fileNameMapper;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.add(set);
        this.add(set);
    }
    
    public void addFilelist(final FileList list) {
        this.add(list);
    }
    
    public synchronized void add(final ResourceCollection rc) {
        (this.resources = ((this.resources == null) ? new Union() : this.resources)).add(rc);
    }
    
    protected synchronized void checkConfiguration() throws BuildException {
        if (this.file == null && this.resources == null) {
            throw new BuildException("Specify at least one source--a file or resource collection.");
        }
        if (this.file != null && this.file.exists() && this.file.isDirectory()) {
            throw new BuildException("Use a resource collection to touch directories.");
        }
        if (this.dateTime != null && !this.dateTimeConfigured) {
            long workmillis = this.millis;
            if ("now".equalsIgnoreCase(this.dateTime)) {
                workmillis = System.currentTimeMillis();
            }
            else {
                DateFormat df = this.dfFactory.getPrimaryFormat();
                ParseException pe = null;
                try {
                    workmillis = df.parse(this.dateTime).getTime();
                }
                catch (ParseException peOne) {
                    df = this.dfFactory.getFallbackFormat();
                    if (df == null) {
                        pe = peOne;
                    }
                    else {
                        try {
                            workmillis = df.parse(this.dateTime).getTime();
                        }
                        catch (ParseException peTwo) {
                            pe = peTwo;
                        }
                    }
                }
                if (pe != null) {
                    throw new BuildException(pe.getMessage(), pe, this.getLocation());
                }
                if (workmillis < 0L) {
                    throw new BuildException("Date of " + this.dateTime + " results in negative " + "milliseconds value " + "relative to epoch " + "(January 1, 1970, " + "00:00:00 GMT).");
                }
            }
            this.log("Setting millis to " + workmillis + " from datetime attribute", (this.millis < 0L) ? 4 : 3);
            this.setMillis(workmillis);
            this.dateTimeConfigured = true;
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        this.touch();
    }
    
    protected void touch() throws BuildException {
        final long defaultTimestamp = this.getTimestamp();
        if (this.file != null) {
            this.touch(new FileResource(this.file.getParentFile(), this.file.getName()), defaultTimestamp);
        }
        if (this.resources == null) {
            return;
        }
        for (final Resource r : this.resources) {
            final Touchable t = r.as(Touchable.class);
            if (t == null) {
                throw new BuildException("Can't touch " + r);
            }
            this.touch(r, defaultTimestamp);
        }
        for (int size = this.filesets.size(), i = 0; i < size; ++i) {
            final FileSet fs = this.filesets.elementAt(i);
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            final File fromDir = fs.getDir(this.getProject());
            final String[] srcDirs = ds.getIncludedDirectories();
            for (int j = 0; j < srcDirs.length; ++j) {
                this.touch(new FileResource(fromDir, srcDirs[j]), defaultTimestamp);
            }
        }
    }
    
    @Deprecated
    protected void touch(final File file) {
        this.touch(file, this.getTimestamp());
    }
    
    private long getTimestamp() {
        return (this.millis < 0L) ? System.currentTimeMillis() : this.millis;
    }
    
    private void touch(final Resource r, final long defaultTimestamp) {
        if (this.fileNameMapper == null) {
            final FileProvider fp = r.as(FileProvider.class);
            if (fp != null) {
                this.touch(fp.getFile(), defaultTimestamp);
            }
            else {
                r.as(Touchable.class).touch(defaultTimestamp);
            }
        }
        else {
            final String[] mapped = this.fileNameMapper.mapFileName(r.getName());
            if (mapped != null && mapped.length > 0) {
                long modTime = defaultTimestamp;
                if (this.millis < 0L && r.isExists()) {
                    modTime = r.getLastModified();
                }
                for (int i = 0; i < mapped.length; ++i) {
                    this.touch(this.getProject().resolveFile(mapped[i]), modTime);
                }
            }
        }
    }
    
    private void touch(final File file, final long modTime) {
        if (!file.exists()) {
            this.log("Creating " + file, this.verbose ? 2 : 3);
            try {
                Touch.FILE_UTILS.createNewFile(file, this.mkdirs);
            }
            catch (IOException ioe) {
                throw new BuildException("Could not create " + file, ioe, this.getLocation());
            }
        }
        if (!file.canWrite()) {
            throw new BuildException("Can not change modification date of read-only file " + file);
        }
        Touch.FILE_UTILS.setFileLastModified(file, modTime);
    }
    
    static {
        DEFAULT_DF_FACTORY = new DateFormatFactory() {
            public DateFormat getPrimaryFormat() {
                return DateFormat.getDateTimeInstance(3, 3, Locale.US);
            }
            
            public DateFormat getFallbackFormat() {
                return DateFormat.getDateTimeInstance(3, 2, Locale.US);
            }
        };
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    public interface DateFormatFactory
    {
        DateFormat getPrimaryFormat();
        
        DateFormat getFallbackFormat();
    }
}
