// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import org.eclipse.jetty.util.resource.Resource;
import java.util.List;

public interface Ordering
{
    List<Resource> order(final List<Resource> p0);
    
    boolean isAbsolute();
    
    boolean hasOther();
    
    public static class AbsoluteOrdering implements Ordering
    {
        public static final String OTHER = "@@-OTHER-@@";
        protected List<String> _order;
        protected boolean _hasOther;
        protected MetaData _metaData;
        
        public AbsoluteOrdering(final MetaData metaData) {
            this._order = new ArrayList<String>();
            this._hasOther = false;
            this._metaData = metaData;
        }
        
        @Override
        public List<Resource> order(final List<Resource> jars) {
            final List<Resource> orderedList = new ArrayList<Resource>();
            final List<Resource> tmp = new ArrayList<Resource>(jars);
            final Map<String, FragmentDescriptor> others = new HashMap<String, FragmentDescriptor>(this._metaData.getNamedFragments());
            int index = -1;
            for (final String item : this._order) {
                if (!item.equals("@@-OTHER-@@")) {
                    final FragmentDescriptor f = others.remove(item);
                    if (f == null) {
                        continue;
                    }
                    final Resource jar = this._metaData.getJarForFragment(item);
                    orderedList.add(jar);
                    tmp.remove(jar);
                }
                else {
                    index = orderedList.size();
                }
            }
            if (this._hasOther) {
                orderedList.addAll((index < 0) ? 0 : index, tmp);
            }
            return orderedList;
        }
        
        @Override
        public boolean isAbsolute() {
            return true;
        }
        
        public void add(final String name) {
            this._order.add(name);
        }
        
        public void addOthers() {
            if (this._hasOther) {
                throw new IllegalStateException("Duplicate <other> element in absolute ordering");
            }
            this._hasOther = true;
            this._order.add("@@-OTHER-@@");
        }
        
        @Override
        public boolean hasOther() {
            return this._hasOther;
        }
    }
    
    public static class RelativeOrdering implements Ordering
    {
        protected MetaData _metaData;
        protected LinkedList<Resource> _beforeOthers;
        protected LinkedList<Resource> _afterOthers;
        protected LinkedList<Resource> _noOthers;
        
        public RelativeOrdering(final MetaData metaData) {
            this._beforeOthers = new LinkedList<Resource>();
            this._afterOthers = new LinkedList<Resource>();
            this._noOthers = new LinkedList<Resource>();
            this._metaData = metaData;
        }
        
        @Override
        public List<Resource> order(final List<Resource> jars) {
            this._beforeOthers.clear();
            this._afterOthers.clear();
            this._noOthers.clear();
            for (final Resource jar : jars) {
                final FragmentDescriptor descriptor = this._metaData.getFragment(jar);
                if (descriptor != null) {
                    switch (descriptor.getOtherType()) {
                        case None: {
                            this.addNoOthers(jar);
                            continue;
                        }
                        case Before: {
                            this.addBeforeOthers(jar);
                            continue;
                        }
                        case After: {
                            this.addAfterOthers(jar);
                            continue;
                        }
                    }
                }
                else {
                    this.addNoOthers(jar);
                }
            }
            final List<Resource> orderedList = new ArrayList<Resource>();
            int maxIterations = 2;
            boolean done = false;
            do {
                final boolean changesBefore = this.orderList(this._beforeOthers);
                final boolean changesAfter = this.orderList(this._afterOthers);
                final boolean changesNone = this.orderList(this._noOthers);
                done = (!changesBefore && !changesAfter && !changesNone);
            } while (!done && --maxIterations > 0);
            if (!done) {
                throw new IllegalStateException("Circular references for fragments");
            }
            for (final Resource r : this._beforeOthers) {
                orderedList.add(r);
            }
            for (final Resource r : this._noOthers) {
                orderedList.add(r);
            }
            for (final Resource r : this._afterOthers) {
                orderedList.add(r);
            }
            return orderedList;
        }
        
        @Override
        public boolean isAbsolute() {
            return false;
        }
        
        @Override
        public boolean hasOther() {
            return !this._beforeOthers.isEmpty() || !this._afterOthers.isEmpty();
        }
        
        public void addBeforeOthers(final Resource r) {
            this._beforeOthers.addLast(r);
        }
        
        public void addAfterOthers(final Resource r) {
            this._afterOthers.addLast(r);
        }
        
