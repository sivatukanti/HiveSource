// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.component;

import java.util.EventListener;
import org.mortbay.log.Log;
import org.mortbay.util.LazyList;

public class Container
{
    private Object _listeners;
    
    public synchronized void addEventListener(final Listener listener) {
        this._listeners = LazyList.add(this._listeners, listener);
    }
    
    public synchronized void removeEventListener(final Listener listener) {
        this._listeners = LazyList.remove(this._listeners, listener);
    }
    
    public synchronized void update(final Object parent, final Object oldChild, final Object child, final String relationship) {
        if (oldChild != null && !oldChild.equals(child)) {
            this.remove(parent, oldChild, relationship);
        }
        if (child != null && !child.equals(oldChild)) {
            this.add(parent, child, relationship);
        }
    }
    
    public synchronized void update(final Object parent, final Object oldChild, final Object child, final String relationship, final boolean addRemove) {
        if (oldChild != null && !oldChild.equals(child)) {
            this.remove(parent, oldChild, relationship);
            if (addRemove) {
                this.removeBean(oldChild);
            }
        }
        if (child != null && !child.equals(oldChild)) {
            if (addRemove) {
                this.addBean(child);
            }
            this.add(parent, child, relationship);
        }
    }
    
    public synchronized void update(final Object parent, final Object[] oldChildren, final Object[] children, final String relationship) {
        this.update(parent, oldChildren, children, relationship, false);
    }
    
    public synchronized void update(final Object parent, final Object[] oldChildren, final Object[] children, final String relationship, final boolean addRemove) {
        Object[] newChildren = null;
        if (children != null) {
            newChildren = new Object[children.length];
            int i = children.length;
            while (i-- > 0) {
                boolean new_child = true;
                if (oldChildren != null) {
                    int j = oldChildren.length;
                    while (j-- > 0) {
                        if (children[i] != null && children[i].equals(oldChildren[j])) {
                            oldChildren[j] = null;
                            new_child = false;
                        }
                    }
                }
                if (new_child) {
                    newChildren[i] = children[i];
                }
            }
        }
        if (oldChildren != null) {
            int i = oldChildren.length;
            while (i-- > 0) {
                if (oldChildren[i] != null) {
                    this.remove(parent, oldChildren[i], relationship);
                    if (!addRemove) {
                        continue;
                    }
                    this.removeBean(oldChildren[i]);
                }
            }
        }
        if (newChildren != null) {
            for (int i = 0; i < newChildren.length; ++i) {
                if (newChildren[i] != null) {
                    if (addRemove) {
                        this.addBean(newChildren[i]);
                    }
                    this.add(parent, newChildren[i], relationship);
                }
            }
        }
    }
    
    public void addBean(final Object obj) {
        if (this._listeners != null) {
            for (int i = 0; i < LazyList.size(this._listeners); ++i) {
                final Listener listener = (Listener)LazyList.get(this._listeners, i);
                listener.addBean(obj);
            }
        }
    }
    
    public void removeBean(final Object obj) {
        if (this._listeners != null) {
            for (int i = 0; i < LazyList.size(this._listeners); ++i) {
                ((Listener)LazyList.get(this._listeners, i)).removeBean(obj);
            }
        }
    }
    
    private void add(final Object parent, final Object child, final String relationship) {
        if (Log.isDebugEnabled()) {
            Log.debug("Container " + parent + " + " + child + " as " + relationship);
        }
        if (this._listeners != null) {
            final Relationship event = new Relationship(this, parent, child, relationship);
            for (int i = 0; i < LazyList.size(this._listeners); ++i) {
                ((Listener)LazyList.get(this._listeners, i)).add(event);
            }
        }
    }
    
    private void remove(final Object parent, final Object child, final String relationship) {
        if (Log.isDebugEnabled()) {
            Log.debug("Container " + parent + " - " + child + " as " + relationship);
        }
        if (this._listeners != null) {
            final Relationship event = new Relationship(this, parent, child, relationship);
            for (int i = 0; i < LazyList.size(this._listeners); ++i) {
                ((Listener)LazyList.get(this._listeners, i)).remove(event);
            }
        }
    }
    
    public static class Relationship
    {
        private Object _parent;
        private Object _child;
        private String _relationship;
        private Container _container;
        
        private Relationship(final Container container, final Object parent, final Object child, final String relationship) {
            this._container = container;
            this._parent = parent;
            this._child = child;
            this._relationship = relationship;
        }
        
        public Container getContainer() {
            return this._container;
        }
        
        public Object getChild() {
            return this._child;
        }
        
        public Object getParent() {
            return this._parent;
        }
        
        public String getRelationship() {
            return this._relationship;
        }
        
        public String toString() {
            return this._parent + "---" + this._relationship + "-->" + this._child;
        }
        
        public int hashCode() {
            return this._parent.hashCode() + this._child.hashCode() + this._relationship.hashCode();
        }
        
        public boolean equals(final Object o) {
            if (o == null || !(o instanceof Relationship)) {
                return false;
            }
            final Relationship r = (Relationship)o;
            return r._parent == this._parent && r._child == this._child && r._relationship.equals(this._relationship);
        }
    }
    
    public interface Listener extends EventListener
    {
        void addBean(final Object p0);
        
        void removeBean(final Object p0);
        
        void add(final Relationship p0);
        
        void remove(final Relationship p0);
    }
}
