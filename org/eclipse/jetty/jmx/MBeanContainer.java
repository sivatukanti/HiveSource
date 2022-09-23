// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jmx;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.util.component.LifeCycle;
import javax.management.ObjectInstance;
import javax.management.InstanceNotFoundException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.management.ObjectName;
import java.util.WeakHashMap;
import javax.management.MBeanServer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class MBeanContainer extends AbstractLifeCycle implements Container.Listener, Dumpable
{
    private static final Logger LOG;
    private final MBeanServer _server;
    private final WeakHashMap<Object, ObjectName> _beans;
    private final HashMap<String, Integer> _unique;
    private final WeakHashMap<ObjectName, List<Container.Relationship>> _relations;
    private String _domain;
    
    public synchronized ObjectName findMBean(final Object object) {
        final ObjectName bean = this._beans.get(object);
        return (bean == null) ? null : bean;
    }
    
    public synchronized Object findBean(final ObjectName oname) {
        for (final Map.Entry<Object, ObjectName> entry : this._beans.entrySet()) {
            final ObjectName bean = entry.getValue();
            if (bean.equals(oname)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public MBeanContainer(final MBeanServer server) {
        this._beans = new WeakHashMap<Object, ObjectName>();
        this._unique = new HashMap<String, Integer>();
        this._relations = new WeakHashMap<ObjectName, List<Container.Relationship>>();
        this._domain = null;
        this._server = server;
    }
    
    public MBeanServer getMBeanServer() {
        return this._server;
    }
    
    public void setDomain(final String domain) {
        this._domain = domain;
    }
    
    public String getDomain() {
        return this._domain;
    }
    
    public synchronized void add(final Container.Relationship relationship) {
        MBeanContainer.LOG.debug("add {}", relationship);
        ObjectName parent = this._beans.get(relationship.getParent());
        if (parent == null) {
            this.addBean(relationship.getParent());
            parent = this._beans.get(relationship.getParent());
        }
        ObjectName child = this._beans.get(relationship.getChild());
        if (child == null) {
            this.addBean(relationship.getChild());
            child = this._beans.get(relationship.getChild());
        }
        if (parent != null && child != null) {
            List<Container.Relationship> rels = this._relations.get(parent);
            if (rels == null) {
                rels = new ArrayList<Container.Relationship>();
                this._relations.put(parent, rels);
            }
            rels.add(relationship);
        }
    }
    
    public synchronized void remove(final Container.Relationship relationship) {
        MBeanContainer.LOG.debug("remove {}", relationship);
        final ObjectName parent = this._beans.get(relationship.getParent());
        final ObjectName child = this._beans.get(relationship.getChild());
        if (parent != null && child != null) {
            final List<Container.Relationship> rels = this._relations.get(parent);
            if (rels != null) {
                final Iterator<Container.Relationship> i = rels.iterator();
                while (i.hasNext()) {
                    final Container.Relationship r = i.next();
                    if (relationship.equals(r) || r.getChild() == null) {
                        i.remove();
                    }
                }
            }
        }
    }
    
    public synchronized void removeBean(final Object obj) {
        MBeanContainer.LOG.debug("removeBean {}", obj);
        final ObjectName bean = this._beans.remove(obj);
        if (bean != null) {
            final List<Container.Relationship> beanRelations = this._relations.remove(bean);
            if (beanRelations != null) {
                MBeanContainer.LOG.debug("Unregister {}", beanRelations);
                final List<?> removeList = new ArrayList<Object>(beanRelations);
                for (final Object r : removeList) {
                    final Container.Relationship relation = (Container.Relationship)r;
                    relation.getContainer().update(relation.getParent(), relation.getChild(), (Object)null, relation.getRelationship(), true);
                }
            }
            try {
                this._server.unregisterMBean(bean);
                MBeanContainer.LOG.debug("Unregistered {}", bean);
            }
            catch (InstanceNotFoundException e) {
                MBeanContainer.LOG.ignore(e);
            }
            catch (Exception e2) {
                MBeanContainer.LOG.warn(e2);
            }
        }
    }
    
    public synchronized void addBean(final Object obj) {
        MBeanContainer.LOG.debug("addBean {}", obj);
        try {
            if (obj == null || this._beans.containsKey(obj)) {
                return;
            }
            final Object mbean = ObjectMBean.mbeanFor(obj);
            if (mbean == null) {
                return;
            }
            ObjectName oname = null;
            if (mbean instanceof ObjectMBean) {
                ((ObjectMBean)mbean).setMBeanContainer(this);
                oname = ((ObjectMBean)mbean).getObjectName();
            }
            if (oname == null) {
                String type = obj.getClass().getName().toLowerCase();
                final int dot = type.lastIndexOf(46);
                if (dot >= 0) {
                    type = type.substring(dot + 1);
                }
                String context = null;
                if (mbean instanceof ObjectMBean) {
                    context = this.makeName(((ObjectMBean)mbean).getObjectContextBasis());
                }
                String name = null;
                if (mbean instanceof ObjectMBean) {
                    name = this.makeName(((ObjectMBean)mbean).getObjectNameBasis());
                }
                final StringBuffer buf = new StringBuffer();
                buf.append("type=").append(type);
                if (context != null && context.length() > 1) {
                    buf.append((buf.length() > 0) ? "," : "");
                    buf.append("context=").append(context);
                }
                if (name != null && name.length() > 1) {
                    buf.append((buf.length() > 0) ? "," : "");
                    buf.append("name=").append(name);
                }
                final String basis = buf.toString();
                Integer count = this._unique.get(basis);
                count = ((count == null) ? 0 : (1 + count));
                this._unique.put(basis, count);
                String domain = this._domain;
                if (domain == null) {
                    domain = obj.getClass().getPackage().getName();
                }
                oname = ObjectName.getInstance(domain + ":" + basis + ",id=" + count);
            }
            final ObjectInstance oinstance = this._server.registerMBean(mbean, oname);
            MBeanContainer.LOG.debug("Registered {}", oinstance.getObjectName());
            this._beans.put(obj, oinstance.getObjectName());
        }
        catch (Exception e) {
            MBeanContainer.LOG.warn("bean: " + obj, e);
        }
    }
    
    public String makeName(final String basis) {
        if (basis == null) {
            return basis;
        }
        return basis.replace(':', '_').replace('*', '_').replace('?', '_').replace('=', '_').replace(',', '_').replace(' ', '_');
    }
    
    public void doStart() {
        ShutdownThread.register(this);
    }
    
    public void doStop() {
        final Set<Object> removeSet = new HashSet<Object>(this._beans.keySet());
        for (final Object removeObj : removeSet) {
            this.removeBean(removeObj);
        }
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        AggregateLifeCycle.dumpObject(out, this);
        AggregateLifeCycle.dump(out, indent, this._beans.entrySet());
    }
    
    public String dump() {
        return AggregateLifeCycle.dump(this);
    }
    
    static {
        LOG = Log.getLogger(MBeanContainer.class.getName());
    }
}
