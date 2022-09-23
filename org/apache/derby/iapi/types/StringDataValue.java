// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.Clob;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import java.text.RuleBasedCollator;
import org.apache.derby.iapi.error.StandardException;

public interface StringDataValue extends ConcatableDataValue
{
    public static final int BOTH = 0;
    public static final int TRAILING = 1;
    public static final int LEADING = 2;
    public static final int COLLATION_DERIVATION_NONE = 0;
    public static final int COLLATION_DERIVATION_IMPLICIT = 1;
    public static final int COLLATION_DERIVATION_EXPLICIT = 2;
    public static final int COLLATION_TYPE_UCS_BASIC = 0;
    public static final int COLLATION_TYPE_TERRITORY_BASED = 1;
    public static final int COLLATION_TYPE_TERRITORY_BASED_PRIMARY = 2;
    public static final int COLLATION_TYPE_TERRITORY_BASED_SECONDARY = 3;
    public static final int COLLATION_TYPE_TERRITORY_BASED_TERTIARY = 4;
    public static final int COLLATION_TYPE_TERRITORY_BASED_IDENTICAL = 5;
    
    StringDataValue concatenate(final StringDataValue p0, final StringDataValue p1, final StringDataValue p2) throws StandardException;
    
    BooleanDataValue like(final DataValueDescriptor p0) throws StandardException;
    
    BooleanDataValue like(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    StringDataValue ansiTrim(final int p0, final StringDataValue p1, final StringDataValue p2) throws StandardException;
    
    StringDataValue upper(final StringDataValue p0) throws StandardException;
    
    StringDataValue lower(final StringDataValue p0) throws StandardException;
    
    NumberDataValue locate(final StringDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    char[] getCharArray() throws StandardException;
    
    StringDataValue getValue(final RuleBasedCollator p0);
    
    StreamHeaderGenerator getStreamHeaderGenerator();
    
    void setStreamHeaderFormat(final Boolean p0);
    
    CharacterStreamDescriptor getStreamWithDescriptor() throws StandardException;
    
    void setValue(final Clob p0) throws StandardException;
}
