// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarInputStream;
import java.io.InputStream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.tar.TarEntry;
import java.io.File;

public class TarResource extends ArchiveResource
{
    private String userName;
    private String groupName;
    private int uid;
    private int gid;
    
    public TarResource() {
        this.userName = "";
        this.groupName = "";
    }
    
    public TarResource(final File a, final TarEntry e) {
        super(a, true);
        this.userName = "";
        this.groupName = "";
        this.setEntry(e);
    }
    
    public TarResource(final Resource a, final TarEntry e) {
        super(a, true);
        this.userName = "";
        this.groupName = "";
        this.setEntry(e);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getInputStream();
        }
        final Resource archive = this.getArchive();
        final TarInputStream i = new TarInputStream(archive.getInputStream());
        TarEntry te = null;
        while ((te = i.getNextEntry()) != null) {
            if (te.getName().equals(this.getName())) {
                return i;
            }
        }
        FileUtils.close(i);
        throw new BuildException("no entry " + this.getName() + " in " + this.getArchive());
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getOutputStream();
        }
        throw new UnsupportedOperationException("Use the tar task for tar output.");
    }
    
    public String getUserName() {
        if (this.isReference()) {
            return ((TarResource)this.getCheckedRef()).getUserName();
        }
        this.checkEntry();
        return this.userName;
    }
    
    public String getGroup() {
        if (this.isReference()) {
            return ((TarResource)this.getCheckedRef()).getGroup();
        }
        this.checkEntry();
        return this.groupName;
    }
    
    public int getUid() {
        if (this.isReference()) {
            return ((TarResource)this.getCheckedRef()).getUid();
        }
        this.checkEntry();
        return this.uid;
    }
    
    public int getGid() {
        if (this.isReference()) {
            return ((TarResource)this.getCheckedRef()).getGid();
        }
        this.checkEntry();
        return this.gid;
    }
    
    @Override
    protected void fetchEntry() {
        final Resource archive = this.getArchive();
        TarInputStream i = null;
        try {
            i = new TarInputStream(archive.getInputStream());
            TarEntry te = null;
            while ((te = i.getNextEntry()) != null) {
                if (te.getName().equals(this.getName())) {
                    this.setEntry(te);
                    return;
                }
            }
        }
        catch (IOException e) {
            this.log(e.getMessage(), 4);
            throw new BuildException(e);
        }
        finally {
            if (i != null) {
                FileUtils.close(i);
            }
        }
        this.setEntry(null);
    }
    
    private void setEntry(final TarEntry e) {
        if (e == null) {
            this.setExists(false);
            return;
        }
        this.setName(e.getName());
        this.setExists(true);
        this.setLastModified(e.getModTime().getTime());
        this.setDirectory(e.isDirectory());
        this.setSize(e.getSize());
        this.setMode(e.getMode());
        this.userName = e.getUserName();
        this.groupName = e.getGroupName();
        this.uid = e.getUserId();
        this.gid = e.getGroupId();
    }
}
