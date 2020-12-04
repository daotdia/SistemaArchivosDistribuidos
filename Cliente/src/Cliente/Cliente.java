package Cliente;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class Cliente {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor;
	private static int port = 2100;
	
	public static void main (String [] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		ServicioDiscoClienteInterface sdc;
		
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("rmi://"+ ip + ":2323/sg_remoto/1");
		
		try {
			sdc = new ServicioDiscoClienteImpl();
			port = servicio_gestor.getPortCliente();
			Remote sdc_remoto = UnicastRemoteObject.exportObject(sdc, port);
			registry.rebind("rmi://"+ ip + ":3434/sdc_remoto/" + port , sdc_remoto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			sdc = (ServicioDiscoClienteInterface) registry.lookup("rmi://"+ ip + ":3434/sdc_remoto/" + port);	
		}
		
		
		
		String nombre = args[0];
		boolean salir_archivos = false;
		Scanner in = new Scanner(System.in);
		if(servicio_autenticacion.comprobarCliente(nombre)) {
			System.out.println("");
			System.out.println("Se encuentra en la sesión del usuario " + args[0]);
			System.out.println("");
			
			while(!salir_archivos) {
				System.out.println("Elige la opción de gestión de archivos que considere");
				System.out.println("1. Subir Archivo");
				System.out.println("2. Bajar Archivo");
				System.out.println("3. Borrar Archivo");
				System.out.println("4. Compartir Fichero");
				System.out.println("5. Listar tus ficheros");
				System.out.println("6. Listar clientes del sistema");
				System.out.println("7. Cerrar Sesion y salir");
				System.out.println("8. Cerrar sesión, eliminar perfil y salir");
				int opcion_gestion = -1;
				try {
					opcion_gestion = in.nextInt();
				}
				catch (Exception e) {
					opcion_gestion = 1000;
				}
				in.nextLine();
				String path = null;
				String nombre_fichero = null;
				switch(opcion_gestion) {
				case 1:
					//Subir fihchero de path indicado.
					System.out.println("Indique el path del archivo a subir");
					path = in.nextLine();
					System.out.println("Indique el nombre del archivo");
					nombre_fichero = in.nextLine();
					try {
						servicio_gestor.subirFichero(nombre, nombre_fichero, path);
					}
					catch (CustomExceptions.NoHayRepositoriosLibres e){
						System.out.println("No hay repositorios libres para el usuario inicialice nuevos");
						break;
					}
					break;
				case 2: 
					//Bajar ficheor en path indicado
					System.out.println("Indique el nombre del archivo a bajar");
					nombre_fichero = in.nextLine();
					System.out.println("Indique el path local donde bajar el archivo");
					path = in.nextLine();		
					try {
					servicio_gestor.bajarFichero(nombre, nombre_fichero, path, port);
					} catch (RuntimeException e) {
						System.out.println("Fichero con nombre " + nombre_fichero + " no encontrado, compruebelo y pruebe otra vez");
						break;
					}
					break;
				case 3:
					System.out.println("Indique el nombre del archivo a borrar");
					nombre_fichero = in.nextLine();
					try {
					servicio_gestor.borrarFichero(nombre, nombre_fichero);
					}catch (CustomExceptions.PermisoDenegado e){
						System.out.println("No tienes permiso para borrar el archivo " + nombre_fichero + " no eres el propietario");
						break;
					} 
					catch(Exception e) {
						System.out.println("Archivo " + nombre_fichero + " no eliminado, asegurase de que el nombre del archivo sea correcto");
						break;
					}
					
					break;
				case 4:
					System.out.println("Indique el nombre del archivo que quiere compartir, debe de ser propietario");
					nombre_fichero = in.nextLine();
					System.out.println("Indique el nombre del usuario con el que compartir el archivo");
					String nombre_destinatario = in.nextLine();
					try {
					servicio_gestor.compartirFichero(nombre, nombre_destinatario, nombre_fichero);
					} catch (CustomExceptions.PermisoDenegado e) {
						System.out.println("No es el propietario del archivo " + nombre_fichero + ", no puede compartirlo");
					}
					break;
				case 5:
					System.out.println("Los ficheros del cliente " + nombre + " son:");
					System.out.println(Arrays.toString(servicio_gestor.getListaFicheros(nombre).toArray()));
					break;
				case 6:
					System.out.println("Los clientes registrados en el sistema actualmente son:");
					System.out.println(Arrays.toString(servicio_gestor.getListaClientesSistema().toArray()));
					break;
				case 7:
					servicio_autenticacion.cerrarSesion(nombre, 0);
					System.out.println("Sesion cerrada del cliente " + nombre);
					System.out.println("Gracias por usar el sistema, vuelve pronto!");
					salir_archivos = true;
					break;
				case 8:
					servicio_autenticacion.deleteObjeto(nombre, 0);
					servicio_autenticacion.cerrarSesion(nombre, 0);
					System.out.println("Cliente " + nombre + " eliminado");
					System.out.println("Gracias por usar el sistema, vuelve pronto!");
					salir_archivos = true;
					break;
				default:
					System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-9).");
					break;
				}
			}
		} else {
			System.out.println("Este usuario no está conectado, conectelo antes en el menu de autenticación");
		}
		registry.unbind("rmi://"+ ip + ":3434/sdc_remoto/" + port);
		try {
			UnicastRemoteObject.unexportObject(sdc, true);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			in.close();
		}
	}
	
	public static int getPort() {
		return port + 1;
	}
}
