package no.sintef.bvr.tool.ui.command.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.controller.BVRNotifiableController;
import no.sintef.bvr.tool.ui.loader.Pair;
import no.sintef.bvr.ui.framework.elements.ChoicePanel;
import bvr.NamedElement;
import bvr.VSpec;

public class CutEvent implements ActionListener {

	private Map<JComponent, NamedElement> vmMap;
	private JPanel p;
	private BVRNotifiableController view;

	public CutEvent(JPanel cp, Map<JComponent, NamedElement> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, BVRNotifiableController view) {
		this.p = cp;
		this.vmMap = vmMap;
		this.view = view;
	}

	public void actionPerformed(ActionEvent arg0) {
		/*NamedElement v = vmMap.get(p);
		
		// Modify model
		VSpec parent = null;
		for(NamedElement _c : vmMap.values()){
			if(_c instanceof VSpec){
				VSpec c = (VSpec)_c;
				if(c.getChild().contains(v))
					parent = c;
			}
		}
		
		if(parent != null){
			//parent.getChild().remove(v);
			Context.eINSTANCE.getEditorCommands().removeNamedElementVSpec(parent, v);
		}else{
			ConfigurableUnit cu = view.getCU();
			Context.eINSTANCE.getEditorCommands().removeOwnedVSpecConfigurableUnit(cu, v);
			//cu.getOwnedVSpec().remove(v);
		}
		
		// Save cut
		Main.vSpecCut = v;*/
	}

}
