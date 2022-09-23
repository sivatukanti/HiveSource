// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.jdo.listener.StoreCallback;
import javax.jdo.listener.LoadCallback;
import javax.jdo.listener.DeleteCallback;
import javax.jdo.listener.ClearCallback;

public interface InstanceCallbacks extends ClearCallback, DeleteCallback, LoadCallback, StoreCallback
{
}