        public void addNoOthers(final Resource r) {
            this._noOthers.addLast(r);
        }
        
        protected boolean orderList(final LinkedList<Resource> list) {
            boolean changes = false;
            final List<Resource> iterable = new ArrayList<Resource>(list);
            for (final Resource r : iterable) {
                final FragmentDescriptor f = this._metaData.getFragment(r);
                if (f == null) {
                    continue;
                }
                final List<String> befores = f.getBefores();
                if (befores != null && !befores.isEmpty()) {
                    for (final String b : befores) {
                        if (!this.isBefore(list, f.getName(), b)) {
                            final int idx1 = this.getIndexOf(list, f.getName());
                            final int idx2 = this.getIndexOf(list, b);
                            if (idx2 < 0) {
                                changes = true;
                                final Resource bResource = this._metaData.getJarForFragment(b);
                                if (bResource == null || !this._noOthers.remove(bResource)) {
                                    continue;
                                }
                                this.insert(list, idx1 + 1, b);
                            }
                            else {
                                list.remove(idx1);
                                this.insert(list, idx2, f.getName());
                                changes = true;
                            }
                        }
                    }
                }
                final List<String> afters = f.getAfters();
                if (afters == null || afters.isEmpty()) {
                    continue;
                }
                for (final String a : afters) {
                    if (!this.isAfter(list, f.getName(), a)) {
                        final int idx3 = this.getIndexOf(list, f.getName());
                        final int idx4 = this.getIndexOf(list, a);
                        if (idx4 < 0) {
                            changes = true;
                            final Resource aResource = this._metaData.getJarForFragment(a);
                            if (aResource == null || !this._noOthers.remove(aResource)) {
                                continue;
                            }
                            this.insert(list, idx3, aResource);
                        }
                        else {
                            list.remove(idx4);
                            this.insert(list, idx3, a);
                            changes = true;
                        }
                    }
                }
            }
            return changes;
        }
        
        protected boolean isBefore(final List<Resource> list, final String fragNameA, final String fragNameB) {
            final int idxa = this.getIndexOf(list, fragNameA);
            final int idxb = this.getIndexOf(list, fragNameB);
            if (idxb >= 0 && idxb < idxa) {
                return false;
            }
            if (idxb < 0) {
                if (list == this._beforeOthers) {
                    return true;
                }
                if (list == this._afterOthers) {
                    if (this._beforeOthers.contains(fragNameB)) {
                        throw new IllegalStateException("Incorrect relationship: " + fragNameA + " before " + fragNameB);
                    }
                    return false;
                }
            }
            return true;
        }
        
        protected boolean isAfter(final List<Resource> list, final String fragNameA, final String fragNameB) {
            final int idxa = this.getIndexOf(list, fragNameA);
            final int idxb = this.getIndexOf(list, fragNameB);
            if (idxb >= 0 && idxa < idxb) {
                return false;
            }
            if (idxb < 0) {
                if (list == this._afterOthers) {
                    return true;
                }
                if (list == this._beforeOthers) {
                    if (this._afterOthers.contains(fragNameB)) {
                        throw new IllegalStateException("Incorrect relationship: " + fragNameB + " after " + fragNameA);
                    }
                    return false;
                }
            }
            return true;
        }
        
        protected void insert(final List<Resource> list, final int index, final String fragName) {
            final Resource jar = this._metaData.getJarForFragment(fragName);
            if (jar == null) {
                throw new IllegalStateException("No jar for insertion");
            }
            this.insert(list, index, jar);
        }
        
        protected void insert(final List<Resource> list, final int index, final Resource resource) {
            if (list == null) {
                throw new IllegalStateException("List is null for insertion");
            }
            if (index > list.size()) {
                list.add(resource);
            }
            else {
                list.add(index, resource);
            }
        }
        
        protected void remove(final List<Resource> resources, final Resource r) {
            if (resources == null) {
                return;
            }
            resources.remove(r);
        }
        
        protected int getIndexOf(final List<Resource> resources, final String fragmentName) {
            final FragmentDescriptor fd = this._metaData.getFragment(fragmentName);
            if (fd == null) {
                return -1;
            }
            final Resource r = this._metaData.getJarForFragment(fragmentName);
            if (r == null) {
                return -1;
            }
            return resources.indexOf(r);
        }
    }
}
