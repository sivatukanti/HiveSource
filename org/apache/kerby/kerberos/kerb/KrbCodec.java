// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.kerberos.kerb.type.base.KrbError;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsRep;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.type.kdc.AsRep;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsReq;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Type;

public class KrbCodec
{
    public static byte[] encode(final Asn1Type krbObj) throws KrbException {
        try {
            return krbObj.encode();
        }
        catch (IOException e) {
            throw new KrbException("encode failed", e);
        }
    }
    
    public static void encode(final Asn1Type krbObj, final ByteBuffer buffer) throws KrbException {
        try {
            krbObj.encode(buffer);
        }
        catch (IOException e) {
            throw new KrbException("Encoding failed", e);
        }
    }
    
    public static void decode(final byte[] content, final Asn1Type value) throws KrbException {
        decode(ByteBuffer.wrap(content), value);
    }
    
    public static void decode(final ByteBuffer content, final Asn1Type value) throws KrbException {
        try {
            value.decode(content);
        }
        catch (IOException e) {
            throw new KrbException("Decoding failed", e);
        }
    }
    
    public static <T extends Asn1Type> T decode(final byte[] content, final Class<T> krbType) throws KrbException {
        return decode(ByteBuffer.wrap(content), krbType);
    }
    
    public static <T extends Asn1Type> T decode(final ByteBuffer content, final Class<T> krbType) throws KrbException {
        Asn1Type implObj;
        try {
            implObj = krbType.newInstance();
        }
        catch (Exception e) {
            throw new KrbException("Decoding failed", e);
        }
        try {
            implObj.decode(content);
        }
        catch (IOException e2) {
            throw new KrbException("Decoding failed", e2);
        }
        return (T)implObj;
    }
    
    public static KrbMessage decodeMessage(final ByteBuffer buffer) throws IOException {
        final Asn1ParseResult parsingResult = Asn1.parse(buffer);
        final Tag tag = parsingResult.tag();
        final KrbMessageType msgType = KrbMessageType.fromValue(tag.tagNo());
        KrbMessage msg;
        if (msgType == KrbMessageType.TGS_REQ) {
            msg = new TgsReq();
        }
        else if (msgType == KrbMessageType.AS_REP) {
            msg = new AsRep();
        }
        else if (msgType == KrbMessageType.AS_REQ) {
            msg = new AsReq();
        }
        else if (msgType == KrbMessageType.TGS_REP) {
            msg = new TgsRep();
        }
        else if (msgType == KrbMessageType.AP_REQ) {
            msg = new ApReq();
        }
        else if (msgType == KrbMessageType.AP_REP) {
            msg = new ApReq();
        }
        else {
            if (msgType != KrbMessageType.KRB_ERROR) {
                throw new IOException("To be supported krb message type with tag: " + tag);
            }
            msg = new KrbError();
        }
        msg.decode(parsingResult);
        return msg;
    }
}
