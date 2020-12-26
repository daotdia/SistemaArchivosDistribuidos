/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que se encarga de implementar la funcionalidad del repositorio una vez está logueado.
 * 
 * */
package es.uned.sisdist.repositorio;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class Repositorio {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	private static int port;
	private static ServicioSrOperadorInterface sso;
	private static ServicioClOperadorInterface sco;
	
	public static void main (String [] args) throws Exception {
		System.out.println("");
		System.out.println("Se encuentra en la sesión del usuario " + args[0]);
		System.out.println("");
		
		//Obtengo el registro del sistema distribuido.
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		
		String nombre_repositorio = args[0];
		Scanner in = new Scanner(System.in);
		
		//Obtengo los servicios de autenticación y gestor del Servidor.
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("rmi://"+ ip + ":2323/sg_remoto/1");
		
		
		try {
			//Trato de bindar el servicio SSO del repositorio.
			port = servicio_gestor.getPortRepositorio(nombre_repositorio);
			sso = new ServicioSrOperadorImpl();
			ServicioSrOperadorInterface sso_remoto = (ServicioSrOperadorInterface) UnicastRemoteObject.exportObject(sso, port);
			registry.rebind("rmi://"+ ip + ":5555/sso_remoto/" + port, sso_remoto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			//Trato de bindar el servicio SCO del repositorio.
			sco = new ServicioClOperadorImpl();
			ServicioClOperadorInterface sco_remoto = (ServicioClOperadorInterface) UnicastRemoteObject.exportObject(sco, port + 1);
			registry.rebind("rmi://"+ ip + ":2222/sco_remoto/" + (port + 1), sco_remoto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		boolean salir = false;
		int opcion = -1000;
		
		//Si el repositorio está logieado.
		if(servicio_autenticacion.comprobarRepositorio(nombre_repositorio)) {
			while(!salir) {
				System.out.println("Elige la operación de repositorio");
				System.out.println("1. Listar clientes");
				System.out.println("2. Listar ficheros del cliente");
				System.out.println("3. Cerrar sesion y salir");
				System.out.println("4. Eliminar Repositorio, cerrar sesión y salir");
				
				//Obtengo la opción elegida por el usuario.
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
							//Listo los clientes del repositorio.
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
							//listo los ficheros de un cliente determinado.
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
						//Cierro la sesión del repositorio.
						servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
						System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
						salir = true;
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						System.out.println("");
						break;
					case 4:
						try {
						//Elimino el repositorio del sistema, para ello primero cierro su sesión.
						servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
						System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
						servicio_autenticacion.deleteObjeto(nombre_repositorio, 1);
						System.out.println("Repositorio " + nombre_repositorio + " eliminado");
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						salir = true;
						System.out.println("");
						break;
						} catch (CustomExceptions.RepositorioTodaviaNoUtilizado e) {
							//En el caso de que no se haya utilizado el repositorio, su eliminaación no afecta al sistema de archivos.
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
		
		//Unbind de los servicios propiedad del repositorio.
		registry.unbind("rmi://"+ ip + ":2222/sco_remoto/" + (port + 1));
		UnicastRemoteObject.unexportObject(sso, true);
		registry.unbind("rmi://"+ ip + ":5555/sso_remoto/" + port);
		UnicastRemoteObject.unexportObject(sco, true);
		//Cierro el escaner.
		in.close();
	}
}
