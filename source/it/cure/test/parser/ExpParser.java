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

package it.cure.test.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author roberto.battistoni
 *
 */
public class ExpParser {

	private File traceFile;
	private long timeDelay;
	private int nResponseErr;
	private int nTimeoutErr;
	private int nTimelineErr;
	private int nResponseHack;
	private int nTimeoutHack;
	private int nTimelineHack;		
	private int nTracelines;
	private int stopTimeout;
	private int nTc;
	private double timelineFP;
	private double timeoutFP;
	private double responseFP;
	
	/**
	 * Constructor
	 * @param file 
	 * 
	 */
	public ExpParser(File file) {
		getFile(file);
	}
	
	/**
	 * Get the File
	 * @param file 
	 */
	private void getFile(File file)
	{
		this.traceFile = file;
	}

	/**
	 * 
	 * Analyze the file 
	 * @throws IOException
	 */
	public void analyze() throws IOException
	{		
		List<String> traceLines = FileUtils.readLines(traceFile);

		int cont =0;
		long startTime  =0;
		long endTime =0;
		for (String line: traceLines)
		{					
			String[] parts = line.split(";");
		
			if (cont==0)
				startTime = Long.parseLong(parts[2]);
			if (cont == traceLines.size()-1)
				endTime = Long.parseLong(parts[2]);
			
			// 1: date
			// 3: type message
			// 4,5,6: timeline, timeout, response

			if (parts[3].equals("TIMELINE"))
			{
				nTimelineErr ++;
				nTimelineHack += Integer.parseInt(parts[4]);
				
			}
			else
				if (parts[3].equals("TIMEOUT"))
				{
					nTimeoutErr ++;
					nTimeoutHack +=Integer.parseInt(parts[5]);

				}
				else
					if (parts[3].equals("RESPONSE"))
					{
						nResponseErr ++;
						nResponseHack +=Integer.parseInt(parts[6]);
					}
			
			if (parts.length>=8)
			{
				nTc = Integer.parseInt(parts[8]);	
				stopTimeout = Integer.parseInt(parts[7]);
			}
			
			cont++;
		}
		
		this.timeDelay = endTime - startTime;
		this.nTracelines = traceLines.size();
		
		// False Positives
		// (refactored on 25/12/2012)
		if (nTimelineErr>0)
		{
			timelineFP = 1.0-nTimelineHack*1.0/nTimelineErr;
		}
		if (nTimeoutErr>0)
		{
			timeoutFP = 1.0-nTimeoutHack*1.0/nTimeoutErr;
		}
		if (nResponseErr>0)
		{
			responseFP = 1.0-nResponseHack*1.0/nResponseErr;
		}
		
	}

	/**
	 * Reset the internal state
	 */
	public void reset()
	{
		timeDelay =0;
		nResponseErr =0;
		nTimeoutErr =0;
		nTimelineErr =0;			
	}

	/**
	 * @return the stopTimeout
	 */
	public int getStopTimeout() {
		return stopTimeout;
	}

	/**
	 * @return the nTc
	 */
	public int getnTc() {
		return nTc;
	}
	
	/**
	 * @return the timeDelay
	 */
	public long getTimeDelay() {
		return timeDelay;
	}

	/**
	 * @return the nResponse
	 */
	public int getnResponse() {
		return nResponseErr;
	}

	/**
	 * @return the nTimeout
	 */
	public int getnTimeout() {
		return nTimeoutErr;
	}

	/**
	 * @return the nTimeline
	 */
	public int getnTimeline() {
		return nTimelineErr;
	}

	/**
	 * @return the traceFile
	 */
	public File getTraceFile() {
		return traceFile;
	}

	/**
	 * @return the nTracelines
	 */
	public int getnTracelines() {
		return nTracelines;
	}

	/**
	 * @return the nResponseHack
	 */
	public int getnResponseHack() {
		return nResponseHack;
	}

	/**
	 * @return the nTimeoutHack
	 */
	public int getnTimeoutHack() {
		return nTimeoutHack;
	}

	/**
	 * @return the nTimelineHack
	 */
	public int getnTimelineHack() {
		return nTimelineHack;
	}

	/**
	 * @return the timelineFP
	 */
	public double getTimelineFP() {
		return timelineFP;
	}

	/**
	 * @return the timeoutFP
	 */
	public double getTimeoutFP() {
		return timeoutFP;
	}

	/**
	 * @return the responseFP
	 */
	public double getResponseFP() {
		return responseFP;
	}
	
}
