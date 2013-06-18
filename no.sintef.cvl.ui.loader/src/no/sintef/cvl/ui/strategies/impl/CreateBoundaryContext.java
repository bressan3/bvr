package no.sintef.cvl.ui.strategies.impl;

import no.sintef.cvl.ui.strategies.PlacementBoundaryCalcStrategy;
import no.sintef.cvl.ui.strategies.ReplacementBoundaryCalcStrategy;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import cvl.NamedElement;
import cvl.PlacementFragment;
import cvl.ReplacementFragmentType;

public class CreateBoundaryContext {

	private PlacementBoundaryCalcStrategy defaultPlacementCalc;
	private ReplacementBoundaryCalcStrategy defaultReplacementCalc;

	public CreateBoundaryContext(){
		this.defaultPlacementCalc = new DefaultPlacementBoundaryCalcStrategy();
		this.defaultReplacementCalc = new DefaultReplacementBoundaryCalcStrategy();
	}
	
	public void creatBoundaries(NamedElement fragment, EList<EObject> selectedElements){
		PlacementFragment placement = null;
		ReplacementFragmentType replacement = null;
		if(fragment instanceof PlacementFragment){
			placement = (PlacementFragment) fragment;
			this.defaultPlacementCalc.calculateBoundaries(placement, selectedElements);
		}else if (fragment instanceof ReplacementFragmentType){
			replacement = (ReplacementFragmentType) fragment;
			this.defaultReplacementCalc.calculateBoundaries(replacement, selectedElements);
		}else{
			throw new UnsupportedOperationException("fragment should be either PlacementFragment or ReplacementFragmentType");
		}
	}
}