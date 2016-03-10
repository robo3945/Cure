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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * It defines the context of the TimeController
 */
public class Context {

	private static class Log4jConfigurator 
	{
		public void setup(URL xmlFilename) 
		{
	        if(xmlFilename != null) 
	        	DOMConfigurator.configure(xmlFilename);
	        else 
	        	Logger.getRootLogger().error("File di configurazione Log4j non trovato.");
		}
	}

	
	/**
	 * Singleton
	 */
	private static Context instance = new Context();
	
	/**
	 * It's the buffer of packets on which evaluate the timeline consistency
	 */
	public static int TIMELINEWINDOWPACKSIZE;

	/**
	 * This is the timeout fake value 
	 */
	final public static int TIMEOUT_FAKE_VALUE = -1;
	
	/**
	 * Localhost
	 */
	public final static String LOCALHOST = "127.0.0.1";

	// XXX: questo flag permette di escludere i pacchetti di tipo Response ad invii dello stesso CC; nella prima versione venivano inclusi e questo provocava un innalzamento dei Timeout
	/**
	 * The ordinal for Response Packet
	 */
	public static final int ORDINAL_FOR_RESPONSE_PACKET = -999;
	
	/**
	 * it is the MOD for when print the debug information
	 */
	public final static long MOD_FOR_DEBUG_CONT_THREAD = 1000;
	
	/**
	 * The max lenght of an UDP buffer
	 */
	public static  int UDP_PACKET_BUF_MAXSIZE;

	/**
	 * The SO_RCVBUF
	 */
	public static int UDP_RECEIVE_BUF_SIZE;
	
	/**
	 * The socket timeout for the receive
	 */
	public static  int SOCKET_TIMEOUT;
	
	/**
	 * The time when it has to check the timeline
	 */
	public static  int TIMELINE_CHECK_DELAY;
	
	/**
	 * The time when it has to check the timeout
	 */
	public static  int TIMEOUT_CHECK_DELAY;
	
	/**
	 * This is the timeout threshold
	 */
	public static int TIMEOUT_THRESHOLD;
	
	
	/**
	 * The time when it has to check the response
	 */
	public static  int RESPONSE_CHECK_DELAY;

	/**
	 * How many rows before flush
	 */
	public static  int MOD_FOR_TRACES_FLUSH;
	
	/**
	 * The time interval when it has to send HB
	 */
	public static int SENDER_UDP_SEND_DELAY;
	
	/**
	 * The encoding
	 */
	public static  Charset charset = Charset.forName("UTF-8");
	
	/**
	 * Lenght of the SALT
	 */
	public static  int saltLenght;
	
	/**
	 * Lenght of the challenge
	 */
	public static  int CHALLENGE_LENGHT;
	
	/**
	 * The challenge hash
	 * 
	 */
	public static  String CHALLANGE_HASH;
	
	/**
	 * The cyphering algorithm
	 */
	public static String CRYPTO_ALGO;
	
	/**
	 * Computation's time
	 */
	public static int UNTIL_STOP_SECS;
	
	/**
	 * Number of time controller
	 */
	public static int TC_NUMBER;
	
	/**
	 * Probability of failure of Guests
	 */
	public static Double FAIL_PROB_GUESTS;
	
	/**
	 * Probability of failure of Hosts
	 */
	public static Double FAIL_PROB_HOSTS;
	
	/**
	 * Probability of hacking of Guests
	 */
	public static Double HACK_PROB_GUESTS;
	
	/**
	 * Probability of hacking of Hosts
	 */	
	public static Double HACK_PROB_HOSTS;
	
	/**
	 * Simulation Model for Guests
	 */
	public static String GUEST_SIM_MODEL;
	
	/**
	 * Simulation Model for Hosts
	 */
	public static String HOST_SIM_MODEL;
	
	/**
	 * Guest for Hosts
	 */
	public static int GUEST_FOR_HOST;

	/**
	 * Ratio for the Commander stop
	 * 
	 * This is used to diminish the time that use the sender to send packets to the 
	 * receivers. So the receivers have time to detect all the sent packets
	 * 
	 */
	public static int STOP_COMMANDER_TIMER_RATIO;

	/**
	 * The config file
	 */
	public static String configFile;

	/**
	 * Fake time to manage timeout hacked
	 */
	//public static long FAKETIME_FOR_TIMEOUTHACKED = -1;
	
