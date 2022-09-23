// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;

public class POJOPropertyBuilder extends BeanPropertyDefinition implements Comparable<POJOPropertyBuilder>
{
    protected final boolean _forSerialization;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final PropertyName _name;
    protected final PropertyName _internalName;
    protected Linked<AnnotatedField> _fields;
    protected Linked<AnnotatedParameter> _ctorParameters;
    protected Linked<AnnotatedMethod> _getters;
    protected Linked<AnnotatedMethod> _setters;
    
    public POJOPropertyBuilder(final PropertyName internalName, final AnnotationIntrospector ai, final boolean forSerialization) {
        this(internalName, internalName, ai, forSerialization);
    }
    
    protected POJOPropertyBuilder(final PropertyName internalName, final PropertyName name, final AnnotationIntrospector annotationIntrospector, final boolean forSerialization) {
        this._internalName = internalName;
        this._name = name;
        this._annotationIntrospector = annotationIntrospector;
        this._forSerialization = forSerialization;
    }
    
    @Deprecated
    public POJOPropertyBuilder(final String simpleInternalName, final AnnotationIntrospector annotationIntrospector, final boolean forSerialization) {
        this(new PropertyName(simpleInternalName), annotationIntrospector, forSerialization);
    }
    
    public POJOPropertyBuilder(final POJOPropertyBuilder src, final PropertyName newName) {
        this._internalName = src._internalName;
        this._name = newName;
        this._annotationIntrospector = src._annotationIntrospector;
        this._fields = src._fields;
        this._ctorParameters = src._ctorParameters;
        this._getters = src._getters;
        this._setters = src._setters;
        this._forSerialization = src._forSerialization;
    }
    
    @Deprecated
    @Override
    public POJOPropertyBuilder withName(final String newName) {
        return this.withSimpleName(newName);
    }
    
    @Override
    public POJOPropertyBuilder withName(final PropertyName newName) {
        return new POJOPropertyBuilder(this, newName);
    }
    
    @Override
    public POJOPropertyBuilder withSimpleName(final String newSimpleName) {
        final PropertyName newName = this._name.withSimpleName(newSimpleName);
        return (newName == this._name) ? this : new POJOPropertyBuilder(this, newName);
    }
    
    @Override
    public int compareTo(final POJOPropertyBuilder other) {
        if (this._ctorParameters != null) {
            if (other._ctorParameters == null) {
                return -1;
            }
        }
        else if (other._ctorParameters != null) {
            return 1;
        }
        return this.getName().compareTo(other.getName());
    }
    
    @Override
    public String getName() {
        return (this._name == null) ? null : this._name.getSimpleName();
    }
    
    @Override
    public PropertyName getFullName() {
        return this._name;
    }
    
    @Override
    public String getInternalName() {
        return this._internalName.getSimpleName();
    }
    
    @Override
    public PropertyName getWrapperName() {
        final AnnotatedMember member = this.getPrimaryMember();
        return (member == null || this._annotationIntrospector == null) ? null : this._annotationIntrospector.findWrapperName(member);
    }
    
    @Override
    public boolean isExplicitlyIncluded() {
        return this._anyExplicits(this._fields) || this._anyExplicits(this._getters) || this._anyExplicits(this._setters) || this._anyExplicits(this._ctorParameters);
    }
    
    @Override
    public boolean isExplicitlyNamed() {
        return this._anyExplicitNames(this._fields) || this._anyExplicitNames(this._getters) || this._anyExplicitNames(this._setters) || this._anyExplicitNames(this._ctorParameters);
    }
    
    @Override
    public boolean hasGetter() {
        return this._getters != null;
    }
    
    @Override
    public boolean hasSetter() {
        return this._setters != null;
    }
    
    @Override
    public boolean hasField() {
        return this._fields != null;
    }
    
    @Override
    public boolean hasConstructorParameter() {
        return this._ctorParameters != null;
    }
    
    @Override
    public boolean couldDeserialize() {
        return this._ctorParameters != null || this._setters != null || this._fields != null;
    }
    
    @Override
    public boolean couldSerialize() {
        return this._getters != null || this._fields != null;
    }
    
