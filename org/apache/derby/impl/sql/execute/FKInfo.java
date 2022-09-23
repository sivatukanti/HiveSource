// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import java.io.StreamCorruptedException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectOutput;
import java.util.Vector;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.io.Formatable;

public class FKInfo implements Formatable
{
    public static final int FOREIGN_KEY = 1;
    public static final int REFERENCED_KEY = 2;
    public String[] fkConstraintNames;
    public String tableName;
    public int type;
    public UUID refUUID;
    public long refConglomNumber;
    public UUID[] fkUUIDs;
    public long[] fkConglomNumbers;
    public boolean[] fkIsSelfReferencing;
    public int[] colArray;
    public int stmtType;
    public RowLocation rowLocation;
    public int[] raRules;
    
    public FKInfo() {
    }
    
    public FKInfo(final String[] fkConstraintNames, final String tableName, final int stmtType, final int type, final UUID refUUID, final long refConglomNumber, final UUID[] fkUUIDs, final long[] fkConglomNumbers, final boolean[] fkIsSelfReferencing, final int[] colArray, final RowLocation rowLocation, final int[] raRules) {
        this.fkConstraintNames = fkConstraintNames;
        this.tableName = tableName;
        this.stmtType = stmtType;
        this.type = type;
        this.refUUID = refUUID;
        this.refConglomNumber = refConglomNumber;
        this.fkUUIDs = fkUUIDs;
        this.fkConglomNumbers = fkConglomNumbers;
        this.fkIsSelfReferencing = fkIsSelfReferencing;
        this.colArray = colArray;
        this.rowLocation = rowLocation;
        this.raRules = raRules;
    }
    
    public static FKInfo[] chooseRelevantFKInfos(final FKInfo[] array, final int[] array2, final boolean b) {
        if (array == null) {
            return null;
        }
        final Vector vector = new Vector<FKInfo>();
        FKInfo[] array3 = null;
        for (int i = 0; i < array.length; ++i) {
            if (b && array[i].type == 1) {
                vector.addElement(array[i]);
            }
            else {
                for (int length = array[i].colArray.length, j = 0; j < length; ++j) {
                    for (int k = 0; k < array2.length; ++k) {
                        if (array[i].colArray[j] == array2[k]) {
                            vector.addElement(array[i]);
                            j = length;
                            break;
                        }
                    }
                }
            }
        }
        final int size = vector.size();
        if (size > 0) {
            array3 = new FKInfo[size];
            for (int l = 0; l < size; ++l) {
                array3[l] = vector.elementAt(l);
            }
        }
        return array3;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        FormatIdUtil.writeFormatIdInteger(objectOutput, this.rowLocation.getTypeFormatId());
        objectOutput.writeObject(this.tableName);
        objectOutput.writeInt(this.type);
        objectOutput.writeInt(this.stmtType);
        objectOutput.writeObject(this.refUUID);
        objectOutput.writeLong(this.refConglomNumber);
        ArrayUtil.writeArray(objectOutput, this.fkConstraintNames);
        ArrayUtil.writeArray(objectOutput, this.fkUUIDs);
        ArrayUtil.writeLongArray(objectOutput, this.fkConglomNumbers);
        ArrayUtil.writeBooleanArray(objectOutput, this.fkIsSelfReferencing);
        ArrayUtil.writeIntArray(objectOutput, this.colArray);
        ArrayUtil.writeIntArray(objectOutput, this.raRules);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        try {
            this.rowLocation = (RowLocation)Monitor.newInstanceFromIdentifier(FormatIdUtil.readFormatIdInteger(objectInput));
            this.tableName = (String)objectInput.readObject();
            this.type = objectInput.readInt();
            this.stmtType = objectInput.readInt();
            this.refUUID = (UUID)objectInput.readObject();
            this.refConglomNumber = objectInput.readLong();
            ArrayUtil.readArrayItems(objectInput, this.fkConstraintNames = new String[ArrayUtil.readArrayLength(objectInput)]);
            ArrayUtil.readArrayItems(objectInput, this.fkUUIDs = new UUID[ArrayUtil.readArrayLength(objectInput)]);
            this.fkConglomNumbers = ArrayUtil.readLongArray(objectInput);
            this.fkIsSelfReferencing = ArrayUtil.readBooleanArray(objectInput);
            this.colArray = ArrayUtil.readIntArray(objectInput);
            this.raRules = ArrayUtil.readIntArray(objectInput);
        }
        catch (StandardException ex) {
            throw new StreamCorruptedException(ex.toString());
        }
    }
    
    public int getTypeFormatId() {
        return 282;
    }
    
    public String toString() {
        return "";
    }
}
