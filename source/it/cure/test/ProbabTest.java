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

package it.cure.test;

import it.cure.core.implem.simmodel.base.SimModelFailureType;
import it.cure.core.implem.simmodel.hackfail.ProbabHackFail_NETWORK_SimModel;
import it.cure.core.implem.simmodel.normalfail.ProbabFail_NETWORK_SimModel;

/**
 * This is a test to evaluate the simulation model and the probability
 *
 */
public class ProbabTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		simpleProbabilityTest();
		combinedProbabilityTest();

	}

	/**
	 * This tests the SimulationModel based onto an Attack probability and an Infrastructure failure probability
	 * 
	 * The test is good if the resulting probability is circa the Infrastructure probability
	 */
	protected static void combinedProbabilityTest() {
		ProbabHackFail_NETWORK_SimModel phm = new ProbabHackFail_NETWORK_SimModel(2, 5); 
		
		long contFailure =0;
		long cont =0;
		for (long i=0; i<1000000000L; i++)
		{
			if (phm.askOracle(SimModelFailureType.TIMEOUT)==false)
				contFailure++;
			
			cont++;
		}
		
		System.out.println("----------> Combined Probability test <------------ ");
		System.out.println("Failures number: "+contFailure + " - Total askOracle: " + cont);
		System.out.println("P(I U A): "+ contFailure*1.0/cont);
	}

	
	protected static void simpleProbabilityTest() {
		ProbabFail_NETWORK_SimModel phm = new ProbabFail_NETWORK_SimModel(2); 
		
		long contFailure =0;
		long cont =0;
		for (long i=0; i<1000000000L; i++)
		{
			if (phm.askOracle(SimModelFailureType.TIMEOUT)==false)
				contFailure++;
			
			cont++;
		}
		
		System.out.println("----------> Simple Probability test <------------ ");
		System.out.println("Failures number: "+contFailure + " - Total askOracle: " + cont);
		System.out.println("P(I): "+ contFailure*1.0/cont);
	}
	
}
