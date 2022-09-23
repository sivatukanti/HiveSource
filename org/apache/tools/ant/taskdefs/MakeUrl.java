// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import java.util.ListIterator;
import java.util.LinkedList;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;
import java.util.List;
import java.io.File;
import org.apache.tools.ant.Task;

public class MakeUrl extends Task
{
    private String property;
    private File file;
    private String separator;
    private List<FileSet> filesets;
    private List<Path> paths;
    private boolean validate;
    public static final String ERROR_MISSING_FILE = "A source file is missing: ";
    public static final String ERROR_NO_PROPERTY = "No property defined";
    public static final String ERROR_NO_FILES = "No files defined";
    
    public MakeUrl() {
        this.separator = " ";
        this.filesets = new LinkedList<FileSet>();
        this.paths = new LinkedList<Path>();
        this.validate = true;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void addFileSet(final FileSet fileset) {
        this.filesets.add(fileset);
    }
    
    public void setSeparator(final String separator) {
        this.separator = separator;
    }
    
    public void setValidate(final boolean validate) {
        this.validate = validate;
    }
    
    public void addPath(final Path path) {
        this.paths.add(path);
    }
    
    private String filesetsToURL() {
        if (this.filesets.isEmpty()) {
            return "";
        }
        int count = 0;
        final StringBuilder urls = new StringBuilder();
        final ListIterator<FileSet> list = this.filesets.listIterator();
        while (list.hasNext()) {
            final FileSet set = list.next();
            final DirectoryScanner scanner = set.getDirectoryScanner(this.getProject());
            final String[] files = scanner.getIncludedFiles();
            for (int i = 0; i < files.length; ++i) {
                final File f = new File(scanner.getBasedir(), files[i]);
                this.validateFile(f);
                final String asUrl = this.toURL(f);
                urls.append(asUrl);
                this.log(asUrl, 4);
                urls.append(this.separator);
                ++count;
            }
        }
        return this.stripTrailingSeparator(urls, count);
    }
    
    private String stripTrailingSeparator(final StringBuilder urls, final int count) {
        if (count > 0) {
            urls.delete(urls.length() - this.separator.length(), urls.length());
            return new String(urls);
        }
        return "";
    }
    
    private String pathsToURL() {
        if (this.paths.isEmpty()) {
            return "";
        }
        int count = 0;
        final StringBuilder urls = new StringBuilder();
        final ListIterator<Path> list = this.paths.listIterator();
        while (list.hasNext()) {
            final Path path = list.next();
            final String[] elements = path.list();
            for (int i = 0; i < elements.length; ++i) {
                final File f = new File(elements[i]);
                this.validateFile(f);
                final String asUrl = this.toURL(f);
                urls.append(asUrl);
                this.log(asUrl, 4);
                urls.append(this.separator);
                ++count;
            }
        }
        return this.stripTrailingSeparator(urls, count);
    }
    
    private void validateFile(final File fileToCheck) {
        if (this.validate && !fileToCheck.exists()) {
            throw new BuildException("A source file is missing: " + fileToCheck.toString());
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        if (this.getProject().getProperty(this.property) != null) {
            return;
        }
        final String filesetURL = this.filesetsToURL();
        String url;
        if (this.file != null) {
            this.validateFile(this.file);
            url = this.toURL(this.file);
            if (filesetURL.length() > 0) {
                url = url + this.separator + filesetURL;
            }
        }
        else {
            url = filesetURL;
        }
        final String pathURL = this.pathsToURL();
        if (pathURL.length() > 0) {
            if (url.length() > 0) {
                url = url + this.separator + pathURL;
            }
            else {
                url = pathURL;
            }
        }
        this.log("Setting " + this.property + " to URL " + url, 3);
        this.getProject().setNewProperty(this.property, url);
    }
    
    private void validate() {
        if (this.property == null) {
            throw new BuildException("No property defined");
        }
        if (this.file == null && this.filesets.isEmpty() && this.paths.isEmpty()) {
            throw new BuildException("No files defined");
        }
    }
    
    private String toURL(final File fileToConvert) {
        final String url = FileUtils.getFileUtils().toURI(fileToConvert.getAbsolutePath());
        return url;
    }
}
