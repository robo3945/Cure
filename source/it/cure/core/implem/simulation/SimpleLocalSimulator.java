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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 */
public class SimpleLocalSimulator extends AbstractSimulator {

	/**
	 * @param tracesPath
	 */
	public SimpleLocalSimulator(String tracesPath) {
		super(tracesPath);
	}

	
	/**
	 * A very simple simulation
	 * 
	 * @param keyStorePath
	 * @param startPort
	 * @param numTcGuests
	 * @param simModelGuests
	 * @param simModelHosts
	 * @param simModelCC
	 * @param runArgs 
	 */
	public void nGuestAndOneCC(String keyStorePath, int startPort,
			int numTcGuests,
			ISimulationModel simModelGuests, ISimulationModel simModelHosts,
			ISimulationModel simModelCC, String runArgs) {

		Context.getInstance().getLogger().info("Inizio del test");

		// -------------------- Port initialization

		int[] portGuests = new int[numTcGuests];
		int portHbCC = 44445;

		for (int i = 0; i < numTcGuests; i++)
		{
			portGuests[i] = startPort + i;
		}
		
		// -------------------- Target initialization

		Node[] targetHbCc = new Node[1];
		targetHbCc[0] = new Node("ccHBCont", Context.LOCALHOST, portHbCC, ENodeType.CloudController);

		Node[] targetHbG = new Node[numTcGuests];
		for (int i = 0; i < numTcGuests; i++)
		{
			targetHbG[i] = new Node("g" + i + "-node", Context.LOCALHOST, portGuests[i], ENodeType.Guest);
		}

		// ---------------------- Set the Key store

		ArrayList<byte[]> arKeys = SymKeyMgr.getKeyStore(keyStorePath);
		SymKeyMgr.setKeysList(arKeys);

		// -------------------- Time Controllers

		String datepart = TcTracer.getNowTimestamp();
		String tracepath = pathForTrace + "/"+datepart+"_"+Context.UNTIL_STOP_SECS;
		
		// ---------- CC
		TcDataController mrCC = new TcDataController("ccTC",	tracepath, runArgs);
		
		ISimulationModel[] arSimModelCC = new ISimulationModel[] { simModelCC };
		try {
			TimeController tc = new TimeController(mrCC, portHbCC, ENodeType.CloudController, targetHbG, Context.SENDER_UDP_SEND_DELAY, Context.UNTIL_STOP_SECS);
			tc.setSimulationModel(arSimModelCC);
			tc.run();

		} catch (IOException e) {
			Context.getInstance().getLogger()
					.error(ExceptionUtils.getStackTrace(e));
		}
		
		// ---------- Guests
		TcDataController tcContextGuests[] = new TcDataController[numTcGuests];
		TimeController tcGuests[] = new TimeController[numTcGuests];
		ISimulationModel[] arSimModel;
		for (int i = 0; i < numTcGuests; i++) 
		{
			
			// TODO: se sono nel bucket pari allora i Guest falliscono anche per il modello di simulazione degli Hosts
			if ((i / Context.GUEST_FOR_HOST) % 2 == 0)
				arSimModel = new ISimulationModel[] { simModelGuests, simModelHosts };
			else
				arSimModel = new ISimulationModel[] { simModelGuests};
			
			tcContextGuests[i] = new TcDataController("g" + (i + 1) + "TC", null, runArgs);
			try {
				tcGuests[i] = new TimeController(tcContextGuests[i], portGuests[i], ENodeType.Guest, targetHbCc, Context.SENDER_UDP_SEND_DELAY, Context.UNTIL_STOP_SECS);
				tcGuests[i].setSimulationModel(arSimModel);
				tcGuests[i].run();
			} catch (IOException e) {
				Context.getInstance().getLogger()
						.error(ExceptionUtils.getStackTrace(e));
			}
		}

		// ---------- Print to the Console

		// showMessageRepositorySignalPanel("CC-Mr", mrCC.getSignalPanel(), 60);

	}
}
