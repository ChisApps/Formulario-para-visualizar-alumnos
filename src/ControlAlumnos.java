/* JORGE FIGUEROLA CABALLERO - PRÁCTICA 1 AD Y DI 2ª EVALUACIÓN */

import java.sql.*;

import javax.swing.JOptionPane;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;


public class ControlAlumnos {
	DBConnection conexion = new DBConnection(); /* Crea conexión. */
	Statement stmt = (Statement) conexion.getConnection().createStatement(
	ResultSet.TYPE_SCROLL_SENSITIVE, /* Carácterísticas del result set. */
	ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = stmt.executeQuery("SELECT * FROM alumnos"); /* Carga a todos los alumnos en el ResultSet */
	private boolean DNI_error=false; //Variable para controlar que el campo DNI es correcto.
	/**
	 * * CONSTRUCTOR.
	*/
	public ControlAlumnos() throws SQLException {
		rs.first(); // Le pasamos el método FIRST para mostrar al primer alumno en el formulario al abrir la apk 
	}
	
	
	/* Métodos que controlan si nos salimos del ResulSet y devuelven a la posición que precede,los vamos a usar para evitar excepciones*/
	
	public void control_puntero() throws SQLException{
		if (rs.isAfterLast()){
			rs.last();
		}else{
			if (rs.isBeforeFirst()){
				rs.first();
			}
		}
	}
	
	/* Método para pasar los alumnos hacia delante una posición */
	
	public void siguiente(){
		try {
			rs.next();
			control_puntero();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Método para pasar los alumnos hacia atrás una posición */
	
	public void anterior(){	
		try {
			rs.previous();
			control_puntero();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Método para para ir a la primera posición */
	
	public void primera(){	
		try {
			rs.first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Método para para ir a la última posición */
	
	public void ultima(){	
		try {
			rs.last();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*Método para borrar fila seleccionada de tabla alumnos */
	
	public void borraralumno() throws MySQLIntegrityConstraintViolationException{	
		try {
			rs.deleteRow();
            JOptionPane.showMessageDialog(null, "Registro eliminado corréctamente");
			control_puntero();//Evitamos salirnos del rs
			anterior();//Devuelve a la fila anterior a la borrada
		} catch (SQLException e) {// Capturamos excepción y lanzamos diálogo de texto
			JOptionPane.showMessageDialog(null, "No se puede eliminar el registro ya que el campo DNI está asociado a otra tabla de la BD");
			throw new MySQLIntegrityConstraintViolationException();
		}
	}
	
	/* Método para para ir a la siguiente posición de inserción */
	
	public void nuevo(){	
		try {
			// mueve el cursor a la fila de inserción	
			rs.moveToInsertRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Método para cancelar cambios realizados */
	
	public void cancela(){	
		try {
			rs.cancelRowUpdates();
			rs.moveToCurrentRow();//Devuelve a la fila actual al descartar cambios
			JOptionPane.showMessageDialog(null, "Se han cancelado  los cambios, no se realizarán modificaciones");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Método para actualizar cambios realizados */
	
	public void actualiza() throws SQLException{	
		try {
			if (DNI_error==false){// Si DNI es correcto actualizamos(el campo dni es no nulo y debe tener valores validos para actualizar en la BD).
				rs.updateRow();   // de ahí esta restricción.
				JOptionPane.showMessageDialog(null, "Guardado");
			}else{
				cancela();//De lo contrario llamamos al método cancela()
			}
		} catch (SQLException e) {// Capturamos excepción y lanzamos diálogo de texto
			JOptionPane.showMessageDialog(null, "ERROR EN ACTUALIZACIÓN\n Posiblemente el campo DNI es incorrecto, está asociado a otra tabla de la BD o corresponde a otro alumno ");
			throw new MySQLIntegrityConstraintViolationException();
		}
	}
	
	/* Método para insertar una nueva fila */
	
	public void insertar() throws SQLException{	
		try {
			rs.insertRow();
			rs.moveToCurrentRow();
			JOptionPane.showMessageDialog( null,"Nuevo registro realizado con exito");
		} catch (SQLException e) {// Capturamos excepción y lanzamos diálogo de texto
			JOptionPane.showMessageDialog( null,"ERROR EN CREACIÓN DE REGISTRO\n DNI es incorrecto o corresponde a otro alumno");
			rs.refreshRow();
		}
	}
	
	
	/* Método para conocer la posición actual del puntero */
	
	public int posicion_puntero() throws SQLException {
		int posactual = rs.getRow();
		return posactual;
	}
	
	/* Método para conocer la posición final del puntero */
	
	public int posicion_final() throws SQLException {
		int posactual = rs.getRow();
		rs.last();
		int posfinal = rs.getRow();
		rs.absolute(posactual);
		return posfinal;
	}

	// Métodos get y set para pasar valores verdadero o falso desde GUIAlumnos según si DNI es erróneo
	public boolean isDNI_error() {
		return DNI_error;
	}

	public void setDNI_error(boolean dNI_error) {
		DNI_error = dNI_error;
	}
	
	/* Método para cerrar la conexión con la BD */
	
	public void cerrar_conexion() throws SQLException {
		rs.close();// Cierra ResultSet
		stmt.close(); // Cierra Statement
		conexion.desconectar(); // Cierra conexión con BD llamando a método de la instacia DBConnection
		JOptionPane.showMessageDialog( null,"Cerrada conexion con base de datos");//Lanza diálogo
	}
}
