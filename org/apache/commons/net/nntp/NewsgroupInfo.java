// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.nntp;

public final class NewsgroupInfo
{
    public static final int UNKNOWN_POSTING_PERMISSION = 0;
    public static final int MODERATED_POSTING_PERMISSION = 1;
    public static final int PERMITTED_POSTING_PERMISSION = 2;
    public static final int PROHIBITED_POSTING_PERMISSION = 3;
    private String __newsgroup;
    private long __estimatedArticleCount;
    private long __firstArticle;
    private long __lastArticle;
    private int __postingPermission;
    
    void _setNewsgroup(final String newsgroup) {
        this.__newsgroup = newsgroup;
    }
    
    void _setArticleCount(final long count) {
        this.__estimatedArticleCount = count;
    }
    
    void _setFirstArticle(final long first) {
        this.__firstArticle = first;
    }
    
    void _setLastArticle(final long last) {
        this.__lastArticle = last;
    }
    
    void _setPostingPermission(final int permission) {
        this.__postingPermission = permission;
    }
    
    public String getNewsgroup() {
        return this.__newsgroup;
    }
    
    public long getArticleCountLong() {
        return this.__estimatedArticleCount;
    }
    
    public long getFirstArticleLong() {
        return this.__firstArticle;
    }
    
    public long getLastArticleLong() {
        return this.__lastArticle;
    }
    
    public int getPostingPermission() {
        return this.__postingPermission;
    }
    
    @Deprecated
    public int getArticleCount() {
        return (int)this.__estimatedArticleCount;
    }
    
    @Deprecated
    public int getFirstArticle() {
        return (int)this.__firstArticle;
    }
    
    @Deprecated
    public int getLastArticle() {
        return (int)this.__lastArticle;
    }
}
