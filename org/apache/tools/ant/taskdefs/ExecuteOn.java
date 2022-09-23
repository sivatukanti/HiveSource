// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.SourceFileScanner;
import java.util.HashSet;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import java.io.IOException;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import java.io.File;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.AbstractFileSet;
import java.util.Vector;

public class ExecuteOn extends ExecTask
{
    protected Vector<AbstractFileSet> filesets;
    private Union resources;
    private boolean relative;
    private boolean parallel;
    private boolean forwardSlash;
    protected String type;
    protected Commandline.Marker srcFilePos;
    private boolean skipEmpty;
    protected Commandline.Marker targetFilePos;
    protected Mapper mapperElement;
    protected FileNameMapper mapper;
    protected File destDir;
    private int maxParallel;
    private boolean addSourceFile;
    private boolean verbose;
    private boolean ignoreMissing;
    private boolean force;
    protected boolean srcIsFirst;
    
    public ExecuteOn() {
        this.filesets = new Vector<AbstractFileSet>();
        this.resources = null;
        this.relative = false;
        this.parallel = false;
        this.forwardSlash = false;
        this.type = "file";
        this.srcFilePos = null;
        this.skipEmpty = false;
        this.targetFilePos = null;
        this.mapperElement = null;
        this.mapper = null;
        this.destDir = null;
        this.maxParallel = -1;
        this.addSourceFile = true;
        this.verbose = false;
        this.ignoreMissing = true;
        this.force = false;
        this.srcIsFirst = true;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    public void addDirset(final DirSet set) {
        this.filesets.addElement(set);
    }
    
    public void addFilelist(final FileList list) {
        this.add(list);
    }
    
    public void add(final ResourceCollection rc) {
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }
    
    public void setRelative(final boolean relative) {
        this.relative = relative;
    }
    
    public void setParallel(final boolean parallel) {
        this.parallel = parallel;
    }
    
    public void setType(final FileDirBoth type) {
        this.type = type.getValue();
    }
    
    public void setSkipEmptyFilesets(final boolean skip) {
        this.skipEmpty = skip;
    }
    
    public void setDest(final File destDir) {
        this.destDir = destDir;
    }
    
    public void setForwardslash(final boolean forwardSlash) {
        this.forwardSlash = forwardSlash;
    }
    
    public void setMaxParallel(final int max) {
        this.maxParallel = max;
    }
    
    public void setAddsourcefile(final boolean b) {
        this.addSourceFile = b;
    }
    
    public void setVerbose(final boolean b) {
        this.verbose = b;
    }
    
    public void setIgnoremissing(final boolean b) {
        this.ignoreMissing = b;
    }
    
    public void setForce(final boolean b) {
        this.force = b;
    }
    
    public Commandline.Marker createSrcfile() {
        if (this.srcFilePos != null) {
            throw new BuildException(this.getTaskType() + " doesn't support multiple " + "srcfile elements.", this.getLocation());
        }
        return this.srcFilePos = this.cmdl.createMarker();
    }
    
    public Commandline.Marker createTargetfile() {
        if (this.targetFilePos != null) {
            throw new BuildException(this.getTaskType() + " doesn't support multiple " + "targetfile elements.", this.getLocation());
        }
        this.targetFilePos = this.cmdl.createMarker();
        this.srcIsFirst = (this.srcFilePos != null);
        return this.targetFilePos;
    }
    
    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        return this.mapperElement = new Mapper(this.getProject());
    }
    
    public void add(final FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }
    
    @Override
    protected void checkConfiguration() {
        if ("execon".equals(this.getTaskName())) {
            this.log("!! execon is deprecated. Use apply instead. !!");
        }
        super.checkConfiguration();
        if (this.filesets.size() == 0 && this.resources == null) {
            throw new BuildException("no resources specified", this.getLocation());
        }
        if (this.targetFilePos != null && this.mapperElement == null) {
            throw new BuildException("targetfile specified without mapper", this.getLocation());
        }
        if (this.destDir != null && this.mapperElement == null) {
            throw new BuildException("dest specified without mapper", this.getLocation());
        }
        if (this.mapperElement != null) {
            this.mapper = this.mapperElement.getImplementation();
        }
    }
    
