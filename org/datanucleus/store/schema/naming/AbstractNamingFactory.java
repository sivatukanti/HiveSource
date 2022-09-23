// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.naming;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public abstract class AbstractNamingFactory implements NamingFactory
{
    protected String wordSeparator;
    protected String quoteString;
    protected NamingCase namingCase;
    protected NucleusContext nucCtx;
    protected ClassLoaderResolver clr;
    Map<SchemaComponent, Integer> maxLengthByComponent;
    private static final int TRUNCATE_HASH_LENGTH = 4;
    private static final int TRUNCATE_HASH_RANGE;
    
    public AbstractNamingFactory(final NucleusContext nucCtx) {
        this.wordSeparator = "_";
        this.quoteString = "\"";
        this.namingCase = NamingCase.MIXED_CASE;
        this.maxLengthByComponent = new HashMap<SchemaComponent, Integer>();
        this.nucCtx = nucCtx;
        this.clr = nucCtx.getClassLoaderResolver(null);
    }
    
    @Override
    public NamingFactory setQuoteString(final String quote) {
        if (quote != null) {
            this.quoteString = quote;
        }
        return this;
    }
    
    @Override
    public NamingFactory setWordSeparator(final String sep) {
        if (sep != null) {
            this.wordSeparator = sep;
        }
        return this;
    }
    
    @Override
    public NamingFactory setNamingCase(final NamingCase nameCase) {
        if (nameCase != null) {
            this.namingCase = nameCase;
        }
        return this;
    }
    
    @Override
    public NamingFactory setMaximumLength(final SchemaComponent cmpt, final int max) {
        this.maxLengthByComponent.put(cmpt, max);
        return this;
    }
    
    protected int getMaximumLengthForComponent(final SchemaComponent cmpt) {
        if (this.maxLengthByComponent.containsKey(cmpt)) {
            return this.maxLengthByComponent.get(cmpt);
        }
        return -1;
    }
    
    @Override
    public String getTableName(final AbstractClassMetaData cmd) {
        String name = null;
        if (cmd.getTable() != null) {
            name = cmd.getTable();
        }
        if (name == null) {
            name = cmd.getName();
        }
        final int maxLength = this.getMaximumLengthForComponent(SchemaComponent.TABLE);
        if (name != null && maxLength > 0 && name.length() > maxLength) {
            name = truncate(name, maxLength);
        }
        name = this.getNameInRequiredCase(name);
        return name;
    }
    
    @Override
    public String getColumnName(final AbstractMemberMetaData mmd, final ColumnType type) {
        return this.getColumnName(mmd, type, 0);
    }
    
    private static final int calculateHashMax() {
        int hm = 1;
        for (int i = 0; i < 4; ++i) {
            hm *= 36;
        }
        return hm;
    }
    
    protected static String truncate(final String name, final int length) {
        if (length == 0) {
            return name;
        }
        if (name.length() <= length) {
            return name;
        }
        if (length < 4) {
            throw new IllegalArgumentException("The length argument (=" + length + ") is less than HASH_LENGTH(=" + 4 + ")!");
        }
        final int tailIndex = length - 4;
        int tailHash = name.hashCode();
        if (tailHash < 0) {
            tailHash *= -1;
        }
        tailHash %= AbstractNamingFactory.TRUNCATE_HASH_RANGE;
        String suffix = Integer.toString(tailHash, 36);
        if (suffix.length() > 4) {
            throw new IllegalStateException("Calculated hash \"" + suffix + "\" has more characters than defined by HASH_LENGTH (=" + 4 + ")! This should never happen!");
        }
        if (suffix.length() < 4) {
            final StringBuilder sb = new StringBuilder(4);
            sb.append(suffix);
            while (sb.length() < 4) {
                sb.insert(0, '0');
            }
            suffix = sb.toString();
        }
        return name.substring(0, tailIndex) + suffix;
    }
    
    protected String getNameInRequiredCase(final String name) {
        if (name == null) {
            return null;
        }
        final StringBuilder id = new StringBuilder();
        if ((this.namingCase == NamingCase.LOWER_CASE_QUOTED || this.namingCase == NamingCase.MIXED_CASE_QUOTED || this.namingCase == NamingCase.UPPER_CASE_QUOTED) && !name.startsWith(this.quoteString)) {
            id.append(this.quoteString);
        }
        if (this.namingCase == NamingCase.LOWER_CASE || this.namingCase == NamingCase.LOWER_CASE_QUOTED) {
            id.append(name.toLowerCase());
        }
        else if (this.namingCase == NamingCase.UPPER_CASE || this.namingCase == NamingCase.UPPER_CASE_QUOTED) {
            id.append(name.toUpperCase());
        }
        else {
            id.append(name);
        }
        if ((this.namingCase == NamingCase.LOWER_CASE_QUOTED || this.namingCase == NamingCase.MIXED_CASE_QUOTED || this.namingCase == NamingCase.UPPER_CASE_QUOTED) && !name.endsWith(this.quoteString)) {
            id.append(this.quoteString);
        }
        return id.toString();
    }
    
    protected String prepareColumnNameForUse(final String name) {
        String preparedName = name;
        final int maxLength = this.getMaximumLengthForComponent(SchemaComponent.COLUMN);
        if (preparedName != null && maxLength > 0 && preparedName.length() > maxLength) {
            preparedName = truncate(preparedName, maxLength);
        }
        return this.getNameInRequiredCase(preparedName);
    }
    
    static {
        TRUNCATE_HASH_RANGE = calculateHashMax();
    }
}
