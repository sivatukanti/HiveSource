// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.bindings;

import java.util.Iterator;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.graph.Node;
import java.util.LinkedList;
import org.eclipse.jetty.deploy.AppLifeCycle;

public class OrderedGroupBinding implements AppLifeCycle.Binding
{
    private String[] _bindingTargets;
    private LinkedList<AppLifeCycle.Binding> _orderedBindings;
    
    public OrderedGroupBinding(final String[] bindingTargets) {
        this._bindingTargets = bindingTargets;
    }
    
    public void addBinding(final AppLifeCycle.Binding binding) {
        if (this._orderedBindings == null) {
            this._orderedBindings = new LinkedList<AppLifeCycle.Binding>();
        }
        this._orderedBindings.add(binding);
    }
    
    public void addBindings(final AppLifeCycle.Binding[] bindings) {
        if (this._orderedBindings == null) {
            this._orderedBindings = new LinkedList<AppLifeCycle.Binding>();
        }
        for (final AppLifeCycle.Binding binding : bindings) {
            this._orderedBindings.add(binding);
        }
    }
    
    public String[] getBindingTargets() {
        return this._bindingTargets;
    }
    
    public void processBinding(final Node node, final App app) throws Exception {
        for (final AppLifeCycle.Binding binding : this._orderedBindings) {
            binding.processBinding(node, app);
        }
    }
}
