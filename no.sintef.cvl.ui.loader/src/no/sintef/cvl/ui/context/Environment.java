package no.sintef.cvl.ui.context;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import no.sintef.cvl.ui.loader.CVLModel;

public interface Environment {
	
	public CVLModel loadModelFromFile(File file);
	
	public void writeModelToFile(CVLModel model, File file);
	
	public void reloadModel(CVLModel model);
	
	public EObject getEObject(Object object);
	
	public List<Object> getSelections();
	
	public void highlightObjects(EList<HashMap<EObject, Integer>> objectsToHighlightList);
	
	public void clearHighlights();
	
	public JFileChooser getFileChooser();
	
}