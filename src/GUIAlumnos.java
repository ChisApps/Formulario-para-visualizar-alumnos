/* JORGE FIGUEROLA CABALLERO - PRÁCTICA 1 AD Y DI 2ª EVALUACIÓN */

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Label;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JTextField;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;


public class GUIAlumnos {
	
	
	/* VARIABLES GLOBALES A LAS QUE LES PASAREMOS VALORES */

	private JFrame frame;
	private JTextField textField_nombre;
	private JTextField textField_dni;
	private JTextField textField_ape1;
	private JTextField textField_ape2;
	private JTextField textField_dir;
	private JTextField textField_prov;
	private JTextField textField_f_nac;
	private JTextField textField_loc;
	private Label label_posicion = new Label("/");				
									 
	ControlAlumnos alumno = new ControlAlumnos();// Creamos un instancia de control alumno para recoger y pasar
												 // valores de la GUI a la BD.
	
	/**
	 * Launch the application.
	 * @throws SQLException 
	 */
	public static void main(String[] args) {;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIAlumnos window = new GUIAlumnos();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws SQLException 
	 */
	public GUIAlumnos() throws SQLException {
		initialize();
	}
	
	/* DECLARAMOS MÉTODO QUE VA A RELLENAR TODOS LOS CAMPOS DE LOS COMPONENTES QUE LO REQUIERAN */
	
	public void rellena_datos() throws SQLException {
		/* Rellena campo DNI casteando el int y añadiendo ceros a la izquierda */
		String dniatexto = Integer.toString(alumno.rs.getInt("dni"));
		for (;dniatexto.length() < 9;){
			dniatexto="0"+dniatexto;
		}
		textField_dni.setText(dniatexto);
		/* Rellena resto de campos string */
		textField_nombre.setText(alumno.rs.getString(4));
		textField_ape1.setText(alumno.rs.getString(2));
		textField_ape2.setText(alumno.rs.getString(3));
		textField_dir.setText(alumno.rs.getString(5));
		textField_loc.setText(alumno.rs.getString(6));
		textField_prov.setText(alumno.rs.getString(7));
		
		/* Rellena campo fecha haciendo cast y dando formato  a la fecha si campo no es nulo*/
		
		if ((alumno.rs.getDate("fecha_nac") != null)){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String fechaatexto = df.format(alumno.rs.getDate("fecha_nac"));
			textField_f_nac.setText(fechaatexto);
		}else{
			textField_f_nac.setText("");
		}
		
		/* Rellena campo indicando la posición del puntero en el rs sobre la final */
		
		int posicion_actual = alumno.posicion_puntero();
		int posicion_final = alumno.posicion_final();
		String actualatexto = ""+ posicion_actual;
		String finalatexto = ""+ posicion_final;
		label_posicion.setText(actualatexto+"/"+finalatexto);	
	}
	
	/* MÉTODO RECOGE DATOS DE LOS JTEXTFIELDS Y LOS PASA AL RESULTSET */
	
	public void recibe_datos() throws SQLException, ParseException, ArrayIndexOutOfBoundsException {
		
		/*MÉTODO PARA RECOGER DNI CAPTURANDO Y DEPURANDO POSIBLES EXCEPCIONES */
		
		 String texto_dni = textField_dni.getText().replaceAll("-",""); //Recogemos texto reemplazando guiones por espacios vacios, para así evitar números negativos
		 try{
			 int dni_num = Integer.parseInt(texto_dni);//Pasamos String DNI a int
			 // Si DNI tiene un valor 0 lo declaramos como incorrecto para cancelar actualización desde ControlAlumnos y enviamos mensaje
			 // no recogemos valor y así aseguramos que salté excepción controlada si se va insertar un nuevo registro
			 if (dni_num==0){
				 JOptionPane.showMessageDialog(frame, "DNI INCORRECTO:\nEl campo DNI debe estar compuesto por un número positivo con una longitud máxima de 9 dígitos");
				 alumno.setDNI_error(true); 
			 }else{
				 // Si no se recoge dato del DNI en el rs y se declara dni correcto
				 alumno.setDNI_error(false);
				 alumno.rs.updateInt("dni", dni_num); 
			 }
			 			 
		 /*Capturamos excepción si DNI contiene algún campo no númerico o su longitud es mayor que 9*/
		 
		}catch ( NumberFormatException | SQLException  e1){
			JOptionPane.showMessageDialog(frame, "DNI INCORRECTO:\nEl campo DNI solo puede contener números y su longitud no debe exceder 9 dígitos");
			
			 /*Se declara DNI incorrecto y así cancelamos cualquier tipo de actualización.
			  *  */
				alumno.setDNI_error(true); 
		}
		 
		/*Recogemos resto de campos que son Strings en el resultset */
		 
		 alumno.rs.updateString((4),textField_nombre.getText());
		 alumno.rs.updateString((2),textField_ape1.getText());
		 alumno.rs.updateString((3),textField_ape2.getText());
		 alumno.rs.updateString((5),textField_dir.getText());
		 alumno.rs.updateString((6),textField_loc.getText());
		 alumno.rs.updateString((7),textField_prov.getText());
		 
		 
		 /* RECOGE CAMPO FECHA DEL TEXTFIELD Y LE DA FORMATO CORRECTO */
		 
		 /*Recoge cadena completa */
		 String fecha = textField_f_nac.getText();
		 
		 /*Variables a las que les vamos a pasar dividiendo mediante split cadenas día, mes y año que regocemos en array*/
		 String fechaArray[] = fecha.split("/");
		 String dia="";
		 String mes="";
		 String year="";
		 
		 try{
			 for(int i=0 ; i<fechaArray.length;i++){
				 /* Recorremos el array asignando valores */
				 dia=fechaArray[0]; 
				 mes=fechaArray[1];
				 year=fechaArray[2];
			 }
			try{	
			/*Comprobamos que los parámetros sean númericos y que tengan los valores necesarios para poder
			 * formar parte de una fecha, de lo contrario devolvemos las cadenas día,mes y year vacias
			 */
				int dia_comprueba= Integer.parseInt(dia);
				int mes_comprueba= Integer.parseInt(mes);
				int year_comprueba= Integer.parseInt(year);
				if (dia_comprueba<1 || dia_comprueba >31){
					dia="";
					}else{
						if (mes_comprueba<1 ||mes_comprueba >12){
							mes="";
						}else{
							if (year_comprueba<0 ||year_comprueba >9999){
								year="";
							}
						}
					}	
				}catch (NumberFormatException e_fecha){
					//Capturamos excepción si los parámetros no eran númericos y declaramos las cadenas vacias 
					dia="";
					mes="";
					year="";
				}
			
			/*Construimos la cadena fecha con el formato adecuado para poderlo pasar a la BD,agrupando las
			 * cadenas día, mes y year.
			 */
			 fecha=(year+"-"+mes+"-"+dia);
			 
			 /*Comprobamos que la longitud total de fecha sea 10 para controlar que no hemos metido ningún número de 
			  * más en el formulario, si no mostramos mensaje por pantalla.
			  */
			 if (fecha.length()==10){
				 alumno.rs.updateString("fecha_nac", fecha);
			 }else{
				 JOptionPane.showMessageDialog(frame, "FECHA INCORRECTA:\nIntroduzca fecha en formato dd/MM/yyyy");
				 JOptionPane.showMessageDialog(frame, "No se realizarán modificaciones en el campo F.Nac");
			 }
			 /*En el caso que fecha no cumpla los requisitos mostramos mensaje por pantalla y capturamos excepción */	 
		 }catch (ArrayIndexOutOfBoundsException e_fecha){
			 JOptionPane.showMessageDialog(frame, "FECHA INCORRECTA:\nIntroduzca fecha en formato dd/MM/yyyy y valores de fecha valido");
			 JOptionPane.showMessageDialog(frame, "No se realizarán modificaciones en el campo F.Nac");
		 }
	}
	
	/*MÉTODO LIMPIA CAMPOS PARA MOSTRAR EL FORMULARIO VACIO*/
	
	public void limpiacampos() throws SQLException {
		textField_nombre.setText("");
		textField_dni.setText("");
		textField_ape1.setText("");
		textField_ape2.setText("");
		textField_dir.setText("");
		textField_loc.setText("");
		textField_prov.setText("");
		textField_f_nac.setText("");
		
		/* Rellena campo sobre la posición del puntero en el rs como nueva al traterse de una nueva fila
		 * la que queremos insertar
		 *  */
		
		label_posicion.setText("Nueva");
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 * @throws SQLException 
	 */
	private void initialize() throws SQLException {
	
	/* INICIALIZAMOS LA GUI Y DECLARAMOS TODOS LOS CONTENDORES Y COMPONENTES */
		
		frame = new JFrame();
		frame.setBounds(500, 500, 541, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	// Método para desconectar de la BD y cerrar el programa desde el botón close del frame
		frame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	        	try {
					alumno.cerrar_conexion();// Llama al método cerrar conexión de alumno
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 	        System.exit(0);
	          }        
	       });    
		
		
		JPanel panel_principal = new JPanel();
		panel_principal.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_principal, BorderLayout.CENTER);
		panel_principal.setLayout(null);
		
		/* Panel con la información del alumno */
		
		Panel panel_info = new Panel();
		panel_info.setBackground(new Color(211, 211, 211));
		panel_info.setBounds(10, 10, 382, 289);
		panel_principal.add(panel_info);
		panel_info.setLayout(null);
		
		JPanel panel_etiquetas = new JPanel();
		panel_etiquetas.setBounds(10, 11, 157, 267);
		panel_info.add(panel_etiquetas);
		panel_etiquetas.setLayout(null);
		
		Label label_dni = new Label("DNI");
		label_dni.setBounds(10, 10, 137, 25);
		panel_etiquetas.add(label_dni);
		
		Label label_nombre = new Label("Nombre");
		label_nombre.setBounds(10, 41, 137, 25);
		panel_etiquetas.add(label_nombre);
		
		Label label_ape1 = new Label("Primer apellido");
		label_ape1.setBounds(10, 72, 137, 25);
		panel_etiquetas.add(label_ape1);
		
		Label label_ap2 = new Label("Segundo apellido");
		label_ap2.setBounds(10, 103, 137, 25);
		panel_etiquetas.add(label_ap2);
		
		Label label_dir = new Label("Direcci\u00F3n");
		label_dir.setBounds(10, 134, 137, 25);
		panel_etiquetas.add(label_dir);

		Label label_loc = new Label("Localidad");
		label_loc.setBounds(10, 165, 137, 25);
		panel_etiquetas.add(label_loc);
				
		Label label_f_nac = new Label("F.Nac");
		label_f_nac.setBounds(10, 227, 137, 25);
		panel_etiquetas.add(label_f_nac);
		
		Label label_prov = new Label("Provincia");
		label_prov.setBounds(10, 196, 137, 25);
		panel_etiquetas.add(label_prov);
		
		JPanel panel_campos = new JPanel();
		panel_campos.setBounds(177, 11, 195, 267);
		panel_info.add(panel_campos);
		panel_campos.setLayout(null);
		
		textField_dni = new JTextField();
		textField_dni.setBounds(10, 11, 175, 20);
		panel_campos.add(textField_dni);
		textField_dni.setColumns(10);
		
		textField_nombre = new JTextField();
		textField_nombre.setBounds(10, 42, 175, 20);
		panel_campos.add(textField_nombre);
		textField_nombre.setColumns(10);
		
		textField_ape1 = new JTextField();
		textField_ape1.setBounds(10, 73, 175, 20);
		panel_campos.add(textField_ape1);
		textField_ape1.setColumns(10);
		
		textField_ape2 = new JTextField();
		textField_ape2.setBounds(10, 104, 175, 20);
		panel_campos.add(textField_ape2);
		textField_ape2.setColumns(10);
		
		textField_dir = new JTextField();
		textField_dir.setBounds(10, 135, 175, 20);
		panel_campos.add(textField_dir);
		textField_dir.setColumns(10);
		
		textField_loc = new JTextField();
		textField_loc.setBounds(10, 166, 175, 20);
		panel_campos.add(textField_loc);
		textField_loc.setColumns(10);
		
		textField_prov = new JTextField();
		textField_prov.setBounds(10, 197, 175, 20);
		panel_campos.add(textField_prov);
		textField_prov.setColumns(10);
		
		textField_f_nac = new JTextField();
		textField_f_nac.setBounds(10, 228, 175, 20);
		panel_campos.add(textField_f_nac);
		textField_f_nac.setColumns(10);
		
		/* Panel inferior */
		
		Panel panel_inferior = new Panel();
		panel_inferior.setBackground(new Color(211, 211, 211));
		panel_inferior.setBounds(10, 309, 505, 43);
		panel_principal.add(panel_inferior);
		panel_inferior.setLayout(null);
		
		Button button_cancelar = new Button("Cancelar");
		button_cancelar.setBounds(425, 10, 70, 22);
		panel_inferior.add(button_cancelar);
		
		Button button_aceptar = new Button("Aceptar");
		button_aceptar.setBounds(349, 10, 70, 22);
		panel_inferior.add(button_aceptar);
		
		Button button_primero = new Button("<<");
		button_primero.setBounds(10, 10, 30, 22);
		panel_inferior.add(button_primero);
		
		Button button_anterior = new Button("<");
		button_anterior.setBounds(46, 10, 30, 22);
		panel_inferior.add(button_anterior);
		    
		label_posicion.setAlignment(Label.CENTER);
		label_posicion.setBackground(Color.WHITE);
		label_posicion.setBounds(82, 10, 62, 22);
		panel_inferior.add(label_posicion);
		
		Button button_siguiente = new Button(">");
		button_siguiente.setBounds(150, 10, 30, 22);
		panel_inferior.add(button_siguiente);
		
		Button button_ultimo = new Button(">>");
		button_ultimo.setBounds(186, 10, 30, 22);
		panel_inferior.add(button_ultimo);
		
		/* Panel modifica */
		
		Panel panel_modifica = new Panel();
		panel_modifica.setForeground(Color.BLACK);
		panel_modifica.setBackground(new Color(220, 220, 220));
		panel_modifica.setBounds(398, 10, 117, 106);
		panel_principal.add(panel_modifica);
		panel_modifica.setLayout(null);
		
		Button button_nuevo = new Button("Nuevo");
		button_nuevo.setActionCommand("Nuevo");
		button_nuevo.setBounds(26, 20, 70, 22);
		panel_modifica.add(button_nuevo);
		
		Button button_eliminar = new Button("Elminar");
		button_eliminar.setActionCommand("Eliminar");
		button_eliminar.setBounds(26, 62, 70, 22);
		panel_modifica.add(button_eliminar);
		
		/* Relleanamos datos de los campos */
		
		rellena_datos();
		
		
		/* AÑADIMOS ACTIONLISTENER A LOS BOTONES */
		
		/*Siguente */
		
		button_siguiente.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            alumno.siguiente(); /*llamamos método siguiente de la clase alumno */
	            try {
					rellena_datos();/*Volvemos a rellenar los datos de los campos con la posición actual del rs */
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         }
		});      
		
		
		/*Anterior */
		 
		button_anterior.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            alumno.anterior(); /*llamamos método anterior de la clase alumno */
	            try {
					rellena_datos();/*Volvemos a rellenar los datos de los campos con la posición actual del rs */
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         }
		});   
		
		/*Primera */
		
		button_primero.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            alumno.primera(); /*llamamos método primera de la clase alumno */
	            try {
					rellena_datos();/*Volvemos a rellenar los datos de los campos con la posición actual del rs */
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         }
		}); 
		
		/*Último */
		
		button_ultimo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            alumno.ultima(); /*llamamos método ultima de la clase alumno */
	            try {
					rellena_datos();/*Volvemos a rellenar los datos de los campos con la posición actual del rs */
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         }
		});   
		
		
		/*Nuevo */
		
		button_nuevo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					limpiacampos(); // Limpiamos campos
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            alumno.nuevo();// Llamamos al método nuevo de la clase alumno
	         }
		});   
		
		/*Cancelar */
		
		button_cancelar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            alumno.cancela(); /*llamamos método cancela de la clase alumno */
	            try {
					rellena_datos(); // Rellenamos
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	         }
		});   
		
		/*Borrar */
		
		button_eliminar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	            try {
					alumno.borraralumno(); /*llamamos método borraralumno() de la clase alumno */
				} catch (MySQLIntegrityConstraintViolationException e2) {
					// TODO Auto-generated catch block
				}	
	            try {
					rellena_datos();/*Volvemos a rellenar los datos de los campos con la posición actual del rs */
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         }
		});   
		
		/*Aceptar */
		
		button_aceptar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					try {
						recibe_datos();//Recibimos los datos de los textfield
		// Si la etiqueta de posición muestra el texto "Nueva" significa que estamos en la posción de insercción
		// por lo que llamaremos al método insertar, de lo contario llamamos al método actualizar.				
						if (label_posicion.getText().equals("Nueva")){
							alumno.insertar();      
						}else{
						    alumno.actualiza();
						}
			            rellena_datos(); // Rellenamos campos
					} catch (SQLException | ParseException e1) { // Capturamos excepiones y mostranis diálogo
						JOptionPane.showMessageDialog(frame, "No se han guardado los cambios en la BD");
						try {
							if (label_posicion.getText().equals("Nueva")){
								limpiacampos();// Si estamos en la pos de insercción volvemos a limpiar el formulario
							}else{
								rellena_datos();// Si no volvemos a rellenar el formulario con el alumno
							}					// de la posición donde nos encontramos
						} catch (SQLException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
			}

		});   
		
	}
}
