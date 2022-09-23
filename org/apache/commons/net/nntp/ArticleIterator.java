// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.nntp;

import java.util.Iterator;

class ArticleIterator implements Iterator<Article>, Iterable<Article>
{
    private final Iterator<String> stringIterator;
    
    public ArticleIterator(final Iterable<String> iterableString) {
        this.stringIterator = iterableString.iterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.stringIterator.hasNext();
    }
    
    @Override
    public Article next() {
        final String line = this.stringIterator.next();
        return NNTPClient.__parseArticleEntry(line);
    }
    
    @Override
    public void remove() {
        this.stringIterator.remove();
    }
    
    @Override
    public Iterator<Article> iterator() {
        return this;
    }
}
