/*******************************************************************************
 * Copyright (c) All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package no.sintef.bvr.tool.chain.impl;

import no.sintef.bvr.tool.chain.ExecutionHandler;
import no.sintef.bvr.tool.context.Context;
import no.sintef.bvr.tool.exception.AbstractError;
import no.sintef.bvr.tool.exception.UnexpectedException;
import no.sintef.bvr.tool.primitive.ExecutionRequest;
import no.sintef.bvr.tool.primitive.SymbolVSpecResolutionTable;
import no.sintef.bvr.tool.strategy.PlacementIntersectionResolverStrategy;
import no.sintef.bvr.tool.strategy.impl.PlacementIntersectionLessTwinAbleStrategy;

public class IntersactionResolverExecutionHandler implements ExecutionHandler {

	private ExecutionHandler successor;
	private PlacementIntersectionResolverStrategy defaultStrategy;

	public IntersactionResolverExecutionHandler(ExecutionHandler successor) {
		this.successor = successor;
		// defaultStrategy = new PlacementIntersectionLessStrategy();
		defaultStrategy = new PlacementIntersectionLessTwinAbleStrategy();
	}

	@Override
	public void handleRequest(ExecutionRequest request) throws AbstractError {
		SymbolVSpecResolutionTable table = (SymbolVSpecResolutionTable) request.getDataField("productSymbolTable");
		if (table == null)
			throw new UnexpectedException("symbol table is not set, can not proceed");

		// do check intersections if the mode is on
		if (Context.eINSTANCE.getConfig().isIntersectionDetectionMode())
			defaultStrategy.resolveIntersection(table);

		if (successor != null)
			successor.handleRequest(request);
	}

}
