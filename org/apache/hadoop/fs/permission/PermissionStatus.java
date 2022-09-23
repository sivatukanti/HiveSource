// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.io.WritableFactories;
import java.io.DataOutput;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.io.WritableFactory;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class PermissionStatus implements Writable
{
    static final WritableFactory FACTORY;
    private String username;
    private String groupname;
    private FsPermission permission;
    
    public static PermissionStatus createImmutable(final String user, final String group, final FsPermission permission) {
        return new PermissionStatus(user, group, permission) {
            @Override
            public void readFields(final DataInput in) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private PermissionStatus() {
    }
    
    public PermissionStatus(final String user, final String group, final FsPermission permission) {
        this.username = user;
        this.groupname = group;
        this.permission = permission;
    }
    
    public String getUserName() {
        return this.username;
    }
    
    public String getGroupName() {
        return this.groupname;
    }
    
    public FsPermission getPermission() {
        return this.permission;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.username = Text.readString(in, 1048576);
        this.groupname = Text.readString(in, 1048576);
        this.permission = FsPermission.read(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        write(out, this.username, this.groupname, this.permission);
    }
    
    public static PermissionStatus read(final DataInput in) throws IOException {
        final PermissionStatus p = new PermissionStatus();
        p.readFields(in);
        return p;
    }
    
    public static void write(final DataOutput out, final String username, final String groupname, final FsPermission permission) throws IOException {
        Text.writeString(out, username, 1048576);
        Text.writeString(out, groupname, 1048576);
        permission.write(out);
    }
    
    @Override
    public String toString() {
        return this.username + ":" + this.groupname + ":" + this.permission;
    }
    
    static {
        WritableFactories.setFactory(PermissionStatus.class, FACTORY = new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new PermissionStatus(null);
            }
        });
    }
}
