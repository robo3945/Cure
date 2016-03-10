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
import it.cure.core.implem.security.SecUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Class to print messages formatted in a defined way
 */
public class TcTracer {

	final private static String SEP = ";";	
	private PrintWriter fileWriter;
	private PrintWriter fileWriterForExp;
	private String contextName;	
	private int countWrites;		
	private int modForWrites;
	private String tracepath;
	
	/**
	 * Constructor
	 * @param contextName 
	 * @param path 
	 * @param runArgs 
	 * @param modForWrites 
	 */
	public TcTracer(String contextName, String path, String runArgs, int modForWrites) 
	{
		this.contextName = contextName;
		this.modForWrites = modForWrites;
		this.countWrites = 0;
		
		tracepath = path;
		
		if (path!=null)
		{
			// create the dir
	        File f = new File(tracepath);
	        f.mkdir();
	        
	        // insert the runargs file with cmd args
	        try {
				FileUtils.writeStringToFile(new File(tracepath+"/runArgs.txt"), runArgs);
			} catch (IOException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
		}        
	}
	
	
	/**
	 * @return
	 */
	public static String getNowTimestamp() 
	{
		Calendar cal = new GregorianCalendar();
        String datepart = String.format("%04d", cal.get(Calendar.YEAR))+
        String.format("%02d", cal.get(Calendar.MONTH)+1)+
        		String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))+"-"+
        				String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))+"_"+
        						String.format("%02d", cal.get(Calendar.MINUTE))+"_"+
        								String.format("%02d", cal.get(Calendar.SECOND))+"_"+
        										String.format("%04d", cal.get(Calendar.MILLISECOND));
		return datepart;
	}
	
	/**
	 * Get the time in millisec
	 * @return
	 */
	public static long getNowTimeLong() {
		Calendar cal = new GregorianCalendar();        
		return cal.getTimeInMillis();
	}
	
	/*
	 * 
	/**
	 * 
	 * @param errorCode
	 * @param sender
	 * @param msgId 
	 * @param messageType
	 * @param errorDesc
	 * @param isTimelineHacked 
	 * @param isTimoutHacked 
	 * @param isResponseHacked 
	 * @return
	 * /
	public static String makeTraceLine(String errorCode, String sender, String msgId, String messageType, String errorDesc,
			boolean isTimelineHacked, boolean isTimoutHacked, boolean isResponseHacked) 
	{
		String now = getNowTimestamp();
		StringBuilder sb = new StringBuilder(now);
		return sb.append(SEP).append(errorCode)
					.append(SEP).append(sender)
					.append(SEP)
					.append(SEP).append(msgId)
					.append(SEP)
					.append(SEP).append(messageType)
					.append(SEP).append(errorDesc)
					.append(SEP).append(isTimelineHacked)
					.append(SEP).append(isTimoutHacked)
					.append(SEP).append(isResponseHacked)					
					.toString();
	}
	*/
	
	
	/**
	 * Builds a traceline (this is for timeout)
	 * 
	 * @param errorCode
	 * @param sender
	 * @param msgId
	 * @param messageType
	 * @param errorDesc
	 * @param isTimoutHacked super(normalFailProb,hackFailProbab );
	 * @return
	 */
	public static String makeTraceLineForTimeout(String errorCode, String sender, String msgId, String messageType, String errorDesc, boolean isTimoutHacked) 
	{
		String now = getNowTimestamp();
		StringBuilder sb = new StringBuilder(now);
		return sb.append(SEP).append(errorCode)
					.append(SEP).append(sender)
					.append(SEP)
					.append(SEP).append(msgId)
					.append(SEP)
					.append(SEP).append(messageType)
					.append(SEP).append(errorDesc)
					.append(SEP).append(false)
					.append(SEP).append(isTimoutHacked)
					.append(SEP).append(false)
					.toString();
	}
	
	/**
	 * Analogous the condensed version
	 * (updated 25/12/2012)
	 * @param errorCode
	 * @param isTimelineHacked 
	 * @param isTimoutHacked
	 * @param isResponseHacked 
	 * @return
	 */
	public static String makeTraceLineSmall(String errorCode,  boolean isTimelineHacked, boolean isTimoutHacked, boolean isResponseHacked) 
	{
		String now = getNowTimestamp();
		Long nowLong = getNowTimeLong();
		StringBuilder sb = new StringBuilder(now);
		return sb.append(SEP).append(nowLong)
					.append(SEP).append(errorCode)
					.append(SEP).append((isTimelineHacked==true) ? 1 : 0)
					.append(SEP).append((isTimoutHacked==true) ? 1 : 0)
					.append(SEP).append((isResponseHacked==true) ? 1 : 0)
					.append(SEP).append(Context.TC_NUMBER)
					.append(SEP).append(Context.UNTIL_STOP_SECS)
					.toString();
	}
	
	/**
	 * Builds a traceline
	 * 
	 * @param errorCode
	 * @param sender
	 * @param receiver
	 * @param msgId
	 * @param msgOrdinal
	 * @param messageType
	 * @param errorDesc
	 * @param isTimelineHacked
	 * @param isTimoutHacked
	 * @param isResponseHacked
	 * @return
	 */
	public static String makeTraceLineFull(String errorCode, String sender, String receiver,
			String msgId, String msgOrdinal, String messageType, String errorDesc, 
			boolean isTimelineHacked, boolean isTimoutHacked, boolean isResponseHacked) 
	{
		
		String now = getNowTimestamp();
		StringBuilder sb = new StringBuilder(now);
		return sb.append(SEP).append(errorCode)
					.append(SEP).append(sender)
					.append(SEP).append(receiver)
					.append(SEP).append(msgId)
					.append(SEP).append(msgOrdinal)
					.append(SEP).append(messageType)
					.append(SEP).append(errorDesc)
					.append(SEP).append(isTimelineHacked)
					.append(SEP).append(isTimoutHacked)
					.append(SEP).append(isResponseHacked)
					.toString();
	}
	
	

	
	
	

	/**
	 * Append the text to the text file
	 * 
	 * @param text
	 * @param textForExp 
	 */
	public void appendToTraceFile(String text, String textForExp)
	{
		if (this.fileWriter!=null && this.fileWriterForExp!=null)
		{
			StringBuilder sb = new StringBuilder(Integer.toString(countWrites));
			StringBuilder sbForExp = new StringBuilder(Integer.toString(countWrites));
			
			sb.append(SEP).append(text);
			sbForExp.append(SEP).append(textForExp);
			
			this.fileWriter.println(sb.toString());
			this.fileWriterForExp.println(sbForExp.toString());
			
			if (countWrites++ % modForWrites == 0)
			{
				this.fileWriter.flush();
				this.fileWriterForExp.flush();
			}
		}
	}

	
	/**
	 * Get the new File 
	 * 
	 * @param datepart
	 * @return
	 */
	private File getTraceFile(String datepart) 
	{
		String randompart = SecUtils.getHex(SecUtils.getRandomBytes(SecUtils.getSeedAsString(),4));
        String filename = tracepath + "/"+ contextName+"_" + datepart +"_" +randompart+".txt.csv";
        return new File(filename);
	}
	
	/**
	 * Get the new File for experimenation
	 * @param datepart
	 * @return
	 */
	private File getTraceFileForExperimentation(String datepart) {
		String randompart = SecUtils.getHex(SecUtils.getRandomBytes(SecUtils.getSeedAsString(),4));
        String filename = tracepath + "/"+ contextName+"_" + datepart +"_"+randompart+"_" + Context.UNTIL_STOP_SECS+".dat.csv";
        return new File(filename);
	}
	
	/**
	 */
	public void setPrinterFileWriter() {
		
		if (this.tracepath!=null)
		{
			String datepart = getNowTimestamp();
			
			File file = getTraceFile(datepart);
			File fileForExp = getTraceFileForExperimentation(datepart);
	        try {
				 fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				 fileWriterForExp = new PrintWriter(new BufferedWriter(new FileWriter(fileForExp)));
			} catch (IOException e) {
				Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
}
