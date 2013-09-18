package no.sintef.cvl.ui.loader;

import java.io.File;
import java.io.IOException;

import no.sintef.cvl.ui.context.Context;
import no.sintef.cvl.ui.primitive.SymbolTable;
import no.sintef.cvl.ui.strategy.impl.RRComposerStrategy;
import no.sintef.cvl.ui.strategy.impl.RealizationStrategyBottomUp;
import no.sintef.cvl.ui.strategy.impl.ScopeResolverStrategyScopeable;
import no.sintef.test.common.TestProject;
import no.sintef.test.common.TestResourceHolder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cvl.ConfigurableUnit;
import cvl.VSpecResolution;

public class BottomUpResolverTest {

	private static TestProject testProject;
	private static String[] testFolders = {
			"TestFolder", "TestFolder/vm",
			"TestFolder/products", "TestFolder/expproducts"
		};
	private static TestResourceHolder[] testResources = {
		new TestResourceHolder("/test/resources/vm/repetitionsSveralInstances.cvl", "/TestFolder/vm/repetitionsSveralInstances.cvl"),
		new TestResourceHolder("/test/resources/repetitions.uml", "/TestFolder/repetitions.uml"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsSveralInstances_repetitions.uml", "/TestFolder/expproducts/repetitionsSveralInstances_repetitions.uml"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsSveralInstances1_repetitions.uml", "/TestFolder/expproducts/repetitionsSveralInstances1_repetitions.uml"),
		
		new TestResourceHolder("/test/resources/vm/repetitionsSveralInstancesV1.cvl", "/TestFolder/vm/repetitionsSveralInstancesV1.cvl"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsSveralInstancesV1_repetitiopnsV1.uml", "/TestFolder/expproducts/repetitionsSveralInstancesV1_repetitiopnsV1.uml"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsSveralInstancesV1_1_repetitiopnsV1.uml", "/TestFolder/expproducts/repetitionsSveralInstancesV1_1_repetitiopnsV1.uml"),
		new TestResourceHolder("/test/resources/repetitiopnsV1.uml", "/TestFolder/repetitiopnsV1.uml"),
		
		new TestResourceHolder("/test/resources/vm/prinerpoolPrinterPoolPrinterCartridge.cvl", "/TestFolder/vm/prinerpoolPrinterPoolPrinterCartridge.cvl"),
		new TestResourceHolder("/test/resources/printerpool.uml", "/TestFolder/printerpool.uml"),
		new TestResourceHolder("/test/resources/printerpoollib.uml", "/TestFolder/printerpoollib.uml"),
		new TestResourceHolder("/test/resources/printerpoollib2.uml", "/TestFolder/printerpoollib2.uml"),
		new TestResourceHolder("/test/resources/expproducts/pool2printers3cart_printerpool.uml", "/TestFolder/expproducts/pool2printers3cart_printerpool.uml"),
		new TestResourceHolder("/test/resources/expproducts/ptinter_cart_printerpool.uml", "/TestFolder/expproducts/ptinter_cart_printerpool.uml"),
		new TestResourceHolder("/test/resources/printerlib3.uml", "/TestFolder/printerlib3.uml"),
		
		new TestResourceHolder("/test/resources/office.cvl", "/TestFolder/office.cvl"),
		new TestResourceHolder("/test/resources/base.uml", "/TestFolder/base.uml"),
		new TestResourceHolder("/test/resources/lib.uml", "/TestFolder/lib.uml"),
		new TestResourceHolder("/test/resources/expproducts/office1_base.uml", "/TestFolder/expproducts/office1_base.uml"),
		new TestResourceHolder("/test/resources/expproducts/office2_base.uml", "/TestFolder/expproducts/office2_base.uml"),
		new TestResourceHolder("/test/resources/expproducts/office3_base.uml", "/TestFolder/expproducts/office3_base.uml"),
		new TestResourceHolder("/test/resources/expproducts/office4_base.uml", "/TestFolder/expproducts/office4_base.uml"),
		
		new TestResourceHolder("/test/resources/vm/prinerpoolPrinterPoolPrinterCartridgeVClass.cvl", "/TestFolder/vm/prinerpoolPrinterPoolPrinterCartridgeVClass.cvl"),
		new TestResourceHolder("/test/resources/expproducts/printerPoolprinterCarVClass_printerpool.uml", "/TestFolder/expproducts/printerPoolprinterCarVClass_printerpool.uml"),
		
		new TestResourceHolder("/test/resources/vm/repetitionsScopeless.cvl", "/TestFolder/vm/repetitionsScopeless.cvl"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsScopeless1_repetitions.uml", "/TestFolder/expproducts/repetitionsScopeless1_repetitions.uml"),
		new TestResourceHolder("/test/resources/expproducts/repetitionsScopeless2_repetitions.uml", "/TestFolder/expproducts/repetitionsScopeless2_repetitions.uml"),
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testProject = new TestProject("BottomUpResolverProjectTest");
		for(String folder : testFolders)
			testProject.createFolder(folder);
		for(TestResourceHolder resource : testResources){
			IFile iFile = testProject.copyFileFromPlugin(resource.getSource(), resource.getTarget());
			resource.setiFile(iFile);
		}
	}

	private RRComposerStrategy composer;
	private ScopeResolverStrategyScopeable scopeResolver;
	private RealizationStrategyBottomUp productResolver;
	
	@Before
	public void setUp() throws Exception {
		IWorkbenchWindow iWorkBenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Context.eINSTANCE.setIWorkbenchWindow(iWorkBenchWindow);
		
		composer = new RRComposerStrategy();
		scopeResolver = new ScopeResolverStrategyScopeable();
		productResolver = new RealizationStrategyBottomUp();

	}

	@After
	public void tearDown() throws Exception {
		Context.eINSTANCE.getCvlModels().clear();
		Context.eINSTANCE.getCvlViews().clear();
		//testProject.disposeTestProject();
	}

	@Test
	public void repetitionsSveralInstances() throws IOException, CoreException {	
			File fileVarModel = testResources[0].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstances");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstances_repetitions.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[2].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void repetitionsSveralInstances1() throws IOException, CoreException {	
			File fileVarModel = testResources[0].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(1);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstances1");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstances1_repetitions.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[3].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void repetitionsSveralInstancesV1() throws IOException, CoreException {	
			File fileVarModel = testResources[4].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstancesV1");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstancesV1_repetitiopnsV1.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[5].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void repetitionsSveralInstancesV1_1() throws IOException, CoreException {	
			File fileVarModel = testResources[4].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(1);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstancesV1_1");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsSveralInstancesV1_1_repetitiopnsV1.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[6].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void printerCartirdgeSveralInstances() throws IOException, CoreException {	
			File fileVarModel = testResources[8].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/pool2printers3cart");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/pool2printers3cart_printerpool.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[12].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void printerCartirdgeSingleInstance() throws IOException, CoreException {	
			File fileVarModel = testResources[8].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(1);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/ptinter_cart");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/ptinter_cart_printerpool.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[13].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void office1() throws IOException, CoreException {	
			File fileVarModel = testResources[15].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/office1");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/office1_base.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[18].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void office2() throws IOException, CoreException {	
			File fileVarModel = testResources[15].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(1);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/office2");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/office2_base.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[19].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void office3() throws IOException, CoreException {	
			File fileVarModel = testResources[15].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(2);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/office3");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/office3_base.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[20].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void office4() throws IOException, CoreException {	
			File fileVarModel = testResources[15].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(3);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/office4");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/office4_base.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[21].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void printerPrinterPoolCartirdgeMoreVClassifiers() throws IOException, CoreException {	
			File fileVarModel = testResources[22].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/printerPoolprinterCarVClass");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/printerPoolprinterCarVClass_printerpool.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[23].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void repetitionsScopeless1() throws IOException, CoreException {	
			File fileVarModel = testResources[24].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(0);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsScopeless1");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsScopeless1_repetitions.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[25].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}
	
	@Test
	public void repetitionsScopeless2() throws IOException, CoreException {	
			File fileVarModel = testResources[24].getiFile().getLocation().toFile();
			CVLModel model = Context.eINSTANCE.loadModelFromFile(fileVarModel);
			ConfigurableUnit cu = model.getCU();
			VSpecResolution vSpecResolution = cu.getOwnedVSpecResolution().get(1);
			
			SymbolTable symbolTable = composer.buildSymbolTable(cu, vSpecResolution);
			scopeResolver.resolveScopes(symbolTable);
			productResolver.deriveProduct(symbolTable);
			
			IFile iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsScopeless2");
			File fileProduct = iProduct.getLocation().toFile();
			Context.eINSTANCE.writeProductsToFiles(Context.eINSTANCE.getSubEngine().getCopiedBaseModels(), fileProduct);
			
			iProduct = testProject.iProject.getFile("/TestFolder/products/repetitionsScopeless2_repetitions.uml");
			fileProduct = iProduct.getLocation().toFile();
			
			boolean isIdentical = TestProject.isIdentical(testResources[26].getiFile().getLocation().toFile().getAbsolutePath(), fileProduct.getAbsolutePath());
			Assert.assertTrue("derived and expected products are different", isIdentical);
	}

}