    @Override
    protected ExecuteStreamHandler createHandler() throws BuildException {
        return (this.redirectorElement == null) ? super.createHandler() : new PumpStreamHandler();
    }
    
    @Override
    protected void setupRedirector() {
        super.setupRedirector();
        this.redirector.setAppendProperties(true);
    }
    
    @Override
    protected void runExec(final Execute exe) throws BuildException {
        int totalFiles = 0;
        int totalDirs = 0;
        boolean haveExecuted = false;
        try {
            final Vector<String> fileNames = new Vector<String>();
            final Vector<File> baseDirs = new Vector<File>();
            for (int size = this.filesets.size(), i = 0; i < size; ++i) {
                String currentType = this.type;
                final AbstractFileSet fs = this.filesets.elementAt(i);
                if (fs instanceof DirSet && !"dir".equals(this.type)) {
                    this.log("Found a nested dirset but type is " + this.type + ". " + "Temporarily switching to type=\"dir\" on the" + " assumption that you really did mean" + " <dirset> not <fileset>.", 4);
                    currentType = "dir";
                }
                final File base = fs.getDir(this.getProject());
                final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                if (!"dir".equals(currentType)) {
                    final String[] s = this.getFiles(base, ds);
                    for (int j = 0; j < s.length; ++j) {
                        ++totalFiles;
                        fileNames.addElement(s[j]);
                        baseDirs.addElement(base);
                    }
                }
                if (!"file".equals(currentType)) {
                    final String[] s = this.getDirs(base, ds);
                    for (int j = 0; j < s.length; ++j) {
                        ++totalDirs;
                        fileNames.addElement(s[j]);
                        baseDirs.addElement(base);
                    }
                }
                if (fileNames.size() == 0 && this.skipEmpty) {
                    this.logSkippingFileset(currentType, ds, base);
                }
                else if (!this.parallel) {
                    final String[] s = new String[fileNames.size()];
                    fileNames.copyInto(s);
                    for (int j = 0; j < s.length; ++j) {
                        final String[] command = this.getCommandline(s[j], base);
                        this.log(Commandline.describeCommand(command), 3);
                        exe.setCommandline(command);
                        if (this.redirectorElement != null) {
                            this.setupRedirector();
                            this.redirectorElement.configure(this.redirector, s[j]);
                        }
                        if (this.redirectorElement != null || haveExecuted) {
                            exe.setStreamHandler(this.redirector.createHandler());
                        }
                        this.runExecute(exe);
                        haveExecuted = true;
                    }
                    fileNames.removeAllElements();
                    baseDirs.removeAllElements();
                }
            }
            if (this.resources != null) {
                for (final Resource res : this.resources) {
                    if (!res.isExists() && this.ignoreMissing) {
                        continue;
                    }
                    File base2 = null;
                    String name = res.getName();
                    final FileProvider fp = res.as(FileProvider.class);
                    if (fp != null) {
                        final FileResource fr = ResourceUtils.asFileResource(fp);
                        base2 = fr.getBaseDir();
                        if (base2 == null) {
                            name = fr.getFile().getAbsolutePath();
                        }
                    }
                    if (this.restrict(new String[] { name }, base2).length == 0) {
                        continue;
                    }
                    if ((!res.isDirectory() || !res.isExists()) && !"dir".equals(this.type)) {
                        ++totalFiles;
                    }
                    else {
                        if (!res.isDirectory() || "file".equals(this.type)) {
                            continue;
                        }
                        ++totalDirs;
                    }
                    baseDirs.add(base2);
                    fileNames.add(name);
                    if (this.parallel) {
                        continue;
                    }
                    final String[] command2 = this.getCommandline(name, base2);
                    this.log(Commandline.describeCommand(command2), 3);
                    exe.setCommandline(command2);
                    if (this.redirectorElement != null) {
                        this.setupRedirector();
                        this.redirectorElement.configure(this.redirector, name);
                    }
                    if (this.redirectorElement != null || haveExecuted) {
                        exe.setStreamHandler(this.redirector.createHandler());
                    }
                    this.runExecute(exe);
                    haveExecuted = true;
                    fileNames.removeAllElements();
                    baseDirs.removeAllElements();
                }
            }
            if (this.parallel && (fileNames.size() > 0 || !this.skipEmpty)) {
                this.runParallel(exe, fileNames, baseDirs);
                haveExecuted = true;
            }
            if (haveExecuted) {
                this.log("Applied " + this.cmdl.getExecutable() + " to " + totalFiles + " file" + ((totalFiles != 1) ? "s" : "") + " and " + totalDirs + " director" + ((totalDirs != 1) ? "ies" : "y") + ".", this.verbose ? 2 : 3);
            }
        }
        catch (IOException e) {
            throw new BuildException("Execute failed: " + e, e, this.getLocation());
        }
        finally {
            this.logFlush();
            this.redirector.setAppendProperties(false);
            this.redirector.setProperties();
        }
    }
    
