/*******************************************************************************
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package splar.core.heuristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import splar.core.constraints.BooleanVariableInterface;
import splar.core.constraints.CNFFormula;
import splar.core.fm.FeatureGroup;
import splar.core.fm.FeatureModel;
import splar.core.fm.FeatureTreeNode;


public class FTPostOrderTraversalHeuristic extends FTTraversalHeuristic {

	public FTPostOrderTraversalHeuristic(String name, FeatureModel featureModel) {
		super(name, featureModel);
	}
	
	public String[] runHeuristic(CNFFormula cnf) {

//		runPreProcessing(cnf);
		
		featureModel.resetNodesAttachedData();

		String varOrder[] = new String[cnf.getVariables().size()];
		Set<BooleanVariableInterface> cnfVariables = cnf.getVariables();

		Stack<FeatureTreeNode> nodes = new Stack<FeatureTreeNode>();
		FeatureTreeNode curNode = featureModel.getRoot();
		List<FeatureTreeNode> childNodes = new ArrayList<FeatureTreeNode>();
		for( FeatureTreeNode node : orderChildNodes(curNode)) {
			childNodes.add(node);
		}
		curNode.attachData(childNodes);
		
		nodes.push(curNode);
		int curIndex = 0;
		
		while ( nodes.size() > 0 ) {
			curNode = nodes.peek();
			
			if ( curNode != null ) {
				
				childNodes = (List<FeatureTreeNode>)(curNode.getAttachedData());
				
				if ( childNodes.size() > 0 ) {
					FeatureTreeNode childNode = childNodes.get(0);
					childNodes.remove(0);
					
					List<FeatureTreeNode> childChildNodes = new ArrayList<FeatureTreeNode>(); 
					for( FeatureTreeNode node : orderChildNodes(childNode)) {
						childChildNodes.add(node);
					}
					childNode.attachData(childChildNodes);
					nodes.push(childNode);			
				}
				else {
					nodes.pop();
					if ( !(curNode instanceof FeatureGroup) ) {
						if ( cnfVariables.contains(curNode) ) {
							varOrder[curIndex++] = curNode.getID();
						}
					}
				}
			}
		}
		
//		runPostProcessing(cnf);
		
		return varOrder;
	}
	
	protected FeatureTreeNode[] orderChildNodes(FeatureTreeNode node) {
		int count = node.getChildCount();
		FeatureTreeNode nodes[] = new FeatureTreeNode[count];
		for( int i = 0 ; i < count ; i++ ) {
			nodes[i] = ((FeatureTreeNode)node.getChildAt(i));									
		}
		return nodes;
	}
	
}
