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

package it.cure.core.implem.timecontroller.base;

import it.cure.core.enums.ENodeType;

import java.io.Serializable;

/**
 * A Node of the network
 *
 */
public class Node implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 745202370101179989L;
	private String ip;
	private int port;
	private int id;
	private String name;
	private ENodeType targetType; 
	private String description;
			
	/**
	 * Constructor
	 * @param name 
	 * @param ip 
	 * @param port 
	 * @param targetType 
	 */
	public Node(String name, String ip, int port, ENodeType targetType) {
		this(name, ip, port, targetType, null);
	}

	/**
	 * Constructor
	 * @param name 
	 * @param ip 
	 * @param port 
	 * @param targetType 
	 * @param description 
	 */
	public Node(String name, String ip, int port, ENodeType targetType, String description) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.targetType = targetType;
		this.description = description;
		this.id = this.hashCode();
	}
	

	public String getDesc() 
	{
		return this.description;
	}

	public int getId() {
		return this.id;
	}
	
	public String getIp() {
		return this.ip;
	}

	public String getName() {		
		return this.name;
	}

	public int getPort() {
		return this.port;
	}

	public ENodeType getType() {
		return this.targetType;
	}

	public void setDesc(String desc) {
		this.description = desc;		
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;

	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setType(ENodeType targetType) {
		this.targetType = targetType;

	}

}
