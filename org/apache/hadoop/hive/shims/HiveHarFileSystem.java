// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.HarFileSystem;

public class HiveHarFileSystem extends HarFileSystem
{
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus file, final long start, final long len) throws IOException {
        final String[] hosts = { "DUMMY_HOST" };
        return new BlockLocation[] { new BlockLocation(null, hosts, 0L, file.getLen()) };
    }
    
    @Override
    public ContentSummary getContentSummary(final Path f) throws IOException {
        final FileStatus status = this.getFileStatus(f);
        if (!status.isDir()) {
            return new ContentSummary(status.getLen(), 1L, 0L);
        }
        final long[] summary = { 0L, 0L, 1L };
        for (final FileStatus s : this.listStatus(f)) {
            final ContentSummary c = s.isDir() ? this.getContentSummary(s.getPath()) : new ContentSummary(s.getLen(), 1L, 0L);
            final long[] array = summary;
            final int n = 0;
            array[n] += c.getLength();
            final long[] array2 = summary;
            final int n2 = 1;
            array2[n2] += c.getFileCount();
            final long[] array3 = summary;
            final int n3 = 2;
            array3[n3] += c.getDirectoryCount();
        }
        return new ContentSummary(summary[0], summary[1], summary[2]);
    }
}
