// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class TagFileInfo
{
    private String name;
    private String path;
    private TagInfo tagInfo;
    
    public TagFileInfo(final String name, final String path, final TagInfo tagInfo) {
        this.name = name;
        this.path = path;
        this.tagInfo = tagInfo;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public TagInfo getTagInfo() {
        return this.tagInfo;
    }
}
