package es.uned.sisdist.repositorio;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class Repositorio {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	
	public static void main (String [] args) throws RemoteException, NotBoundException, UnknownHostException {
		System.out.println("");
		System.out.println("Se encuentra en la sesión del usuario " + args[0]);
		System.out.println("");
		
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("rmi://"+ ip + ":2323/sg_remoto/1");
		
		String nombre_repositorio = args[0];
		Scanner in = new Scanner(System.in);
		
		boolean salir = false;
		int opcion = -1000;
		
		if(servicio_autenticacion.comprobarRepositorio(nombre_repositorio)) {
			while(!salir) {
				System.out.println("Elige la operación de repositorio");
				System.out.println("1. Listar clientes");
				System.out.println("2. Listar ficheros del cliente");
				System.out.println("3. Cerrar sesion y salir");
				System.out.println("4. Eliminar Repositorio, cerrar sesión y salir");
				try {
					opcion = in.nextInt();
				}
				catch (Exception e) {
					opcion = 1000;
				}
				in.nextLine();
				switch(opcion) {
					case 1: 
						try {
							System.out.println("Los clientes con archivos en este repositorio actualmente son:");
							System.out.println(Arrays.toString(servicio_gestor.getListaClientesRepositorio(nombre_repositorio).toArray()));	
							System.out.println("");
							break;
						}
						catch (NullPointerException e) {
							System.out.println("Este repositorio todavía no tiene clientes");
							System.out.println("");
							break;
						}
					case 2:
						try {
							System.out.println("Indique el nombre del cliente:");
							String nombre_cliente = in.nextLine();
							System.out.println("Los archivos del cliente en este repositorio son:");
							System.out.println(Arrays.toString(servicio_gestor.getFicherosClienteRepositorio(nombre_cliente, nombre_repositorio).toArray()));
							System.out.println("");
							break;
						}
						catch (NullPointerException e){
							System.out.println("Este cliente todavía no tiene archivos en este repositorio");
							System.out.println("");
							break;
						}
					case 3:
						servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
						System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
						salir = true;
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						System.out.println("");
						break;
					case 4:
						try {
						servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
						System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
						servicio_autenticacion.deleteObjeto(nombre_repositorio, 1);
						System.out.println("Repositorio " + nombre_repositorio + " eliminado");
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						salir = true;
						System.out.println("");
						break;
						} catch (CustomExceptions.RepositorioTodaviaNoUtilizado e) {
							System.out.println("Repositorio eliminado antes de haber sido utilizado, no se han producido cambios en el sistema");
							salir = true;
							break;
						}
						catch (CustomExceptions.ObjetoNoRegistrado e) {
							System.out.println("No se puede eliminar un objeto no registrado");
							salir = true;
							break;
						}
					default:
						System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-4).");
						System.out.println("");
						break;	
				}
			}	
		} else {
			System.out.println("Este repositorio no está conectado, conectelo antes en el menu de autenticación");
		}
		in.close();
	}
}
