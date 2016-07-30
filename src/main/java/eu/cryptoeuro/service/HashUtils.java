package eu.cryptoeuro.service;

import java.security.MessageDigest;

import org.bouncycastle.jcajce.provider.digest.Keccak.Digest256;
import org.bouncycastle.jcajce.provider.digest.Keccak.DigestKeccak;

public class HashUtils {

    public static String keccak256(final String input) {
        final DigestKeccak keccak256 = new Digest256();
        keccak256.update(input.getBytes());
        return hashToString(keccak256);
    }

    ///// PRIVATE METHODS /////

    private static String hashToString(MessageDigest hash) {
        return hashToString(hash.digest());
    }

    private static String hashToString(byte[] hash) {
        StringBuffer buff = new StringBuffer();
        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }
        return buff.toString();
    }

}