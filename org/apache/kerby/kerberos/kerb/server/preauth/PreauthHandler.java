// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.server.preauth.pkinit.PkinitPreauth;
import org.apache.kerby.kerberos.kerb.server.preauth.token.TokenPreauth;
import org.apache.kerby.kerberos.kerb.server.preauth.builtin.TgtPreauth;
import org.apache.kerby.kerberos.kerb.server.preauth.builtin.EncTsPreauth;
import java.util.ArrayList;
import java.util.List;

public class PreauthHandler
{
    private List<KdcPreauth> preauths;
    
    public void init() {
        this.loadPreauthPlugins();
    }
    
    private void loadPreauthPlugins() {
        this.preauths = new ArrayList<KdcPreauth>();
        KdcPreauth preauth = new EncTsPreauth();
        this.preauths.add(preauth);
        preauth = new TgtPreauth();
        this.preauths.add(preauth);
        preauth = new TokenPreauth();
        this.preauths.add(preauth);
        preauth = new PkinitPreauth();
        this.preauths.add(preauth);
    }
    
    public void initWith(final KdcContext context) {
        for (final KdcPreauth preauth : this.preauths) {
            preauth.initWith(context);
        }
    }
    
    public PreauthContext preparePreauthContext(final KdcRequest kdcRequest) {
        final PreauthContext preauthContext = new PreauthContext();
        final KdcContext kdcContext = kdcRequest.getKdcContext();
        this.initWith(kdcContext);
        preauthContext.setPreauthRequired(kdcContext.getConfig().isPreauthRequired());
        for (final KdcPreauth preauth : this.preauths) {
            final PreauthHandle handle = new PreauthHandle(preauth);
            handle.initRequestContext(kdcRequest);
            preauthContext.getHandles().add(handle);
        }
        return preauthContext;
    }
    
    public void provideEdata(final KdcRequest kdcRequest, final PaData outPaData) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PreauthHandle handle : preauthContext.getHandles()) {
            handle.provideEdata(kdcRequest, outPaData);
        }
    }
    
    public void verify(final KdcRequest kdcRequest, final PaData paData) throws KrbException {
        for (final PaDataEntry paEntry : paData.getElements()) {
            final PreauthHandle handle = this.findHandle(kdcRequest, paEntry.getPaDataType());
            if (handle != null) {
                handle.verify(kdcRequest, paEntry);
            }
        }
    }
    
    public void providePaData(final KdcRequest kdcRequest, final PaData paData) {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PreauthHandle handle : preauthContext.getHandles()) {
            handle.providePaData(kdcRequest, paData);
        }
    }
    
    private PreauthHandle findHandle(final KdcRequest kdcRequest, final PaDataType paType) {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PreauthHandle handle : preauthContext.getHandles()) {
            for (final PaDataType pt : handle.preauth.getPaTypes()) {
                if (pt == paType) {
                    return handle;
                }
            }
        }
        return null;
    }
    
    public void destroy() {
        for (final KdcPreauth preauth : this.preauths) {
            preauth.destroy();
        }
    }
    
    public static boolean isToken(final PaData paData) {
        if (paData != null) {
            for (final PaDataEntry paEntry : paData.getElements()) {
                if (paEntry.getPaDataType() == PaDataType.TOKEN_REQUEST) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isPkinit(final PaData paData) {
        if (paData != null) {
            for (final PaDataEntry paEntry : paData.getElements()) {
                if (paEntry.getPaDataType() == PaDataType.PK_AS_REQ) {
                    return true;
                }
            }
        }
        return false;
    }
}
