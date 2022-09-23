// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;

public class AuthorizationData extends KrbSequenceOfType<AuthorizationDataEntry>
{
    public AuthorizationData clone() {
        final AuthorizationData result = new AuthorizationData();
        for (final AuthorizationDataEntry entry : super.getElements()) {
            result.add(entry.clone());
        }
        return result;
    }
}
