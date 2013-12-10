package no.sintef.cvl.tool.ui.loader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import no.sintef.cvl.tool.context.Context;
import no.sintef.cvl.tool.filter.CVLFilter;
import no.sintef.cvl.tool.filter.SHFilter;
import no.sintef.cvl.tool.ui.context.StaticUICommands;
import no.sintef.ict.splcatool.CSVException;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.CoveringArrayFile;
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.GraphMLFM;

public class ImportResolutions implements ActionListener {
	private JTabbedPane filePane;
	private List<CVLModel> models;
	private List<CVLView> views;
	
	public ImportResolutions(JTabbedPane filePane, List<CVLModel> models, List<CVLView> views) {
		this.filePane = filePane;
		this.models = models;
		this.views = views;
	}

	public void actionPerformed(ActionEvent ae) {
		int i = filePane.getSelectedIndex();
		CVLModel m = models.get(i);
		CVLView v = views.get(i);
		
		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new SHFilter());
		fc.addChoosableFileFilter(new CVLFilter());
		fc.showOpenDialog(filePane);
		
		File sf = fc.getSelectedFile();
		GraphMLFM gfm = null;
		try {
			CoveringArray ca = new CoveringArrayFile(sf);
			GUIDSL gdsl = m.getCVLM().getGUIDSL();
			gfm = gdsl.getGraphMLFMConf(ca);
		} catch (Exception e) {
			Context.eINSTANCE.logger.error("Importing resolutions failed: ", e);
			StaticUICommands.showMessageErrorDialog(null, e, "Importing resolutions failed: ");
			return;
		}
		
		m.getCVLM().injectConfigurations(gfm);
		
		v.notifyResolutionViewUpdate();
	}
}