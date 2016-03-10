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

package it.cure.core.implem.timecontroller.message.bean;

import it.cure.core.implem.timecontroller.time.BasicTimeStampService;
import it.cure.core.implem.timecontroller.time.bean.Timestamp;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.model.ISimulationParameter;
import it.cure.core.interfaces.timecontroller.time.ITimestamp;

/**
 * The heartbeat is packet send to the machines
 */
public class HeartBeatMessage extends AbstractMessage implements ISimulationParameter{
	
	private static final long serialVersionUID = -2678476563424252055L;
	private Signal signal;
	private ISimulationModel[] simulationModel;
	private ITimestamp ts;
	private boolean isATimelineError;
	private boolean isResponseHacked;
	private boolean isTimelineHacked;
	private boolean isTimeoutHacked;	


	/**
	 * Constructor that defines a new timestamp
	 * 
	 * @param signal the signal transported inside of the heartbeat
	 * @param simulationModel 
	 */
	public HeartBeatMessage(Signal signal, ISimulationModel[] simulationModel) {
		this(simulationModel);
		this.signal = signal;
	}

	/**
	 * @param simulationModel
	 */
	public HeartBeatMessage(ISimulationModel[] simulationModel) {
		super();
		
		this.simulationModel = simulationModel;
		this.ts = new Timestamp(new BasicTimeStampService(this.simulationModel, this));
		this.signal = new Signal("");
	}

	public Signal getSignal() {
		return this.signal;
	}
	
	public boolean isTimelineError() {
		return this.isATimelineError;
	}

	public ITimestamp getTimestamp() {		
		return ts;
	}

	public boolean isResponseVerified() {
		return this.response.match(challenge.getStringChallenge(), this.response.getStringResponse());
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
		
	}

	@Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;		
	}


	/**
	 * @return the simulationModel
	 */
	public ISimulationModel[] getSimulationModel() {
		return simulationModel;
	}


	public void setTimelineError() {
		this.isATimelineError = true;
	}

	@Override 
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("T: ").append(this.getTimestamp().getTimestampAsLong()).append(" - SIG: (((").append(this.getSignal().getSignalText()+")))");
		return sb.toString();		
	}


	public boolean isResponseHacked() {
		return isResponseHacked;
	}


	public void setResponseHacked() {
		isResponseHacked = true;
	}


	public boolean isTimeoutHacked() {
		return isTimeoutHacked;
	}


	public void setTimeoutHacked() {
		isTimeoutHacked = true;
	}

	public boolean isTimelineHacked() {
		return isTimelineHacked;
	}

	public void setTimelineHacked() {
		isTimelineHacked = true;
	}


}
