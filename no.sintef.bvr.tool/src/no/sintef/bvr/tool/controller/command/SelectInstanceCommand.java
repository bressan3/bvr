/*******************************************************************************
 * Copyright (c)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.controller.command;


import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bvr.NamedElement;
import no.sintef.bvr.tool.interfaces.controller.BVRNotifiableController;
import no.sintef.bvr.tool.interfaces.controller.command.Command;
import no.sintef.bvr.tool.interfaces.ui.editor.BVRUIKernelInterface;
import no.sintef.bvr.tool.interfaces.ui.editor.Pair;
import no.sintef.bvr.tool.ui.edit.BVREditorPanel;
import no.sintef.bvr.ui.framework.SelectElement;
import no.sintef.bvr.ui.framework.elements.BVRModelPanel;


public class SelectInstanceCommand<EDITOR_PANEL extends BVREditorPanel, MODEL_PANEL extends BVRModelPanel>
		implements Command<EDITOR_PANEL, MODEL_PANEL> {

    private BVRUIKernelInterface<EDITOR_PANEL, MODEL_PANEL> kernel;
    private static SelectElement currentlySelected = null;
    private JComponent parent;
    private SelectElement selectableElement;
    
	public static void unselect() {
		System.out.println("unselect");
		if (currentlySelected != null)
			currentlySelected.setSelected(false);
	}

	public Command<EDITOR_PANEL, MODEL_PANEL> init(BVRUIKernelInterface<EDITOR_PANEL, MODEL_PANEL> rootPanel, Object p, JComponent parent_, Map<JComponent, NamedElement> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, BVRNotifiableController view) {
		
		kernel = rootPanel;
		parent = parent_;
        if (p instanceof SelectElement) {
        	selectableElement = (SelectElement) p;
        }else{
        	throw new UnsupportedOperationException(p + " not instance of SelectElement");
        }
        return this;
	}

	public JComponent execute() {
		if (currentlySelected != null && currentlySelected != selectableElement) {
            currentlySelected.setSelected(false);
        }
        currentlySelected = selectableElement;
		
		currentlySelected.setSelected(!currentlySelected.isSelected());

		((BVREditorPanel) kernel.getEditorPanel()).showPropertyFor((JPanel) currentlySelected, parent);


        ((JPanel)currentlySelected).repaint();
        ((JPanel)currentlySelected).revalidate();
        kernel.getModelPanel().repaint();
        kernel.getModelPanel().revalidate();

		return null;
	}
}
