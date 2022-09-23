// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import org.datanucleus.ClassConstants;
import java.util.Collection;
import java.util.ArrayList;
import javax.jdo.listener.AttachCallback;
import javax.jdo.listener.AttachLifecycleListener;
import javax.jdo.listener.DetachCallback;
import javax.jdo.listener.DetachLifecycleListener;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.LoadCallback;
import javax.jdo.listener.DirtyLifecycleListener;
import javax.jdo.listener.DeleteCallback;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.ClearCallback;
import javax.jdo.listener.ClearLifecycleListener;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import javax.jdo.JDOUserCallbackException;
import javax.jdo.listener.StoreCallback;
import javax.jdo.listener.StoreLifecycleListener;
import java.util.Iterator;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.CreateLifecycleListener;
import java.util.IdentityHashMap;
import java.util.List;
import javax.jdo.listener.InstanceLifecycleListener;
import java.util.Map;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;
import org.datanucleus.state.CallbackHandler;

public class JDOCallbackHandler implements CallbackHandler
{
    protected static final Localiser LOCALISER;
    NucleusContext nucleusCtx;
    private final Map<InstanceLifecycleListener, LifecycleListenerForClass> listeners;
    private List<LifecycleListenerForClass> listenersWorkingCopy;
    CallbackHandler beanValidationHandler;
    
    public JDOCallbackHandler(final NucleusContext nucleusCtx) {
        this.listeners = new IdentityHashMap<InstanceLifecycleListener, LifecycleListenerForClass>(1);
        this.listenersWorkingCopy = null;
        this.nucleusCtx = nucleusCtx;
    }
    
    public void setValidationListener(final CallbackHandler handler) {
        this.beanValidationHandler = handler;
    }
    
