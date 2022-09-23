// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;
import org.apache.log4j.rolling.helper.ZipCompressAction;
import org.apache.log4j.rolling.helper.GZCompressAction;
import org.apache.log4j.rolling.helper.FileRenameAction;
import java.io.File;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.pattern.PatternConverter;
import java.util.Date;

public final class TimeBasedRollingPolicy extends RollingPolicyBase implements TriggeringPolicy
{
    private long nextCheck;
    private String lastFileName;
    private int suffixLength;
    
    public TimeBasedRollingPolicy() {
        this.nextCheck = 0L;
        this.lastFileName = null;
        this.suffixLength = 0;
    }
    
    public void activateOptions() {
        super.activateOptions();
        final PatternConverter dtc = this.getDatePatternConverter();
        if (dtc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.getFileNamePattern() + "] does not contain a valid date format specifier");
        }
        final long n = System.currentTimeMillis();
        final StringBuffer buf = new StringBuffer();
        this.formatFileName(new Date(n), buf);
        this.lastFileName = buf.toString();
        this.suffixLength = 0;
        if (this.lastFileName.endsWith(".gz")) {
            this.suffixLength = 3;
        }
        else if (this.lastFileName.endsWith(".zip")) {
            this.suffixLength = 4;
        }
    }
    
    public RolloverDescription initialize(final String currentActiveFile, final boolean append) {
        final long n = System.currentTimeMillis();
        this.nextCheck = (n / 1000L + 1L) * 1000L;
        final StringBuffer buf = new StringBuffer();
        this.formatFileName(new Date(n), buf);
        this.lastFileName = buf.toString();
        if (this.activeFileName != null) {
            return new RolloverDescriptionImpl(this.activeFileName, append, null, null);
        }
        if (currentActiveFile != null) {
            return new RolloverDescriptionImpl(currentActiveFile, append, null, null);
        }
        return new RolloverDescriptionImpl(this.lastFileName.substring(0, this.lastFileName.length() - this.suffixLength), append, null, null);
    }
    
    public RolloverDescription rollover(final String currentActiveFile) {
        final long n = System.currentTimeMillis();
        this.nextCheck = (n / 1000L + 1L) * 1000L;
        final StringBuffer buf = new StringBuffer();
        this.formatFileName(new Date(n), buf);
        final String newFileName = buf.toString();
        if (newFileName.equals(this.lastFileName)) {
            return null;
        }
        Action renameAction = null;
        Action compressAction = null;
        final String lastBaseName = this.lastFileName.substring(0, this.lastFileName.length() - this.suffixLength);
        String nextActiveFile = newFileName.substring(0, newFileName.length() - this.suffixLength);
        if (!currentActiveFile.equals(lastBaseName)) {
            renameAction = new FileRenameAction(new File(currentActiveFile), new File(lastBaseName), true);
            nextActiveFile = currentActiveFile;
        }
        if (this.suffixLength == 3) {
            compressAction = new GZCompressAction(new File(lastBaseName), new File(this.lastFileName), true);
        }
        if (this.suffixLength == 4) {
            compressAction = new ZipCompressAction(new File(lastBaseName), new File(this.lastFileName), true);
        }
        this.lastFileName = newFileName;
        return new RolloverDescriptionImpl(nextActiveFile, false, renameAction, compressAction);
    }
    
    public boolean isTriggeringEvent(final Appender appender, final LoggingEvent event, final String filename, final long fileLength) {
        return System.currentTimeMillis() >= this.nextCheck;
    }
}
