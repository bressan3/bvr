/**
 */
package CVLMetamodelMaster.Main;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>VSpec Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * <p>A constraint expression that references a VSpec. It must reference an existing VSpec.</p>
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link CVLMetamodelMaster.Main.VSpecRef#getVSpec <em>VSpec</em>}</li>
 * </ul>
 * </p>
 *
 * @see CVLMetamodelMaster.Main.MainPackage#getVSpecRef()
 * @model
 * @generated
 */
public interface VSpecRef extends BCLExpression {
	/**
	 * Returns the value of the '<em><b>VSpec</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>Reference to the VSpec.</p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>VSpec</em>' reference.
	 * @see #setVSpec(VSpec)
	 * @see CVLMetamodelMaster.Main.MainPackage#getVSpecRef_VSpec()
	 * @model required="true" ordered="false"
	 * @generated
	 */
	VSpec getVSpec();

	/**
	 * Sets the value of the '{@link CVLMetamodelMaster.Main.VSpecRef#getVSpec <em>VSpec</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>VSpec</em>' reference.
	 * @see #getVSpec()
	 * @generated
	 */
	void setVSpec(VSpec value);

} // VSpecRef
