// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.net.MalformedURLException;
import org.apache.tools.ant.types.resources.URLResource;
import java.net.URL;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import java.util.Collection;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.types.resources.FileProvider;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.Task;

public class ImportTask extends Task
{
    private String file;
    private boolean optional;
    private String targetPrefix;
    private String prefixSeparator;
    private final Union resources;
    private static final FileUtils FILE_UTILS;
    
    public ImportTask() {
        this.targetPrefix = "USE_PROJECT_NAME_AS_TARGET_PREFIX";
        this.prefixSeparator = ".";
        (this.resources = new Union()).setCache(true);
    }
    
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
    public void setAs(final String prefix) {
        this.targetPrefix = prefix;
    }
    
    public void setPrefixSeparator(final String s) {
        this.prefixSeparator = s;
    }
    
    public void add(final ResourceCollection r) {
        this.resources.add(r);
    }
    
    @Override
    public void execute() {
        if (this.file == null && this.resources.size() == 0) {
            throw new BuildException("import requires file attribute or at least one nested resource");
        }
        if (this.getOwningTarget() == null || !"".equals(this.getOwningTarget().getName())) {
            throw new BuildException("import only allowed as a top-level task");
        }
        final ProjectHelper helper = this.getProject().getReference("ant.projectHelper");
        if (helper == null) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        final Vector<Object> importStack = helper.getImportStack();
        if (importStack.size() == 0) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        if (this.getLocation() == null || this.getLocation().getFileName() == null) {
            throw new BuildException("Unable to get location of import task");
        }
        final Union resourcesToImport = new Union(this.getProject(), this.resources);
        final Resource fromFileAttribute = this.getFileAttributeResource();
        if (fromFileAttribute != null) {
            this.resources.add(fromFileAttribute);
        }
        for (final Resource r : resourcesToImport) {
            this.importResource(helper, r);
        }
    }
    
    private void importResource(final ProjectHelper helper, final Resource importedResource) {
        final Vector<Object> importStack = helper.getImportStack();
        this.getProject().log("Importing file " + importedResource + " from " + this.getLocation().getFileName(), 3);
        if (!importedResource.isExists()) {
            final String message = "Cannot find " + importedResource + " imported from " + this.getLocation().getFileName();
            if (this.optional) {
                this.getProject().log(message, 3);
                return;
            }
            throw new BuildException(message);
        }
        else {
            File importedFile = null;
            final FileProvider fp = importedResource.as(FileProvider.class);
            if (fp != null) {
                importedFile = fp.getFile();
            }
            if (!this.isInIncludeMode() && (importStack.contains(importedResource) || (importedFile != null && importStack.contains(importedFile)))) {
                this.getProject().log("Skipped already imported file:\n   " + importedResource + "\n", 3);
                return;
            }
            final String oldPrefix = ProjectHelper.getCurrentTargetPrefix();
            final boolean oldIncludeMode = ProjectHelper.isInIncludeMode();
            final String oldSep = ProjectHelper.getCurrentPrefixSeparator();
            try {
                String prefix;
                if (this.isInIncludeMode() && oldPrefix != null && this.targetPrefix != null) {
                    prefix = oldPrefix + oldSep + this.targetPrefix;
                }
                else if (this.isInIncludeMode()) {
                    prefix = this.targetPrefix;
                }
                else if (!"USE_PROJECT_NAME_AS_TARGET_PREFIX".equals(this.targetPrefix)) {
                    prefix = this.targetPrefix;
                }
                else {
                    prefix = oldPrefix;
                }
                setProjectHelperProps(prefix, this.prefixSeparator, this.isInIncludeMode());
                final ProjectHelper subHelper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(importedResource);
                subHelper.getImportStack().addAll(helper.getImportStack());
                subHelper.getExtensionStack().addAll(helper.getExtensionStack());
                this.getProject().addReference("ant.projectHelper", subHelper);
                subHelper.parse(this.getProject(), importedResource);
                this.getProject().addReference("ant.projectHelper", helper);
                helper.getImportStack().clear();
                helper.getImportStack().addAll(subHelper.getImportStack());
                helper.getExtensionStack().clear();
                helper.getExtensionStack().addAll(subHelper.getExtensionStack());
            }
            catch (BuildException ex) {
                throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
            }
            finally {
                setProjectHelperProps(oldPrefix, oldSep, oldIncludeMode);
            }
        }
    }
    
    private Resource getFileAttributeResource() {
        if (this.file != null) {
            final File buildFile = new File(this.getLocation().getFileName()).getAbsoluteFile();
            if (buildFile.exists()) {
                final File buildFileParent = new File(buildFile.getParent());
                final File importedFile = ImportTask.FILE_UTILS.resolveFile(buildFileParent, this.file);
                return new FileResource(importedFile);
            }
            try {
                final URL buildFileURL = new URL(this.getLocation().getFileName());
                final URL importedFile2 = new URL(buildFileURL, this.file);
                return new URLResource(importedFile2);
            }
            catch (MalformedURLException ex) {
                this.log(ex.toString(), 3);
                throw new BuildException("failed to resolve " + this.file + " relative to " + this.getLocation().getFileName());
            }
        }
        return null;
    }
    
    protected final boolean isInIncludeMode() {
        return "include".equals(this.getTaskType());
    }
    
    private static void setProjectHelperProps(final String prefix, final String prefixSep, final boolean inIncludeMode) {
        ProjectHelper.setCurrentTargetPrefix(prefix);
        ProjectHelper.setCurrentPrefixSeparator(prefixSep);
        ProjectHelper.setInIncludeMode(inIncludeMode);
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
