// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling.helper;

import java.io.File;

public final class FileRenameAction extends ActionBase
{
    private final File source;
    private final File destination;
    private final boolean renameEmptyFiles;
    
    public FileRenameAction(final File src, final File dst, final boolean renameEmptyFiles) {
        this.source = src;
        this.destination = dst;
        this.renameEmptyFiles = renameEmptyFiles;
    }
    
    public boolean execute() {
        return execute(this.source, this.destination, this.renameEmptyFiles);
    }
    
    public static boolean execute(final File source, final File destination, final boolean renameEmptyFiles) {
        if (renameEmptyFiles || source.length() > 0L) {
            return source.renameTo(destination);
        }
        return source.delete();
    }
}
