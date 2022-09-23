// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtensionRegistryLite
{
    private static volatile boolean eagerlyParseMessageSets;
    private final Map<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>> extensionsByNumber;
    private static final ExtensionRegistryLite EMPTY;
    
    public static boolean isEagerlyParseMessageSets() {
        return ExtensionRegistryLite.eagerlyParseMessageSets;
    }
    
    public static void setEagerlyParseMessageSets(final boolean isEagerlyParse) {
        ExtensionRegistryLite.eagerlyParseMessageSets = isEagerlyParse;
    }
    
    public static ExtensionRegistryLite newInstance() {
        return new ExtensionRegistryLite();
    }
    
    public static ExtensionRegistryLite getEmptyRegistry() {
        return ExtensionRegistryLite.EMPTY;
    }
    
    public ExtensionRegistryLite getUnmodifiable() {
        return new ExtensionRegistryLite(this);
    }
    
    public <ContainingType extends MessageLite> GeneratedMessageLite.GeneratedExtension<ContainingType, ?> findLiteExtensionByNumber(final ContainingType containingTypeDefaultInstance, final int fieldNumber) {
        return (GeneratedMessageLite.GeneratedExtension<ContainingType, ?>)this.extensionsByNumber.get(new ObjectIntPair(containingTypeDefaultInstance, fieldNumber));
    }
    
    public final void add(final GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
        this.extensionsByNumber.put(new ObjectIntPair(extension.getContainingTypeDefaultInstance(), extension.getNumber()), extension);
    }
    
    ExtensionRegistryLite() {
        this.extensionsByNumber = new HashMap<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>>();
    }
    
    ExtensionRegistryLite(final ExtensionRegistryLite other) {
        if (other == ExtensionRegistryLite.EMPTY) {
            this.extensionsByNumber = Collections.emptyMap();
        }
        else {
            this.extensionsByNumber = Collections.unmodifiableMap((Map<? extends ObjectIntPair, ? extends GeneratedMessageLite.GeneratedExtension<?, ?>>)other.extensionsByNumber);
        }
    }
    
    private ExtensionRegistryLite(final boolean empty) {
        this.extensionsByNumber = Collections.emptyMap();
    }
    
    static {
        ExtensionRegistryLite.eagerlyParseMessageSets = false;
        EMPTY = new ExtensionRegistryLite(true);
    }
    
    private static final class ObjectIntPair
    {
        private final Object object;
        private final int number;
        
        ObjectIntPair(final Object object, final int number) {
            this.object = object;
            this.number = number;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.object) * 65535 + this.number;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ObjectIntPair)) {
                return false;
            }
            final ObjectIntPair other = (ObjectIntPair)obj;
            return this.object == other.object && this.number == other.number;
        }
    }
}
