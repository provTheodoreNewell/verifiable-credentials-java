package com.danubetech.verifiablecredentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.LinkedHashMap;

import com.apicatalog.jsonld.api.JsonLdError;
import foundation.identity.jsonld.JsonLDObject;
import foundation.identity.jsonld.JsonLDUtils;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import org.junit.jupiter.api.Test;

import com.github.jsonldjava.utils.JsonUtils;

import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.signer.RsaSignature2018LdSigner;
import info.weboftrust.ldsignatures.suites.SignatureSuites;

class SignTest {

	@Test
	void testSign() throws Throwable {

		VerifiableCredential verifiableCredential = VerifiableCredential.fromJson(new InputStreamReader(VerifyTest.class.getResourceAsStream("input.vc.jsonld")));

		URI creator = URI.create("did:sov:1yvXbmgPoUm4dl66D7KhyD#keys-1");
		Date created = JsonLDUtils.DATE_FORMAT.parse("2018-01-01T21:19:10Z");
		String domain = null;
		String nonce = "c0ae1c8e-c7e7-469f-b252-86e6a0e7387e";

		RsaSignature2018LdSigner signer = new RsaSignature2018LdSigner(TestUtil.testRSAPrivateKey);
		signer.setCreator(creator);
		signer.setCreated(created);
		signer.setDomain(domain);
		signer.setNonce(nonce);
		LdProof ldSignature = signer.sign(verifiableCredential, true, false);

		System.out.println(verifiableCredential.t);

		assertEquals(SignatureSuites.SIGNATURE_SUITE_RSASIGNATURE2018.getTerm(), ldSignature.getType());
		assertEquals(creator, ldSignature.getCreator());
		assertEquals(created, ldSignature.getCreated());
		assertEquals(domain, ldSignature.getDomain());
		assertEquals(nonce, ldSignature.getNonce());
		assertEquals("eyJjcml0IjpbImI2NCJdLCJiNjQiOmZhbHNlLCJhbGciOiJSUzI1NiJ9..pZtcYsR_vEtm5ZLEGNJZPYuWQeD_drBG55gDrX4V-Zxe-R0ue90QzfLn9ZAheBrnWxQNobOsmc0wLBLnSNp5fMbmxHzaMuPadkMXgyqdgH6r13YHidLhtsg8OWGBU0nlFQe5NPztP8HJdgdTmK8ohQlx1pB7BQuB3-iY_cHO7PLuVJFplI616v7zINW46SNc6PE2cJ_O-dnehA_PaNCnUn7s-TfqTYC7LQ2N95XImBt9zW5DYE7NRY7ZZh1sBNaSnHweOYZay-W6u789J3zTFxgbl-hZGziFA4EOJoWUAdb1vCBzlBWasfmkD0LAxlv7UV0Fp3wG2laIFiTwgrm9eg", ldSignature.getJws());

		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(TestUtil.testEd25519PublicKey);
		boolean verify = verifier.verify(verifiableCredential);
		assertTrue(verify);
	}
}
