// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.rolling.helper.FileRenameAction;
import org.apache.log4j.rolling.helper.ZipCompressAction;
import org.apache.log4j.rolling.helper.GZCompressAction;
import java.io.File;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.helpers.LogLog;

public final class FixedWindowRollingPolicy extends RollingPolicyBase
{
    private static final int MAX_WINDOW_SIZE = 12;
    private int maxIndex;
    private int minIndex;
    private boolean explicitActiveFile;
    
    public FixedWindowRollingPolicy() {
        this.minIndex = 1;
        this.maxIndex = 7;
    }
    
    public void activateOptions() {
        super.activateOptions();
        if (this.maxIndex < this.minIndex) {
            LogLog.warn("MaxIndex (" + this.maxIndex + ") cannot be smaller than MinIndex (" + this.minIndex + ").");
            LogLog.warn("Setting maxIndex to equal minIndex.");
            this.maxIndex = this.minIndex;
        }
        if (this.maxIndex - this.minIndex > 12) {
            LogLog.warn("Large window sizes are not allowed.");
            this.maxIndex = this.minIndex + 12;
            LogLog.warn("MaxIndex reduced to " + String.valueOf(this.maxIndex) + ".");
        }
        final PatternConverter itc = this.getIntegerPatternConverter();
        if (itc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.getFileNamePattern() + "] does not contain a valid integer format specifier");
        }
    }
    
    public RolloverDescription initialize(final String file, final boolean append) {
        String newActiveFile = file;
        this.explicitActiveFile = false;
        if (this.activeFileName != null) {
            this.explicitActiveFile = true;
            newActiveFile = this.activeFileName;
        }
        if (file != null) {
            this.explicitActiveFile = true;
            newActiveFile = file;
        }
        if (!this.explicitActiveFile) {
            final StringBuffer buf = new StringBuffer();
            this.formatFileName(new Integer(this.minIndex), buf);
            newActiveFile = buf.toString();
        }
        return new RolloverDescriptionImpl(newActiveFile, append, null, null);
    }
    
    public RolloverDescription rollover(final String currentFileName) {
        if (this.maxIndex < 0) {
            return null;
        }
        int purgeStart = this.minIndex;
        if (!this.explicitActiveFile) {
            ++purgeStart;
        }
        if (!this.purge(purgeStart, this.maxIndex)) {
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        this.formatFileName(new Integer(purgeStart), buf);
        final String compressedName;
        String renameTo = compressedName = buf.toString();
        Action compressAction = null;
        if (renameTo.endsWith(".gz")) {
            renameTo = renameTo.substring(0, renameTo.length() - 3);
            compressAction = new GZCompressAction(new File(renameTo), new File(compressedName), true);
        }
        else if (renameTo.endsWith(".zip")) {
            renameTo = renameTo.substring(0, renameTo.length() - 4);
            compressAction = new ZipCompressAction(new File(renameTo), new File(compressedName), true);
        }
        final FileRenameAction renameAction = new FileRenameAction(new File(currentFileName), new File(renameTo), false);
        return new RolloverDescriptionImpl(currentFileName, false, renameAction, compressAction);
    }
    
    public int getMaxIndex() {
        return this.maxIndex;
    }
    
    public int getMinIndex() {
        return this.minIndex;
    }
    
    public void setMaxIndex(final int maxIndex) {
        this.maxIndex = maxIndex;
    }
    
    public void setMinIndex(final int minIndex) {
        this.minIndex = minIndex;
    }
    
    private boolean purge(final int lowIndex, final int highIndex) {
        int suffixLength = 0;
        final List renames = new ArrayList();
        final StringBuffer buf = new StringBuffer();
        this.formatFileName(new Integer(lowIndex), buf);
        String lowFilename = buf.toString();
        if (lowFilename.endsWith(".gz")) {
            suffixLength = 3;
        }
        else if (lowFilename.endsWith(".zip")) {
            suffixLength = 4;
        }
        int i = lowIndex;
        while (i <= highIndex) {
            File toRename = new File(lowFilename);
            boolean isBase = false;
            if (suffixLength > 0) {
                final File toRenameBase = new File(lowFilename.substring(0, lowFilename.length() - suffixLength));
                if (toRename.exists()) {
                    if (toRenameBase.exists()) {
                        toRenameBase.delete();
                    }
                }
                else {
                    toRename = toRenameBase;
                    isBase = true;
                }
            }
            if (!toRename.exists()) {
                break;
            }
            if (i == highIndex) {
                if (!toRename.delete()) {
                    return false;
                }
                break;
            }
            else {
                buf.setLength(0);
                this.formatFileName(new Integer(i + 1), buf);
                String renameTo;
                final String highFilename = renameTo = buf.toString();
                if (isBase) {
                    renameTo = highFilename.substring(0, highFilename.length() - suffixLength);
                }
                renames.add(new FileRenameAction(toRename, new File(renameTo), true));
                lowFilename = highFilename;
                ++i;
            }
        }
        for (i = renames.size() - 1; i >= 0; --i) {
            final Action action = renames.get(i);
            try {
                if (!action.execute()) {
                    return false;
                }
            }
            catch (Exception ex) {
                LogLog.warn("Exception during purge in RollingFileAppender", ex);
                return false;
            }
        }
        return true;
    }
}
