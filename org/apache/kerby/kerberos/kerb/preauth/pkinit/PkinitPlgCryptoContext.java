// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.KrbException;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

public class PkinitPlgCryptoContext
{
    private static final String ID_PKINIT_AUTHDATA = "1.3.6.1.5.2.3.1";
    private static final String ID_PKINIT_DHKEYDATA = "1.3.6.1.5.2.3.2";
    private static final String ID_PKINIT_RKEYDATA = "1.3.6.1.5.2.3.3";
    public X509Certificate trustedCAs;
    public X509Certificate intermediateCAs;
    public X509Certificate revoked;
    
    public BigInteger getPkinit1024Prime() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1");
        sb.append("29024E088A67CC74020BBEA63B139B22514A08798E3404DD");
        sb.append("EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245");
        sb.append("E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED");
        sb.append("EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381");
        sb.append("FFFFFFFFFFFFFFFF");
        return new BigInteger(sb.toString(), 16);
    }
    
    public BigInteger getPkinit2048Prime() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1");
        sb.append("29024E088A67CC74020BBEA63B139B22514A08798E3404DD");
        sb.append("EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245");
        sb.append("E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED");
        sb.append("EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D");
        sb.append("C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F");
        sb.append("83655D23DCA3AD961C62F356208552BB9ED529077096966D");
        sb.append("670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B");
        sb.append("E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9");
        sb.append("DE2BCBF6955817183995497CEA956AE515D2261898FA0510");
        sb.append("15728E5A8AACAA68FFFFFFFFFFFFFFFF");
        return new BigInteger(sb.toString(), 16);
    }
    
    public BigInteger getPkinit4096Prime() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1");
        sb.append("29024E088A67CC74020BBEA63B139B22514A08798E3404DD");
        sb.append("EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245");
        sb.append("E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED");
        sb.append("EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D");
        sb.append("C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F");
        sb.append("83655D23DCA3AD961C62F356208552BB9ED529077096966D");
        sb.append("670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B");
        sb.append("E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9");
        sb.append("DE2BCBF6955817183995497CEA956AE515D2261898FA0510");
        sb.append("15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64");
        sb.append("ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7");
        sb.append("ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B");
        sb.append("F12FFA06D98A0864D87602733EC86A64521F2B18177B200C");
        sb.append("BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31");
        sb.append("43DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D7");
        sb.append("88719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA");
        sb.append("2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6");
        sb.append("287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED");
        sb.append("1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA9");
        sb.append("93B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934063199");
        sb.append("FFFFFFFFFFFFFFFF");
        return new BigInteger(sb.toString(), 16);
    }
    
    public DHParameterSpec createDHParameterSpec(final int dhSize) throws KrbException {
        final BigInteger g = BigInteger.valueOf(2L);
        BigInteger p = null;
        switch (dhSize) {
            case 1024: {
                p = this.getPkinit1024Prime();
                break;
            }
            case 2048: {
                p = this.getPkinit2048Prime();
                break;
            }
            case 4096: {
                p = this.getPkinit4096Prime();
                break;
            }
            default: {
                throw new KrbException("Unsupported dh size:" + dhSize);
            }
        }
        return new DHParameterSpec(p, g);
    }
    
    public static String getIdPkinitAuthDataOID() {
        return "1.3.6.1.5.2.3.1";
    }
    
    public static String getIdPkinitDHKeyDataOID() {
        return "1.3.6.1.5.2.3.2";
    }
    
    public static String getIdPkinitRkeyDataOID() {
        return "1.3.6.1.5.2.3.3";
    }
}
