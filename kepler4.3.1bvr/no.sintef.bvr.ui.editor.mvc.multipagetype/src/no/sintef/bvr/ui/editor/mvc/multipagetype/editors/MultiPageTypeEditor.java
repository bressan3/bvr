package no.sintef.bvr.ui.editor.mvc.multipagetype.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import no.sintef.bvr.ui.editor.common.MVCEditor;


public class MultiPageTypeEditor extends MVCEditor {

	private CTabFolder container;
	private int index = 0; 
	
	@Override
	public void setTitle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createView() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void createPartControl(Composite parent) {
		CTabFolder continer = createContainer(parent);
		createPage(continer);
	}
	
	void createPage(Composite parent) {

		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		Button fontButton = new Button(composite, SWT.NONE);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		fontButton.setLayoutData(gd);
		fontButton.setText("Create Tab "+ index +"...");
		
		fontButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createPage(parent);
			}
		});
		
		CTabItem item = createItem(index, composite);
		item.setText("Tab " + index);
		index ++;
	}
	
	private CTabFolder createContainer(Composite parent){
		parent.setLayout(new FillLayout());
		container = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT | SWT.CLOSE);
		return container;
	}
	
	private CTabItem createItem(int index, Control control) {
		CTabItem item = new CTabItem(container, SWT.NONE, index);
		item.setControl(control);
		return item;
	}



}
