// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import org.datanucleus.ClassConstants;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.io.File;
import java.util.ArrayList;
import org.apache.tools.ant.types.FileSet;
import java.util.List;
import org.datanucleus.util.Localiser;
import org.apache.tools.ant.taskdefs.Java;

public class SchemaToolTask extends Java
{
    protected static final Localiser LOCALISER;
    private int runMode;
    List<FileSet> filesets;
    
    public SchemaToolTask() {
        this.runMode = 1;
        this.filesets = new ArrayList<FileSet>();
        this.setClassname("org.datanucleus.store.schema.SchemaTool");
        this.setFork(true);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.runMode == 1) {
            this.createArg().setValue("-create");
        }
        else if (this.runMode == 2) {
            this.createArg().setValue("-delete");
        }
        else if (this.runMode == 3) {
            this.createArg().setValue("-deletecreate");
        }
        else if (this.runMode == 4) {
            this.createArg().setValue("-validate");
        }
        else if (this.runMode == 5) {
            this.createArg().setValue("-dbinfo");
        }
        else if (this.runMode == 6) {
            this.createArg().setValue("-schemainfo");
        }
        final File[] files = this.getFiles();
        for (int i = 0; i < files.length; ++i) {
            this.createArg().setFile(files[i]);
        }
        super.execute();
    }
    
    public void addFileSet(final FileSet fs) {
        this.filesets.add(fs);
    }
    
    protected File[] getFiles() {
        final List<File> v = new ArrayList<File>();
        for (final FileSet fs : this.filesets) {
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            ds.scan();
            final String[] f = ds.getIncludedFiles();
            for (int j = 0; j < f.length; ++j) {
                final String pathname = f[j];
                File file = new File(ds.getBasedir(), pathname);
                file = this.getProject().resolveFile(file.getPath());
                v.add(file);
            }
        }
        return v.toArray(new File[v.size()]);
    }
    
    public void setVerbose(final boolean verbose) {
        if (verbose) {
            this.createArg().setValue("-v");
            this.log("SchemaTool verbose: " + verbose, 3);
        }
    }
    
    public void setProps(final String propsFileName) {
        if (propsFileName != null && propsFileName.length() > 0) {
            this.createArg().setLine("-props " + propsFileName);
            this.log("SchemaTool props: " + propsFileName, 3);
        }
    }
    
    public void setDdlFile(final String file) {
        if (file != null && file.length() > 0) {
            this.createArg().setLine("-ddlFile " + file);
            this.log("SchemaTool ddlFile: " + file, 3);
        }
    }
    
    public void setCompleteDdl(final boolean complete) {
        if (complete) {
            this.createArg().setValue("-completeDdl");
            this.log("SchemaTool completeDdl: " + complete, 3);
        }
    }
    
    public void setIncludeAutoStart(final boolean include) {
        if (include) {
            this.createArg().setValue("-includeAutoStart");
            this.log("SchemaTool includeAutoStart: " + include, 3);
        }
    }
    
    public void setPersistenceUnit(final String unitName) {
        if (unitName != null && unitName.length() > 0) {
            this.createArg().setLine("-pu " + unitName);
            this.log("SchemaTool pu: " + unitName, 3);
        }
    }
    
    public void setApi(final String api) {
        if (api != null && api.length() > 0) {
            this.createArg().setValue("-api");
            this.createArg().setValue(api);
            this.log("SchemaTool api: " + api, 3);
        }
    }
    
    public void setMode(final String mode) {
        if (mode == null) {
            return;
        }
        if (mode.equalsIgnoreCase("create")) {
            this.runMode = 1;
        }
        else if (mode.equalsIgnoreCase("delete")) {
            this.runMode = 2;
        }
        else if (mode.equalsIgnoreCase("deletecreate")) {
            this.runMode = 3;
        }
        else if (mode.equalsIgnoreCase("validate")) {
            this.runMode = 4;
        }
        else if (mode.equalsIgnoreCase("dbinfo")) {
            this.runMode = 5;
        }
        else if (mode.equalsIgnoreCase("schemainfo")) {
            this.runMode = 6;
        }
        else {
            System.err.println(SchemaToolTask.LOCALISER.msg("014036"));
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