    private void logSkippingFileset(final String currentType, final DirectoryScanner ds, final File base) {
        final int includedCount = ("dir".equals(currentType) ? 0 : ds.getIncludedFilesCount()) + ("file".equals(currentType) ? 0 : ds.getIncludedDirsCount());
        this.log("Skipping fileset for directory " + base + ". It is " + ((includedCount > 0) ? "up to date." : "empty."), this.verbose ? 2 : 3);
    }
    
    protected String[] getCommandline(String[] srcFiles, final File[] baseDirs) {
        final char fileSeparator = File.separatorChar;
        final Vector<String> targets = new Vector<String>();
        if (this.targetFilePos != null) {
            final HashSet<String> addedFiles = new HashSet<String>();
            for (int i = 0; i < srcFiles.length; ++i) {
                final String[] subTargets = this.mapper.mapFileName(srcFiles[i]);
                if (subTargets != null) {
                    for (int j = 0; j < subTargets.length; ++j) {
                        String name = null;
                        if (!this.relative) {
                            name = new File(this.destDir, subTargets[j]).getAbsolutePath();
                        }
                        else {
                            name = subTargets[j];
                        }
                        if (this.forwardSlash && fileSeparator != '/') {
                            name = name.replace(fileSeparator, '/');
                        }
                        if (!addedFiles.contains(name)) {
                            targets.addElement(name);
                            addedFiles.add(name);
                        }
                    }
                }
            }
        }
        final String[] targetFiles = targets.toArray(new String[targets.size()]);
        if (!this.addSourceFile) {
            srcFiles = new String[0];
        }
        final String[] orig = this.cmdl.getCommandline();
        final String[] result = new String[orig.length + srcFiles.length + targetFiles.length];
        int srcIndex = orig.length;
        if (this.srcFilePos != null) {
            srcIndex = this.srcFilePos.getPosition();
        }
        if (this.targetFilePos != null) {
            final int targetIndex = this.targetFilePos.getPosition();
            if (srcIndex < targetIndex || (srcIndex == targetIndex && this.srcIsFirst)) {
                System.arraycopy(orig, 0, result, 0, srcIndex);
                System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length, targetIndex - srcIndex);
                insertTargetFiles(targetFiles, result, targetIndex + srcFiles.length, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(orig, targetIndex, result, targetIndex + srcFiles.length + targetFiles.length, orig.length - targetIndex);
            }
            else {
                System.arraycopy(orig, 0, result, 0, targetIndex);
                insertTargetFiles(targetFiles, result, targetIndex, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(orig, targetIndex, result, targetIndex + targetFiles.length, srcIndex - targetIndex);
                System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length + targetFiles.length, orig.length - srcIndex);
                srcIndex += targetFiles.length;
            }
        }
        else {
            System.arraycopy(orig, 0, result, 0, srcIndex);
            System.arraycopy(orig, srcIndex, result, srcIndex + srcFiles.length, orig.length - srcIndex);
        }
        for (int k = 0; k < srcFiles.length; ++k) {
            String src;
            if (this.relative) {
                src = srcFiles[k];
            }
            else {
                src = new File(baseDirs[k], srcFiles[k]).getAbsolutePath();
            }
            if (this.forwardSlash && fileSeparator != '/') {
                src = src.replace(fileSeparator, '/');
            }
            if (this.srcFilePos != null && (this.srcFilePos.getPrefix().length() > 0 || this.srcFilePos.getSuffix().length() > 0)) {
                src = this.srcFilePos.getPrefix() + src + this.srcFilePos.getSuffix();
            }
            result[srcIndex + k] = src;
        }
        return result;
    }
    
