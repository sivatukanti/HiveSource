// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.changes;

import java.io.OutputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.util.Iterator;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import java.util.Set;

public class ChangeSetPerformer
{
    private final Set<Change> changes;
    
    public ChangeSetPerformer(final ChangeSet changeSet) {
        this.changes = changeSet.getChanges();
    }
    
    public ChangeSetResults perform(final ArchiveInputStream in, final ArchiveOutputStream out) throws IOException {
        final ChangeSetResults results = new ChangeSetResults();
        final Set<Change> workingSet = new LinkedHashSet<Change>(this.changes);
        final Iterator<Change> it = workingSet.iterator();
        while (it.hasNext()) {
            final Change change = it.next();
            if (change.type() == 2 && change.isReplaceMode()) {
                this.copyStream(change.getInput(), out, change.getEntry());
                it.remove();
                results.addedFromChangeSet(change.getEntry().getName());
            }
        }
        ArchiveEntry entry = null;
        while ((entry = in.getNextEntry()) != null) {
            boolean copy = true;
            final Iterator<Change> it2 = workingSet.iterator();
            while (it2.hasNext()) {
                final Change change2 = it2.next();
                final int type = change2.type();
                final String name = entry.getName();
                if (type == 1 && name != null) {
                    if (name.equals(change2.targetFile())) {
                        copy = false;
                        it2.remove();
                        results.deleted(name);
                        break;
                    }
                    continue;
                }
                else {
                    if (type == 4 && name != null && name.startsWith(change2.targetFile() + "/")) {
                        copy = false;
                        results.deleted(name);
                        break;
                    }
                    continue;
                }
            }
            if (copy && !this.isDeletedLater(workingSet, entry) && !results.hasBeenAdded(entry.getName())) {
                this.copyStream(in, out, entry);
                results.addedFromStream(entry.getName());
            }
        }
        final Iterator<Change> it3 = workingSet.iterator();
        while (it3.hasNext()) {
            final Change change3 = it3.next();
            if (change3.type() == 2 && !change3.isReplaceMode() && !results.hasBeenAdded(change3.getEntry().getName())) {
                this.copyStream(change3.getInput(), out, change3.getEntry());
                it3.remove();
                results.addedFromChangeSet(change3.getEntry().getName());
            }
        }
        out.finish();
        return results;
    }
    
    private boolean isDeletedLater(final Set<Change> workingSet, final ArchiveEntry entry) {
        final String source = entry.getName();
        if (!workingSet.isEmpty()) {
            for (final Change change : workingSet) {
                final int type = change.type();
                final String target = change.targetFile();
                if (type == 1 && source.equals(target)) {
                    return true;
                }
                if (type == 4 && source.startsWith(target + "/")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void copyStream(final InputStream in, final ArchiveOutputStream out, final ArchiveEntry entry) throws IOException {
        out.putArchiveEntry(entry);
        IOUtils.copy(in, out);
        out.closeArchiveEntry();
    }
}
