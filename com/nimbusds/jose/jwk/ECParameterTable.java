// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.security.spec.ECPoint;
import java.security.spec.ECField;
import java.security.spec.EllipticCurve;
import java.security.spec.ECFieldFp;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;

class ECParameterTable
{
    private static final ECParameterSpec P_256_SPEC;
    private static final ECParameterSpec P_384_SPEC;
    private static final ECParameterSpec P_521_SPEC;
    
    static {
        P_256_SPEC = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")), new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"), new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")), new ECPoint(new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"), new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")), new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"), 1);
        P_384_SPEC = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319")), new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112316"), new BigInteger("27580193559959705877849011840389048093056905856361568521428707301988689241309860865136260764883745107765439761230575")), new ECPoint(new BigInteger("26247035095799689268623156744566981891852923491109213387815615900925518854738050089022388053975719786650872476732087"), new BigInteger("8325710961489029985546751289520108179287853048861315594709205902480503199884419224438643760392947333078086511627871")), new BigInteger("39402006196394479212279040100143613805079739270465446667946905279627659399113263569398956308152294913554433653942643"), 1);
        P_521_SPEC = new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057148"), new BigInteger("1093849038073734274511112390766805569936207598951683748994586394495953116150735016013708737573759623248592132296706313309438452531591012912142327488478985984")), new ECPoint(new BigInteger("2661740802050217063228768716723360960729859168756973147706671368418802944996427808491545080627771902352094241225065558662157113545570916814161637315895999846"), new BigInteger("3757180025770020463545507224491183603594455134769762486694567779615544477440556316691234405012945539562144444537289428522585666729196580810124344277578376784")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449"), 1);
    }
    
    public static ECParameterSpec get(final ECKey.Curve curve) {
        if (ECKey.Curve.P_256.equals(curve)) {
            return ECParameterTable.P_256_SPEC;
        }
        if (ECKey.Curve.P_384.equals(curve)) {
            return ECParameterTable.P_384_SPEC;
        }
        if (ECKey.Curve.P_521.equals(curve)) {
            return ECParameterTable.P_521_SPEC;
        }
        return null;
    }
    
    public static ECKey.Curve get(final ECParameterSpec spec) {
        if (spec == null) {
            return null;
        }
        if (spec.getCurve().getField().getFieldSize() == ECParameterTable.P_256_SPEC.getCurve().getField().getFieldSize() && spec.getCurve().getA().equals(ECParameterTable.P_256_SPEC.getCurve().getA()) && spec.getCurve().getB().equals(ECParameterTable.P_256_SPEC.getCurve().getB()) && spec.getGenerator().getAffineX().equals(ECParameterTable.P_256_SPEC.getGenerator().getAffineX()) && spec.getGenerator().getAffineY().equals(ECParameterTable.P_256_SPEC.getGenerator().getAffineY()) && spec.getOrder().equals(ECParameterTable.P_256_SPEC.getOrder()) && spec.getCofactor() == ECParameterTable.P_256_SPEC.getCofactor()) {
            return ECKey.Curve.P_256;
        }
        if (spec.getCurve().getField().getFieldSize() == ECParameterTable.P_384_SPEC.getCurve().getField().getFieldSize() && spec.getCurve().getA().equals(ECParameterTable.P_384_SPEC.getCurve().getA()) && spec.getCurve().getB().equals(ECParameterTable.P_384_SPEC.getCurve().getB()) && spec.getGenerator().getAffineX().equals(ECParameterTable.P_384_SPEC.getGenerator().getAffineX()) && spec.getGenerator().getAffineY().equals(ECParameterTable.P_384_SPEC.getGenerator().getAffineY()) && spec.getOrder().equals(ECParameterTable.P_384_SPEC.getOrder()) && spec.getCofactor() == ECParameterTable.P_384_SPEC.getCofactor()) {
            return ECKey.Curve.P_384;
        }
        if (spec.getCurve().getField().getFieldSize() == ECParameterTable.P_521_SPEC.getCurve().getField().getFieldSize() && spec.getCurve().getA().equals(ECParameterTable.P_521_SPEC.getCurve().getA()) && spec.getCurve().getB().equals(ECParameterTable.P_521_SPEC.getCurve().getB()) && spec.getGenerator().getAffineX().equals(ECParameterTable.P_521_SPEC.getGenerator().getAffineX()) && spec.getGenerator().getAffineY().equals(ECParameterTable.P_521_SPEC.getGenerator().getAffineY()) && spec.getOrder().equals(ECParameterTable.P_521_SPEC.getOrder()) && spec.getCofactor() == ECParameterTable.P_521_SPEC.getCofactor()) {
            return ECKey.Curve.P_521;
        }
        return null;
    }
    
    private ECParameterTable() {
    }
    
    private static class ECFieldImpl implements ECField
    {
        private int size;
        
        public ECFieldImpl(final int size) {
            this.size = size;
        }
        
        @Override
        public int getFieldSize() {
            return this.size;
        }
    }
}
