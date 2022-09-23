// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.util.IdUtil;
import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectInput;
import org.apache.derby.catalog.TypeDescriptor;

public class RoutineAliasInfo extends MethodAliasInfo
{
    private static final String[] SQL_CONTROL;
    public static final short MODIFIES_SQL_DATA = 0;
    public static final short READS_SQL_DATA = 1;
    public static final short CONTAINS_SQL = 2;
    public static final short NO_SQL = 3;
    public static final short PS_JAVA = 0;
    public static final short PS_DERBY_JDBC_RESULT_SET = 1;
    public static final short PS_DERBY = 2;
    private static final short SQL_ALLOWED_MASK = 15;
    private static final short DETERMINISTIC_MASK = 16;
    private static final short SECURITY_DEFINER_MASK = 32;
    private static final short VARARGS_MASK = 64;
    private int parameterCount;
    private TypeDescriptor[] parameterTypes;
    private String[] parameterNames;
    private int[] parameterModes;
    private int dynamicResultSets;
    private TypeDescriptor returnType;
    private short parameterStyle;
    private short sqlOptions;
    private String specificName;
    private boolean calledOnNullInput;
    private transient char aliasType;
    
    public RoutineAliasInfo() {
    }
    
    public RoutineAliasInfo(final String s, final int n, final String[] array, final TypeDescriptor[] array2, final int[] array3, final int n2, final short n3, final short n4, final boolean b, final boolean b2) {
        this(s, n, array, array2, array3, n2, n3, n4, b, b2, false, true, null);
    }
    
    public RoutineAliasInfo(final String s, final int parameterCount, final String[] parameterNames, final TypeDescriptor[] parameterTypes, final int[] parameterModes, final int dynamicResultSets, final short parameterStyle, final short n, final boolean b, final boolean b2, final boolean b3, final boolean calledOnNullInput, final TypeDescriptor returnType) {
        super(s);
        this.parameterCount = parameterCount;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
        this.parameterModes = parameterModes;
        this.dynamicResultSets = dynamicResultSets;
        this.parameterStyle = parameterStyle;
        this.sqlOptions = (short)(n & 0xF);
        if (b) {
            this.sqlOptions |= 0x10;
        }
        if (b2) {
            this.sqlOptions |= 0x40;
        }
        if (b3) {
            this.sqlOptions |= 0x20;
        }
        this.calledOnNullInput = calledOnNullInput;
        this.returnType = returnType;
    }
    
    public int getParameterCount() {
        return this.parameterCount;
    }
    
    public TypeDescriptor[] getParameterTypes() {
        return this.parameterTypes;
    }
    
    public int[] getParameterModes() {
        return this.parameterModes;
    }
    
    public String[] getParameterNames() {
        return this.parameterNames;
    }
    
    public int getMaxDynamicResultSets() {
        return this.dynamicResultSets;
    }
    
    public short getParameterStyle() {
        return this.parameterStyle;
    }
    
    public short getSQLAllowed() {
        return (short)(this.sqlOptions & 0xF);
    }
    
    public boolean isDeterministic() {
        return (this.sqlOptions & 0x10) != 0x0;
    }
    
    public boolean hasVarargs() {
        return (this.sqlOptions & 0x40) != 0x0;
    }
    
    public boolean hasDefinersRights() {
        return (this.sqlOptions & 0x20) != 0x0;
    }
    
    public boolean calledOnNullInput() {
        return this.calledOnNullInput;
    }
    
    public TypeDescriptor getReturnType() {
        return this.returnType;
    }
    
