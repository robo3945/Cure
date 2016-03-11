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

package it.cure.core.implem.security.keys;

import it.cure.core.implem.Context;
import it.cure.core.implem.timecontroller.time.BasicTimeStampService;
import it.cure.core.implem.timecontroller.time.bean.Timestamp;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Key Manager
 *
 */
public final class SymKeyMgr {

	private static ArrayList<byte[]> arKeys;
	private static Hashtable<String, SymKeyMgr> instance = new Hashtable<String, SymKeyMgr>();
	
	/**
	 * 
	 * @param keyStorePath
	 * @param numberKeys
	 * @return
	 */
	public static String createKeyStore(String keyStorePath, int numberKeys) {
		SymKeyGenerator skm;
		try {
			
			// Write keys on disk
			skm = new SymKeyGenerator(new File(keyStorePath));
			return skm.serializeToDisk(skm.makeKeys(numberKeys));
			
		} catch (IOException e) {
            Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchAlgorithmException e) {
            Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
		
		return null;
	}

	/**
	 * Select a key from the set
	 * 
	 * @param idx
	 * @return
	 */
	public static byte[] getAKey(int idx) {
		return arKeys.get(idx);
	}
	
	/**
	 * Factory of singleton
	 * @param instanceType 
	 * @return
	 */
	public static SymKeyMgr getInstance(String instanceType)
	{
		SymKeyMgr skm = instance.get(instanceType);
		if (skm == null)
		{
			skm = new SymKeyMgr();
			instance.put(instanceType, skm);
		}		
		return skm;		
	}
	
	/**
	 * @param path
	 * @return
	 */
	public static ArrayList<byte[]> getKeyStore(String path) {
		SymKeyGenerator skm;
		try {
						
			// read keys from the disk
			skm = new SymKeyGenerator(new File(path));
			return skm.deserializeFromDisk();
			
		} catch (IOException e) {
            Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
		
		return null;
	}
		
	
	/**
	 * Set the key lists to use
	 * @param arKeys
	 */
	public static void setKeysList(ArrayList<byte[]> arKeys)
	{
		SymKeyMgr.arKeys = arKeys;
	}
	

	private int instanceKeyIdx;
	
	/**
	 * Private constructor
	 */
	private SymKeyMgr() {
		Random rnd = new Random(new Timestamp(new BasicTimeStampService()).getTimestampAsLong());
		this.instanceKeyIdx = rnd.nextInt(arKeys.size());
	}

	/**
	 * Gets the created key
	 * 
	 * @return
	 */
	public byte[] getKey() {
		return arKeys.get(instanceKeyIdx);
	}	
	
	/**
	 * Gets the created key index 
	 * 
	 * @return
	 */
	public int getKeyIdx() {
		return instanceKeyIdx;
	}
	
}
