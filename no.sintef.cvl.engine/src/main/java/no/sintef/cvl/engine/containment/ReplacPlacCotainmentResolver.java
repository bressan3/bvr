package no.sintef.cvl.engine.containment;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Sets;

import cvl.FromPlacement;
import cvl.FromReplacement;
import cvl.ObjectHandle;
import cvl.PlacementFragment;
import cvl.ReplacementFragmentType;
import cvl.ToPlacement;
import cvl.ToReplacement;
import no.sintef.cvl.engine.common.Utility;
import no.sintef.cvl.engine.fragment.impl.FragmentSubstitutionHolder;


public class ReplacPlacCotainmentResolver {

	private ReplacPlacCotainmentFinder finder;

	public ReplacPlacCotainmentResolver(ReplacPlacCotainmentFinder intersectionFinder){
		finder = intersectionFinder;
	}
	
	public void resolve(FragmentSubstitutionHolder fragment){
		adjustBoundaries(fragment);
	}
	
	private void adjustBoundaries(FragmentSubstitutionHolder fragmentHolder){
		PlacementFragment placement = fragmentHolder.getPlacement().getPlacementFragment();
		HashMap<ReplacementFragmentType, HashMap<ToPlacement, HashSet<ToReplacement>>> replacementToBoundary = finder.getAdjacentToBoundaries().get(placement);
		if(replacementToBoundary != null){
			HashSet<EObject> placementStaleElements = finder.getPlacementStaleElements().get(placement);
			EList<EObject> invalidPlacementElements = new BasicEList<EObject>(Sets.difference(placementStaleElements, fragmentHolder.getPlacement().getElements()));
			for(Map.Entry<ReplacementFragmentType, HashMap<ToPlacement, HashSet<ToReplacement>>> entry : replacementToBoundary.entrySet()){
				HashMap<ToPlacement, HashSet<ToReplacement>> boundarysMap = entry.getValue();
				for(Map.Entry<ToPlacement, HashSet<ToReplacement>> boundaryMap : boundarysMap.entrySet()){
					ToPlacement toPlacement = boundaryMap.getKey();
					HashSet<ToReplacement> toReplacements = boundaryMap.getValue();
					for(ToReplacement toReplacement : toReplacements){
						removeInvalidReferences(toReplacement, invalidPlacementElements);
						adjustInsideBoundaryRefereneces(toReplacement, toPlacement, placement);
					}
				}
			}
		}
		
		HashMap<ReplacementFragmentType, HashMap<FromPlacement, HashSet<FromReplacement>>> replacementFromBoundary = finder.getAdjacentFromBoundaries().get(placement);
		if(replacementFromBoundary != null){
			for(Map.Entry<ReplacementFragmentType, HashMap<FromPlacement, HashSet<FromReplacement>>> entry : replacementFromBoundary.entrySet()){
				HashMap<FromPlacement, HashSet<FromReplacement>> boundarysMap = entry.getValue();
				for(Map.Entry<FromPlacement, HashSet<FromReplacement>> boundaryMap : boundarysMap.entrySet()){
					FromPlacement fromPlacement = boundaryMap.getKey();
					HashSet<FromReplacement> fromReplacements = boundaryMap.getValue();
					EObject insideElementPlac = fromPlacement.getInsideBoundaryElement().getMOFRef();
					for(FromReplacement fromReplacement : fromReplacements){
						EObject insideElementRepl = fromReplacement.getInsideBoundaryElement().getMOFRef();
						if(!insideElementPlac.equals(insideElementRepl)){
							ObjectHandle objectHandle = no.sintef.cvl.common.CommonUtility.testObjectHandle(entry.getKey(), insideElementPlac);
							fromReplacement.setInsideBoundaryElement(objectHandle);
						}
					}
				}
			}
		}
	}
	
	
	private void adjustInsideBoundaryRefereneces(ToReplacement toReplacement, ToPlacement toPlacement, PlacementFragment placement){
		EList<EObject> insideEObjectsRepl = Utility.resolveProxies(toReplacement.getInsideBoundaryElement());
		EList<EObject> insideEObjectsPlc = Utility.resolveProxies(toPlacement.getInsideBoundaryElement());
		for(EObject eObject : insideEObjectsPlc){
			if(insideEObjectsRepl.indexOf(eObject) < 0){
				ObjectHandle objectHandle = no.sintef.cvl.common.CommonUtility.testObjectHandle(placement, eObject);
				toReplacement.getInsideBoundaryElement().add(objectHandle);
			}
		}
	}
	
	private void removeInvalidReferences(ToReplacement toReplacement, EList<EObject> invalidEObjects){
		if(invalidEObjects.isEmpty())
			return;
		EList<ObjectHandle> objectHandles = toReplacement.getInsideBoundaryElement();
		Iterator<ObjectHandle> iterator = objectHandles.iterator();
		while(iterator.hasNext()){
			ObjectHandle objectHandle = iterator.next();
			EObject eObject = objectHandle.getMOFRef();
			if(invalidEObjects.indexOf(eObject) >= 0){
				iterator.remove();
			}
		}
	}
}