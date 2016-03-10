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

package it.cure.core.implem.udp;

import it.cure.core.implem.Context;
import it.cure.core.implem.security.SecEnvelope;
import it.cure.core.implem.security.keys.SymKeyMgr;
import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.message.Utils;
import it.cure.core.implem.timecontroller.message.base.AbstractMessageClient;
import it.cure.core.implem.timecontroller.message.bean.AbstractMessage;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.exception.ExceptionUtils;

/** 
 * This is an UDP client implementation for IMessage type
 */
public class UdpMessageClient extends AbstractMessageClient {
	
	protected DatagramSocket socket;
	
	/**
	 * @param tcContext 
	 * @param socket 
	 * @param sender
	 * @param server
	 * @throws SocketException 
	 */
	public UdpMessageClient(TcDataController tcContext, DatagramSocket socket, Node sender, Node server) throws SocketException {
		super(tcContext, sender, server);
		
		this.socket = socket;
		socket.setSoTimeout(Context.SOCKET_TIMEOUT);
	}


	@Override
	public void send(AbstractMessage message) throws UnknownHostException,
			SocketException {
		
		byte[] buf = null;
		try {
			
			// TODO spostato il fallimento al chiamante
			// --------------- Sends the message		
			//if (SimulationModelAbstract.askOracle(simulationModel, SimulationModelFailureType.NETWORK))
			//{
				// Set the sender of the packet
				message.setSender(sender);
				message.setReceiver(server);
				this.server.setDesc(server.getName());
				
				// send the message
				buf = Utils.serializeObject(new SecEnvelope(Utils.serializeObject(message),
						SymKeyMgr.getInstance(tcContext.getContextName()).getKey(),
						SymKeyMgr.getInstance(tcContext.getContextName()).getKeyIdx()));
	
				InetAddress address = InetAddress.getByName(this.server.getIp());
				DatagramPacket packet = new DatagramPacket(buf, buf.length,	address, this.server.getPort());
				
				this.socket.send(packet);

			//}

		} catch (IOException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));	
		} catch (InvalidKeyException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchAlgorithmException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchPaddingException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (IllegalBlockSizeException e) {
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		} catch (BadPaddingException e) {
 			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
        
	}
	
	
}
