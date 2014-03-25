package no.sintef.bvr.ui.editor.commands;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.ui.IEditorReference;

import bvr.BCLConstraint;
import bvr.BCLExpression;
import bvr.Choice;
import bvr.ChoiceResolutuion;
import bvr.ConfigurableUnit;
import bvr.Constraint;
import bvr.MultiplicityInterval;
import bvr.NamedElement;
import bvr.PlacementFragment;
import bvr.PrimitveType;
import bvr.ReplacementFragmentType;
import bvr.VClassifier;
import bvr.VInstance;
import bvr.VSpec;
import bvr.VSpecResolution;
import bvr.Variable;
import bvr.VariableValueAssignment;
import bvr.Variabletype;

public interface EditorCommands {
	
	public void addChoice(Choice choice, ConfigurableUnit cu);
	
	public void addChoice(Choice choice, VSpec vs);
	
	public TransactionalEditingDomain testTransactionalEditingDomain();
	
	public boolean testXMIResourceUnload(XMIResource resource, IEditorReference[] editorReferences);
	
	public void createNewResolution(ChoiceResolutuion cr, ConfigurableUnit cu);
	
	public void setResolutionDecision(ChoiceResolutuion cr, boolean decision);
	
	public void setChoiceResolvedVSpec(ChoiceResolutuion cr, Choice choice);
	
	public void addChoiceResolved(Choice target, VSpecResolution vsper, ChoiceResolutuion cr);
	
	public void addVClassifierToVSpec(VSpec parentVSpec, VClassifier childCClassifier);
	
	public void addVClassifierToConfigurableUnit(ConfigurableUnit cu, VClassifier childCClassifier);
	
	public void addBCLConstraint(ConfigurableUnit cu, BCLConstraint constraint);
	
	public void addVariableType(ConfigurableUnit cu, PrimitveType primType);
	
	public void addVariable(VSpec vSpec, Variable variable);
	
	public void addVariableValueAssignment(VSpecResolution parentVSpecResolution, VariableValueAssignment varValueAssignment);
	
	public void addVInstance(VSpecResolution parentVSpecResolution, VInstance vInstance);
	
	public void removeNamedElementVSpec(VSpec parentVSpec, NamedElement namedElement);
	
	public void removeOwnedVSpecConfigurableUnit(ConfigurableUnit cu, NamedElement namedElement);
	
	public void addVSpecToVSpec(VSpec parentVSpec, VSpec childVSpec);

	public void addVSpecToConfigurableUnit(ConfigurableUnit cu, VSpec childVSpec);
	
	public void removeConstraintConfigurableUnit(ConfigurableUnit cu, NamedElement namedElement);
	
	public void removeAllConstraintConfigurableUnit(ConfigurableUnit cu, List<Constraint> constraints);
	
	public void setName(NamedElement namedElement, String name);
	
	public void setVSpecComment(VSpec vSpec, String comment);
	
	public void setIsImpliedByParent(Choice choice, boolean isImplied);
	
	public void setVSpecGroupMultiplicity(VSpec vSpec, MultiplicityInterval eObject);
	
	public void setGroupMultiplicityUpperBound(MultiplicityInterval mInterval, int upperBound);
	
	public void setGroupMultiplicityLowerBound(MultiplicityInterval mInterval, int lowerBound);

	public void setTypeForVariable(Variable variable, Variabletype variableType);
	
	public void clearBCLConstraintExpressions(BCLConstraint constraint);
	
	public void addBCLExpressionConstraint(BCLConstraint constraint, BCLExpression expression);
	
	public void removeVSpecVariable(VSpec vSpec, Variable var);
	
	public void removeNamedElementVSpecResolution(VSpecResolution vSpecResolution, NamedElement namedElement);
	
	public void removeOwnedVSpecResolutionConfigurableUnit(ConfigurableUnit cu, NamedElement namedElement);
	
	public void removeOwnedVSpecResolutions(ConfigurableUnit cu);
	
	public void addOwnedVSpecResolutionConfigurableUnit(ConfigurableUnit cu, VSpecResolution vSpecResolution);
	
	public void addOwnedVSpecResolutionsConfigurableUnit(ConfigurableUnit cu, EList<VSpecResolution> vSpecResolutions);
	
	public void addPlacementFrgament(ConfigurableUnit cu, PlacementFragment placementFragment);
	
	public void addReplacementFrgament(ConfigurableUnit cu, ReplacementFragmentType replacementFragment);
}
