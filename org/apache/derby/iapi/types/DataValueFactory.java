// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.text.RuleBasedCollator;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.Clob;
import java.sql.Blob;
import org.apache.derby.iapi.error.StandardException;

public interface DataValueFactory
{
    NumberDataValue getDataValue(final Integer p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final char p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final Short p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final Byte p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final Long p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final Float p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final Double p0, final NumberDataValue p1) throws StandardException;
    
    BooleanDataValue getDataValue(final Boolean p0, final BooleanDataValue p1) throws StandardException;
    
    BitDataValue getLongVarbitDataValue(final byte[] p0, final BitDataValue p1) throws StandardException;
    
    BitDataValue getBlobDataValue(final byte[] p0, final BitDataValue p1) throws StandardException;
    
    BitDataValue getBlobDataValue(final Blob p0, final BitDataValue p1) throws StandardException;
    
    StringDataValue getVarcharDataValue(final String p0);
    
    StringDataValue getVarcharDataValue(final String p0, final StringDataValue p1) throws StandardException;
    
    StringDataValue getVarcharDataValue(final String p0, final StringDataValue p1, final int p2) throws StandardException;
    
    StringDataValue getLongvarcharDataValue(final String p0);
    
    StringDataValue getLongvarcharDataValue(final String p0, final StringDataValue p1) throws StandardException;
    
    StringDataValue getLongvarcharDataValue(final String p0, final StringDataValue p1, final int p2) throws StandardException;
    
    StringDataValue getClobDataValue(final String p0, final StringDataValue p1) throws StandardException;
    
    StringDataValue getClobDataValue(final Clob p0, final StringDataValue p1) throws StandardException;
    
    StringDataValue getClobDataValue(final String p0, final StringDataValue p1, final int p2) throws StandardException;
    
    StringDataValue getClobDataValue(final Clob p0, final StringDataValue p1, final int p2) throws StandardException;
    
    UserDataValue getDataValue(final Object p0, final UserDataValue p1);
    
    RefDataValue getDataValue(final RowLocation p0, final RefDataValue p1);
    
    NumberDataValue getDataValue(final int p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final long p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final float p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final double p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final short p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDataValue(final byte p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDecimalDataValue(final Number p0) throws StandardException;
    
    NumberDataValue getDecimalDataValue(final Number p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDecimalDataValue(final Long p0, final NumberDataValue p1) throws StandardException;
    
    NumberDataValue getDecimalDataValue(final String p0) throws StandardException;
    
    NumberDataValue getDecimalDataValue(final String p0, final NumberDataValue p1) throws StandardException;
    
    BooleanDataValue getDataValue(final boolean p0, final BooleanDataValue p1) throws StandardException;
    
    BitDataValue getBitDataValue(final byte[] p0) throws StandardException;
    
    BitDataValue getBitDataValue(final byte[] p0, final BitDataValue p1) throws StandardException;
    
    BitDataValue getVarbitDataValue(final byte[] p0, final BitDataValue p1) throws StandardException;
    
    StringDataValue getCharDataValue(final String p0);
    
    StringDataValue getCharDataValue(final String p0, final StringDataValue p1) throws StandardException;
    
    StringDataValue getCharDataValue(final String p0, final StringDataValue p1, final int p2) throws StandardException;
    
    DateTimeDataValue getDataValue(final Date p0, final DateTimeDataValue p1) throws StandardException;
    
    DateTimeDataValue getDataValue(final Time p0, final DateTimeDataValue p1) throws StandardException;
    
    DateTimeDataValue getDataValue(final Timestamp p0, final DateTimeDataValue p1) throws StandardException;
    
    DateTimeDataValue getTimestamp(final DataValueDescriptor p0) throws StandardException;
    
    DateTimeDataValue getTimestamp(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    DateTimeDataValue getDate(final DataValueDescriptor p0) throws StandardException;
    
    DateTimeDataValue getDateValue(final String p0, final boolean p1) throws StandardException;
    
    DateTimeDataValue getTimeValue(final String p0, final boolean p1) throws StandardException;
    
    DateTimeDataValue getTimestampValue(final String p0, final boolean p1) throws StandardException;
    
    XMLDataValue getXMLDataValue(final XMLDataValue p0) throws StandardException;
    
    NumberDataValue getNullInteger(final NumberDataValue p0);
    
    NumberDataValue getNullShort(final NumberDataValue p0);
    
    NumberDataValue getNullByte(final NumberDataValue p0);
    
    NumberDataValue getNullLong(final NumberDataValue p0);
    
    NumberDataValue getNullFloat(final NumberDataValue p0);
    
    NumberDataValue getNullDouble(final NumberDataValue p0);
    
    NumberDataValue getNullDecimal(final NumberDataValue p0);
    
    BooleanDataValue getNullBoolean(final BooleanDataValue p0);
    
    BitDataValue getNullBit(final BitDataValue p0) throws StandardException;
    
    BitDataValue getNullVarbit(final BitDataValue p0) throws StandardException;
    
    BitDataValue getNullLongVarbit(final BitDataValue p0) throws StandardException;
    
    BitDataValue getNullBlob(final BitDataValue p0) throws StandardException;
    
    StringDataValue getNullChar(final StringDataValue p0);
    
    StringDataValue getNullChar(final StringDataValue p0, final int p1) throws StandardException;
    
    StringDataValue getNullVarchar(final StringDataValue p0);
    
    StringDataValue getNullVarchar(final StringDataValue p0, final int p1) throws StandardException;
    
    StringDataValue getNullLongvarchar(final StringDataValue p0);
    
    StringDataValue getNullLongvarchar(final StringDataValue p0, final int p1) throws StandardException;
    
    StringDataValue getNullClob(final StringDataValue p0);
    
    StringDataValue getNullClob(final StringDataValue p0, final int p1) throws StandardException;
    
    UserDataValue getNullObject(final UserDataValue p0);
    
    RefDataValue getNullRef(final RefDataValue p0);
    
    DateTimeDataValue getNullDate(final DateTimeDataValue p0);
    
    DateTimeDataValue getNullTime(final DateTimeDataValue p0);
    
    DateTimeDataValue getNullTimestamp(final DateTimeDataValue p0);
    
    XMLDataValue getNullXML(final XMLDataValue p0);
    
    RuleBasedCollator getCharacterCollator(final int p0) throws StandardException;
    
    DataValueDescriptor getNull(final int p0, final int p1) throws StandardException;
}
