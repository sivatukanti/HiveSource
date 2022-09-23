// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public interface AsyncCallback
{
    @InterfaceAudience.Public
    public interface MultiCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final List<OpResult> p3);
    }
    
    @InterfaceAudience.Public
    public interface VoidCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2);
    }
    
    @InterfaceAudience.Public
    public interface StringCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final String p3);
    }
    
    @InterfaceAudience.Public
    public interface Children2Callback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final List<String> p3, final Stat p4);
    }
    
    @InterfaceAudience.Public
    public interface ChildrenCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final List<String> p3);
    }
    
    @InterfaceAudience.Public
    public interface ACLCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final List<ACL> p3, final Stat p4);
    }
    
    @InterfaceAudience.Public
    public interface DataCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final byte[] p3, final Stat p4);
    }
    
    @InterfaceAudience.Public
    public interface StatCallback extends AsyncCallback
    {
        void processResult(final int p0, final String p1, final Object p2, final Stat p3);
    }
}
