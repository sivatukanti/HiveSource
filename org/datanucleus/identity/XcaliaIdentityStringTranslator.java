// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ExecutionContext;

public class XcaliaIdentityStringTranslator implements IdentityStringTranslator
{
    @Override
    public Object getIdentity(final ExecutionContext ec, final String stringId) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        Object id = null;
        final int idStringPos = stringId.indexOf(58);
        if (idStringPos > 0) {
            final String definer = stringId.substring(0, idStringPos);
            final String idKey = stringId.substring(idStringPos + 1);
            AbstractClassMetaData acmd = null;
            try {
                clr.classForName(definer);
                acmd = ec.getMetaDataManager().getMetaDataForClass(definer, clr);
            }
            catch (ClassNotResolvedException cnre) {
                acmd = ec.getMetaDataManager().getMetaDataForDiscriminator(definer);
            }
            if (acmd != null) {
                if (acmd.getIdentityType() == IdentityType.DATASTORE) {
                    try {
                        final Long keyLong = Long.valueOf(idKey);
                        id = OIDFactory.getInstance(ec.getNucleusContext(), acmd.getFullClassName(), keyLong);
                    }
                    catch (NumberFormatException nfe) {
                        id = OIDFactory.getInstance(ec.getNucleusContext(), acmd.getFullClassName(), idKey);
                    }
                }
                else if (acmd.getIdentityType() == IdentityType.APPLICATION) {
                    id = ec.getApiAdapter().getNewApplicationIdentityObjectId(clr, acmd, idKey);
                }
            }
        }
        return id;
    }
}
