/*******************************************************************************
 * Copyright (c)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.controller.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.interfaces.controller.BVRNotifiableController;
import no.sintef.bvr.tool.interfaces.controller.command.Command;
import no.sintef.bvr.tool.interfaces.ui.editor.BVRUIKernelInterface;
import no.sintef.bvr.tool.interfaces.ui.editor.Pair;
import bvr.VNode;
import bvr.NamedElement;
import bvr.VSpec;
import bvr.Variable;


public class UpdateVClassOccurence<EDITOR_PANEL, MODEL_PANEL> extends UpdateVSpec<EDITOR_PANEL, MODEL_PANEL> {
	
	protected int lower;
	protected int upper;
	
	protected Map<Variable, String> varNames = new HashMap<Variable, String>();
	protected Map<Variable, String> varTypes = new HashMap<Variable, String>();
	protected String comment;
	
	protected String typeName;
		
	public Command<EDITOR_PANEL, MODEL_PANEL> init(BVRUIKernelInterface<EDITOR_PANEL, MODEL_PANEL> rootPanel, Object p, JComponent parent,
			Map<JComponent, NamedElement> vmMap, List<JComponent> nodes,
			List<Pair<JComponent, JComponent>> bindings, BVRNotifiableController controller) {
		return super.init(rootPanel, (VSpec) p, parent, vmMap, nodes, bindings, controller);
	}

	@SuppressWarnings("unchecked")
	public JComponent execute() {
		controller.getVSpecControllerInterface().setVClassOccurenceGroupMultiplicityUpperBound(parent, upper);
		controller.getVSpecControllerInterface().setVClassOccurenceGroupMultiplicityLowerBound(parent, lower);
		
		controller.getVSpecControllerInterface().setNodeName(parent, name);
		controller.getVSpecControllerInterface().setNodeComment(parent, comment);
		
		controller.getVSpecControllerInterface().setVClassOccurenceType(parent, typeName);
		
		for(Variable v : ((VNode) namedElement).getVariable()){
				String newName = varNames.get(v);
				String newType = varTypes.get(v);
				controller.getVSpecControllerInterface().updateVariable(v, newName, newType);
		}
		
		return parent;
	}

	@Override
	public void setVariable(Variable v, String name, String type) {
		varNames.put(v, name);
		varTypes.put(v, type);
	}

	@Override
	public void setComment(String text) {
		comment = text;
	}
	
	public void setLower(int lower) {
		this.lower = lower;
	}
	
	public void setUpper(int upper) {
		this.upper = upper;
	}
	
	public void setType(String typeName) {
		this.typeName = typeName;
	}

}
