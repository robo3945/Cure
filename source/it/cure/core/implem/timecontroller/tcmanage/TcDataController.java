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

package it.cure.core.implem.timecontroller.tcmanage;

import it.cure.core.implem.Context;
import it.cure.core.implem.timecontroller.message.bean.AbstractMessage;
import it.cure.core.implem.timecontroller.message.bean.HeartBeatMessage;
import it.cure.core.implem.timecontroller.message.bean.Signal;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * It is the place where the messages are stored and managed
 *
 */
public class TcDataController  {

	private Map<Integer, AbstractMessage> htForResponse;
	private Map<Integer, AbstractMessage> htForTimeline;
	private Map<Integer, AbstractMessage> htForTimeout;

	private Map<Integer, TreeMap<Integer, AbstractMessage>> recvForSenderList;
	
	private String contextName;
	private TcTracer tracer;
	private boolean stopCommanderRun;
	private boolean stopControllerRun;
	private TcTimelineChecker checker;
	
	
	/**
	 * Constructor 
	 * 
	 * @param controllerName
	 * @param traceFilePath 
	 * @param runArgs 
	 */
	public TcDataController(String controllerName, String traceFilePath, String runArgs) {
		this.contextName = controllerName;
		
		
        this.htForResponse = new ConcurrentHashMap<Integer, AbstractMessage>();
        this.htForTimeline = new ConcurrentHashMap<Integer, AbstractMessage>();
        this.htForTimeout = new ConcurrentHashMap<Integer, AbstractMessage>();
        this.recvForSenderList = new ConcurrentHashMap<Integer, TreeMap<Integer, AbstractMessage>>();

        this.checker = new TcTimelineChecker(this);
        
        // initialize the file
        
        tracer = new TcTracer(this.contextName, traceFilePath,  runArgs, Context.MOD_FOR_TRACES_FLUSH);
        tracer.setPrinterFileWriter();
             
	}

	/**
	 * Messages received from others
	 * 
	 * @param message
	 */
	public void putInRecvdQueue(AbstractMessage message) {		
		this.htForResponse.put(message.getId(),message);		
		this.htForTimeline.put(message.getId(), message);		
		// (29/12/2012): aggiunta questa riga
		this.htForTimeout.put(message.getId(), message);
		
		// check the timeline of the received message
		this.checker.setFlagsForTimeline((HeartBeatMessage) message);
		
		// set the timestamp of the last arrived messages
		//this.checker.setTimestampForTimeout((IHeartBeat)message);
	}
	

	 
	/**
	 * Get the Context Name
	 * @return
	 */
	public String getContextName() {
		return contextName;
	}
			
	/**
	 * 
	 * @return
	 */
	public Map<Integer, AbstractMessage> getHtForResponse() {
		return htForResponse;
	}


	/**
	 * 
	 * @return
	 */
	public Map<Integer, AbstractMessage> getHtForTimeline() {
		return htForTimeline;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<Integer, AbstractMessage> getHtForTimeout() {
		return htForTimeout;
	}


	/**
	 * @param contextName the contextName to set
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}
	
	
	/**
	 * Write the signal to the owner Signal Panel
	 * 
	 * @param signal
	 * @param signalForExp 
	 */
	public void writeToSignalPanel(Signal signal, Signal signalForExp)
	{
		//System.out.println(signal.getSignalText());
		this.tracer.appendToTraceFile(signal.getSignalText(), signalForExp.getSignalText());
	}

	/**
	 * @param stopRun the stopRun to set
	 */
	public synchronized void setStopCommanderRun(boolean stopRun) {
		this.stopCommanderRun = stopRun;
	}

	/**
	 * @return the stopRun
	 */
	public synchronized boolean isStopCommanderRun() {
		return stopCommanderRun;
	}

	
	/**
	 * @param stopRun the stopRun to set
	 */
	public synchronized void setStopControllerRun(boolean stopRun) {
		this.stopControllerRun = stopRun;
	}

	/**
	 * @return the stopRun
	 */
	public synchronized boolean isStopControllerRun() {
		return stopControllerRun;
	}
	
	/**
	 * @return the recvForSenderList
	 */
	public Map<Integer, TreeMap<Integer, AbstractMessage>> getRecvForSenderList() {
		return recvForSenderList;
	}
}