    @Override
    public AnnotatedMethod getGetter() {
        Linked<AnnotatedMethod> curr = this._getters;
        if (curr == null) {
            return null;
        }
        Linked<AnnotatedMethod> next = curr.next;
        if (next == null) {
            return curr.value;
        }
        while (next != null) {
            final Class<?> currClass = curr.value.getDeclaringClass();
            final Class<?> nextClass = next.value.getDeclaringClass();
            Label_0198: {
                if (currClass != nextClass) {
                    if (currClass.isAssignableFrom(nextClass)) {
                        curr = next;
                        break Label_0198;
                    }
                    if (nextClass.isAssignableFrom(currClass)) {
                        break Label_0198;
                    }
                }
                final int priNext = this._getterPriority(next.value);
                final int priCurr = this._getterPriority(curr.value);
                if (priNext == priCurr) {
                    throw new IllegalArgumentException("Conflicting getter definitions for property \"" + this.getName() + "\": " + curr.value.getFullName() + " vs " + next.value.getFullName());
                }
                if (priNext < priCurr) {
                    curr = next;
                }
            }
            next = next.next;
        }
        this._getters = curr.withoutNext();
        return curr.value;
    }
    
    @Override
    public AnnotatedMethod getSetter() {
        Linked<AnnotatedMethod> curr = this._setters;
        if (curr == null) {
            return null;
        }
        Linked<AnnotatedMethod> next = curr.next;
        if (next == null) {
            return curr.value;
        }
        while (next != null) {
            final Class<?> currClass = curr.value.getDeclaringClass();
            final Class<?> nextClass = next.value.getDeclaringClass();
            Label_0198: {
                if (currClass != nextClass) {
                    if (currClass.isAssignableFrom(nextClass)) {
                        curr = next;
                        break Label_0198;
                    }
                    if (nextClass.isAssignableFrom(currClass)) {
                        break Label_0198;
                    }
                }
                final int priNext = this._setterPriority(next.value);
                final int priCurr = this._setterPriority(curr.value);
                if (priNext == priCurr) {
                    throw new IllegalArgumentException("Conflicting setter definitions for property \"" + this.getName() + "\": " + curr.value.getFullName() + " vs " + next.value.getFullName());
                }
                if (priNext < priCurr) {
                    curr = next;
                }
            }
            next = next.next;
        }
        this._setters = curr.withoutNext();
        return curr.value;
    }
    
    @Override
    public AnnotatedField getField() {
        if (this._fields == null) {
            return null;
        }
        AnnotatedField field = this._fields.value;
        Linked<AnnotatedField> next = this._fields.next;
        while (next != null) {
            final AnnotatedField nextField = next.value;
            final Class<?> fieldClass = field.getDeclaringClass();
            final Class<?> nextClass = nextField.getDeclaringClass();
            if (fieldClass != nextClass) {
                if (fieldClass.isAssignableFrom(nextClass)) {
                    field = nextField;
                }
                else if (!nextClass.isAssignableFrom(fieldClass)) {
                    throw new IllegalArgumentException("Multiple fields representing property \"" + this.getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
                }
                next = next.next;
                continue;
            }
            throw new IllegalArgumentException("Multiple fields representing property \"" + this.getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
        }
        return field;
    }
    
    @Override
    public AnnotatedParameter getConstructorParameter() {
        if (this._ctorParameters == null) {
            return null;
        }
        Linked<AnnotatedParameter> curr = this._ctorParameters;
        while (!(curr.value.getOwner() instanceof AnnotatedConstructor)) {
            curr = curr.next;
            if (curr == null) {
                return this._ctorParameters.value;
            }
        }
        return curr.value;
    }
    
    @Override
    public AnnotatedMember getAccessor() {
        AnnotatedMember m = this.getGetter();
        if (m == null) {
            m = this.getField();
        }
        return m;
    }
    
    @Override
    public AnnotatedMember getMutator() {
        AnnotatedMember m = this.getConstructorParameter();
        if (m == null) {
            m = this.getSetter();
            if (m == null) {
                m = this.getField();
            }
        }
        return m;
    }
    
    @Override
    public AnnotatedMember getNonConstructorMutator() {
        AnnotatedMember m = this.getSetter();
        if (m == null) {
            m = this.getField();
        }
        return m;
    }
    
    @Override
    public AnnotatedMember getPrimaryMember() {
        if (this._forSerialization) {
            return this.getAccessor();
        }
        return this.getMutator();
    }
    
    protected int _getterPriority(final AnnotatedMethod m) {
        final String name = m.getName();
        if (name.startsWith("get") && name.length() > 3) {
            return 1;
        }
        if (name.startsWith("is") && name.length() > 2) {
            return 2;
        }
        return 3;
    }
    
    protected int _setterPriority(final AnnotatedMethod m) {
        final String name = m.getName();
        if (name.startsWith("set") && name.length() > 3) {
            return 1;
        }
        return 2;
    }
    
    @Override
    public Class<?>[] findViews() {
        return (Class<?>[])this.fromMemberAnnotations((WithMember<Class[]>)new WithMember<Class<?>[]>() {
            @Override
            public Class<?>[] withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.findViews(member);
            }
        });
    }
    
    @Override
    public AnnotationIntrospector.ReferenceProperty findReferenceType() {
        return this.fromMemberAnnotations((WithMember<AnnotationIntrospector.ReferenceProperty>)new WithMember<AnnotationIntrospector.ReferenceProperty>() {
            @Override
            public AnnotationIntrospector.ReferenceProperty withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.findReferenceType(member);
            }
        });
    }
    
