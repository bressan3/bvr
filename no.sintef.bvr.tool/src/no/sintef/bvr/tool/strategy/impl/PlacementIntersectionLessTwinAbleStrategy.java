/*******************************************************************************
 * Copyright (c) All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.strategy.impl;

import java.util.ArrayList;
import java.util.HashSet;

import no.sintef.bvr.engine.interfaces.fragment.IPlacementElementHolder;
import no.sintef.bvr.tool.common.LoaderUtility;
import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.primitive.SymbolVSpecResolutionTable;
import no.sintef.bvr.tool.strategy.PlacementIntersectionResolverStrategy;

import org.eclipse.emf.ecore.EObject;

import bvr.FragmentSubstitution;
import bvr.PlacementFragment;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class PlacementIntersectionLessTwinAbleStrategy implements PlacementIntersectionResolverStrategy {

	@Override
	public void resolveIntersection(SymbolVSpecResolutionTable table) {
		HashSet<FragmentSubstitution> fragmentSubstitutions = LoaderUtility.collectFragmentSubstitutionsInTable(table);
		ArrayList<ArrayList<Object>> intersections = calculatePlacementIntersections(fragmentSubstitutions);
		if (!intersections.isEmpty()) {
			String message = new String();
			for (ArrayList<Object> intersection : intersections) {
				message += "the placement '" + ((PlacementFragment) intersection.get(0)).getName() + "' intersects the placement '"
						+ ((PlacementFragment) intersection.get(1)).getName() + "' on the elements :" + intersection.get(2) + "\n";
			}
			throw new UnsupportedOperationException("can not handle a placements intersection:\n" + message);
		}
	}

	private ArrayList<ArrayList<Object>> calculatePlacementIntersections(HashSet<FragmentSubstitution> fragments) {
		HashSet<PlacementFragment> placements = new HashSet<PlacementFragment>();
		for (FragmentSubstitution fragment : fragments)
			placements.add(fragment.getPlacement());

		ArrayList<ArrayList<Object>> intersections = new ArrayList<ArrayList<Object>>();
		if (placements.toArray().length <= 1)
			return intersections;

		ArrayList<PlacementFragment> arrayPlacements = new ArrayList<PlacementFragment>(placements);
		for (int i = 0; i < arrayPlacements.size() - 1; i++) {
			for (int j = i + 1; j <= arrayPlacements.size() - 1; j++) {
				IPlacementElementHolder placement0 = Context.eINSTANCE.getSubEngine().createPlacementElementHolder(arrayPlacements.get(i));
				IPlacementElementHolder placement1 = Context.eINSTANCE.getSubEngine().createPlacementElementHolder(arrayPlacements.get(j));
				SetView<EObject> intersection = Sets.intersection(placement0.getElements(), placement1.getElements());
				if (!intersection.isEmpty()) {
					SetView<EObject> symetricDifference = Sets.symmetricDifference(placement0.getElements(), placement1.getElements());
					if (symetricDifference.isEmpty()) {
						Context.eINSTANCE.logger.warn("twins detected " + placement0.getPlacementFragment() + " and " + placement1.getPlacementFragment());
					} else {
						ArrayList<Object> intersectonData = new ArrayList<Object>();
						intersectonData.add(arrayPlacements.get(i));
						intersectonData.add(arrayPlacements.get(j));
						intersectonData.add(new HashSet<EObject>(intersection));
						intersections.add(intersectonData);
					}
				}
			}
		}
		return intersections;
	}

}
