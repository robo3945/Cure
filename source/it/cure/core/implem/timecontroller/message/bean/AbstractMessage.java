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

import it.cure.core.implem.timecontroller.auth.Challenge;
import it.cure.core.implem.timecontroller.auth.Response;
import it.cure.core.implem.timecontroller.base.Node;

import java.io.Serializable;

/**
 * A Message
 *
 */
public class AbstractMessage implements Serializable, Cloneable{

	private static final long serialVersionUID = -5595081212335308587L;

	protected Node sender;
	protected Node receiver;
	protected Challenge challenge;
	protected Response response;
	protected int idMessage;
	protected int ordinal;
	
	/**
	 * Register if the message contains a response error
	 */
	protected boolean responseError;
	
	/**
	 * True if the message has already been traced
	 */
	protected boolean responseTraced;
	
	protected boolean timelineTraced;

	protected boolean timeoutTraced;

	
	/**
	 * Constructor: initializes the internal ID and the ordinal
	 */
	public AbstractMessage() {
		//this.idMessage = SecUtils.getHex(SecUtils.getRandomBytes(SecUtils.getSeed(), 20));
		this.idMessage = this.hashCode();
		this.setChallenge(new Challenge());
	}
	
	@Override
	public Object clone() {
	    try {
	      return super.clone();
	    }
	    catch(CloneNotSupportedException e) {
	      return null;
	    }
	  } 
	
	
	/**
	 * Get the Challenge 
	 * @return 
	 */
	public Challenge getChallenge()
	{
		return this.challenge;		
	}

	/**
	 * Get the ID
	 * @return 
	 */
	public int getId()
	{
		return this.idMessage;
	}
	
	/**
	 * Get the receiver
	 * @return
	 */
	public Node getReceiver() {
		return this.receiver;
	}

	/**
	 * Get the response
	 * @return
	 */
	public Response getResponse()
	{
		return this.response;		
	}


	/**
	 * Get the sender
	 * @return
	 */
	public Node getSender() {
		return this.sender;
	}
	
	/**
	 * Get the ordinal of the message
	 * @return
	 */
	public int getTimelineOrdinal()
	{
		return this.ordinal;
	}

	/**
	 * gets the state for the response
	 * 
	 * @return
	 */
	public boolean isResponseError()
	{
		return this.responseError;		
	}
	
	/**
	 * @return the sent
	 */
	public boolean isResponseTraced() {
		return responseTraced;
	}
	
	
	/**
	 * @return the sent
	 */
	public boolean isTimelineTraced() {
		return timelineTraced;
	}

	/**
	 * @return the tracing flag
	 */
	public boolean isTimeoutTraced() {
		return timeoutTraced;
	}

	/**
	 * Set the Challenge
	 * @param challenge
	 */
	public void setChallenge(Challenge challenge)
	{
		this.challenge = challenge;
		
	}

	
	/**
	 * Set the receiver
	 * @param receiver 
	 */
	public void setReceiver(Node receiver) {
		this.receiver = receiver;
		
	}

	
	/**
	 * Set the response
	 * @param response 
	 */
	public void setResponse(Response response)
	{
		this.response = response;		
	}
	
	/**
	 * Sets the error for an erroneous response
	 * @param value 
	 */
	public void setResponseError(boolean value)
	{
		this.responseError = value;
	}

	/**
	 * @param sent the sent to set
	 */
	public void setResponseTraced(boolean sent) {
		this.responseTraced = sent;
	}

	/**
	 * Set the sender
	 * @param sender
	 */
	public void setSender(Node sender) {
		this.sender = sender;
		
	}
	
	/**
	 * Set the ordinal of the message
	 * @param ordinal 
	 */
	public void setTimelineOrdinal(int ordinal)
	{
		this.ordinal = ordinal;
	}
	
	/**
	 * @param sent the sent to set
	 */
	public void setTimelineTraced(boolean sent) {
		this.timelineTraced = sent;
	}

	
	/**
	 * @param traced the sent to set
	 */
	public void setTimeoutTraced(boolean traced) {
		this.timelineTraced = traced;
	}

}
