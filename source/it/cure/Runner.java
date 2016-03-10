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


package it.cure;

import it.cure.core.implem.Agent;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * It is a test runner
 *
 */
public class Runner {
	/**
	    * Bare minimum heap memory for starting app and allowing
	    * for reasonable sized images (note deliberate 1 MB
	    * smaller than 512 due to rounding/non accurate free 
	    * heap calculation). The solution allows JVMs with
	    * enough heap already to just start without spawning 
	    * a new process. 
	    */
	    private final static int MIN_HEAP = 511;
	    
	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		 

		    // Do we have enough memory already (some VMs and later Java 6 
		    // revisions have bigger default heaps based on total machine memory)?
		    float heapSizeMegs = ((float) Runtime.getRuntime().maxMemory()/1024)/1024;

		    // Yes so start
		    if (heapSizeMegs > MIN_HEAP) 
		    {
		    	System.out.println(heapSizeMegs);
		    	Agent.main(args);
		    } 
		    else 
		    {
		    	System.out.println("Alternate to max memory");
		    	String pathToJar = Runner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		    	ProcessBuilder pb = new ProcessBuilder("java","-Xmx1024m", "-classpath", pathToJar, "it.cure.implem.Agent");
			    pb.start();
		    }

	}

}
