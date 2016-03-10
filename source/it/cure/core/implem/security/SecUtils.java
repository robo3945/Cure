/*******************************************************************************
 The MIT License (MIT)

Copyright (c) 2015,2016 Roberto Battistoni, Roberto Di Pietro, Flavio Lombardi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*******************************************************************************/

package it.cure.core.implem.security;

import it.cure.core.implem.Context;
import it.cure.core.implem.timecontroller.time.BasicTimeStampService;
import it.cure.core.implem.timecontroller.time.bean.Timestamp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * Sec API Utilities
 * 
 */
public class SecUtils {

	/**
	 * Decrypts a buffer
	 * 
	 * @param key
	 * @param bufferToDecrypt
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] decrypt(byte[] key, byte[] bufferToDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, Context.CRYPTO_ALGO);

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance(Context.CRYPTO_ALGO);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		
		return cipher.doFinal(bufferToDecrypt);
	}
	
	/**
	 * Encrypts a buffer
	 * @param key
	 * @param bufferToEncrypt
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] encrypt(byte[] key, byte[] bufferToEncrypt)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, Context.CRYPTO_ALGO);

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance(Context.CRYPTO_ALGO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		
		return cipher.doFinal(bufferToEncrypt);
	}

	/**
	 * it must be called only once to create a symmetric password and then
	 * hardcodes it into the code
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateKey() throws NoSuchAlgorithmException {
		// Get the KeyGenerator
		KeyGenerator kgen = KeyGenerator.getInstance(Context.CRYPTO_ALGO);
		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}

	/**
	 * @param raw
	 * @return
	 */
	public static String getB64(byte[] raw) {		
		return Base64.encodeBase64String(raw);
	}
	
	/**
	 * Transforms a byte sequence in an hex string
	 * 
	 * @param raw
	 * @return
	 */
	public static String getHex(byte[] raw) {		
		return Hex.encodeHexString(raw);
	}

	/**
	 * @param seed
	 * @param numBytes
	 * @return
	 */
	public static byte[] getRandomBytes(String seed, int numBytes) {
		SecureRandom random = new SecureRandom(seed.getBytes(Context.charset));
		byte bytes[] = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * @return
	 */
	public static Long getSeedAsLong() {
		return new Timestamp(new BasicTimeStampService()).getTimestampAsLong();
	}
	
	/**
	 * @return
	 */
	public static String getSeedAsString() {
		return Long.toString(new Timestamp(new BasicTimeStampService()).getTimestampAsLong());
	}
}
