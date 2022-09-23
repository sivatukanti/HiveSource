// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.inject.servlet.RequestScoped;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@RequestScoped
public class ResponseInfo implements Iterable<Item>
{
    final List<Item> items;
    String about;
    
    public ResponseInfo() {
        this.items = (List<Item>)Lists.newArrayList();
        this.about = "Info";
    }
    
    public static ResponseInfo $about(final String about) {
        final ResponseInfo info = new ResponseInfo();
        info.about = about;
        return info;
    }
    
    public ResponseInfo about(final String about) {
        this.about = about;
        return this;
    }
    
    public String about() {
        return this.about;
    }
    
    public ResponseInfo _(final String key, final Object value) {
        this.items.add(Item.of(key, value, false));
        return this;
    }
    
    public ResponseInfo _(final String key, final String url, final Object anchor) {
        this.items.add(Item.of(key, url, anchor));
        return this;
    }
    
    public ResponseInfo _r(final String key, final Object value) {
        this.items.add(Item.of(key, value, true));
        return this;
    }
    
    public void clear() {
        this.items.clear();
    }
    
    @Override
    public Iterator<Item> iterator() {
        return this.items.iterator();
    }
    
    public static class Item
    {
        public final String key;
        public final String url;
        public final Object value;
        public final boolean isRaw;
        
        Item(final String key, final String url, final Object value, final boolean isRaw) {
            this.key = key;
            this.url = url;
            this.value = value;
            this.isRaw = isRaw;
        }
        
        public static Item of(final String key, final Object value, final boolean isRaw) {
            return new Item(key, null, value, isRaw);
        }
        
        public static Item of(final String key, final String url, final Object value) {
            return new Item(key, url, value, false);
        }
    }
}
