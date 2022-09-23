// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import java.util.HashSet;
import org.apache.tools.ant.util.IdentityMapper;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.tools.ant.util.FileNameMapper;
import java.io.IOException;
import java.util.Date;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.PatternSet;
import java.util.Vector;
import org.apache.tools.ant.types.Mapper;
import java.io.File;
import org.apache.tools.ant.Task;

public class Expand extends Task
{
    private static final int BUFFER_SIZE = 1024;
    private File dest;
    private File source;
    private boolean overwrite;
    private Mapper mapperElement;
    private Vector<PatternSet> patternsets;
    private Union resources;
    private boolean resourcesSpecified;
    private boolean failOnEmptyArchive;
    private boolean stripAbsolutePathSpec;
    private boolean scanForUnicodeExtraFields;
    public static final String NATIVE_ENCODING = "native-encoding";
    private String encoding;
    public static final String ERROR_MULTIPLE_MAPPERS = "Cannot define more than one mapper";
    private static final FileUtils FILE_UTILS;
    
    public Expand() {
        this.overwrite = true;
        this.mapperElement = null;
        this.patternsets = new Vector<PatternSet>();
        this.resources = new Union();
        this.resourcesSpecified = false;
        this.failOnEmptyArchive = false;
        this.stripAbsolutePathSpec = false;
        this.scanForUnicodeExtraFields = true;
        this.encoding = "UTF8";
    }
    
    public void setFailOnEmptyArchive(final boolean b) {
        this.failOnEmptyArchive = b;
    }
    
    public boolean getFailOnEmptyArchive() {
        return this.failOnEmptyArchive;
    }
    
    @Override
    public void execute() throws BuildException {
        if ("expand".equals(this.getTaskType())) {
            this.log("!! expand is deprecated. Use unzip instead. !!");
        }
        if (this.source == null && !this.resourcesSpecified) {
            throw new BuildException("src attribute and/or resources must be specified");
        }
        if (this.dest == null) {
            throw new BuildException("Dest attribute must be specified");
        }
        if (this.dest.exists() && !this.dest.isDirectory()) {
            throw new BuildException("Dest must be a directory.", this.getLocation());
        }
        if (this.source != null) {
            if (this.source.isDirectory()) {
                throw new BuildException("Src must not be a directory. Use nested filesets instead.", this.getLocation());
            }
            if (!this.source.exists()) {
                throw new BuildException("src '" + this.source + "' doesn't exist.");
            }
            if (!this.source.canRead()) {
                throw new BuildException("src '" + this.source + "' cannot be read.");
            }
            this.expandFile(Expand.FILE_UTILS, this.source, this.dest);
        }
        for (final Resource r : this.resources) {
            if (!r.isExists()) {
                this.log("Skipping '" + r.getName() + "' because it doesn't exist.");
            }
            else {
                final FileProvider fp = r.as(FileProvider.class);
                if (fp != null) {
                    this.expandFile(Expand.FILE_UTILS, fp.getFile(), this.dest);
                }
                else {
                    this.expandResource(r, this.dest);
                }
            }
        }
    }
    
