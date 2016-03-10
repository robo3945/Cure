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

import java.io.Serializable;

/**
 * It's a generic Signal
 */
public class Signal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 962362606062023128L;
	
	private String signalContent;
	
	/**
	 * Creates a new Signal
	 * @param signalContent 
	 */
	public Signal(String signalContent) {
		this.signalContent = signalContent;
	}
	
	/**
	 * Get the signal content
	 * @return 
	 */
	public String getSignalText() {
		return this.signalContent;
	}

	/**
	 * Set the signal content
	 * @param content 
	 */
	public void setSignalText(String content) {
		this.signalContent = content;
	}
	
	/**
	 * ToString() override
	 */
	@Override 
	public String toString()
	{
		return this.getSignalText();		
	}



}
