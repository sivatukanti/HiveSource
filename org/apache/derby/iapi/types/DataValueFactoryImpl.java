// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.db.DatabaseContext;
import org.apache.derby.impl.store.access.heap.HeapRowLocation;
import java.text.Collator;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.Clob;
import java.sql.Blob;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.io.RegisteredFormatIds;
import java.util.Properties;
import java.text.RuleBasedCollator;
import java.util.Locale;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import org.apache.derby.iapi.services.monitor.ModuleControl;

abstract class DataValueFactoryImpl implements DataValueFactory, ModuleControl
{
    LocaleFinder localeFinder;
    private Locale databaseLocale;
    private RuleBasedCollator collatorForCharacterTypes;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final DataValueDescriptor dataValueDescriptor = TypeId.decimalImplementation = this.getNullDecimal(null);
        RegisteredFormatIds.TwoByte[200] = ((NumberDataValue)dataValueDescriptor).getClass().getName();
        final DataValueDescriptor newNull = dataValueDescriptor.getNewNull();
        newNull.setValue(0L);
        NumberDataType.ZERO_DECIMAL = newNull;
        this.databaseLocale = Monitor.getMonitor().getLocale(this);
        if (b) {
            final String property = properties.getProperty("collation");
            if (property != null) {
                final int collationType = DataTypeDescriptor.getCollationType(property);
                if (collationType != 0) {
                    if (collationType < 1 || collationType >= 5) {
                        throw StandardException.newException("XBM03.D", property);
                    }
                    this.collatorForCharacterTypes = this.verifyCollatorSupport(collationType - 2);
                }
            }
        }
    }
    
    public void stop() {
    }
    
    public NumberDataValue getDataValue(final int value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLInteger(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Integer value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLInteger(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final char value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLInteger(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final short value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLSmallint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Short value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLSmallint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final byte value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLTinyint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Byte value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLTinyint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final long value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLLongint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Long value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLLongint(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final float value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLReal(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Float value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLReal(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final double value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLDouble(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDataValue(final Double value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return new SQLDouble(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public final NumberDataValue getDecimalDataValue(final Number value) throws StandardException {
        final NumberDataValue nullDecimal = this.getNullDecimal(null);
        nullDecimal.setValue(value);
        return nullDecimal;
    }
    
    public final NumberDataValue getDecimalDataValue(final Number value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return this.getDecimalDataValue(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public final NumberDataValue getDecimalDataValue(final String value, final NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            return this.getDecimalDataValue(value);
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public BooleanDataValue getDataValue(final boolean value, final BooleanDataValue booleanDataValue) throws StandardException {
        if (booleanDataValue == null) {
            return new SQLBoolean(value);
        }
        booleanDataValue.setValue(value);
        return booleanDataValue;
    }
    
    public BooleanDataValue getDataValue(final Boolean value, final BooleanDataValue booleanDataValue) throws StandardException {
        if (booleanDataValue == null) {
            return new SQLBoolean(value);
        }
        booleanDataValue.setValue(value);
        return booleanDataValue;
    }
    
    public BitDataValue getBitDataValue(final byte[] array) throws StandardException {
        return new SQLBit(array);
    }
    
    public BitDataValue getBitDataValue(final byte[] value, final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLBit(value);
        }
        bitDataValue.setValue(value);
        return bitDataValue;
    }
    
    public BitDataValue getVarbitDataValue(final byte[] value, final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLVarbit(value);
        }
        bitDataValue.setValue(value);
        return bitDataValue;
    }
    
    public BitDataValue getLongVarbitDataValue(final byte[] value, final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLLongVarbit(value);
        }
        bitDataValue.setValue(value);
        return bitDataValue;
    }
    
    public BitDataValue getBlobDataValue(final byte[] value, final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLBlob(value);
        }
        bitDataValue.setValue(value);
        return bitDataValue;
    }
    
    public BitDataValue getBlobDataValue(final Blob value, final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLBlob(value);
        }
        bitDataValue.setValue(value);
        return bitDataValue;
    }
    
    public StringDataValue getCharDataValue(final String s) {
        return new SQLChar(s);
    }
    
    public StringDataValue getCharDataValue(final String value, final StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            return new SQLChar(value);
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getCharDataValue(final String value, final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getCharDataValue(value, stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLChar(value, this.getCharacterCollator(n));
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getVarcharDataValue(final String s) {
        return new SQLVarchar(s);
    }
    
    public StringDataValue getVarcharDataValue(final String value, final StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            return new SQLVarchar(value);
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getVarcharDataValue(final String value, final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getVarcharDataValue(value, stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLVarchar(value, this.getCharacterCollator(n));
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getLongvarcharDataValue(final String s) {
        return new SQLLongvarchar(s);
    }
    
    public StringDataValue getLongvarcharDataValue(final String value, final StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            return new SQLLongvarchar(value);
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getLongvarcharDataValue(final String value, final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getLongvarcharDataValue(value, stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLLongvarchar(value, this.getCharacterCollator(n));
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getClobDataValue(final String value, final StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            return new SQLClob(value);
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getClobDataValue(final Clob value, final StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            return new SQLClob(value);
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getClobDataValue(final Clob value, final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getClobDataValue(value, stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLClob(value, this.getCharacterCollator(n));
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public StringDataValue getClobDataValue(final String value, final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getClobDataValue(value, stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLClob(value, this.getCharacterCollator(n));
        }
        stringDataValue.setValue(value);
        return stringDataValue;
    }
    
    public DateTimeDataValue getDataValue(final Date value, final DateTimeDataValue dateTimeDataValue) throws StandardException {
        if (dateTimeDataValue == null) {
            return new SQLDate(value);
        }
        dateTimeDataValue.setValue(value);
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getDataValue(final Time value, final DateTimeDataValue dateTimeDataValue) throws StandardException {
        if (dateTimeDataValue == null) {
            return new SQLTime(value);
        }
        dateTimeDataValue.setValue(value);
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getDataValue(final Timestamp value, final DateTimeDataValue dateTimeDataValue) throws StandardException {
        if (dateTimeDataValue == null) {
            return new SQLTimestamp(value);
        }
        dateTimeDataValue.setValue(value);
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getDate(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return SQLDate.computeDateFunction(dataValueDescriptor, this);
    }
    
    public DateTimeDataValue getTimestamp(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return SQLTimestamp.computeTimestampFunction(dataValueDescriptor, this);
    }
    
    public DateTimeDataValue getTimestamp(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return new SQLTimestamp(dataValueDescriptor, dataValueDescriptor2);
    }
    
    public UserDataValue getDataValue(final Object value, final UserDataValue userDataValue) {
        if (userDataValue == null) {
            return new UserType(value);
        }
        ((UserType)userDataValue).setValue(value);
        return userDataValue;
    }
    
    public RefDataValue getDataValue(final RowLocation value, final RefDataValue refDataValue) {
        if (refDataValue == null) {
            return new SQLRef(value);
        }
        refDataValue.setValue(value);
        return refDataValue;
    }
    
    public NumberDataValue getNullInteger(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLInteger();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public NumberDataValue getNullShort(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLSmallint();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public NumberDataValue getNullLong(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLLongint();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public NumberDataValue getNullByte(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLTinyint();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public NumberDataValue getNullFloat(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLReal();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public NumberDataValue getNullDouble(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLDouble();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
    
    public BooleanDataValue getNullBoolean(final BooleanDataValue booleanDataValue) {
        if (booleanDataValue == null) {
            return new SQLBoolean();
        }
        booleanDataValue.setToNull();
        return booleanDataValue;
    }
    
    public BitDataValue getNullBit(final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return this.getBitDataValue(null);
        }
        bitDataValue.setToNull();
        return bitDataValue;
    }
    
    public BitDataValue getNullVarbit(final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLVarbit();
        }
        bitDataValue.setToNull();
        return bitDataValue;
    }
    
    public BitDataValue getNullLongVarbit(final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLLongVarbit();
        }
        bitDataValue.setToNull();
        return bitDataValue;
    }
    
    public BitDataValue getNullBlob(final BitDataValue bitDataValue) throws StandardException {
        if (bitDataValue == null) {
            return new SQLBlob();
        }
        bitDataValue.setToNull();
        return bitDataValue;
    }
    
    public StringDataValue getNullChar(final StringDataValue stringDataValue) {
        if (stringDataValue == null) {
            return this.getCharDataValue(null);
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullChar(final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getNullChar(stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLChar(this.getCharacterCollator(n));
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullVarchar(final StringDataValue stringDataValue) {
        if (stringDataValue == null) {
            return this.getVarcharDataValue(null);
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullVarchar(final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getNullChar(stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLVarchar(this.getCharacterCollator(n));
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullLongvarchar(final StringDataValue stringDataValue) {
        if (stringDataValue == null) {
            return this.getLongvarcharDataValue(null);
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullLongvarchar(final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getNullChar(stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLLongvarchar(this.getCharacterCollator(n));
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullClob(final StringDataValue stringDataValue) {
        if (stringDataValue == null) {
            return new SQLClob();
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public StringDataValue getNullClob(final StringDataValue stringDataValue, final int n) throws StandardException {
        if (n == 0) {
            return this.getNullChar(stringDataValue);
        }
        if (stringDataValue == null) {
            return new CollatorSQLClob(this.getCharacterCollator(n));
        }
        stringDataValue.setToNull();
        return stringDataValue;
    }
    
    public UserDataValue getNullObject(final UserDataValue userDataValue) {
        if (userDataValue == null) {
            return new UserType(null);
        }
        userDataValue.setToNull();
        return userDataValue;
    }
    
    public RefDataValue getNullRef(final RefDataValue refDataValue) {
        if (refDataValue == null) {
            return new SQLRef();
        }
        refDataValue.setToNull();
        return refDataValue;
    }
    
    public DateTimeDataValue getNullDate(final DateTimeDataValue dateTimeDataValue) {
        if (dateTimeDataValue == null) {
            try {
                return new SQLDate(null);
            }
            catch (StandardException ex) {
                return null;
            }
        }
        dateTimeDataValue.setToNull();
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getNullTime(final DateTimeDataValue dateTimeDataValue) {
        if (dateTimeDataValue == null) {
            try {
                return new SQLTime(null);
            }
            catch (StandardException ex) {
                return null;
            }
        }
        dateTimeDataValue.setToNull();
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getNullTimestamp(final DateTimeDataValue dateTimeDataValue) {
        if (dateTimeDataValue == null) {
            try {
                return new SQLTimestamp(null);
            }
            catch (StandardException ex) {
                return null;
            }
        }
        dateTimeDataValue.setToNull();
        return dateTimeDataValue;
    }
    
    public DateTimeDataValue getDateValue(final String s, final boolean b) throws StandardException {
        return new SQLDate(s, b, this.getLocaleFinder());
    }
    
    public DateTimeDataValue getTimeValue(final String s, final boolean b) throws StandardException {
        return new SQLTime(s, b, this.getLocaleFinder());
    }
    
    public DateTimeDataValue getTimestampValue(final String s, final boolean b) throws StandardException {
        return new SQLTimestamp(s, b, this.getLocaleFinder());
    }
    
    public XMLDataValue getXMLDataValue(final XMLDataValue xmlDataValue) throws StandardException {
        return this.getNullXML(xmlDataValue);
    }
    
    public XMLDataValue getNullXML(final XMLDataValue xmlDataValue) {
        if (xmlDataValue == null) {
            return new XML();
        }
        xmlDataValue.setToNull();
        return xmlDataValue;
    }
    
    public RuleBasedCollator getCharacterCollator(final int n) throws StandardException {
        if (n == 0) {
            return null;
        }
        if (this.collatorForCharacterTypes == null) {
            return this.collatorForCharacterTypes = this.verifyCollatorSupport(n - 2);
        }
        return this.collatorForCharacterTypes;
    }
    
    private RuleBasedCollator verifyCollatorSupport(final int strength) throws StandardException {
        final Locale[] availableLocales = Collator.getAvailableLocales();
        boolean b = false;
        for (int i = 0; i < availableLocales.length; ++i) {
            if (availableLocales[i].equals(this.databaseLocale)) {
                b = true;
                break;
            }
        }
        if (!b) {
            throw StandardException.newException("XBM04.D", this.databaseLocale.toString());
        }
        final RuleBasedCollator ruleBasedCollator = (RuleBasedCollator)Collator.getInstance(this.databaseLocale);
        if (strength != -1) {
            ruleBasedCollator.setStrength(strength);
        }
        return ruleBasedCollator;
    }
    
    public DataValueDescriptor getNull(final int n, final int n2) throws StandardException {
        if (n == 200) {
            return this.getNullDecimal(null);
        }
        final DataValueDescriptor nullDVDWithUCS_BASICcollation = getNullDVDWithUCS_BASICcollation(n);
        if (n2 == 0) {
            return nullDVDWithUCS_BASICcollation;
        }
        if (nullDVDWithUCS_BASICcollation instanceof StringDataValue) {
            return ((StringDataValue)nullDVDWithUCS_BASICcollation).getValue(this.getCharacterCollator(n2));
        }
        return nullDVDWithUCS_BASICcollation;
    }
    
    public static DataValueDescriptor getNullDVDWithUCS_BASICcollation(final int n) {
        switch (n) {
            case 87: {
                return new SQLBit();
            }
            case 77: {
                return new SQLBoolean();
            }
            case 78: {
                return new SQLChar();
            }
            case 298: {
                return new SQLDate();
            }
            case 79: {
                return new SQLDouble();
            }
            case 80: {
                return new SQLInteger();
            }
            case 84: {
                return new SQLLongint();
            }
            case 81: {
                return new SQLReal();
            }
            case 82: {
                return new SQLRef();
            }
            case 83: {
                return new SQLSmallint();
            }
            case 299: {
                return new SQLTime();
            }
            case 31: {
                return new SQLTimestamp();
            }
            case 199: {
                return new SQLTinyint();
            }
            case 85: {
                return new SQLVarchar();
            }
            case 235: {
                return new SQLLongvarchar();
            }
            case 88: {
                return new SQLVarbit();
            }
            case 234: {
                return new SQLLongVarbit();
            }
            case 266: {
                return new UserType();
            }
            case 443: {
                return new SQLBlob();
            }
            case 447: {
                return new SQLClob();
            }
            case 458: {
                return new XML();
            }
            case 90: {
                return new HeapRowLocation();
            }
            default: {
                return null;
            }
        }
    }
    
    private LocaleFinder getLocaleFinder() {
        if (this.localeFinder == null) {
            final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
            if (databaseContext != null) {
                this.localeFinder = databaseContext.getDatabase();
            }
        }
        return this.localeFinder;
    }
}
