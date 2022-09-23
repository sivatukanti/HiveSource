// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.io.retry.AtMostOnce;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface ApplicationMasterProtocol
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RegisterApplicationMasterResponse registerApplicationMaster(final RegisterApplicationMasterRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @AtMostOnce
    FinishApplicationMasterResponse finishApplicationMaster(final FinishApplicationMasterRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @AtMostOnce
    AllocateResponse allocate(final AllocateRequest p0) throws YarnException, IOException;
}
