// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.Date;
import org.apache.derby.iapi.error.StandardException;

public interface DateTimeDataValue extends DataValueDescriptor
{
    public static final int YEAR_FIELD = 0;
    public static final int MONTH_FIELD = 1;
    public static final int DAY_FIELD = 2;
    public static final int HOUR_FIELD = 3;
    public static final int MINUTE_FIELD = 4;
    public static final int SECOND_FIELD = 5;
    public static final int FRAC_SECOND_INTERVAL = 0;
    public static final int SECOND_INTERVAL = 1;
    public static final int MINUTE_INTERVAL = 2;
    public static final int HOUR_INTERVAL = 3;
    public static final int DAY_INTERVAL = 4;
    public static final int WEEK_INTERVAL = 5;
    public static final int MONTH_INTERVAL = 6;
    public static final int QUARTER_INTERVAL = 7;
    public static final int YEAR_INTERVAL = 8;
    
    NumberDataValue getYear(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue getMonth(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue getDate(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue getHours(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue getMinutes(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue getSeconds(final NumberDataValue p0) throws StandardException;
    
    DateTimeDataValue timestampAdd(final int p0, final NumberDataValue p1, final Date p2, final DateTimeDataValue p3) throws StandardException;
    
    NumberDataValue timestampDiff(final int p0, final DateTimeDataValue p1, final Date p2, final NumberDataValue p3) throws StandardException;
}
