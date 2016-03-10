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

package it.cure.core.implem.util;

import it.cure.core.implem.Context;

/**
 *
 */
public class Diagnose {

	/**
	 * Print the memory allocation
	 */
	public static void printMemory()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(((Runtime.getRuntime().maxMemory()*1.0/1024)/1024)+" MB").append(";");
		sb.append(((Runtime.getRuntime().totalMemory()*1.0/1024)/1024)+" MB").append(";");
		sb.append(((Runtime.getRuntime().freeMemory()*1.0/1024)/1024)+" MB").append(";");
		Context.getInstance().getLogger().debug("==> HEAP;"+sb.toString());
	}

	/**
	 * Print the packet number
	 * 
	 * @param contString 
	 * 
	 */
	public static void printNumPacket(String contString) {
		Context.getInstance().getLogger().debug("==> #PACKETS: "+contString);
	}

	/**
	 * 
	 */
	public static void printComputationEnd() {
		Context.getInstance().getLogger().debug("=================> END <=================");
		
	}
}
