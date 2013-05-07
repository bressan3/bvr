package no.sintef.cvl.ui.editor;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import cvl.NamedElement;
import cvl.VSpec;
import no.sintef.cvl.ui.edit.CVLEditorPanel;
import no.sintef.cvl.ui.framework.elements.ConfigurableUnitPanel;
import no.sintef.cvl.ui.loader.CVLView;

public class CVLUIKernel {
	
	private CVLEditorPanel editorPanel;
	private ConfigurableUnitPanel modelPanel;
	private CVLView view;
	
	public CVLUIKernel(Map<JComponent, NamedElement> vmMap, CVLView view, List<Map<JComponent, NamedElement>> resolutionvmMaps) {
		this.view = view;
		modelPanel = new ConfigurableUnitPanel();
		editorPanel = new CVLEditorPanel(this, vmMap, view, resolutionvmMaps);
	}
	
	public CVLEditorPanel getEditorPanel() {
		return editorPanel;
	}
	public void setEditorPanel(CVLEditorPanel editorPanel) {
		this.editorPanel = editorPanel;
	}
	public ConfigurableUnitPanel getModelPanel() {
		return modelPanel;
	}


}
