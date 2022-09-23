// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.bind.v2.runtime.Name;

class TagAndType
{
    final Name tagName;
    final JaxBeanInfo beanInfo;
    
    TagAndType(final Name tagName, final JaxBeanInfo beanInfo) {
        this.tagName = tagName;
        this.beanInfo = beanInfo;
    }
}
