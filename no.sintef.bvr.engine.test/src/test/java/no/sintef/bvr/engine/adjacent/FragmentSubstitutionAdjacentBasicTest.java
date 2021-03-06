/*******************************************************************************
 * Copyright (c)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.engine.adjacent;

import java.io.File;
import java.util.HashMap;

import no.sintef.bvr.engine.adjacent.impl.AdjacentFinderImpl;
import no.sintef.bvr.engine.fragment.impl.FragmentSubstitutionHolder;
import no.sintef.bvr.engine.interfaces.adjacent.IAdjacentFragment;
import no.sintef.bvr.engine.interfaces.fragment.IFragSubHolder;
import no.sintef.bvr.engine.testutils.SetUpUtils;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bvr.BVRModel;
import bvr.FragmentSubstitution;
import bvr.FromBinding;
import bvr.ToBinding;
import bvr.VariationPoint;

public class FragmentSubstitutionAdjacentBasicTest {

	private File file;
	private HashMap<String, Object> map;
	private BVRModel cu;
	private EList<FragmentSubstitution> fragSubs;
	private Resource baseModel;
	private FragmentSubstitutionHolder fragmentSubHolder1;
	private FragmentSubstitutionHolder fragmentSubHolder2;
	private FragmentSubstitutionHolder fragmentSubHolder3;
	private BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList;

	@Before
	public void setUp() throws Exception {
		fragSubs = new BasicEList<FragmentSubstitution>();
		file = new File("src/test/resources/nodeAdjacent/exp1/node.newbvr2.bvr");
		map = SetUpUtils.load(file);
		cu = (BVRModel) ((Resource) map.get("resource")).getContents().get(0);
		EList<VariationPoint> vps = cu.getRealizationModel();
		for(VariationPoint vp : vps){
			if(vp instanceof FragmentSubstitution){
				fragSubs.add((FragmentSubstitution) vp);
			}
		}
		
		Assert.assertTrue("can not locate a fragment substitution, the test can not be executed", fragSubs.size() == 3);
		fragmentSubHolder1 = new FragmentSubstitutionHolder(fragSubs.get(2));
		fragmentSubHolder2 = new FragmentSubstitutionHolder(fragSubs.get(1));
		fragmentSubHolder3 = new FragmentSubstitutionHolder(fragSubs.get(0));
		fragmentSubHolderList = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList.add(fragmentSubHolder1);
		fragmentSubHolderList.add(fragmentSubHolder2);
		fragmentSubHolderList.add(fragmentSubHolder3);
		baseModel = cu.eResource().getResourceSet().getResource(URI.createFileURI("base.node"), false);
		Assert.assertNotNull("base model is not found, the test cases can not be executed", baseModel);
	}

	@After
	public void tearDown() throws Exception {
		SetUpUtils.tearDown(map);
	}
	
	@Test
	public void testAdjacentTest1() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
	}
	
	@Test
	public void testAdjacentTest2() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
		
		
		BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList1 = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList1.add(fragmentSubHolder1);
		fragmentSubHolderList1.add(fragmentSubHolder3);
		fragmentSubHolderList1.add(fragmentSubHolder2);
		AdjacentFinderImpl adjacenFinder1 = new AdjacentFinderImpl(fragmentSubHolderList1);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap1 = adjacenFinder1.getAdjacentMap();
		
		IAdjacentFragment adjacent11 = adjacentMap1.get(fragmentSubHolder1);
		IAdjacentFragment adjacent12 = adjacentMap1.get(fragmentSubHolder2);
		IAdjacentFragment adjacent13 = adjacentMap1.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent11.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent12.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent11));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent13));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent1.getFragmentHolder()));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent13.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent13.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From21 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To21 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To21);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From31 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To11 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From31.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To11.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To1)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To11)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From21 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To21 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From21);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To2)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To21)));
	}
	
	@Test
	public void testAdjacentTest3() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
		
		
		BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList1 = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList1.add(fragmentSubHolder2);
		fragmentSubHolderList1.add(fragmentSubHolder1);
		fragmentSubHolderList1.add(fragmentSubHolder3);
		AdjacentFinderImpl adjacenFinder1 = new AdjacentFinderImpl(fragmentSubHolderList1);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap1 = adjacenFinder1.getAdjacentMap();
		
		IAdjacentFragment adjacent11 = adjacentMap1.get(fragmentSubHolder1);
		IAdjacentFragment adjacent12 = adjacentMap1.get(fragmentSubHolder2);
		IAdjacentFragment adjacent13 = adjacentMap1.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent11.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent12.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent11));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent13));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent1.getFragmentHolder()));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent13.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent13.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From21 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To21 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To21);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From31 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To11 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From31.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To11.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To1)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To11)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From21 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To21 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From21);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To2)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To21)));
	}
	
	@Test
	public void testAdjacentTest4() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
		
		
		BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList1 = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList1.add(fragmentSubHolder2);
		fragmentSubHolderList1.add(fragmentSubHolder3);
		fragmentSubHolderList1.add(fragmentSubHolder1);
		AdjacentFinderImpl adjacenFinder1 = new AdjacentFinderImpl(fragmentSubHolderList1);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap1 = adjacenFinder1.getAdjacentMap();
		
		IAdjacentFragment adjacent11 = adjacentMap1.get(fragmentSubHolder1);
		IAdjacentFragment adjacent12 = adjacentMap1.get(fragmentSubHolder2);
		IAdjacentFragment adjacent13 = adjacentMap1.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent11.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent12.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent11));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent13));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent1.getFragmentHolder()));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent13.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent13.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From21 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To21 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To21);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From31 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To11 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From31.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To11.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To1)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To11)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From21 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To21 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From21);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To2)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To21)));
	}
	
	@Test
	public void testAdjacentTest5() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
		
		
		BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList1 = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList1.add(fragmentSubHolder3);
		fragmentSubHolderList1.add(fragmentSubHolder1);
		fragmentSubHolderList1.add(fragmentSubHolder2);
		AdjacentFinderImpl adjacenFinder1 = new AdjacentFinderImpl(fragmentSubHolderList1);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap1 = adjacenFinder1.getAdjacentMap();
		
		IAdjacentFragment adjacent11 = adjacentMap1.get(fragmentSubHolder1);
		IAdjacentFragment adjacent12 = adjacentMap1.get(fragmentSubHolder2);
		IAdjacentFragment adjacent13 = adjacentMap1.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent11.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent12.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent11));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent13));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent1.getFragmentHolder()));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent13.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent13.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From21 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To21 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To21);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From31 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To11 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From31.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To11.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To1)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To11)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From21 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To21 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From21);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To2)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To21)));
	}

	@Test
	public void testAdjacentTest6() throws Exception {
		AdjacentFinderImpl adjacenFinder = new AdjacentFinderImpl(fragmentSubHolderList);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap = adjacenFinder.getAdjacentMap();
		
		IAdjacentFragment adjacent1 = adjacentMap.get(fragmentSubHolder1);
		IAdjacentFragment adjacent2 = adjacentMap.get(fragmentSubHolder2);
		IAdjacentFragment adjacent3 = adjacentMap.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent1.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent1.getAdjacentFragmentsList().get(0).equals(adjacent2));
		Assert.assertTrue("the fragment should be adjacent", adjacent2.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent1));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent2.getAdjacentFragmentsList().contains(adjacent3));
		Assert.assertTrue("the fragment should be adjacent", adjacent3.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent3.getAdjacentFragmentsList().get(0).equals(adjacent2));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From2 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To2 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To2);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From3 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To1 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From3.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To1.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From2, SetUpUtils.reverseMap(adjacentBindings2To1)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From2 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To2 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To2.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From3, SetUpUtils.reverseMap(adjacentBindings3To2)));
		
		
		BasicEList<FragmentSubstitutionHolder> fragmentSubHolderList1 = new BasicEList<FragmentSubstitutionHolder>();
		fragmentSubHolderList1.add(fragmentSubHolder3);
		fragmentSubHolderList1.add(fragmentSubHolder2);
		fragmentSubHolderList1.add(fragmentSubHolder1);
		AdjacentFinderImpl adjacenFinder1 = new AdjacentFinderImpl(fragmentSubHolderList1);
		HashMap<IFragSubHolder, IAdjacentFragment> adjacentMap1 = adjacenFinder1.getAdjacentMap();
		
		IAdjacentFragment adjacent11 = adjacentMap1.get(fragmentSubHolder1);
		IAdjacentFragment adjacent12 = adjacentMap1.get(fragmentSubHolder2);
		IAdjacentFragment adjacent13 = adjacentMap1.get(fragmentSubHolder3);
		Assert.assertTrue("the fragment should be adjacent", adjacent11.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent11.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent12.getAdjacentFragmentsList().size() == 2);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent11));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent12.getAdjacentFragmentsList().contains(adjacent13));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent1.getFragmentHolder()));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", this.getFragmentSubstitutionHolders(adjacent12.getAdjacentFragmentsList()).contains(adjacent13.getFragmentHolder()));
		Assert.assertTrue("the fragment should be adjacent", adjacent13.getAdjacentFragmentsList().size() == 1);
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).equals(adjacent12));
		Assert.assertTrue("the fragment is adjacent to the wrong fragment", adjacent13.getAdjacentFragmentsList().get(0).getFragmentHolder().equals(adjacent2.getFragmentHolder()));
		
		HashMap<FromBinding, ToBinding> adjacentBindings1From21 = adjacent1.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings1To21 = adjacent1.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent1.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent1.getAllAdjacentToBindings().size() == 0);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings1From21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent ToBinding", adjacentBindings1To21);
		
		HashMap<FromBinding, ToBinding> adjacentBindings2From31 = adjacent2.getAdjacentFromBindings(adjacent3);
		HashMap<ToBinding, FromBinding> adjacentBindings2To11 = adjacent2.getAdjacentToBindings(adjacent1);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent2.getAllAdjacentFromBindings().size() == 1);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent2.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent FromBinding", adjacentBindings2From31.size() == 2);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings2To11.size() == 2);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To1)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings1From21, SetUpUtils.reverseMap(adjacentBindings2To11)));
		
		HashMap<FromBinding, ToBinding> adjacentBindings3From21 = adjacent3.getAdjacentFromBindings(adjacent2);
		HashMap<ToBinding, FromBinding> adjacentBindings3To21 = adjacent3.getAdjacentToBindings(adjacent2);
		Assert.assertTrue("the set of adjacent FromBinding-s is wrong", adjacent3.getAllAdjacentFromBindings().size() == 0);
		Assert.assertTrue("the set of adjacent ToBinding-s is wrong", adjacent3.getAllAdjacentToBindings().size() == 1);
		Assert.assertTrue("the fragment should have 2 adjacent ToBinding", adjacentBindings3To21.size() == 2);
		Assert.assertNull("the fragment shoudl to have any adjacent FromBinding", adjacentBindings3From21);
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To2)));
		Assert.assertTrue("wrong set of adjacent bindings", SetUpUtils.compareHashMaps(adjacentBindings2From31, SetUpUtils.reverseMap(adjacentBindings3To21)));
	}
	
	private EList<FragmentSubstitutionHolder> getFragmentSubstitutionHolders(EList<IAdjacentFragment> adjacentFragmants){
		EList<FragmentSubstitutionHolder> fragSubHolders = new BasicEList<FragmentSubstitutionHolder>();
		for(IAdjacentFragment adjacentFragment : adjacentFragmants){
			fragSubHolders.add((FragmentSubstitutionHolder) adjacentFragment.getFragmentHolder());
		}
		return fragSubHolders;
	}
}