    public boolean isTableFunction() {
        return this.returnType != null && this.returnType.isRowMultiSet();
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.specificName = (String)objectInput.readObject();
        this.dynamicResultSets = objectInput.readInt();
        this.parameterCount = objectInput.readInt();
        this.parameterStyle = objectInput.readShort();
        this.sqlOptions = objectInput.readShort();
        this.returnType = getStoredType(objectInput.readObject());
        this.calledOnNullInput = objectInput.readBoolean();
        objectInput.readInt();
        if (this.parameterCount != 0) {
            this.parameterNames = new String[this.parameterCount];
            this.parameterTypes = new TypeDescriptor[this.parameterCount];
            ArrayUtil.readArrayItems(objectInput, this.parameterNames);
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                this.parameterTypes[i] = getStoredType(objectInput.readObject());
            }
            this.parameterModes = ArrayUtil.readIntArray(objectInput);
        }
        else {
            this.parameterNames = null;
            this.parameterTypes = null;
            this.parameterModes = null;
        }
    }
    
    public static TypeDescriptor getStoredType(final Object o) {
        if (o instanceof OldRoutineType) {
            return ((OldRoutineType)o).getCatalogType();
        }
        return (TypeDescriptor)o;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.specificName);
        objectOutput.writeInt(this.dynamicResultSets);
        objectOutput.writeInt(this.parameterCount);
        objectOutput.writeShort(this.parameterStyle);
        objectOutput.writeShort(this.sqlOptions);
        objectOutput.writeObject(this.returnType);
        objectOutput.writeBoolean(this.calledOnNullInput);
        objectOutput.writeInt(0);
        if (this.parameterCount != 0) {
            ArrayUtil.writeArrayItems(objectOutput, this.parameterNames);
            ArrayUtil.writeArrayItems(objectOutput, this.parameterTypes);
            ArrayUtil.writeIntArray(objectOutput, this.parameterModes);
        }
    }
    
    public int getTypeFormatId() {
        return 451;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append(this.getMethodName());
        sb.append('(');
        for (int i = 0; i < this.parameterCount; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            if (this.returnType == null) {
                sb.append(parameterMode(this.parameterModes[i]));
                sb.append(' ');
            }
            sb.append(IdUtil.normalToDelimited(this.parameterNames[i]));
            sb.append(' ');
            sb.append(this.parameterTypes[i].getSQLstring());
        }
        if (this.hasVarargs()) {
            sb.append(" ... ");
        }
        sb.append(')');
        if (this.returnType != null) {
            sb.append(" RETURNS " + this.returnType.getSQLstring());
        }
        sb.append(" LANGUAGE JAVA PARAMETER STYLE ");
        switch (this.parameterStyle) {
            case 0: {
                sb.append("JAVA ");
                break;
            }
            case 1: {
                sb.append("DERBY_JDBC_RESULT_SET ");
                break;
            }
            case 2: {
                sb.append("DERBY ");
                break;
            }
        }
        if (this.isDeterministic()) {
            sb.append(" DETERMINISTIC ");
        }
        if (this.hasDefinersRights()) {
            sb.append(" EXTERNAL SECURITY DEFINER ");
        }
        sb.append(RoutineAliasInfo.SQL_CONTROL[this.getSQLAllowed()]);
        if (this.returnType == null && this.dynamicResultSets != 0) {
            sb.append(" DYNAMIC RESULT SETS ");
            sb.append(this.dynamicResultSets);
        }
        if (this.returnType != null) {
            sb.append(this.calledOnNullInput ? " CALLED " : " RETURNS NULL ");
            sb.append("ON NULL INPUT");
        }
        return sb.toString();
    }
    
    public static String parameterMode(final int n) {
        switch (n) {
            case 1: {
                return "IN";
            }
            case 4: {
                return "OUT";
            }
            case 2: {
                return "INOUT";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    public void setCollationTypeForAllStringTypes(final int n) {
        if (this.parameterCount != 0) {
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                this.parameterTypes[i] = DataTypeDescriptor.getCatalogType(this.parameterTypes[i], n);
            }
        }
        if (this.returnType != null) {
            this.returnType = DataTypeDescriptor.getCatalogType(this.returnType, n);
        }
    }
    
    static {
        SQL_CONTROL = new String[] { "MODIFIES SQL DATA", "READS SQL DATA", "CONTAINS SQL", "NO SQL" };
    }
}
