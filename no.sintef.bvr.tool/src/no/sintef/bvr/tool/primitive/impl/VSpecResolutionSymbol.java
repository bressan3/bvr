/*******************************************************************************
 * Copyright (c)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.primitive.impl;

import java.util.HashMap;

import no.sintef.bvr.common.CommonUtility;
import no.sintef.bvr.tool.exception.UnimplementedBVRException;
import no.sintef.bvr.tool.primitive.AbstractSymbol;
import no.sintef.bvr.tool.primitive.SymbolVSpec;

import org.eclipse.emf.common.util.BasicEList;

import bvr.ChoiceResolution;
import bvr.FragmentSubstitution;
import bvr.VSpecResolution;


public class VSpecResolutionSymbol extends AbstractSymbol {

	public VSpecResolutionSymbol(VSpecResolution vSpecRes){
		multi = false;
		vSpecResolution = vSpecRes;
		children = new BasicEList<SymbolVSpec>();
		vSpec = CommonUtility.getResolvedVSpec(vSpecRes);
		fragSubs = new BasicEList<FragmentSubstitution>();
		fragSubsToExecute = new BasicEList<FragmentSubstitution>();
		fragmentSubCopyMap = new HashMap<FragmentSubstitution, FragmentSubstitution>();
		if(vSpecRes instanceof ChoiceResolution){
			if(vSpec == null)
				throw new UnimplementedBVRException("Can not find a resolved VSpec for " + vSpecRes);
		}else {
			throw new UnimplementedBVRException("Can not create a symble from somthing other than ChoiceResolution " + vSpecRes);
		}
	}
}
