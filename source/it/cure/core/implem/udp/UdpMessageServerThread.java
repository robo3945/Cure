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
import it.cure.core.implem.simmodel.base.AbstractSimModel;
import it.cure.core.implem.simmodel.base.SimModelFailureType;
import it.cure.core.implem.timecontroller.auth.Challenge;
import it.cure.core.implem.timecontroller.auth.Response;
import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.message.Utils;
import it.cure.core.implem.timecontroller.message.base.AbstractMessageServerThread;
import it.cure.core.implem.timecontroller.message.bean.AbstractMessage;
import it.cure.core.implem.timecontroller.message.bean.HeartBeatMessage;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.implem.util.Diagnose;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.exception.ExceptionUtils;

/** 
 * Udp Server for the IMessage
 * 
 */
public class UdpMessageServerThread extends AbstractMessageServerThread 
{
	protected DatagramSocket socket = null;
    protected BufferedReader in = null;


    /**
     * Constructor
     * 
     * @param tcContext 
     * @param receiver 
     * 
     * @param name name of the Server
     * @param port
     * @throws IOException
     */
    public UdpMessageServerThread(TcDataController tcContext, Node receiver, String name, int port) throws IOException {
        super(tcContext, receiver,  name, port);
        
        socket = new DatagramSocket(port);  
        socket.setSoTimeout(Context.SOCKET_TIMEOUT); 
        
        Context.getInstance().getLogger().debug("**> SO_RCVBUF current is: "+socket.getReceiveBufferSize());
        socket.setReceiveBufferSize(Context.UDP_RECEIVE_BUF_SIZE);
        Context.getInstance().getLogger().debug("**> NEW SO_RCVBUF is: "+socket.getReceiveBufferSize());
    }

    /**
	 * Exchange the sender with the receiver
	 * 
	 * @param msg
	 */
	protected void exchangeSenderReceiver(HeartBeatMessage msg) {
		Node nodeTemp = msg.getReceiver();
		msg.setReceiver(msg.getSender());
		msg.setSender(nodeTemp);
	}

    /**
     * Start the Thread
     */
    @Override
	public void run() {

    	HeartBeatMessage msg = null;
    	DatagramPacket packet = null;
    	byte[] msgBuf = null;
    	SecEnvelope sece = null;
    	int contRecvNumPacket =0;
    	Context.getInstance().getLogger().info("Server thread STARTED");
    	
        do 
        {
        	if (contRecvNumPacket % Context.MOD_FOR_DEBUG_CONT_THREAD == 0)
        	{
        		//Diagnose.printMemory();
        		Diagnose.printNumPacket(" RECEIVED: " + contRecvNumPacket);
        	}
        	
        	
            try {
            	
            	
            	// ------------------------- Receive the message            	
            	
            	// XXX: disabilitare il fallimento della rete su ricezione? 
            	// ==> Simuliamo solo il Timeout come timeover ma i pacchetti vengono sempre ricevuti correttamente 
            	//if (SimulationModelAbstract.askOracle(simulationModel, SimulationModelFailureType.TIMEOUT))
	            //{
	            	// allocation of the buffer for packet
	                msgBuf = new byte[Context.UDP_PACKET_BUF_MAXSIZE];
	
	                // receive request (wait for the timeout)
	                packet = new DatagramPacket(msgBuf, msgBuf.length);
	                socket.receive(packet);
	                
	                
	                sece = (SecEnvelope) Utils.deserializeObject(packet.getData());
					msg = (HeartBeatMessage) Utils.deserializeObject(sece.getDecryptedBuffer(SymKeyMgr.getAKey(sece.getIdxKey())));
	    			msg.setReceiver(receiver);
	    			
	                // if the message doesn't have the response set	    			
	    			if (msg.getResponse()==null)
	    			{
						sendResponseMessage(msg);
	    			}
	    			
	    			contRecvNumPacket++;
	    			this.tcContext.putInRecvdQueue(msg);
	    			
	           //}
            	
            }
            catch (SocketTimeoutException e) 
            {
            	Context.getInstance().getLogger().error("** Server thread receive Timeout");
            }
            catch (IOException e) 
            {
            	Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
            } catch (IllegalArgumentException e) {
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
			} catch (ClassNotFoundException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
			finally
			{
				// for the GC:
    			msgBuf=null;
    			packet = null;
    			sece = null;	
			}
        }
        while (this.tcContext.isStopControllerRun() == false);
        
        // The computation is end
        Diagnose.printComputationEnd();
		Diagnose.printMemory();
		Diagnose.printNumPacket(" RECEIVED: " + contRecvNumPacket);

    }

	/**
	 * Send the response
	 * 
	 * @param msg
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private void sendUdpResponse(AbstractMessage msg, Node rcv)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		
		//if (SimulationModelAbstract.askOracle(simulationModel, SimulationModelFailureType.RESPONSE))
		//{
		    // returns the serialized message with the response	
			byte[] buf = Utils.serializeObject(new SecEnvelope(Utils.serializeObject(msg), 
					SymKeyMgr.getInstance(tcContext.getContextName()).getKey(),
					SymKeyMgr.getInstance(tcContext.getContextName()).getKeyIdx()));
		
			InetAddress address = InetAddress.getByName(rcv.getIp());
			
		    // send the response to the client at "address" and "port"
			DatagramPacket packet = new DatagramPacket(buf, buf.length,	address, rcv.getPort());
			
			//Context.getInstance().getLogger().info("address: "+address);
			
		    socket.send(packet);
		    	
		    //System.out.println("Server resp ("+this.receiver.getDesc()+") send to: " + msg.getSender().getDesc()+" to: "+msg.getReceiver().getDesc() +" Response: "+msg.getResponse().getStringResponse());
		    		    
			// Save the received message with the response in an hashtable
			//this.tcContext.addToServerSentResponseMessages(msg);
		//}
	}


	/**
	 * Send a Responde Message
	 * 
	 * @param msg
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	protected void sendResponseMessage(HeartBeatMessage msg) throws IOException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException 
	{
		
		// creates the new HB for the response
		HeartBeatMessage hbResponse = new HeartBeatMessage(msg.getSimulationModel());
		hbResponse.setChallenge(msg.getChallenge());
		hbResponse.setSender(msg.getReceiver());
		hbResponse.setReceiver(msg.getSender());
		
		// XXX: this ordinal mustn't be considered
		hbResponse.setTimelineOrdinal(Context.ORDINAL_FOR_RESPONSE_PACKET);
		
		// analyze the response
		String stringChallenge = hbResponse.getChallenge().getStringChallenge();
		
		if (AbstractSimModel.askOracle(simulationModel, SimModelFailureType.RESPONSE))
		{
			hbResponse.setResponse(new Response(stringChallenge));
		}
		else
		{
			hbResponse.setResponse(new Response((new Challenge()).getStringChallenge()));
			
			// XXX: il fatto che viene cambiato qui e che dopo viene fatto il controllo implica che gli hacked sono un sottoinsieme degli error
			hbResponse.setResponseHacked();
			hbResponse.setResponseError(true);
		}

		if (hbResponse.getResponse().match(stringChallenge, hbResponse.getResponse().getStringResponse()) == false)
		{
			hbResponse.setResponseError(true);						
		}
		
		/*
		if (AbstractSimulationModel.askOracle(simulationModel, SimulationModelFailureType.TIMEOUT) == false)
		{
			hbResponse.setTimeoutHacked();
		}
		*/

		sendUdpResponse(hbResponse, hbResponse.getReceiver());
		
	}
}
