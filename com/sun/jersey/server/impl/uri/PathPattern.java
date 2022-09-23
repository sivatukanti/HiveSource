// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri;

import com.sun.jersey.api.uri.UriTemplate;
import java.util.Comparator;
import com.sun.jersey.api.uri.UriPattern;

public final class PathPattern extends UriPattern
{
    public static final PathPattern EMPTY_PATH;
    private static final String RIGHT_HAND_SIDE = "(/.*)?";
    public static final Comparator<PathPattern> COMPARATOR;
    private final UriTemplate template;
    
    private PathPattern() {
        this.template = UriTemplate.EMPTY;
    }
    
    public PathPattern(final UriTemplate template) {
        super(postfixWithCapturingGroup(template.getPattern().getRegex()), indexCapturingGroup(template.getPattern().getGroupIndexes()));
        this.template = template;
    }
    
    public PathPattern(final UriTemplate template, final String rightHandSide) {
        super(postfixWithCapturingGroup(template.getPattern().getRegex(), rightHandSide), indexCapturingGroup(template.getPattern().getGroupIndexes()));
        this.template = template;
    }
    
    public UriTemplate getTemplate() {
        return this.template;
    }
    
    private static String postfixWithCapturingGroup(final String regex) {
        return postfixWithCapturingGroup(regex, "(/.*)?");
    }
    
    private static String postfixWithCapturingGroup(final String regex, final String rightHandSide) {
        return (regex.endsWith("/") ? regex.substring(0, regex.length() - 1) : regex) + rightHandSide;
    }
    
    private static int[] indexCapturingGroup(final int[] indexes) {
        if (indexes.length == 0) {
            return indexes;
        }
        final int[] cgIndexes = new int[indexes.length + 1];
        System.arraycopy(indexes, 0, cgIndexes, 0, indexes.length);
        cgIndexes[indexes.length] = cgIndexes[indexes.length - 1] + 1;
        return cgIndexes;
    }
    
    static {
        EMPTY_PATH = new PathPattern();
        COMPARATOR = new Comparator<PathPattern>() {
            @Override
            public int compare(final PathPattern o1, final PathPattern o2) {
                return UriTemplate.COMPARATOR.compare(o1.template, o2.template);
            }
        };
    }
}
