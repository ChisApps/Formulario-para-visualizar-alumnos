/* JORGE FIGUEROLA CABALLERO - PR�CTICA 1 AD Y DI 2� EVALUACI�N */

import java.sql.*;

import javax.swing.JOptionPane;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;


public class ControlAlumnos {
	DBConnection conexion = new DBConnection(); /* Crea conexi�n. */
	Statement stmt = (Statement) conexion.getConnection().createStatement(
	ResultSet.TYPE_SCROLL_SENSITIVE, /* Car�cter�sticas del result set. */
	ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = stmt.executeQuery("SELECT * FROM alumnos"); /* Carga a todos los alumnos en el ResultSet */
	private boolean DNI_error=false; //Variable para controlar que el campo DNI es correcto.
	/**
	 * * CONSTRUCTOR.
	*/
	public ControlAlumnos() throws SQLException {
		rs.first(); // Le pasamos el m�todo FIRST para mostrar al primer alumno en el formulario al abrir la apk 
	}
	
	
	/* M�todos que controlan si nos salimos del ResulSet y devuelven a la posici�n que precede,los vamos a usar para evitar excepciones*/
	
	public void control_puntero() throws SQLException{
		if (rs.isAfterLast()){
			rs.last();
		}else{
			if (rs.isBeforeFirst()){
				rs.first();
			}
		}
	}
	
	/* M�todo para pasar los alumnos hacia delante una posici�n */
	
	public void siguiente(){
		try {
			rs.next();
			control_puntero();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* M�todo para pasar los alumnos hacia atr�s una posici�n */
	
	public void anterior(){	
		try {
			rs.previous();
			control_puntero();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* M�todo para para ir a la primera posici�n */
	
	public void primera(){	
		try {
			rs.first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* M�todo para para ir a la �ltima posici�n */
	
	public void ultima(){	
		try {
			rs.last();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*M�todo para borrar fila seleccionada de tabla alumnos */
	
	public void borraralumno() throws MySQLIntegrityConstraintViolationException{	
		try {
			rs.deleteRow();
            JOptionPane.showMessageDialog(null, "Registro eliminado corr�ctamente");
			control_puntero();//Evitamos salirnos del rs
			anterior();//Devuelve a la fila anterior a la borrada
		} catch (SQLException e) {// Capturamos excepci�n y lanzamos di�logo de texto
			JOptionPane.showMessageDialog(null, "No se puede eliminar el registro ya que el campo DNI est� asociado a otra tabla de la BD");
			throw new MySQLIntegrityConstraintViolationException();
		}
	}
	
	/* M�todo para para ir a la siguiente posici�n de inserci�n */
	
	public void nuevo(){	
		try {
			// mueve el cursor a la fila de inserci�n	
			rs.moveToInsertRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* M�todo para cancelar cambios realizados */
	
	public void cancela(){	
		try {
			rs.cancelRowUpdates();
			rs.moveToCurrentRow();//Devuelve a la fila actual al descartar cambios
			JOptionPane.showMessageDialog(null, "Se han cancelado  los cambios, no se realizar�n modificaciones");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* M�todo para actualizar cambios realizados */
	
	public void actualiza() throws SQLException{	
		try {
			if (DNI_error==false){// Si DNI es correcto actualizamos(el campo dni es no nulo y debe tener valores validos para actualizar en la BD).
				rs.updateRow();   // de ah� esta restricci�n.
				JOptionPane.showMessageDialog(null, "Guardado");
			}else{
				cancela();//De lo contrario llamamos al m�todo cancela()
			}
		} catch (SQLException e) {// Capturamos excepci�n y lanzamos di�logo de texto
			JOptionPane.showMessageDialog(null, "ERROR EN ACTUALIZACI�N\n Posiblemente el campo DNI es incorrecto, est� asociado a otra tabla de la BD o corresponde a otro alumno ");
			throw new MySQLIntegrityConstraintViolationException();
		}
	}
	
	/* M�todo para insertar una nueva fila */
	
	public void insertar() throws SQLException{	
		try {
			rs.insertRow();
			rs.moveToCurrentRow();
			JOptionPane.showMessageDialog( null,"Nuevo registro realizado con exito");
		} catch (SQLException e) {// Capturamos excepci�n y lanzamos di�logo de texto
			JOptionPane.showMessageDialog( null,"ERROR EN CREACI�N DE REGISTRO\n DNI es incorrecto o corresponde a otro alumno");
			rs.refreshRow();
		}
	}
	
	
	/* M�todo para conocer la posici�n actual del puntero */
	
	public int posicion_puntero() throws SQLException {
		int posactual = rs.getRow();
		return posactual;
	}
	
	/* M�todo para conocer la posici�n final del puntero */
	
	public int posicion_final() throws SQLException {
		int posactual = rs.getRow();
		rs.last();
		int posfinal = rs.getRow();
		rs.absolute(posactual);
		return posfinal;
	}

	// M�todos get y set para pasar valores verdadero o falso desde GUIAlumnos seg�n si DNI es err�neo
	public boolean isDNI_error() {
		return DNI_error;
	}

	public void setDNI_error(boolean dNI_error) {
		DNI_error = dNI_error;
	}
	
	/* M�todo para cerrar la conexi�n con la BD */
	
	public void cerrar_conexion() throws SQLException {
		rs.close();// Cierra ResultSet
		stmt.close(); // Cierra Statement
		conexion.desconectar(); // Cierra conexi�n con BD llamando a m�todo de la instacia DBConnection
		JOptionPane.showMessageDialog( null,"Cerrada conexion con base de datos");//Lanza di�logo
	}
}
