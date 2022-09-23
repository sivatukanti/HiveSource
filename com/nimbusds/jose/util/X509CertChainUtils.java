// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.JSONArray;

public class X509CertChainUtils
{
    public static List<Base64> parseX509CertChain(final JSONArray jsonArray) throws ParseException {
        final List<Base64> chain = new LinkedList<Base64>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            final Object item = jsonArray.get(i);
            if (item == null) {
                throw new ParseException("The X.509 certificate at position " + i + " must not be null", 0);
            }
            if (!(item instanceof String)) {
                throw new ParseException("The X.509 certificate at position " + i + " must be encoded as a Base64 string", 0);
            }
            chain.add(new Base64((String)item));
        }
        return chain;
    }
    
    private X509CertChainUtils() {
    }
}
