package no.sintef.bvr.tool.strategy.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;





import bvr.ChoiceResolution;
import bvr.FragmentSubstitution;
import no.sintef.bvr.common.CommonUtility;
import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.primitive.SymbolVSpec;
import no.sintef.bvr.tool.primitive.SymbolVSpecResolutionTable;
import no.sintef.bvr.tool.strategy.RealizationStrategy;

public class RealizationStrategyBottomUp implements RealizationStrategy {
	

	@Override
	public void deriveProduct(SymbolVSpecResolutionTable table) {
		EList<FragmentSubstitution> fragmentSubstitutions = new BasicEList<FragmentSubstitution>(getFragmentSubstitutionsToResolve(table));
		Context.eINSTANCE.initSubEngine(fragmentSubstitutions);
		resolve(table);
	}
	
	private HashSet<FragmentSubstitution> getFragmentSubstitutionsToResolve(SymbolVSpecResolutionTable table){
		HashSet<FragmentSubstitution> fss = new HashSet<FragmentSubstitution>();
		ArrayList<SymbolVSpec> symbols = table.getSymbols();
		for(SymbolVSpec symbol : symbols){
			EList<FragmentSubstitution> fragmentSubstitutions = symbol.getFragmentSubstitutions();
			for(FragmentSubstitution fragment : fragmentSubstitutions){
				FragmentSubstitution fragmentCopy = symbol.getFragmentSubstitutionCopy(fragment);
				FragmentSubstitution fragmentToExecute = (fragmentCopy == null) ? fragment : fragmentCopy;
				symbol.addFragmentSubstitutionsToExecute(fragmentToExecute);
				fss.add(fragmentToExecute);
			}
		}
		ArrayList<SymbolVSpecResolutionTable> tables = table.getChildren();
		for(SymbolVSpecResolutionTable symbolTable : tables){
			fss.addAll(getFragmentSubstitutionsToResolve(symbolTable));
		}
		return fss;
	}
	
	private void resolve(SymbolVSpecResolutionTable table){
		ArrayList<SymbolVSpecResolutionTable> children = table.getChildren();
		for(SymbolVSpecResolutionTable child : children){
			resolve(child);
		}
		Context.eINSTANCE.performSubstitutions(prioritizeSymbols(table.getSymbols()));
	}
	
	private ArrayList<SymbolVSpec> prioritizeSymbols(ArrayList<SymbolVSpec> symbols){
		ArrayList<SymbolVSpec> prioritizedSymbols = new ArrayList<SymbolVSpec>();
		Iterator<SymbolVSpec> iterator = symbols.iterator();
		while(iterator.hasNext()){
			SymbolVSpec symbol = iterator.next();
			if(CommonUtility.isVSpecResolutionVClassifier(symbol.getVSpecResolution())){
				prioritizedSymbols.add(prioritizedSymbols.size(), symbol);
			}else{
				prioritizedSymbols.add(0, symbol);
			}
		}
		return prioritizedSymbols;
	}
}
