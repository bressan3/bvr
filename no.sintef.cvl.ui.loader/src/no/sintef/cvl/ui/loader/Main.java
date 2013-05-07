package no.sintef.cvl.ui.loader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import no.sintef.cvl.ui.commands.events.CloseModelEvent;
import no.sintef.cvl.ui.commands.events.NewResolutionEvent;
import cvl.NamedElement;

public class Main {
	private JFrame jframe;
	public static NamedElement vSpecCut;
	private List<CVLView> views;
	private List<CVLModel> models;

	public static void main(String[] args) throws CVLModelException {
		new Main().main();
	}

	private void main(){
		views = new ArrayList<CVLView>();
		models = new ArrayList<CVLModel>();

		// Create window
		jframe = new JFrame("CVL 2 Editor by SINTEF");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setPreferredSize(new Dimension(1000, 800));
		jframe.setBackground(Color.WHITE);

		// Tabbed pane
		JTabbedPane filePane = new JTabbedPane();

		// Add menu bar
		JMenuBar menuBar = new JMenuBar();
		jframe.setJMenuBar(menuBar);

		// File
		JMenu filemenu = new JMenu("File");
		JMenuItem x = new JMenuItem("New");
		x.addActionListener(new NewModelEvent(models, views, filePane));
		filemenu.add(x);
		JMenuItem openfile = new JMenuItem("Open ...");
		openfile.addActionListener(new OpenModelEvent(filePane, models, views));
		filemenu.add(openfile);
		filemenu.add(new JSeparator());
		JMenuItem save = new JMenuItem("Save");
		filemenu.add(save);
		save.addActionListener(new SaveModelAsEvent(filePane, models, views, true));
		//TODO: filemenu.add(new JMenuItem("Save all"));
		JMenuItem saveas = new JMenuItem("Save as ...");
		saveas.addActionListener(new SaveModelAsEvent(filePane, models, views, false));
		filemenu.add(saveas);
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new CloseModelEvent(filePane, models, views));
		filemenu.add(close);
		
		filemenu.add(new JSeparator());
		
		JMenuItem saveasImage = new JMenuItem("Export Diagram as PNG ...");
		filemenu.add(saveasImage);
		saveasImage.addActionListener(new ExportModelImage(views, filePane));
		JMenuItem saveasSVG = new JMenuItem("Export Diagram as SVG ...");
		filemenu.add(saveasSVG);
		saveasSVG.addActionListener(new ExportModelSVG(views, filePane));
		
		//TODO: filemenu.add(new JSeparator());
		
		//TODO: filemenu.add(new JMenuItem("Exit"));
		menuBar.add(filemenu);

		// VSpec
		JMenu vsmenu = new JMenu("VSpec");
		JMenuItem importvspec = new JMenuItem("Import ...");
		importvspec.addActionListener(new ImportVSpecEvent(filePane, models, views));
		vsmenu.add(importvspec);
		//TODO: vsmenu.add(new JMenuItem("Export ..."));
		//TODO: vsmenu.add(new JMenuItem("Count Solutions"));
		menuBar.add(vsmenu);

		// Resolutions
		JMenu camenu = new JMenu("Resolutions");
		JMenuItem newres = new JMenuItem("New");
		newres.addActionListener(new NewResolutionEvent(filePane, models, views));
		camenu.add(newres);
		camenu.add(new JSeparator());
		JMenuItem importres = new JMenuItem("Import ...");
		importres.addActionListener(new ImportResolutions(filePane, models, views));
		camenu.add(importres);
		//TODO: camenu.add(new JMenuItem("Export ..."));
		
		camenu.add(new JSeparator());
		JMenuItem valres = new JMenuItem("Validate Resolutions");
		valres.addActionListener(new ValidateResolutions(filePane, models, views));
		camenu.add(valres);
		JMenu cc1 = new JMenu("Calculate Coverage");
		JMenuItem calccov1 = new JMenuItem("1-wise");
		calccov1.addActionListener(new CalculateCoverage(filePane, models, views, 1));
		cc1.add(calccov1);
		JMenuItem calccov2 = new JMenuItem("2-wise");
		calccov2.addActionListener(new CalculateCoverage(filePane, models, views, 2));
		cc1.add(calccov2);
		JMenuItem calccov3 = new JMenuItem("3-wise");
		calccov3.addActionListener(new CalculateCoverage(filePane, models, views, 3));
		cc1.add(calccov3);
		camenu.add(cc1);
		camenu.add(new JSeparator());
		
		JMenu genca = new JMenu("Generate Covering Array");
		JMenuItem genca1 = new JMenuItem("1-wise");
		genca1.addActionListener(new GenerateCoveringArray(filePane, models, views, 1));
		genca.add(genca1);
		JMenuItem genca2 = new JMenuItem("2-wise");
		genca2.addActionListener(new GenerateCoveringArray(filePane, models, views, 2));
		genca.add(genca2);
		JMenuItem genca3 = new JMenuItem("3-wise");
		genca3.addActionListener(new GenerateCoveringArray(filePane, models, views, 3));
		genca.add(genca3);
		camenu.add(genca);
		menuBar.add(camenu);

		// Done
		jframe.add(filePane, BorderLayout.CENTER);
		jframe.pack();
		jframe.repaint();
		jframe.setVisible(true);
	}
}


