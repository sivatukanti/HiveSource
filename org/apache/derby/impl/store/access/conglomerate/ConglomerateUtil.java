// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.DataOutput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectInput;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Properties;

public final class ConglomerateUtil
{
    public static Properties createRawStorePropertySet(Properties userRawStorePropertySet) {
        userRawStorePropertySet = createUserRawStorePropertySet(userRawStorePropertySet);
        userRawStorePropertySet.put("derby.storage.reusableRecordId", "");
        return userRawStorePropertySet;
    }
    
    public static Properties createUserRawStorePropertySet(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put("derby.storage.pageSize", "");
        properties.put("derby.storage.minimumRecordSize", "");
        properties.put("derby.storage.pageReservedSpace", "");
        properties.put("derby.storage.initialPages", "");
        return properties;
    }
    
    public static int[] createFormatIds(final DataValueDescriptor[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].getTypeFormatId();
        }
        return array2;
    }
    
    public static int[] readFormatIdArray(final int n, final ObjectInput objectInput) throws IOException {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = FormatIdUtil.readFormatIdInteger(objectInput);
        }
        return array;
    }
    
    public static void writeFormatIdArray(final int[] array, final ObjectOutput objectOutput) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            FormatIdUtil.writeFormatIdInteger(objectOutput, array[i]);
        }
    }
    
    public static int[] createCollationIds(final int n, final int[] array) {
        final int[] array2 = new int[n];
        if (array != null) {
            System.arraycopy(array, 0, array2, 0, array.length);
        }
        else {
            for (int i = 0; i < array2.length; ++i) {
                array2[i] = 0;
            }
        }
        return array2;
    }
    
    public static void writeCollationIdArray(final int[] array, final ObjectOutput objectOutput) throws IOException {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != 0) {
                ++n;
            }
        }
        CompressedNumber.writeInt(objectOutput, n);
        for (int j = 0; j < array.length; ++j) {
            if (array[j] != 0) {
                CompressedNumber.writeInt(objectOutput, j);
                CompressedNumber.writeInt(objectOutput, array[j]);
            }
        }
    }
    
    public static boolean readCollationIdArray(final int[] array, final ObjectInput objectInput) throws IOException {
        final int int1 = CompressedNumber.readInt(objectInput);
        for (int i = 0; i < int1; ++i) {
            array[CompressedNumber.readInt(objectInput)] = CompressedNumber.readInt(objectInput);
        }
        return int1 > 0;
    }
    
    public static String debugPage(final Page page, final int n, final boolean b, final DataValueDescriptor[] array) {
        return null;
    }
}
