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

package it.cure.core.implem.timecontroller.message.base;

import it.cure.core.implem.timecontroller.base.Node;
import it.cure.core.implem.timecontroller.tcmanage.TcDataController;
import it.cure.core.interfaces.model.ISimulationModel;
import it.cure.core.interfaces.model.ISimulationParameter;

import java.io.IOException;

/**
 * The server receives the HB and SIG from various clients, so:
 * 		1) it stores the messages received and can do all the analysis it wants
 *
 */
public abstract class AbstractMessageServerThread extends Thread implements ISimulationParameter {

	protected TcDataController tcContext;
	protected ISimulationModel[] simulationModel;
	protected Node receiver;
	
    /**
     * Constructor
     * @param tcContext 
     * @param receiver 
     * 
     * @param name name of the Server
     * @param port
     * @throws IOException
     */
    public AbstractMessageServerThread(TcDataController tcContext, Node receiver, String name, int port) throws IOException {
        super("Thread-server-"+name);
        
        this.tcContext = tcContext;
        this.receiver = receiver;
    }
	
    @Override
	public void setSimulationModel(ISimulationModel[] simulationModel) {
		this.simulationModel = simulationModel;
	}

	

}
