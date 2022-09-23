// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.proto.CheckVersionRequest;
import org.apache.zookeeper.proto.SetDataRequest;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.proto.CreateRequest;
import java.util.Iterator;
import java.util.Arrays;
import org.apache.zookeeper.common.PathUtils;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import java.util.List;

public abstract class Op
{
    private int type;
    private String path;
    
    private Op(final int type, final String path) {
        this.type = type;
        this.path = path;
    }
    
    public static Op create(final String path, final byte[] data, final List<ACL> acl, final int flags) {
        return new Create(path, data, (List)acl, flags);
    }
    
    public static Op create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) {
        return new Create(path, data, (List)acl, createMode);
    }
    
    public static Op delete(final String path, final int version) {
        return new Delete(path, version);
    }
    
    public static Op setData(final String path, final byte[] data, final int version) {
        return new SetData(path, data, version);
    }
    
    public static Op check(final String path, final int version) {
        return new Check(path, version);
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public abstract Record toRequestRecord();
    
    abstract Op withChroot(final String p0);
    
    void validate() throws KeeperException {
        PathUtils.validatePath(this.path);
    }
    
    public static class Create extends Op
    {
        private byte[] data;
        private List<ACL> acl;
        private int flags;
        
        private Create(final String path, final byte[] data, final List<ACL> acl, final int flags) {
            super(1, path, null);
            this.data = data;
            this.acl = acl;
            this.flags = flags;
        }
        
        private Create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) {
            super(1, path, null);
            this.data = data;
            this.acl = acl;
            this.flags = createMode.toFlag();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Create)) {
                return false;
            }
            final Create op = (Create)o;
            boolean aclEquals = true;
            final Iterator<ACL> i = op.acl.iterator();
            for (final ACL acl : op.acl) {
                final boolean hasMoreData = i.hasNext();
                if (!hasMoreData) {
                    aclEquals = false;
                    break;
                }
                final ACL otherAcl = i.next();
                if (!acl.equals(otherAcl)) {
                    aclEquals = false;
                    break;
                }
            }
            return !i.hasNext() && this.getType() == op.getType() && Arrays.equals(this.data, op.data) && this.flags == op.flags && aclEquals;
        }
        
        @Override
        public int hashCode() {
            return this.getType() + this.getPath().hashCode() + Arrays.hashCode(this.data);
        }
        
        @Override
        public Record toRequestRecord() {
            return new CreateRequest(this.getPath(), this.data, this.acl, this.flags);
        }
        
        @Override
        Op withChroot(final String path) {
            return new Create(path, this.data, this.acl, this.flags);
        }
        
        @Override
        void validate() throws KeeperException {
            final CreateMode createMode = CreateMode.fromFlag(this.flags);
            PathUtils.validatePath(this.getPath(), createMode.isSequential());
        }
    }
    
    public static class Delete extends Op
    {
        private int version;
        
        private Delete(final String path, final int version) {
            super(2, path, null);
            this.version = version;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Delete)) {
                return false;
            }
            final Delete op = (Delete)o;
            return this.getType() == op.getType() && this.version == op.version && this.getPath().equals(op.getPath());
        }
        
        @Override
        public int hashCode() {
            return this.getType() + this.getPath().hashCode() + this.version;
        }
        
        @Override
        public Record toRequestRecord() {
            return new DeleteRequest(this.getPath(), this.version);
        }
        
        @Override
        Op withChroot(final String path) {
            return new Delete(path, this.version);
        }
    }
    
    public static class SetData extends Op
    {
        private byte[] data;
        private int version;
        
        private SetData(final String path, final byte[] data, final int version) {
            super(5, path, null);
            this.data = data;
            this.version = version;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetData)) {
                return false;
            }
            final SetData op = (SetData)o;
            return this.getType() == op.getType() && this.version == op.version && this.getPath().equals(op.getPath()) && Arrays.equals(this.data, op.data);
        }
        
        @Override
        public int hashCode() {
            return this.getType() + this.getPath().hashCode() + Arrays.hashCode(this.data) + this.version;
        }
        
        @Override
        public Record toRequestRecord() {
            return new SetDataRequest(this.getPath(), this.data, this.version);
        }
        
        @Override
        Op withChroot(final String path) {
            return new SetData(path, this.data, this.version);
        }
    }
    
    public static class Check extends Op
    {
        private int version;
        
        private Check(final String path, final int version) {
            super(13, path, null);
            this.version = version;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Check)) {
                return false;
            }
            final Check op = (Check)o;
            return this.getType() == op.getType() && this.getPath().equals(op.getPath()) && this.version == op.version;
        }
        
        @Override
        public int hashCode() {
            return this.getType() + this.getPath().hashCode() + this.version;
        }
        
        @Override
        public Record toRequestRecord() {
            return new CheckVersionRequest(this.getPath(), this.version);
        }
        
        @Override
        Op withChroot(final String path) {
            return new Check(path, this.version);
        }
    }
}
