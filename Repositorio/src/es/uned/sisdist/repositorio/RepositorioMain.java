package es.uned.sisdist.repositorio;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class RepositorioMain {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	
	public void main (String [] args) throws RemoteException, NotBoundException {
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre_repositorio = "";
		Scanner in = new Scanner(System.in);
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("autenticacion_remota");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("sg_remoto");
		
		System.out.println("Elige la opción de autenticacion");
		System.out.println("1. Registrar repositorio");
		System.out.println("2. Iniciar sesion repositorio");
		System.out.println("3. Exit");
		opcion = in.nextInt();
		in.nextLine();
		while(opcion != 3) {
			switch(opcion) {
				case 1:
					System.out.println("Indique el nombre del repositorio");
					String nombre = in.nextLine();					
					servicio_autenticacion.registrarObjeto(nombre, 1);
					break;
				case 2: 
					System.out.println("Indique el nombre del repositorio");
					nombre_repositorio = in.nextLine();					
					servicio_autenticacion.iniciarSesion(nombre_repositorio, 1);
					break;
				case 3:
					System.out.println("Gracias por utilizar el sistema, vuelva pronto!");
					opcion = 3;
					break;
			}
			if(opcion == 2 && servicio_autenticacion.comprobarCliente(nombre_repositorio)) {
				while(opcion != 3 || opcion != 4 || opcion != 5) {
					System.out.println("Elige la operación de repositorio");
					System.out.println("1. Listar clientes");
					System.out.println("2. Listar ficheros del cliente");
					System.out.println("3. Cerrar Sesion");
					System.out.println("4. Eliminar Repositorio");
					System.out.println("5. Cerrar sesion y salir");
					opcion = in.nextInt();
					in.nextLine();
					switch(opcion) {
						case 1: 
							System.out.println("Los clientes con archivos en este repositorio actualmente son:");
							System.out.println(Arrays.toString(servicio_gestor.getListaClientesRepositorio(nombre_repositorio).toArray()));	
							break;
						case 2:
							System.out.println("Indique el nombre del cliente:");
							String nombre_cliente = in.nextLine();
							System.out.println("Los archivos del cliente en este repositorio son:");
							System.out.println(Arrays.toString(servicio_gestor.getFicherosClienteRepositorio(nombre_cliente, nombre_repositorio).toArray()));
						case 3:
							servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
							System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
							opcion = 4;
						case 4:
							servicio_autenticacion.deleteObjeto(nombre_repositorio, 1);
							System.out.println("Repositorio " + nombre_repositorio + " eliminado");
							opcion = 5;
						case 5:
							servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
							System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
							opcion = 3;
							System.out.println("Gracias por usar el sistema, vuelve pronto!");
					}	
				}
			}	
		}
	}
}
