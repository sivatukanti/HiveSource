// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.preauth.token.TokenPreauth;
import org.apache.kerby.kerberos.kerb.client.preauth.pkinit.PkinitPreauth;
import org.apache.kerby.kerberos.kerb.client.preauth.builtin.TgtPreauth;
import org.apache.kerby.kerberos.kerb.client.preauth.builtin.EncTsPreauth;
import java.util.ArrayList;
import java.util.List;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class PreauthHandler
{
    private KrbContext krbContext;
    private List<KrbPreauth> preauths;
    
    public void init(final KrbContext krbContext) {
        this.loadPreauthPlugins(this.krbContext = krbContext);
    }
    
    private void loadPreauthPlugins(final KrbContext context) {
        this.preauths = new ArrayList<KrbPreauth>();
        KrbPreauth preauth = new EncTsPreauth();
        preauth.init(context);
        this.preauths.add(preauth);
        preauth = new TgtPreauth();
        preauth.init(context);
        this.preauths.add(preauth);
        preauth = new PkinitPreauth();
        preauth.init(context);
        this.preauths.add(preauth);
        preauth = new TokenPreauth();
        preauth.init(context);
        this.preauths.add(preauth);
    }
    
    public PreauthContext preparePreauthContext(final KdcRequest kdcRequest) {
        final PreauthContext preauthContext = new PreauthContext();
        preauthContext.setPreauthRequired(this.krbContext.getConfig().isPreauthRequired());
        for (final KrbPreauth preauth : this.preauths) {
            final PreauthHandle handle = new PreauthHandle(preauth);
            handle.initRequestContext(kdcRequest);
            preauthContext.getHandles().add(handle);
        }
        return preauthContext;
    }
    
    public void preauth(final KdcRequest kdcRequest) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        if (!preauthContext.isPreauthRequired()) {
            return;
        }
        this.setPreauthOptions(kdcRequest, kdcRequest.getPreauthOptions());
        if (!preauthContext.hasInputPaData()) {
            this.tryFirst(kdcRequest, preauthContext.getOutputPaData());
            return;
        }
        this.prepareUserResponses(kdcRequest, preauthContext.getInputPaData());
        preauthContext.getUserResponser().respondQuestions();
        if (!kdcRequest.isRetrying()) {
            this.process(kdcRequest, preauthContext.getInputPaData(), preauthContext.getOutputPaData());
        }
        else {
            this.tryAgain(kdcRequest, preauthContext.getInputPaData(), preauthContext.getOutputPaData());
        }
    }
    
    public void prepareUserResponses(final KdcRequest kdcRequest, final PaData inPadata) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PaDataEntry pae : inPadata.getElements()) {
            if (!preauthContext.isPaTypeAllowed(pae.getPaDataType())) {
                continue;
            }
            final PreauthHandle handle = this.findHandle(kdcRequest, pae.getPaDataType());
            if (handle == null) {
                continue;
            }
            handle.prepareQuestions(kdcRequest);
        }
    }
    
    public void setPreauthOptions(final KdcRequest kdcRequest, final KOptions preauthOptions) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PreauthHandle handle : preauthContext.getHandles()) {
            handle.setPreauthOptions(kdcRequest, preauthOptions);
        }
    }
    
    public void tryFirst(final KdcRequest kdcRequest, final PaData outPadata) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        final PreauthHandle handle = this.findHandle(kdcRequest, preauthContext.getAllowedPaType());
        handle.tryFirst(kdcRequest, outPadata);
    }
    
    public void process(final KdcRequest kdcRequest, final PaData inPadata, final PaData outPadata) throws KrbException {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (int real = 0; real <= 1; ++real) {
            for (final PaDataEntry pae : inPadata.getElements()) {
                if (real > 0 && !preauthContext.isPaTypeAllowed(pae.getPaDataType())) {
                    continue;
                }
                final PreauthHandle handle = this.findHandle(kdcRequest, preauthContext.getAllowedPaType());
                if (handle == null) {
                    continue;
                }
                if (real > 0 && preauthContext.checkAndPutTried(pae.getPaDataType())) {
                    continue;
                }
                final boolean gotData = handle.process(kdcRequest, pae, outPadata);
                if (real > 0 && gotData) {
                    return;
                }
            }
        }
    }
    
    public void tryAgain(final KdcRequest kdcRequest, final PaData inPadata, final PaData outPadata) {
        final PreauthContext preauthContext = kdcRequest.getPreauthContext();
        for (final PaDataEntry pae : inPadata.getElements()) {
            final PreauthHandle handle = this.findHandle(kdcRequest, pae.getPaDataType());
            if (handle != null) {
                handle.tryAgain(kdcRequest, pae.getPaDataType(), preauthContext.getErrorPaData(), outPadata);
            }
        }
    }
    
    public void destroy() {
        for (final KrbPreauth preauth : this.preauths) {
            preauth.destroy();
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
}
