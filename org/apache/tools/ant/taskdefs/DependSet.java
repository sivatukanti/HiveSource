// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.Exists;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class DependSet extends MatchingTask
{
    private static final ResourceSelector NOT_EXISTS;
    private static final ResourceComparator DATE;
    private static final ResourceComparator REVERSE_DATE;
    private Union sources;
    private Path targets;
    private boolean verbose;
    
    public DependSet() {
        this.sources = null;
        this.targets = null;
    }
    
    public synchronized Union createSources() {
        return this.sources = ((this.sources == null) ? new Union() : this.sources);
    }
    
    public void addSrcfileset(final FileSet fs) {
        this.createSources().add(fs);
    }
    
    public void addSrcfilelist(final FileList fl) {
        this.createSources().add(fl);
    }
    
    public synchronized Path createTargets() {
        return this.targets = ((this.targets == null) ? new Path(this.getProject()) : this.targets);
    }
    
    public void addTargetfileset(final FileSet fs) {
        this.createTargets().add(new HideMissingBasedir(fs));
    }
    
    public void addTargetfilelist(final FileList fl) {
        this.createTargets().add(fl);
    }
    
    public void setVerbose(final boolean b) {
        this.verbose = b;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.sources == null) {
            throw new BuildException("At least one set of source resources must be specified");
        }
        if (this.targets == null) {
            throw new BuildException("At least one set of target files must be specified");
        }
        if (this.sources.size() > 0 && this.targets.size() > 0 && !this.uptodate(this.sources, this.targets)) {
            this.log("Deleting all target files.", 3);
            if (this.verbose) {
                final String[] t = this.targets.list();
                for (int i = 0; i < t.length; ++i) {
                    this.log("Deleting " + t[i]);
                }
            }
            final Delete delete = new Delete();
            delete.bindToOwner(this);
            delete.add(this.targets);
            delete.perform();
        }
    }
    
    private boolean uptodate(final ResourceCollection src, final ResourceCollection target) {
        final Date datesel = new Date();
        datesel.setMillis(System.currentTimeMillis());
        datesel.setWhen(TimeComparison.AFTER);
        datesel.setGranularity(0L);
        this.logFuture(this.targets, datesel);
        final NonExistent missingTargets = new NonExistent((ResourceCollection)this.targets);
        final int neTargets = missingTargets.size();
        if (neTargets > 0) {
            this.log(neTargets + " nonexistent targets", 3);
            this.logMissing(missingTargets, "target");
            return false;
        }
        final Resource oldestTarget = this.getOldest(this.targets);
        this.logWithModificationTime(oldestTarget, "oldest target file");
        this.logFuture(this.sources, datesel);
        final NonExistent missingSources = new NonExistent((ResourceCollection)this.sources);
        final int neSources = missingSources.size();
        if (neSources > 0) {
            this.log(neSources + " nonexistent sources", 3);
            this.logMissing(missingSources, "source");
            return false;
        }
        final Resource newestSource = this.getNewest(this.sources);
        this.logWithModificationTime(newestSource, "newest source");
        return oldestTarget.getLastModified() >= newestSource.getLastModified();
    }
    
    private void logFuture(final ResourceCollection rc, final ResourceSelector rsel) {
        final Restrict r = new Restrict();
        r.add(rsel);
        r.add(rc);
        for (final Resource res : r) {
            this.log("Warning: " + res + " modified in the future.", 1);
        }
    }
    
    private Resource getXest(final ResourceCollection rc, final ResourceComparator c) {
        final Iterator<Resource> i = rc.iterator();
        if (!i.hasNext()) {
            return null;
        }
        Resource xest = i.next();
        while (i.hasNext()) {
            final Resource next = i.next();
            if (c.compare(xest, next) < 0) {
                xest = next;
            }
        }
        return xest;
    }
    
    private Resource getOldest(final ResourceCollection rc) {
        return this.getXest(rc, DependSet.REVERSE_DATE);
    }
    
    private Resource getNewest(final ResourceCollection rc) {
        return this.getXest(rc, DependSet.DATE);
    }
    
    private void logWithModificationTime(final Resource r, final String what) {
        this.log(r.toLongString() + " is " + what + ", modified at " + new java.util.Date(r.getLastModified()), this.verbose ? 2 : 3);
    }
    
    private void logMissing(final ResourceCollection missing, final String what) {
        if (this.verbose) {
            for (final Resource r : missing) {
                this.log("Expected " + what + " " + r.toLongString() + " is missing.");
            }
        }
    }
    
    static {
        NOT_EXISTS = new Not(new Exists());
        DATE = new org.apache.tools.ant.types.resources.comparators.Date();
        REVERSE_DATE = new Reverse(DependSet.DATE);
    }
    
    private static final class NonExistent extends Restrict
    {
        private NonExistent(final ResourceCollection rc) {
            super.add(rc);
            super.add(DependSet.NOT_EXISTS);
        }
    }
    
    private static final class HideMissingBasedir implements ResourceCollection
    {
        private FileSet fs;
        
        private HideMissingBasedir(final FileSet fs) {
            this.fs = fs;
        }
        
        public Iterator<Resource> iterator() {
            return this.basedirExists() ? this.fs.iterator() : Resources.EMPTY_ITERATOR;
        }
        
        public int size() {
            return this.basedirExists() ? this.fs.size() : 0;
        }
        
        public boolean isFilesystemOnly() {
            return true;
        }
        
        private boolean basedirExists() {
            final File basedir = this.fs.getDir();
            return basedir == null || basedir.exists();
        }
    }
}
