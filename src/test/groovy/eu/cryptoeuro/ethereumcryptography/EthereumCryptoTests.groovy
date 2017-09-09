package eu.cryptoeuro.ethereumcryptography

import org.bouncycastle.util.encoders.Hex
import org.ethereum.crypto.ECKey
import org.ethereum.crypto.HashUtil
import spock.lang.Ignore
import spock.lang.Specification

class EthereumCryptoTests extends Specification {

    def "build signature and they obtain address from it & verify it"() {
        given:
            BigInteger r = new BigInteger("c52c114d4f5a3ba904a9b3036e5e118fe0dbb987fe3955da20f2cd8f6c21ab9c", 16);
            BigInteger s = new BigInteger("6ba4c2874299a55ad947dbc98a25ee895aabf6b625c26c435e84bfd70edf2f69", 16);
        when:
            ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(r.toByteArray(), s.toByteArray(), (byte) 0x1b);
            byte[] rawtx = Hex.decode("f82804881bc16d674ec8000094cd2a3d9f938e13cd947ec05abc7fe734df8dd8268609184e72a0006480");
            byte[] rawHash = HashUtil.sha3(rawtx);
            byte[] address = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
            ECKey key = ECKey.signatureToKey(rawHash, sig);
            System.out.println("Signature public key\t: " + Hex.toHexString(key.getPubKey()));
            System.out.println("Sender is\t\t: " + Hex.toHexString(key.getAddress()));
        then:
            key == ECKey.signatureToKey(rawHash, sig.toBase64())
            key == ECKey.recoverFromSignature(0, sig, rawHash)
            key.getPubKey() == ECKey.recoverPubBytesFromSignature(0, sig, rawHash)
            address == key.getAddress()
            address == ECKey.signatureToAddress(rawHash, sig)
            address == ECKey.signatureToAddress(rawHash, sig.toBase64())
            address == ECKey.recoverAddressFromSignature(0, sig, rawHash)
            key.verify(rawHash, sig) == true
    }

    @Ignore
    def "get signature and message hash and derive the signer address"() {
        given:
            String hexSignature = "0x"
            byte[] signatureDER = Hex.decode(hexSignature)
            ECKey.ECDSASignature sig = ECKey.ECDSASignature.decodeFromDER(signatureDER)
        when:
            byte[] rawtx = Hex.decode("f82804881bc16d674ec8000094cd2a3d9f938e13cd947ec05abc7fe734df8dd8268609184e72a0006480");
            byte[] rawHash = HashUtil.sha3(rawtx);
            byte[] address = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
            ECKey key = ECKey.signatureToKey(rawHash, sig);
            System.out.println("Signature public key\t: " + Hex.toHexString(key.getPubKey()));
            System.out.println("Sender is\t\t: " + Hex.toHexString(key.getAddress()));
        then:
            key == ECKey.signatureToKey(rawHash, sig.toBase64())
            key == ECKey.recoverFromSignature(0, sig, rawHash)
            key.getPubKey() == ECKey.recoverPubBytesFromSignature(0, sig, rawHash)
            address == key.getAddress()
            address == ECKey.signatureToAddress(rawHash, sig)
            address == ECKey.signatureToAddress(rawHash, sig.toBase64())
            address == ECKey.recoverAddressFromSignature(0, sig, rawHash)
            key.verify(rawHash, sig) == true
    }
}
