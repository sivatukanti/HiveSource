// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1Collection extends Asn1Constructed
{
    public Asn1Collection(final UniversalTag universalTag) {
        super(new Tag(universalTag));
    }
    
    public static boolean isCollection(final Tag tag) {
        return isCollection(tag.universalTag());
    }
    
    public static boolean isCollection(final int tag) {
        return isCollection(new Tag(tag));
    }
    
    public static boolean isCollection(final UniversalTag universalTag) {
        switch (universalTag) {
            case SEQUENCE:
            case SEQUENCE_OF:
            case SET:
            case SET_OF: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static Asn1Collection createCollection(final Tag tag) {
        if (!isCollection(tag)) {
            throw new IllegalArgumentException("Not collection type, tag: " + tag);
        }
        return createCollection(tag.universalTag());
    }
    
    public static Asn1Collection createCollection(final UniversalTag universalTag) {
        if (!isCollection(universalTag)) {
            throw new IllegalArgumentException("Not collection type, tag: " + universalTag);
        }
        switch (universalTag) {
            case SEQUENCE: {
                return new Asn1Sequence();
            }
            case SEQUENCE_OF: {
                return new Asn1Sequence();
            }
            case SET: {
                return new Asn1Set();
            }
            case SET_OF: {
                return new Asn1Set();
            }
            default: {
                throw new IllegalArgumentException("Unexpected tag " + universalTag.getValue());
            }
        }
    }
}
