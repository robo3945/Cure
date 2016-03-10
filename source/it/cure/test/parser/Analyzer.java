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

import it.cure.core.implem.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Parsing an entire directory tree outcoming a result file
 * 
 * The outcome file is to estimate the Numbers and the FP rate of different error types
 *
 */
public class Analyzer {

	private String ownDir;
	private final static String SEP = ";" ;
	
	
	// Running the parser
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Context.getInstance().getLogger().info("Starting the parsing phase...");
		Analyzer dp = new Analyzer(args[0]);
		dp.runNumbFPRate();
		dp.runErrorTypeDistribution();
		Context.getInstance().getLogger().info("end.");
	}
	
	/**
	 * Constructor
	 * @param dir 
	 */
	public Analyzer(String dir) {
		Context.getInstance().getLogger().info("Args Dir is: "+dir);
		this.ownDir = dir;		
	}
	
	/**
	 * Execute the analysis through a directory: it return the errors number and FP rate
	 */
	public void runNumbFPRate()
	{
		ArrayList<String> resultArray = new ArrayList<String>();
		
		for (File d : new File(this.ownDir).listFiles())
		{
			if (d.isDirectory() == true)
			{
				for (Iterator<File> itInside = FileUtils.iterateFiles(d, new String[] { "dat.csv"}, false); itInside.hasNext();)
				{
					File f = (File) itInside.next();
						
					// Analyze the file into the directory
					ExpParser expParser = new ExpParser(f);
					try {
						expParser.analyze();
					} catch (IOException e) {	
						Context.getInstance().getLogger().error("problema nell'analisi del seguente file: "+f.getPath());
					}
					StringBuilder result = new StringBuilder();					
					result.append(FilenameUtils.getName(expParser.getTraceFile().getPath())).append(SEP).
							append(expParser.getnTc()).append(SEP).
							append(expParser.getStopTimeout()).append(SEP).
							append(expParser.getTimeDelay()).append(SEP).
							append(expParser.getnTimeline()).append(SEP).
							append(expParser.getnTimelineHack()).append(SEP).
							append(expParser.getnTimeout()).append(SEP).
							append(expParser.getnTimeoutHack()).append(SEP).
							append(expParser.getnResponse()).append(SEP).
							append(expParser.getnResponseHack()).append(SEP).
							append(expParser.getTimelineFP()).append(SEP).
							append(expParser.getTimeoutFP()).append(SEP).
							append(expParser.getResponseFP()).append(SEP).
							append(expParser.getnTracelines());
				
					resultArray.add(result.toString());
				}
			}
        }
		
		// creates the resulting file
		
		String outputPath = this.ownDir+"/"+"res.NumbersFPRate.outcome.txt";
		File resFile = new File(outputPath);
		Context.getInstance().getLogger().info("The output path is: "+outputPath);
		try 
		{
			FileUtils.writeLines(resFile, resultArray);
		} catch (IOException e) 
		{
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
		
		
	}
	
	/**
	 * Execute the analysis through a directory:it return the distribution of errors type for the execution time
	 */
	public void runErrorTypeDistribution()
	{
		ArrayList<String> resultArray = new ArrayList<String>();
		
		for (Iterator<File> itInside = FileUtils.iterateFiles(new File(this.ownDir), new String[] { "dat.csv"}, true); itInside.hasNext();)
		{
			File f = (File) itInside.next();
				
			// Analyze the file into the directory
			ExpParser expParser = new ExpParser(f);
			try {
				expParser.analyze();
			} catch (IOException e) {
				Context.getInstance().getLogger().error("problema nell'analisi del seguente file: "+f.getPath());
			}
			StringBuilder result = new StringBuilder();					
			result.append(FilenameUtils.getName(expParser.getTraceFile().getPath())).append(SEP).
					append(expParser.getnTc()).append(SEP).
					append(expParser.getTimeDelay()).append(SEP).
					append(expParser.getnTimeline()).append(SEP).
					append(expParser.getnTimeout()).append(SEP).
					append(expParser.getnResponse()).append(SEP).
					append(expParser.getnTracelines());
		
			resultArray.add(result.toString());
        }
		
		// creates the resulting file
		
		String outputPath = this.ownDir+"/"+"res.ErrorTypeDistribution.outcome.txt";
		File resFile = new File(outputPath);
		Context.getInstance().getLogger().info("The output path is: "+outputPath);
		try 
		{
			FileUtils.writeLines(resFile, resultArray);
		} catch (IOException e) 
		{
			Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
		}
		
		
	}
}
