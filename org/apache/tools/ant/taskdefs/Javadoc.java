// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.ArrayList;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.ProjectComponent;
import java.io.FilenameFilter;
import org.apache.tools.ant.types.PatternSet;
import java.util.HashSet;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.BuildException;
import java.util.StringTokenizer;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.DirSet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Javadoc extends Task
{
    private static final boolean JAVADOC_5;
    private static final FileUtils FILE_UTILS;
    private Commandline cmd;
    private boolean failOnError;
    private Path sourcePath;
    private File destDir;
    private Vector<SourceFile> sourceFiles;
    private Vector<PackageName> packageNames;
    private Vector<PackageName> excludePackageNames;
    private boolean author;
    private boolean version;
    private DocletInfo doclet;
    private Path classpath;
    private Path bootclasspath;
    private String group;
    private String packageList;
    private Vector<LinkArgument> links;
    private Vector<GroupArgument> groups;
    private Vector<Object> tags;
    private boolean useDefaultExcludes;
    private Html doctitle;
    private Html header;
    private Html footer;
    private Html bottom;
    private boolean useExternalFile;
    private String source;
    private boolean linksource;
    private boolean breakiterator;
    private String noqualifier;
    private boolean includeNoSourcePackages;
    private String executable;
    private boolean docFilesSubDirs;
    private String excludeDocFilesSubDir;
    private ResourceCollectionContainer nestedSourceFiles;
    private Vector<DirSet> packageSets;
    static final String[] SCOPE_ELEMENTS;
    
    public Javadoc() {
        this.cmd = new Commandline();
        this.failOnError = false;
        this.sourcePath = null;
        this.destDir = null;
        this.sourceFiles = new Vector<SourceFile>();
        this.packageNames = new Vector<PackageName>();
        this.excludePackageNames = new Vector<PackageName>(1);
        this.author = true;
        this.version = true;
        this.doclet = null;
        this.classpath = null;
        this.bootclasspath = null;
        this.group = null;
        this.packageList = null;
        this.links = new Vector<LinkArgument>();
        this.groups = new Vector<GroupArgument>();
        this.tags = new Vector<Object>();
        this.useDefaultExcludes = true;
        this.doctitle = null;
        this.header = null;
        this.footer = null;
        this.bottom = null;
        this.useExternalFile = false;
        this.source = null;
        this.linksource = false;
        this.breakiterator = false;
        this.includeNoSourcePackages = false;
        this.executable = null;
        this.docFilesSubDirs = false;
        this.excludeDocFilesSubDir = null;
        this.nestedSourceFiles = new ResourceCollectionContainer();
        this.packageSets = new Vector<DirSet>();
    }
    
    private void addArgIf(final boolean b, final String arg) {
        if (b) {
            this.cmd.createArgument().setValue(arg);
        }
    }
    
    private void addArgIfNotEmpty(final String key, final String value) {
        if (value != null && value.length() != 0) {
            this.cmd.createArgument().setValue(key);
            this.cmd.createArgument().setValue(value);
        }
        else {
            this.log("Warning: Leaving out empty argument '" + key + "'", 1);
        }
    }
    
    public void setUseExternalFile(final boolean b) {
        this.useExternalFile = b;
    }
    
    public void setDefaultexcludes(final boolean useDefaultExcludes) {
        this.useDefaultExcludes = useDefaultExcludes;
    }
    
    public void setMaxmemory(final String max) {
        this.cmd.createArgument().setValue("-J-Xmx" + max);
    }
    
    public void setAdditionalparam(final String add) {
        this.cmd.createArgument().setLine(add);
    }
    
    public Commandline.Argument createArg() {
        return this.cmd.createArgument();
    }
    
    public void setSourcepath(final Path src) {
        if (this.sourcePath == null) {
            this.sourcePath = src;
        }
        else {
            this.sourcePath.append(src);
        }
    }
    
    public Path createSourcepath() {
        if (this.sourcePath == null) {
            this.sourcePath = new Path(this.getProject());
        }
        return this.sourcePath.createPath();
    }
    
    public void setSourcepathRef(final Reference r) {
        this.createSourcepath().setRefid(r);
    }
    
    public void setDestdir(final File dir) {
        this.destDir = dir;
        this.cmd.createArgument().setValue("-d");
        this.cmd.createArgument().setFile(this.destDir);
    }
    
    public void setSourcefiles(final String src) {
        final StringTokenizer tok = new StringTokenizer(src, ",");
        while (tok.hasMoreTokens()) {
            final String f = tok.nextToken();
            final SourceFile sf = new SourceFile();
            sf.setFile(this.getProject().resolveFile(f.trim()));
            this.addSource(sf);
        }
    }
    
    public void addSource(final SourceFile sf) {
        this.sourceFiles.addElement(sf);
    }
    
    public void setPackagenames(final String packages) {
        final StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            final String p = tok.nextToken();
            final PackageName pn = new PackageName();
            pn.setName(p);
            this.addPackage(pn);
        }
    }
    
    public void addPackage(final PackageName pn) {
        this.packageNames.addElement(pn);
    }
    
    public void setExcludePackageNames(final String packages) {
        final StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            final String p = tok.nextToken();
            final PackageName pn = new PackageName();
            pn.setName(p);
            this.addExcludePackage(pn);
        }
    }
    
    public void addExcludePackage(final PackageName pn) {
        this.excludePackageNames.addElement(pn);
    }
    
    public void setOverview(final File f) {
        this.cmd.createArgument().setValue("-overview");
        this.cmd.createArgument().setFile(f);
    }
    
    public void setPublic(final boolean b) {
        this.addArgIf(b, "-public");
    }
    
    public void setProtected(final boolean b) {
        this.addArgIf(b, "-protected");
    }
    
    public void setPackage(final boolean b) {
        this.addArgIf(b, "-package");
    }
    
    public void setPrivate(final boolean b) {
        this.addArgIf(b, "-private");
    }
    
    public void setAccess(final AccessType at) {
        this.cmd.createArgument().setValue("-" + at.getValue());
    }
    
    public void setDoclet(final String docletName) {
        if (this.doclet == null) {
            (this.doclet = new DocletInfo()).setProject(this.getProject());
        }
        this.doclet.setName(docletName);
    }
    
    public void setDocletPath(final Path docletPath) {
        if (this.doclet == null) {
            (this.doclet = new DocletInfo()).setProject(this.getProject());
        }
        this.doclet.setPath(docletPath);
    }
    
    public void setDocletPathRef(final Reference r) {
        if (this.doclet == null) {
            (this.doclet = new DocletInfo()).setProject(this.getProject());
        }
        this.doclet.createPath().setRefid(r);
    }
    
    public DocletInfo createDoclet() {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
        }
        return this.doclet;
    }
    
    public void addTaglet(final ExtensionInfo tagletInfo) {
        this.tags.addElement(tagletInfo);
    }
    
    public void setOld(final boolean b) {
        this.log("Javadoc 1.4 doesn't support the -1.1 switch anymore", 1);
    }
    
    public void setClasspath(final Path path) {
        if (this.classpath == null) {
            this.classpath = path;
        }
        else {
            this.classpath.append(path);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setBootclasspath(final Path path) {
        if (this.bootclasspath == null) {
            this.bootclasspath = path;
        }
        else {
            this.bootclasspath.append(path);
        }
    }
    
    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(this.getProject());
        }
        return this.bootclasspath.createPath();
    }
    
    public void setBootClasspathRef(final Reference r) {
        this.createBootclasspath().setRefid(r);
    }
    
    @Deprecated
    public void setExtdirs(final String path) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setValue(path);
    }
    
    public void setExtdirs(final Path path) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setPath(path);
    }
    
    public void setVerbose(final boolean b) {
        this.addArgIf(b, "-verbose");
    }
    
    public void setLocale(final String locale) {
        this.cmd.createArgument(true).setValue(locale);
        this.cmd.createArgument(true).setValue("-locale");
    }
    
    public void setEncoding(final String enc) {
        this.cmd.createArgument().setValue("-encoding");
        this.cmd.createArgument().setValue(enc);
    }
    
    public void setVersion(final boolean b) {
        this.version = b;
    }
    
    public void setUse(final boolean b) {
        this.addArgIf(b, "-use");
    }
    
    public void setAuthor(final boolean b) {
        this.author = b;
    }
    
    public void setSplitindex(final boolean b) {
        this.addArgIf(b, "-splitindex");
    }
    
    public void setWindowtitle(final String title) {
        this.addArgIfNotEmpty("-windowtitle", title);
    }
    
    public void setDoctitle(final String doctitle) {
        final Html h = new Html();
        h.addText(doctitle);
        this.addDoctitle(h);
    }
    
    public void addDoctitle(final Html text) {
        this.doctitle = text;
    }
    
    public void setHeader(final String header) {
        final Html h = new Html();
        h.addText(header);
        this.addHeader(h);
    }
    
    public void addHeader(final Html text) {
        this.header = text;
    }
    
    public void setFooter(final String footer) {
        final Html h = new Html();
        h.addText(footer);
        this.addFooter(h);
    }
    
    public void addFooter(final Html text) {
        this.footer = text;
    }
    
    public void setBottom(final String bottom) {
        final Html h = new Html();
        h.addText(bottom);
        this.addBottom(h);
    }
    
    public void addBottom(final Html text) {
        this.bottom = text;
    }
    
    public void setLinkoffline(final String src) {
        final LinkArgument le = this.createLink();
        le.setOffline(true);
        final String linkOfflineError = "The linkoffline attribute must include a URL and a package-list file location separated by a space";
        if (src.trim().length() == 0) {
            throw new BuildException(linkOfflineError);
        }
        final StringTokenizer tok = new StringTokenizer(src, " ", false);
        le.setHref(tok.nextToken());
        if (!tok.hasMoreTokens()) {
            throw new BuildException(linkOfflineError);
        }
        le.setPackagelistLoc(this.getProject().resolveFile(tok.nextToken()));
    }
    
    public void setGroup(final String src) {
        this.group = src;
    }
    
    public void setLink(final String src) {
        this.createLink().setHref(src);
    }
    
    public void setNodeprecated(final boolean b) {
        this.addArgIf(b, "-nodeprecated");
    }
    
    public void setNodeprecatedlist(final boolean b) {
        this.addArgIf(b, "-nodeprecatedlist");
    }
    
    public void setNotree(final boolean b) {
        this.addArgIf(b, "-notree");
    }
    
    public void setNoindex(final boolean b) {
        this.addArgIf(b, "-noindex");
    }
    
    public void setNohelp(final boolean b) {
        this.addArgIf(b, "-nohelp");
    }
    
    public void setNonavbar(final boolean b) {
        this.addArgIf(b, "-nonavbar");
    }
    
    public void setSerialwarn(final boolean b) {
        this.addArgIf(b, "-serialwarn");
    }
    
    public void setStylesheetfile(final File f) {
        this.cmd.createArgument().setValue("-stylesheetfile");
        this.cmd.createArgument().setFile(f);
    }
    
    public void setHelpfile(final File f) {
        this.cmd.createArgument().setValue("-helpfile");
        this.cmd.createArgument().setFile(f);
    }
    
    public void setDocencoding(final String enc) {
        this.cmd.createArgument().setValue("-docencoding");
        this.cmd.createArgument().setValue(enc);
    }
    
    public void setPackageList(final String src) {
        this.packageList = src;
    }
    
    public LinkArgument createLink() {
        final LinkArgument la = new LinkArgument();
        this.links.addElement(la);
        return la;
    }
    
    public TagArgument createTag() {
        final TagArgument ta = new TagArgument();
        this.tags.addElement(ta);
        return ta;
    }
    
    public GroupArgument createGroup() {
        final GroupArgument ga = new GroupArgument();
        this.groups.addElement(ga);
        return ga;
    }
    
    public void setCharset(final String src) {
        this.addArgIfNotEmpty("-charset", src);
    }
    
    public void setFailonerror(final boolean b) {
        this.failOnError = b;
    }
    
    public void setSource(final String source) {
        this.source = source;
    }
    
    public void setExecutable(final String executable) {
        this.executable = executable;
    }
    
    public void addPackageset(final DirSet packageSet) {
        this.packageSets.addElement(packageSet);
    }
    
    public void addFileset(final FileSet fs) {
        this.createSourceFiles().add(fs);
    }
    
    public ResourceCollectionContainer createSourceFiles() {
        return this.nestedSourceFiles;
    }
    
    public void setLinksource(final boolean b) {
        this.linksource = b;
    }
    
    public void setBreakiterator(final boolean b) {
        this.breakiterator = b;
    }
    
    public void setNoqualifier(final String noqualifier) {
        this.noqualifier = noqualifier;
    }
    
    public void setIncludeNoSourcePackages(final boolean b) {
        this.includeNoSourcePackages = b;
    }
    
    public void setDocFilesSubDirs(final boolean b) {
        this.docFilesSubDirs = b;
    }
    
    public void setExcludeDocFilesSubDir(final String s) {
        this.excludeDocFilesSubDir = s;
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkTaskName();
        final Vector<String> packagesToDoc = new Vector<String>();
        final Path sourceDirs = new Path(this.getProject());
        this.checkPackageAndSourcePath();
        if (this.sourcePath != null) {
            sourceDirs.addExisting(this.sourcePath);
        }
        this.parsePackages(packagesToDoc, sourceDirs);
        this.checkPackages(packagesToDoc, sourceDirs);
        final Vector<SourceFile> sourceFilesToDoc = (Vector<SourceFile>)this.sourceFiles.clone();
        this.addSourceFiles(sourceFilesToDoc);
        this.checkPackagesToDoc(packagesToDoc, sourceFilesToDoc);
        this.log("Generating Javadoc", 2);
        final Commandline toExecute = (Commandline)this.cmd.clone();
        if (this.executable != null) {
            toExecute.setExecutable(this.executable);
        }
        else {
            toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }
        this.generalJavadocArguments(toExecute);
        this.doSourcePath(toExecute, sourceDirs);
        this.doDoclet(toExecute);
        this.doBootPath(toExecute);
        this.doLinks(toExecute);
        this.doGroup(toExecute);
        this.doGroups(toExecute);
        this.doDocFilesSubDirs(toExecute);
        this.doJava14(toExecute);
        if (this.breakiterator && (this.doclet == null || Javadoc.JAVADOC_5)) {
            toExecute.createArgument().setValue("-breakiterator");
        }
        if (this.useExternalFile) {
            this.writeExternalArgs(toExecute);
        }
        File tmpList = null;
        FileWriter wr = null;
        try {
            BufferedWriter srcListWriter = null;
            if (this.useExternalFile) {
                tmpList = Javadoc.FILE_UTILS.createTempFile("javadoc", "", null, true, true);
                toExecute.createArgument().setValue("@" + tmpList.getAbsolutePath());
                wr = new FileWriter(tmpList.getAbsolutePath(), true);
                srcListWriter = new BufferedWriter(wr);
            }
            this.doSourceAndPackageNames(toExecute, packagesToDoc, sourceFilesToDoc, this.useExternalFile, tmpList, srcListWriter);
            if (this.useExternalFile) {
                srcListWriter.flush();
            }
        }
        catch (IOException e) {
            tmpList.delete();
            throw new BuildException("Error creating temporary file", e, this.getLocation());
        }
        finally {
            FileUtils.close(wr);
        }
        if (this.packageList != null) {
            toExecute.createArgument().setValue("@" + this.packageList);
        }
        this.log(toExecute.describeCommand(), 3);
        this.log("Javadoc execution", 2);
        final JavadocOutputStream out = new JavadocOutputStream(2);
        final JavadocOutputStream err = new JavadocOutputStream(1);
        final Execute exe = new Execute(new PumpStreamHandler(out, err));
        exe.setAntRun(this.getProject());
        exe.setWorkingDirectory(null);
        try {
            exe.setCommandline(toExecute.getCommandline());
            final int ret = exe.execute();
            if (ret != 0 && this.failOnError) {
                throw new BuildException("Javadoc returned " + ret, this.getLocation());
            }
        }
        catch (IOException e2) {
            throw new BuildException("Javadoc failed: " + e2, e2, this.getLocation());
        }
        finally {
            if (tmpList != null) {
                tmpList.delete();
                tmpList = null;
            }
            out.logFlush();
            err.logFlush();
            try {
                out.close();
                err.close();
            }
            catch (IOException ex) {}
        }
    }
    
    private void checkTaskName() {
        if ("javadoc2".equals(this.getTaskType())) {
            this.log("Warning: the task name <javadoc2> is deprecated. Use <javadoc> instead.", 1);
        }
    }
    
    private void checkPackageAndSourcePath() {
        if (this.packageList != null && this.sourcePath == null) {
            final String msg = "sourcePath attribute must be set when specifying packagelist.";
            throw new BuildException(msg);
        }
    }
    
    private void checkPackages(final Vector<String> packagesToDoc, final Path sourceDirs) {
        if (packagesToDoc.size() != 0 && sourceDirs.size() == 0) {
            final String msg = "sourcePath attribute must be set when specifying package names.";
            throw new BuildException(msg);
        }
    }
    
    private void checkPackagesToDoc(final Vector<String> packagesToDoc, final Vector<SourceFile> sourceFilesToDoc) {
        if (this.packageList == null && packagesToDoc.size() == 0 && sourceFilesToDoc.size() == 0) {
            throw new BuildException("No source files and no packages have been specified.");
        }
    }
    
    private void doSourcePath(final Commandline toExecute, final Path sourceDirs) {
        if (sourceDirs.size() > 0) {
            toExecute.createArgument().setValue("-sourcepath");
            toExecute.createArgument().setPath(sourceDirs);
        }
    }
    
    private void generalJavadocArguments(final Commandline toExecute) {
        if (this.doctitle != null) {
            toExecute.createArgument().setValue("-doctitle");
            toExecute.createArgument().setValue(this.expand(this.doctitle.getText()));
        }
        if (this.header != null) {
            toExecute.createArgument().setValue("-header");
            toExecute.createArgument().setValue(this.expand(this.header.getText()));
        }
        if (this.footer != null) {
            toExecute.createArgument().setValue("-footer");
            toExecute.createArgument().setValue(this.expand(this.footer.getText()));
        }
        if (this.bottom != null) {
            toExecute.createArgument().setValue("-bottom");
            toExecute.createArgument().setValue(this.expand(this.bottom.getText()));
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject()).concatSystemClasspath("last");
        }
        else {
            this.classpath = this.classpath.concatSystemClasspath("ignore");
        }
        if (this.classpath.size() > 0) {
            toExecute.createArgument().setValue("-classpath");
            toExecute.createArgument().setPath(this.classpath);
        }
        if (this.version && this.doclet == null) {
            toExecute.createArgument().setValue("-version");
        }
        if (this.author && this.doclet == null) {
            toExecute.createArgument().setValue("-author");
        }
        if (this.doclet == null && this.destDir == null) {
            throw new BuildException("destdir attribute must be set!");
        }
    }
    
    private void doDoclet(final Commandline toExecute) {
        if (this.doclet != null) {
            if (this.doclet.getName() == null) {
                throw new BuildException("The doclet name must be specified.", this.getLocation());
            }
            toExecute.createArgument().setValue("-doclet");
            toExecute.createArgument().setValue(this.doclet.getName());
            if (this.doclet.getPath() != null) {
                final Path docletPath = this.doclet.getPath().concatSystemClasspath("ignore");
                if (docletPath.size() != 0) {
                    toExecute.createArgument().setValue("-docletpath");
                    toExecute.createArgument().setPath(docletPath);
                }
            }
            final Enumeration<DocletParam> e = this.doclet.getParams();
            while (e.hasMoreElements()) {
                final DocletParam param = e.nextElement();
                if (param.getName() == null) {
                    throw new BuildException("Doclet parameters must have a name");
                }
                toExecute.createArgument().setValue(param.getName());
                if (param.getValue() == null) {
                    continue;
                }
                toExecute.createArgument().setValue(param.getValue());
            }
        }
    }
    
    private void writeExternalArgs(final Commandline toExecute) {
        File optionsTmpFile = null;
        BufferedWriter optionsListWriter = null;
        try {
            optionsTmpFile = Javadoc.FILE_UTILS.createTempFile("javadocOptions", "", null, true, true);
            final String[] listOpt = toExecute.getArguments();
            toExecute.clearArgs();
            toExecute.createArgument().setValue("@" + optionsTmpFile.getAbsolutePath());
            optionsListWriter = new BufferedWriter(new FileWriter(optionsTmpFile.getAbsolutePath(), true));
            for (int i = 0; i < listOpt.length; ++i) {
                final String string = listOpt[i];
                if (string.startsWith("-J-")) {
                    toExecute.createArgument().setValue(string);
                }
                else if (string.startsWith("-")) {
                    optionsListWriter.write(string);
                    optionsListWriter.write(" ");
                }
                else {
                    optionsListWriter.write(this.quoteString(string));
                    optionsListWriter.newLine();
                }
            }
            optionsListWriter.close();
        }
        catch (IOException ex) {
            if (optionsTmpFile != null) {
                optionsTmpFile.delete();
            }
            throw new BuildException("Error creating or writing temporary file for javadoc options", ex, this.getLocation());
        }
        finally {
            FileUtils.close(optionsListWriter);
        }
    }
    
    private void doBootPath(final Commandline toExecute) {
        Path bcp = new Path(this.getProject());
        if (this.bootclasspath != null) {
            bcp.append(this.bootclasspath);
        }
        bcp = bcp.concatSystemBootClasspath("ignore");
        if (bcp.size() > 0) {
            toExecute.createArgument().setValue("-bootclasspath");
            toExecute.createArgument().setPath(bcp);
        }
    }
    
    private void doLinks(final Commandline toExecute) {
        if (this.links.size() != 0) {
            final Enumeration<LinkArgument> e = this.links.elements();
            while (e.hasMoreElements()) {
                final LinkArgument la = e.nextElement();
                if (la.getHref() == null || la.getHref().length() == 0) {
                    this.log("No href was given for the link - skipping", 3);
                }
                else {
                    String link = null;
                    if (la.shouldResolveLink()) {
                        final File hrefAsFile = this.getProject().resolveFile(la.getHref());
                        if (hrefAsFile.exists()) {
                            try {
                                link = Javadoc.FILE_UTILS.getFileURL(hrefAsFile).toExternalForm();
                            }
                            catch (MalformedURLException ex) {
                                this.log("Warning: link location was invalid " + hrefAsFile, 1);
                            }
                        }
                    }
                    if (link == null) {
                        try {
                            final URL base = new URL("file://.");
                            new URL(base, la.getHref());
                            link = la.getHref();
                        }
                        catch (MalformedURLException mue) {
                            this.log("Link href \"" + la.getHref() + "\" is not a valid url - skipping link", 1);
                            continue;
                        }
                    }
                    if (la.isLinkOffline()) {
                        final File packageListLocation = la.getPackagelistLoc();
                        URL packageListURL = la.getPackagelistURL();
                        if (packageListLocation == null && packageListURL == null) {
                            throw new BuildException("The package list location for link " + la.getHref() + " must be provided " + "because the link is " + "offline");
                        }
                        if (packageListLocation != null) {
                            final File packageListFile = new File(packageListLocation, "package-list");
                            if (packageListFile.exists()) {
                                try {
                                    packageListURL = Javadoc.FILE_UTILS.getFileURL(packageListLocation);
                                }
                                catch (MalformedURLException ex2) {
                                    this.log("Warning: Package list location was invalid " + packageListLocation, 1);
                                }
                            }
                            else {
                                this.log("Warning: No package list was found at " + packageListLocation, 3);
                            }
                        }
                        if (packageListURL == null) {
                            continue;
                        }
                        toExecute.createArgument().setValue("-linkoffline");
                        toExecute.createArgument().setValue(link);
                        toExecute.createArgument().setValue(packageListURL.toExternalForm());
                    }
                    else {
                        toExecute.createArgument().setValue("-link");
                        toExecute.createArgument().setValue(link);
                    }
                }
            }
        }
    }
    
    private void doGroup(final Commandline toExecute) {
        if (this.group != null) {
            final StringTokenizer tok = new StringTokenizer(this.group, ",", false);
            while (tok.hasMoreTokens()) {
                final String grp = tok.nextToken().trim();
                final int space = grp.indexOf(" ");
                if (space > 0) {
                    final String name = grp.substring(0, space);
                    final String pkgList = grp.substring(space + 1);
                    toExecute.createArgument().setValue("-group");
                    toExecute.createArgument().setValue(name);
                    toExecute.createArgument().setValue(pkgList);
                }
            }
        }
    }
    
    private void doGroups(final Commandline toExecute) {
        if (this.groups.size() != 0) {
            final Enumeration<GroupArgument> e = this.groups.elements();
            while (e.hasMoreElements()) {
                final GroupArgument ga = e.nextElement();
                final String title = ga.getTitle();
                final String packages = ga.getPackages();
                if (title == null || packages == null) {
                    throw new BuildException("The title and packages must be specified for group elements.");
                }
                toExecute.createArgument().setValue("-group");
                toExecute.createArgument().setValue(this.expand(title));
                toExecute.createArgument().setValue(packages);
            }
        }
    }
    
    private void doJava14(final Commandline toExecute) {
        final Enumeration<Object> e = this.tags.elements();
        while (e.hasMoreElements()) {
            final Object element = e.nextElement();
            if (element instanceof TagArgument) {
                final TagArgument ta = (TagArgument)element;
                final File tagDir = ta.getDir(this.getProject());
                if (tagDir == null) {
                    toExecute.createArgument().setValue("-tag");
                    toExecute.createArgument().setValue(ta.getParameter());
                }
                else {
                    final DirectoryScanner tagDefScanner = ta.getDirectoryScanner(this.getProject());
                    final String[] files = tagDefScanner.getIncludedFiles();
                    for (int i = 0; i < files.length; ++i) {
                        final File tagDefFile = new File(tagDir, files[i]);
                        try {
                            final BufferedReader in = new BufferedReader(new FileReader(tagDefFile));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                toExecute.createArgument().setValue("-tag");
                                toExecute.createArgument().setValue(line);
                            }
                            in.close();
                        }
                        catch (IOException ioe) {
                            throw new BuildException("Couldn't read  tag file from " + tagDefFile.getAbsolutePath(), ioe);
                        }
                    }
                }
            }
            else {
                final ExtensionInfo tagletInfo = (ExtensionInfo)element;
                toExecute.createArgument().setValue("-taglet");
                toExecute.createArgument().setValue(tagletInfo.getName());
                if (tagletInfo.getPath() == null) {
                    continue;
                }
                final Path tagletPath = tagletInfo.getPath().concatSystemClasspath("ignore");
                if (tagletPath.size() == 0) {
                    continue;
                }
                toExecute.createArgument().setValue("-tagletpath");
                toExecute.createArgument().setPath(tagletPath);
            }
        }
        final String sourceArg = (this.source != null) ? this.source : this.getProject().getProperty("ant.build.javac.source");
        if (sourceArg != null) {
            toExecute.createArgument().setValue("-source");
            toExecute.createArgument().setValue(sourceArg);
        }
        if (this.linksource && this.doclet == null) {
            toExecute.createArgument().setValue("-linksource");
        }
        if (this.noqualifier != null && this.doclet == null) {
            toExecute.createArgument().setValue("-noqualifier");
            toExecute.createArgument().setValue(this.noqualifier);
        }
    }
    
    private void doDocFilesSubDirs(final Commandline toExecute) {
        if (this.docFilesSubDirs) {
            toExecute.createArgument().setValue("-docfilessubdirs");
            if (this.excludeDocFilesSubDir != null && this.excludeDocFilesSubDir.trim().length() > 0) {
                toExecute.createArgument().setValue("-excludedocfilessubdir");
                toExecute.createArgument().setValue(this.excludeDocFilesSubDir);
            }
        }
    }
    
    private void doSourceAndPackageNames(final Commandline toExecute, final Vector<String> packagesToDoc, final Vector<SourceFile> sourceFilesToDoc, final boolean useExternalFile, final File tmpList, final BufferedWriter srcListWriter) throws IOException {
        for (final String packageName : packagesToDoc) {
            if (useExternalFile) {
                srcListWriter.write(packageName);
                srcListWriter.newLine();
            }
            else {
                toExecute.createArgument().setValue(packageName);
            }
        }
        for (final SourceFile sf : sourceFilesToDoc) {
            final String sourceFileName = sf.getFile().getAbsolutePath();
            if (useExternalFile) {
                if (sourceFileName.indexOf(" ") > -1) {
                    String name = sourceFileName;
                    if (File.separatorChar == '\\') {
                        name = sourceFileName.replace(File.separatorChar, '/');
                    }
                    srcListWriter.write("\"" + name + "\"");
                }
                else {
                    srcListWriter.write(sourceFileName);
                }
                srcListWriter.newLine();
            }
            else {
                toExecute.createArgument().setValue(sourceFileName);
            }
        }
    }
    
    private String quoteString(final String str) {
        if (!this.containsWhitespace(str) && str.indexOf(39) == -1 && str.indexOf(34) == -1) {
            return str;
        }
        if (str.indexOf(39) == -1) {
            return this.quoteString(str, '\'');
        }
        return this.quoteString(str, '\"');
    }
    
    private boolean containsWhitespace(final String s) {
        for (int len = s.length(), i = 0; i < len; ++i) {
            if (Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    private String quoteString(final String str, final char delim) {
        final StringBuffer buf = new StringBuffer(str.length() * 2);
        buf.append(delim);
        final int len = str.length();
        boolean lastCharWasCR = false;
        for (int i = 0; i < len; ++i) {
            final char c = str.charAt(i);
            if (c == delim) {
                buf.append('\\').append(c);
                lastCharWasCR = false;
            }
            else {
                switch (c) {
                    case '\\': {
                        buf.append("\\\\");
                        lastCharWasCR = false;
                        break;
                    }
                    case '\r': {
                        buf.append("\\\r");
                        lastCharWasCR = true;
                        break;
                    }
                    case '\n': {
                        if (!lastCharWasCR) {
                            buf.append("\\\n");
                        }
                        else {
                            buf.append("\n");
                        }
                        lastCharWasCR = false;
                        break;
                    }
                    default: {
                        buf.append(c);
                        lastCharWasCR = false;
                        break;
                    }
                }
            }
        }
        buf.append(delim);
        return buf.toString();
    }
    
    private void addSourceFiles(final Vector<SourceFile> sf) {
        final Iterator<ResourceCollection> e = this.nestedSourceFiles.iterator();
        while (e.hasNext()) {
            ResourceCollection rc = e.next();
            if (!rc.isFilesystemOnly()) {
                throw new BuildException("only file system based resources are supported by javadoc");
            }
            if (rc instanceof FileSet) {
                final FileSet fs = (FileSet)rc;
                if (!fs.hasPatterns() && !fs.hasSelectors()) {
                    final FileSet fs2 = (FileSet)fs.clone();
                    fs2.createInclude().setName("**/*.java");
                    if (this.includeNoSourcePackages) {
                        fs2.createInclude().setName("**/package.html");
                    }
                    rc = fs2;
                }
            }
            for (final Resource r : rc) {
                sf.addElement(new SourceFile(r.as(FileProvider.class).getFile()));
            }
        }
    }
    
    private void parsePackages(final Vector<String> pn, final Path sp) {
        final HashSet<String> addedPackages = new HashSet<String>();
        final Vector<DirSet> dirSets = (Vector<DirSet>)this.packageSets.clone();
        if (this.sourcePath != null) {
            final PatternSet ps = new PatternSet();
            ps.setProject(this.getProject());
            if (this.packageNames.size() > 0) {
                final Enumeration<PackageName> e = this.packageNames.elements();
                while (e.hasMoreElements()) {
                    final PackageName p = e.nextElement();
                    String pkg = p.getName().replace('.', '/');
                    if (pkg.endsWith("*")) {
                        pkg += "*";
                    }
                    ps.createInclude().setName(pkg);
                }
            }
            else {
                ps.createInclude().setName("**");
            }
            final Enumeration<PackageName> e = this.excludePackageNames.elements();
            while (e.hasMoreElements()) {
                final PackageName p = e.nextElement();
                String pkg = p.getName().replace('.', '/');
                if (pkg.endsWith("*")) {
                    pkg += "*";
                }
                ps.createExclude().setName(pkg);
            }
            final String[] pathElements = this.sourcePath.list();
            for (int i = 0; i < pathElements.length; ++i) {
                final File dir = new File(pathElements[i]);
                if (dir.isDirectory()) {
                    final DirSet ds = new DirSet();
                    ds.setProject(this.getProject());
                    ds.setDefaultexcludes(this.useDefaultExcludes);
                    ds.setDir(dir);
                    ds.createPatternSet().addConfiguredPatternset(ps);
                    dirSets.addElement(ds);
                }
                else {
                    this.log("Skipping " + pathElements[i] + " since it is no directory.", 1);
                }
            }
        }
        final Enumeration<DirSet> e2 = dirSets.elements();
        while (e2.hasMoreElements()) {
            final DirSet ds2 = e2.nextElement();
            final File baseDir = ds2.getDir(this.getProject());
            this.log("scanning " + baseDir + " for packages.", 4);
            final DirectoryScanner dsc = ds2.getDirectoryScanner(this.getProject());
            final String[] dirs = dsc.getIncludedDirectories();
            boolean containsPackages = false;
            for (int j = 0; j < dirs.length; ++j) {
                final File pd = new File(baseDir, dirs[j]);
                final String[] files = pd.list(new FilenameFilter() {
                    public boolean accept(final File dir1, final String name) {
                        return name.endsWith(".java") || (Javadoc.this.includeNoSourcePackages && name.equals("package.html"));
                    }
                });
                if (files.length > 0) {
                    if ("".equals(dirs[j])) {
                        this.log(baseDir + " contains source files in the default package," + " you must specify them as source files" + " not packages.", 1);
                    }
                    else {
                        containsPackages = true;
                        final String packageName = dirs[j].replace(File.separatorChar, '.');
                        if (!addedPackages.contains(packageName)) {
                            addedPackages.add(packageName);
                            pn.addElement(packageName);
                        }
                    }
                }
            }
            if (containsPackages) {
                sp.createPathElement().setLocation(baseDir);
            }
            else {
                this.log(baseDir + " doesn't contain any packages, dropping it.", 3);
            }
        }
    }
    
    protected String expand(final String content) {
        return this.getProject().replaceProperties(content);
    }
    
    static {
        JAVADOC_5 = !JavaEnvUtils.isJavaVersion("1.4");
        FILE_UTILS = FileUtils.getFileUtils();
        SCOPE_ELEMENTS = new String[] { "overview", "packages", "types", "constructors", "methods", "fields" };
    }
    
    public class DocletParam
    {
        private String name;
        private String value;
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public static class ExtensionInfo extends ProjectComponent
    {
        private String name;
        private Path path;
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setPath(final Path path) {
            if (this.path == null) {
                this.path = path;
            }
            else {
                this.path.append(path);
            }
        }
        
        public Path getPath() {
            return this.path;
        }
        
        public Path createPath() {
            if (this.path == null) {
                this.path = new Path(this.getProject());
            }
            return this.path.createPath();
        }
        
        public void setPathRef(final Reference r) {
            this.createPath().setRefid(r);
        }
    }
    
    public class DocletInfo extends ExtensionInfo
    {
        private Vector<DocletParam> params;
        
        public DocletInfo() {
            this.params = new Vector<DocletParam>();
        }
        
        public DocletParam createParam() {
            final DocletParam param = new DocletParam();
            this.params.addElement(param);
            return param;
        }
        
        public Enumeration<DocletParam> getParams() {
            return this.params.elements();
        }
    }
    
    public static class PackageName
    {
        private String name;
        
        public void setName(final String name) {
            this.name = name.trim();
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public String toString() {
            return this.getName();
        }
    }
    
    public static class SourceFile
    {
        private File file;
        
        public SourceFile() {
        }
        
        public SourceFile(final File file) {
            this.file = file;
        }
        
        public void setFile(final File file) {
            this.file = file;
        }
        
        public File getFile() {
            return this.file;
        }
    }
    
    public static class Html
    {
        private StringBuffer text;
        
        public Html() {
            this.text = new StringBuffer();
        }
        
        public void addText(final String t) {
            this.text.append(t);
        }
        
        public String getText() {
            return this.text.substring(0);
        }
    }
    
    public static class AccessType extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "protected", "public", "package", "private" };
        }
    }
    
    public class ResourceCollectionContainer
    {
        private ArrayList<ResourceCollection> rcs;
        
        public ResourceCollectionContainer() {
            this.rcs = new ArrayList<ResourceCollection>();
        }
        
        public void add(final ResourceCollection rc) {
            this.rcs.add(rc);
        }
        
        private Iterator<ResourceCollection> iterator() {
            return this.rcs.iterator();
        }
    }
    
    public class LinkArgument
    {
        private String href;
        private boolean offline;
        private File packagelistLoc;
        private URL packagelistURL;
        private boolean resolveLink;
        
        public LinkArgument() {
            this.offline = false;
            this.resolveLink = false;
        }
        
        public void setHref(final String hr) {
            this.href = hr;
        }
        
        public String getHref() {
            return this.href;
        }
        
        public void setPackagelistLoc(final File src) {
            this.packagelistLoc = src;
        }
        
        public File getPackagelistLoc() {
            return this.packagelistLoc;
        }
        
        public void setPackagelistURL(final URL src) {
            this.packagelistURL = src;
        }
        
        public URL getPackagelistURL() {
            return this.packagelistURL;
        }
        
        public void setOffline(final boolean offline) {
            this.offline = offline;
        }
        
        public boolean isLinkOffline() {
            return this.offline;
        }
        
        public void setResolveLink(final boolean resolve) {
            this.resolveLink = resolve;
        }
        
        public boolean shouldResolveLink() {
            return this.resolveLink;
        }
    }
    
    public class TagArgument extends FileSet
    {
        private String name;
        private boolean enabled;
        private String scope;
        
        public TagArgument() {
            this.name = null;
            this.enabled = true;
            this.scope = "a";
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public void setScope(String verboseScope) throws BuildException {
            verboseScope = verboseScope.toLowerCase(Locale.ENGLISH);
            final boolean[] elements = new boolean[Javadoc.SCOPE_ELEMENTS.length];
            boolean gotAll = false;
            boolean gotNotAll = false;
            final StringTokenizer tok = new StringTokenizer(verboseScope, ",");
            while (tok.hasMoreTokens()) {
                final String next = tok.nextToken().trim();
                if (next.equals("all")) {
                    if (gotAll) {
                        this.getProject().log("Repeated tag scope element: all", 3);
                    }
                    gotAll = true;
                }
                else {
                    int i;
                    for (i = 0; i < Javadoc.SCOPE_ELEMENTS.length && !next.equals(Javadoc.SCOPE_ELEMENTS[i]); ++i) {}
                    if (i == Javadoc.SCOPE_ELEMENTS.length) {
                        throw new BuildException("Unrecognised scope element: " + next);
                    }
                    if (elements[i]) {
                        this.getProject().log("Repeated tag scope element: " + next, 3);
                    }
                    elements[i] = true;
                    gotNotAll = true;
                }
            }
            if (gotNotAll && gotAll) {
                throw new BuildException("Mixture of \"all\" and other scope elements in tag parameter.");
            }
            if (!gotNotAll && !gotAll) {
                throw new BuildException("No scope elements specified in tag parameter.");
            }
            if (gotAll) {
                this.scope = "a";
            }
            else {
                final StringBuffer buff = new StringBuffer(elements.length);
                for (int i = 0; i < elements.length; ++i) {
                    if (elements[i]) {
                        buff.append(Javadoc.SCOPE_ELEMENTS[i].charAt(0));
                    }
                }
                this.scope = buff.toString();
            }
        }
        
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getParameter() throws BuildException {
            if (this.name == null || this.name.equals("")) {
                throw new BuildException("No name specified for custom tag.");
            }
            if (this.getDescription() != null) {
                return this.name + ":" + (this.enabled ? "" : "X") + this.scope + ":" + this.getDescription();
            }
            if (!this.enabled || !"a".equals(this.scope)) {
                return this.name + ":" + (this.enabled ? "" : "X") + this.scope;
            }
            return this.name;
        }
    }
    
    public class GroupArgument
    {
        private Html title;
        private Vector<PackageName> packages;
        
        public GroupArgument() {
            this.packages = new Vector<PackageName>();
        }
        
        public void setTitle(final String src) {
            final Html h = new Html();
            h.addText(src);
            this.addTitle(h);
        }
        
        public void addTitle(final Html text) {
            this.title = text;
        }
        
        public String getTitle() {
            return (this.title != null) ? this.title.getText() : null;
        }
        
        public void setPackages(final String src) {
            final StringTokenizer tok = new StringTokenizer(src, ",");
            while (tok.hasMoreTokens()) {
                final String p = tok.nextToken();
                final PackageName pn = new PackageName();
                pn.setName(p);
                this.addPackage(pn);
            }
        }
        
        public void addPackage(final PackageName pn) {
            this.packages.addElement(pn);
        }
        
        public String getPackages() {
            final StringBuffer p = new StringBuffer();
            for (int size = this.packages.size(), i = 0; i < size; ++i) {
                if (i > 0) {
                    p.append(":");
                }
                p.append(this.packages.elementAt(i).toString());
            }
            return p.toString();
        }
    }
    
    private class JavadocOutputStream extends LogOutputStream
    {
        private String queuedLine;
        
        JavadocOutputStream(final int level) {
            super(Javadoc.this, level);
            this.queuedLine = null;
        }
        
        @Override
        protected void processLine(final String line, final int messageLevel) {
            if (messageLevel == 2 && line.startsWith("Generating ")) {
                if (this.queuedLine != null) {
                    super.processLine(this.queuedLine, 3);
                }
                this.queuedLine = line;
            }
            else {
                if (this.queuedLine != null) {
                    if (line.startsWith("Building ")) {
                        super.processLine(this.queuedLine, 3);
                    }
                    else {
                        super.processLine(this.queuedLine, 2);
                    }
                    this.queuedLine = null;
                }
                super.processLine(line, messageLevel);
            }
        }
        
        protected void logFlush() {
            if (this.queuedLine != null) {
                super.processLine(this.queuedLine, 3);
                this.queuedLine = null;
            }
        }
    }
}
