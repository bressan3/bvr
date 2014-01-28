package no.sintef.cvl.tool.strategy;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import no.sintef.cvl.tool.exception.AbstractError;
import cvl.NamedElement;
import cvl.VariationPoint;

public abstract class BoundaryCalculatorStrategy {

	public abstract ArrayList<NamedElement> getCompliedBoundaries(NamedElement forBoundary, ArrayList<NamedElement> options) throws AbstractError;
	
	protected boolean isInstance(EClassifier eClassifier, EList<EObject> eObjectList){
		for(EObject eObject : eObjectList){
			if(!eClassifier.isInstance(eObject)){
				return false;
			}
		}
		return true;
	}
	
	protected boolean isInstance(EClassifier eClassifier, EObject eObject){
		return eClassifier.isInstance(eObject);
	}
}