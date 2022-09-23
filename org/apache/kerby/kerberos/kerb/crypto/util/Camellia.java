// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

public class Camellia
{
    private static final int BLOCK_SIZE = 16;
    private int[] state;
    private CamelliaKey camKey;
    
    public Camellia() {
        this.state = new int[4];
    }
    
    public void setKey(final boolean forEncryption, final byte[] key) {
        this.camKey = new CamelliaKey(key, forEncryption);
    }
    
    private void process128Block(final byte[] in, final int inOff, final byte[] out, final int outOff) {
        for (int i = 0; i < 4; ++i) {
            this.state[i] = BytesUtil.bytes2int(in, inOff + i * 4, true);
            final int[] state = this.state;
            final int n = i;
            state[n] ^= this.camKey.kw[i];
        }
        this.camKey.f2(this.state, this.camKey.subkey, 0);
        this.camKey.f2(this.state, this.camKey.subkey, 4);
        this.camKey.f2(this.state, this.camKey.subkey, 8);
        this.camKey.fls(this.state, this.camKey.ke, 0);
        this.camKey.f2(this.state, this.camKey.subkey, 12);
        this.camKey.f2(this.state, this.camKey.subkey, 16);
        this.camKey.f2(this.state, this.camKey.subkey, 20);
        this.camKey.fls(this.state, this.camKey.ke, 4);
        this.camKey.f2(this.state, this.camKey.subkey, 24);
        this.camKey.f2(this.state, this.camKey.subkey, 28);
        this.camKey.f2(this.state, this.camKey.subkey, 32);
        final int[] state2 = this.state;
        final int n2 = 2;
        state2[n2] ^= this.camKey.kw[4];
        final int[] state3 = this.state;
        final int n3 = 3;
        state3[n3] ^= this.camKey.kw[5];
        final int[] state4 = this.state;
        final int n4 = 0;
        state4[n4] ^= this.camKey.kw[6];
        final int[] state5 = this.state;
        final int n5 = 1;
        state5[n5] ^= this.camKey.kw[7];
        BytesUtil.int2bytes(this.state[2], out, outOff, true);
        BytesUtil.int2bytes(this.state[3], out, outOff + 4, true);
        BytesUtil.int2bytes(this.state[0], out, outOff + 8, true);
        BytesUtil.int2bytes(this.state[1], out, outOff + 12, true);
    }
    
    private void processBlockLargerBlock(final byte[] in, final int inOff, final byte[] out, final int outOff) {
        for (int i = 0; i < 4; ++i) {
            this.state[i] = BytesUtil.bytes2int(in, inOff + i * 4, true);
            final int[] state = this.state;
            final int n = i;
            state[n] ^= this.camKey.kw[i];
        }
        this.camKey.f2(this.state, this.camKey.subkey, 0);
        this.camKey.f2(this.state, this.camKey.subkey, 4);
        this.camKey.f2(this.state, this.camKey.subkey, 8);
        this.camKey.fls(this.state, this.camKey.ke, 0);
        this.camKey.f2(this.state, this.camKey.subkey, 12);
        this.camKey.f2(this.state, this.camKey.subkey, 16);
        this.camKey.f2(this.state, this.camKey.subkey, 20);
        this.camKey.fls(this.state, this.camKey.ke, 4);
        this.camKey.f2(this.state, this.camKey.subkey, 24);
        this.camKey.f2(this.state, this.camKey.subkey, 28);
        this.camKey.f2(this.state, this.camKey.subkey, 32);
        this.camKey.fls(this.state, this.camKey.ke, 8);
        this.camKey.f2(this.state, this.camKey.subkey, 36);
        this.camKey.f2(this.state, this.camKey.subkey, 40);
        this.camKey.f2(this.state, this.camKey.subkey, 44);
        final int[] state2 = this.state;
        final int n2 = 2;
        state2[n2] ^= this.camKey.kw[4];
        final int[] state3 = this.state;
        final int n3 = 3;
        state3[n3] ^= this.camKey.kw[5];
        final int[] state4 = this.state;
        final int n4 = 0;
        state4[n4] ^= this.camKey.kw[6];
        final int[] state5 = this.state;
        final int n5 = 1;
        state5[n5] ^= this.camKey.kw[7];
        BytesUtil.int2bytes(this.state[2], out, outOff, true);
        BytesUtil.int2bytes(this.state[3], out, outOff + 4, true);
        BytesUtil.int2bytes(this.state[0], out, outOff + 8, true);
        BytesUtil.int2bytes(this.state[1], out, outOff + 12, true);
    }
    
    public void processBlock(final byte[] in, final int inOff) {
        final byte[] out = new byte[16];
        if (this.camKey.is128()) {
            this.process128Block(in, inOff, out, 0);
        }
        else {
            this.processBlockLargerBlock(in, inOff, out, 0);
        }
        System.arraycopy(out, 0, in, inOff, 16);
    }
    
