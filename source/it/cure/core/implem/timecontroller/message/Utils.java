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

package it.cure.core.implem.timecontroller.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Generic utilities
 *
 */
public class Utils {
	/**
	 * Deserialize the buffer into an Object (to cast)
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static Object deserializeObject(byte[] buffer) throws IOException,
			ClassNotFoundException 
	{	
		
		return commDeserializeObject(buffer);
		//return ownDeserializeObject(buffer);
	}

	/**
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	protected static Object commDeserializeObject(byte[] buffer)
			throws IOException {
		//GzipCompressorInputStream  stream = new GzipCompressorInputStream(new ByteArrayInputStream(buffer));
		ByteArrayInputStream  stream = new ByteArrayInputStream(buffer);
		return SerializationUtils.deserialize(stream);
	}

	/**
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	/*
	protected static Object ownDeserializeObject(byte[] buffer)
			throws IOException, ClassNotFoundException {
		// Deserialize from a byte array
		Object ret;
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new GzipCompressorInputStream(new ByteArrayInputStream(buffer)));
			ret = in.readObject();
		}
		finally
		{
			if (in!=null)
				in.close();
		}
			    
		return ret;
	}
	*/
	
	/**
	 * Serialize an object to an array of byte
	 * 
	 * @param obj
	 * @return
	 * @throws IOException 
	 */
	public static byte[] serializeObject(Serializable obj) throws IOException {
		
		return commSerializeObject(obj);
		
		//return ownSerializeObject(obj);
	}

	/**
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	protected static byte[] commSerializeObject(Serializable obj)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		//GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bos);
		SerializationUtils.serialize(obj, bos);
		return bos.toByteArray();
	}

	/**
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	/*
	protected static byte[] ownSerializeObject(Serializable obj) throws IOException {
		ObjectOutputStream out = null;
		ByteArrayOutputStream bos = null;
		byte[] res;
		try
		{
			bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(new GzipCompressorOutputStream(bos)) ;
			out.writeObject(obj);
			res = bos.toByteArray();
		}
		finally
		{
			if (out!=null)
				out.close();
			if (bos!=null)
				bos.close();
		}
	    return res;
	}
	*/
}
