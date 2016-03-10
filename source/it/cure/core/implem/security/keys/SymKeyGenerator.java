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

import it.cure.core.implem.security.SecUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * it creates a list of keys to use
 *
 */
public class SymKeyGenerator {

	private File keyFileName;
	
	/**
	 * @param keyFileName 
	 * @throws IOException 
	 * 
	 */
	public SymKeyGenerator(File keyFileName) throws IOException {
		this.keyFileName = keyFileName;	
	}
	
	
	/**
	 * Deserializes to disk the list of the generated keys
	 * 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<byte[]> deserializeFromDisk() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(this.keyFileName));
		BASE64Decoder decoder = new BASE64Decoder();
		
		ArrayList<byte[]> arKeys = null;
		
		String line;
		while ((line = br.readLine())!=null)
		{
			if (arKeys==null)
				arKeys = new ArrayList<byte[]>();
			arKeys.add(decoder.decodeBuffer(line));
		}

		br.close();
		
		return arKeys;
	}
	
	/**
	 * Creates a list of generated symmetric keys
	 * 
	 * @param number number of key to generate
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public ArrayList<byte[]> makeKeys(int number) throws NoSuchAlgorithmException
	{
		ArrayList<byte[]> ar = new ArrayList<byte[]>();
		for (int i=0;i<number;i++)
		{
			ar.add(SecUtils.generateKey());
		}
		
		return ar;
	}
	
	/**
	 * Serializes to disk the list of the generated keys
	 * @param arKeys
	 * @return
	 * @throws IOException
	 */
	public String serializeToDisk(ArrayList<byte[]> arKeys) throws IOException
	{
		File ftemp = this.keyFileName;		
		BufferedWriter bw = new BufferedWriter(new FileWriter(ftemp));
		BASE64Encoder encoder = new BASE64Encoder();
		
		for (byte[] raw: arKeys)
		{
			bw.write(encoder.encodeBuffer(raw));
			//bw.newLine();
		}
		bw.flush();
		bw.close();
		
		return ftemp.getPath();
	}
}
