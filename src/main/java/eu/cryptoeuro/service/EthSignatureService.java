package eu.cryptoeuro.service;

import org.bouncycastle.util.encoders.Hex;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Arrays;

@Component
public class EthSignatureService {

    public ECKey.ECDSASignature parseHexSignature(String signatureHex) {
        byte[] signatureBytes = Hex.decode(signatureHex);
        byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
        byte v = signatureBytes[64];
        ECKey.ECDSASignature ecdsaSignature = ECKey.ECDSASignature.fromComponents(r,s,v);
        return ecdsaSignature;
    }

    public byte[] rawHashSignableMessage(String signableString) {
        return HashUtil.sha3(signableString.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifySignature(byte[] rawHashOfSignableMessage, ECKey.ECDSASignature signature) throws SignatureException {
        return ECKey.signatureToKey(rawHashOfSignableMessage, signature).verify(rawHashOfSignableMessage, signature);
    }

    public String obtainEthAddressFromSignature(byte[] rawHashOfSignableMessage, ECKey.ECDSASignature signature) throws SignatureException {
        return Hex.toHexString(ECKey.signatureToAddress(rawHashOfSignableMessage, signature));
    }
}
