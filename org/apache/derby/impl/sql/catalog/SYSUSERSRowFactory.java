// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Timestamp;
import java.util.Arrays;
import org.apache.derby.iapi.types.SQLTimestamp;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.sql.dictionary.UserDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSUSERSRowFactory extends CatalogRowFactory
{
    public static final String TABLE_NAME = "SYSUSERS";
    public static final String SYSUSERS_UUID = "9810800c-0134-14a5-40c1-000004f61f90";
    public static final String PASSWORD_COL_NAME = "PASSWORD";
    private static final int SYSUSERS_COLUMN_COUNT = 4;
    public static final int USERNAME_COL_NUM = 1;
    public static final int HASHINGSCHEME_COL_NUM = 2;
    public static final int PASSWORD_COL_NUM = 3;
    public static final int LASTMODIFIED_COL_NUM = 4;
    static final int SYSUSERS_INDEX1_ID = 0;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSUSERSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(4, "SYSUSERS", SYSUSERSRowFactory.indexColumnPositions, SYSUSERSRowFactory.uniqueness, SYSUSERSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String userName = null;
        String hashingScheme = null;
        char[] andZeroPassword = null;
        Timestamp lastModified = null;
        ExecRow valueRow;
        try {
            if (tupleDescriptor != null) {
                final UserDescriptor userDescriptor = (UserDescriptor)tupleDescriptor;
                userName = userDescriptor.getUserName();
                hashingScheme = userDescriptor.getHashingScheme();
                andZeroPassword = userDescriptor.getAndZeroPassword();
                lastModified = userDescriptor.getLastModified();
            }
            valueRow = this.getExecutionFactory().getValueRow(4);
            valueRow.setColumn(1, new SQLVarchar(userName));
            valueRow.setColumn(2, new SQLVarchar(hashingScheme));
            valueRow.setColumn(3, new SQLVarchar(andZeroPassword));
            valueRow.setColumn(4, new SQLTimestamp(lastModified));
        }
        finally {
            if (andZeroPassword != null) {
                Arrays.fill(andZeroPassword, '\0');
            }
        }
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        char[] rawDataAndZeroIt = null;
        SQLChar sqlChar = null;
        UserDescriptor userDescriptor;
        try {
            final String string = execRow.getColumn(1).getString();
            final String string2 = execRow.getColumn(2).getString();
            sqlChar = (SQLVarchar)execRow.getColumn(3);
            rawDataAndZeroIt = sqlChar.getRawDataAndZeroIt();
            userDescriptor = dataDescriptorGenerator.newUserDescriptor(string, string2, rawDataAndZeroIt, execRow.getColumn(4).getTimestamp(new GregorianCalendar()));
        }
        finally {
            if (rawDataAndZeroIt != null) {
                Arrays.fill(rawDataAndZeroIt, '\0');
            }
            if (sqlChar != null) {
                sqlChar.zeroRawData();
            }
        }
        return userDescriptor;
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getIdentifierColumn("USERNAME", false), SystemColumnImpl.getColumn("HASHINGSCHEME", 12, false, 32672), SystemColumnImpl.getColumn("PASSWORD", 12, false, 32672), SystemColumnImpl.getColumn("LASTMODIFIED", 93, false) };
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 } };
        uniqueness = null;
        uuids = new String[] { "9810800c-0134-14a5-40c1-000004f61f90", "9810800c-0134-14a5-a609-000004f61f90", "9810800c-0134-14a5-f1cd-000004f61f90" };
    }
}
