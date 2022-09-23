// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.Collections;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.selectors.FileSelector;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Vector;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;

public class Files extends AbstractSelectorContainer implements ResourceCollection
{
    private static final Iterator<Resource> EMPTY_ITERATOR;
    private PatternSet defaultPatterns;
    private Vector<PatternSet> additionalPatterns;
    private boolean useDefaultExcludes;
    private boolean caseSensitive;
    private boolean followSymlinks;
    private DirectoryScanner ds;
    
    public Files() {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new Vector<PatternSet>();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.ds = null;
    }
    
    protected Files(final Files f) {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new Vector<PatternSet>();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.ds = null;
        this.defaultPatterns = f.defaultPatterns;
        this.additionalPatterns = f.additionalPatterns;
        this.useDefaultExcludes = f.useDefaultExcludes;
        this.caseSensitive = f.caseSensitive;
        this.followSymlinks = f.followSymlinks;
        this.ds = f.ds;
        this.setProject(f.getProject());
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (this.hasPatterns(this.defaultPatterns)) {
            throw this.tooManyAttributes();
        }
        if (!this.additionalPatterns.isEmpty()) {
            throw this.noChildrenAllowed();
        }
        if (this.hasSelectors()) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }
    
    public synchronized PatternSet createPatternSet() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        final PatternSet patterns = new PatternSet();
        this.additionalPatterns.addElement(patterns);
        this.ds = null;
        this.setChecked(false);
        return patterns;
    }
    
    public synchronized PatternSet.NameEntry createInclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createInclude();
    }
    
    public synchronized PatternSet.NameEntry createIncludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createIncludesFile();
    }
    
    public synchronized PatternSet.NameEntry createExclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExclude();
    }
    
    public synchronized PatternSet.NameEntry createExcludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExcludesFile();
    }
    
    public synchronized void setIncludes(final String includes) {
        this.checkAttributesAllowed();
        this.defaultPatterns.setIncludes(includes);
        this.ds = null;
    }
    
    public synchronized void appendIncludes(final String[] includes) {
        this.checkAttributesAllowed();
        if (includes != null) {
            for (int i = 0; i < includes.length; ++i) {
                this.defaultPatterns.createInclude().setName(includes[i]);
            }
            this.ds = null;
        }
    }
    
    public synchronized void setExcludes(final String excludes) {
        this.checkAttributesAllowed();
        this.defaultPatterns.setExcludes(excludes);
        this.ds = null;
    }
    
    public synchronized void appendExcludes(final String[] excludes) {
        this.checkAttributesAllowed();
        if (excludes != null) {
            for (int i = 0; i < excludes.length; ++i) {
                this.defaultPatterns.createExclude().setName(excludes[i]);
            }
            this.ds = null;
        }
    }
    
    public synchronized void setIncludesfile(final File incl) throws BuildException {
        this.checkAttributesAllowed();
        this.defaultPatterns.setIncludesfile(incl);
        this.ds = null;
    }
    
    public synchronized void setExcludesfile(final File excl) throws BuildException {
        this.checkAttributesAllowed();
        this.defaultPatterns.setExcludesfile(excl);
        this.ds = null;
    }
    
    public synchronized void setDefaultexcludes(final boolean useDefaultExcludes) {
        this.checkAttributesAllowed();
        this.useDefaultExcludes = useDefaultExcludes;
        this.ds = null;
    }
    
    public synchronized boolean getDefaultexcludes() {
        return this.isReference() ? this.getRef().getDefaultexcludes() : this.useDefaultExcludes;
    }
    
    public synchronized void setCaseSensitive(final boolean caseSensitive) {
        this.checkAttributesAllowed();
        this.caseSensitive = caseSensitive;
        this.ds = null;
    }
    
    public synchronized boolean isCaseSensitive() {
        return this.isReference() ? this.getRef().isCaseSensitive() : this.caseSensitive;
    }
    
    public synchronized void setFollowSymlinks(final boolean followSymlinks) {
        this.checkAttributesAllowed();
        this.followSymlinks = followSymlinks;
        this.ds = null;
    }
    
    public synchronized boolean isFollowSymlinks() {
        return this.isReference() ? this.getRef().isFollowSymlinks() : this.followSymlinks;
    }
    
    public synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.ensureDirectoryScannerSetup();
        this.ds.scan();
        final int fct = this.ds.getIncludedFilesCount();
        final int dct = this.ds.getIncludedDirsCount();
        if (fct + dct == 0) {
            return Files.EMPTY_ITERATOR;
        }
        final FileResourceIterator result = new FileResourceIterator(this.getProject());
        if (fct > 0) {
            result.addFiles(this.ds.getIncludedFiles());
        }
        if (dct > 0) {
            result.addFiles(this.ds.getIncludedDirectories());
        }
        return result;
    }
    
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.ensureDirectoryScannerSetup();
        this.ds.scan();
        return this.ds.getIncludedFilesCount() + this.ds.getIncludedDirsCount();
    }
    
    public synchronized boolean hasPatterns() {
        if (this.isReference()) {
            return this.getRef().hasPatterns();
        }
        this.dieOnCircularReference();
        if (this.hasPatterns(this.defaultPatterns)) {
            return true;
        }
        for (final PatternSet patternSet : this.additionalPatterns) {
            if (this.hasPatterns(patternSet)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized void appendSelector(final FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        super.appendSelector(selector);
        this.ds = null;
    }
    
    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        final Iterator<Resource> i = this.iterator();
        if (!i.hasNext()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        while (i.hasNext()) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(i.next());
        }
        return sb.toString();
    }
    
    @Override
    public synchronized Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        final Files f = (Files)super.clone();
        f.defaultPatterns = (PatternSet)this.defaultPatterns.clone();
        f.additionalPatterns = new Vector<PatternSet>(this.additionalPatterns.size());
        for (final PatternSet ps : this.additionalPatterns) {
            f.additionalPatterns.add((PatternSet)ps.clone());
        }
        return f;
    }
    
    public String[] mergeIncludes(final Project p) {
        return this.mergePatterns(p).getIncludePatterns(p);
    }
    
    public String[] mergeExcludes(final Project p) {
        return this.mergePatterns(p).getExcludePatterns(p);
    }
    
    public synchronized PatternSet mergePatterns(final Project p) {
        if (this.isReference()) {
            return this.getRef().mergePatterns(p);
        }
        this.dieOnCircularReference();
        final PatternSet ps = new PatternSet();
        ps.append(this.defaultPatterns, p);
        for (int count = this.additionalPatterns.size(), i = 0; i < count; ++i) {
            final Object o = this.additionalPatterns.elementAt(i);
            ps.append((PatternSet)o, p);
        }
        return ps;
    }
    
    public boolean isFilesystemOnly() {
        return true;
    }
    
    protected Files getRef() {
        return (Files)this.getCheckedRef();
    }
    
    private synchronized void ensureDirectoryScannerSetup() {
        this.dieOnCircularReference();
        if (this.ds == null) {
            this.ds = new DirectoryScanner();
            final PatternSet ps = this.mergePatterns(this.getProject());
            this.ds.setIncludes(ps.getIncludePatterns(this.getProject()));
            this.ds.setExcludes(ps.getExcludePatterns(this.getProject()));
            this.ds.setSelectors(this.getSelectors(this.getProject()));
            if (this.useDefaultExcludes) {
                this.ds.addDefaultExcludes();
            }
            this.ds.setCaseSensitive(this.caseSensitive);
            this.ds.setFollowSymlinks(this.followSymlinks);
        }
    }
    
    private boolean hasPatterns(final PatternSet ps) {
        final String[] includePatterns = ps.getIncludePatterns(this.getProject());
        final String[] excludePatterns = ps.getExcludePatterns(this.getProject());
        return (includePatterns != null && includePatterns.length > 0) || (includePatterns != null && excludePatterns.length > 0);
    }
    
    static {
        EMPTY_ITERATOR = Collections.emptySet().iterator();
    }
}
