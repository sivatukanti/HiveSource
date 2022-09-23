// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class ObjectDetachedException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public ObjectDetachedException(final String class_name) {
        super(ObjectDetachedException.LOCALISER.msg("018006", class_name));
    }
    
    public ObjectDetachedException(final String class_name, final Exception nested) {
        super(ObjectDetachedException.LOCALISER.msg("018006", class_name), nested);
    }
    
    public ObjectDetachedException(final Throwable[] nested) {
        super(ObjectDetachedException.LOCALISER.msg("018006"), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
