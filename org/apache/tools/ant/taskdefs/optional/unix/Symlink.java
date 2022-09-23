// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.unix;

import java.io.PrintStream;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.DirectoryScanner;
import java.util.HashSet;
import org.apache.tools.ant.taskdefs.Execute;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.types.FileSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.tools.ant.Task;
import java.io.File;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import org.apache.tools.ant.util.SymbolicLinkUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.dispatch.DispatchTask;

public class Symlink extends DispatchTask
{
    private static final FileUtils FILE_UTILS;
    private static final SymbolicLinkUtils SYMLINK_UTILS;
    private String resource;
    private String link;
    private Vector fileSets;
    private String linkFileName;
    private boolean overwrite;
    private boolean failonerror;
    private boolean executing;
    
    public Symlink() {
        this.fileSets = new Vector();
        this.executing = false;
    }
    
    @Override
    public void init() throws BuildException {
        super.init();
        this.setDefaults();
    }
    
    @Override
    public synchronized void execute() throws BuildException {
        if (this.executing) {
            throw new BuildException("Infinite recursion detected in Symlink.execute()");
        }
        try {
            this.executing = true;
            DispatchUtils.execute(this);
        }
        finally {
            this.executing = false;
        }
    }
    
    public void single() throws BuildException {
        try {
            if (this.resource == null) {
                this.handleError("Must define the resource to symlink to!");
                return;
            }
            if (this.link == null) {
                this.handleError("Must define the link name for symlink!");
                return;
            }
            this.doLink(this.resource, this.link);
        }
        finally {
            this.setDefaults();
        }
    }
    
    public void delete() throws BuildException {
        try {
            if (this.link == null) {
                this.handleError("Must define the link name for symlink!");
                return;
            }
            this.log("Removing symlink: " + this.link);
            Symlink.SYMLINK_UTILS.deleteSymbolicLink(Symlink.FILE_UTILS.resolveFile(new File("."), this.link), this);
        }
        catch (FileNotFoundException fnfe) {
            this.handleError(fnfe.toString());
        }
        catch (IOException ioe) {
            this.handleError(ioe.toString());
        }
        finally {
            this.setDefaults();
        }
    }
    
