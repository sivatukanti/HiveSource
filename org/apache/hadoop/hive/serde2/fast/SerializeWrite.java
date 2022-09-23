// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.fast;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.ByteStream;

public interface SerializeWrite
{
    void set(final ByteStream.Output p0);
    
    void setAppend(final ByteStream.Output p0);
    
    void reset();
    
    void writeNull() throws IOException;
    
    void writeBoolean(final boolean p0) throws IOException;
    
    void writeByte(final byte p0) throws IOException;
    
    void writeShort(final short p0) throws IOException;
    
    void writeInt(final int p0) throws IOException;
    
    void writeLong(final long p0) throws IOException;
    
    void writeFloat(final float p0) throws IOException;
    
    void writeDouble(final double p0) throws IOException;
    
    void writeString(final byte[] p0) throws IOException;
    
    void writeString(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void writeHiveChar(final HiveChar p0) throws IOException;
    
    void writeHiveVarchar(final HiveVarchar p0) throws IOException;
    
    void writeBinary(final byte[] p0) throws IOException;
    
    void writeBinary(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void writeDate(final Date p0) throws IOException;
    
    void writeDate(final int p0) throws IOException;
    
    void writeTimestamp(final Timestamp p0) throws IOException;
    
    void writeHiveIntervalYearMonth(final HiveIntervalYearMonth p0) throws IOException;
    
    void writeHiveIntervalYearMonth(final int p0) throws IOException;
    
    void writeHiveIntervalDayTime(final HiveIntervalDayTime p0) throws IOException;
    
    void writeHiveIntervalDayTime(final long p0) throws IOException;
    
    void writeHiveDecimal(final HiveDecimal p0) throws IOException;
}
