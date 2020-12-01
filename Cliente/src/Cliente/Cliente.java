package Cliente;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class Cliente {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor;
	
	public static void main (String [] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("rmi://"+ ip + ":2323/sg_remoto/1");
		
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
					servicio_gestor.subirFichero(nombre, nombre_fichero, path);
					break;
				case 2: 
					//Bajar ficheor en path indicado
					System.out.println("Indique el nombre del archivo a bajar");
					nombre_fichero = in.nextLine();
					System.out.println("Indique el path local donde bajar el archivo");
					path = in.nextLine();					
					servicio_gestor.bajarFichero(nombre, nombre_fichero, path);
					break;
				case 3:
					System.out.println("Indique el nombre del archivo a borrar");
					nombre_fichero = in.nextLine();
					servicio_gestor.borrarFichero(nombre, nombre_fichero);
					break;
				case 4:
					break;
				case 5:
					System.out.println("Los ficheros del cliente " + nombre + " son:");
					System.out.println(Arrays.toString(servicio_gestor.getListaFicheros(nombre).toArray()));
					break;
				case 6:
					System.out.println("Los clientes con archivos en el sistema actualmente son:");
					System.out.println(Arrays.toString(servicio_gestor.getListaClientesSistema().toArray()));
					break;
				case 7:
					servicio_autenticacion.cerrarSesion(nombre, 0);
					System.out.println("Sesion cerrada del cliente " + nombre);
					salir_archivos = true;
					break;
				case 8:
					servicio_autenticacion.cerrarSesion(nombre, 0);
					servicio_autenticacion.deleteObjeto(nombre, 0);
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
		in.close();
	}
}
