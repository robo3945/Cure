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

package it.cure.core.implem.simulation;

import it.cure.core.enums.ENodeType;
import it.cure.core.implem.Context;
import it.cure.core.implem.security.keys.SymKeyMgr;
import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.implem.timecontroller.tcmanage.TcTracer;
import it.cure.core.implem.timecontroller.time.TimeController;
import it.cure.core.interfaces.model.ISimulationModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * This simulator uses the topology.xml file
 *
 */
public class SimpleDistributedSimulator extends AbstractSimulator {

	/**
	 * @param tracesPath
	 */
	public SimpleDistributedSimulator(String tracesPath) {
		super(tracesPath);
	}
	
	/**
	 * Start the simulator starting from a file descriptor of the topology of the network
	 * @param topologyTableFileName 
	 * @param keyStorePath
	 * @param simModelGuests
	 * @param simModelHosts
	 * @param simModelCC
	 * @param runArgs
	 */
	public void nGuestOneCC(String topologyTableFileName, String keyStorePath,  
			ISimulationModel simModelGuests, ISimulationModel simModelHosts,
			ISimulationModel simModelCC, String runArgs) 
	{

		ArrayList<Node> nodesCC = new ArrayList<Node>();
		ArrayList<Node> nodesGuests = new ArrayList<Node>();
		
		//String localHostIp = getLocalHostIp();
		
		if (Context.getInstance().getLocalIp()==null)
			Context.getInstance().getLogger()
			.error("You must pass the IP of the local execution for this kind of simulation!");
		
		String localHostIp = Context.getInstance().getLocalIp();
		extractTargetFromTable(topologyTableFileName, nodesCC, nodesGuests);
		
		//debugPrintTable(nodesCC, nodesGuests);
		
		// ---------------------- Set the Key store
		ArrayList<byte[]> arKeys = SymKeyMgr.getKeyStore(keyStorePath);
		SymKeyMgr.setKeysList(arKeys);

		// -------------------- Time Controllers
		String datepart = TcTracer.getNowTimestamp();
		String tracepath = pathForTrace + "/ccTC_"+datepart+"_"+Context.UNTIL_STOP_SECS;

		// CC -------------- if the current IP is the same of the CC
		if (localHostIp.equals(nodesCC.get(0).getIp()) == true)
		{	
			Context.getInstance().getLogger().info("This is the CC.");
			
			try {
				
				Context.setConfigProperties(Context.configFile, true);
				Context.getInstance().getLogger().info("Using configuration file: CC");
				
				TcDataController mrCC = new TcDataController("ccTC", tracepath, runArgs);
				
				ISimulationModel[] arSimModelCC = new ISimulationModel[] { simModelCC };

				TimeController tc = new TimeController(mrCC, nodesCC.get(0).getPort(), ENodeType.CloudController, nodesGuests.toArray(new Node[0]), Context.SENDER_UDP_SEND_DELAY, Context.UNTIL_STOP_SECS);
				tc.setSimulationModel(arSimModelCC);
				tc.run();
	
			} catch (IOException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
		}
		
		// GUESTS -------------- if the current IP is NOT the same of the CC
		else
		{
			try {
				Context.setConfigProperties(Context.configFile, false);
				Context.getInstance().getLogger().info("Using configuration file: GUEST/HOST");
				
				// understand how many guests are in the table for the local IP			
				int numTcGuests =0;
				for (Node node: nodesGuests)
				{
					if (node.getIp().equals(localHostIp) == true)
						numTcGuests++;
				}
	
				TcDataController tcContextGuests[] = new TcDataController[numTcGuests];
				TimeController tcGuests[] = new TimeController[numTcGuests];
				
				int nodeIndex =0;
				int portShift =0;
				
				for (Node node: nodesGuests)
				{
					if (node.getIp().equals(localHostIp) == true)
					{	
											
						int error =0;
						int maxStepCount=0;
						int MAX_STEP = 20;
						do
						{
							int nodePort = node.getPort() + portShift;
							
							error = runGuestSim(simModelGuests, simModelHosts, runArgs,
												nodesCC, localHostIp, tcContextGuests, tcGuests, nodeIndex,
												nodePort);
							
							if (error==1)
								portShift++;
							
							maxStepCount++;
						} 
						while (error==1 && maxStepCount<MAX_STEP);
						
						// i must be incremented in the same Node, so the different Host are with the same IP!
						nodeIndex++;
					}
				}
			} catch (InvalidPropertiesFormatException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			} catch (IOException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
		}
			
	}

	/**
	 * Run the guest simulation
	 * 
	 * @param simModelGuests
	 * @param simModelHosts
	 * @param runArgs
	 * @param nodesCC
	 * @param localHostIp
	 * @param tcContextGuests
	 * @param tcGuests
	 * @param nodeIndex
	 * @param nodePort
	 * 
	 * @return 1 the shift, 0 ok, -1 error 
	 */
	protected int runGuestSim(ISimulationModel simModelGuests,
			ISimulationModel simModelHosts, String runArgs,
			ArrayList<Node> nodesCC, String localHostIp,
			TcDataController[] tcContextGuests, TimeController[] tcGuests,
			int nodeIndex, int nodePort) 
	{
		
		
		ISimulationModel[] arSimModel;
		Context.getInstance().getLogger().info("NodeIndex: "+nodeIndex+" Got the IP: "+localHostIp + " & port: "+ nodePort);
		
		// XXX: se sono nei bucket pari allora i Guest falliscono anche per il modello di simulazione degli Hosts
		if ((nodeIndex / Context.GUEST_FOR_HOST) % 2 == 0)
		{
			arSimModel = new ISimulationModel[] { simModelGuests, simModelHosts };
		}
		else
		{
			arSimModel = new ISimulationModel[] { simModelGuests};
		}
		
		String prefix = "g" + (nodeIndex + 1) + "TC";
		String datepart = TcTracer.getNowTimestamp();
		String tracepath = pathForTrace + "/"+prefix + "_" + datepart+"_"+Context.UNTIL_STOP_SECS;
		
		tcContextGuests[nodeIndex] = new TcDataController(prefix, tracepath, runArgs);
		try 
		{
			tcGuests[nodeIndex] = new TimeController(tcContextGuests[nodeIndex], nodePort, ENodeType.Guest, nodesCC.toArray(new Node[0]), Context.SENDER_UDP_SEND_DELAY, Context.UNTIL_STOP_SECS);
			tcGuests[nodeIndex].setSimulationModel(arSimModel);
			try
			{
				tcGuests[nodeIndex].run();
			}
			catch (java.net.BindException e)
			{
				Context.getInstance().getLogger().error("Address already in use. Trying another port.");
				
				// it means it has to shift of 1 after for the port
				return 1;
			}
		} catch (IOException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			
			// it is an unrecoverable error
			return -1;
		}
		
		// All works
		return 0;
	}

	/**
	 * @param nodesCC
	 * @param nodesGuests
	 */
	protected void debugPrintTable(ArrayList<Node> nodesCC,
			ArrayList<Node> nodesGuests) {
		
		Context.getInstance().getLogger().info("---START-------- TOPOLOGY TABLE --------------");
		Context.getInstance().getLogger().info("------------- CC --------------");

		Context.getInstance().getLogger().info("Node Ip: "+nodesCC.get(0).getIp());
		Context.getInstance().getLogger().info("Node Port: "+nodesCC.get(0).getPort());
		Context.getInstance().getLogger().info("Node Type: "+nodesCC.get(0).getType());			
		
		Context.getInstance().getLogger().info("------------- Guests --------------");
		for (Node node: nodesGuests)
		{
			Context.getInstance().getLogger().info("Node Ip: "+node.getIp());
			Context.getInstance().getLogger().info("Node Port: "+node.getPort());
			Context.getInstance().getLogger().info("Node Type: "+node.getType());			
		}
		
		Context.getInstance().getLogger().info("---END---------- TOPOLOGY TABLE --------------");
	}


	/**
	 * Extract target from the table defined in the prop.xml file
	 * 
	 * @param topologyTableFileName
	 * @param ipList
	 * @param startPortList
	 * @param numTcList
	 */
	protected void extractTargetFromTable(String topologyTableFileName, ArrayList<Node> ccNodes, ArrayList<Node> guestsNodes) 
	{
		
		// ------------ Read the table from topology.prop.xml
		
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {
			fis = new FileInputStream(topologyTableFileName);
			properties.loadFromXML(fis);
		} catch (InvalidPropertiesFormatException e) {
			Context.getInstance().getLogger()
			.error(ExceptionUtils.getStackTrace(e));
		} catch (IOException e) {
			Context.getInstance().getLogger()
			.error(ExceptionUtils.getStackTrace(e));
		}
		
	    //<entry key="VM-1">192.168.0.1</entry>
	    //<entry key="STARTPORT-1">4444</entry>
	    //<entry key="NUMTC-1">30</entry>
		
		int i=0;
		try
		{
			// the ZERO element is the Cloud Controller
			for (;;)
			{
				String currentIp = (String) properties.get("VM-"+i);
				Integer startPort = Integer.parseInt((String) properties.get("STARTPORT-"+i));
				Integer numTc = Integer.parseInt((String) properties.get("NUMTC-"+i));
				
				// in the ZERO position we have the CC
				if (i==0)
				{
					ccNodes.add(new Node("cc",currentIp, startPort, ENodeType.CloudController));
				}
				else
					// it cycles for every numTc to allocate new Node						
					for (int j=0;j<numTc; j++)
					{
						Node nTemp = new Node("guest-"+i, currentIp, startPort+j, ENodeType.Guest);
						guestsNodes.add(nTemp);
					}
				
				i++;
			}
		}
		catch (Exception e)
		{
			Context.getInstance().getLogger().info("host configuration file: end of file reached");
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
		finally
		{
			try {
				fis.close();
			} catch (IOException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
		}
	}

}