    protected String[] getCommandline(final String srcFile, final File baseDir) {
        return this.getCommandline(new String[] { srcFile }, new File[] { baseDir });
    }
    
    protected String[] getFiles(final File baseDir, final DirectoryScanner ds) {
        return this.restrict(ds.getIncludedFiles(), baseDir);
    }
    
    protected String[] getDirs(final File baseDir, final DirectoryScanner ds) {
        return this.restrict(ds.getIncludedDirectories(), baseDir);
    }
    
    protected String[] getFilesAndDirs(final FileList list) {
        return this.restrict(list.getFiles(this.getProject()), list.getDir(this.getProject()));
    }
    
    private String[] restrict(final String[] s, final File baseDir) {
        return (this.mapper == null || this.force) ? s : new SourceFileScanner(this).restrict(s, baseDir, this.destDir, this.mapper);
    }
    
    protected void runParallel(final Execute exe, final Vector<String> fileNames, final Vector<File> baseDirs) throws IOException, BuildException {
        final String[] s = new String[fileNames.size()];
        fileNames.copyInto(s);
        final File[] b = new File[baseDirs.size()];
        baseDirs.copyInto(b);
        if (this.maxParallel <= 0 || s.length == 0) {
            final String[] command = this.getCommandline(s, b);
            this.log(Commandline.describeCommand(command), 3);
            exe.setCommandline(command);
            if (this.redirectorElement != null) {
                this.setupRedirector();
                this.redirectorElement.configure(this.redirector, null);
                exe.setStreamHandler(this.redirector.createHandler());
            }
            this.runExecute(exe);
        }
        else {
            int currentAmount;
            for (int stillToDo = fileNames.size(), currentOffset = 0; stillToDo > 0; stillToDo -= currentAmount, currentOffset += currentAmount) {
                currentAmount = Math.min(stillToDo, this.maxParallel);
                final String[] cs = new String[currentAmount];
                System.arraycopy(s, currentOffset, cs, 0, currentAmount);
                final File[] cb = new File[currentAmount];
                System.arraycopy(b, currentOffset, cb, 0, currentAmount);
                final String[] command2 = this.getCommandline(cs, cb);
                this.log(Commandline.describeCommand(command2), 3);
                exe.setCommandline(command2);
                if (this.redirectorElement != null) {
                    this.setupRedirector();
                    this.redirectorElement.configure(this.redirector, null);
                }
                if (this.redirectorElement != null || currentOffset > 0) {
                    exe.setStreamHandler(this.redirector.createHandler());
                }
                this.runExecute(exe);
            }
        }
    }
    
    private static void insertTargetFiles(final String[] targetFiles, final String[] arguments, final int insertPosition, final String prefix, final String suffix) {
        if (prefix.length() == 0 && suffix.length() == 0) {
            System.arraycopy(targetFiles, 0, arguments, insertPosition, targetFiles.length);
        }
        else {
            for (int i = 0; i < targetFiles.length; ++i) {
                arguments[insertPosition + i] = prefix + targetFiles[i] + suffix;
            }
        }
    }
    
    public static class FileDirBoth extends EnumeratedAttribute
    {
        public static final String FILE = "file";
        public static final String DIR = "dir";
        
        @Override
        public String[] getValues() {
            return new String[] { "file", "dir", "both" };
        }
    }
}
