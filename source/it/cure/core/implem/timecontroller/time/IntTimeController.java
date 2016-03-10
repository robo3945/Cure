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
import it.cure.core.implem.timecontroller.message.base.AbstractMessageServerThread;
import it.cure.core.implem.timecontroller.message.bean.AbstractMessage;
import it.cure.core.implem.timecontroller.message.bean.HeartBeatMessage;
import it.cure.core.implem.timecontroller.message.bean.Signal;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.implem.timecontroller.tcmanage.TcTracer;
import it.cure.core.implem.udp.UdpMessageServerThread;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.model.ISimulationParameter;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The controller of Timeout, Timeline and Response
 */
public class IntTimeController implements ISimulationParameter {

	private String hbControllerName;
	private int hbPort;
	private AbstractMessageServerThread hbUdpServerThread;
	private TcDataController tcContext;
	private ISimulationModel[] simulationModel;
	private IntTimeCommander intTimeCommander;
	private Timer stopControllerTimer;
	private Timer stopCommanderTimer;
	
	/**
	 * Constructor
	 * @param tcContext 
	 * @param agCommander 
	 */
	public IntTimeController(TcDataController tcContext,
			IntTimeCommander agCommander) {
		this.tcContext = tcContext;
		this.intTimeCommander = (IntTimeCommander) agCommander;
		this.stopControllerTimer = new Timer("stopControllerTimerThread: "+tcContext.getContextName(), true);
		this.stopCommanderTimer = new Timer("stopCommanderTimerThread: "+tcContext.getContextName(), true);
	}

	/**
	 * Initializes the HB Controller
	 * 
	 * @param hbControllerName
	 * @param hbPort
	 * @throws IOException
	 */
	public void initHeartBeatController(String hbControllerName, int hbPort)
			throws IOException {
		this.hbControllerName = hbControllerName;
		this.hbPort = hbPort;

		this.hbUdpServerThread = new UdpMessageServerThread(this.tcContext,
				this.intTimeCommander.getSender(), this.hbControllerName,
				this.hbPort);
		this.hbUdpServerThread.setSimulationModel(this.simulationModel);
	}

