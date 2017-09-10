package eu.cryptoeuro.ethereumcryptography

import eu.cryptoeuro.euro2paymenturi.Euro2PaymentURI
import eu.cryptoeuro.service.EthSignatureService
import org.bouncycastle.util.encoders.Hex
import org.ethereum.crypto.ECKey
import org.ethereum.crypto.HashUtil
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class EthereumCryptoTests extends Specification {

    EthSignatureService ethSignatureService = new EthSignatureService()

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

    def "get signature and message hash and derive the signer address from the euro2PaymentUri - solution example"() {
        given:

            String euro2PaymentRequestUri = "euro2:0xae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e/payment?amount=1200&signature_type=ETH&message=eee&signature=c5bad2f2fafd3458358f6f5eb5d6eca009d76d2a052df48c54e75eee6463111a304404db1b462e25bb20ac01be90345bc6eed529de78ac73c578f36d489b47c01c"
            Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse(euro2PaymentRequestUri)
            euro2PaymentURI.getAddress()

            String signatureString = euro2PaymentURI.getSignature();
            byte[] signatureBytes = Hex.decode(signatureString);
            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
            byte v = signatureBytes[64]

            ECKey.ECDSASignature ecdsaSignature = ECKey.ECDSASignature.fromComponents(r,s,v)
            String uriWithoutSignature = euro2PaymentRequestUri.replace("&signature=" + euro2PaymentURI.getSignature(), "")
            assert uriWithoutSignature == "euro2:0xae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e/payment?amount=1200&signature_type=ETH&message=eee"
        when:
            byte[] uriWithoutSignatureRawHash = HashUtil.sha3(uriWithoutSignature.getBytes(StandardCharsets.UTF_8));

        then:
            ECKey.signatureToKey(uriWithoutSignatureRawHash, ecdsaSignature).verify(uriWithoutSignatureRawHash, ecdsaSignature)
            String requestorAddress = Hex.toHexString(ECKey.signatureToAddress(uriWithoutSignatureRawHash, ecdsaSignature))
            println requestorAddress
            "ae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e" == requestorAddress
    }

    def "get signature and message hash and derive the signer address from the euro2PaymentUri using ethSignatureService"() {
        given:
            String euro2PaymentRequestUri = "euro2:0xae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e/payment?amount=1200&signature_type=ETH&message=eee&signature=c5bad2f2fafd3458358f6f5eb5d6eca009d76d2a052df48c54e75eee6463111a304404db1b462e25bb20ac01be90345bc6eed529de78ac73c578f36d489b47c01c"
            Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse(euro2PaymentRequestUri)
            ECKey.ECDSASignature ecdsaSignature = ethSignatureService.parseHexSignature(euro2PaymentURI.getSignature());
            String uriWithoutSignature = euro2PaymentRequestUri.replace("&signature=" + euro2PaymentURI.getSignature(), "")
            assert uriWithoutSignature == "euro2:0xae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e/payment?amount=1200&signature_type=ETH&message=eee"
        when:
            byte[] rawHashOfUriWithoutSignature = ethSignatureService.rawHashSignableMessage(uriWithoutSignature);
        then:
            ethSignatureService.verifySignature(rawHashOfUriWithoutSignature, ecdsaSignature)
            String requestorAddress = ethSignatureService.obtainEthAddressFromSignature(rawHashOfUriWithoutSignature, ecdsaSignature)
            println requestorAddress
            "ae0a73fdf0fa05e118b9b4e4da41d0e77d4b932e" == requestorAddress
    }
}
