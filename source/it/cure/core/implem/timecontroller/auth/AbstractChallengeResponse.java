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

package it.cure.core.implem.timecontroller.auth;

import it.cure.core.implem.Context;
import it.cure.core.implem.security.SecUtils;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * A class to manage challenge&response 
 *
 */
public abstract class AbstractChallengeResponse implements Serializable{

	private static final long serialVersionUID = -6619462504654886503L;
	// TODO: static password
	private static String password="secretpasswordtoprotect";
	protected String salt;
		
	/**
	 * @param challenge
	 * @return
	 */
	protected String doResponse(String challenge) {
		String cpwd = password + challenge;	
		
		if (salt == null)
			salt = SecUtils.getB64(SecUtils.getRandomBytes(SecUtils.getSeedAsString(), Context.saltLenght));
		
		String hash = makeHash(cpwd, salt);	
		return hash;
	}
	
	
	

	/**
	 * Get the computed salt
	 * @return
	 */
	public String getSalt()
	{
		return this.salt;
	}
	
	/**
	 * Computes the Hash
	 * 
	 * It takes the password and calculates H(H(H(PW+SALT)+SALT)...)
	 * @param salt  
	 * @return
	 */
	protected String makeHash(String password, String salt)
	{
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(Context.CHALLANGE_HASH);
		    md.update((password+salt).getBytes(Context.charset));
		    return SecUtils.getB64(md.digest())+salt;
		    
		} catch (NoSuchAlgorithmException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}

		return null;
	}

	/**
	 * Verify the match between the challenge and the response
	 * 
	 * @param challenge
	 * @param response
	 * @return 
	 */
	public boolean match(String challenge, String response) {
		
		String newResponse = doResponse(challenge);
		if (newResponse.equals(response))
			return true;
		
		return false;
		
	}
	

}
