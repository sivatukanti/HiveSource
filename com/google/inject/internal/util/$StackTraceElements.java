// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

public class $StackTraceElements
{
    static final Map<Class<?>, $LineNumbers> lineNumbersCache;
    
    public static Object forMember(final Member member) {
        if (member == null) {
            return $SourceProvider.UNKNOWN_SOURCE;
        }
        final Class declaringClass = member.getDeclaringClass();
        final $LineNumbers lineNumbers = $StackTraceElements.lineNumbersCache.get(declaringClass);
        final String fileName = lineNumbers.getSource();
        final Integer lineNumberOrNull = lineNumbers.getLineNumber(member);
        final int lineNumber = (lineNumberOrNull == null) ? lineNumbers.getFirstLine() : lineNumberOrNull;
        final Class<? extends Member> memberType = $Classes.memberType(member);
        final String memberName = (memberType == Constructor.class) ? "<init>" : member.getName();
        return new StackTraceElement(declaringClass.getName(), memberName, fileName, lineNumber);
    }
    
    public static Object forType(final Class<?> implementation) {
        final $LineNumbers lineNumbers = $StackTraceElements.lineNumbersCache.get(implementation);
        final int lineNumber = lineNumbers.getFirstLine();
        final String fileName = lineNumbers.getSource();
        return new StackTraceElement(implementation.getName(), "class", fileName, lineNumber);
    }
    
    static {
        lineNumbersCache = new $MapMaker().weakKeys().softValues().makeComputingMap(($Function<? super Object, ?>)new $Function<Class<?>, $LineNumbers>() {
            public $LineNumbers apply(final Class<?> key) {
                try {
                    return new $LineNumbers(key);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
