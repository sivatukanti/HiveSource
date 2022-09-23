// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.zip.JarMarker;
import org.apache.tools.ant.types.ArchiveFileSet;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Collections;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.StringTokenizer;
import java.util.Collection;
import java.io.ByteArrayInputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.ant.types.Path;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.spi.Service;
import java.util.List;

public class Jar extends Zip
{
    private static final String INDEX_NAME = "META-INF/INDEX.LIST";
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private List<Service> serviceList;
    private Manifest configuredManifest;
    private Manifest savedConfiguredManifest;
    private Manifest filesetManifest;
    private Manifest originalManifest;
    private FilesetManifestConfig filesetManifestConfig;
    private boolean mergeManifestsMain;
    private Manifest manifest;
    private String manifestEncoding;
    private File manifestFile;
    private boolean index;
    private boolean indexMetaInf;
    private boolean createEmpty;
    private Vector<String> rootEntries;
    private Path indexJars;
    private StrictMode strict;
    private boolean mergeClassPaths;
    private boolean flattenClassPaths;
    private static final ZipExtraField[] JAR_MARKER;
    
    public Jar() {
        this.serviceList = new ArrayList<Service>();
        this.mergeManifestsMain = true;
        this.index = false;
        this.indexMetaInf = false;
        this.createEmpty = false;
        this.strict = new StrictMode("ignore");
        this.mergeClassPaths = false;
        this.flattenClassPaths = false;
        this.archiveType = "jar";
        this.emptyBehavior = "create";
        this.setEncoding("UTF8");
        this.setZip64Mode(Zip64ModeAttribute.NEVER);
        this.rootEntries = new Vector<String>();
    }
    
    @Override
    public void setWhenempty(final WhenEmpty we) {
        this.log("JARs are never empty, they contain at least a manifest file", 1);
    }
    
    public void setWhenmanifestonly(final WhenEmpty we) {
        this.emptyBehavior = we.getValue();
    }
    
    public void setStrict(final StrictMode strict) {
        this.strict = strict;
    }
    
    @Deprecated
    public void setJarfile(final File jarFile) {
        this.setDestFile(jarFile);
    }
    
    public void setIndex(final boolean flag) {
        this.index = flag;
    }
    
    public void setIndexMetaInf(final boolean flag) {
        this.indexMetaInf = flag;
    }
    
    public void setManifestEncoding(final String manifestEncoding) {
        this.manifestEncoding = manifestEncoding;
    }
    
    public void addConfiguredManifest(final Manifest newManifest) throws ManifestException {
        if (this.configuredManifest == null) {
            this.configuredManifest = newManifest;
        }
        else {
            this.configuredManifest.merge(newManifest, false, this.mergeClassPaths);
        }
        this.savedConfiguredManifest = this.configuredManifest;
    }
    
    public void setManifest(final File manifestFile) {
        if (!manifestFile.exists()) {
            throw new BuildException("Manifest file: " + manifestFile + " does not exist.", this.getLocation());
        }
        this.manifestFile = manifestFile;
    }
    
