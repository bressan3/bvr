package no.sintef.cvl.engine.fragment.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import no.sintef.cvl.engine.common.Utility;
import no.sintef.cvl.engine.error.BasicCVLEngineException;
import no.sintef.cvl.engine.error.GeneralCVLEngineException;
import no.sintef.cvl.engine.fragment.ElementHolderOIF;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import cvl.FromPlacement;
import cvl.ObjectHandle;
import cvl.PlacementBoundaryElement;
import cvl.PlacementFragment;
import cvl.ToPlacement;

public class PlacementElementHolder extends BasicElementHolder implements ElementHolderOIF {

	protected EList<ToPlacement> tbe;
	protected EList<FromPlacement> fbe;
	protected HashSet<EObject> innerElements;
	protected HashSet<EObject> outerElements;
	private HashSet<EObject> vVertices;
	private PlacementFragment placement;
	private HashMap<FromPlacement, HashSet<EObject>> fromPlacementInsBoundaryMap;
	private HashMap<ToPlacement, HashSet<EObject>> toPlacementOutBoundaryMap;

	public PlacementElementHolder(PlacementFragment pf) throws BasicCVLEngineException {
		placement = pf;
		fromPlacementInsBoundaryMap = new HashMap<FromPlacement, HashSet<EObject>>();
		toPlacementOutBoundaryMap = new HashMap<ToPlacement, HashSet<EObject>>();
		for(PlacementBoundaryElement pbe : placement.getPlacementBoundaryElement()){
			if(pbe instanceof ToPlacement){
				ToPlacement toPlacement = (ToPlacement) pbe;
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(Utility.resolveProxies(toPlacement.getOutsideBoundaryElement()));
				toPlacementOutBoundaryMap.put(toPlacement, set);
			}
			if(pbe instanceof FromPlacement){
				FromPlacement fromPlacement = (FromPlacement) pbe;
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(Utility.resolveProxies(fromPlacement.getInsideBoundaryElement()));
				fromPlacementInsBoundaryMap.put(fromPlacement, set);
			}
		}
		this.locate();
	}
		
	public void update() throws BasicCVLEngineException{
		this.locate();
	}
	
	@Override
	protected void locate() throws BasicCVLEngineException{
		super.locate();
		vVertices = new HashSet<EObject>();
		outerElements = new HashSet<EObject>();
		innerElements = new HashSet<EObject>();
		tbe = new BasicEList<ToPlacement>();
		fbe = new BasicEList<FromPlacement>();
		
		EList<PlacementBoundaryElement> pbes = placement.getPlacementBoundaryElement();
		for(PlacementBoundaryElement pbe : pbes){
			if(pbe instanceof ToPlacement){
				tbe.add((ToPlacement)pbe);
				frBElementsInternal.addAll(Utility.resolveProxies(((ToPlacement)pbe).getInsideBoundaryElement()));
				outerElements.add(Utility.resolveProxies(((ToPlacement)pbe).getOutsideBoundaryElement()));
				innerElements.addAll(Utility.resolveProxies(((ToPlacement)pbe).getInsideBoundaryElement()));
				
				HashSet<EObject> outsideBoundaryElements = toPlacementOutBoundaryMap.get((ToPlacement)pbe);
				if(outsideBoundaryElements == null){
					throw new GeneralCVLEngineException("failed to locate given toPlacement in the map");
				}
				outerElements.addAll(outsideBoundaryElements);
			}
			if(pbe instanceof FromPlacement){
				fbe.add((FromPlacement) pbe);
				frBElementsExternal.add(Utility.resolveProxies(((FromPlacement)pbe).getInsideBoundaryElement()));
				outerElements.addAll(Utility.resolveProxies(((FromPlacement)pbe).getOutsideBoundaryElement()));
				innerElements.add(Utility.resolveProxies(((FromPlacement)pbe).getInsideBoundaryElement()));
				
				HashSet<EObject> insideBoundaryElements = fromPlacementInsBoundaryMap.get((FromPlacement) pbe);
				if(insideBoundaryElements == null){
					throw new GeneralCVLEngineException("failed to locate given fromPlacement in the map");
				}
				frBElementsExternal.addAll(insideBoundaryElements);
				innerElements.addAll(insideBoundaryElements);
			}
		}
		this.calculatePlElementsInternal();
		frElementsOriginal.addAll(frBElementsExternal);
		frElementsOriginal.addAll(frBElementsInternal);
		frElementsOriginal.addAll(frElementsInternal);
	}
	
