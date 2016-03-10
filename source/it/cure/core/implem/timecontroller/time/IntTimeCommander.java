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
import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.message.bean.HeartBeatMessage;
import it.cure.core.implem.timecontroller.message.bean.Signal;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.implem.udp.UdpMessageClient;
import it.cure.core.implem.util.Diagnose;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.model.ISimulationParameter;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Commander part to send messages or Heartbeat with or without signal
 * 
 */
public class IntTimeCommander  implements ISimulationParameter{

	private Node sender;
	private DatagramSocket socket;

	private TcDataController tcContext;
	private ConcurrentLinkedQueue<Signal> currentSignal;
	private ISimulationModel[] simulationModel;
	/**
	 * Constructor
	 * @param tcContext 
	 * @param socket 
	 * @param sender 
	 */
	public IntTimeCommander(TcDataController tcContext,
			DatagramSocket socket, Node sender) {
		this.sender = sender;
		this.tcContext = tcContext;
		this.socket = socket;
		this.currentSignal = new ConcurrentLinkedQueue<Signal>();
	}


	/**
	 * @return the sender
	 */
	public Node getSender() {
		return sender;
	}

	/**
	 * 
	 */
	protected  void resetSignal() {
		this.currentSignal = new ConcurrentLinkedQueue<Signal>();
	}

	/**
	 * Send the heartbeat to the targets
	 * @param heartbeat 
	 * @param newTimelineOrdinal 
	 * @param targets 
	 */
	public void sendHeartBeat(HeartBeatMessage heartbeat, int newTimelineOrdinal, Node[] targets) {
		try {			
				heartbeat.setTimelineOrdinal(newTimelineOrdinal);
				
				for (Node target : targets)
				{
					UdpMessageClient mc = new UdpMessageClient(this.tcContext, this.socket, this.sender, target);
					mc.setSimulationModel(simulationModel);
					
					if (AbstractSimModel.askOracle(simulationModel, SimModelFailureType.TIMEOUT) == false)
					{
						// XXX: imposto il timestamp in modo malizioso al "fake value" (-1): in questo modo si supera sicuramente la soglia
						heartbeat.getTimestamp().setTimestamp(Context.TIMEOUT_FAKE_VALUE);
						heartbeat.setTimeoutHacked();
					}
					mc.send(heartbeat);
				}
		} catch (UnknownHostException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (SocketException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Sends the signal as a notifications to the targets
	 * @param signal 
	 */
	public  void sendSignal(Signal signal) {
		this.currentSignal.add(signal);
	}

	@Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;		
	}

	/**
	 * @param targets
	 * @param delay 
	 */
	public void startHeartBeatSenderThread(final Node[] targets,final int delay) {

		new Thread("Thread-client-"+tcContext.getContextName()) 
		{
			
			public void run() 			
			{
				try {
					int ordinal =0;
					
					do 
					{
						/*
						if (ordinal % Context.MOD_FOR_DEBUG_CONT_THREAD == 0)
			        	{
			        		Diagnose.printNumPacket(" SENT: " + ordinal);
			        	}
			        	*/
						
						if (IntTimeCommander.this.currentSignal.size()>0)
						{
							Context.getInstance().getLogger().info("Signal queue not empty!");
							for (Signal sig: IntTimeCommander.this.currentSignal)
							{
								HeartBeatMessage hb = new HeartBeatMessage(sig, simulationModel);
								ordinal++;
								sendHeartBeat(hb, ordinal, targets);
							}												
							resetSignal();
						}
						// otherwise the normal HB
						else
						{
							ordinal++;
							sendHeartBeat(new HeartBeatMessage(simulationModel), ordinal,  targets);
						}
						
						sleep(delay);
					}
					while (tcContext.isStopCommanderRun() == false);
					
					Diagnose.printComputationEnd();
					Diagnose.printNumPacket(" SENT: " + ordinal);
					
				} 
				catch (InterruptedException ex) 
				{
					Context.getInstance().getLogger().info("Interruzione programmata del Thread (Sender): " + ExceptionUtils.getStackTrace(ex));
				}
			}
			
		}.start();
	}

}