    public void recreate() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                this.handleError("File set identifying link file(s) required for action recreate");
                return;
            }
            final Properties links = this.loadLinks(this.fileSets);
            for (final String lnk : links.keySet()) {
                final String res = links.getProperty(lnk);
                try {
                    final File test = new File(lnk);
                    if (!Symlink.SYMLINK_UTILS.isSymbolicLink(lnk)) {
                        this.doLink(res, lnk);
                    }
                    else {
                        if (test.getCanonicalPath().equals(new File(res).getCanonicalPath())) {
                            continue;
                        }
                        Symlink.SYMLINK_UTILS.deleteSymbolicLink(test, this);
                        this.doLink(res, lnk);
                    }
                }
                catch (IOException ioe) {
                    this.handleError("IO exception while creating link");
                }
            }
        }
        finally {
            this.setDefaults();
        }
    }
    
    public void record() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                this.handleError("Fileset identifying links to record required");
                return;
            }
            if (this.linkFileName == null) {
                this.handleError("Name of file to record links in required");
                return;
            }
            final Hashtable byDir = new Hashtable();
            for (final File thisLink : this.findLinks(this.fileSets)) {
                final File parent = thisLink.getParentFile();
                Vector v = byDir.get(parent);
                if (v == null) {
                    v = new Vector();
                    byDir.put(parent, v);
                }
                v.addElement(thisLink);
            }
            for (final File dir : byDir.keySet()) {
                final Vector linksInDir = byDir.get(dir);
                final Properties linksToStore = new Properties();
                for (final File lnk : linksInDir) {
                    try {
                        linksToStore.put(lnk.getName(), lnk.getCanonicalPath());
                    }
                    catch (IOException ioe) {
                        this.handleError("Couldn't get canonical name of parent link");
                    }
                }
                this.writePropertyFile(linksToStore, dir);
            }
        }
        finally {
            this.setDefaults();
        }
    }
    
    private void setDefaults() {
        this.resource = null;
        this.link = null;
        this.linkFileName = null;
        this.failonerror = true;
        this.overwrite = false;
        this.setAction("single");
        this.fileSets.clear();
    }
    
    public void setOverwrite(final boolean owrite) {
        this.overwrite = owrite;
    }
    
    public void setFailOnError(final boolean foe) {
        this.failonerror = foe;
    }
    
    @Override
    public void setAction(final String action) {
        super.setAction(action);
    }
    
    public void setLink(final String lnk) {
        this.link = lnk;
    }
    
    public void setResource(final String src) {
        this.resource = src;
    }
    
    public void setLinkfilename(final String lf) {
        this.linkFileName = lf;
    }
    
    public void addFileset(final FileSet set) {
        this.fileSets.addElement(set);
    }
    
    @Deprecated
    public static void deleteSymlink(final String path) throws IOException, FileNotFoundException {
        Symlink.SYMLINK_UTILS.deleteSymbolicLink(new File(path), null);
    }
    
    @Deprecated
    public static void deleteSymlink(final File linkfil) throws IOException {
        Symlink.SYMLINK_UTILS.deleteSymbolicLink(linkfil, null);
    }
    
    private void writePropertyFile(final Properties properties, final File dir) throws BuildException {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File(dir, this.linkFileName)));
            properties.store(bos, "Symlinks from " + dir);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe, this.getLocation());
        }
        finally {
            FileUtils.close(bos);
        }
    }
    
    private void handleError(final String msg) {
        if (this.failonerror) {
            throw new BuildException(msg);
        }
        this.log(msg);
    }
    
    private void doLink(final String res, final String lnk) throws BuildException {
        final File linkfil = new File(lnk);
        String options = "-s";
        if (this.overwrite) {
            options += "f";
            if (linkfil.exists()) {
                try {
                    Symlink.SYMLINK_UTILS.deleteSymbolicLink(linkfil, this);
                }
                catch (FileNotFoundException fnfe) {
                    this.log("Symlink disappeared before it was deleted: " + lnk);
                }
                catch (IOException ioe) {
                    this.log("Unable to overwrite preexisting link or file: " + lnk, ioe, 2);
                }
            }
        }
        final String[] cmd = { "ln", options, res, lnk };
        try {
            Execute.runCommand(this, cmd);
        }
        catch (BuildException failedToExecute) {
            if (this.failonerror) {
                throw failedToExecute;
            }
            this.log(failedToExecute.getMessage(), failedToExecute, 2);
        }
    }
    
    private HashSet findLinks(final Vector v) {
        final HashSet result = new HashSet();
        for (int size = v.size(), i = 0; i < size; ++i) {
            final FileSet fs = v.get(i);
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            final String[][] fnd = { ds.getIncludedFiles(), ds.getIncludedDirectories() };
            final File dir = fs.getDir(this.getProject());
            for (int j = 0; j < fnd.length; ++j) {
                for (int k = 0; k < fnd[j].length; ++k) {
                    try {
                        final File f = new File(dir, fnd[j][k]);
                        final File pf = f.getParentFile();
                        final String name = f.getName();
                        if (Symlink.SYMLINK_UTILS.isSymbolicLink(pf, name)) {
                            result.add(new File(pf.getCanonicalFile(), name));
                        }
                    }
                    catch (IOException e) {
                        this.handleError("IOException: " + fnd[j][k] + " omitted");
                    }
                }
            }
        }
        return result;
    }
    
    private Properties loadLinks(final Vector v) {
        final Properties finalList = new Properties();
        for (int size = v.size(), i = 0; i < size; ++i) {
            final FileSet fs = v.elementAt(i);
            final DirectoryScanner ds = new DirectoryScanner();
            fs.setupDirectoryScanner(ds, this.getProject());
            ds.setFollowSymlinks(false);
            ds.scan();
            final String[] incs = ds.getIncludedFiles();
            final File dir = fs.getDir(this.getProject());
            for (int j = 0; j < incs.length; ++j) {
                final File inc = new File(dir, incs[j]);
                File pf = inc.getParentFile();
                final Properties lnks = new Properties();
                InputStream is = null;
                try {
                    is = new BufferedInputStream(new FileInputStream(inc));
                    lnks.load(is);
                    pf = pf.getCanonicalFile();
                }
                catch (FileNotFoundException fnfe) {
                    this.handleError("Unable to find " + incs[j] + "; skipping it.");
                }
                catch (IOException ioe) {
                    this.handleError("Unable to open " + incs[j] + " or its parent dir; skipping it.");
                }
                finally {
                    FileUtils.close(is);
                }
                lnks.list(new PrintStream(new LogOutputStream(this, 2)));
                for (final String key : lnks.keySet()) {
                    finalList.put(new File(pf, key).getAbsolutePath(), lnks.getProperty(key));
                }
            }
        }
        return finalList;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
        SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    }
}
