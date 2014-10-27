package no.sintef.bvr.tool.controller.command;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.controller.BVRNotifiableController;
import no.sintef.bvr.tool.ui.command.CommandMouseListener;
import no.sintef.bvr.tool.ui.command.SelectInstanceCommand;
import no.sintef.bvr.tool.ui.dropdown.BVRModelDropDownListener;
import no.sintef.bvr.tool.ui.editor.BVRUIKernel;
import no.sintef.bvr.tool.ui.loader.Pair;
import no.sintef.bvr.ui.framework.elements.BVRModelSymbolPanel;
import bvr.BVRModel;
import bvr.NamedElement;


public class AddBVRModel implements Command {

	private BVRUIKernel rootPanel;
	private BVRModel model;
	private CommandMouseListener listener;
	private Map<JComponent, NamedElement> vmMap;
	private List<JComponent> nodes;
	private List<Pair<JComponent, JComponent>> bindings;
	private BVRNotifiableController view;

	@Override
	public Command init(BVRUIKernel rootPanel, Object model, JComponent parent, Map<JComponent, NamedElement> vmMap, List<JComponent> nodes, List<Pair<JComponent, JComponent>> bindings, BVRNotifiableController view) {
		this.rootPanel = rootPanel;
		this.model = (BVRModel) model;
		this.vmMap = vmMap;
		this.nodes = nodes;
		this.bindings = bindings;
		this.view = view;
		return this;
	}

	@Override
	public JComponent execute() {
		BVRModelSymbolPanel cp = new BVRModelSymbolPanel(rootPanel.getModelPanel());
		nodes.add(cp);
		vmMap.put(cp, model);
		
        listener = new CommandMouseListener();
        cp.addMouseListener(new BVRModelDropDownListener(cp, vmMap, nodes, bindings, view));
        cp.addMouseListener(listener);
        
        SelectInstanceCommand command = new SelectInstanceCommand();
        command.init(rootPanel, cp, null, vmMap, nodes, bindings, view);
        listener.setLeftClickCommand(command);
		
        String name = model.getName();
        if(name == null) name = "(unnamed)";
        cp.setTitle(name);
        rootPanel.getModelPanel().addNode(cp);
        return cp;
	}
}