// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.StringUtils;

public enum NodeNameMatchers implements NodeMatcher<String>
{
    EQUALS {
        @Override
        public <T> boolean matches(final T node, final NodeHandler<T> handler, final String criterion) {
            return StringUtils.equals(criterion, handler.nodeName(node));
        }
    }, 
    EQUALS_IGNORE_CASE {
        @Override
        public <T> boolean matches(final T node, final NodeHandler<T> handler, final String criterion) {
            return StringUtils.equalsIgnoreCase(criterion, handler.nodeName(node));
        }
    };
}
