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

import it.cure.core.enums.ENodeType;
import it.cure.core.implem.Context;
import it.cure.core.implem.timecontroller.base.AbstractTimeController;
import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * The Time Controller actor
 * 
 */
public class TimeController extends AbstractTimeController {

	/**
	 * Create the TimeController name (used by the HB)
	 * 
	 * @param tcContext
	 * @param heartbeatLocalPort
	 * @throws UnknownHostException
	 */
	private static String createTimeControllerName(TcDataController tcContext,
			int heartbeatLocalPort) throws UnknownHostException {
		return tcContext.getContextName()+"#"+ Context.getInstance().getLocalIp()+":"+heartbeatLocalPort;
	}


	/**
	 * Full Constructor: it runs the Thread for Server and for Client
	 * 
	 * @param tcContext
	 * @param nodeType
	 * @param heartbeatLocalPort
	 * @param targets
	 * @param senderDelay 
	 * @param stopTimeSeconds 
	 * 
	 * @throws IOException
	 */
	public TimeController(TcDataController tcContext, int heartbeatLocalPort, 
			ENodeType nodeType, Node[] targets, int senderDelay,int stopTimeSeconds) throws IOException 
	{
		super(tcContext, createTimeControllerName(tcContext, heartbeatLocalPort), heartbeatLocalPort, nodeType, targets, senderDelay, stopTimeSeconds);		
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.clorofor.implem.cc.ITimeController#runHeartBeatCollector(java.lang
	 * .String, int)
	 */
	@Override
	public void runTimeControllerThreads(String name, int port) throws IOException {
		
		//Context.getInstance().getLogger().info("Free memory:"+ ((Runtime.getRuntime().freeMemory()/1024)/1024)+" MB");

		
		this.intTimeController.initHeartBeatController(name, port);
		this.intTimeController.startHeartbeatServerThread();
		
		this.intTimeController.startTimelineThread(tcContext, Context.TIMELINE_CHECK_DELAY);	
		this.intTimeController.startClientResponseThread(tcContext, Context.RESPONSE_CHECK_DELAY);
		this.intTimeController.startTimeoutThread(Context.TIMEOUT_CHECK_DELAY);
		this.intTimeController.startStopRunControllerThread(this.stopControllerTimeSeconds);
		this.intTimeController.startStopRunCommanderThread(this.stopCommanderTimeSeconds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.clorofor.implem.cc.base.AbstractTimeController#runHeartBeatSender()
	 */
	@Override
	public void runHeartBeatSenderThread(int delay) {
		this.intTimeCommander.startHeartBeatSenderThread(this.targetsHb, delay);
	}

}
