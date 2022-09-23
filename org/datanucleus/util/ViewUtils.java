// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class ViewUtils
{
    protected static final Localiser LOCALISER;
    
    public static void checkForCircularViewReferences(final Map viewReferences, final String referencer_name, final String referencee_name, List referenceChain) {
        final Set class_names = viewReferences.get(referencee_name);
        if (class_names != null) {
            if (referenceChain == null) {
                referenceChain = new ArrayList<String>();
                referenceChain.add(referencer_name);
            }
            referenceChain.add(referencee_name);
            for (final String current_name : class_names) {
                if (current_name.equals(referencer_name)) {
                    final StringBuilder error = new StringBuilder(ViewUtils.LOCALISER.msg("031003"));
                    final Iterator chainIter = referenceChain.iterator();
                    while (chainIter.hasNext()) {
                        error.append(chainIter.next());
                        if (chainIter.hasNext()) {
                            error.append(" -> ");
                        }
                    }
                    throw new NucleusUserException(error.toString()).setFatal();
                }
                checkForCircularViewReferences(viewReferences, referencer_name, current_name, referenceChain);
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
