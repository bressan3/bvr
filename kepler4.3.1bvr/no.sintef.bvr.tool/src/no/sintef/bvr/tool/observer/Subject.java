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
package no.sintef.bvr.tool.observer;

import no.sintef.bvr.tool.primitive.impl.ObserverDataBulk;

public interface Subject {

	public void attach(Observer observer);
	
	public void detach(Observer observer);
	
	public void notifyObserver();

	public void setState(ObserverDataBulk data);
	
	public boolean isApplicable(ObserverDataBulk data);
}