    public void postCreate(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof CreateLifecycleListener) {
                ((CreateLifecycleListener)listener.getListener()).postCreate(new InstanceLifecycleEvent(pc, 0, null));
            }
        }
    }
    
    public void prePersist(final Object pc) {
        if (this.beanValidationHandler != null) {
            this.beanValidationHandler.prePersist(pc);
        }
    }
    
    public void preStore(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof StoreLifecycleListener) {
                final ExecutionContext ec = this.nucleusCtx.getApiAdapter().getExecutionContext(pc);
                String[] fieldNames = null;
                final ObjectProvider op = ec.findObjectProvider(pc);
                fieldNames = op.getDirtyFieldNames();
                if (fieldNames == null) {
                    fieldNames = op.getLoadedFieldNames();
                }
                ((StoreLifecycleListener)listener.getListener()).preStore(new FieldInstanceLifecycleEvent(pc, 2, null, fieldNames));
            }
        }
        if (pc instanceof StoreCallback) {
            try {
                ((StoreCallback)pc).jdoPreStore();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPreStore"), e);
            }
        }
        if (this.beanValidationHandler != null) {
            this.beanValidationHandler.prePersist(pc);
        }
    }
    
    public void postStore(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof StoreLifecycleListener) {
                ((StoreLifecycleListener)listener.getListener()).postStore(new InstanceLifecycleEvent(pc, 2, null));
            }
        }
    }
    
    public void preClear(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof ClearLifecycleListener) {
                ((ClearLifecycleListener)listener.getListener()).preClear(new InstanceLifecycleEvent(pc, 3, null));
            }
        }
        if (pc instanceof ClearCallback) {
            try {
                ((ClearCallback)pc).jdoPreClear();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPreClear"), e);
            }
        }
    }
    
    public void postClear(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof ClearLifecycleListener) {
                ((ClearLifecycleListener)listener.getListener()).postClear(new InstanceLifecycleEvent(pc, 3, null));
            }
        }
    }
    
    public void preDelete(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DeleteLifecycleListener) {
                ((DeleteLifecycleListener)listener.getListener()).preDelete(new InstanceLifecycleEvent(pc, 4, null));
            }
        }
        if (pc instanceof DeleteCallback) {
            try {
                ((DeleteCallback)pc).jdoPreDelete();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPreDelete"), e);
            }
        }
        if (this.beanValidationHandler != null) {
            this.beanValidationHandler.preDelete(pc);
        }
    }
    
    public void postDelete(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DeleteLifecycleListener) {
                ((DeleteLifecycleListener)listener.getListener()).postDelete(new InstanceLifecycleEvent(pc, 4, null));
            }
        }
    }
    
    public void preDirty(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DirtyLifecycleListener) {
                ((DirtyLifecycleListener)listener.getListener()).preDirty(new InstanceLifecycleEvent(pc, 5, null));
            }
        }
    }
    
    public void postDirty(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DirtyLifecycleListener) {
                ((DirtyLifecycleListener)listener.getListener()).postDirty(new InstanceLifecycleEvent(pc, 5, null));
            }
        }
    }
    
    public void postLoad(final Object pc) {
        if (pc instanceof LoadCallback) {
            try {
                ((LoadCallback)pc).jdoPostLoad();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPostLoad"), e);
            }
        }
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof LoadLifecycleListener) {
                ((LoadLifecycleListener)listener.getListener()).postLoad(new InstanceLifecycleEvent(pc, 1, null));
            }
        }
    }
    
    public void postRefresh(final Object pc) {
    }
    
    public void preDetach(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DetachLifecycleListener) {
                ((DetachLifecycleListener)listener.getListener()).preDetach(new InstanceLifecycleEvent(pc, 6, null));
            }
        }
        if (pc instanceof DetachCallback) {
            try {
                ((DetachCallback)pc).jdoPreDetach();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPreDetach"), e);
            }
        }
    }
    
    public void postDetach(final Object pc, final Object detachedPC) {
        if (pc instanceof DetachCallback) {
            try {
                ((DetachCallback)detachedPC).jdoPostDetach(pc);
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPostDetach"), e);
            }
        }
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof DetachLifecycleListener) {
                ((DetachLifecycleListener)listener.getListener()).postDetach(new InstanceLifecycleEvent(detachedPC, 6, pc));
            }
        }
    }
    
    public void preAttach(final Object pc) {
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof AttachLifecycleListener) {
                ((AttachLifecycleListener)listener.getListener()).preAttach(new InstanceLifecycleEvent(pc, 7, null));
            }
        }
        if (pc instanceof AttachCallback) {
            try {
                ((AttachCallback)pc).jdoPreAttach();
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPreAttach"), e);
            }
        }
    }
    
    public void postAttach(final Object pc, final Object detachedPC) {
        if (pc instanceof AttachCallback) {
            try {
                ((AttachCallback)pc).jdoPostAttach(detachedPC);
            }
            catch (Exception e) {
                throw new JDOUserCallbackException(JDOCallbackHandler.LOCALISER.msg("025001", "jdoPostAttach"), e);
            }
        }
        for (final LifecycleListenerForClass listener : this.getListenersWorkingCopy()) {
            if (listener.forClass(pc.getClass()) && listener.getListener() instanceof AttachLifecycleListener) {
                ((AttachLifecycleListener)listener.getListener()).postAttach(new InstanceLifecycleEvent(pc, 7, detachedPC));
            }
        }
    }
    
    public void addListener(final Object listener, final Class[] classes) {
        if (listener == null) {
            return;
        }
        final InstanceLifecycleListener jdoListener = (InstanceLifecycleListener)listener;
        LifecycleListenerForClass entry;
        if (this.listeners.containsKey(jdoListener)) {
            entry = this.listeners.get(jdoListener).mergeClasses(classes);
        }
        else {
            entry = new LifecycleListenerForClass(jdoListener, classes);
        }
        this.listeners.put(jdoListener, entry);
        this.listenersWorkingCopy = null;
    }
    
    public void removeListener(final Object listener) {
        if (this.listeners.remove(listener) != null) {
            this.listenersWorkingCopy = null;
        }
    }
    
    public void close() {
        this.listeners.clear();
        this.listenersWorkingCopy = null;
    }
    
    protected List<LifecycleListenerForClass> getListenersWorkingCopy() {
        if (this.listenersWorkingCopy == null) {
            this.listenersWorkingCopy = new ArrayList<LifecycleListenerForClass>(this.listeners.values());
        }
        return this.listenersWorkingCopy;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
