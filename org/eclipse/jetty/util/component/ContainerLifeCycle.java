// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import java.io.IOException;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Implementation of Container and LifeCycle")
public class ContainerLifeCycle extends AbstractLifeCycle implements Container, Destroyable, Dumpable
{
    private static final Logger LOG;
    private final List<Bean> _beans;
    private final List<Container.Listener> _listeners;
    private boolean _doStarted;
    private boolean _destroyed;
    
    public ContainerLifeCycle() {
        this._beans = new CopyOnWriteArrayList<Bean>();
        this._listeners = new CopyOnWriteArrayList<Container.Listener>();
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._destroyed) {
            throw new IllegalStateException("Destroyed container cannot be restarted");
        }
        this._doStarted = true;
        for (final Bean b : this._beans) {
            if (b._bean instanceof LifeCycle) {
                final LifeCycle l = (LifeCycle)b._bean;
                switch (b._managed) {
                    case MANAGED: {
                        if (!l.isRunning()) {
                            this.start(l);
                            continue;
                        }
                        continue;
                    }
                    case AUTO: {
                        if (l.isRunning()) {
                            this.unmanage(b);
                            continue;
                        }
                        this.manage(b);
                        this.start(l);
                        continue;
                    }
                }
            }
        }
        super.doStart();
    }
    
    protected void start(final LifeCycle l) throws Exception {
        l.start();
    }
    
    protected void stop(final LifeCycle l) throws Exception {
        l.stop();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._doStarted = false;
        super.doStop();
        final List<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        for (final Bean b : reverse) {
            if (b._managed == Managed.MANAGED && b._bean instanceof LifeCycle) {
                final LifeCycle l = (LifeCycle)b._bean;
                this.stop(l);
            }
        }
    }
    
    @Override
    public void destroy() {
        this._destroyed = true;
        final List<Bean> reverse = new ArrayList<Bean>(this._beans);
        Collections.reverse(reverse);
        for (final Bean b : reverse) {
            if (b._bean instanceof Destroyable && (b._managed == Managed.MANAGED || b._managed == Managed.POJO)) {
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
                return b.isManaged();
            }
        }
        return false;
    }
    
    @Override
    public boolean addBean(final Object o) {
        if (o instanceof LifeCycle) {
            final LifeCycle l = (LifeCycle)o;
            return this.addBean(o, l.isRunning() ? Managed.UNMANAGED : Managed.AUTO);
        }
        return this.addBean(o, Managed.POJO);
    }
    
    public boolean addBean(final Object o, final boolean managed) {
        if (o instanceof LifeCycle) {
            return this.addBean(o, managed ? Managed.MANAGED : Managed.UNMANAGED);
        }
        return this.addBean(o, managed ? Managed.POJO : Managed.UNMANAGED);
    }
    
    public boolean addBean(final Object o, final Managed managed) {
        if (o == null || this.contains(o)) {
            return false;
        }
        final Bean new_bean = new Bean(o);
        if (o instanceof Container.Listener) {
            this.addEventListener((Container.Listener)o);
        }
        this._beans.add(new_bean);
        for (final Container.Listener l : this._listeners) {
            l.beanAdded(this, o);
        }
        try {
            switch (managed) {
                case UNMANAGED: {
                    this.unmanage(new_bean);
                    break;
                }
                case MANAGED: {
                    this.manage(new_bean);
                    if (this.isStarting() && this._doStarted) {
                        final LifeCycle i = (LifeCycle)o;
                        if (!i.isRunning()) {
                            this.start(i);
                        }
                        break;
                    }
                    break;
                }
                case AUTO: {
                    if (o instanceof LifeCycle) {
                        final LifeCycle i = (LifeCycle)o;
                        if (this.isStarting()) {
                            if (i.isRunning()) {
                                this.unmanage(new_bean);
                            }
                            else if (this._doStarted) {
                                this.manage(new_bean);
                                this.start(i);
                            }
                            else {
                                new_bean._managed = Managed.AUTO;
                            }
                        }
                        else if (this.isStarted()) {
                            this.unmanage(new_bean);
                        }
                        else {
                            new_bean._managed = Managed.AUTO;
                        }
                        break;
                    }
                    new_bean._managed = Managed.POJO;
                    break;
                }
                case POJO: {
                    new_bean._managed = Managed.POJO;
                    break;
                }
            }
        }
        catch (RuntimeException | Error ex) {
            final Throwable t;
            final Throwable e = t;
            throw e;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
        if (ContainerLifeCycle.LOG.isDebugEnabled()) {
            ContainerLifeCycle.LOG.debug("{} added {}", this, new_bean);
        }
        return true;
    }
    
    public void addManaged(final LifeCycle lifecycle) {
        this.addBean(lifecycle, true);
        try {
            if (this.isRunning() && !lifecycle.isRunning()) {
                this.start(lifecycle);
            }
        }
        catch (RuntimeException | Error ex) {
            final Throwable t;
            final Throwable e = t;
            throw e;
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
    
    @Override
    public void addEventListener(final Container.Listener listener) {
        if (this._listeners.contains(listener)) {
            return;
        }
        this._listeners.add(listener);
        for (final Bean b : this._beans) {
            listener.beanAdded(this, b._bean);
            if (listener instanceof InheritedListener && b.isManaged() && b._bean instanceof Container) {
                if (b._bean instanceof ContainerLifeCycle) {
                    ((ContainerLifeCycle)b._bean).addBean(listener, false);
                }
                else {
                    ((Container)b._bean).addBean(listener);
                }
            }
        }
    }
    
    public void manage(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                this.manage(b);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }
    
    private void manage(final Bean bean) {
        if (bean._managed != Managed.MANAGED) {
            bean._managed = Managed.MANAGED;
            if (bean._bean instanceof Container) {
                for (final Container.Listener l : this._listeners) {
                    if (l instanceof InheritedListener) {
                        if (bean._bean instanceof ContainerLifeCycle) {
                            ((ContainerLifeCycle)bean._bean).addBean(l, false);
                        }
                        else {
                            ((Container)bean._bean).addBean(l);
                        }
                    }
                }
            }
            if (bean._bean instanceof AbstractLifeCycle) {
                ((AbstractLifeCycle)bean._bean).setStopTimeout(this.getStopTimeout());
            }
        }
    }
    
    public void unmanage(final Object bean) {
        for (final Bean b : this._beans) {
            if (b._bean == bean) {
                this.unmanage(b);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }
    
    private void unmanage(final Bean bean) {
        if (bean._managed != Managed.UNMANAGED) {
            if (bean._managed == Managed.MANAGED && bean._bean instanceof Container) {
                for (final Container.Listener l : this._listeners) {
                    if (l instanceof InheritedListener) {
                        ((Container)bean._bean).removeBean(l);
                    }
                }
            }
            bean._managed = Managed.UNMANAGED;
        }
    }
    
    @Override
    public Collection<Object> getBeans() {
        return this.getBeans(Object.class);
    }
    
    public void setBeans(final Collection<Object> beans) {
        for (final Object bean : beans) {
            this.addBean(bean);
        }
    }
    
    @Override
    public <T> Collection<T> getBeans(final Class<T> clazz) {
        final ArrayList<T> beans = new ArrayList<T>();
        for (final Bean b : this._beans) {
            if (clazz.isInstance(b._bean)) {
                beans.add(clazz.cast(b._bean));
            }
        }
        return beans;
    }
    
    @Override
    public <T> T getBean(final Class<T> clazz) {
        for (final Bean b : this._beans) {
            if (clazz.isInstance(b._bean)) {
                return clazz.cast(b._bean);
            }
        }
        return null;
    }
    
    public void removeBeans() {
        final ArrayList<Bean> beans = new ArrayList<Bean>(this._beans);
        for (final Bean b : beans) {
            this.remove(b);
        }
    }
    
    private Bean getBean(final Object o) {
        for (final Bean b : this._beans) {
            if (b._bean == o) {
                return b;
            }
        }
        return null;
    }
    
    @Override
    public boolean removeBean(final Object o) {
        final Bean b = this.getBean(o);
        return b != null && this.remove(b);
    }
    
    private boolean remove(final Bean bean) {
        if (this._beans.remove(bean)) {
            final boolean wasManaged = bean.isManaged();
            this.unmanage(bean);
            for (final Container.Listener l : this._listeners) {
                l.beanRemoved(this, bean._bean);
            }
            if (bean._bean instanceof Container.Listener) {
                this.removeEventListener((Container.Listener)bean._bean);
            }
            if (wasManaged && bean._bean instanceof LifeCycle) {
                try {
                    this.stop((LifeCycle)bean._bean);
                }
                catch (RuntimeException | Error ex) {
                    final Throwable t;
                    final Throwable e = t;
                    throw e;
                }
                catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void removeEventListener(final Container.Listener listener) {
        if (this._listeners.remove(listener)) {
            for (final Bean b : this._beans) {
                listener.beanRemoved(this, b._bean);
                if (listener instanceof InheritedListener && b.isManaged() && b._bean instanceof Container) {
                    ((Container)b._bean).removeBean(listener);
                }
            }
        }
    }
    
    @Override
    public void setStopTimeout(final long stopTimeout) {
        super.setStopTimeout(stopTimeout);
        for (final Bean bean : this._beans) {
            if (bean.isManaged() && bean._bean instanceof AbstractLifeCycle) {
                ((AbstractLifeCycle)bean._bean).setStopTimeout(stopTimeout);
            }
        }
    }
    
    @ManagedOperation("Dump the object to stderr")
    public void dumpStdErr() {
        try {
            this.dump(System.err, "");
        }
        catch (IOException e) {
            ContainerLifeCycle.LOG.warn(e);
        }
    }
    
    @ManagedOperation("Dump the object to a string")
    @Override
    public String dump() {
        return dump(this);
    }
    
    public static String dump(final Dumpable dumpable) {
        final StringBuilder b = new StringBuilder();
        try {
            dumpable.dump(b, "");
        }
        catch (IOException e) {
            ContainerLifeCycle.LOG.warn(e);
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
        try {
            if (o instanceof LifeCycle) {
                out.append(String.valueOf(o)).append(" - ").append(AbstractLifeCycle.getState((LifeCycle)o)).append("\n");
            }
            else {
                out.append(String.valueOf(o)).append("\n");
            }
        }
        catch (Throwable th) {
            out.append(" => ").append(th.toString()).append('\n');
        }
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, (Collection<?>[])new Collection[0]);
    }
    
    protected void dumpBeans(final Appendable out, final String indent, final Collection<?>... collections) throws IOException {
        this.dumpThis(out);
        int size = this._beans.size();
        for (final Collection<?> c : collections) {
            size += c.size();
        }
        if (size == 0) {
            return;
        }
        int i = 0;
        for (final Bean b : this._beans) {
            ++i;
            switch (b._managed) {
                case POJO: {
                    out.append(indent).append(" +- ");
                    if (b._bean instanceof Dumpable) {
                        ((Dumpable)b._bean).dump(out, indent + ((i == size) ? "    " : " |  "));
                        continue;
                    }
                    dumpObject(out, b._bean);
                    continue;
                }
                case MANAGED: {
                    out.append(indent).append(" += ");
                    if (b._bean instanceof Dumpable) {
                        ((Dumpable)b._bean).dump(out, indent + ((i == size) ? "    " : " |  "));
                        continue;
                    }
                    dumpObject(out, b._bean);
                    continue;
                }
                case UNMANAGED: {
                    out.append(indent).append(" +~ ");
                    dumpObject(out, b._bean);
                    continue;
                }
                case AUTO: {
                    out.append(indent).append(" +? ");
                    if (b._bean instanceof Dumpable) {
                        ((Dumpable)b._bean).dump(out, indent + ((i == size) ? "    " : " |  "));
                        continue;
                    }
                    dumpObject(out, b._bean);
                    continue;
                }
            }
        }
        if (i < size) {
            out.append(indent).append(" |\n");
        }
        for (final Collection<?> c2 : collections) {
            for (final Object o : c2) {
                ++i;
                out.append(indent).append(" +> ");
                if (o instanceof Dumpable) {
                    ((Dumpable)o).dump(out, indent + ((i == size) ? "    " : " |  "));
                }
                else {
                    dumpObject(out, o);
                }
            }
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
        }
    }
    
    public void updateBean(final Object oldBean, final Object newBean) {
        if (newBean != oldBean) {
            if (oldBean != null) {
                this.removeBean(oldBean);
            }
            if (newBean != null) {
                this.addBean(newBean);
            }
        }
    }
    
    public void updateBean(final Object oldBean, final Object newBean, final boolean managed) {
        if (newBean != oldBean) {
            if (oldBean != null) {
                this.removeBean(oldBean);
            }
            if (newBean != null) {
                this.addBean(newBean, managed);
            }
        }
    }
    
    public void updateBeans(final Object[] oldBeans, final Object[] newBeans) {
        if (oldBeans != null) {
            for (final Object o : oldBeans) {
                Label_0078: {
                    if (newBeans != null) {
                        for (final Object n : newBeans) {
                            if (o == n) {
                                break Label_0078;
                            }
                        }
                    }
                    this.removeBean(o);
                }
            }
        }
        if (newBeans != null) {
            for (final Object n2 : newBeans) {
                Label_0162: {
                    if (oldBeans != null) {
                        for (final Object o2 : oldBeans) {
                            if (o2 == n2) {
                                break Label_0162;
                            }
                        }
                    }
                    this.addBean(n2);
                }
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ContainerLifeCycle.class);
    }
    
    enum Managed
    {
        POJO, 
        MANAGED, 
        UNMANAGED, 
        AUTO;
    }
    
    private static class Bean
    {
        private final Object _bean;
        private volatile Managed _managed;
        
        private Bean(final Object b) {
            this._managed = Managed.POJO;
            if (b == null) {
                throw new NullPointerException();
            }
            this._bean = b;
        }
        
        public boolean isManaged() {
            return this._managed == Managed.MANAGED;
        }
        
        @Override
        public String toString() {
            return String.format("{%s,%s}", this._bean, this._managed);
        }
    }
}
