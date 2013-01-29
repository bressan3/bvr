package no.sintef.cvl.engine.converters.rls;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import no.sintef.cvl.engine.converters.common.CVLModelNew;
import no.sintef.cvl.engine.converters.common.CVLModelOld;
import no.sintef.cvl.engine.converters.common.PlacementElementHolder;
import no.sintef.cvl.engine.converters.common.ReplacementElementHolder;
import no.sintef.dsl.node.nodePackage;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.ExternalCrossReferencer;
import org.eclipse.emf.ecore.util.EcoreUtil.UsageCrossReferencer;
import org.eclipse.uml2.uml.UMLPackage;

import org.variabilitymodeling.cvl.BoundaryElementBinding;
import org.variabilitymodeling.cvl.CVLModel;
import org.variabilitymodeling.cvl.CompositeVariability;
import org.variabilitymodeling.cvl.PlacementBoundaryElement;
import org.variabilitymodeling.cvl.ReplacementBoundaryElement;
import org.variabilitymodeling.cvl.ReplacementFragment;

import cvl.Choice;
import cvl.ConfigurableUnit;
import cvl.FromPlacement;
import cvl.ObjectHandle;
import cvl.OpaqueVariationPoint;
import cvl.PlacementFragment;
import cvl.ReplacementFragmentType;
import cvl.ToReplacement;
import cvl.VClassifier;
import cvl.VSpec;
import cvl.cvlFactory;
import cvl.ToPlacement;
import cvl.FromReplacement;
import cvl.FragmentSubstitution;
import cvl.ToBinding;
import cvl.FromBinding;

public class OldCvlToNewCvl {

	private File oldcvlfile;
	private File newcvlfile;
	private cvlFactory factory = cvlFactory.eINSTANCE;
	private ConfigurableUnit cu;
	private HashMap<org.variabilitymodeling.cvl.PlacementFragment, cvl.PlacementFragment> plMap = new HashMap<org.variabilitymodeling.cvl.PlacementFragment, cvl.PlacementFragment>();
	private HashMap<org.variabilitymodeling.cvl.ReplacementFragment, cvl.ReplacementFragmentType> rplMap = new HashMap<org.variabilitymodeling.cvl.ReplacementFragment, cvl.ReplacementFragmentType>();
	private HashMap<CompositeVariability, VSpec> compositeVSpecMap = new HashMap<CompositeVariability, VSpec>();
	private HashMap<FragmentSubstitution, HashMap<org.variabilitymodeling.cvl.PlacementBoundaryElement, cvl.PlacementBoundaryElement>> placBoundMap = new HashMap<FragmentSubstitution, HashMap<org.variabilitymodeling.cvl.PlacementBoundaryElement, cvl.PlacementBoundaryElement>>();
	private HashMap<FragmentSubstitution, HashMap<org.variabilitymodeling.cvl.ReplacementBoundaryElement, cvl.ReplacementBoundaryElement>> replBoundMap = new HashMap<FragmentSubstitution ,HashMap<org.variabilitymodeling.cvl.ReplacementBoundaryElement, cvl.ReplacementBoundaryElement>>();
	private ResourceSet resSet;
	private EList<ObjectHandle> objectHandleList = new BasicEList<ObjectHandle>();
	
	public OldCvlToNewCvl(File oldcvl, File newcvl){
		this.oldcvlfile = oldcvl;
		this.newcvlfile = newcvl;
		String path = this.oldcvlfile.getAbsolutePath();
		path = path.replaceAll(oldcvl.getName(), "");
		System.setProperty( "user.dir", path);
		this.resSet = new ResourceSetImpl();
	}

