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

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Time Controller Timeline checker: checking APIs
 *
 */
class TcTimelineChecker {

	private TcDataController tcDataController;
	
	/**
	 * @param tcDataController 
	 * 
	 */
	public TcTimelineChecker(TcDataController tcDataController) {
		this.tcDataController = tcDataController;
	}
	
	/*
	/ **
	 * Set the last timestamp for the arrived message
	 * 
	 * @param message
	 * /
	protected void setTimestampForTimeout(IHeartBeat message) 
	{	
		if (message.isTimeoutHacked())
		{
			// TODO caso di timeout hacked
			this.tcDataController.getGotMessagesTimestamp().put(message, Context.FAKETIME_FOR_TIMEOUTHACKED);
		}
		else
		{
			this.tcDataController.getGotMessagesTimestamp().put(message, new BasicTimeStampService().getTimeStamp());
		}
	}
	*/

	/**
	 * Check the timeline and set the error
	 * 
	 * @param message
	 */
	protected void setFlagsForTimeline(HeartBeatMessage message) 
	{
		// does not consider response time ordinal
		if (message.getTimelineOrdinal()<0)
			return;
		
		// creates the structure to organize the messages for sender ID
		int senderId = message.getSender().getId();

		// I must check the existence of the senderId before, otherwise the Treemap will be reinitialized
		if (this.tcDataController.getRecvForSenderList().get(senderId)== null)
			this.tcDataController.getRecvForSenderList().put(senderId, new TreeMap<Integer, AbstractMessage>());
		
		TreeMap<Integer, AbstractMessage> senderTreeMap = this.tcDataController.getRecvForSenderList().get(senderId);
		// add the message to the correct queue
		senderTreeMap.put(message.getTimelineOrdinal(), (AbstractMessage) message);
		
		evalTimesNG(senderTreeMap, Context.TIMELINEWINDOWPACKSIZE);
		
		//evalNTimes(senderTreeMap, Context.TIMELINEWINDOWPACKSIZE);
		
	}

	/**
	 * Evaluate the last three messages and remove them
	 * 
	 * @param senderTreeMap
	 */
	protected void eval3Times(TreeMap<Integer, AbstractMessage> senderTreeMap) 
	{
		if (senderTreeMap.values().size()>=3)
		{
			// the first one is the oldest
			Entry<Integer, AbstractMessage> lastEntry;
			
			lastEntry = senderTreeMap.pollLastEntry();
			HeartBeatMessage third = (HeartBeatMessage) lastEntry.getValue();
			
			lastEntry = senderTreeMap.pollLastEntry();
			HeartBeatMessage second = (HeartBeatMessage) lastEntry.getValue();
			
			lastEntry = senderTreeMap.pollLastEntry();
			HeartBeatMessage first = (HeartBeatMessage) lastEntry.getValue();
			
			
			// Check the middle message timestamp
			if (first!=null && second!=null && third!=null)
			{
				if ((second.getTimestamp().getTimestampAsLong() >= first.getTimestamp().getTimestampAsLong() &&
						second.getTimestamp().getTimestampAsLong() <= third.getTimestamp().getTimestampAsLong()) == false)
				{
					second.setTimelineError();
					//System.out.println("Timeline error of: "+second.getSender().getDesc() + " - " +second.getReceiver().getDesc() + "-idSender:" + senderId);
				}
			}
		}
	}
	
	protected void evalNTimes(TreeMap<Integer, AbstractMessage> senderTreeMap, int N) 
	{
		if (senderTreeMap.values().size()>=N)
		{
			// the first one is the oldest
			HeartBeatMessage[] arHB = new HeartBeatMessage[N];
			for (int i=0;i<N;i++)
			{
				Entry<Integer, AbstractMessage> lastEntry = senderTreeMap.pollLastEntry();
				arHB[i] = (HeartBeatMessage) lastEntry.getValue();
			}
			
			// verify the timeline sequence			
			for (int i=0;i<N-1;i++)
			{
				long first = arHB[i].getTimestamp().getTimestampAsLong();
				long second = arHB[i+1].getTimestamp().getTimestampAsLong();
				
				if (first<second)
				{
					arHB[i+1].setTimelineError();
					//System.out.println("First: "+first + " - Second: "+second);
				}
			}
			
		}
	}
	
	/**
	 * New Generation to evaluate Timeline correctness: it analyzes a Window of N packets and considers the last and the before ones, ever!
	 * (created 27/12/2012)
	 * 
	 * @param senderTreeMap
	 * @param N
	 */
	protected void evalTimesNG(TreeMap<Integer, AbstractMessage> senderTreeMap, int N) 
	{
		if (senderTreeMap.values().size()>N)
		{
			Entry<Integer, AbstractMessage> lastEntry = senderTreeMap.pollLastEntry();
			for (int i=0;i<N-1;i++)
			{
				Entry<Integer, AbstractMessage> beforeLastEntry = senderTreeMap.pollLastEntry();

				long last =       ((HeartBeatMessage) lastEntry.getValue()).getTimestamp().getTimestampAsLong();
				long beforeLast = ((HeartBeatMessage) beforeLastEntry.getValue()).getTimestamp().getTimestampAsLong();

				if (last<beforeLast)
				{
					((HeartBeatMessage) beforeLastEntry.getValue()).setTimelineError();
				}
				
				lastEntry = beforeLastEntry;
			}						
		}
	}
	
	 /** Evaluate the last three messages and remove them
	 * 
	 * @param senderTreeMap
	 */
	protected void evalLastTimes(TreeMap<Integer, AbstractMessage> senderTreeMap) 
	{
		if (senderTreeMap.size()>=2)
		{
			// the first one is the oldest
			Entry<Integer, AbstractMessage> lastEntry = senderTreeMap.pollLastEntry();
			HeartBeatMessage last = (HeartBeatMessage) lastEntry.getValue();
			
			HeartBeatMessage current = (HeartBeatMessage) senderTreeMap.lastEntry().getValue();
						
			// Check the middle message timestamp
			if (last!=null && current!=null)
			{
				if (last.getTimestamp().getTimestampAsLong() < current.getTimestamp().getTimestampAsLong())
				{
					last.setTimelineError();
					//System.out.println("Timeline error of: "+last.getSender().getDesc() + " - " +last.getReceiver().getDesc());
				}
			}
		}
	}

}