	/**
	 * Set the config properties
	 * 
	 * @param fileName
	 * @param isCC 
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public static void setConfigProperties(String fileName, boolean isCC) throws InvalidPropertiesFormatException, IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		Properties properties = new Properties();
		properties.loadFromXML(fis);
		
		// FOR CC or GUEST/HOST
		
		String prefix="";
		if (isCC)
		{
			prefix = "cc.";
		}
			
		UDP_PACKET_BUF_MAXSIZE = Integer.parseInt((String) properties.get(prefix+"udp.packetBufMaxLenght"));
		UDP_RECEIVE_BUF_SIZE = Integer.parseInt((String) properties.get(prefix+"udp.socketRecvBufSize"));
		
		SOCKET_TIMEOUT = Integer.parseInt((String) properties.get(prefix+"udp.socketTimeout"));
		TIMELINE_CHECK_DELAY = Integer.parseInt((String) properties.get(prefix+"delay.timeline.check"));
		TIMEOUT_CHECK_DELAY = Integer.parseInt((String) properties.get(prefix+"delay.timout.check"));
		RESPONSE_CHECK_DELAY = Integer.parseInt((String) properties.get(prefix+"delay.response.check"));
		
		SENDER_UDP_SEND_DELAY  = Integer.parseInt((String) properties.get(prefix+"delay.sender"));
		
		TIMEOUT_THRESHOLD = Integer.parseInt((String) properties.get(prefix+"timout.threshold"));
		TIMELINEWINDOWPACKSIZE = Integer.parseInt((String) properties.get(prefix+"timeline.windowsize"));
		
		fis.close();
	}
	
	/**
	 * Set the general Config Props
	 * @param fileName
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public static void setGeneralConfigProperties(String fileName) throws InvalidPropertiesFormatException, IOException
	{
		FileInputStream fis = new FileInputStream(fileName);
		Properties properties = new Properties();
		properties.loadFromXML(fis);
		
		// GENERAL SETTINGS
		
		MOD_FOR_TRACES_FLUSH = Integer.parseInt((String) properties.get("trace.rawNumBuffer"));
		
		STOP_COMMANDER_TIMER_RATIO = Integer.parseInt((String) properties.get("commander.stop.ratio"));

		charset = Charset.forName((String) properties.get("charset"));
		
		saltLenght = Integer.parseInt((String) properties.get("response.saltLenght"));
		CHALLENGE_LENGHT = Integer.parseInt((String) properties.get("response.challengeLenght"));		
		CHALLANGE_HASH= (String) properties.get("response.challengeHashAlg");
		
		CRYPTO_ALGO = (String) properties.get("cypher.alg");

		FAIL_PROB_GUESTS = Double.parseDouble((String) properties.get("fail.probab.guest"));
		FAIL_PROB_HOSTS= Double.parseDouble((String) properties.get("fail.probab.host"));
		HACK_PROB_GUESTS = Double.parseDouble((String) properties.get("hack.probab.guest"));
		HACK_PROB_HOSTS = Double.parseDouble((String) properties.get("hack.probab.host"));
		GUEST_SIM_MODEL= (String) properties.get("simmodel.guest");
		HOST_SIM_MODEL= (String) properties.get("simmodel.host");
		GUEST_FOR_HOST = Integer.parseInt((String) properties.get("guest.for.host"));
		
		fis.close();
	}
	
	
	
	
	/**
	 * The singleton 
	 * @return
	 */
	public static Context getInstance()
	{
		return instance;
	}

	private Logger logger; 
	private String localIp;

	/**
	 * Sets the localIp
	 * @param localIp
	 */
	public void setLocalIp(String localIp)
	{
		this.localIp = localIp;
	}
	
	/**
	 * Gets the localIp
	 * @return
	 */
	public String getLocalIp()
	{
		return this.localIp;
	}
	
	/**
	 * Constructor
	 */
	private Context() {
		
		initLogger();
	}
	
	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * 
	 */
	protected void initLogger() 
	{
		setLog();
		logger = Logger.getLogger("it.cure");
	}
	
	private void setLog() {
		URL url = getClass().getClassLoader().getResource("prop/log4j.xml");
		Log4jConfigurator logConf = new Log4jConfigurator();
		logConf.setup(url);		
	}
	
}
