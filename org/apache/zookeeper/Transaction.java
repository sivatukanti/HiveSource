// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.data.ACL;
import java.util.ArrayList;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class Transaction
{
    private ZooKeeper zk;
    private List<Op> ops;
    
    protected Transaction(final ZooKeeper zk) {
        this.ops = new ArrayList<Op>();
        this.zk = zk;
    }
    
    public Transaction create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) {
        this.ops.add(Op.create(path, data, acl, createMode.toFlag()));
        return this;
    }
    
    public Transaction delete(final String path, final int version) {
        this.ops.add(Op.delete(path, version));
        return this;
    }
    
    public Transaction check(final String path, final int version) {
        this.ops.add(Op.check(path, version));
        return this;
    }
    
    public Transaction setData(final String path, final byte[] data, final int version) {
        this.ops.add(Op.setData(path, data, version));
        return this;
    }
    
    public List<OpResult> commit() throws InterruptedException, KeeperException {
        return this.zk.multi(this.ops);
    }
    
    public void commit(final AsyncCallback.MultiCallback cb, final Object ctx) {
        this.zk.multi(this.ops, cb, ctx);
    }
}
