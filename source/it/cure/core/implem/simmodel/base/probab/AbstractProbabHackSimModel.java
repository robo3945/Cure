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

package it.cure.core.implem.simmodel.base.probab;

import it.cure.core.implem.timecontroller.time.BasicTimeStampService;
import it.cure.core.implem.timecontroller.time.bean.Timestamp;

import java.util.Random;

/**
 * A Model
 *
 */
public abstract class AbstractProbabHackSimModel extends AbstractProbabSimModel {

	private static final long serialVersionUID = 1834260663147426422L;
	protected Random rndNormal;
	protected double normalFailProbab;
	protected double underAttackProbab;
	protected Random rndHack;
	protected int scaleNormal = 100;
	protected int scaleHacked = 100;
	protected int factorNormal;
	protected int factorHacked;
	

	
	/**
	 * Constructor (it models the case of only one attacker for the cloud: when the attacker has finished to attack a node it's not possible another attack on that node
	 * 
	 * @param normalFailProb it's the normal failure probability of the infrastructure 
	 * @param hackFailProbab it's the probability that an attack is finished or not; P(H) ==> H={"it could be under attack"} and 1-H = {"the attack is just finished"} 
	 * 
	 */
	public AbstractProbabHackSimModel(double normalFailProb, double hackFailProbab) {
		super();
		
		this.rndNormal = new Random(new Timestamp(new BasicTimeStampService()).getTimestampAsLong());
		this.rndHack = new Random(new Timestamp(new BasicTimeStampService()).getTimestampAsLong()+1000);

		this.normalFailProbab = normalFailProb;
		this.underAttackProbab = hackFailProbab;
		
		this.factorNormal = getPower10Factor(normalFailProbab);
		this.factorHacked = getPower10Factor(underAttackProbab);		
		
	}

	protected boolean askOracle() 
	{
				
		// extracts a number from 100
		int resNormal = rndNormal.nextInt(scaleNormal * factorNormal);
		int resHack = rndHack.nextInt(scaleHacked * factorHacked);
		
		//if (resHack<underAttackProbab*factorHacked)
		//	isAttackFinished = true;
		
		/*if (isAttackFinished == false)
		{*/
			// this is the first case when both the infrastructure failure or attack are possible
			// I = {there is an infrastructure failure} and A ={there is an attack}
			// A and B ARE COMPATIBLE because if there is an attack there could be and infrastructure failure too => P(A n B) >0 
			// 		and P(A n B) = P(A)*P(B) because the attack is INDEPENDENT from the infrastructure failure
			// T0 -> Th: A U B => P(A U B) = P(A) + P(B) - P(A n B) = P(A) + P(B) - P(A)*P(B)
			// T>Th: P(I)
			
			
			if (resNormal < normalFailProbab*factorNormal || resHack<underAttackProbab*factorHacked )
				return false;
		/*}
		else
		{
			if (resNormal < normalFailProbab*factorNormal)
				return false;				
		}*/
		
		
		return true;
	}

}
