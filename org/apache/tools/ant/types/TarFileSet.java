// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

public class TarFileSet extends ArchiveFileSet
{
    private boolean userNameSet;
    private boolean groupNameSet;
    private boolean userIdSet;
    private boolean groupIdSet;
    private String userName;
    private String groupName;
    private int uid;
    private int gid;
    
    public TarFileSet() {
        this.userName = "";
        this.groupName = "";
    }
    
    protected TarFileSet(final FileSet fileset) {
        super(fileset);
        this.userName = "";
        this.groupName = "";
    }
    
    protected TarFileSet(final TarFileSet fileset) {
        super(fileset);
        this.userName = "";
        this.groupName = "";
    }
    
    public void setUserName(final String userName) {
        this.checkTarFileSetAttributesAllowed();
        this.userNameSet = true;
        this.userName = userName;
    }
    
    public String getUserName() {
        if (this.isReference()) {
            return ((TarFileSet)this.getCheckedRef()).getUserName();
        }
        return this.userName;
    }
    
    public boolean hasUserNameBeenSet() {
        return this.userNameSet;
    }
    
    public void setUid(final int uid) {
        this.checkTarFileSetAttributesAllowed();
        this.userIdSet = true;
        this.uid = uid;
    }
    
    public int getUid() {
        if (this.isReference()) {
            return ((TarFileSet)this.getCheckedRef()).getUid();
        }
        return this.uid;
    }
    
    public boolean hasUserIdBeenSet() {
        return this.userIdSet;
    }
    
    public void setGroup(final String groupName) {
        this.checkTarFileSetAttributesAllowed();
        this.groupNameSet = true;
        this.groupName = groupName;
    }
    
    public String getGroup() {
        if (this.isReference()) {
            return ((TarFileSet)this.getCheckedRef()).getGroup();
        }
        return this.groupName;
    }
    
    public boolean hasGroupBeenSet() {
        return this.groupNameSet;
    }
    
    public void setGid(final int gid) {
        this.checkTarFileSetAttributesAllowed();
        this.groupIdSet = true;
        this.gid = gid;
    }
    
    public int getGid() {
        if (this.isReference()) {
            return ((TarFileSet)this.getCheckedRef()).getGid();
        }
        return this.gid;
    }
    
    public boolean hasGroupIdBeenSet() {
        return this.groupIdSet;
    }
    
    @Override
    protected ArchiveScanner newArchiveScanner() {
        final TarScanner zs = new TarScanner();
        return zs;
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (this.userNameSet || this.userIdSet || this.groupNameSet || this.groupIdSet) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    protected AbstractFileSet getRef(final Project p) {
        this.dieOnCircularReference(p);
        final Object o = this.getRefid().getReferencedObject(p);
        if (o instanceof TarFileSet) {
            return (AbstractFileSet)o;
        }
        if (o instanceof FileSet) {
            final TarFileSet zfs = new TarFileSet((FileSet)o);
            this.configureFileSet(zfs);
            return zfs;
        }
        final String msg = this.getRefid().getRefId() + " doesn't denote a tarfileset or a fileset";
        throw new BuildException(msg);
    }
    
    @Override
    protected void configureFileSet(final ArchiveFileSet zfs) {
        super.configureFileSet(zfs);
        if (zfs instanceof TarFileSet) {
            final TarFileSet tfs = (TarFileSet)zfs;
            tfs.setUserName(this.userName);
            tfs.setGroup(this.groupName);
            tfs.setUid(this.uid);
            tfs.setGid(this.gid);
        }
    }
    
    @Override
    public Object clone() {
        if (this.isReference()) {
            return ((TarFileSet)this.getRef(this.getProject())).clone();
        }
        return super.clone();
    }
    
    private void checkTarFileSetAttributesAllowed() {
        if (this.getProject() == null || (this.isReference() && this.getRefid().getReferencedObject(this.getProject()) instanceof TarFileSet)) {
            this.checkAttributesAllowed();
        }
    }
}
