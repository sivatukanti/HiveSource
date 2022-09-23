// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.util.StringUtils;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class QuotaUsage
{
    private long fileAndDirectoryCount;
    private long quota;
    private long spaceConsumed;
    private long spaceQuota;
    private long[] typeConsumed;
    private long[] typeQuota;
    protected static final String QUOTA_STRING_FORMAT = "%12s %15s ";
    protected static final String SPACE_QUOTA_STRING_FORMAT = "%15s %15s ";
    protected static final String[] QUOTA_HEADER_FIELDS;
    protected static final String QUOTA_HEADER;
    private static final String STORAGE_TYPE_SUMMARY_FORMAT = "%13s %17s ";
    private static final String QUOTA_NONE = "none";
    private static final String QUOTA_INF = "inf";
    
    protected QuotaUsage() {
    }
    
    protected QuotaUsage(final Builder builder) {
        this.fileAndDirectoryCount = builder.fileAndDirectoryCount;
        this.quota = builder.quota;
        this.spaceConsumed = builder.spaceConsumed;
        this.spaceQuota = builder.spaceQuota;
        this.typeConsumed = builder.typeConsumed;
        this.typeQuota = builder.typeQuota;
    }
    
    protected void setQuota(final long quota) {
        this.quota = quota;
    }
    
    protected void setSpaceConsumed(final long spaceConsumed) {
        this.spaceConsumed = spaceConsumed;
    }
    
    protected void setSpaceQuota(final long spaceQuota) {
        this.spaceQuota = spaceQuota;
    }
    
    public long getFileAndDirectoryCount() {
        return this.fileAndDirectoryCount;
    }
    
    public long getQuota() {
        return this.quota;
    }
    
    public long getSpaceConsumed() {
        return this.spaceConsumed;
    }
    
    public long getSpaceQuota() {
        return this.spaceQuota;
    }
    
    public long getTypeQuota(final StorageType type) {
        return (this.typeQuota != null) ? this.typeQuota[type.ordinal()] : -1L;
    }
    
    public long getTypeConsumed(final StorageType type) {
        return (this.typeConsumed != null) ? this.typeConsumed[type.ordinal()] : 0L;
    }
    
    private long[] getTypesQuota() {
        return this.typeQuota;
    }
    
    private long[] getTypesConsumed() {
        return this.typeConsumed;
    }
    
    public boolean isTypeQuotaSet() {
        if (this.typeQuota == null) {
            return false;
        }
        for (final StorageType t : StorageType.getTypesSupportingQuota()) {
            if (this.typeQuota[t.ordinal()] > 0L) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isTypeConsumedAvailable() {
        if (this.typeConsumed == null) {
            return false;
        }
        for (final StorageType t : StorageType.getTypesSupportingQuota()) {
            if (this.typeConsumed[t.ordinal()] > 0L) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object to) {
        return this == to || (to instanceof QuotaUsage && this.getFileAndDirectoryCount() == ((QuotaUsage)to).getFileAndDirectoryCount() && this.getQuota() == ((QuotaUsage)to).getQuota() && this.getSpaceConsumed() == ((QuotaUsage)to).getSpaceConsumed() && this.getSpaceQuota() == ((QuotaUsage)to).getSpaceQuota() && Arrays.equals(this.getTypesQuota(), ((QuotaUsage)to).getTypesQuota()) && Arrays.equals(this.getTypesConsumed(), ((QuotaUsage)to).getTypesConsumed()));
    }
    
    @Override
    public int hashCode() {
        long result = this.getFileAndDirectoryCount() ^ this.getQuota() ^ this.getSpaceConsumed() ^ this.getSpaceQuota();
        if (this.getTypesQuota() != null) {
            for (final long quota : this.getTypesQuota()) {
                result ^= quota;
            }
        }
        if (this.getTypesConsumed() != null) {
            for (final long consumed : this.getTypesConsumed()) {
                result ^= consumed;
            }
        }
        return (int)result;
    }
    
    public static String getHeader() {
        return QuotaUsage.QUOTA_HEADER;
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    public String toString(final boolean hOption) {
        return this.toString(hOption, false, null);
    }
    
    public String toString(final boolean hOption, final boolean tOption, final List<StorageType> types) {
        if (tOption) {
            return this.getTypesQuotaUsage(hOption, types);
        }
        return this.getQuotaUsage(hOption);
    }
    
    protected String getQuotaUsage(final boolean hOption) {
        String quotaStr = "none";
        String quotaRem = "inf";
        String spaceQuotaStr = "none";
        String spaceQuotaRem = "inf";
        if (this.quota > 0L) {
            quotaStr = this.formatSize(this.quota, hOption);
            quotaRem = this.formatSize(this.quota - this.fileAndDirectoryCount, hOption);
        }
        if (this.spaceQuota >= 0L) {
            spaceQuotaStr = this.formatSize(this.spaceQuota, hOption);
            spaceQuotaRem = this.formatSize(this.spaceQuota - this.spaceConsumed, hOption);
        }
        return String.format("%12s %15s %15s %15s ", quotaStr, quotaRem, spaceQuotaStr, spaceQuotaRem);
    }
    
    protected String getTypesQuotaUsage(final boolean hOption, final List<StorageType> types) {
        final StringBuffer content = new StringBuffer();
        for (final StorageType st : types) {
            final long typeQuota = this.getTypeQuota(st);
            final long typeConsumed = this.getTypeConsumed(st);
            String quotaStr = "none";
            String quotaRem = "inf";
            if (typeQuota >= 0L) {
                quotaStr = this.formatSize(typeQuota, hOption);
                quotaRem = this.formatSize(typeQuota - typeConsumed, hOption);
            }
            content.append(String.format("%13s %17s ", quotaStr, quotaRem));
        }
        return content.toString();
    }
    
    public static String getStorageTypeHeader(final List<StorageType> storageTypes) {
        final StringBuffer header = new StringBuffer();
        for (final StorageType st : storageTypes) {
            final String storageName = st.toString();
            header.append(String.format("%13s %17s ", storageName + "_QUOTA", "REM_" + storageName + "_QUOTA"));
        }
        return header.toString();
    }
    
    private String formatSize(final long size, final boolean humanReadable) {
        return humanReadable ? StringUtils.TraditionalBinaryPrefix.long2String(size, "", 1) : String.valueOf(size);
    }
    
    static {
        QUOTA_HEADER_FIELDS = new String[] { "QUOTA", "REM_QUOTA", "SPACE_QUOTA", "REM_SPACE_QUOTA" };
        QUOTA_HEADER = String.format("%12s %15s %15s %15s ", (Object[])QuotaUsage.QUOTA_HEADER_FIELDS);
    }
    
    public static class Builder
    {
        private long fileAndDirectoryCount;
        private long quota;
        private long spaceConsumed;
        private long spaceQuota;
        private long[] typeConsumed;
        private long[] typeQuota;
        
        public Builder() {
            this.quota = -1L;
            this.spaceQuota = -1L;
            this.typeConsumed = new long[StorageType.values().length];
            this.typeQuota = new long[StorageType.values().length];
            for (int i = 0; i < this.typeQuota.length; ++i) {
                this.typeQuota[i] = -1L;
            }
        }
        
        public Builder fileAndDirectoryCount(final long count) {
            this.fileAndDirectoryCount = count;
            return this;
        }
        
        public Builder quota(final long quota) {
            this.quota = quota;
            return this;
        }
        
        public Builder spaceConsumed(final long spaceConsumed) {
            this.spaceConsumed = spaceConsumed;
            return this;
        }
        
        public Builder spaceQuota(final long spaceQuota) {
            this.spaceQuota = spaceQuota;
            return this;
        }
        
        public Builder typeConsumed(final long[] typeConsumed) {
            for (int i = 0; i < typeConsumed.length; ++i) {
                this.typeConsumed[i] = typeConsumed[i];
            }
            return this;
        }
        
        public Builder typeQuota(final StorageType type, final long quota) {
            this.typeQuota[type.ordinal()] = quota;
            return this;
        }
        
        public Builder typeConsumed(final StorageType type, final long consumed) {
            this.typeConsumed[type.ordinal()] = consumed;
            return this;
        }
        
        public Builder typeQuota(final long[] typeQuota) {
            for (int i = 0; i < typeQuota.length; ++i) {
                this.typeQuota[i] = typeQuota[i];
            }
            return this;
        }
        
        public QuotaUsage build() {
            return new QuotaUsage(this);
        }
    }
}