    public void encrypt(final byte[] data, final byte[] iv) {
        final byte[] cipherState = new byte[16];
        int blocksNum = (data.length + 16 - 1) / 16;
        final int lastBlockLen = data.length - (blocksNum - 1) * 16;
        if (blocksNum == 1) {
            this.cbcEnc(data, 0, 1, cipherState);
            return;
        }
        if (iv != null) {
            System.arraycopy(iv, 0, cipherState, 0, 16);
        }
        int offset = 0;
        while (blocksNum > 2) {
            int contBlocksNum = (data.length - offset) / 16;
            if (contBlocksNum > 0) {
                contBlocksNum = ((contBlocksNum > blocksNum - 2) ? (blocksNum - 2) : contBlocksNum);
                this.cbcEnc(data, offset, contBlocksNum, cipherState);
                offset += contBlocksNum * 16;
                blocksNum -= contBlocksNum;
            }
            else {
                this.cbcEnc(data, offset, 1, cipherState);
                offset += 16;
                --blocksNum;
            }
        }
        final byte[] blockN2 = new byte[16];
        final byte[] blockN3 = new byte[16];
        System.arraycopy(data, offset, blockN2, 0, 16);
        this.cbcEnc(blockN2, 0, 1, cipherState);
        System.arraycopy(data, offset + 16, blockN3, 0, lastBlockLen);
        this.cbcEnc(blockN3, 0, 1, cipherState);
        System.arraycopy(blockN3, 0, data, offset, 16);
        System.arraycopy(blockN2, 0, data, offset + 16, lastBlockLen);
        if (iv != null) {
            System.arraycopy(cipherState, 0, iv, 0, 16);
        }
    }
    
    public void decrypt(final byte[] data, final byte[] iv) {
        final byte[] cipherState = new byte[16];
        int blocksNum = (data.length + 16 - 1) / 16;
        final int lastBlockLen = data.length - (blocksNum - 1) * 16;
        if (blocksNum == 1) {
            this.cbcDec(data, 0, 1, cipherState);
            return;
        }
        if (iv != null) {
            System.arraycopy(iv, 0, cipherState, 0, 16);
        }
        int offset = 0;
        while (blocksNum > 2) {
            int contBlocksNum = (data.length - offset) / 16;
            if (contBlocksNum > 0) {
                contBlocksNum = ((contBlocksNum > blocksNum - 2) ? (blocksNum - 2) : contBlocksNum);
                this.cbcDec(data, offset, contBlocksNum, cipherState);
                offset += contBlocksNum * 16;
                blocksNum -= contBlocksNum;
            }
            else {
                this.cbcDec(data, offset, 1, cipherState);
                offset += 16;
                --blocksNum;
            }
        }
        final byte[] blockN2 = new byte[16];
        final byte[] blockN3 = new byte[16];
        System.arraycopy(data, offset, blockN2, 0, 16);
        System.arraycopy(data, offset + 16, blockN3, 0, lastBlockLen);
        if (iv != null) {
            System.arraycopy(blockN2, 0, iv, 0, 16);
        }
        final byte[] tmpCipherState = new byte[16];
        System.arraycopy(blockN3, 0, tmpCipherState, 0, 16);
        this.cbcDec(blockN2, 0, 1, tmpCipherState);
        System.arraycopy(blockN2, lastBlockLen, blockN3, lastBlockLen, 16 - lastBlockLen);
        this.cbcDec(blockN3, 0, 1, cipherState);
        System.arraycopy(blockN3, 0, data, offset, 16);
        System.arraycopy(blockN2, 0, data, offset + 16, lastBlockLen);
    }
    
    public void cbcEnc(final byte[] data, final int offset, final int blocksNum, final byte[] cipherState) {
        final byte[] cipher = new byte[16];
        for (int i = 0; i < blocksNum; ++i) {
            System.arraycopy(data, offset + i * 16, cipher, 0, 16);
            BytesUtil.xor(cipherState, 0, cipher);
            this.processBlock(cipher, 0);
            System.arraycopy(cipher, 0, data, offset + i * 16, 16);
            System.arraycopy(cipher, 0, cipherState, 0, 16);
        }
    }
    
    public void cbcDec(final byte[] data, final int offset, final int blocksNum, final byte[] cipherState) {
        final byte[] lastBlock = new byte[16];
        final byte[] cipher = new byte[16];
        System.arraycopy(data, offset + (blocksNum - 1) * 16, lastBlock, 0, 16);
        for (int i = blocksNum; i > 0; --i) {
            System.arraycopy(data, offset + (i - 1) * 16, cipher, 0, 16);
            this.processBlock(cipher, 0);
            if (i == 1) {
                BytesUtil.xor(cipherState, 0, cipher);
            }
            else {
                BytesUtil.xor(data, offset + (i - 2) * 16, cipher);
            }
            System.arraycopy(cipher, 0, data, offset + (i - 1) * 16, 16);
        }
        System.arraycopy(lastBlock, 0, cipherState, 0, 16);
    }
}
