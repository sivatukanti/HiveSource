// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.List;

public final class ImmutableNode
{
    private final String nodeName;
    private final Object value;
    private final List<ImmutableNode> children;
    private final Map<String, Object> attributes;
    
    private ImmutableNode(final Builder b) {
        this.children = b.createChildren();
        this.attributes = b.createAttributes();
        this.nodeName = b.name;
        this.value = b.value;
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public List<ImmutableNode> getChildren() {
        return this.children;
    }
    
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
    
    public ImmutableNode setName(final String name) {
        return new Builder((List)this.children, (Map)this.attributes).name(name).value(this.value).create();
    }
    
    public ImmutableNode setValue(final Object newValue) {
        return new Builder((List)this.children, (Map)this.attributes).name(this.nodeName).value(newValue).create();
    }
    
    public ImmutableNode addChild(final ImmutableNode child) {
        checkChildNode(child);
        final Builder builder = new Builder(this.children.size() + 1, (Map)this.attributes);
        builder.addChildren(this.children).addChild(child);
        return this.createWithBasicProperties(builder);
    }
    
    public ImmutableNode removeChild(final ImmutableNode child) {
        final Builder builder = new Builder(this.children.size(), (Map)this.attributes);
        boolean foundChild = false;
        for (final ImmutableNode c : this.children) {
            if (c == child) {
                foundChild = true;
            }
            else {
                builder.addChild(c);
            }
        }
        return foundChild ? this.createWithBasicProperties(builder) : this;
    }
    
    public ImmutableNode replaceChild(final ImmutableNode oldChild, final ImmutableNode newChild) {
        checkChildNode(newChild);
        final Builder builder = new Builder(this.children.size(), (Map)this.attributes);
        boolean foundChild = false;
        for (final ImmutableNode c : this.children) {
            if (c == oldChild) {
                builder.addChild(newChild);
                foundChild = true;
            }
            else {
                builder.addChild(c);
            }
        }
        return foundChild ? this.createWithBasicProperties(builder) : this;
    }
    
    public ImmutableNode replaceChildren(final Collection<ImmutableNode> newChildren) {
        final Builder builder = new Builder((List)null, (Map)this.attributes);
        builder.addChildren(newChildren);
        return this.createWithBasicProperties(builder);
    }
    
    public ImmutableNode setAttribute(final String name, final Object value) {
        final Map<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        newAttrs.put(name, value);
        return this.createWithNewAttributes(newAttrs);
    }
    
    public ImmutableNode setAttributes(final Map<String, ?> newAttributes) {
        if (newAttributes == null || newAttributes.isEmpty()) {
            return this;
        }
        final Map<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        newAttrs.putAll(newAttributes);
        return this.createWithNewAttributes(newAttrs);
    }
    
    public ImmutableNode removeAttribute(final String name) {
        final Map<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        if (newAttrs.remove(name) != null) {
            return this.createWithNewAttributes(newAttrs);
        }
        return this;
    }
    
    private ImmutableNode createWithBasicProperties(final Builder builder) {
        return builder.name(this.nodeName).value(this.value).create();
    }
    
    private ImmutableNode createWithNewAttributes(final Map<String, Object> newAttrs) {
        return this.createWithBasicProperties(new Builder((List)this.children, (Map)null).addAttributes(newAttrs));
    }
    
    private static void checkChildNode(final ImmutableNode child) {
        if (child == null) {
            throw new IllegalArgumentException("Child node must not be null!");
        }
    }
    
    public static final class Builder
    {
        private final List<ImmutableNode> directChildren;
        private final Map<String, Object> directAttributes;
        private List<ImmutableNode> children;
        private Map<String, Object> attributes;
        private String name;
        private Object value;
        
        public Builder() {
            this(null, null);
        }
        
        public Builder(final int childCount) {
            this();
            this.initChildrenCollection(childCount);
        }
        
        private Builder(final List<ImmutableNode> dirChildren, final Map<String, Object> dirAttrs) {
            this.directChildren = dirChildren;
            this.directAttributes = dirAttrs;
        }
        
        private Builder(final int childCount, final Map<String, Object> dirAttrs) {
            this(null, dirAttrs);
            this.initChildrenCollection(childCount);
        }
        
        public Builder name(final String n) {
            this.name = n;
            return this;
        }
        
        public Builder value(final Object v) {
            this.value = v;
            return this;
        }
        
        public Builder addChild(final ImmutableNode c) {
            if (c != null) {
                this.ensureChildrenExist();
                this.children.add(c);
            }
            return this;
        }
        
        public Builder addChildren(final Collection<? extends ImmutableNode> children) {
            if (children != null) {
                this.ensureChildrenExist();
                this.children.addAll(filterNull(children));
            }
            return this;
        }
        
        public Builder addAttribute(final String name, final Object value) {
            this.ensureAttributesExist();
            this.attributes.put(name, value);
            return this;
        }
        
        public Builder addAttributes(final Map<String, ?> attrs) {
            if (attrs != null) {
                this.ensureAttributesExist();
                this.attributes.putAll(attrs);
            }
            return this;
        }
        
        public ImmutableNode create() {
            final ImmutableNode newNode = new ImmutableNode(this, null);
            this.children = null;
            this.attributes = null;
            return newNode;
        }
        
        List<ImmutableNode> createChildren() {
            if (this.directChildren != null) {
                return this.directChildren;
            }
            if (this.children != null) {
                return Collections.unmodifiableList((List<? extends ImmutableNode>)this.children);
            }
            return Collections.emptyList();
        }
        
        private Map<String, Object> createAttributes() {
            if (this.directAttributes != null) {
                return this.directAttributes;
            }
            if (this.attributes != null) {
                return Collections.unmodifiableMap((Map<? extends String, ?>)this.attributes);
            }
            return Collections.emptyMap();
        }
        
        private void ensureChildrenExist() {
            if (this.children == null) {
                this.children = new LinkedList<ImmutableNode>();
            }
        }
        
        private void ensureAttributesExist() {
            if (this.attributes == null) {
                this.attributes = new HashMap<String, Object>();
            }
        }
        
        private void initChildrenCollection(final int childCount) {
            if (childCount > 0) {
                this.children = new ArrayList<ImmutableNode>(childCount);
            }
        }
        
        private static Collection<? extends ImmutableNode> filterNull(final Collection<? extends ImmutableNode> children) {
            final List<ImmutableNode> result = new ArrayList<ImmutableNode>(children.size());
            for (final ImmutableNode c : children) {
                if (c != null) {
                    result.add(c);
                }
            }
            return result;
        }
    }
}