	public void run() throws Exception{
		try {
			this.newcvlfile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//UMLPackage.eINSTANCE.eClass();
		//nodePackage.eINSTANCE.eClass();
		
		CVLModelNew cvn = new CVLModelNew(this.resSet);
		this.cu = cvn.creat();
		
		CVLModelOld cvo = new CVLModelOld(this.resSet);
		CVLModel cv = cvo.load(this.oldcvlfile);
		
		this.parseOldTree(cv, cu);
				
		cvn.writeToFile(this.newcvlfile, cu);
		System.out.println("DONE!!!");
	}
	
	public void run1(){
		CVLModelNew cvn = new CVLModelNew(this.resSet);
		this.cu = cvn.load(newcvlfile);
		System.out.println(this.cu);
	}
	
	private void parseOldTree(EObject node, EObject newnode) throws Exception{
		if(node instanceof CVLModel){
			((ConfigurableUnit) newnode).setName(((CVLModel) node).getName());
		}
		for(EObject child : node.eContents()){
			if(child instanceof CompositeVariability){
				VSpec vspec = factory.createChoice();
				vspec.setName(((CompositeVariability) child).getName());
				this.compositeVSpecMap.put((CompositeVariability) child, vspec);
				if(newnode instanceof ConfigurableUnit){
					((ConfigurableUnit) newnode).getOwnedVSpec().add(vspec);
				}
				if(newnode instanceof VSpec){
					((VSpec) newnode).getChild().add(vspec);
				}
				this.parseOldTree(child, vspec);
			}
			if(child instanceof org.variabilitymodeling.cvl.FragmentSubstitution){
				org.variabilitymodeling.cvl.FragmentSubstitution fso = (org.variabilitymodeling.cvl.FragmentSubstitution) child;
				FragmentSubstitution fsn = factory.createFragmentSubstitution();
				fsn.setName(fso.getName());
				PlacementFragment pf = this.createPlacementFragment(fso.getPlacement(), fsn);
				ReplacementFragmentType rft = this.createReplacementFragment(fso.getReplacement(), fsn);
				
				fsn.setPlacement(pf);
				fsn.setReplacement(rft);				
				this.bindFragmentSubstitution(fsn, newnode);
				
				EList<BoundaryElementBinding> obs = fso.getBoundaryElementBinding();
				for (BoundaryElementBinding ob: obs){
					if(ob instanceof org.variabilitymodeling.cvl.ToBinding){
						org.variabilitymodeling.cvl.ToBinding tob = (org.variabilitymodeling.cvl.ToBinding) ob;
						ToBinding tnb = factory.createToBinding();
						HashMap<ToPlacement, ToReplacement> pr = this.getToPlacementReplacement(tob, fsn);
						tnb.setToPlacement(pr.keySet().iterator().next());
						tnb.setToReplacement(pr.values().iterator().next());
						fsn.getBoundaryElementBinding().add(tnb);
						pr.keySet().iterator().next().setToReplacement(pr.values().iterator().next());
						pr.values().iterator().next().setToPlacement(pr.keySet().iterator().next());
					}
					if(ob instanceof org.variabilitymodeling.cvl.FromBinding){
						org.variabilitymodeling.cvl.FromBinding fob = (org.variabilitymodeling.cvl.FromBinding) ob;
						FromBinding fnb = factory.createFromBinding();
						HashMap<FromPlacement, FromReplacement> pr = this.getFromPlacementReplacement(fob, fsn);
						fnb.setFromPlacement(pr.keySet().iterator().next());
						fnb.setFromReplacement(pr.values().iterator().next());
						fsn.getBoundaryElementBinding().add(fnb);
						pr.keySet().iterator().next().setFromReplacement(pr.values().iterator().next());
						pr.values().iterator().next().setFromPlacement(pr.keySet().iterator().next());
					}
				}
				
				this.cu.getOwnedVariationPoint().add(fsn);
			}
		}
	}
	
	private PlacementFragment createPlacementFragment(org.variabilitymodeling.cvl.PlacementFragment opf, FragmentSubstitution fsn){
		PlacementFragment pf = factory.createPlacementFragment();
		pf.setName(opf.getName());
		this.plMap.put(opf, pf);
		PlacementElementHolder pef = new PlacementElementHolder(opf);
		HashMap<org.variabilitymodeling.cvl.FromPlacement, EObject> mapFromPlacementInside = pef.getInsideBoundaryMapForPlacement();
				
		HashMap<org.variabilitymodeling.cvl.PlacementBoundaryElement, cvl.PlacementBoundaryElement> map = new HashMap<org.variabilitymodeling.cvl.PlacementBoundaryElement, cvl.PlacementBoundaryElement>();
		EList<PlacementBoundaryElement> opbes = opf.getBoundaryElement();
		for(PlacementBoundaryElement element : opbes){
			if(element instanceof org.variabilitymodeling.cvl.ToPlacement){
				org.variabilitymodeling.cvl.ToPlacement tpbe = (org.variabilitymodeling.cvl.ToPlacement) element;
				ToPlacement tpbeNew = factory.createToPlacement();
				map.put(tpbe, tpbeNew);
				
				tpbeNew.setPropertyName(tpbe.getPropertyName());
				pf.getPlacementBoundaryElement().add(tpbeNew);
				
				EList<EObject> ibeos = tpbe.getInsideBoundaryElement();
				for(EObject ibeo : ibeos){
					ObjectHandle obh = this.getObjectHandleInFS(ibeo, fsn);
					tpbeNew.getInsideBoundaryElement().add(obh);
				}
				
				EObject obeo = tpbe.getOutsideBoundaryElement();
				ObjectHandle obh = this.getObjectHandleInFS(obeo, fsn);
				tpbeNew.setOutsideBoundaryElement(obh);
			}
			
			if(element instanceof org.variabilitymodeling.cvl.FromPlacement){
				org.variabilitymodeling.cvl.FromPlacement fpbe = (org.variabilitymodeling.cvl.FromPlacement) element;
				FromPlacement fpbeNew = factory.createFromPlacement();
				map.put(fpbe, fpbeNew);
				
				pf.getPlacementBoundaryElement().add(fpbeNew);
										
				EList<EObject> obeos = fpbe.getOutsideBoundaryElement();
				for(EObject obeo : obeos){
					ObjectHandle obh = this.getObjectHandleInFS(obeo, fsn);
					fpbeNew.getOutsideBoundaryElement().add(obh);
				}
				
				EObject insideBoundaryElementOld = mapFromPlacementInside.get(fpbe);
				ObjectHandle obh = this.getObjectHandleInFS(insideBoundaryElementOld, fsn);
				fpbeNew.setInsideBoundaryElement(obh);
			}
		}
		this.placBoundMap.put(fsn, map);
		this.cu.getOwnedVariationPoint().add(pf);
		return pf;
	}
	
	private ReplacementFragmentType createReplacementFragment(org.variabilitymodeling.cvl.ReplacementFragmentAbstract orfa, FragmentSubstitution fsn){
		ReplacementFragment orf = (org.variabilitymodeling.cvl.ReplacementFragment) orfa;
		ReplacementFragmentType rft = factory.createReplacementFragmentType();
		rft.setName(orf.getName());
		this.rplMap.put(orf, rft);
		
		ReplacementElementHolder reh = new ReplacementElementHolder(orf);
		HashMap<org.variabilitymodeling.cvl.ToReplacement, EObject> mapToReplacementOutside = reh.getOutsideBoundaryElementMap();
		
		HashMap<org.variabilitymodeling.cvl.ReplacementBoundaryElement, cvl.ReplacementBoundaryElement> map = new  HashMap<org.variabilitymodeling.cvl.ReplacementBoundaryElement, cvl.ReplacementBoundaryElement>();
		EList<ReplacementBoundaryElement> orbes = orf.getBoundaryElement();
		for(ReplacementBoundaryElement element : orbes){
			if(element instanceof org.variabilitymodeling.cvl.FromReplacement){
				org.variabilitymodeling.cvl.FromReplacement frbe = (org.variabilitymodeling.cvl.FromReplacement) element;
				FromReplacement frbeNew = factory.createFromReplacement();
				map.put(frbe, frbeNew);
				
				frbeNew.setPropertyName(frbe.getPropertyName());
				rft.getReplacementBoundaryElement().add(frbeNew);
				
				EList<EObject> obeos = frbe.getOutsideBoundaryElement();
				for(EObject obeo : obeos){
					ObjectHandle obh = this.getObjectHandleInFS(obeo, fsn);
					frbeNew.getOutsideBoundaryElement().add(obh);
				}
				
				EObject ibeo = frbe.getInsideBoundaryElement();
				ObjectHandle obh = this.getObjectHandleInFS(ibeo, fsn);
				frbeNew.setInsideBoundaryElement(obh);
			}
			if(element instanceof org.variabilitymodeling.cvl.ToReplacement){
				org.variabilitymodeling.cvl.ToReplacement trbe = (org.variabilitymodeling.cvl.ToReplacement) element;
				ToReplacement trbeNew = factory.createToReplacement();
				map.put(trbe, trbeNew);
				
				rft.getReplacementBoundaryElement().add(trbeNew);
				
				EList<EObject> ibeos = trbe.getInsideBoundaryElement();
				for(EObject ibeo : ibeos){
					ObjectHandle obh = this.getObjectHandleInFS(ibeo, fsn);
					trbeNew.getInsideBoundaryElement().add(obh);
				}
				
				EObject outsideBoundaryElement = mapToReplacementOutside.get(trbe);
				ObjectHandle obh = this.getObjectHandleInFS(outsideBoundaryElement, fsn);
				trbeNew.setOutsideBoundaryElement(obh);
			}
		}
		this.replBoundMap.put(fsn, map);
		this.cu.getOwnedVariabletype().add(rft);
		return rft;
	}
	
	private ObjectHandle getObjectHandleInFS(EObject eObject, FragmentSubstitution fs){
		EList<ObjectHandle> objectHandles = fs.getSourceObject();
		for(ObjectHandle objectHanldle : objectHandles){
			EObject resolved = objectHanldle.getMOFRef();
			if((resolved != null && resolved.equals(eObject)) || eObject == resolved){
				return objectHanldle;
			}
		}
		ObjectHandle objectHandle = factory.createObjectHandle();
		objectHandle.setMOFRef(eObject);
		fs.getSourceObject().add(objectHandle);
		return objectHandle;
	}
	
	private FromPlacement addDummyBoundariesForPF(FragmentSubstitution fsn){
		PlacementFragment placFrag = fsn.getPlacement();
		FromPlacement fromPlacement = factory.createFromPlacement();
		ObjectHandle oh = this.getObjectHandleInFS(null, fsn);
		fromPlacement.getOutsideBoundaryElement().add(oh);
		fromPlacement.setInsideBoundaryElement(oh);
		placFrag.getPlacementBoundaryElement().add(fromPlacement);
		//HashMap<PlacementBoundaryElement, cvl.PlacementBoundaryElement> map = this.placBoundMap.get(fsn);
		//map.put(null, fromPlacement);
		//this.placBoundMap.put(fsn, map);
		return fromPlacement;
	}
	
	private ToReplacement addDummyBoundariesForRF(FragmentSubstitution fsn){
		ReplacementFragmentType replacFrag = fsn.getReplacement();
		ToReplacement toReplacment = factory.createToReplacement();
		ObjectHandle oh = this.getObjectHandleInFS(null, fsn);
		toReplacment.setOutsideBoundaryElement(oh);
		toReplacment.getInsideBoundaryElement().add(oh);
		replacFrag.getReplacementBoundaryElement().add(toReplacment);
		//HashMap<ReplacementBoundaryElement, cvl.ReplacementBoundaryElement> map = this.replBoundMap.get(fsn);
		//map.put(null, toReplacment);
		//this.replBoundMap.put(fsn, map);
		return toReplacment;
	}
	
	
	private HashMap<FromPlacement, FromReplacement> getFromPlacementReplacement(org.variabilitymodeling.cvl.FromBinding fob, FragmentSubstitution fsn) throws Exception {
		cvl.PlacementBoundaryElement nfp = this.placBoundMap.get(fsn).get(fob.getFromPlacement());
		cvl.ReplacementBoundaryElement nfr = this.replBoundMap.get(fsn).get(fob.getFromReplacement());
		nfp = (nfp == null) ? this.addDummyBoundariesForPF(fsn) : nfp;
		if(nfp == null){
			throw new Exception("can not find FromPlacement matching " + fob.getFromPlacement() + " in " + fob);
		}
		if(nfr == null){
			throw new Exception("can not find FromReplacement matching " + fob.getFromReplacement() + " in " + fob);
		}
		HashMap<FromPlacement, FromReplacement> val = new HashMap<FromPlacement, FromReplacement>();
		val.put((FromPlacement) nfp, (FromReplacement) nfr);
		return val;
	}

	private HashMap<ToPlacement, ToReplacement> getToPlacementReplacement(org.variabilitymodeling.cvl.ToBinding tb, FragmentSubstitution fsn) throws Exception{
		cvl.PlacementBoundaryElement ntp = this.placBoundMap.get(fsn).get(tb.getToPlacement());
		cvl.ReplacementBoundaryElement ntr = this.replBoundMap.get(fsn).get(tb.getToReplacement());
		ntr = (ntr == null) ? this.addDummyBoundariesForRF(fsn) : ntr;
		if(ntp == null){
			throw new Exception("can not find ToPlacement matching " + tb.getToPlacement() + " in " + tb);
		}
		if(ntr == null){
			throw new Exception("can not find ToReplacement matching " + tb.getToReplacement() + " in " + tb);
		}
		HashMap<ToPlacement, ToReplacement> val = new HashMap<ToPlacement, ToReplacement>();
		val.put((ToPlacement) ntp, (ToReplacement) ntr);
		return val;
	}
	
	private void bindFragmentSubstitution(FragmentSubstitution fs, EObject vspec) throws Exception{
		if(vspec instanceof VClassifier){
			fs.setBindingClassifier((VClassifier)vspec);
		}else if(vspec instanceof Choice){
			fs.setBindingChoice((Choice) vspec);
		}else if(vspec instanceof VSpec){
			fs.setBindingVSpec((VSpec) vspec);
		}else{
			throw new Exception("vspec is of the wrong type" + vspec);
		}
	}
}
