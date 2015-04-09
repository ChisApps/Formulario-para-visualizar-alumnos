/* JORGE FIGUEROLA CABALLERO - PR�CTICA 1 AD Y DI 2� EVALUACI�N */

import java.sql.*;
/**
* Clase para facilitar la manipulaci�n y consulta de datos con MySQL
*
*/
public class DBConnection {
	// Constantes de conexi�n
	private static final String db = "universidad";
	private static final String user = "root";
	private static final String pass = "";
	private static final String host = "localhost";
	// Variables de conexi�n para tener acceso desde todos los m�todos
	private Connection conexion = null;
	private Statement s = null;
	private ResultSet rs = null;
	/**
	 * * Constructor
	*/
	public DBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection("jdbc:mysql://"+ host +"/"+ db, user, pass);
			if (conexion != null){
				System.out.println("Conexi�n a base de datos"+db+" OK\n");
			}
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	* Retorna el objeto Connection por si se necesita
	* @return Connection
	*/
	public Connection getConnection(){
		return conexion;
	}
	
	/**
	* Realiza todas las desconexiones
	*/
	public void desconectar(){
		try {
			conexion.close();// Cierra conexi�n
		} catch (SQLException e) {
			
		}
	}
	
	public ResultSet getResulset(String query){
		try {
			s = conexion.createStatement();
			rs = s.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
}

