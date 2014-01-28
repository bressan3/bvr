package no.sintef.bvr.tool.ui.command;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.ui.editor.BVRUIKernel;
import no.sintef.bvr.tool.ui.loader.BVRView;
import no.sintef.bvr.tool.ui.loader.Pair;
import bvr.NamedElement;
import bvr.VSpec;

public class UpdateVSpec implements Command {

	protected BVRUIKernel rootPanel;
	protected JComponent parent;
	protected NamedElement vc;
	protected Map<JComponent, NamedElement> vmMap;
	protected List<JComponent> nodes;
	protected List<Pair<JComponent, JComponent>> bindings;
	protected BVRView view;
	
	protected String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Command init(BVRUIKernel rootPanel, Object p, JComponent parent,
			Map<JComponent, NamedElement> vmMap, List<JComponent> nodes,
			List<Pair<JComponent, JComponent>> bindings, BVRView view) {
		
		//System.out.println("p: " + p);
		//System.out.println("p instanceof VSpec: " + (p instanceof VSpec));
		
		if(p instanceof NamedElement){
			this.rootPanel = rootPanel;
			this.vc = (NamedElement) p;
			this.parent = parent;
			
			this.name = vc.getName();
		}
		
		this.vmMap = vmMap;
		this.nodes = nodes;
		this.bindings = bindings;
		this.view = view;
		
		return this;
		
	}

	public JComponent execute() {
		//System.out.println("vc: " + vc);
		vc.setName(name);
		view.notifyVspecViewUpdate();
		
		return null;
	}

}