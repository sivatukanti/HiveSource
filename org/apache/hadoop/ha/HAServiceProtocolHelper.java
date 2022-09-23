// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import java.io.IOException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class HAServiceProtocolHelper
{
    public static void monitorHealth(final HAServiceProtocol svc, final HAServiceProtocol.StateChangeRequestInfo reqInfo) throws IOException {
        try {
            svc.monitorHealth();
        }
        catch (RemoteException e) {
            throw e.unwrapRemoteException(HealthCheckFailedException.class);
        }
    }
    
    public static void transitionToActive(final HAServiceProtocol svc, final HAServiceProtocol.StateChangeRequestInfo reqInfo) throws IOException {
        try {
            svc.transitionToActive(reqInfo);
        }
        catch (RemoteException e) {
            throw e.unwrapRemoteException(ServiceFailedException.class);
        }
    }
    
    public static void transitionToStandby(final HAServiceProtocol svc, final HAServiceProtocol.StateChangeRequestInfo reqInfo) throws IOException {
        try {
            svc.transitionToStandby(reqInfo);
        }
        catch (RemoteException e) {
            throw e.unwrapRemoteException(ServiceFailedException.class);
        }
    }
}
