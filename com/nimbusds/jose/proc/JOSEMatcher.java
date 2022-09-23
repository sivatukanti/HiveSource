// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Iterator;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWEObject;
import java.net.URI;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEObject;
import java.util.Set;

public class JOSEMatcher
{
    private final Set<Class<? extends JOSEObject>> classes;
    private final Set<Algorithm> algs;
    private final Set<EncryptionMethod> encs;
    private final Set<URI> jkus;
    private final Set<String> kids;
    
    public JOSEMatcher(final Set<Class<? extends JOSEObject>> classes, final Set<Algorithm> algs, final Set<EncryptionMethod> encs, final Set<URI> jkus, final Set<String> kids) {
        this.classes = classes;
        this.algs = algs;
        this.encs = encs;
        this.jkus = jkus;
        this.kids = kids;
    }
    
    public Set<Class<? extends JOSEObject>> getJOSEClasses() {
        return this.classes;
    }
    
    public Set<Algorithm> getAlgorithms() {
        return this.algs;
    }
    
    public Set<EncryptionMethod> getEncryptionMethods() {
        return this.encs;
    }
    
    public Set<URI> getJWKURLs() {
        return this.jkus;
    }
    
    public Set<String> getKeyIDs() {
        return this.kids;
    }
    
    public boolean matches(final JOSEObject joseObject) {
        if (this.classes != null) {
            boolean pass = false;
            for (final Class<? extends JOSEObject> c : this.classes) {
                if (c != null && c.isInstance(joseObject)) {
                    pass = true;
                }
            }
            if (!pass) {
                return false;
            }
        }
        if (this.algs != null && !this.algs.contains(joseObject.getHeader().getAlgorithm())) {
            return false;
        }
        if (this.encs != null) {
            if (!(joseObject instanceof JWEObject)) {
                return false;
            }
            final JWEObject jweObject = (JWEObject)joseObject;
            if (!this.encs.contains(jweObject.getHeader().getEncryptionMethod())) {
                return false;
            }
        }
        if (this.jkus != null) {
            URI jku;
            if (joseObject instanceof JWSObject) {
                jku = ((JWSObject)joseObject).getHeader().getJWKURL();
            }
            else if (joseObject instanceof JWEObject) {
                jku = ((JWEObject)joseObject).getHeader().getJWKURL();
            }
            else {
                jku = null;
            }
            if (!this.jkus.contains(jku)) {
                return false;
            }
        }
        if (this.kids != null) {
            String kid;
            if (joseObject instanceof JWSObject) {
                kid = ((JWSObject)joseObject).getHeader().getKeyID();
            }
            else if (joseObject instanceof JWEObject) {
                kid = ((JWEObject)joseObject).getHeader().getKeyID();
            }
            else {
                kid = null;
            }
            if (!this.kids.contains(kid)) {
                return false;
            }
        }
        return true;
    }
    
    public static class Builder
    {
        private Set<Class<? extends JOSEObject>> classes;
        private Set<Algorithm> algs;
        private Set<EncryptionMethod> encs;
        private Set<URI> jkus;
        private Set<String> kids;
        
        public Builder joseClass(final Class<? extends JOSEObject> clazz) {
            if (clazz == null) {
                this.classes = null;
            }
            else {
                this.classes = new HashSet<Class<? extends JOSEObject>>(Collections.singletonList(clazz));
            }
            return this;
        }
        
        public Builder joseClasses(final Class<? extends JOSEObject>... classes) {
            this.joseClasses(new HashSet<Class<? extends JOSEObject>>(Arrays.asList(classes)));
            return this;
        }
        
        public Builder joseClasses(final Set<Class<? extends JOSEObject>> classes) {
            this.classes = classes;
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
            this.algorithms(new HashSet<Algorithm>(Arrays.asList(algs)));
            return this;
        }
        
        public Builder algorithms(final Set<Algorithm> algs) {
            this.algs = algs;
            return this;
        }
        
        public Builder encryptionMethod(final EncryptionMethod enc) {
            if (enc == null) {
                this.encs = null;
            }
            else {
                this.encs = new HashSet<EncryptionMethod>(Collections.singletonList(enc));
            }
            return this;
        }
        
        public Builder encryptionMethods(final EncryptionMethod... encs) {
            this.encryptionMethods(new HashSet<EncryptionMethod>(Arrays.asList(encs)));
            return this;
        }
        
        public Builder encryptionMethods(final Set<EncryptionMethod> encs) {
            this.encs = encs;
            return this;
        }
        
        public Builder jwkURL(final URI jku) {
            if (jku == null) {
                this.jkus = null;
            }
            else {
                this.jkus = new HashSet<URI>(Collections.singletonList(jku));
            }
            return this;
        }
        
        public Builder jwkURLs(final URI... jkus) {
            this.jwkURLs(new HashSet<URI>(Arrays.asList(jkus)));
            return this;
        }
        
        public Builder jwkURLs(final Set<URI> jkus) {
            this.jkus = jkus;
            return this;
        }
        
        public Builder keyID(final String kid) {
            if (kid == null) {
                this.kids = null;
            }
            else {
                this.kids = new HashSet<String>(Collections.singletonList(kid));
            }
            return this;
        }
        
        public Builder keyIDs(final String... ids) {
            this.keyIDs(new HashSet<String>(Arrays.asList(ids)));
            return this;
        }
        
        public Builder keyIDs(final Set<String> kids) {
            this.kids = kids;
            return this;
        }
        
        public JOSEMatcher build() {
            return new JOSEMatcher(this.classes, this.algs, this.encs, this.jkus, this.kids);
        }
    }
}
