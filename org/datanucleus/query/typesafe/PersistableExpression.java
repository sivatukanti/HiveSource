// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface PersistableExpression<T> extends Expression<T>
{
    Expression jdoObjectId();
    
    Expression jdoVersion();
}
