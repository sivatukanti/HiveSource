// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import java.sql.Time;
import java.sql.Date;

public interface TypesafeQuery<T>
{
    public static final String QUERY_CLASS_PREFIX = "Q";
    
    PersistableExpression candidate();
    
    Expression parameter(final String p0, final Class p1);
    
    StringExpression stringParameter(final String p0);
    
    CharacterExpression characterParameter(final String p0);
    
    NumericExpression<Long> longParameter(final String p0);
    
    NumericExpression<Integer> integerParameter(final String p0);
    
    NumericExpression<Short> shortParameter(final String p0);
    
    NumericExpression<Double> doubleParameter(final String p0);
    
    NumericExpression<Float> floatParameter(final String p0);
    
    DateExpression<Date> dateParameter(final String p0);
    
    TimeExpression<Time> timeParameter(final String p0);
    
    DateTimeExpression<java.util.Date> datetimeParameter(final String p0);
    
    CollectionExpression collectionParameter(final String p0);
    
    MapExpression mapParameter(final String p0);
    
    ListExpression listParameter(final String p0);
    
    Expression variable(final String p0, final Class p1);
    
    PersistenceManager getPersistenceManager();
    
    FetchPlan getFetchPlan();
    
    TypesafeQuery<T> setIgnoreCache(final boolean p0);
    
    TypesafeQuery<T> setCandidates(final Collection<T> p0);
    
    TypesafeQuery<T> excludeSubclasses();
    
    TypesafeQuery<T> includeSubclasses();
    
    TypesafeQuery<T> filter(final BooleanExpression p0);
    
    TypesafeQuery<T> groupBy(final Expression... p0);
    
    TypesafeQuery<T> having(final Expression p0);
    
    TypesafeQuery<T> orderBy(final OrderExpression... p0);
    
    TypesafeQuery<T> range(final NumericExpression p0, final NumericExpression p1);
    
    TypesafeQuery<T> range(final long p0, final long p1);
    
    TypesafeQuery<T> range(final Expression p0, final Expression p1);
    
    TypesafeSubquery<T> subquery(final String p0);
    
     <S> TypesafeSubquery<S> subquery(final Class<S> p0, final String p1);
    
    TypesafeQuery<T> setParameter(final Expression p0, final Object p1);
    
    TypesafeQuery<T> setParameter(final String p0, final Object p1);
    
    TypesafeQuery<T> addExtension(final String p0, final Object p1);
    
    TypesafeQuery<T> setExtensions(final Map<String, Object> p0);
    
     <T> List<T> executeList();
    
     <T> T executeUnique();
    
     <R> List<R> executeResultList(final Class<R> p0, final boolean p1, final Expression... p2);
    
     <R> R executeResultUnique(final Class<R> p0, final boolean p1, final Expression... p2);
    
    List<Object> executeResultList(final boolean p0, final Expression p1);
    
    Object executeResultUnique(final boolean p0, final Expression p1);
    
    List<Object[]> executeResultList(final boolean p0, final Expression... p1);
    
    Object[] executeResultUnique(final boolean p0, final Expression... p1);
    
    long deletePersistentAll();
    
    void close(final Object p0);
    
    void closeAll();
    
    String toString();
}
