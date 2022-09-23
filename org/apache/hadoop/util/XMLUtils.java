// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class XMLUtils
{
    public static void transform(final InputStream styleSheet, final InputStream xml, final Writer out) throws TransformerConfigurationException, TransformerException {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer(new StreamSource(styleSheet));
        transformer.transform(new StreamSource(xml), new StreamResult(out));
    }
}
