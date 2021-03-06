package es.uji.security.crypto.jxades;

import es.uji.security.crypto.ISignFormatProvider;
import es.uji.security.crypto.SignatureResult;
import es.uji.security.crypto.VerificationResult;
import es.uji.security.crypto.config.OS;
import es.uji.security.crypto.test.BaseCryptoAppletTest;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class JXAdESTest extends BaseCryptoAppletTest
{
    @Before
    public void init() throws FileNotFoundException
    {
        signatureOptions.setDataToSign(new ByteArrayInputStream(data));
    }

    @Test
    public void jxadesEnvelopedWithoutCosign() throws Exception
    {
        // Sign

        ISignFormatProvider signFormatProvider = new JXAdESSignatureFactory();
        SignatureResult signatureResult = signFormatProvider.formatSignature(signatureOptions);

        showErrors(signatureResult, "target/out-jxades-enveloped.xml");

        // Verify

        byte[] signedData = OS.inputStreamToByteArray(new FileInputStream("target/out-jxades-enveloped.xml"));

        JXAdESSignatureVerifier signatureVerifier = new JXAdESSignatureVerifier();
        VerificationResult verificationResult = signatureVerifier.verify(signedData);

        showErrors(verificationResult);
    }

    @Test
    public void jxadesEnvelopedCosign() throws Exception
    {
        // Sign

        signatureOptions.setCoSignEnabled(true);

        ISignFormatProvider signFormatProvider = new JXAdESSignatureFactory();
        SignatureResult signatureResult = signFormatProvider.formatSignature(signatureOptions);

        showErrors(signatureResult, "target/out-jxades-enveloped-cosign.xml");

        // Verify

        byte[] signedData = OS.inputStreamToByteArray(new FileInputStream("target/out-jxades-enveloped-cosign.xml"));

        JXAdESSignatureVerifier signatureVerifier = new JXAdESSignatureVerifier();
        VerificationResult verificationResult = signatureVerifier.verify(signedData);

        showErrors(verificationResult);
    }

    @Test
    public void jxadesDetachedCosign() throws Exception
    {
        // CoSign

        byte[] data = "<?xml version=\"1.0\"?><root><d Id=\"D0\">a</d><d Id=\"D1\">b</d></root>"
                .getBytes();

        signatureOptions.setEnveloped(false);
        signatureOptions.setCoSignEnabled(true);
        signatureOptions.setReferences(Arrays.asList(new String[]{"D0", "D1"}));

        for (int i = 0; i < 3; i++)
        {
            signatureOptions.setDataToSign(new ByteArrayInputStream(data));

            ISignFormatProvider signFormatProvider = new JXAdESSignatureFactory();
            SignatureResult signatureResult = signFormatProvider.formatSignature(signatureOptions);

            showErrors(signatureResult, "target/out-jxades-detached-cosign.xml");

            data = OS.inputStreamToByteArray(new FileInputStream("target/out-jxades-detached-cosign.xml"));
        }

        // Verify

        data = OS.inputStreamToByteArray(new FileInputStream("target/out-jxades-detached-cosign.xml"));

        JXAdESSignatureVerifier signatureVerifier = new JXAdESSignatureVerifier();
        VerificationResult verificationResult = signatureVerifier.verify(data);

        showErrors(verificationResult);
    }
}