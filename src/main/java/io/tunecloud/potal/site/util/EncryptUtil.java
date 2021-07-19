/**
 * 
 */
package io.tunecloud.potal.site.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtil.class);
	private String iv;
	private Key keySpec;
	private static String aesKey;
	private static final String RSA_WEB_KEY = "_RSA_WEB_Key_";
	private static final String RSA_INSTANCE = "RSA";

	@Value("${encrypt.aeskey}")
	public void setAesKey(String aesKey) {
		aesKey = aesKey;
	}

	public EncryptUtil() {
	}

	public EncryptUtil(String stringKey) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[32];
		byte[] b = stringKey.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}

		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		this.iv = stringKey.substring(0, 16);
		this.keySpec = keySpec;
	}

	public EncryptUtil(String stringKey, String iv) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[32];
		byte[] b = stringKey.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}

		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		this.iv = iv;
		this.keySpec = keySpec;
	}

	public static String encrypt(String src) throws Exception {
		return encrypt(src, "SHA-256");
	}

	public static String encrypt(String src, String algorithm) throws Exception {
		byte[] txtByte = src.getBytes();
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(txtByte);
		byte[] digest = md.digest();
		byte[] base64 = Base64.encodeBase64(digest);
		return new String(base64, "UTF-8");
	}

	public String encryptAES(String str)
			throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(1, this.keySpec, new IvParameterSpec(this.iv.getBytes()));
		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));
		return enStr;
	}

	public static String encryptAESThymeleaf(String str) {
		String encryptStr = null;

		try {
			EncryptUtil encryptUtil = new EncryptUtil(aesKey);
			if (StringUtils.isNotEmpty(str)) {
				encryptStr = encryptUtil.encryptAES(str);
			}
		} catch (UnsupportedEncodingException | GeneralSecurityException var3) {
			LOGGER.error("GeneralSecurityException | UnsupportedEncodingException", var3);
		}

		return encryptStr;
	}

	public String decryptAES(String str)
			throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(2, this.keySpec, new IvParameterSpec(this.iv.getBytes()));
		byte[] byteStr = Base64.decodeBase64(str.getBytes());
		return new String(c.doFinal(byteStr), "UTF-8");
	}

	public static String decryptAESThymeleaf(String encryptStr) {
		String decryptStr = null;

		try {
			EncryptUtil encryptUtil = new EncryptUtil(aesKey);
			if (StringUtils.isNotEmpty(encryptStr)) {
				decryptStr = encryptUtil.decryptAES(encryptStr);
			}
		} catch (UnsupportedEncodingException | GeneralSecurityException var3) {
			LOGGER.error("GeneralSecurityException | UnsupportedEncodingException", var3);
		}

		return decryptStr;
	}

	public static void main(String[] args) throws Exception {
		String securityKey = "TUNECLOUD SAAS PROVISIONING SYSTEM";
		EncryptUtil encryptUtil = new EncryptUtil(securityKey);
		String accessKey = "932a6c1b-ef0f-4d41-940e-9cd69f1f764b";
		String secretKey = "Nn4i3CaJVelTWtETkM63eMaSTwKuZoEY-u";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("_" + encryptUtil.encryptAES("010-7576-1707"));
			LOGGER.debug("---ACCESS_KEY" + encryptUtil.encryptAES(accessKey));
			LOGGER.debug("---SECRET_KEY" + encryptUtil.encryptAES(secretKey));
			accessKey = "TBmfgOPRvKFcs6QtvG8byvSzfZLEOxOJ8VNsSwu077c=";
			secretKey = "uJN0bzFJdBqacXPHy0CHRTj6rlG1rj5cwg5EHzaj4QRkr5/YE0wTjE7aF9A9LFxv";
			LOGGER.debug("---decrypt:" + encryptUtil.decryptAES(accessKey));
			LOGGER.debug("---decrypt:" + encryptUtil.decryptAES(secretKey));
			LOGGER.debug("---decrypt:" + encryptUtil.decryptAES("dPoOGZCIfSyjiHngra+FIYI9V9gysLNVBVgLSQ2qVP0="));
		}

	}

	public static void initRsa(HttpServletRequest request) {
		HttpSession session = request.getSession();

		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			KeyPair keyPair = generator.genKeyPair();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			session.setAttribute("_RSA_WEB_Key_", privateKey);
			RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			String publicKeyModulus = publicSpec.getModulus().toString(16);
			String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
			request.setAttribute("RSAModulus", publicKeyModulus);
			request.setAttribute("RSAExponent", publicKeyExponent);
		} catch (Exception var10) {
			LOGGER.error("Exception", var10);
		}

	}

	private static String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(2, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8");
		return decryptedValue;
	}

	public static String decryptRsa(HttpSession session, String securedValue) throws Exception {
		PrivateKey privateKey = (PrivateKey) session.getAttribute("_RSA_WEB_Key_");
		return decryptRsa(privateKey, securedValue);
	}

	public static String decryptRsa(HttpServletRequest request, String securedValue) throws Exception {
		HttpSession session = request.getSession();
		return decryptRsa(session, securedValue);
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex != null && hex.length() % 2 == 0) {
			byte[] bytes = new byte[hex.length() / 2];

			for (int i = 0; i < hex.length(); i += 2) {
				byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
				bytes[(int) Math.floor((double) (i / 2))] = value;
			}

			return bytes;
		} else {
			return new byte[0];
		}
	}

	public static void dropRsaKey(HttpSession session) {
		session.removeAttribute("_RSA_WEB_Key_");
	}

	public static void dropRsaKey(HttpServletRequest request) {
		HttpSession session = request.getSession();
		dropRsaKey(session);
	}
}