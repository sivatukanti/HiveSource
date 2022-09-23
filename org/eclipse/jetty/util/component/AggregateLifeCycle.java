// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class AggregateLifeCycle extends AbstractLifeCycle implements Destroyable, Dumpable
{
    private static final Logger LOG;
    private final List<Bean> _beans;
    private boolean _started;
    
    public AggregateLifeCycle() {
        this._beans = new CopyOnWriteArrayList<Bean>();
        this._started = false;
    }
    
    @Override
    protected void doStart() throws Exception {
        for (final Bean b : this._beans) {
            if (b._managed && b._bean instanceof LifeCycle) {
                final LifeCycle l = (LifeCycle)b._bean;
                if (l.isRunning()) {
                    continue;
                }
                l.start();
            }
        }
        this._started = true;
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._started = false;
        super.doStop();
        final List<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        for (final Bean b : reverse) {
            if (b._managed && b._bean instanceof LifeCycle) {
                final LifeCycle l = (LifeCycle)b._bean;
                if (!l.isRunning()) {
                    continue;
                }
                l.stop();
            }
        }
    }
    
    public void destroy() {
        final List<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        for (final Bean b : reverse) {
            if (b._bean instanceof Destroyable && b._managed) {
                final Destroyable d = (Destroyable)b._bean;
                d.destroy();
            }
        }
        this._beans.clear();
    }
    
    public boolean contains(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isManaged(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                return b._managed;
            }
        }
        return false;
    }
    
    public boolean addBean(final Object o) {
        return this.addBean(o, !(o instanceof LifeCycle) || !((LifeCycle)o).isStarted());
    }
    
    public boolean addBean(final Object o, final boolean managed) {
        if (this.contains(o)) {
            return false;
        }
        final Bean b = new Bean(o);
        b._managed = managed;
        this._beans.add(b);
        if (o instanceof LifeCycle) {
            final LifeCycle l = (LifeCycle)o;
            if (managed && this._started) {
                try {
                    l.start();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
    
    public void manage(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                b._managed = true;
                return;
            }
        }
        throw new IllegalArgumentException();
    }
    
    public void unmanage(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                b._managed = false;
                return;
            }
        }
        throw new IllegalArgumentException();
    }
    
    public Collection<Object> getBeans() {
        return this.getBeans(Object.class);
    }
    
    public <T> List<T> getBeans(final Class<T> clazz) {
        final ArrayList<T> beans = new ArrayList<T>();
        for (final Bean b : this._beans) {
            if (clazz.isInstance(b._bean)) {
                beans.add((T)b._bean);
            }
        }
        return beans;
    }
    
    public <T> T getBean(final Class<T> clazz) {
        for (final Bean b : this._beans) {
            if (clazz.isInstance(b._bean)) {
                return (T)b._bean;
            }
        }
        return null;
    }
    
    public void removeBeans() {
        this._beans.clear();
    }
    
    public boolean removeBean(final Object o) {
        for (final Bean b : this._beans) {
            if (b._bean == o) {
                this._beans.remove(b);
                return true;
            }
        }
        return false;
    }
    
    public void dumpStdErr() {
        try {
            this.dump(System.err, "");
        }
        catch (IOException e) {
            AggregateLifeCycle.LOG.warn(e);
        }
    }
    
    public String dump() {
        return dump(this);
    }
    
    public static String dump(final Dumpable dumpable) {
        final StringBuilder b = new StringBuilder();
        try {
            dumpable.dump(b, "");
        }
        catch (IOException e) {
            AggregateLifeCycle.LOG.warn(e);
        }
        return b.toString();
    }
    
    public void dump(final Appendable out) throws IOException {
        this.dump(out, "");
    }
    
    protected void dumpThis(final Appendable out) throws IOException {
        out.append(String.valueOf(this)).append(" - ").append(this.getState()).append("\n");
    }
    
    public static void dumpObject(final Appendable out, final Object o) throws IOException {
        if (o instanceof LifeCycle) {
            out.append(String.valueOf(o)).append(" - ").append(AbstractLifeCycle.getState((LifeCycle)o)).append("\n");
        }
        else {
            out.append(String.valueOf(o)).append("\n");
        }
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpThis(out);
        final int size = this._beans.size();
        if (size == 0) {
            return;
        }
        int i = 0;
        for (final Bean b : this._beans) {
            ++i;
            if (b._managed) {
                out.append(indent).append(" +- ");
                if (b._bean instanceof Dumpable) {
                    ((Dumpable)b._bean).dump(out, indent + ((i == size) ? "    " : " |  "));
                }
                else {
                    dumpObject(out, b._bean);
                }
            }
            else {
                dumpObject(out, b._bean);
            }
        }
        if (i != size) {
            out.append(indent).append(" |\n");
        }
    }
    
    public static void dump(final Appendable out, final String indent, final Collection<?>... collections) throws IOException {
        if (collections.length == 0) {
            return;
        }
        int size = 0;
        for (final Collection<?> c : collections) {
            size += c.size();
        }
        if (size == 0) {
            return;
        }
        int i = 0;
        for (final Collection<?> c2 : collections) {
            for (final Object o : c2) {
                ++i;
                out.append(indent).append(" +- ");
                if (o instanceof Dumpable) {
                    ((Dumpable)o).dump(out, indent + ((i == size) ? "    " : " |  "));
                }
                else {
                    dumpObject(out, o);
                }
            }
            if (i != size) {
                out.append(indent).append(" |\n");
            }
        }
    }
    
    static {
        LOG = Log.getLogger(AggregateLifeCycle.class);
    }
    
    private class Bean
    {
        final Object _bean;
        volatile boolean _managed;
        
        Bean(final Object b) {
            this._managed = true;
            this._bean = b;
        }
    }
}