    protected void expandFile(final FileUtils fileUtils, final File srcF, final File dir) {
        this.log("Expanding: " + srcF + " into " + dir, 2);
        ZipFile zf = null;
        final FileNameMapper mapper = this.getMapper();
        if (!srcF.exists()) {
            throw new BuildException("Unable to expand " + srcF + " as the file does not exist", this.getLocation());
        }
        try {
            zf = new ZipFile(srcF, this.encoding, this.scanForUnicodeExtraFields);
            boolean empty = true;
            final Enumeration<ZipEntry> e = zf.getEntries();
            while (e.hasMoreElements()) {
                empty = false;
                final ZipEntry ze = e.nextElement();
                InputStream is = null;
                this.log("extracting " + ze.getName(), 4);
                try {
                    this.extractFile(fileUtils, srcF, dir, is = zf.getInputStream(ze), ze.getName(), new Date(ze.getTime()), ze.isDirectory(), mapper);
                }
                finally {
                    FileUtils.close(is);
                }
            }
            if (empty && this.getFailOnEmptyArchive()) {
                throw new BuildException("archive '" + srcF + "' is empty");
            }
            this.log("expand complete", 3);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe);
        }
        finally {
            ZipFile.closeQuietly(zf);
        }
    }
    
    protected void expandResource(final Resource srcR, final File dir) {
        throw new BuildException("only filesystem based resources are supported by this task.");
    }
    
    protected FileNameMapper getMapper() {
        FileNameMapper mapper = null;
        if (this.mapperElement != null) {
            mapper = this.mapperElement.getImplementation();
        }
        else {
            mapper = new IdentityMapper();
        }
        return mapper;
    }
    
    protected void extractFile(final FileUtils fileUtils, final File srcF, final File dir, final InputStream compressedInputStream, String entryName, final Date entryDate, final boolean isDirectory, final FileNameMapper mapper) throws IOException {
        if (this.stripAbsolutePathSpec && entryName.length() > 0 && (entryName.charAt(0) == File.separatorChar || entryName.charAt(0) == '/' || entryName.charAt(0) == '\\')) {
            this.log("stripped absolute path spec from " + entryName, 3);
            entryName = entryName.substring(1);
        }
        if (this.patternsets != null && this.patternsets.size() > 0) {
            final String name = entryName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            boolean included = false;
            final Set<String> includePatterns = new HashSet<String>();
            final Set<String> excludePatterns = new HashSet<String>();
            for (int size = this.patternsets.size(), v = 0; v < size; ++v) {
                final PatternSet p = this.patternsets.elementAt(v);
                String[] incls = p.getIncludePatterns(this.getProject());
                if (incls == null || incls.length == 0) {
                    incls = new String[] { "**" };
                }
                for (int w = 0; w < incls.length; ++w) {
                    String pattern = incls[w].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                    if (pattern.endsWith(File.separator)) {
                        pattern += "**";
                    }
                    includePatterns.add(pattern);
                }
                final String[] excls = p.getExcludePatterns(this.getProject());
                if (excls != null) {
                    for (int w2 = 0; w2 < excls.length; ++w2) {
                        String pattern2 = excls[w2].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                        if (pattern2.endsWith(File.separator)) {
                            pattern2 += "**";
                        }
                        excludePatterns.add(pattern2);
                    }
                }
            }
            String pattern3;
            for (Iterator<String> iter = includePatterns.iterator(); !included && iter.hasNext(); included = SelectorUtils.matchPath(pattern3, name)) {
                pattern3 = iter.next();
            }
            for (Iterator<String> iter = excludePatterns.iterator(); included && iter.hasNext(); included = !SelectorUtils.matchPath(pattern3, name)) {
                pattern3 = iter.next();
            }
            if (!included) {
                this.log("skipping " + entryName + " as it is excluded or not included.", 3);
                return;
            }
        }
        String[] mappedNames = mapper.mapFileName(entryName);
        if (mappedNames == null || mappedNames.length == 0) {
            mappedNames = new String[] { entryName };
        }
        final File f = fileUtils.resolveFile(dir, mappedNames[0]);
        try {
            if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime()) {
                this.log("Skipping " + f + " as it is up-to-date", 4);
                return;
            }
            this.log("expanding " + entryName + " to " + f, 3);
            final File dirF = f.getParentFile();
            if (dirF != null) {
                dirF.mkdirs();
            }
            if (isDirectory) {
                f.mkdirs();
            }
            else {
                final byte[] buffer = new byte[1024];
                int length = 0;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    while ((length = compressedInputStream.read(buffer)) >= 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    fos = null;
                }
                finally {
                    FileUtils.close(fos);
                }
            }
            fileUtils.setFileLastModified(f, entryDate.getTime());
        }
        catch (FileNotFoundException ex) {
            this.log("Unable to expand to file " + f.getPath(), ex, 1);
        }
    }
    
    public void setDest(final File d) {
        this.dest = d;
    }
    
    public void setSrc(final File s) {
        this.source = s;
    }
    
    public void setOverwrite(final boolean b) {
        this.overwrite = b;
    }
    
    public void addPatternset(final PatternSet set) {
        this.patternsets.addElement(set);
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void add(final ResourceCollection rc) {
        this.resourcesSpecified = true;
        this.resources.add(rc);
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
        this.internalSetEncoding(encoding);
    }
    
    protected void internalSetEncoding(String encoding) {
        if ("native-encoding".equals(encoding)) {
            encoding = null;
        }
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setStripAbsolutePathSpec(final boolean b) {
        this.stripAbsolutePathSpec = b;
    }
    
    public void setScanForUnicodeExtraFields(final boolean b) {
        this.internalSetScanForUnicodeExtraFields(b);
    }
    
    protected void internalSetScanForUnicodeExtraFields(final boolean b) {
        this.scanForUnicodeExtraFields = b;
    }
    
    public boolean getScanForUnicodeExtraFields() {
        return this.scanForUnicodeExtraFields;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
