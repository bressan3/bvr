package no.sintef.bvr.tool.strategy.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import bvr.ChoiceResolutuion;
import bvr.ConfigurableUnit;
import bvr.FragmentSubstitution;
import bvr.VInstance;
import bvr.VSpecResolution;
import bvr.VariationPoint;
import no.sintef.bvr.tool.primitive.Symbol;
import no.sintef.bvr.tool.primitive.SymbolTable;
import no.sintef.bvr.tool.primitive.impl.VSpecResolutionSymbol;
import no.sintef.bvr.tool.primitive.impl.VSpecResolutionSymbolTable;
import no.sintef.bvr.tool.strategy.TableBuilderStrategy;

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
		parse(symbol, table);
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
			boolean isDecision = parse(childSymbol, table);
			if(isDecision)
				symbol.addChild(childSymbol);
		}
		return true;
	}

}