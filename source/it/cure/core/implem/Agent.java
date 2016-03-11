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

package it.cure.core.implem;

import it.cure.core.implem.security.keys.SymKeyMgr;
import it.cure.core.implem.simmodel.AlwaysTrueSimulationModel;
import it.cure.core.implem.simulation.SimpleDistributedSimulator;
import it.cure.core.interfaces.model.ISimulationModel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.InvalidPropertiesFormatException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/** 
 * The Agent
 */
public class Agent {

	/**
	 * The main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		String keyStorePath = null;
		String tracesDir =null;
		String topologyFile =null;
		int startPort = 0;
		int numKeys = 0;
		boolean parserError = false;
	    
		Context.getInstance().getLogger().info("Heap max:"+ ((Runtime.getRuntime().maxMemory()/1024)/1024)+" MB");
		Context.getInstance().getLogger().info("Free memory:"+ ((Runtime.getRuntime().freeMemory()/1024)/1024)+" MB");
		Context.getInstance().getLogger().info("Totale memory:"+ ((Runtime.getRuntime().totalMemory()/1024)/1024)+" MB");
		
		
		Options options = new Options();
		
		// Key store
		options.addOption("k", "createKeyStore", true, "KEYSTORE: create the keystore");
		options.addOption("kn", "numkeysToCreate", true, "KEYSTORE: number of keys to create");
		
		// Topology based
		options.addOption("tf", "topologyFile", true,"TOPOLOGY SIM BASED: The topology XML file");
		options.addOption("tIp", "localIp", true,"TOPOLOGY SIM BASED: define the local IP of the machine");

		// Local simulator only
		options.addOption("nTc", "tcNumber", true, "LOCAL SIM BASED: Time Controller number to activate");
		options.addOption("sp", "startUdpPort", true, "LOCAL SIM BASED: Start UDP port");
		
		// General
		options.addOption("kp", "keyStorePath", true,"GENERAL: the path of the keystore to use");
		options.addOption("d", "delayTime", true,"GENERAL: how long the test runs");
		options.addOption("td", "traceDir", true,"GENERAL: the traces dir");
		options.addOption("cfg", "configFile", true,"GENERAL: the config file");

		options.addOption("h", "help", false, "GENERAL: print the help");
		
		CommandLineParser parser = new PosixParser();
		
		try
		{
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("h"))
			{
				printHelp(options);
				return;
			}
			
			// Topology Based
			if (line.hasOption("tf"))
			{
				topologyFile = line.getOptionValue("tf");
				if (line.hasOption("tIp"))
				{
					Context.getInstance().setLocalIp(line.getOptionValue("tIp"));
				}
				else
				{
					Context.getInstance().getLogger().error("Topology based simulation needs the definition of the localIp");
					printHelp(options);
					return;
				}
			}
			else
				// the local simulator parameters
				if (line.hasOption("nTc"))
				{
						Context.TC_NUMBER = Integer.parseInt(line.getOptionValue("nTc"));
						
						if (line.hasOption("sp"))
						{
							startPort = Integer.parseInt(line.getOptionValue("sp"));
						}
						else
						{
							Context.getInstance().getLogger().error("You need to specify the starting UDP port");
							printHelp(options);
							return;
						}
				}				
				else
				// Create the key stores
					if (line.hasOption("k"))
					{
						keyStorePath = line.getOptionValue("k");						
						if (line.hasOption("kn"))
						{
							numKeys = Integer.parseInt(line.getOptionValue("kn"));
							String path = SymKeyMgr.createKeyStore(keyStorePath, numKeys);
							Context.getInstance().getLogger().info("KeyStoreFile created. See in \""+path+"\"");
							return;					
						}
						else
						{
							Context.getInstance().getLogger().error("You need the number of keys to create");
							printHelp(options);
							return;
						}
					}

			
			// General option
			if (line.hasOption("kp"))
			{
				keyStorePath = line.getOptionValue("kp");
			}
			else
			{
				Context.getInstance().getLogger().error("Key store path is needed");
				parserError = true;
			}
			
			if (line.hasOption("d"))
			{
				Context.UNTIL_STOP_SECS = Integer.parseInt(line.getOptionValue("d"));
			}
			else
			{
				Context.getInstance().getLogger().error("Delay time is needed");
				parserError = true;
			}

				
			if (line.hasOption("td"))
			{
				tracesDir = line.getOptionValue("td");
			}
			else
			{
				Context.getInstance().getLogger().error("Trace Directory path is needed");
				parserError = true;
			}
			
			if (line.hasOption("cfg"))
			{
				Context.configFile = line.getOptionValue("cfg");
				try {
					Context.setGeneralConfigProperties(Context.configFile);
					Context.getInstance().getLogger().info("Using configuration file: GENERAL");
					
					// Start the simulation!
					
					Context.getInstance().getLogger().info("Starting the simulation...");
					
					try {
										
						Context.getInstance().getLogger().info("Using the Guest model: "+Context.GUEST_SIM_MODEL.trim());
						Context.getInstance().getLogger().info("Using the Host model: "+Context.HOST_SIM_MODEL.trim());
						
						// ------- SimulationModel for the Guests
						@SuppressWarnings("unchecked")
						Class<ISimulationModel> cG = (Class<ISimulationModel>) Class.forName(Context.GUEST_SIM_MODEL.trim());
						ISimulationModel simModelGuests = (ISimulationModel) cG.getDeclaredConstructor(double.class, double.class).newInstance(Context.FAIL_PROB_GUESTS, Context.HACK_PROB_GUESTS);
						
						// ------- SimulationModel for the Hosts
						@SuppressWarnings("unchecked")
						Class<ISimulationModel> cH = (Class<ISimulationModel>) Class.forName(Context.HOST_SIM_MODEL.trim());
						ISimulationModel simModelHosts = (ISimulationModel) cH.getDeclaredConstructor(double.class, double.class).newInstance(Context.FAIL_PROB_HOSTS, Context.HACK_PROB_HOSTS);
						
						// ------- SimuationModel for the CC
						
						// XXX: This means that the CC does not FAIL! So there aren't errors packets on Guest!
						ISimulationModel simModelCC = new AlwaysTrueSimulationModel();
						
						SimpleDistributedSimulator simulator = new SimpleDistributedSimulator(tracesDir);
						simulator.nGuestOneCC(topologyFile, keyStorePath,  simModelGuests, simModelHosts, simModelCC, StringUtils.join(args," "));
						
					} catch (ClassNotFoundException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (InstantiationException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (IllegalAccessException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (IllegalArgumentException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (SecurityException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (InvocationTargetException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					} catch (NoSuchMethodException e) {
						Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
					}
					
					
				} catch (InvalidPropertiesFormatException e) {
					Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
				} catch (IOException e) {
					Context.getInstance().getLogger().error(ExceptionUtils.getStackTrace(e));
				}
			}
			else
			{
				Context.getInstance().getLogger().error("Configuration file path is needed");
				parserError = true;
			}

			if (parserError)
			{
				printHelp(options);
			}
			
		} catch (ParseException exp)
		{			
			Context.getInstance().getLogger().error("Command line error: " + exp.getMessage());
		}
	
		
			
		
	}

	protected static void printHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Cure Agent v. 1.0", options);

	}
	
}
