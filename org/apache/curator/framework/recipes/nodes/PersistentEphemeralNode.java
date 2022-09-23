// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.nodes;

import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.CuratorFramework;

@Deprecated
public class PersistentEphemeralNode extends PersistentNode
{
    public PersistentEphemeralNode(final CuratorFramework client, final Mode mode, final String basePath, final byte[] initData) {
        super(client, mode.getCreateMode(false), mode.isProtected(), basePath, initData);
    }
    
    @Deprecated
    public enum Mode
    {
        EPHEMERAL {
            @Override
            protected CreateMode getCreateMode(final boolean pathIsSet) {
                return CreateMode.EPHEMERAL;
            }
            
            @Override
            protected boolean isProtected() {
                return false;
            }
        }, 
        EPHEMERAL_SEQUENTIAL {
            @Override
            protected CreateMode getCreateMode(final boolean pathIsSet) {
                return pathIsSet ? CreateMode.EPHEMERAL : CreateMode.EPHEMERAL_SEQUENTIAL;
            }
            
            @Override
            protected boolean isProtected() {
                return false;
            }
        }, 
        PROTECTED_EPHEMERAL {
            @Override
            protected CreateMode getCreateMode(final boolean pathIsSet) {
                return CreateMode.EPHEMERAL;
            }
            
            @Override
            protected boolean isProtected() {
                return true;
            }
        }, 
        PROTECTED_EPHEMERAL_SEQUENTIAL {
            @Override
            protected CreateMode getCreateMode(final boolean pathIsSet) {
                return pathIsSet ? CreateMode.EPHEMERAL : CreateMode.EPHEMERAL_SEQUENTIAL;
            }
            
            @Override
            protected boolean isProtected() {
                return true;
            }
        };
        
        protected abstract CreateMode getCreateMode(final boolean p0);
        
        protected abstract boolean isProtected();
    }
}
