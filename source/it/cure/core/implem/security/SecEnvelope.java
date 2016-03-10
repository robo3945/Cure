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

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This is a secure envelope for buffer
 * 
 * It is needed to manage the length of the buffer after the encrypting process. The routine getting the 
 * packet fill a predetermined buffer so the returned buffer would be different in length respect to the encrypted  buffer
 * raising a BadPaddingException. 
 * 
 * The envelope object is a trick to serialize/deserialize an object of certain size!
 * 
 *
 */
public class SecEnvelope implements Serializable{

	private static final long serialVersionUID = -3526755881940960726L;
	private byte[] cryptedBuffer;
	private int idxKey;
	
	/**
	 * Constructor
	 * 
	 * @param bufferToEncrypt
	 * @param key 
	 * @param keyIndex
	 * 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public SecEnvelope(byte[] bufferToEncrypt, byte[] key, int keyIndex) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		super();
		
		this.idxKey = keyIndex;
		this.cryptedBuffer = SecUtils.encrypt(key, bufferToEncrypt);		
	}

	/**
	 * Get the decrypted buffer
	 * @param key 
	 * 
	 * @return the cryptedBuffer
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public byte[] getDecryptedBuffer(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return SecUtils.decrypt(key, cryptedBuffer);
	}
	
	/**
	 * @return the idxKey
	 */
	public int getIdxKey() {
		return idxKey;
	}
	
	
	
}
