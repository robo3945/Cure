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

package it.cure.core.implem.timecontroller.time;

import it.cure.core.implem.Context;
import it.cure.core.implem.simmodel.base.AbstractSimModel;
import it.cure.core.implem.simmodel.base.SimModelFailureType;
import it.cure.core.implem.timecontroller.message.bean.HeartBeatMessage;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.timecontroller.time.ITimestampService;

/**
 * Service that makes the Timestamps
 */
public class BasicTimeStampService implements ITimestampService {
	
	private static final long serialVersionUID = 1584244508414238935L;
	private ISimulationModel[] simulationModel;
	private long timeStamp;
	
	/**
	 * Constructor without SimulationModel: returns the current time
	 */
	public BasicTimeStampService() {
		timeStamp = new java.util.Date().getTime();
	}
	
	/**
	 * Constructor for the Timestamp generated for simulate
	 * @param simulationModel 
	 * @param senderHb 
	 */
	public BasicTimeStampService(ISimulationModel[] simulationModel, HeartBeatMessage senderHb) 
	{	
		if (AbstractSimModel.askOracle(simulationModel, SimModelFailureType.TIMELINE) == true)
		{
			timeStamp = new java.util.Date().getTime();
		}
		else
		{
			// it has been added a minute to the real time to maliciously change the time
			timeStamp =  new java.util.Date().getTime()+ (long) (Context.TIMEOUT_THRESHOLD*1.0/10);
			if (senderHb!=null) 
			{
				// TODO: devo settare il setTimeLineError? Altrimenti ci sono pacchetti Hacked che non vengono considerati
				// viene considerato infatti il sottoinsieme dei pacchetti hacked tra quelli con il setTimeLineError=true
				
				// 24/12/2012 ==> problema Flip Flop delle sperimentazioni
				senderHb.setTimelineError();
				// --
				senderHb.setTimelineHacked();
			}
		}

	}
	

	@Override
	public long getTimeStamp()
	{
		return this.timeStamp;
	}
	
	/**
	 * Set the timestamp: is needed to modify the time
	 * @param time
	 */
	@Override
	public void setTimestamp(long time)
	{
		this.timeStamp = time;
	}

	@Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;
	}
}
