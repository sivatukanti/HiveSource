// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HiveStatsUtils
{
    public static FileStatus[] getFileStatusRecurse(final Path path, final int level, final FileSystem fs) throws IOException {
        if (level < 0) {
            final List<FileStatus> result = new ArrayList<FileStatus>();
            try {
                final FileStatus fileStatus = fs.getFileStatus(path);
                FileUtils.listStatusRecursively(fs, fileStatus, result);
            }
            catch (IOException e) {
                return new FileStatus[0];
            }
            return result.toArray(new FileStatus[result.size()]);
        }
        final StringBuilder sb = new StringBuilder(path.toUri().getPath());
        for (int i = 0; i < level; ++i) {
            sb.append("/").append("*");
        }
        final Path pathPattern = new Path(path, sb.toString());
        return fs.globStatus(pathPattern, FileUtils.HIDDEN_FILES_PATH_FILTER);
    }
}
