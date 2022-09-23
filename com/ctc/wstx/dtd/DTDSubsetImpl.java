// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import javax.xml.stream.Location;
import com.ctc.wstx.exc.WstxParsingException;
import java.text.MessageFormat;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.DataUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import com.ctc.wstx.sr.InputProblemReporter;
import java.util.Iterator;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.events.NotationDeclaration;
import java.util.Set;
import java.util.List;
import com.ctc.wstx.ent.EntityDecl;
import java.util.HashMap;

public final class DTDSubsetImpl extends DTDSubset
{
    final boolean mIsCachable;
    final boolean mFullyValidating;
    final boolean mHasNsDefaults;
    final HashMap<String, EntityDecl> mGeneralEntities;
    transient volatile List<EntityDecl> mGeneralEntityList;
    final Set<String> mRefdGEs;
    final HashMap<String, EntityDecl> mDefinedPEs;
    final Set<String> mRefdPEs;
    final HashMap<String, NotationDeclaration> mNotations;
    transient List<NotationDeclaration> mNotationList;
    final HashMap<PrefixedName, DTDElement> mElements;
    
    private DTDSubsetImpl(final boolean cachable, final HashMap<String, EntityDecl> genEnt, final Set<String> refdGEs, final HashMap<String, EntityDecl> paramEnt, final Set<String> peRefs, final HashMap<String, NotationDeclaration> notations, final HashMap<PrefixedName, DTDElement> elements, final boolean fullyValidating) {
        this.mGeneralEntityList = null;
        this.mNotationList = null;
        this.mIsCachable = cachable;
        this.mGeneralEntities = genEnt;
        this.mRefdGEs = refdGEs;
        this.mDefinedPEs = paramEnt;
        this.mRefdPEs = peRefs;
        this.mNotations = notations;
        this.mElements = elements;
        this.mFullyValidating = fullyValidating;
        boolean anyNsDefs = false;
        if (elements != null) {
            for (final DTDElement elem : elements.values()) {
                if (elem.hasNsDefaults()) {
                    anyNsDefs = true;
                    break;
                }
            }
        }
        this.mHasNsDefaults = anyNsDefs;
    }
    
    public static DTDSubsetImpl constructInstance(final boolean cachable, final HashMap<String, EntityDecl> genEnt, final Set<String> refdGEs, final HashMap<String, EntityDecl> paramEnt, final Set<String> refdPEs, final HashMap<String, NotationDeclaration> notations, final HashMap<PrefixedName, DTDElement> elements, final boolean fullyValidating) {
        return new DTDSubsetImpl(cachable, genEnt, refdGEs, paramEnt, refdPEs, notations, elements, fullyValidating);
    }
    
    @Override
    public DTDSubset combineWithExternalSubset(final InputProblemReporter rep, final DTDSubset extSubset) throws XMLStreamException {
        HashMap<String, EntityDecl> ge1 = this.getGeneralEntityMap();
        final HashMap<String, EntityDecl> ge2 = extSubset.getGeneralEntityMap();
        if (ge1 == null || ge1.isEmpty()) {
            ge1 = ge2;
        }
        else if (ge2 != null && !ge2.isEmpty()) {
            combineMaps(ge1, ge2);
        }
        HashMap<String, NotationDeclaration> n1 = this.getNotationMap();
        final HashMap<String, NotationDeclaration> n2 = extSubset.getNotationMap();
        if (n1 == null || n1.isEmpty()) {
            n1 = n2;
        }
        else if (n2 != null && !n2.isEmpty()) {
            checkNotations(n1, n2);
            combineMaps(n1, n2);
        }
        HashMap<PrefixedName, DTDElement> e1 = this.getElementMap();
        final HashMap<PrefixedName, DTDElement> e2 = extSubset.getElementMap();
        if (e1 == null || e1.isEmpty()) {
            e1 = e2;
        }
        else if (e2 != null && !e2.isEmpty()) {
            this.combineElements(rep, e1, e2);
        }
        return constructInstance(false, ge1, null, null, null, n1, e1, this.mFullyValidating);
    }
    
    @Override
    public XMLValidator createValidator(final ValidationContext ctxt) throws XMLStreamException {
        if (this.mFullyValidating) {
            return new DTDValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
        }
        return new DTDTypingNonValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
    }
    
    @Override
    public int getEntityCount() {
        return (this.mGeneralEntities == null) ? 0 : this.mGeneralEntities.size();
    }
    
    @Override
    public int getNotationCount() {
        return (this.mNotations == null) ? 0 : this.mNotations.size();
    }
    
    @Override
    public boolean isCachable() {
        return this.mIsCachable;
    }
    
    @Override
    public HashMap<String, EntityDecl> getGeneralEntityMap() {
        return this.mGeneralEntities;
    }
    
