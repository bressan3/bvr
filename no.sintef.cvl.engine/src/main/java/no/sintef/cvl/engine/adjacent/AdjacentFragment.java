package no.sintef.cvl.engine.adjacent;

import java.util.HashMap;

import org.eclipse.emf.common.util.EList;

import cvl.FromBinding;
import cvl.ToBinding;

import no.sintef.cvl.engine.fragment.FragSubHolder;

public interface AdjacentFragment {

	public FragSubHolder getFragmentHolder();
	
	public EList<AdjacentFragment> getAdjacentFragments();
	
	public void setAdjacentFragment(AdjacentFragment adjacentFragment);
	
	public HashMap<FromBinding, ToBinding> getAdjacentFromBindings(AdjacentFragment adjacentFragment);
	
	public void setAdjacentFromBindings(AdjacentFragment adjacentFragment, HashMap<FromBinding, ToBinding> adjacentBindings);
	
	public HashMap<ToBinding, FromBinding> getAdjacentToBindings(AdjacentFragment adjacentFragment);
	
	public void setAdjacentToBindings(AdjacentFragment adjacentFragment, HashMap<ToBinding, FromBinding> adjacentBindings);
	
	public HashMap<AdjacentFragment, HashMap<FromBinding, ToBinding>> getAllAdjacentFromBindings();
	
	public HashMap<AdjacentFragment, HashMap<ToBinding, FromBinding>> getAllAdjacentToBindings();
	
}