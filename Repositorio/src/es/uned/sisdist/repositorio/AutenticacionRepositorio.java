/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que se encarga de registrar y autenticar un repositorio, cuando se loguea
 * un repositorio inicializa su actividad.
 * 
 * */
package es.uned.sisdist.repositorio;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioAutenticacionInterface;

public class AutenticacionRepositorio {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	public static String ip;
	
	public static void main (String [] args) throws Exception {
		//Obtengo la ip local.
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		//Obtengo el registro del sistema remoto.
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		int opcion = -1;
		String nombre_repositorio = "";
		Scanner in = new Scanner(System.in);
		boolean salir = false;
		
		//Obtengo el servicio de autenticación remoto.
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		
		//Mientras no se salga del sistema o se loguee un repositorio.
		while(!salir) {
			System.out.println("Elige la opción de autenticacion");
			System.out.println("1. Registrar repositorio");
			System.out.println("2. Iniciar sesion repositorio");
			System.out.println("3. Exit");
			
			//Obtenfo la opción elegida por el cliente.
			try {
			opcion = in.nextInt();
			}
			catch (Exception e) {
				opcion = 1000;
			}
			if(in.hasNextLine()) {
				in.nextLine();
			}
			
			switch(opcion) {
				case 1:
					//Registro repositorio siempre y cuando no esté ya registrado.
					System.out.println("Indique el nombre del repositorio");
					String nombre = in.nextLine();					
					boolean registrado = servicio_autenticacion.registrarObjeto(nombre, 1);
					if(!registrado) {
						System.out.println("Ya existe un repositorio registrado con nombre " + nombre + " pruebe con otro nombre");
					}
					else
						System.out.println("Repositorio registrado con nombre " + nombre);
					System.out.println("");
					break;
				case 2: 
					try {
						//Logueo repositorio.
						System.out.println("Indique el nombre del repositorio");
						nombre_repositorio = in.nextLine();					
						servicio_autenticacion.iniciarSesion(nombre_repositorio, 1);
						System.out.println("Repositorio conectado con nombre " + nombre_repositorio);
						System.out.println("");
					}
					catch (RuntimeException e){
						System.out.println("Repositorio no regitrado, registrelo antes de intentar iniciar sesión");
						System.out.println("");
						break;
					}
					//Inicializo el menu de actividad del repositorio logueado, paso como argumento el nombre del repositorio.
					String [] array = new String[1];
					array[0] = nombre_repositorio;
					Repositorio.main(array);
					salir = true;
					break;
				case 3:
					System.out.println("");
					System.out.println("Gracias por utilizar el sistema, vuelva pronto!");
					salir = true;
					break;
				default:
					System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-3).");
					break;
			}
		}
		//Cierro el escaner.
		in.close();
	}
}