	private void calculatePlElementsInternal(){
		for(EObject element : frBElementsInternal){
			this.traversRelation(element);
		}
	}
	
	private void traversRelation(EObject element) {
		if(!vVertices.contains(element) && !frBElementsExternal.contains(element) && !outerElements.contains(element)){
			vVertices.add(element);
			if(!frBElementsInternal.contains(element)){
				frElementsInternal.add(element);
			}
			EList<EObject> references = element.eCrossReferences();
			for(EObject ref : references){
				this.traversRelation(ref);
			}
			EList<EObject> contents = element.eContents();
			for(EObject con : contents){
				this.traversRelation(con);
			}
		}
	}
	
	@Override
	public HashSet<EObject> getInnerFragmentElements() {
		HashSet<EObject> innElements = new HashSet<EObject>();
		for(HashSet<EObject> innerEl : fromPlacementInsBoundaryMap.values()){
			innElements.addAll(innerEl);
		}
		innElements.addAll(innerElements);
		return innElements;
	}
	
	@Override
	public HashSet<EObject> getBElementsExternal() {
		HashSet<EObject> innElements = new HashSet<EObject>();
		for(HashSet<EObject> innerEl : fromPlacementInsBoundaryMap.values()){
			innElements.addAll(innerEl);
		}
		innElements.addAll(frBElementsExternal);
		return innElements;
	}

	@Override
	public HashSet<EObject> getOuterFragmentElements() {
		return outerElements;
	}
	
	public EList<ToPlacement> getToPlacementBoundaries(){
		return tbe;
	}
	
	public EList<FromPlacement> getFromPlacementBoundaries(){
		return fbe;
	}

	public PlacementFragment getPlacementFragment() {
		return placement;
	}
	
	public HashMap<FromPlacement, HashSet<EObject>> getFromPlacementInsBoundaryMap(){
		return this.fromPlacementInsBoundaryMap;
	}
	
	public HashMap<ToPlacement,HashSet<EObject>> getToPlacementOutBoundaryMap(){
		return this.toPlacementOutBoundaryMap;
	}
	
	public void setFromPlacementInsBoundaryMap(HashMap<FromPlacement, HashSet<ObjectHandle>> fromPlacementInsBoundaryMap){
		HashMap<FromPlacement, HashSet<EObject>> map = new HashMap<FromPlacement, HashSet<EObject>>();
		for(Map.Entry<FromPlacement, HashSet<ObjectHandle>> entry : fromPlacementInsBoundaryMap.entrySet()){
			FromPlacement key = entry.getKey();
			HashSet<ObjectHandle> value = entry.getValue();
			map.put(key, new HashSet<EObject>(Utility.resolveProxies(new BasicEList<ObjectHandle>(value))));
		}
		this.fromPlacementInsBoundaryMap = map;
	}

	public void setToPlacementOutBoundaryMap(HashMap<ToPlacement, HashSet<ObjectHandle>> toPlacementOHOutsideBoundaryMap) {
		HashMap<ToPlacement, HashSet<EObject>> map = new HashMap<ToPlacement, HashSet<EObject>>();
		for(Map.Entry<ToPlacement, HashSet<ObjectHandle>> entry : toPlacementOHOutsideBoundaryMap.entrySet()){
			ToPlacement key = entry.getKey();
			HashSet<ObjectHandle> value = entry.getValue();
			map.put(key, new HashSet<EObject>(Utility.resolveProxies(new BasicEList<ObjectHandle>(value))));
		}
		this.toPlacementOutBoundaryMap = map;
	}
}