    @Override
    public boolean isTypeId() {
        final Boolean b = this.fromMemberAnnotations((WithMember<Boolean>)new WithMember<Boolean>() {
            @Override
            public Boolean withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.isTypeId(member);
            }
        });
        return b != null && b;
    }
    
    @Override
    public PropertyMetadata getMetadata() {
        final Boolean b = this._findRequired();
        final String desc = this._findDescription();
        final Integer idx = this._findIndex();
        if (b == null && idx == null) {
            return (desc == null) ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc);
        }
        return PropertyMetadata.construct(b, desc, idx);
    }
    
    protected Boolean _findRequired() {
        final Boolean b = this.fromMemberAnnotations((WithMember<Boolean>)new WithMember<Boolean>() {
            @Override
            public Boolean withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.hasRequiredMarker(member);
            }
        });
        return b;
    }
    
    protected String _findDescription() {
        return this.fromMemberAnnotations((WithMember<String>)new WithMember<String>() {
            @Override
            public String withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.findPropertyDescription(member);
            }
        });
    }
    
    protected Integer _findIndex() {
        return this.fromMemberAnnotations((WithMember<Integer>)new WithMember<Integer>() {
            @Override
            public Integer withMember(final AnnotatedMember member) {
                return POJOPropertyBuilder.this._annotationIntrospector.findPropertyIndex(member);
            }
        });
    }
    
    @Override
    public ObjectIdInfo findObjectIdInfo() {
        return this.fromMemberAnnotations((WithMember<ObjectIdInfo>)new WithMember<ObjectIdInfo>() {
            @Override
            public ObjectIdInfo withMember(final AnnotatedMember member) {
                ObjectIdInfo info = POJOPropertyBuilder.this._annotationIntrospector.findObjectIdInfo(member);
                if (info != null) {
                    info = POJOPropertyBuilder.this._annotationIntrospector.findObjectReferenceInfo(member, info);
                }
                return info;
            }
        });
    }
    
    public void addField(final AnnotatedField a, final PropertyName name, final boolean explName, final boolean visible, final boolean ignored) {
        this._fields = new Linked<AnnotatedField>(a, this._fields, name, explName, visible, ignored);
    }
    
    public void addCtor(final AnnotatedParameter a, final PropertyName name, final boolean explName, final boolean visible, final boolean ignored) {
        this._ctorParameters = new Linked<AnnotatedParameter>(a, this._ctorParameters, name, explName, visible, ignored);
    }
    
    public void addGetter(final AnnotatedMethod a, final PropertyName name, final boolean explName, final boolean visible, final boolean ignored) {
        this._getters = new Linked<AnnotatedMethod>(a, this._getters, name, explName, visible, ignored);
    }
    
    public void addSetter(final AnnotatedMethod a, final PropertyName name, final boolean explName, final boolean visible, final boolean ignored) {
        this._setters = new Linked<AnnotatedMethod>(a, this._setters, name, explName, visible, ignored);
    }
    
    public void addAll(final POJOPropertyBuilder src) {
        this._fields = merge(this._fields, src._fields);
        this._ctorParameters = merge(this._ctorParameters, src._ctorParameters);
        this._getters = merge(this._getters, src._getters);
        this._setters = merge(this._setters, src._setters);
    }
    
    private static <T> Linked<T> merge(final Linked<T> chain1, final Linked<T> chain2) {
        if (chain1 == null) {
            return chain2;
        }
        if (chain2 == null) {
            return chain1;
        }
        return (Linked<T>)((Linked<Object>)chain1).append((Linked<Object>)chain2);
    }
    
    @Deprecated
    public void addField(final AnnotatedField a, final String name, final boolean visible, final boolean ignored) {
        this.addField(a, this._propName(name), name != null, visible, ignored);
    }
    
    @Deprecated
    public void addField(final AnnotatedField a, final String name, final boolean explName, final boolean visible, final boolean ignored) {
        this.addField(a, this._propName(name), explName, visible, ignored);
    }
    
    @Deprecated
    public void addCtor(final AnnotatedParameter a, final String name, final boolean visible, final boolean ignored) {
        this.addCtor(a, this._propName(name), name != null, visible, ignored);
    }
    
    @Deprecated
    public void addCtor(final AnnotatedParameter a, final String name, final boolean explName, final boolean visible, final boolean ignored) {
        this.addCtor(a, this._propName(name), explName, visible, ignored);
    }
    
    @Deprecated
    public void addGetter(final AnnotatedMethod a, final String name, final boolean visible, final boolean ignored) {
        this.addGetter(a, this._propName(name), name != null, visible, ignored);
    }
    
    @Deprecated
    public void addGetter(final AnnotatedMethod a, final String name, final boolean explName, final boolean visible, final boolean ignored) {
        this.addGetter(a, this._propName(name), explName, visible, ignored);
    }
    
    @Deprecated
    public void addSetter(final AnnotatedMethod a, final String name, final boolean visible, final boolean ignored) {
        this.addSetter(a, this._propName(name), name != null, visible, ignored);
    }
    
    @Deprecated
    public void addSetter(final AnnotatedMethod a, final String name, final boolean explName, final boolean visible, final boolean ignored) {
        this.addSetter(a, this._propName(name), explName, visible, ignored);
    }
    
    private PropertyName _propName(final String simple) {
        return PropertyName.construct(simple, null);
    }
    
    public void removeIgnored() {
        this._fields = this._removeIgnored(this._fields);
        this._getters = this._removeIgnored(this._getters);
        this._setters = this._removeIgnored(this._setters);
        this._ctorParameters = this._removeIgnored(this._ctorParameters);
    }
    
    public void removeNonVisible(final boolean force) {
        this._getters = this._removeNonVisible(this._getters);
        this._ctorParameters = this._removeNonVisible(this._ctorParameters);
        if (force || this._getters == null) {
            this._fields = this._removeNonVisible(this._fields);
            this._setters = this._removeNonVisible(this._setters);
        }
    }
    
    public void trimByVisibility() {
        this._fields = this._trimByVisibility(this._fields);
        this._getters = this._trimByVisibility(this._getters);
        this._setters = this._trimByVisibility(this._setters);
        this._ctorParameters = this._trimByVisibility(this._ctorParameters);
    }
    
    public void mergeAnnotations(final boolean forSerialization) {
        if (forSerialization) {
            if (this._getters != null) {
                final AnnotationMap ann = this._mergeAnnotations(0, this._getters, this._fields, this._ctorParameters, this._setters);
                this._getters = this._getters.withValue(this._getters.value.withAnnotations(ann));
            }
            else if (this._fields != null) {
                final AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._ctorParameters, this._setters);
                this._fields = this._fields.withValue(this._fields.value.withAnnotations(ann));
            }
        }
        else if (this._ctorParameters != null) {
            final AnnotationMap ann = this._mergeAnnotations(0, this._ctorParameters, this._setters, this._fields, this._getters);
            this._ctorParameters = this._ctorParameters.withValue(this._ctorParameters.value.withAnnotations(ann));
        }
        else if (this._setters != null) {
            final AnnotationMap ann = this._mergeAnnotations(0, this._setters, this._fields, this._getters);
            this._setters = this._setters.withValue(this._setters.value.withAnnotations(ann));
        }
        else if (this._fields != null) {
            final AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._getters);
            this._fields = this._fields.withValue(this._fields.value.withAnnotations(ann));
        }
    }
    
    private AnnotationMap _mergeAnnotations(int index, final Linked<? extends AnnotatedMember>... nodes) {
        final AnnotationMap ann = ((AnnotatedMember)nodes[index].value).getAllAnnotations();
        ++index;
        while (index < nodes.length) {
            if (nodes[index] != null) {
                return AnnotationMap.merge(ann, this._mergeAnnotations(index, nodes));
            }
            ++index;
        }
        return ann;
    }
    
    private <T> Linked<T> _removeIgnored(final Linked<T> node) {
        if (node == null) {
            return node;
        }
        return node.withoutIgnored();
    }
    
    private <T> Linked<T> _removeNonVisible(final Linked<T> node) {
        if (node == null) {
            return node;
        }
        return node.withoutNonVisible();
    }
    
    private <T> Linked<T> _trimByVisibility(final Linked<T> node) {
        if (node == null) {
            return node;
        }
        return node.trimByVisibility();
    }
    
    private <T> boolean _anyExplicits(Linked<T> n) {
        while (n != null) {
            if (n.name != null && n.name.hasSimpleName()) {
                return true;
            }
            n = n.next;
        }
        return false;
    }
    
    private <T> boolean _anyExplicitNames(Linked<T> n) {
        while (n != null) {
            if (n.name != null && n.isNameExplicit) {
                return true;
            }
            n = n.next;
        }
        return false;
    }
    
    public boolean anyVisible() {
        return this._anyVisible(this._fields) || this._anyVisible(this._getters) || this._anyVisible(this._setters) || this._anyVisible(this._ctorParameters);
    }
    
    private <T> boolean _anyVisible(Linked<T> n) {
        while (n != null) {
            if (n.isVisible) {
                return true;
            }
            n = n.next;
        }
        return false;
    }
    
    public boolean anyIgnorals() {
        return this._anyIgnorals(this._fields) || this._anyIgnorals(this._getters) || this._anyIgnorals(this._setters) || this._anyIgnorals(this._ctorParameters);
    }
    
    private <T> boolean _anyIgnorals(Linked<T> n) {
        while (n != null) {
            if (n.isMarkedIgnored) {
                return true;
            }
            n = n.next;
        }
        return false;
    }
    
    @Deprecated
    public String findNewName() {
        final Collection<PropertyName> l = this.findExplicitNames();
        if (l == null) {
            return null;
        }
        if (l.size() > 1) {
            throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name '" + this._name + "'): found more than one explicit name: " + l);
        }
        final PropertyName first = l.iterator().next();
        if (first.equals(this._name)) {
            return null;
        }
        return first.getSimpleName();
    }
    
    public Set<PropertyName> findExplicitNames() {
        Set<PropertyName> renamed = null;
        renamed = this._findExplicitNames(this._fields, renamed);
        renamed = this._findExplicitNames(this._getters, renamed);
        renamed = this._findExplicitNames(this._setters, renamed);
        renamed = this._findExplicitNames(this._ctorParameters, renamed);
        if (renamed == null) {
            return Collections.emptySet();
        }
        return renamed;
    }
    
    public Collection<POJOPropertyBuilder> explode(final Collection<PropertyName> newNames) {
        final HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap<PropertyName, POJOPropertyBuilder>();
        this._explode(newNames, props, this._fields);
        this._explode(newNames, props, this._getters);
        this._explode(newNames, props, this._setters);
        this._explode(newNames, props, this._ctorParameters);
        return props.values();
    }
    
    private void _explode(final Collection<PropertyName> newNames, final Map<PropertyName, POJOPropertyBuilder> props, final Linked<?> accessors) {
        final Linked<?> firstAcc = accessors;
        for (Linked<?> node = accessors; node != null; node = node.next) {
            final PropertyName name = node.name;
            if (!node.isNameExplicit || name == null) {
                throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name '" + this._name + "'): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
            }
            POJOPropertyBuilder prop = props.get(name);
            if (prop == null) {
                prop = new POJOPropertyBuilder(this._internalName, name, this._annotationIntrospector, this._forSerialization);
                props.put(name, prop);
            }
            if (firstAcc == this._fields) {
                final Linked<AnnotatedField> n2 = (Linked<AnnotatedField>)node;
                prop._fields = n2.withNext(prop._fields);
            }
            else if (firstAcc == this._getters) {
                final Linked<AnnotatedMethod> n3 = (Linked<AnnotatedMethod>)node;
                prop._getters = n3.withNext(prop._getters);
            }
            else if (firstAcc == this._setters) {
                final Linked<AnnotatedMethod> n3 = (Linked<AnnotatedMethod>)node;
                prop._setters = n3.withNext(prop._setters);
            }
            else {
                if (firstAcc != this._ctorParameters) {
                    throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
                }
                final Linked<AnnotatedParameter> n4 = (Linked<AnnotatedParameter>)node;
                prop._ctorParameters = n4.withNext(prop._ctorParameters);
            }
        }
    }
    
    private Set<PropertyName> _findExplicitNames(Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed) {
        while (node != null) {
            if (node.isNameExplicit) {
                if (node.name != null) {
                    if (renamed == null) {
                        renamed = new HashSet<PropertyName>();
                    }
                    renamed.add(node.name);
                }
            }
            node = node.next;
        }
        return renamed;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Property '").append(this._name).append("'; ctors: ").append(this._ctorParameters).append(", field(s): ").append(this._fields).append(", getter(s): ").append(this._getters).append(", setter(s): ").append(this._setters);
        sb.append("]");
        return sb.toString();
    }
    
    protected <T> T fromMemberAnnotations(final WithMember<T> func) {
        T result = null;
        if (this._annotationIntrospector != null) {
            if (this._forSerialization) {
                if (this._getters != null) {
                    result = func.withMember(this._getters.value);
                }
            }
            else {
                if (this._ctorParameters != null) {
                    result = func.withMember(this._ctorParameters.value);
                }
                if (result == null && this._setters != null) {
                    result = func.withMember(this._setters.value);
                }
            }
            if (result == null && this._fields != null) {
                result = func.withMember(this._fields.value);
            }
        }
        return result;
    }
    
    private static final class Linked<T>
    {
        public final T value;
        public final Linked<T> next;
        public final PropertyName name;
        public final boolean isNameExplicit;
        public final boolean isVisible;
        public final boolean isMarkedIgnored;
        
        public Linked(final T v, final Linked<T> n, final PropertyName name, boolean explName, final boolean visible, final boolean ignored) {
            this.value = v;
            this.next = n;
            this.name = ((name == null || name.isEmpty()) ? null : name);
            if (explName) {
                if (this.name == null) {
                    throw new IllegalArgumentException("Can not pass true for 'explName' if name is null/empty");
                }
                if (!name.hasSimpleName()) {
                    explName = false;
                }
            }
            this.isNameExplicit = explName;
            this.isVisible = visible;
            this.isMarkedIgnored = ignored;
        }
        
        public Linked<T> withoutNext() {
            if (this.next == null) {
                return this;
            }
            return new Linked<T>(this.value, null, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
        }
        
        public Linked<T> withValue(final T newValue) {
            if (newValue == this.value) {
                return this;
            }
            return new Linked<T>(newValue, this.next, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
        }
        
        public Linked<T> withNext(final Linked<T> newNext) {
            if (newNext == this.next) {
                return this;
            }
            return new Linked<T>(this.value, newNext, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
        }
        
        public Linked<T> withoutIgnored() {
            if (this.isMarkedIgnored) {
                return (this.next == null) ? null : this.next.withoutIgnored();
            }
            if (this.next != null) {
                final Linked<T> newNext = this.next.withoutIgnored();
                if (newNext != this.next) {
                    return this.withNext(newNext);
                }
            }
            return this;
        }
        
        public Linked<T> withoutNonVisible() {
            final Linked<T> newNext = (this.next == null) ? null : this.next.withoutNonVisible();
            return this.isVisible ? this.withNext(newNext) : newNext;
        }
        
        private Linked<T> append(final Linked<T> appendable) {
            if (this.next == null) {
                return this.withNext(appendable);
            }
            return this.withNext(this.next.append(appendable));
        }
        
        public Linked<T> trimByVisibility() {
            if (this.next == null) {
                return this;
            }
            final Linked<T> newNext = this.next.trimByVisibility();
            if (this.name != null) {
                if (newNext.name == null) {
                    return this.withNext(null);
                }
                return this.withNext(newNext);
            }
            else {
                if (newNext.name != null) {
                    return newNext;
                }
                if (this.isVisible == newNext.isVisible) {
                    return this.withNext(newNext);
                }
                return this.isVisible ? this.withNext(null) : newNext;
            }
        }
        
        @Override
        public String toString() {
            String msg = this.value.toString() + "[visible=" + this.isVisible + ",ignore=" + this.isMarkedIgnored + ",explicitName=" + this.isNameExplicit + "]";
            if (this.next != null) {
                msg = msg + ", " + this.next.toString();
            }
            return msg;
        }
    }
    
    private interface WithMember<T>
    {
        T withMember(final AnnotatedMember p0);
    }
}
