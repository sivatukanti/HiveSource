// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.FlatFileNameMapper;
import java.util.Collection;
import java.util.Arrays;
import java.io.IOException;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.FileProvider;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.LinkedHashtable;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.Mapper;
import java.util.Hashtable;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public class Copy extends Task
{
    private static final String MSG_WHEN_COPYING_EMPTY_RC_TO_FILE = "Cannot perform operation from directory to file.";
    static final File NULL_FILE_PLACEHOLDER;
    static final String LINE_SEPARATOR;
    protected File file;
    protected File destFile;
    protected File destDir;
    protected Vector<ResourceCollection> rcs;
    protected Vector<ResourceCollection> filesets;
    private boolean enableMultipleMappings;
    protected boolean filtering;
    protected boolean preserveLastModified;
    protected boolean forceOverwrite;
    protected boolean flatten;
    protected int verbosity;
    protected boolean includeEmpty;
    protected boolean failonerror;
    protected Hashtable<String, String[]> fileCopyMap;
    protected Hashtable<String, String[]> dirCopyMap;
    protected Hashtable<File, File> completeDirMap;
    protected Mapper mapperElement;
    protected FileUtils fileUtils;
    private Vector<FilterChain> filterChains;
    private Vector<FilterSet> filterSets;
    private String inputEncoding;
    private String outputEncoding;
    private long granularity;
    private boolean force;
    private boolean quiet;
    private Resource singleResource;
    
    public Copy() {
        this.file = null;
        this.destFile = null;
        this.destDir = null;
        this.rcs = new Vector<ResourceCollection>();
        this.filesets = this.rcs;
        this.enableMultipleMappings = false;
        this.filtering = false;
        this.preserveLastModified = false;
        this.forceOverwrite = false;
        this.flatten = false;
        this.verbosity = 3;
        this.includeEmpty = true;
        this.failonerror = true;
        this.fileCopyMap = new LinkedHashtable<String, String[]>();
        this.dirCopyMap = new LinkedHashtable<String, String[]>();
        this.completeDirMap = new LinkedHashtable<File, File>();
        this.mapperElement = null;
        this.filterChains = new Vector<FilterChain>();
        this.filterSets = new Vector<FilterSet>();
        this.inputEncoding = null;
        this.outputEncoding = null;
        this.granularity = 0L;
        this.force = false;
        this.quiet = false;
        this.singleResource = null;
        this.fileUtils = FileUtils.getFileUtils();
        this.granularity = this.fileUtils.getFileTimestampGranularity();
    }
    
    protected FileUtils getFileUtils() {
        return this.fileUtils;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setTofile(final File destFile) {
        this.destFile = destFile;
    }
    
    public void setTodir(final File destDir) {
        this.destDir = destDir;
    }
    
    public FilterChain createFilterChain() {
        final FilterChain filterChain = new FilterChain();
        this.filterChains.addElement(filterChain);
        return filterChain;
    }
    
    public FilterSet createFilterSet() {
        final FilterSet filterSet = new FilterSet();
        this.filterSets.addElement(filterSet);
        return filterSet;
    }
    
    @Deprecated
    public void setPreserveLastModified(final String preserve) {
        this.setPreserveLastModified(Project.toBoolean(preserve));
    }
    
    public void setPreserveLastModified(final boolean preserve) {
        this.preserveLastModified = preserve;
    }
    
    public boolean getPreserveLastModified() {
        return this.preserveLastModified;
    }
    
    protected Vector<FilterSet> getFilterSets() {
        return this.filterSets;
    }
    
    protected Vector<FilterChain> getFilterChains() {
        return this.filterChains;
    }
    
    public void setFiltering(final boolean filtering) {
        this.filtering = filtering;
    }
    
    public void setOverwrite(final boolean overwrite) {
        this.forceOverwrite = overwrite;
    }
    
    public void setForce(final boolean f) {
        this.force = f;
    }
    
    public boolean getForce() {
        return this.force;
    }
    
    public void setFlatten(final boolean flatten) {
        this.flatten = flatten;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbosity = (verbose ? 2 : 3);
    }
    
    public void setIncludeEmptyDirs(final boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }
    
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
    }
    
    public void setEnableMultipleMappings(final boolean enableMultipleMappings) {
        this.enableMultipleMappings = enableMultipleMappings;
    }
    
    public boolean isEnableMultipleMapping() {
        return this.enableMultipleMappings;
    }
    
    public void setFailOnError(final boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void add(final ResourceCollection res) {
        this.rcs.add(res);
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
    
    public void setEncoding(final String encoding) {
        this.inputEncoding = encoding;
        if (this.outputEncoding == null) {
            this.outputEncoding = encoding;
        }
    }
    
    public String getEncoding() {
        return this.inputEncoding;
    }
    
    public void setOutputEncoding(final String encoding) {
        this.outputEncoding = encoding;
    }
    
    public String getOutputEncoding() {
        return this.outputEncoding;
    }
    
    public void setGranularity(final long granularity) {
        this.granularity = granularity;
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedFile = this.file;
        final File savedDestFile = this.destFile;
        final File savedDestDir = this.destDir;
        ResourceCollection savedRc = null;
        while (true) {
            if (this.file == null && this.destFile != null && this.rcs.size() == 1) {
                savedRc = this.rcs.elementAt(0);
                try {
                    try {
                        this.validateAttributes();
                    }
                    catch (BuildException e) {
                        if (this.failonerror || !this.getMessage(e).equals("Cannot perform operation from directory to file.")) {
                            throw e;
                        }
                        this.log("Warning: " + this.getMessage(e), 0);
                        return;
                    }
                    this.copySingleFile();
                    final HashMap<File, List<String>> filesByBasedir = new HashMap<File, List<String>>();
                    final HashMap<File, List<String>> dirsByBasedir = new HashMap<File, List<String>>();
                    final HashSet<File> baseDirs = new HashSet<File>();
                    final ArrayList<Resource> nonFileResources = new ArrayList<Resource>();
                    for (int size = this.rcs.size(), i = 0; i < size; ++i) {
                        final ResourceCollection rc = this.rcs.elementAt(i);
                        if (rc instanceof FileSet && rc.isFilesystemOnly()) {
                            final FileSet fs = (FileSet)rc;
                            DirectoryScanner ds = null;
                            try {
                                ds = fs.getDirectoryScanner(this.getProject());
                            }
                            catch (BuildException e2) {
                                if (this.failonerror || !this.getMessage(e2).endsWith(" does not exist.")) {
                                    throw e2;
                                }
                                if (!this.quiet) {
                                    this.log("Warning: " + this.getMessage(e2), 0);
                                }
                                continue;
                            }
                            final File fromDir = fs.getDir(this.getProject());
                            final String[] srcFiles = ds.getIncludedFiles();
                            final String[] srcDirs = ds.getIncludedDirectories();
                            if (!this.flatten && this.mapperElement == null && ds.isEverythingIncluded() && !fs.hasPatterns()) {
                                this.completeDirMap.put(fromDir, this.destDir);
                            }
                            add(fromDir, srcFiles, filesByBasedir);
                            add(fromDir, srcDirs, dirsByBasedir);
                            baseDirs.add(fromDir);
                        }
                        else {
                            if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                                throw new BuildException("Only FileSystem resources are supported.");
                            }
                            for (final Resource r : rc) {
                                if (!r.isExists()) {
                                    final String message = "Warning: Could not find resource " + r.toLongString() + " to copy.";
                                    if (this.failonerror) {
                                        throw new BuildException(message);
                                    }
                                    if (this.quiet) {
                                        continue;
                                    }
                                    this.log(message, 0);
                                }
                                else {
                                    File baseDir = Copy.NULL_FILE_PLACEHOLDER;
                                    String name = r.getName();
                                    final FileProvider fp = r.as(FileProvider.class);
                                    if (fp != null) {
                                        final FileResource fr = ResourceUtils.asFileResource(fp);
                                        baseDir = getKeyFile(fr.getBaseDir());
                                        if (fr.getBaseDir() == null) {
                                            name = fr.getFile().getAbsolutePath();
                                        }
                                    }
                                    if (r.isDirectory() || fp != null) {
                                        add(baseDir, name, r.isDirectory() ? dirsByBasedir : filesByBasedir);
                                        baseDirs.add(baseDir);
                                    }
                                    else {
                                        nonFileResources.add(r);
                                    }
                                }
                            }
                        }
                    }
                    this.iterateOverBaseDirs(baseDirs, dirsByBasedir, filesByBasedir);
                    try {
                        this.doFileOperations();
                    }
                    catch (BuildException e3) {
                        if (this.failonerror) {
                            throw e3;
                        }
                        if (!this.quiet) {
                            this.log("Warning: " + this.getMessage(e3), 0);
                        }
                    }
                    if (nonFileResources.size() > 0 || this.singleResource != null) {
                        final Resource[] nonFiles = nonFileResources.toArray(new Resource[nonFileResources.size()]);
                        final Map<Resource, String[]> map = this.scan(nonFiles, this.destDir);
                        if (this.singleResource != null) {
                            map.put(this.singleResource, new String[] { this.destFile.getAbsolutePath() });
                        }
                        try {
                            this.doResourceOperations(map);
                        }
                        catch (BuildException e4) {
                            if (this.failonerror) {
                                throw e4;
                            }
                            if (!this.quiet) {
                                this.log("Warning: " + this.getMessage(e4), 0);
                            }
                        }
                    }
                }
                finally {
                    this.singleResource = null;
                    this.file = savedFile;
                    this.destFile = savedDestFile;
                    this.destDir = savedDestDir;
                    if (savedRc != null) {
                        this.rcs.insertElementAt(savedRc, 0);
                    }
                    this.fileCopyMap.clear();
                    this.dirCopyMap.clear();
                    this.completeDirMap.clear();
                }
                return;
            }
            continue;
        }
    }
    
    private void copySingleFile() {
        if (this.file != null) {
            if (this.file.exists()) {
                if (this.destFile == null) {
                    this.destFile = new File(this.destDir, this.file.getName());
                }
                if (this.forceOverwrite || !this.destFile.exists() || this.file.lastModified() - this.granularity > this.destFile.lastModified()) {
                    this.fileCopyMap.put(this.file.getAbsolutePath(), new String[] { this.destFile.getAbsolutePath() });
                }
                else {
                    this.log(this.file + " omitted as " + this.destFile + " is up to date.", 3);
                }
            }
            else {
                final String message = "Warning: Could not find file " + this.file.getAbsolutePath() + " to copy.";
                if (this.failonerror) {
                    throw new BuildException(message);
                }
                if (!this.quiet) {
                    this.log(message, 0);
                }
            }
        }
    }
    
    private void iterateOverBaseDirs(final HashSet<File> baseDirs, final HashMap<File, List<String>> dirsByBasedir, final HashMap<File, List<String>> filesByBasedir) {
        for (final File f : baseDirs) {
            final List<String> files = filesByBasedir.get(f);
            final List<String> dirs = dirsByBasedir.get(f);
            String[] srcFiles = new String[0];
            if (files != null) {
                srcFiles = files.toArray(srcFiles);
            }
            String[] srcDirs = new String[0];
            if (dirs != null) {
                srcDirs = dirs.toArray(srcDirs);
            }
            this.scan((f == Copy.NULL_FILE_PLACEHOLDER) ? null : f, this.destDir, srcFiles, srcDirs);
        }
    }
    
    protected void validateAttributes() throws BuildException {
        if (this.file == null && this.rcs.size() == 0) {
            throw new BuildException("Specify at least one source--a file or a resource collection.");
        }
        if (this.destFile != null && this.destDir != null) {
            throw new BuildException("Only one of tofile and todir may be set.");
        }
        if (this.destFile == null && this.destDir == null) {
            throw new BuildException("One of tofile or todir must be set.");
        }
        if (this.file != null && this.file.isDirectory()) {
            throw new BuildException("Use a resource collection to copy directories.");
        }
        if (this.destFile != null && this.rcs.size() > 0) {
            if (this.rcs.size() > 1) {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
            final ResourceCollection rc = this.rcs.elementAt(0);
            if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                throw new BuildException("Only FileSystem resources are supported.");
            }
            if (rc.size() == 0) {
                throw new BuildException("Cannot perform operation from directory to file.");
            }
            if (rc.size() != 1) {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
            final Resource res = rc.iterator().next();
            final FileProvider r = res.as(FileProvider.class);
            if (this.file != null) {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
            if (r != null) {
                this.file = r.getFile();
            }
            else {
                this.singleResource = res;
            }
            this.rcs.removeElementAt(0);
        }
        if (this.destFile != null) {
            this.destDir = this.destFile.getParentFile();
        }
    }
    
    protected void scan(final File fromDir, final File toDir, final String[] files, final String[] dirs) {
        final FileNameMapper mapper = this.getMapper();
        this.buildMap(fromDir, toDir, files, mapper, this.fileCopyMap);
        if (this.includeEmpty) {
            this.buildMap(fromDir, toDir, dirs, mapper, this.dirCopyMap);
        }
    }
    
    protected Map<Resource, String[]> scan(final Resource[] fromResources, final File toDir) {
        return this.buildMap(fromResources, toDir, this.getMapper());
    }
    
    protected void buildMap(final File fromDir, final File toDir, final String[] names, final FileNameMapper mapper, final Hashtable<String, String[]> map) {
        String[] toCopy = null;
        if (this.forceOverwrite) {
            final Vector<String> v = new Vector<String>();
            for (int i = 0; i < names.length; ++i) {
                if (mapper.mapFileName(names[i]) != null) {
                    v.addElement(names[i]);
                }
            }
            toCopy = new String[v.size()];
            v.copyInto(toCopy);
        }
        else {
            final SourceFileScanner ds = new SourceFileScanner(this);
            toCopy = ds.restrict(names, fromDir, toDir, mapper, this.granularity);
        }
        for (int j = 0; j < toCopy.length; ++j) {
            final File src = new File(fromDir, toCopy[j]);
            final String[] mappedFiles = mapper.mapFileName(toCopy[j]);
            if (!this.enableMultipleMappings) {
                map.put(src.getAbsolutePath(), new String[] { new File(toDir, mappedFiles[0]).getAbsolutePath() });
            }
            else {
                for (int k = 0; k < mappedFiles.length; ++k) {
                    mappedFiles[k] = new File(toDir, mappedFiles[k]).getAbsolutePath();
                }
                map.put(src.getAbsolutePath(), mappedFiles);
            }
        }
    }
    
    protected Map<Resource, String[]> buildMap(final Resource[] fromResources, final File toDir, final FileNameMapper mapper) {
        final HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
        Resource[] toCopy = null;
        if (this.forceOverwrite) {
            final Vector<Resource> v = new Vector<Resource>();
            for (int i = 0; i < fromResources.length; ++i) {
                if (mapper.mapFileName(fromResources[i].getName()) != null) {
                    v.addElement(fromResources[i]);
                }
            }
            toCopy = new Resource[v.size()];
            v.copyInto(toCopy);
        }
        else {
            toCopy = ResourceUtils.selectOutOfDateSources(this, fromResources, mapper, new ResourceFactory() {
                public Resource getResource(final String name) {
                    return new FileResource(toDir, name);
                }
            }, this.granularity);
        }
        for (int j = 0; j < toCopy.length; ++j) {
            final String[] mappedFiles = mapper.mapFileName(toCopy[j].getName());
            for (int k = 0; k < mappedFiles.length; ++k) {
                if (mappedFiles[k] == null) {
                    throw new BuildException("Can't copy a resource without a name if the mapper doesn't provide one.");
                }
            }
            if (!this.enableMultipleMappings) {
                map.put(toCopy[j], new String[] { new File(toDir, mappedFiles[0]).getAbsolutePath() });
            }
            else {
                for (int l = 0; l < mappedFiles.length; ++l) {
                    mappedFiles[l] = new File(toDir, mappedFiles[l]).getAbsolutePath();
                }
                map.put(toCopy[j], mappedFiles);
            }
        }
        return map;
    }
    
    protected void doFileOperations() {
        if (this.fileCopyMap.size() > 0) {
            this.log("Copying " + this.fileCopyMap.size() + " file" + ((this.fileCopyMap.size() == 1) ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (final Map.Entry<String, String[]> e : this.fileCopyMap.entrySet()) {
                final String fromFile = e.getKey();
                final String[] toFiles = e.getValue();
                for (int i = 0; i < toFiles.length; ++i) {
                    final String toFile = toFiles[i];
                    if (fromFile.equals(toFile)) {
                        this.log("Skipping self-copy of " + fromFile, this.verbosity);
                    }
                    else {
                        try {
                            this.log("Copying " + fromFile + " to " + toFile, this.verbosity);
                            final FilterSetCollection executionFilters = new FilterSetCollection();
                            if (this.filtering) {
                                executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
                            }
                            for (final FilterSet filterSet : this.filterSets) {
                                executionFilters.addFilterSet(filterSet);
                            }
                            this.fileUtils.copyFile(new File(fromFile), new File(toFile), executionFilters, this.filterChains, this.forceOverwrite, this.preserveLastModified, false, this.inputEncoding, this.outputEncoding, this.getProject(), this.getForce());
                        }
                        catch (IOException ioe) {
                            String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + this.getDueTo(ioe);
                            final File targetFile = new File(toFile);
                            if (targetFile.exists() && !targetFile.delete()) {
                                msg = msg + " and I couldn't delete the corrupt " + toFile;
                            }
                            if (this.failonerror) {
                                throw new BuildException(msg, ioe, this.getLocation());
                            }
                            this.log(msg, 0);
                        }
                    }
                }
            }
        }
        if (this.includeEmpty) {
            int createCount = 0;
            for (final String[] dirs : this.dirCopyMap.values()) {
                for (int j = 0; j < dirs.length; ++j) {
                    final File d = new File(dirs[j]);
                    if (!d.exists()) {
                        if (!d.mkdirs()) {
                            this.log("Unable to create directory " + d.getAbsolutePath(), 0);
                        }
                        else {
                            ++createCount;
                        }
                    }
                }
            }
            if (createCount > 0) {
                this.log("Copied " + this.dirCopyMap.size() + " empty director" + ((this.dirCopyMap.size() == 1) ? "y" : "ies") + " to " + createCount + " empty director" + ((createCount == 1) ? "y" : "ies") + " under " + this.destDir.getAbsolutePath());
            }
        }
    }
    
    protected void doResourceOperations(final Map<Resource, String[]> map) {
        if (map.size() > 0) {
            this.log("Copying " + map.size() + " resource" + ((map.size() == 1) ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (final Map.Entry<Resource, String[]> e : map.entrySet()) {
                final Resource fromResource = e.getKey();
                for (final String toFile : e.getValue()) {
                    try {
                        this.log("Copying " + fromResource + " to " + toFile, this.verbosity);
                        final FilterSetCollection executionFilters = new FilterSetCollection();
                        if (this.filtering) {
                            executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
                        }
                        for (final FilterSet filterSet : this.filterSets) {
                            executionFilters.addFilterSet(filterSet);
                        }
                        ResourceUtils.copyResource(fromResource, new FileResource(this.destDir, toFile), executionFilters, this.filterChains, this.forceOverwrite, this.preserveLastModified, false, this.inputEncoding, this.outputEncoding, this.getProject(), this.getForce());
                    }
                    catch (IOException ioe) {
                        String msg = "Failed to copy " + fromResource + " to " + toFile + " due to " + this.getDueTo(ioe);
                        final File targetFile = new File(toFile);
                        if (targetFile.exists() && !targetFile.delete()) {
                            msg = msg + " and I couldn't delete the corrupt " + toFile;
                        }
                        if (this.failonerror) {
                            throw new BuildException(msg, ioe, this.getLocation());
                        }
                        this.log(msg, 0);
                    }
                }
            }
        }
    }
    
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(Copy.class);
    }
    
    private static void add(File baseDir, final String[] names, final Map<File, List<String>> m) {
        if (names != null) {
            baseDir = getKeyFile(baseDir);
            List<String> l = m.get(baseDir);
            if (l == null) {
                l = new ArrayList<String>(names.length);
                m.put(baseDir, l);
            }
            l.addAll(Arrays.asList(names));
        }
    }
    
    private static void add(final File baseDir, final String name, final Map<File, List<String>> m) {
        if (name != null) {
            add(baseDir, new String[] { name }, m);
        }
    }
    
    private static File getKeyFile(final File f) {
        return (f == null) ? Copy.NULL_FILE_PLACEHOLDER : f;
    }
    
    private FileNameMapper getMapper() {
        FileNameMapper mapper = null;
        if (this.mapperElement != null) {
            mapper = this.mapperElement.getImplementation();
        }
        else if (this.flatten) {
            mapper = new FlatFileNameMapper();
        }
        else {
            mapper = new IdentityMapper();
        }
        return mapper;
    }
    
    private String getMessage(final Exception ex) {
        return (ex.getMessage() == null) ? ex.toString() : ex.getMessage();
    }
    
    private String getDueTo(final Exception ex) {
        final boolean baseIOException = ex.getClass() == IOException.class;
        final StringBuffer message = new StringBuffer();
        if (!baseIOException || ex.getMessage() == null) {
            message.append(ex.getClass().getName());
        }
        if (ex.getMessage() != null) {
            if (!baseIOException) {
                message.append(" ");
            }
            message.append(ex.getMessage());
        }
        if (ex.getClass().getName().indexOf("MalformedInput") != -1) {
            message.append(Copy.LINE_SEPARATOR);
            message.append("This is normally due to the input file containing invalid");
            message.append(Copy.LINE_SEPARATOR);
            message.append("bytes for the character encoding used : ");
            message.append((this.inputEncoding == null) ? this.fileUtils.getDefaultEncoding() : this.inputEncoding);
            message.append(Copy.LINE_SEPARATOR);
        }
        return message.toString();
    }
    
    static {
        NULL_FILE_PLACEHOLDER = new File("/NULL_FILE");
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
