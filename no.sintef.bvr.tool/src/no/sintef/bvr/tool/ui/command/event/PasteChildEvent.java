/*******************************************************************************
 * Copyright (c)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.ui.command.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import no.sintef.bvr.tool.interfaces.controller.BVRNotifiableController;


public class PasteChildEvent implements ActionListener {

	private Object p;
	private BVRNotifiableController controller;

	public PasteChildEvent(Object cp, BVRNotifiableController controller) {
		this.p = cp;
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		controller.getVSpecControllerInterface().pastNamedElementAsChild(p);
	}

}
