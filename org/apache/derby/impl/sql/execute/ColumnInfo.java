// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectInput;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.io.Formatable;

public class ColumnInfo implements Formatable
{
    public int action;
    public String name;
    public DataTypeDescriptor dataType;
    public DefaultInfo defaultInfo;
    public ProviderInfo[] providers;
    public DataValueDescriptor defaultValue;
    public UUID newDefaultUUID;
    public UUID oldDefaultUUID;
    public long autoincStart;
    public long autoincInc;
    public long autoinc_create_or_modify_Start_Increment;
    public static final int CREATE = 0;
    public static final int DROP = 1;
    public static final int MODIFY_COLUMN_TYPE = 2;
    public static final int MODIFY_COLUMN_CONSTRAINT = 3;
    public static final int MODIFY_COLUMN_CONSTRAINT_NOT_NULL = 4;
    public static final int MODIFY_COLUMN_DEFAULT_RESTART = 5;
    public static final int MODIFY_COLUMN_DEFAULT_INCREMENT = 6;
    public static final int MODIFY_COLUMN_DEFAULT_VALUE = 7;
    
    public ColumnInfo() {
        this.autoinc_create_or_modify_Start_Increment = -1L;
    }
    
    public ColumnInfo(final String name, final DataTypeDescriptor dataType, final DataValueDescriptor defaultValue, final DefaultInfo defaultInfo, final ProviderInfo[] providers, final UUID newDefaultUUID, final UUID oldDefaultUUID, final int action, final long autoincStart, final long autoincInc, final long autoinc_create_or_modify_Start_Increment) {
        this.autoinc_create_or_modify_Start_Increment = -1L;
        this.name = name;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.defaultInfo = defaultInfo;
        this.providers = providers;
        this.newDefaultUUID = newDefaultUUID;
        this.oldDefaultUUID = oldDefaultUUID;
        this.action = action;
        this.autoincStart = autoincStart;
        this.autoincInc = autoincInc;
        this.autoinc_create_or_modify_Start_Increment = autoinc_create_or_modify_Start_Increment;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final FormatableHashtable formatableHashtable = (FormatableHashtable)objectInput.readObject();
        this.name = formatableHashtable.get("name");
        this.dataType = (DataTypeDescriptor)formatableHashtable.get("dataType");
        this.defaultValue = (DataValueDescriptor)formatableHashtable.get("defaultValue");
        this.defaultInfo = (DefaultInfo)formatableHashtable.get("defaultInfo");
        this.newDefaultUUID = (UUID)formatableHashtable.get("newDefaultUUID");
        this.oldDefaultUUID = (UUID)formatableHashtable.get("oldDefaultUUID");
        this.action = formatableHashtable.getInt("action");
        if (formatableHashtable.get("autoincStart") != null) {
            this.autoincStart = formatableHashtable.getLong("autoincStart");
            this.autoincInc = formatableHashtable.getLong("autoincInc");
        }
        else {
            final long n = 0L;
            this.autoincStart = n;
            this.autoincInc = n;
        }
        final FormatableArrayHolder formatableArrayHolder = (FormatableArrayHolder)formatableHashtable.get("providers");
        if (formatableArrayHolder != null) {
            this.providers = (ProviderInfo[])formatableArrayHolder.getArray(ProviderInfo.class);
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.put("name", this.name);
        formatableHashtable.put("dataType", this.dataType);
        formatableHashtable.put("defaultValue", this.defaultValue);
        formatableHashtable.put("defaultInfo", this.defaultInfo);
        formatableHashtable.put("newDefaultUUID", this.newDefaultUUID);
        formatableHashtable.put("oldDefaultUUID", this.oldDefaultUUID);
        formatableHashtable.putInt("action", this.action);
        if (this.autoincInc != 0L) {
            formatableHashtable.putLong("autoincStart", this.autoincStart);
            formatableHashtable.putLong("autoincInc", this.autoincInc);
        }
        if (this.providers != null) {
            formatableHashtable.put("providers", new FormatableArrayHolder(this.providers));
        }
        objectOutput.writeObject(formatableHashtable);
    }
    
    public int getTypeFormatId() {
        return 358;
    }
    
    public String toString() {
        return "";
    }
}
