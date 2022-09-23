// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface ColumnMetadata extends Metadata
{
    ColumnMetadata setName(final String p0);
    
    String getName();
    
    ColumnMetadata setTarget(final String p0);
    
    String getTarget();
    
    ColumnMetadata setTargetField(final String p0);
    
    String getTargetField();
    
    ColumnMetadata setJDBCType(final String p0);
    
    String getJDBCType();
    
    ColumnMetadata setSQLType(final String p0);
    
    String getSQLType();
    
    ColumnMetadata setLength(final int p0);
    
    Integer getLength();
    
    ColumnMetadata setScale(final int p0);
    
    Integer getScale();
    
    ColumnMetadata setAllowsNull(final boolean p0);
    
    Boolean getAllowsNull();
    
    ColumnMetadata setDefaultValue(final String p0);
    
    String getDefaultValue();
    
    ColumnMetadata setInsertValue(final String p0);
    
    String getInsertValue();
}
