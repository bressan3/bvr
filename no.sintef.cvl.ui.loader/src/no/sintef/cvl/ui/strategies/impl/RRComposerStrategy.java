package no.sintef.cvl.ui.strategies.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cvl.BoundaryElementBinding;
import cvl.ChoiceResolutuion;
import cvl.ConfigurableUnit;
import cvl.FragmentSubstitution;
import cvl.FromBinding;
import cvl.FromReplacement;
import cvl.ToBinding;
import cvl.ToPlacement;
import cvl.ToReplacement;
import cvl.VInstance;
import cvl.VSpecResolution;
import cvl.VariationPoint;
import no.sintef.cvl.ui.primitives.Symbol;
import no.sintef.cvl.ui.primitives.SymbolTable;
import no.sintef.cvl.ui.primitives.impl.VSpecResolutionSymbol;
import no.sintef.cvl.ui.primitives.impl.VSpecResolutionSymbolTable;
import no.sintef.cvl.ui.strategies.TableBuilderStrategy;

public class RRComposerStrategy implements TableBuilderStrategy {

	private EList<VariationPoint> vps;
	private BasicEList<FragmentSubstitution> fss;

	public SymbolTable buildSymbolTable(ConfigurableUnit cu, VSpecResolution vSpecResolution) {
		vps = cu.getOwnedVariationPoint();
		fss = new BasicEList<FragmentSubstitution>();
		for(VariationPoint vp : vps){
			if(vp instanceof FragmentSubstitution){
				fss.add((FragmentSubstitution) vp);
			}
		}
		
		VSpecResolutionSymbolTable table = new VSpecResolutionSymbolTable(vSpecResolution);
		table.setConfigurableUnit(cu);
		VSpecResolutionSymbol symbol = new VSpecResolutionSymbol(vSpecResolution);
		this.parse(symbol, table);
		return table;
	}

	private boolean parse(Symbol symbol, SymbolTable table) {
		VSpecResolution vSpecResolution = symbol.getVSpecResolution();
		if(vSpecResolution instanceof ChoiceResolutuion){
			if(!((ChoiceResolutuion) vSpecResolution).isDecision()){
				return false;
			}
		}else if(vSpecResolution instanceof VInstance){
			VSpecResolutionSymbolTable subTable = new VSpecResolutionSymbolTable(vSpecResolution);
			subTable.setParent(table);
			subTable.setConfigurableUnit(table.getConfigurableUnit());
			table.setChild(subTable);
			table = subTable;
		}else{
			throw new UnsupportedOperationException("we can not handle VSpecResolution of this kind");
		}
		
		for(FragmentSubstitution fs : fss){
			if(fs.getBindingVSpec() != null && fs.getBindingVSpec().equals(symbol.getVSpec())){
				symbol.addFragmentSubstitution(fs);
			}
		}
		
		symbol.setScope(table);
		table.insert(symbol);
		
		EList<VSpecResolution> children = vSpecResolution.getChild();
		for(VSpecResolution child : children){
			VSpecResolutionSymbol childSymbol = new VSpecResolutionSymbol(child);
			childSymbol.setParent(symbol);
			boolean isDecision = this.parse(childSymbol, table);
			if(isDecision)
				symbol.addChild(childSymbol);
		}
		return true;
	}

}
