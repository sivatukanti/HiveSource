// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2Entry;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfoEntry;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.base.MethodData;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.KrbError;

public class KrbErrorUtil
{
    public static List<EncryptionType> getEtypes(final KrbError error) throws IOException {
        final MethodData methodData = new MethodData();
        methodData.decode(error.getEdata());
        for (final PaDataEntry pd : methodData.getElements()) {
            if (pd.getPaDataType() == PaDataType.ETYPE_INFO2) {
                return getEtypes2(pd.getPaDataValue());
            }
            if (pd.getPaDataType() == PaDataType.ETYPE_INFO) {
                return getEtypes(pd.getPaDataValue());
            }
        }
        return Collections.emptyList();
    }
    
    private static List<EncryptionType> getEtypes(final byte[] data) throws IOException {
        final EtypeInfo info = new EtypeInfo();
        info.decode(data);
        final List<EncryptionType> results = new ArrayList<EncryptionType>();
        for (final EtypeInfoEntry entry : info.getElements()) {
            results.add(entry.getEtype());
        }
        return results;
    }
    
    private static List<EncryptionType> getEtypes2(final byte[] data) throws IOException {
        final EtypeInfo2 info2 = new EtypeInfo2();
        info2.decode(data);
        final List<EncryptionType> results = new ArrayList<EncryptionType>();
        for (final EtypeInfo2Entry entry : info2.getElements()) {
            results.add(entry.getEtype());
        }
        return results;
    }
}