    private Manifest getManifest(final File manifestFile) {
        Manifest newManifest = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = new FileInputStream(manifestFile);
            if (this.manifestEncoding == null) {
                isr = new InputStreamReader(fis);
            }
            else {
                isr = new InputStreamReader(fis, this.manifestEncoding);
            }
            newManifest = this.getManifest(isr);
        }
        catch (UnsupportedEncodingException e) {
            throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
        }
        catch (IOException e2) {
            throw new BuildException("Unable to read manifest file: " + manifestFile + " (" + e2.getMessage() + ")", e2);
        }
        finally {
            FileUtils.close(isr);
        }
        return newManifest;
    }
    
    private Manifest getManifestFromJar(final File jarFile) throws IOException {
        ZipFile zf = null;
        try {
            zf = new ZipFile(jarFile);
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = (ZipEntry)e.nextElement();
                if (ze.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    final InputStreamReader isr = new InputStreamReader(zf.getInputStream(ze), "UTF-8");
                    return this.getManifest(isr);
                }
            }
            return null;
        }
        finally {
            if (zf != null) {
                try {
                    zf.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private Manifest getManifest(final Reader r) {
        Manifest newManifest = null;
        try {
            newManifest = new Manifest(r);
        }
        catch (ManifestException e) {
            this.log("Manifest is invalid: " + e.getMessage(), 0);
            throw new BuildException("Invalid Manifest: " + this.manifestFile, e, this.getLocation());
        }
        catch (IOException e2) {
            throw new BuildException("Unable to read manifest file (" + e2.getMessage() + ")", e2);
        }
        return newManifest;
    }
    
    private boolean jarHasIndex(final File jarFile) throws IOException {
        ZipFile zf = null;
        try {
            zf = new ZipFile(jarFile);
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = (ZipEntry)e.nextElement();
                if (ze.getName().equalsIgnoreCase("META-INF/INDEX.LIST")) {
                    return true;
                }
            }
            return false;
        }
        finally {
            if (zf != null) {
                try {
                    zf.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public void setFilesetmanifest(final FilesetManifestConfig config) {
        this.filesetManifestConfig = config;
        this.mergeManifestsMain = "merge".equals(config.getValue());
        if (this.filesetManifestConfig != null && !this.filesetManifestConfig.getValue().equals("skip")) {
            this.doubleFilePass = true;
        }
    }
    
    public void addMetainf(final ZipFileSet fs) {
        fs.setPrefix("META-INF/");
        super.addFileset(fs);
    }
    
    public void addConfiguredIndexJars(final Path p) {
        if (this.indexJars == null) {
            this.indexJars = new Path(this.getProject());
        }
        this.indexJars.append(p);
    }
    
    public void addConfiguredService(final Service service) {
        service.check();
        this.serviceList.add(service);
    }
    
    private void writeServices(final ZipOutputStream zOut) throws IOException {
        for (final Service service : this.serviceList) {
            InputStream is = null;
            try {
                is = service.getAsStream();
                super.zipFile(is, zOut, "META-INF/services/" + service.getType(), System.currentTimeMillis(), null, 33188);
            }
            finally {
                FileUtils.close(is);
            }
        }
    }
    
    public void setMergeClassPathAttributes(final boolean b) {
        this.mergeClassPaths = b;
    }
    
    public void setFlattenAttributes(final boolean b) {
        this.flattenClassPaths = b;
    }
    
    @Override
    protected void initZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
        if (!this.skipWriting) {
            final Manifest jarManifest = this.createManifest();
            this.writeManifest(zOut, jarManifest);
            this.writeServices(zOut);
        }
    }
    
    private Manifest createManifest() throws BuildException {
        try {
            final Manifest finalManifest = Manifest.getDefaultManifest();
            if (this.manifest == null && this.manifestFile != null) {
                this.manifest = this.getManifest(this.manifestFile);
            }
            if (this.isInUpdateMode()) {
                finalManifest.merge(this.originalManifest, false, this.mergeClassPaths);
            }
            finalManifest.merge(this.filesetManifest, false, this.mergeClassPaths);
            finalManifest.merge(this.configuredManifest, !this.mergeManifestsMain, this.mergeClassPaths);
            finalManifest.merge(this.manifest, !this.mergeManifestsMain, this.mergeClassPaths);
            return finalManifest;
        }
        catch (ManifestException e) {
            this.log("Manifest is invalid: " + e.getMessage(), 0);
            throw new BuildException("Invalid Manifest", e, this.getLocation());
        }
    }
    
    private void writeManifest(final ZipOutputStream zOut, final Manifest manifest) throws IOException {
        final Enumeration<String> e = manifest.getWarnings();
        while (e.hasMoreElements()) {
            this.log("Manifest warning: " + e.nextElement(), 1);
        }
        this.zipDir((Resource)null, zOut, "META-INF/", 16877, Jar.JAR_MARKER);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
        final PrintWriter writer = new PrintWriter(osw);
        manifest.write(writer, this.flattenClassPaths);
        if (writer.checkError()) {
            throw new IOException("Encountered an error writing the manifest");
        }
        writer.close();
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try {
            super.zipFile(bais, zOut, "META-INF/MANIFEST.MF", System.currentTimeMillis(), null, 33188);
        }
        finally {
            FileUtils.close(bais);
        }
        super.initZipOutputStream(zOut);
    }
    
    @Override
    protected void finalizeZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
        if (this.index) {
            this.createIndexList(zOut);
        }
    }
    
    private void createIndexList(final ZipOutputStream zOut) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, "UTF8"));
        writer.println("JarIndex-Version: 1.0");
        writer.println();
        writer.println(this.zipFile.getName());
        this.writeIndexLikeList(new ArrayList<String>(this.addedDirs.keySet()), this.rootEntries, writer);
        writer.println();
        if (this.indexJars != null) {
            final Manifest mf = this.createManifest();
            final Manifest.Attribute classpath = mf.getMainSection().getAttribute("Class-Path");
            String[] cpEntries = null;
            if (classpath != null && classpath.getValue() != null) {
                final StringTokenizer tok = new StringTokenizer(classpath.getValue(), " ");
                cpEntries = new String[tok.countTokens()];
                int c = 0;
                while (tok.hasMoreTokens()) {
                    cpEntries[c++] = tok.nextToken();
                }
            }
            final String[] indexJarEntries = this.indexJars.list();
            for (int i = 0; i < indexJarEntries.length; ++i) {
                final String name = findJarName(indexJarEntries[i], cpEntries);
                if (name != null) {
                    final ArrayList<String> dirs = new ArrayList<String>();
                    final ArrayList<String> files = new ArrayList<String>();
                    grabFilesAndDirs(indexJarEntries[i], dirs, files);
                    if (dirs.size() + files.size() > 0) {
                        writer.println(name);
                        this.writeIndexLikeList(dirs, files, writer);
                        writer.println();
                    }
                }
            }
        }
        if (writer.checkError()) {
            throw new IOException("Encountered an error writing jar index");
        }
        writer.close();
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try {
            super.zipFile(bais, zOut, "META-INF/INDEX.LIST", System.currentTimeMillis(), null, 33188);
        }
        finally {
            FileUtils.close(bais);
        }
    }
    
    @Override
    protected void zipFile(final InputStream is, final ZipOutputStream zOut, final String vPath, final long lastModified, final File fromArchive, final int mode) throws IOException {
        if ("META-INF/MANIFEST.MF".equalsIgnoreCase(vPath)) {
            if (this.isFirstPass()) {
                this.filesetManifest(fromArchive, is);
            }
        }
        else if ("META-INF/INDEX.LIST".equalsIgnoreCase(vPath) && this.index) {
            this.logWhenWriting("Warning: selected " + this.archiveType + " files include a " + "META-INF/INDEX.LIST" + " which will" + " be replaced by a newly generated one.", 1);
        }
        else {
            if (this.index && vPath.indexOf("/") == -1) {
                this.rootEntries.addElement(vPath);
            }
            super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode);
        }
    }
    
    private void filesetManifest(final File file, final InputStream is) throws IOException {
        if (this.manifestFile != null && this.manifestFile.equals(file)) {
            this.log("Found manifest " + file, 3);
            try {
                if (is != null) {
                    InputStreamReader isr;
                    if (this.manifestEncoding == null) {
                        isr = new InputStreamReader(is);
                    }
                    else {
                        isr = new InputStreamReader(is, this.manifestEncoding);
                    }
                    this.manifest = this.getManifest(isr);
                }
                else {
                    this.manifest = this.getManifest(file);
                }
                return;
            }
            catch (UnsupportedEncodingException e) {
                throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
            }
        }
        if (this.filesetManifestConfig != null && !this.filesetManifestConfig.getValue().equals("skip")) {
            this.logWhenWriting("Found manifest to merge in file " + file, 3);
            try {
                Manifest newManifest = null;
                if (is != null) {
                    InputStreamReader isr2;
                    if (this.manifestEncoding == null) {
                        isr2 = new InputStreamReader(is);
                    }
                    else {
                        isr2 = new InputStreamReader(is, this.manifestEncoding);
                    }
                    newManifest = this.getManifest(isr2);
                }
                else {
                    newManifest = this.getManifest(file);
                }
                if (this.filesetManifest == null) {
                    this.filesetManifest = newManifest;
                }
                else {
                    this.filesetManifest.merge(newManifest, false, this.mergeClassPaths);
                }
            }
            catch (UnsupportedEncodingException e) {
                throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
            }
            catch (ManifestException e2) {
                this.log("Manifest in file " + file + " is invalid: " + e2.getMessage(), 0);
                throw new BuildException("Invalid Manifest", e2, this.getLocation());
            }
        }
    }
    
    @Override
    protected ArchiveState getResourcesToAdd(final ResourceCollection[] rcs, final File zipFile, boolean needsUpdate) throws BuildException {
        if (this.skipWriting) {
            final Resource[][] manifests = this.grabManifests(rcs);
            int count = 0;
            for (int i = 0; i < manifests.length; ++i) {
                count += manifests[i].length;
            }
            this.log("found a total of " + count + " manifests in " + manifests.length + " resource collections", 3);
            return new ArchiveState(true, manifests);
        }
        if (zipFile.exists()) {
            try {
                this.originalManifest = this.getManifestFromJar(zipFile);
                if (this.originalManifest == null) {
                    this.log("Updating jar since the current jar has no manifest", 3);
                    needsUpdate = true;
                }
                else {
                    final Manifest mf = this.createManifest();
                    if (!mf.equals(this.originalManifest)) {
                        this.log("Updating jar since jar manifest has changed", 3);
                        needsUpdate = true;
                    }
                }
            }
            catch (Throwable t) {
                this.log("error while reading original manifest in file: " + zipFile.toString() + " due to " + t.getMessage(), 1);
                needsUpdate = true;
            }
        }
        else {
            needsUpdate = true;
        }
        this.createEmpty = needsUpdate;
        if (!needsUpdate && this.index) {
            try {
                needsUpdate = !this.jarHasIndex(zipFile);
            }
            catch (IOException e) {
                needsUpdate = true;
            }
        }
        return super.getResourcesToAdd(rcs, zipFile, needsUpdate);
    }
    
    @Override
    protected boolean createEmptyZip(final File zipFile) throws BuildException {
        if (!this.createEmpty) {
            return true;
        }
        if (this.emptyBehavior.equals("skip")) {
            if (!this.skipWriting) {
                this.log("Warning: skipping " + this.archiveType + " archive " + zipFile + " because no files were included.", 1);
            }
            return true;
        }
        if (this.emptyBehavior.equals("fail")) {
            throw new BuildException("Cannot create " + this.archiveType + " archive " + zipFile + ": no files were included.", this.getLocation());
        }
        ZipOutputStream zOut = null;
        try {
            if (!this.skipWriting) {
                this.log("Building MANIFEST-only jar: " + this.getDestFile().getAbsolutePath());
            }
            zOut = new ZipOutputStream(this.getDestFile());
            zOut.setEncoding(this.getEncoding());
            if (this.isCompress()) {
                zOut.setMethod(8);
            }
            else {
                zOut.setMethod(0);
            }
            this.initZipOutputStream(zOut);
            this.finalizeZipOutputStream(zOut);
        }
        catch (IOException ioe) {
            throw new BuildException("Could not create almost empty JAR archive (" + ioe.getMessage() + ")", ioe, this.getLocation());
        }
        finally {
            FileUtils.close(zOut);
            this.createEmpty = false;
        }
        return true;
    }
    
    @Override
    protected void cleanUp() {
        super.cleanUp();
        this.checkJarSpec();
        if (!this.doubleFilePass || !this.skipWriting) {
            this.manifest = null;
            this.configuredManifest = this.savedConfiguredManifest;
            this.filesetManifest = null;
            this.originalManifest = null;
        }
        this.rootEntries.removeAllElements();
    }
    
    private void checkJarSpec() {
        final String br = System.getProperty("line.separator");
        final StringBuffer message = new StringBuffer();
        final Manifest.Section mainSection = (this.configuredManifest == null) ? null : this.configuredManifest.getMainSection();
        if (mainSection == null) {
            message.append("No Implementation-Title set.");
            message.append("No Implementation-Version set.");
            message.append("No Implementation-Vendor set.");
        }
        else {
            if (mainSection.getAttribute("Implementation-Title") == null) {
                message.append("No Implementation-Title set.");
            }
            if (mainSection.getAttribute("Implementation-Version") == null) {
                message.append("No Implementation-Version set.");
            }
            if (mainSection.getAttribute("Implementation-Vendor") == null) {
                message.append("No Implementation-Vendor set.");
            }
        }
        if (message.length() > 0) {
            message.append(br);
            message.append("Location: ").append(this.getLocation());
            message.append(br);
            if (this.strict.getValue().equalsIgnoreCase("fail")) {
                throw new BuildException(message.toString(), this.getLocation());
            }
            this.logWhenWriting(message.toString(), this.strict.getLogLevel());
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.emptyBehavior = "create";
        this.configuredManifest = null;
        this.filesetManifestConfig = null;
        this.mergeManifestsMain = false;
        this.manifestFile = null;
        this.index = false;
    }
    
    protected final void writeIndexLikeList(final List<String> dirs, final List<String> files, final PrintWriter writer) throws IOException {
        Collections.sort(dirs);
        Collections.sort(files);
        for (String dir : dirs) {
            dir = dir.replace('\\', '/');
            if (dir.startsWith("./")) {
                dir = dir.substring(2);
            }
            while (dir.startsWith("/")) {
                dir = dir.substring(1);
            }
            final int pos = dir.lastIndexOf(47);
            if (pos != -1) {
                dir = dir.substring(0, pos);
            }
            if (!this.indexMetaInf && dir.startsWith("META-INF")) {
                continue;
            }
            writer.println(dir);
        }
        for (final String file : files) {
            writer.println(file);
        }
    }
    
    protected static String findJarName(String fileName, final String[] classpath) {
        if (classpath == null) {
            return new File(fileName).getName();
        }
        fileName = fileName.replace(File.separatorChar, '/');
        final TreeMap<String, String> matches = new TreeMap<String, String>(new Comparator<Object>() {
            public int compare(final Object o1, final Object o2) {
                if (o1 instanceof String && o2 instanceof String) {
                    return ((String)o2).length() - ((String)o1).length();
                }
                return 0;
            }
        });
        for (int i = 0; i < classpath.length; ++i) {
            if (fileName.endsWith(classpath[i])) {
                matches.put(classpath[i], classpath[i]);
            }
            else {
                int slash = classpath[i].indexOf("/");
                for (String candidate = classpath[i]; slash > -1; slash = candidate.indexOf("/")) {
                    candidate = candidate.substring(slash + 1);
                    if (fileName.endsWith(candidate)) {
                        matches.put(candidate, classpath[i]);
                        break;
                    }
                }
            }
        }
        return (matches.size() == 0) ? null : matches.get(matches.firstKey());
    }
    
    protected static void grabFilesAndDirs(final String file, final List<String> dirs, final List<String> files) throws IOException {
        org.apache.tools.zip.ZipFile zf = null;
        try {
            zf = new org.apache.tools.zip.ZipFile(file, "utf-8");
            final Enumeration<org.apache.tools.zip.ZipEntry> entries = zf.getEntries();
            final HashSet<String> dirSet = new HashSet<String>();
            while (entries.hasMoreElements()) {
                final org.apache.tools.zip.ZipEntry ze = entries.nextElement();
                final String name = ze.getName();
                if (ze.isDirectory()) {
                    dirSet.add(name);
                }
                else if (name.indexOf("/") == -1) {
                    files.add(name);
                }
                else {
                    dirSet.add(name.substring(0, name.lastIndexOf("/") + 1));
                }
            }
            dirs.addAll(dirSet);
        }
        finally {
            if (zf != null) {
                zf.close();
            }
        }
    }
    
    private Resource[][] grabManifests(final ResourceCollection[] rcs) {
        final Resource[][] manifests = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            Resource[][] resources = null;
            if (rcs[i] instanceof FileSet) {
                resources = this.grabResources(new FileSet[] { (FileSet)rcs[i] });
            }
            else {
                resources = this.grabNonFileSetResources(new ResourceCollection[] { rcs[i] });
            }
            for (int j = 0; j < resources[0].length; ++j) {
                String name = resources[0][j].getName().replace('\\', '/');
                if (rcs[i] instanceof ArchiveFileSet) {
                    final ArchiveFileSet afs = (ArchiveFileSet)rcs[i];
                    if (!"".equals(afs.getFullpath(this.getProject()))) {
                        name = afs.getFullpath(this.getProject());
                    }
                    else if (!"".equals(afs.getPrefix(this.getProject()))) {
                        String prefix = afs.getPrefix(this.getProject());
                        if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                            prefix += "/";
                        }
                        name = prefix + name;
                    }
                }
                if (name.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    manifests[i] = new Resource[] { resources[0][j] };
                    break;
                }
            }
            if (manifests[i] == null) {
                manifests[i] = new Resource[0];
            }
        }
        return manifests;
    }
    
    static {
        JAR_MARKER = new ZipExtraField[] { JarMarker.getInstance() };
    }
    
    public static class FilesetManifestConfig extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "skip", "merge", "mergewithoutmain" };
        }
    }
    
    public static class StrictMode extends EnumeratedAttribute
    {
        public StrictMode() {
        }
        
        public StrictMode(final String value) {
            this.setValue(value);
        }
        
        @Override
        public String[] getValues() {
            return new String[] { "fail", "warn", "ignore" };
        }
        
        public int getLogLevel() {
            return this.getValue().equals("ignore") ? 3 : 1;
        }
    }
}