    @Override
    public List<EntityDecl> getGeneralEntityList() {
        List<EntityDecl> l = this.mGeneralEntityList;
        if (l == null) {
            if (this.mGeneralEntities == null || this.mGeneralEntities.size() == 0) {
                l = Collections.emptyList();
            }
            else {
                l = Collections.unmodifiableList((List<? extends EntityDecl>)new ArrayList<EntityDecl>(this.mGeneralEntities.values()));
            }
            this.mGeneralEntityList = l;
        }
        return l;
    }
    
    @Override
    public HashMap<String, EntityDecl> getParameterEntityMap() {
        return this.mDefinedPEs;
    }
    
    @Override
    public HashMap<String, NotationDeclaration> getNotationMap() {
        return this.mNotations;
    }
    
    @Override
    public synchronized List<NotationDeclaration> getNotationList() {
        List<NotationDeclaration> l = this.mNotationList;
        if (l == null) {
            if (this.mNotations == null || this.mNotations.size() == 0) {
                l = Collections.emptyList();
            }
            else {
                l = Collections.unmodifiableList((List<? extends NotationDeclaration>)new ArrayList<NotationDeclaration>(this.mNotations.values()));
            }
            this.mNotationList = l;
        }
        return l;
    }
    
    @Override
    public HashMap<PrefixedName, DTDElement> getElementMap() {
        return this.mElements;
    }
    
    @Override
    public boolean isReusableWith(final DTDSubset intSubset) {
        final Set<String> refdPEs = this.mRefdPEs;
        if (refdPEs != null && refdPEs.size() > 0) {
            final HashMap<String, EntityDecl> intPEs = intSubset.getParameterEntityMap();
            if (intPEs != null && intPEs.size() > 0 && DataUtil.anyValuesInCommon(refdPEs, intPEs.keySet())) {
                return false;
            }
        }
        final Set<String> refdGEs = this.mRefdGEs;
        if (refdGEs != null && refdGEs.size() > 0) {
            final HashMap<String, EntityDecl> intGEs = intSubset.getGeneralEntityMap();
            if (intGEs != null && intGEs.size() > 0 && DataUtil.anyValuesInCommon(refdGEs, intGEs.keySet())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[DTDSubset: ");
        final int count = this.getEntityCount();
        sb.append(count);
        sb.append(" general entities");
        sb.append(']');
        return sb.toString();
    }
    
    public static void throwNotationException(final NotationDeclaration oldDecl, final NotationDeclaration newDecl) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_NOTATION_REDEFD, newDecl.getName(), oldDecl.getLocation().toString()), newDecl.getLocation());
    }
    
    public static void throwElementException(final DTDElement oldElem, final Location loc) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_ELEM_REDEFD, oldElem.getDisplayName(), oldElem.getLocation().toString()), loc);
    }
    
    private static <K, V> void combineMaps(final Map<K, V> m1, final Map<K, V> m2) {
        for (final Map.Entry<K, V> me : m2.entrySet()) {
            final K key = me.getKey();
            final V old = m1.put(key, me.getValue());
            if (old != null) {
                m1.put(key, old);
            }
        }
    }
    
    private void combineElements(final InputProblemReporter rep, final HashMap<PrefixedName, DTDElement> intElems, final HashMap<PrefixedName, DTDElement> extElems) throws XMLStreamException {
        for (final Map.Entry<PrefixedName, DTDElement> me : extElems.entrySet()) {
            final PrefixedName key = me.getKey();
            final DTDElement extElem = me.getValue();
            final DTDElement intElem = intElems.get(key);
            if (intElem == null) {
                intElems.put(key, extElem);
            }
            else if (extElem.isDefined()) {
                if (intElem.isDefined()) {
                    throwElementException(intElem, extElem.getLocation());
                }
                else {
                    intElem.defineFrom(rep, extElem, this.mFullyValidating);
                }
            }
            else if (!intElem.isDefined()) {
                rep.reportProblem(intElem.getLocation(), ErrorConsts.WT_ENT_DECL, ErrorConsts.W_UNDEFINED_ELEM, extElem.getDisplayName(), null);
            }
            else {
                intElem.mergeMissingAttributesFrom(rep, extElem, this.mFullyValidating);
            }
        }
    }
    
    private static void checkNotations(final HashMap<String, NotationDeclaration> fromInt, final HashMap<String, NotationDeclaration> fromExt) throws XMLStreamException {
        for (final Map.Entry<String, NotationDeclaration> en : fromExt.entrySet()) {
            if (fromInt.containsKey(en.getKey())) {
                throwNotationException(fromInt.get(en.getKey()), en.getValue());
            }
        }
    }
}
