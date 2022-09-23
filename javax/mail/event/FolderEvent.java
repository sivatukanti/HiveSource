// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail.event;

import javax.mail.Folder;

public class FolderEvent extends MailEvent
{
    public static final int CREATED = 1;
    public static final int DELETED = 2;
    public static final int RENAMED = 3;
    protected int type;
    protected transient Folder folder;
    protected transient Folder newFolder;
    private static final long serialVersionUID = 5278131310563694307L;
    
    public FolderEvent(final Object source, final Folder folder, final int type) {
        this(source, folder, folder, type);
    }
    
    public FolderEvent(final Object source, final Folder oldFolder, final Folder newFolder, final int type) {
        super(source);
        this.folder = oldFolder;
        this.newFolder = newFolder;
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Folder getFolder() {
        return this.folder;
    }
    
    public Folder getNewFolder() {
        return this.newFolder;
    }
    
    public void dispatch(final Object listener) {
        if (this.type == 1) {
            ((FolderListener)listener).folderCreated(this);
        }
        else if (this.type == 2) {
            ((FolderListener)listener).folderDeleted(this);
        }
        else if (this.type == 3) {
            ((FolderListener)listener).folderRenamed(this);
        }
    }
}
