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
