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

package it.cure.core.implem.timecontroller.base;

import it.cure.core.enums.ENodeType;
import it.cure.core.implem.Context;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.implem.timecontroller.time.IntTimeCommander;
import it.cure.core.implem.timecontroller.time.IntTimeController;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.model.ISimulationParameter;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * A Time Controller
 *
 */
public abstract class AbstractTimeController implements ISimulationParameter{

	protected IntTimeCommander intTimeCommander;
	protected IntTimeController intTimeController;
	protected TcDataController tcContext;	
	protected Node[] targetsHb;
	protected ISimulationModel[] simulationModel;
	protected String tcHostName;
	private int tcServerPort;
	private int senderDelay;
	protected int stopCommanderTimeSeconds;
	protected int stopControllerTimeSeconds;

	/**
	 * Constructor
	 * @param messageRepository 
	 * @param heartbeatName 
	 * @param heartbeatPort 
	 * @param nodeType 
	 * @param targetsHb 
	 * @param senderDelay 
	 * @param stopTimeSeconds 
	 * @throws SocketException 
	 */
	public AbstractTimeController(TcDataController messageRepository,
			String heartbeatName, int heartbeatPort,			
			ENodeType nodeType, Node[] targetsHb, 
			int senderDelay,
			int stopTimeSeconds) throws SocketException 
	{
		this.tcContext = messageRepository;		
		this.targetsHb = targetsHb;		
		this.tcHostName = heartbeatName;
		this.tcServerPort = heartbeatPort;
		this.senderDelay = senderDelay;
		this.intTimeCommander = new IntTimeCommander(messageRepository, new DatagramSocket(), getItSelfAsSender(heartbeatName, nodeType));		
		this.intTimeController = new IntTimeController(messageRepository, this.intTimeCommander);
		this.stopCommanderTimeSeconds = (int) (stopTimeSeconds*1.0/Context.STOP_COMMANDER_TIMER_RATIO);
		this.stopControllerTimeSeconds = stopTimeSeconds;
	}
	
	/**
	 * Return itself as a Node of type Sender
	 * @param description 
	 * @param nodeType
	 * @return
	 */
	public Node getItSelfAsSender(String description, ENodeType nodeType){ 
		// Determines the sender node: itself
		try {
			return new Node(InetAddress.getLocalHost().getHostName(),Context.getInstance().getLocalIp(), this.tcServerPort, nodeType, description);
			
		} catch (UnknownHostException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
	    
	    return null;
	}

	/**
	 * Run the Controller
	 * @throws IOException
	 */
	public void run() throws IOException
	{
		this.intTimeController.setSimulationModel(simulationModel);
		this.intTimeCommander.setSimulationModel(simulationModel);

		// run the thread
		this.runTimeControllerThreads(tcHostName, tcServerPort);
		this.runHeartBeatSenderThread(senderDelay);
	}
	
	/**
	 * Run the Time Controller Threads
	 * 
	 * @param name
	 * @param port
	 * @throws IOException
	 */
	public abstract void runTimeControllerThreads(String name, int port) throws IOException;

	/**
	 * Thread that sends HB to the targets
	 * @param delay 
	 */
	public abstract void runHeartBeatSenderThread(int delay);
		
	@Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;
	}
	
}
