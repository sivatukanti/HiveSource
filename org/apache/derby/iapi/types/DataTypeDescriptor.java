// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import java.io.ObjectInput;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.catalog.types.BaseTypeIdImpl;
import org.apache.derby.catalog.types.RowMultiSetImpl;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.catalog.types.TypeDescriptorImpl;
import org.apache.derby.iapi.services.io.Formatable;

public final class DataTypeDescriptor implements Formatable
{
    public static final DataTypeDescriptor INTEGER;
    public static final DataTypeDescriptor INTEGER_NOT_NULL;
    public static final DataTypeDescriptor SMALLINT;
    public static final DataTypeDescriptor SMALLINT_NOT_NULL;
    private TypeDescriptorImpl typeDescriptor;
    private TypeId typeId;
    private int collationDerivation;
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final int n) {
        return getBuiltInDataTypeDescriptor(n, true);
    }
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final int n, final int n2) {
        return getBuiltInDataTypeDescriptor(n, true, n2);
    }
    
    public static DataTypeDescriptor getType(final TypeDescriptor typeDescriptor) {
        final DataTypeDescriptor dataTypeDescriptor = new DataTypeDescriptor((TypeDescriptorImpl)typeDescriptor, TypeId.getTypeId(typeDescriptor));
        dataTypeDescriptor.collationDerivation = 1;
        return dataTypeDescriptor;
    }
    
    public static TypeDescriptor getCatalogType(final int n, final int n2) {
        return getBuiltInDataTypeDescriptor(n, n2).getCatalogType();
    }
    
    public static TypeDescriptor getCatalogType(final int n) {
        return getBuiltInDataTypeDescriptor(n).getCatalogType();
    }
    
    public static TypeDescriptor getCatalogType(final TypeDescriptor typeDescriptor, final int n) {
        if (typeDescriptor.isRowMultiSet()) {
            return getRowMultiSetCollation(typeDescriptor, n);
        }
        if (typeDescriptor.getCollationType() == n) {
            return typeDescriptor;
        }
        return getType(typeDescriptor).getCollatedType(n, 1).getCatalogType();
    }
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final int n, final boolean b) {
        switch (n) {
            case 4: {
                return b ? DataTypeDescriptor.INTEGER : DataTypeDescriptor.INTEGER_NOT_NULL;
            }
            case 5: {
                return b ? DataTypeDescriptor.SMALLINT : DataTypeDescriptor.SMALLINT_NOT_NULL;
            }
            default: {
                final TypeId builtInTypeId = TypeId.getBuiltInTypeId(n);
                if (builtInTypeId == null) {
                    return null;
                }
                return new DataTypeDescriptor(builtInTypeId, b);
            }
        }
    }
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final int n, final boolean b, final int n2) {
        final TypeId builtInTypeId = TypeId.getBuiltInTypeId(n);
        if (builtInTypeId == null) {
            return null;
        }
        return new DataTypeDescriptor(builtInTypeId, b, n2);
    }
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final String s) {
        return new DataTypeDescriptor(TypeId.getBuiltInTypeId(s), true);
    }
    
    public static DataTypeDescriptor getBuiltInDataTypeDescriptor(final String s, final int n) {
        return new DataTypeDescriptor(TypeId.getBuiltInTypeId(s), true, n);
    }
    
    public static DataTypeDescriptor getSQLDataTypeDescriptor(final String s) throws StandardException {
        return getSQLDataTypeDescriptor(s, true);
    }
    
    public static DataTypeDescriptor getSQLDataTypeDescriptor(final String s, final boolean b) throws StandardException {
        final TypeId sqlTypeForJavaType = TypeId.getSQLTypeForJavaType(s);
        if (sqlTypeForJavaType == null) {
            return null;
        }
        return new DataTypeDescriptor(sqlTypeForJavaType, b);
    }
    
    public static DataTypeDescriptor getSQLDataTypeDescriptor(final String s, final int n, final int n2, final boolean b, final int n3) throws StandardException {
        final TypeId sqlTypeForJavaType = TypeId.getSQLTypeForJavaType(s);
        if (sqlTypeForJavaType == null) {
            return null;
        }
        return new DataTypeDescriptor(sqlTypeForJavaType, n, n2, b, n3);
    }
    
    public static TypeDescriptor getRowMultiSet(final String[] array, final TypeDescriptor[] array2) {
        return new TypeDescriptorImpl(new RowMultiSetImpl(array, array2), true, -1);
    }
    
    public DataTypeDescriptor() {
        this.collationDerivation = 1;
    }
    
    public DataTypeDescriptor(final TypeId typeId, final int n, final int n2, final boolean b, final int n3) {
        this.collationDerivation = 1;
        this.typeId = typeId;
        this.typeDescriptor = new TypeDescriptorImpl(typeId.getBaseTypeId(), n, n2, b, n3);
    }
    
    public DataTypeDescriptor(final TypeId typeId, final int n, final int n2, final boolean b, final int n3, final int n4, final int collationDerivation) {
        this.collationDerivation = 1;
        this.typeId = typeId;
        this.typeDescriptor = new TypeDescriptorImpl(typeId.getBaseTypeId(), n, n2, b, n3, n4);
        this.collationDerivation = collationDerivation;
    }
    
    public DataTypeDescriptor(final TypeId typeId, final boolean b, final int n) {
        this.collationDerivation = 1;
        this.typeId = typeId;
        this.typeDescriptor = new TypeDescriptorImpl(typeId.getBaseTypeId(), b, n);
    }
    
    public DataTypeDescriptor(final TypeId typeId, final boolean b) {
        this.collationDerivation = 1;
        this.typeId = typeId;
        this.typeDescriptor = new TypeDescriptorImpl(typeId.getBaseTypeId(), typeId.getMaximumPrecision(), typeId.getMaximumScale(), b, typeId.getMaximumMaximumWidth());
    }
    
    private DataTypeDescriptor(final DataTypeDescriptor dataTypeDescriptor, final boolean b) {
        this.collationDerivation = 1;
        this.typeId = dataTypeDescriptor.typeId;
        this.typeDescriptor = new TypeDescriptorImpl(dataTypeDescriptor.typeDescriptor, dataTypeDescriptor.getPrecision(), dataTypeDescriptor.getScale(), b, dataTypeDescriptor.getMaximumWidth(), dataTypeDescriptor.getCollationType());
        this.collationDerivation = dataTypeDescriptor.getCollationDerivation();
    }
    
    private DataTypeDescriptor(final DataTypeDescriptor dataTypeDescriptor, final int n, final int collationDerivation) {
        this.collationDerivation = 1;
        this.typeId = dataTypeDescriptor.typeId;
        this.typeDescriptor = new TypeDescriptorImpl(dataTypeDescriptor.typeDescriptor, dataTypeDescriptor.getPrecision(), dataTypeDescriptor.getScale(), dataTypeDescriptor.isNullable(), dataTypeDescriptor.getMaximumWidth(), n);
        this.collationDerivation = collationDerivation;
    }
    
    public DataTypeDescriptor(final DataTypeDescriptor dataTypeDescriptor, final int n, final int n2, final boolean b, final int n3) {
        this.collationDerivation = 1;
        this.typeId = dataTypeDescriptor.typeId;
        this.typeDescriptor = new TypeDescriptorImpl(dataTypeDescriptor.typeDescriptor, n, n2, b, n3, dataTypeDescriptor.getCollationType());
        this.collationDerivation = dataTypeDescriptor.getCollationDerivation();
    }
    
    public DataTypeDescriptor(final DataTypeDescriptor dataTypeDescriptor, final boolean b, final int n) {
        this.collationDerivation = 1;
        this.typeId = dataTypeDescriptor.typeId;
        this.typeDescriptor = new TypeDescriptorImpl(dataTypeDescriptor.typeDescriptor, dataTypeDescriptor.getPrecision(), dataTypeDescriptor.getScale(), b, n, dataTypeDescriptor.getCollationType());
        this.collationDerivation = dataTypeDescriptor.getCollationDerivation();
    }
    
    private DataTypeDescriptor(final TypeDescriptorImpl typeDescriptor, final TypeId typeId) {
        this.collationDerivation = 1;
        this.typeDescriptor = typeDescriptor;
        this.typeId = typeId;
    }
    
    public DataValueDescriptor normalize(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        if (dataValueDescriptor.isNull()) {
            if (!this.isNullable()) {
                throw StandardException.newException("23502", "");
            }
            dataValueDescriptor2.setToNull();
        }
        else {
            final int jdbcTypeId = this.getJDBCTypeId();
            dataValueDescriptor2.normalize(this, dataValueDescriptor);
            if ((jdbcTypeId == -1 || jdbcTypeId == -4) && dataValueDescriptor.getClass() == dataValueDescriptor2.getClass()) {
                return dataValueDescriptor;
            }
        }
        return dataValueDescriptor2;
    }
    
    public DataTypeDescriptor getDominantType(final DataTypeDescriptor dataTypeDescriptor, final ClassFactory classFactory) {
        int n = this.getPrecision();
        int n2 = this.getScale();
        final TypeId typeId = this.getTypeId();
        final TypeId typeId2 = dataTypeDescriptor.getTypeId();
        final boolean b = this.isNullable() || dataTypeDescriptor.isNullable();
        int n3 = (this.getMaximumWidth() > dataTypeDescriptor.getMaximumWidth()) ? this.getMaximumWidth() : dataTypeDescriptor.getMaximumWidth();
        DataTypeDescriptor builtInDataTypeDescriptor;
        if (!typeId.userType() && !typeId2.userType()) {
            DataTypeDescriptor dataTypeDescriptor2;
            TypeId builtInTypeId;
            TypeId typeId3;
            if (typeId.typePrecedence() > typeId2.typePrecedence()) {
                builtInDataTypeDescriptor = this;
                dataTypeDescriptor2 = dataTypeDescriptor;
                builtInTypeId = typeId;
                typeId3 = typeId2;
            }
            else {
                builtInDataTypeDescriptor = dataTypeDescriptor;
                dataTypeDescriptor2 = this;
                builtInTypeId = typeId2;
                typeId3 = typeId;
            }
            if (builtInTypeId.isRealTypeId() && !typeId3.isRealTypeId() && typeId3.isNumericTypeId()) {
                builtInDataTypeDescriptor = getBuiltInDataTypeDescriptor(8);
                builtInTypeId = TypeId.getBuiltInTypeId(8);
            }
            if (builtInTypeId.isDecimalTypeId() && !typeId3.isStringTypeId()) {
                n = builtInTypeId.getPrecision(this, dataTypeDescriptor);
                if (n > 31) {
                    n = 31;
                }
                n2 = builtInTypeId.getScale(this, dataTypeDescriptor);
                n3 = ((n2 > 0) ? (n + 3) : (n + 1));
            }
            else if (typeId.typePrecedence() != typeId2.typePrecedence()) {
                n = builtInDataTypeDescriptor.getPrecision();
                n2 = builtInDataTypeDescriptor.getScale();
                if (typeId3.isStringTypeId() && builtInTypeId.isBitTypeId() && !builtInTypeId.isLongConcatableTypeId()) {
                    if (typeId3.isLongConcatableTypeId()) {
                        if (n3 > 134217727) {
                            n3 = Integer.MAX_VALUE;
                        }
                        else {
                            n3 *= 16;
                        }
                    }
                    else {
                        final int maximumWidth = dataTypeDescriptor2.getMaximumWidth();
                        int n4;
                        if (maximumWidth > 134217727) {
                            n4 = Integer.MAX_VALUE;
                        }
                        else {
                            n4 = 16 * maximumWidth;
                        }
                        n3 = ((n3 >= n4) ? n3 : n4);
                    }
                }
                if (typeId3.isStringTypeId() && !typeId3.isLongConcatableTypeId() && builtInTypeId.isDecimalTypeId()) {
                    final int maximumWidth2 = dataTypeDescriptor2.getMaximumWidth();
                    int n5;
                    if (maximumWidth2 > 1073741822) {
                        n5 = 2147483644;
                    }
                    else {
                        n5 = maximumWidth2 * 2;
                    }
                    if (n < n5) {
                        n = n5;
                    }
                    if (n2 < maximumWidth2) {
                        n2 = maximumWidth2;
                    }
                    n3 = n + 3;
                }
            }
        }
        else {
            if (classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), typeId2.getCorrespondingJavaTypeName())) {
                builtInDataTypeDescriptor = dataTypeDescriptor;
            }
            else {
                builtInDataTypeDescriptor = this;
            }
            n = builtInDataTypeDescriptor.getPrecision();
            n2 = builtInDataTypeDescriptor.getScale();
        }
        DataTypeDescriptor dataTypeDescriptor3 = new DataTypeDescriptor(builtInDataTypeDescriptor, n, n2, b, n3);
        if (dataTypeDescriptor3.getTypeId().isStringTypeId()) {
            if (this.getCollationDerivation() != dataTypeDescriptor.getCollationDerivation()) {
                if (this.getCollationDerivation() == 0) {
                    dataTypeDescriptor3 = dataTypeDescriptor3.getCollatedType(dataTypeDescriptor.getCollationType(), dataTypeDescriptor.getCollationDerivation());
                }
                else if (dataTypeDescriptor.getCollationDerivation() == 0) {
                    dataTypeDescriptor3 = dataTypeDescriptor3.getCollatedType(this.getCollationType(), this.getCollationDerivation());
                }
                else {
                    dataTypeDescriptor3 = dataTypeDescriptor3.getCollatedType(0, 0);
                }
            }
            else if (this.getCollationType() != dataTypeDescriptor.getCollationType()) {
                dataTypeDescriptor3 = dataTypeDescriptor3.getCollatedType(0, 0);
            }
            else {
                dataTypeDescriptor3 = dataTypeDescriptor3.getCollatedType(this.getCollationType(), this.getCollationDerivation());
            }
        }
        return dataTypeDescriptor3;
    }
    
    public boolean isExactTypeAndLengthMatch(final DataTypeDescriptor dataTypeDescriptor) {
        return this.getMaximumWidth() == dataTypeDescriptor.getMaximumWidth() && this.getScale() == dataTypeDescriptor.getScale() && this.getPrecision() == dataTypeDescriptor.getPrecision() && this.getTypeId().equals(dataTypeDescriptor.getTypeId());
    }
    
    public int getMaximumWidth() {
        return this.typeDescriptor.getMaximumWidth();
    }
    
    public TypeId getTypeId() {
        return this.typeId;
    }
    
    public DataValueDescriptor getNull() throws StandardException {
        final DataValueDescriptor null = this.typeId.getNull();
        if (this.typeDescriptor.getCollationType() == 0) {
            return null;
        }
        if (null instanceof StringDataValue) {
            try {
                return ((StringDataValue)null).getValue(ConnectionUtil.getCurrentLCC().getDataValueFactory().getCharacterCollator(this.typeDescriptor.getCollationType()));
            }
            catch (SQLException ex) {
                throw StandardException.plainWrapException(ex);
            }
        }
        return null;
    }
    
    public String getTypeName() {
        return this.typeDescriptor.getTypeName();
    }
    
    public int getJDBCTypeId() {
        return this.typeDescriptor.getJDBCTypeId();
    }
    
    public int getPrecision() {
        return this.typeDescriptor.getPrecision();
    }
    
    public int getScale() {
        return this.typeDescriptor.getScale();
    }
    
    public int getCollationType() {
        return this.typeDescriptor.getCollationType();
    }
    
    public static int getCollationType(final String s) {
        if (s.equalsIgnoreCase("UCS_BASIC")) {
            return 0;
        }
        if (s.equalsIgnoreCase("TERRITORY_BASED")) {
            return 1;
        }
        if (s.equalsIgnoreCase("TERRITORY_BASED:PRIMARY")) {
            return 2;
        }
        if (s.equalsIgnoreCase("TERRITORY_BASED:SECONDARY")) {
            return 3;
        }
        if (s.equalsIgnoreCase("TERRITORY_BASED:TERTIARY")) {
            return 4;
        }
        if (s.equalsIgnoreCase("TERRITORY_BASED:IDENTICAL")) {
            return 5;
        }
        return -1;
    }
    
    public String getCollationName() {
        if (this.getCollationDerivation() == 0) {
            return "NONE";
        }
        return getCollationName(this.getCollationType());
    }
    
    public static String getCollationName(final int n) {
        switch (n) {
            case 1: {
                return "TERRITORY_BASED";
            }
            case 2: {
                return "TERRITORY_BASED:PRIMARY";
            }
            case 3: {
                return "TERRITORY_BASED:SECONDARY";
            }
            case 4: {
                return "TERRITORY_BASED:TERTIARY";
            }
            case 5: {
                return "TERRITORY_BASED:IDENTICAL";
            }
            default: {
                return "UCS_BASIC";
            }
        }
    }
    
    public int getCollationDerivation() {
        return this.collationDerivation;
    }
    
    public boolean isNullable() {
        return this.typeDescriptor.isNullable();
    }
    
    public DataTypeDescriptor getNullabilityType(final boolean b) {
        if (this.isNullable() == b) {
            return this;
        }
        return new DataTypeDescriptor(this, b);
    }
    
    public DataTypeDescriptor getCollatedType(final int n, final int n2) {
        if (!this.typeDescriptor.isStringType()) {
            return this;
        }
        if (this.getCollationType() == n && this.getCollationDerivation() == n2) {
            return this;
        }
        return new DataTypeDescriptor(this, n, n2);
    }
    
    private static TypeDescriptor getRowMultiSetCollation(final TypeDescriptor typeDescriptor, final int n) {
        final TypeDescriptor[] rowTypes = typeDescriptor.getRowTypes();
        TypeDescriptor[] array = null;
        for (int i = 0; i < rowTypes.length; ++i) {
            final TypeDescriptor catalogType = getCatalogType(rowTypes[i], n);
            if (catalogType != rowTypes[i]) {
                if (array == null) {
                    array = new TypeDescriptor[rowTypes.length];
                    System.arraycopy(rowTypes, 0, array, 0, rowTypes.length);
                }
                array[i] = catalogType;
            }
        }
        if (array == null) {
            return typeDescriptor;
        }
        return getRowMultiSet(typeDescriptor.getRowColumnNames(), array);
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof DataTypeDescriptor)) {
            return false;
        }
        final DataTypeDescriptor dataTypeDescriptor = (DataTypeDescriptor)o;
        return this.typeDescriptor.equals(dataTypeDescriptor.typeDescriptor) && this.collationDerivation == dataTypeDescriptor.collationDerivation;
    }
    
    public boolean comparable(final DataTypeDescriptor dataTypeDescriptor, final boolean b, final ClassFactory classFactory) {
        final TypeId typeId = dataTypeDescriptor.getTypeId();
        final int jdbcTypeId = typeId.getJDBCTypeId();
        if (typeId.isLongConcatableTypeId() || this.typeId.isLongConcatableTypeId()) {
            return false;
        }
        if (this.typeId.isRefTypeId() || typeId.isRefTypeId()) {
            return false;
        }
        if (!this.typeId.isUserDefinedTypeId() && typeId.isUserDefinedTypeId()) {
            return dataTypeDescriptor.comparable(this, b, classFactory);
        }
        if (this.typeId.isNumericTypeId()) {
            return typeId.isNumericTypeId();
        }
        if (this.typeId.isStringTypeId()) {
            return typeId.isDateTimeTimeStampTypeID() || typeId.isBooleanTypeId() || (typeId.isStringTypeId() && this.typeId.isStringTypeId() && this.compareCollationInfo(dataTypeDescriptor));
        }
        if (this.typeId.isBitTypeId()) {
            return typeId.isBitTypeId();
        }
        if (this.typeId.isBooleanTypeId()) {
            return typeId.getSQLTypeName().equals(this.typeId.getSQLTypeName()) || typeId.isStringTypeId();
        }
        if (this.typeId.getJDBCTypeId() == 91) {
            return jdbcTypeId == 91 || typeId.isStringTypeId();
        }
        if (this.typeId.getJDBCTypeId() == 92) {
            return jdbcTypeId == 92 || typeId.isStringTypeId();
        }
        if (this.typeId.getJDBCTypeId() == 93) {
            return jdbcTypeId == 93 || typeId.isStringTypeId();
        }
        return (this.typeId.isUserDefinedTypeId() || this.typeId.getJDBCTypeId() == 1111) && false;
    }
    
    public boolean compareCollationInfo(final DataTypeDescriptor dataTypeDescriptor) {
        return (this.getCollationDerivation() != dataTypeDescriptor.getCollationDerivation() || this.getCollationDerivation() != 0) && (this.getCollationDerivation() == dataTypeDescriptor.getCollationDerivation() && this.getCollationType() == dataTypeDescriptor.getCollationType());
    }
    
    public String getSQLstring() {
        return this.typeId.toParsableString(this);
    }
    
    public TypeDescriptor getCatalogType() {
        return this.typeDescriptor;
    }
    
    public double estimatedMemoryUsage() {
        switch (this.typeId.getTypeFormatId()) {
            case 232: {
                return 10000.0;
            }
            case 27: {
                return (float)this.getMaximumWidth() / 8.0 + 0.5;
            }
            case 4: {
                return 4.0;
            }
            case 5:
            case 13: {
                return 2.0 * this.getMaximumWidth();
            }
            case 230: {
                return 10000.0;
            }
            case 197: {
                return this.getPrecision() * 0.415 + 1.5;
            }
            case 6: {
                return 8.0;
            }
            case 7: {
                return 4.0;
            }
            case 11: {
                return 8.0;
            }
            case 8: {
                return 4.0;
            }
            case 10: {
                return 2.0;
            }
            case 195: {
                return 1.0;
            }
            case 9: {
                return 12.0;
            }
            case 267: {
                if (this.typeId.userType()) {
                    return 256.0;
                }
                return 12.0;
            }
            case 35:
            case 36:
            case 40: {
                return 12.0;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    public static boolean isJDBCTypeEquivalent(final int n, final int n2) {
        if (n == n2) {
            return true;
        }
        if (isNumericType(n)) {
            return isNumericType(n2) || isCharacterType(n2);
        }
        if (isCharacterType(n)) {
            if (isCharacterType(n2)) {
                return true;
            }
            if (isNumericType(n2)) {
                return true;
            }
            switch (n2) {
                case 91:
                case 92:
                case 93: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        else {
            if (isBinaryType(n)) {
                return isBinaryType(n2);
            }
            if (n == 91 || n == 92) {
                return isCharacterType(n2) || n2 == 93;
            }
            if (n == 93) {
                return isCharacterType(n2) || n2 == 91;
            }
            return n == 2005 && isCharacterType(n2);
        }
    }
    
    public static boolean isNumericType(final int n) {
        switch (n) {
            case -7:
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 16: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isCharacterType(final int n) {
        switch (n) {
            case -1:
            case 1:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isBinaryType(final int n) {
        switch (n) {
            case -4:
            case -3:
            case -2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isAsciiStreamAssignable(final int n) {
        return n == 2005 || isCharacterType(n);
    }
    
    public static boolean isBinaryStreamAssignable(final int n) {
        return n == 2004 || isBinaryType(n);
    }
    
    public static boolean isCharacterStreamAssignable(final int n) {
        return isAsciiStreamAssignable(n);
    }
    
    public String toString() {
        return this.typeDescriptor.toString();
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.typeDescriptor = (TypeDescriptorImpl)objectInput.readObject();
        Label_0077: {
            if (this.typeDescriptor.isUserDefinedType()) {
                try {
                    this.typeId = TypeId.getUserDefinedTypeId(((UserDefinedTypeIdImpl)this.typeDescriptor.getTypeId()).getClassName());
                    break Label_0077;
                }
                catch (StandardException cause) {
                    throw (IOException)new IOException(cause.getMessage()).initCause(cause);
                }
            }
            this.typeId = TypeId.getBuiltInTypeId(this.getTypeName());
        }
        this.collationDerivation = objectInput.readInt();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.typeDescriptor);
        objectOutput.writeInt(this.getCollationDerivation());
    }
    
    public int getTypeFormatId() {
        return 240;
    }
    
    public boolean isUserCreatableType() throws StandardException {
        switch (this.typeId.getJDBCTypeId()) {
            case 2000: {
                return this.getTypeId().getBaseTypeId().isAnsiUDT();
            }
            case 3: {
                return this.getPrecision() <= this.typeId.getMaximumPrecision() && this.getScale() <= this.typeId.getMaximumScale() && this.getMaximumWidth() <= this.typeId.getMaximumMaximumWidth();
            }
            default: {
                return true;
            }
        }
    }
    
    public String getFullSQLTypeName() {
        final StringBuffer sb = new StringBuffer(this.typeId.getSQLTypeName());
        if (this.typeId.isDecimalTypeId() || this.typeId.isNumericTypeId()) {
            sb.append("(");
            sb.append(this.getPrecision());
            sb.append(", ");
            sb.append(this.getScale());
            sb.append(")");
        }
        else if (this.typeId.variableLength()) {
            sb.append("(");
            sb.append(this.getMaximumWidth());
            sb.append(")");
        }
        return sb.toString();
    }
    
    public String getSQLTypeNameWithCollation() {
        String str = this.typeId.getSQLTypeName();
        if (this.typeId.isStringTypeId()) {
            str = str + " (" + this.getCollationName() + ")";
        }
        return str;
    }
    
    static {
        INTEGER = new DataTypeDescriptor(TypeId.INTEGER_ID, true);
        INTEGER_NOT_NULL = DataTypeDescriptor.INTEGER.getNullabilityType(false);
        SMALLINT = new DataTypeDescriptor(TypeId.SMALLINT_ID, true);
        SMALLINT_NOT_NULL = DataTypeDescriptor.SMALLINT.getNullabilityType(false);
    }
}
