// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

import org.datanucleus.exceptions.NucleusUserException;

public class PropertyTypeInvalidException extends NucleusUserException
{
    public PropertyTypeInvalidException(final String name, final String type) {
        super("Property \"" + name + "\" is not of required type \"" + type + "\"");
    }
}
