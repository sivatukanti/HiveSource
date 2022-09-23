// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.util.StringUtils;
import java.util.List;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ContentSummary extends QuotaUsage implements Writable
{
    private long length;
    private long fileCount;
    private long directoryCount;
    private long snapshotLength;
    private long snapshotFileCount;
    private long snapshotDirectoryCount;
    private long snapshotSpaceConsumed;
    private String erasureCodingPolicy;
    private static final String SUMMARY_FORMAT = "%12s %12s %18s ";
    private static final String[] SUMMARY_HEADER_FIELDS;
    private static final String SUMMARY_HEADER;
    private static final String ALL_HEADER;
    
    @Deprecated
    public ContentSummary() {
    }
    
    @Deprecated
    public ContentSummary(final long length, final long fileCount, final long directoryCount) {
        this(length, fileCount, directoryCount, -1L, length, -1L);
    }
    
    @Deprecated
    public ContentSummary(final long length, final long fileCount, final long directoryCount, final long quota, final long spaceConsumed, final long spaceQuota) {
        this.length = length;
        this.fileCount = fileCount;
        this.directoryCount = directoryCount;
        this.setQuota(quota);
        this.setSpaceConsumed(spaceConsumed);
        this.setSpaceQuota(spaceQuota);
    }
    
    private ContentSummary(final Builder builder) {
        super(builder);
        this.length = builder.length;
        this.fileCount = builder.fileCount;
        this.directoryCount = builder.directoryCount;
        this.snapshotLength = builder.snapshotLength;
        this.snapshotFileCount = builder.snapshotFileCount;
        this.snapshotDirectoryCount = builder.snapshotDirectoryCount;
        this.snapshotSpaceConsumed = builder.snapshotSpaceConsumed;
        this.erasureCodingPolicy = builder.erasureCodingPolicy;
    }
    
    public long getLength() {
        return this.length;
    }
    
    public long getSnapshotLength() {
        return this.snapshotLength;
    }
    
    public long getDirectoryCount() {
        return this.directoryCount;
    }
    
    public long getSnapshotDirectoryCount() {
        return this.snapshotDirectoryCount;
    }
    
    public long getFileCount() {
        return this.fileCount;
    }
    
    public long getSnapshotFileCount() {
        return this.snapshotFileCount;
    }
    
    public long getSnapshotSpaceConsumed() {
        return this.snapshotSpaceConsumed;
    }
    
    public String getErasureCodingPolicy() {
        return this.erasureCodingPolicy;
    }
    
    @InterfaceAudience.Private
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.length);
        out.writeLong(this.fileCount);
        out.writeLong(this.directoryCount);
        out.writeLong(this.getQuota());
        out.writeLong(this.getSpaceConsumed());
        out.writeLong(this.getSpaceQuota());
    }
    
    @InterfaceAudience.Private
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.length = in.readLong();
        this.fileCount = in.readLong();
        this.directoryCount = in.readLong();
        this.setQuota(in.readLong());
        this.setSpaceConsumed(in.readLong());
        this.setSpaceQuota(in.readLong());
    }
    
    @Override
    public boolean equals(final Object to) {
        if (this == to) {
            return true;
        }
        if (to instanceof ContentSummary) {
            final ContentSummary right = (ContentSummary)to;
            return this.getLength() == right.getLength() && this.getFileCount() == right.getFileCount() && this.getDirectoryCount() == right.getDirectoryCount() && this.getSnapshotLength() == right.getSnapshotLength() && this.getSnapshotFileCount() == right.getSnapshotFileCount() && this.getSnapshotDirectoryCount() == right.getSnapshotDirectoryCount() && this.getSnapshotSpaceConsumed() == right.getSnapshotSpaceConsumed() && this.getErasureCodingPolicy().equals(right.getErasureCodingPolicy()) && super.equals(to);
        }
        return super.equals(to);
    }
    
    @Override
    public int hashCode() {
        final long result = this.getLength() ^ this.getFileCount() ^ this.getDirectoryCount() ^ this.getSnapshotLength() ^ this.getSnapshotFileCount() ^ this.getSnapshotDirectoryCount() ^ this.getSnapshotSpaceConsumed() ^ (long)this.getErasureCodingPolicy().hashCode();
        return (int)result ^ super.hashCode();
    }
    
    public static String getHeader(final boolean qOption) {
        return qOption ? ContentSummary.ALL_HEADER : ContentSummary.SUMMARY_HEADER;
    }
    
    public static String[] getHeaderFields() {
        return ContentSummary.SUMMARY_HEADER_FIELDS;
    }
    
    public static String[] getQuotaHeaderFields() {
        return ContentSummary.QUOTA_HEADER_FIELDS;
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    @Override
    public String toString(final boolean qOption) {
        return this.toString(qOption, false);
    }
    
    public String toString(final boolean qOption, final boolean hOption) {
        return this.toString(qOption, hOption, false, null);
    }
    
    public String toString(final boolean qOption, final boolean hOption, final boolean xOption) {
        return this.toString(qOption, hOption, false, xOption, null);
    }
    
    public String toString(final boolean qOption, final boolean hOption, final boolean tOption, final List<StorageType> types) {
        return this.toString(qOption, hOption, tOption, false, types);
    }
    
    public String toString(final boolean qOption, final boolean hOption, final boolean tOption, final boolean xOption, final List<StorageType> types) {
        String prefix = "";
        if (tOption) {
            return this.getTypesQuotaUsage(hOption, types);
        }
        if (qOption) {
            prefix = this.getQuotaUsage(hOption);
        }
        if (xOption) {
            return prefix + String.format("%12s %12s %18s ", this.formatSize(this.directoryCount - this.snapshotDirectoryCount, hOption), this.formatSize(this.fileCount - this.snapshotFileCount, hOption), this.formatSize(this.length - this.snapshotLength, hOption));
        }
        return prefix + String.format("%12s %12s %18s ", this.formatSize(this.directoryCount, hOption), this.formatSize(this.fileCount, hOption), this.formatSize(this.length, hOption));
    }
    
    private String formatSize(final long size, final boolean humanReadable) {
        return humanReadable ? StringUtils.TraditionalBinaryPrefix.long2String(size, "", 1) : String.valueOf(size);
    }
    
    static {
        SUMMARY_HEADER_FIELDS = new String[] { "DIR_COUNT", "FILE_COUNT", "CONTENT_SIZE" };
        SUMMARY_HEADER = String.format("%12s %12s %18s ", (Object[])ContentSummary.SUMMARY_HEADER_FIELDS);
        ALL_HEADER = ContentSummary.QUOTA_HEADER + ContentSummary.SUMMARY_HEADER;
    }
    
    public static class Builder extends QuotaUsage.Builder
    {
        private long length;
        private long fileCount;
        private long directoryCount;
        private long snapshotLength;
        private long snapshotFileCount;
        private long snapshotDirectoryCount;
        private long snapshotSpaceConsumed;
        private String erasureCodingPolicy;
        
        public Builder length(final long length) {
            this.length = length;
            return this;
        }
        
        public Builder fileCount(final long fileCount) {
            this.fileCount = fileCount;
            return this;
        }
        
        public Builder directoryCount(final long directoryCount) {
            this.directoryCount = directoryCount;
            return this;
        }
        
        public Builder snapshotLength(final long snapshotLength) {
            this.snapshotLength = snapshotLength;
            return this;
        }
        
        public Builder snapshotFileCount(final long snapshotFileCount) {
            this.snapshotFileCount = snapshotFileCount;
            return this;
        }
        
        public Builder snapshotDirectoryCount(final long snapshotDirectoryCount) {
            this.snapshotDirectoryCount = snapshotDirectoryCount;
            return this;
        }
        
        public Builder snapshotSpaceConsumed(final long snapshotSpaceConsumed) {
            this.snapshotSpaceConsumed = snapshotSpaceConsumed;
            return this;
        }
        
        public Builder erasureCodingPolicy(final String ecPolicy) {
            this.erasureCodingPolicy = ecPolicy;
            return this;
        }
        
        @Override
        public Builder quota(final long quota) {
            super.quota(quota);
            return this;
        }
        
        @Override
        public Builder spaceConsumed(final long spaceConsumed) {
            super.spaceConsumed(spaceConsumed);
            return this;
        }
        
        @Override
        public Builder spaceQuota(final long spaceQuota) {
            super.spaceQuota(spaceQuota);
            return this;
        }
        
        @Override
        public Builder typeConsumed(final long[] typeConsumed) {
            super.typeConsumed(typeConsumed);
            return this;
        }
        
        @Override
        public Builder typeQuota(final StorageType type, final long quota) {
            super.typeQuota(type, quota);
            return this;
        }
        
        @Override
        public Builder typeConsumed(final StorageType type, final long consumed) {
            super.typeConsumed(type, consumed);
            return this;
        }
        
        @Override
        public Builder typeQuota(final long[] typeQuota) {
            super.typeQuota(typeQuota);
            return this;
        }
        
        @Override
        public ContentSummary build() {
            super.fileAndDirectoryCount(this.fileCount + this.directoryCount);
            return new ContentSummary(this, null);
        }
    }
}
