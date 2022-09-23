// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public class JarLibDisplayTask extends Task
{
    private File libraryFile;
    private final Vector libraryFileSets;
    
    public JarLibDisplayTask() {
        this.libraryFileSets = new Vector();
    }
    
    public void setFile(final File file) {
        this.libraryFile = file;
    }
    
    public void addFileset(final FileSet fileSet) {
        this.libraryFileSets.addElement(fileSet);
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        final LibraryDisplayer displayer = new LibraryDisplayer();
        if (!this.libraryFileSets.isEmpty()) {
            for (final FileSet fileSet : this.libraryFileSets) {
                final DirectoryScanner scanner = fileSet.getDirectoryScanner(this.getProject());
                final File basedir = scanner.getBasedir();
                final String[] files = scanner.getIncludedFiles();
                for (int i = 0; i < files.length; ++i) {
                    final File file = new File(basedir, files[i]);
                    displayer.displayLibrary(file);
                }
            }
        }
        else {
            displayer.displayLibrary(this.libraryFile);
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.libraryFile && this.libraryFileSets.isEmpty()) {
            final String message = "File attribute not specified.";
            throw new BuildException("File attribute not specified.");
        }
        if (null != this.libraryFile && !this.libraryFile.exists()) {
            final String message = "File '" + this.libraryFile + "' does not exist.";
            throw new BuildException(message);
        }
        if (null != this.libraryFile && !this.libraryFile.isFile()) {
            final String message = "'" + this.libraryFile + "' is not a file.";
            throw new BuildException(message);
        }
    }
}
