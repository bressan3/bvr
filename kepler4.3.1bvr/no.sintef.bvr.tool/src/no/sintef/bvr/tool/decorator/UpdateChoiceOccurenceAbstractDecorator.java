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
package no.sintef.bvr.tool.decorator;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import no.sintef.bvr.tool.controller.command.UpdateChoice;
import no.sintef.bvr.tool.controller.command.UpdateChoiceOccurence;
import no.sintef.bvr.tool.interfaces.controller.BVRNotifiableController;
import no.sintef.bvr.tool.interfaces.controller.command.Command;
import no.sintef.bvr.tool.interfaces.ui.editor.BVRUIKernelInterface;
import no.sintef.bvr.tool.interfaces.ui.editor.Pair;
import bvr.NamedElement;
import bvr.Variable;

public abstract class UpdateChoiceOccurenceAbstractDecorator<EDITOR_PANEL, MODEL_PANEL> extends UpdateChoiceOccurence<EDITOR_PANEL, MODEL_PANEL>  {

	protected UpdateChoiceOccurence<EDITOR_PANEL, MODEL_PANEL> command;
	
	public UpdateChoiceOccurenceAbstractDecorator(UpdateChoiceOccurence<EDITOR_PANEL, MODEL_PANEL> _command) {
		super();
		command = _command;
	}

	@Override
	public Command init(BVRUIKernelInterface<EDITOR_PANEL, MODEL_PANEL> rootPanel, Object p, JComponent parent,
			Map<JComponent, NamedElement> vmMap, List<JComponent> nodes,
			List<Pair<JComponent, JComponent>> bindings,
			BVRNotifiableController controller) {
		super.init(rootPanel, p, parent, vmMap, nodes, bindings, controller);
		return command.init(rootPanel, p, parent, vmMap, nodes, bindings, controller);
	}
	
	@Override
	public JComponent execute() {
		return command.execute();
	}
	

	@Override
	public void setComment(String text) {
		command.setComment(text);
	}
	
	@Override
	public void setVariable(Variable v, String name, String type) {
		command.setVariable(v, name, type);
	}
	
	@Override
	public void setName(String name) {
		command.setName(name);
	}
	
	@Override
	public void setChoiceType(String type) {
		command.setChoiceType(type);
	}
}
