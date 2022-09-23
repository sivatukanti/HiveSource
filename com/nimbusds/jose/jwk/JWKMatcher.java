// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class JWKMatcher
{
    private final Set<KeyType> types;
    private final Set<KeyUse> uses;
    private final Set<KeyOperation> ops;
    private final Set<Algorithm> algs;
    private final Set<String> ids;
    private final boolean hasUse;
    private final boolean hasID;
    private final boolean privateOnly;
    private final boolean publicOnly;
    private final int minSizeBits;
    private final int maxSizeBits;
    private final Set<Integer> sizesBits;
    private final Set<ECKey.Curve> curves;
    
    @Deprecated
    public JWKMatcher(final Set<KeyType> types, final Set<KeyUse> uses, final Set<KeyOperation> ops, final Set<Algorithm> algs, final Set<String> ids, final boolean privateOnly, final boolean publicOnly) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, 0, 0);
    }
    
    @Deprecated
    public JWKMatcher(final Set<KeyType> types, final Set<KeyUse> uses, final Set<KeyOperation> ops, final Set<Algorithm> algs, final Set<String> ids, final boolean privateOnly, final boolean publicOnly, final int minSizeBits, final int maxSizeBits) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null);
    }
    
    @Deprecated
    public JWKMatcher(final Set<KeyType> types, final Set<KeyUse> uses, final Set<KeyOperation> ops, final Set<Algorithm> algs, final Set<String> ids, final boolean privateOnly, final boolean publicOnly, final int minSizeBits, final int maxSizeBits, final Set<ECKey.Curve> curves) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null, curves);
    }
    
    @Deprecated
    public JWKMatcher(final Set<KeyType> types, final Set<KeyUse> uses, final Set<KeyOperation> ops, final Set<Algorithm> algs, final Set<String> ids, final boolean privateOnly, final boolean publicOnly, final int minSizeBits, final int maxSizeBits, final Set<Integer> sizesBits, final Set<ECKey.Curve> curves) {
        this(types, uses, ops, algs, ids, false, false, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves);
    }
    
    public JWKMatcher(final Set<KeyType> types, final Set<KeyUse> uses, final Set<KeyOperation> ops, final Set<Algorithm> algs, final Set<String> ids, final boolean hasUse, final boolean hasID, final boolean privateOnly, final boolean publicOnly, final int minSizeBits, final int maxSizeBits, final Set<Integer> sizesBits, final Set<ECKey.Curve> curves) {
        this.types = types;
        this.uses = uses;
        this.ops = ops;
        this.algs = algs;
        this.ids = ids;
        this.hasUse = hasUse;
        this.hasID = hasID;
        this.privateOnly = privateOnly;
        this.publicOnly = publicOnly;
        this.minSizeBits = minSizeBits;
        this.maxSizeBits = maxSizeBits;
        this.sizesBits = sizesBits;
        this.curves = curves;
    }
    
    public Set<KeyType> getKeyTypes() {
        return this.types;
    }
    
    public Set<KeyUse> getKeyUses() {
        return this.uses;
    }
    
    public Set<KeyOperation> getKeyOperations() {
        return this.ops;
    }
    
    public Set<Algorithm> getAlgorithms() {
        return this.algs;
    }
    
    public Set<String> getKeyIDs() {
        return this.ids;
    }
    
    public boolean hasKeyUse() {
        return this.hasUse;
    }
    
    public boolean hasKeyID() {
        return this.hasID;
    }
    
    public boolean isPrivateOnly() {
        return this.privateOnly;
    }
    
    public boolean isPublicOnly() {
        return this.publicOnly;
    }
    
    @Deprecated
    public int getMinSize() {
        return this.getMinKeySize();
    }
    
    public int getMinKeySize() {
        return this.minSizeBits;
    }
    
    @Deprecated
    public int getMaxSize() {
        return this.getMaxKeySize();
    }
    
    public int getMaxKeySize() {
        return this.maxSizeBits;
    }
    
    public Set<Integer> getKeySizes() {
        return this.sizesBits;
    }
    
    public Set<ECKey.Curve> getCurves() {
        return this.curves;
    }
    
    public boolean matches(final JWK key) {
        if (this.hasUse && key.getKeyUse() == null) {
            return false;
        }
        if (this.hasID && (key.getKeyID() == null || key.getKeyID().trim().isEmpty())) {
            return false;
        }
        if (this.privateOnly && !key.isPrivate()) {
            return false;
        }
        if (this.publicOnly && key.isPrivate()) {
            return false;
        }
        if (this.types != null && !this.types.contains(key.getKeyType())) {
            return false;
        }
        if (this.uses != null && !this.uses.contains(key.getKeyUse())) {
            return false;
        }
        if (this.ops != null && (!this.ops.contains(null) || key.getKeyOperations() != null) && (key.getKeyOperations() == null || !this.ops.containsAll(key.getKeyOperations()))) {
            return false;
        }
        if (this.algs != null && !this.algs.contains(key.getAlgorithm())) {
            return false;
        }
        if (this.ids != null && !this.ids.contains(key.getKeyID())) {
            return false;
        }
        if (this.minSizeBits > 0 && key.size() < this.minSizeBits) {
            return false;
        }
        if (this.maxSizeBits > 0 && key.size() > this.maxSizeBits) {
            return false;
        }
        if (this.sizesBits != null && !this.sizesBits.contains(key.size())) {
            return false;
        }
        if (this.curves != null) {
            if (!(key instanceof ECKey)) {
                return false;
            }
            final ECKey ecKey = (ECKey)key;
            if (!this.curves.contains(ecKey.getCurve())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        append(sb, "kty", this.types);
        append(sb, "use", this.uses);
        append(sb, "key_ops", this.ops);
        append(sb, "alg", this.algs);
        append(sb, "kid", this.ids);
        if (this.hasUse) {
            sb.append("has_use=true ");
        }
        if (this.hasID) {
            sb.append("has_id=true ");
        }
        if (this.privateOnly) {
            sb.append("private_only=true ");
        }
        if (this.publicOnly) {
            sb.append("public_only=true ");
        }
        if (this.minSizeBits > 0) {
            sb.append("min_size=" + this.minSizeBits + " ");
        }
        if (this.maxSizeBits > 0) {
            sb.append("max_size=" + this.maxSizeBits + " ");
        }
        append(sb, "size", this.sizesBits);
        append(sb, "crv", this.curves);
        return sb.toString().trim();
    }
    
    private static void append(final StringBuilder sb, final String key, final Set<?> values) {
        if (values != null) {
            sb.append(key);
            sb.append('=');
            if (values.size() == 1) {
                final Object value = values.iterator().next();
                if (value == null) {
                    sb.append("ANY");
                }
                else {
                    sb.append(value.toString().trim());
                }
            }
            else {
                sb.append(values.toString().trim());
            }
            sb.append(' ');
        }
    }
    
    public static class Builder
    {
        private Set<KeyType> types;
        private Set<KeyUse> uses;
        private Set<KeyOperation> ops;
        private Set<Algorithm> algs;
        private Set<String> ids;
        private boolean hasUse;
        private boolean hasID;
        private boolean privateOnly;
        private boolean publicOnly;
        private int minSizeBits;
        private int maxSizeBits;
        private Set<Integer> sizesBits;
        private Set<ECKey.Curve> curves;
        
        public Builder() {
            this.hasUse = false;
            this.hasID = false;
            this.privateOnly = false;
            this.publicOnly = false;
            this.minSizeBits = 0;
            this.maxSizeBits = 0;
        }
        
        public Builder keyType(final KeyType kty) {
            if (kty == null) {
                this.types = null;
            }
            else {
                this.types = new HashSet<KeyType>(Collections.singletonList(kty));
            }
            return this;
        }
        
        public Builder keyTypes(final KeyType... types) {
            this.keyTypes(new LinkedHashSet<KeyType>(Arrays.asList(types)));
            return this;
        }
        
        public Builder keyTypes(final Set<KeyType> types) {
            this.types = types;
            return this;
        }
        
        public Builder keyUse(final KeyUse use) {
            if (use == null) {
                this.uses = null;
            }
            else {
                this.uses = new HashSet<KeyUse>(Collections.singletonList(use));
            }
            return this;
        }
        
        public Builder keyUses(final KeyUse... uses) {
            this.keyUses(new LinkedHashSet<KeyUse>(Arrays.asList(uses)));
            return this;
        }
        
        public Builder keyUses(final Set<KeyUse> uses) {
            this.uses = uses;
            return this;
        }
        
        public Builder keyOperation(final KeyOperation op) {
            if (op == null) {
                this.ops = null;
            }
            else {
                this.ops = new HashSet<KeyOperation>(Collections.singletonList(op));
            }
            return this;
        }
        
        public Builder keyOperations(final KeyOperation... ops) {
            this.keyOperations(new LinkedHashSet<KeyOperation>(Arrays.asList(ops)));
            return this;
        }
        
        public Builder keyOperations(final Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }
        
        public Builder algorithm(final Algorithm alg) {
            if (alg == null) {
                this.algs = null;
            }
            else {
                this.algs = new HashSet<Algorithm>(Collections.singletonList(alg));
            }
            return this;
        }
        
        public Builder algorithms(final Algorithm... algs) {
            this.algorithms(new LinkedHashSet<Algorithm>(Arrays.asList(algs)));
            return this;
        }
        
        public Builder algorithms(final Set<Algorithm> algs) {
            this.algs = algs;
            return this;
        }
        
        public Builder keyID(final String id) {
            if (id == null) {
                this.ids = null;
            }
            else {
                this.ids = new HashSet<String>(Collections.singletonList(id));
            }
            return this;
        }
        
        public Builder keyIDs(final String... ids) {
            this.keyIDs(new LinkedHashSet<String>(Arrays.asList(ids)));
            return this;
        }
        
        public Builder keyIDs(final Set<String> ids) {
            this.ids = ids;
            return this;
        }
        
        public Builder hasKeyUse(final boolean hasUse) {
            this.hasUse = hasUse;
            return this;
        }
        
        public Builder hasKeyID(final boolean hasID) {
            this.hasID = hasID;
            return this;
        }
        
        public Builder privateOnly(final boolean privateOnly) {
            this.privateOnly = privateOnly;
            return this;
        }
        
        public Builder publicOnly(final boolean publicOnly) {
            this.publicOnly = publicOnly;
            return this;
        }
        
        public Builder minKeySize(final int minSizeBits) {
            this.minSizeBits = minSizeBits;
            return this;
        }
        
        public Builder maxKeySize(final int maxSizeBits) {
            this.maxSizeBits = maxSizeBits;
            return this;
        }
        
        public Builder keySize(final int keySizeBits) {
            if (keySizeBits <= 0) {
                this.sizesBits = null;
            }
            else {
                this.sizesBits = Collections.singleton(keySizeBits);
            }
            return this;
        }
        
        public Builder keySizes(final int... keySizesBits) {
            final Set<Integer> sizesSet = new LinkedHashSet<Integer>();
            for (final int keySize : keySizesBits) {
                sizesSet.add(keySize);
            }
            this.keySizes(sizesSet);
            return this;
        }
        
        public Builder keySizes(final Set<Integer> keySizesBits) {
            this.sizesBits = keySizesBits;
            return this;
        }
        
        public Builder curve(final ECKey.Curve curve) {
            if (curve == null) {
                this.curves = null;
            }
            else {
                this.curves = new HashSet<ECKey.Curve>(Collections.singletonList(curve));
            }
            return this;
        }
        
        public Builder curves(final ECKey.Curve... curves) {
            this.curves(new LinkedHashSet<ECKey.Curve>(Arrays.asList(curves)));
            return this;
        }
        
        public Builder curves(final Set<ECKey.Curve> curves) {
            this.curves = curves;
            return this;
        }
        
        public JWKMatcher build() {
            return new JWKMatcher(this.types, this.uses, this.ops, this.algs, this.ids, this.hasUse, this.hasID, this.privateOnly, this.publicOnly, this.minSizeBits, this.maxSizeBits, this.sizesBits, this.curves);
        }
    }
}
