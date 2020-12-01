package es.uned.sisdist.repositorio;

import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class AutenticacionRepositorio {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	public static String ip;
	
	public static void main (String [] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre_repositorio = "";
		Scanner in = new Scanner(System.in);
		boolean salir = false;
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("rmi://"+ ip + ":2323/sg_remoto/1");
		
		while(!salir) {
			System.out.println("Elige la opción de autenticacion");
			System.out.println("1. Registrar repositorio");
			System.out.println("2. Iniciar sesion repositorio");
			System.out.println("3. Exit");
			try {
			opcion = in.nextInt();
			}
			catch (Exception e) {
				opcion = 1000;
			}
			in.nextLine();
			switch(opcion) {
				case 1:
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
					String [] array = new String[1];
					array[0] = nombre_repositorio;
					Repositorio.main(array);
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
		in.close();
	}
}
