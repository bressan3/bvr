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
package no.sintef.bvr.tool.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import no.sintef.bvr.tool.common.LoaderUtility;

public class FMFilter extends FileFilter {

	public static final String M_EXT = "m";
	public static final String XML_EXT = "xml";
	
	@Override
	public boolean accept(File f) {
	    if (f.isDirectory()) {
	        return true;
	    }

	    String extension = LoaderUtility.getExtension(f);
	    if (extension != null) {
	    	if(extension.equals(M_EXT)) return true;
	    	if(extension.equals(XML_EXT)) return true;
	    }
	    return false;
	}
	@Override
	public String getDescription() {
		return "Other Feature Diagram (*.m, *.xml)";
	}

}