	@Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;
	}

	/**
	 * Start the thread that stops the Controller
	 * @param delaySec
	 */
	public void startStopRunControllerThread(int delaySec) {
		this.stopControllerTimer.schedule(new StopControllerTask(), delaySec*1000);
	}
	
	/**
	 * Start the thread that stops the Commander
	 * @param delaySec
	 */
	public void startStopRunCommanderThread(int delaySec) {
		this.stopCommanderTimer.schedule(new StopCommanderTask(), delaySec*1000);		
	}
	
	/**
	 * Start the Server to collect HeartBeats
	 * @return 
	 */
	public boolean startHeartbeatServerThread() {
		if (this.hbUdpServerThread != null) {
			this.hbUdpServerThread.start();
			return true;
		}
		return false;
	}
	
	/**
	 * Start the thread that checks the response to the client sent messages
	 * @param tcContext 
	 * @param delay 
	 */
	public void startClientResponseThread(final TcDataController tcContext,final int delay) {

		new Thread("Thread-response-"+tcContext.getContextName()) {
			public void run() {
				try {
					do 
					{				
						// Check the whole collection
						for (Entry<Integer, AbstractMessage> nodeEntry : tcContext.getHtForResponse().entrySet()) 
						{
							HeartBeatMessage hb = (HeartBeatMessage) nodeEntry.getValue();
							
							if (hb!=null)
							{
								// responses received and not yet processed
								if (hb.isResponseError() && !hb.isResponseTraced())
								{
									hb.setResponseTraced(true);
									
									// (refactored on 25/12/2012)
									String packTypeString = "RESPONSE";
									String status = "RECEIVED";
									String message = "RESPONSE_ERROR of " +hb.getSender().getDesc() +" registered on: "+ hbControllerName.toUpperCase();
									
									String signalText = TcTracer.makeTraceLineFull(
											packTypeString,
											hb.getSender().getDesc(),
											hb.getReceiver().getDesc(),
											Integer.toString(hb.getId()),
											Integer.toString(hb.getTimelineOrdinal()),
											status,
											message,
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());									
									Signal sig = new Signal(signalText);
									
									String signalTextForExp = TcTracer.makeTraceLineSmall(
											packTypeString, 
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());
									
									Signal sigForExp = new Signal(signalTextForExp);									
									IntTimeController.this.tcContext.writeToSignalPanel(sig, sigForExp);								
	
									/*signalText = Tracer.makeTraceLine(
											"RESPONSE",
											msg.getSender().getDesc(),
											msg.getReceiver().getDesc(),
											Integer.toString(msg.getId()),
											Integer.toString(msg.getTimelineOrdinal()),
											"RECEIVED",
											"RESPONSE_ERROR of " +msg.getSender().getDesc() +" echoed by: "+ hbControllerName.toUpperCase());
									
									sig = new Signal(signalText);
									
									// viene inviata una segnalazione a tutti gli altri target (una specie di echo)
									InternalTimeController.this.intTimeCommander.sendSignal(sig);
									*/
									
									// removes the message always because it has already been managed: the error has been trapped
									tcContext.getHtForResponse().remove(nodeEntry.getKey());
								}
							}
							
							tcContext.getHtForResponse().remove(nodeEntry.getKey());
						}
						
						if (delay==-1)
						{
							yield();							
						}
						else
						{
							sleep(delay);
						}

					}
					while (!tcContext.isStopControllerRun());
					
				} catch (InterruptedException ex) 
				{
					Context.getInstance().getLogger().info("Interruzione programmata del Thread Response Receiver: " + ExceptionUtils.getStackTrace(ex));
				}
			}
		}.start();

	}


	
	/**
	 * Start the thread that checks the timeline
	 * @param tcContext 
	 * @param delay 
	 */
	public void startTimelineThread(final TcDataController tcContext,
			final int delay) {

		new Thread("Thread-timeline-"+tcContext.getContextName()) {
			public void run() {
				try {
					do 
					{				
						// Check the whole timeline
						for (Entry<Integer, AbstractMessage> nodeEntry: tcContext.getHtForTimeline().entrySet()) 
						{
							HeartBeatMessage hb = (HeartBeatMessage) nodeEntry.getValue();
							
							// XXX: Context.ORDINAL_FOR_RESPONSE_PACKET viene usato per scartare i pacchetti di Response agli invii del CC
							if (hb!=null)
							{
								if (	hb.isTimelineError() 
										&& !hb.isTimelineTraced()
										&& hb.getTimelineOrdinal() != Context.ORDINAL_FOR_RESPONSE_PACKET) 
								{
									hb.setTimelineTraced(true);
																		
									// (wrote on 25/12/2012)
									String packTypeString = "TIMELINE";
									String status = "RECEIVED";
									String message = "CORRUPTED_TIMELINE on "+hb.getSender().getDesc() +  " registered on: "+ IntTimeController.this.hbControllerName;
									
									String signalText = TcTracer.makeTraceLineFull(
											packTypeString,
											hb.getSender().getDesc(),
											hb.getReceiver().getDesc(),
											Integer.toString(hb.getId()),
											Integer.toString(hb.getTimelineOrdinal()),
											status,
											message,
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());									
									Signal sig = new Signal(signalText);
									
									String signalTextForExp = TcTracer.makeTraceLineSmall(
											packTypeString, 
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());
									
									Signal sigForExp = new Signal(signalTextForExp);									
									IntTimeController.this.tcContext.writeToSignalPanel(sig, sigForExp);								

									//InternalTimeController.this.intTimeCommander.sendSignal(sig);
																	
									// removes the message always because it has already been managed: the error has been trapped
									tcContext.getHtForTimeline().remove(nodeEntry.getKey());									
								}
							}							
							tcContext.getHtForTimeline().remove(nodeEntry.getKey());
						}
						
						if (delay==-1)
						{
							yield();							
						}
						else
						{
							sleep(delay);
						}
					}
					while (!tcContext.isStopControllerRun());
					
				} catch (InterruptedException ex) 
				{
					Context.getInstance().getLogger().info("Interruzione programmata del Thread Timeline Receiver: " + ExceptionUtils.getStackTrace(ex));
				}
			}
		}.start();

	}

	/**
	 * Start the thread that checks the timeout of the sender
	 * @param delay 
	 */
	public void startTimeoutThread(final int delay) {

		new Thread("Thread-timeout-"+tcContext.getContextName()) {
			public void run() {
				try {
					
					do
					{					
						for (Entry<Integer, AbstractMessage> nodeEntry: tcContext.getHtForTimeout().entrySet())
						{
							HeartBeatMessage hb = (HeartBeatMessage) nodeEntry.getValue();
							
							if (hb!=null)
							{
								// XXX: Context.ORDINAL_FOR_RESPONSE_PACKET viene usato per scartare i pacchetti di Response agli invii del CC
								// (cambiato il 27/12/2012: inserita la proprietà del threshold, prima era pari al delay)
								// (aggiornato il 29/12/2012: inserito il remove dei pacchetti dalla coda e il controllo se il pacchetto è stato già processato per il timeout
								if (	new BasicTimeStampService().getTimeStamp()  > hb.getTimestamp().getTimestampAsLong() + Context.TIMEOUT_THRESHOLD // criterio per il superamento della soglia 
										&& !hb.isTimeoutTraced()	// Solo quelli non processati per il Timeout														
										&& hb.getTimelineOrdinal() != Context.ORDINAL_FOR_RESPONSE_PACKET // Solo i pacchetti ricevuti dal sender e non le response ai proprio pacchetti inviati
									) 
								{
									// set the flag saying the packet is processed
									hb.setTimeoutTraced(true);
									
									// (refactored on 25/12/2012)
									String packTypeString = "TIMEOUT";
									String status = "RECEIVED";
									String message = "TIMEOUT ERROR on node " + hb.getSender().getDesc() + " registered on: " + IntTimeController.this.hbControllerName;
									
									String signalText = TcTracer.makeTraceLineFull(
											packTypeString,
											hb.getSender().getDesc(),
											hb.getReceiver().getDesc(),
											Integer.toString(hb.getId()),
											Integer.toString(hb.getTimelineOrdinal()),
											status,
											message,
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());									
									
									
									Signal sig = new Signal(signalText);
																		
									String signalTextForExp = TcTracer.makeTraceLineSmall(
											packTypeString, 
											hb.isTimelineHacked(),
											hb.isTimeoutHacked(),
											hb.isResponseHacked());
									
									Signal sigForExp = new Signal(signalTextForExp);	
									IntTimeController.this.tcContext.writeToSignalPanel(sig,sigForExp);								
									//InternalTimeController.this.intTimeCommander.sendSignal(sig);
									
								}
							
							}
							
							tcContext.getHtForTimeout().remove(nodeEntry.getKey());
							
						}
						
						if (delay==-1)
						{
							yield();							
						}
						else
						{
							sleep(delay);
						}

					}
					while (!tcContext.isStopControllerRun());
						
				} catch (InterruptedException ex) 
				{
					Context.getInstance().getLogger().info("Interruzione programmata del Thread Timeout Receiver: " + ExceptionUtils.getStackTrace(ex));
				}
			}
		}.start();

	}

	/**
	 * 
	 * The task that stop the commander threads
	 *
	 */
	class StopCommanderTask extends TimerTask
	{

		@Override
		public void run() {
			IntTimeController.this.tcContext.setStopCommanderRun(true);
			//this.cancel();
			
		}
		
	}
	
	/**
	 * 
	 * The task that stop the controller threads
	 *
	 */
	class StopControllerTask extends TimerTask
	{

		@Override
		public void run() {
			IntTimeController.this.tcContext.setStopControllerRun(true);
			//this.cancel();
			
		}
		
	}

	
}