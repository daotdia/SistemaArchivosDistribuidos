import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class Cliente {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	
	public static void main(String[] args) throws Exception {
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre = "";
		Scanner in = new Scanner(System.in);
		boolean salir = false;
		
		ServicioDiscoClienteInterface sdc = new ServicioDiscoClienteImpl();
		Remote sdc_remoto = UnicastRemoteObject.exportObject(sdc, 3434);
		registry.rebind("sdc_remoto", sdc_remoto);
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("autenticacion_remota");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("sg_remoto");
		
		while(!salir) {
			System.out.println("Elige la opción de autenticacion");
			System.out.println("1. Registrar usuario");
			System.out.println("2. Iniciar sesion usuario");
			System.out.println("3. Exit");
			opcion = in.nextInt();
			in.nextLine();
			switch(opcion) {
				case 1:
					System.out.println("Indique el nombre del usuario");
					nombre = in.nextLine();					
					boolean registrado = servicio_autenticacion.registrarObjeto(nombre, 0);
					if(registrado) {
						System.out.println("Usuario con nombre " + nombre + " registrado");
					}
					else
						System.out.println("Ya existe un cliente registrado con nombre " + nombre + " intentelo con otro nombre");
					System.out.println("");
					break;
				case 2: 
					try {
					System.out.println("Indique el nombre del usuario");
					nombre = in.nextLine();					
					servicio_autenticacion.iniciarSesion(nombre, 0);
					System.out.println("Usuario con nombre " + nombre + " conectado");
					System.out.println("");
					break;
					}
					catch (RuntimeException e) {
						System.out.println("Cliente no regitrado, registrelo antes de intentar iniciar sesión");
						System.out.println("");
						break;
					}
					catch (Exception e) {
						System.out.println("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
						System.out.println("");
						break;
					}
				case 3:
					System.out.println("Gracias por usar el sistema, vuelve pronto!");
					salir = true;
					break;
				default:
					System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-3).");
					break;
			}
			if(opcion == 2 && servicio_autenticacion.comprobarCliente(nombre)) {
				while(!salir) {
					System.out.println("Elige la opción de gestión de archivos que considere");
					System.out.println("1. Subir Archivo");
					System.out.println("2. Bajar Archivo");
					System.out.println("3. Borrar Archivo");
					System.out.println("4. Compartir Fichero");
					System.out.println("5. Listar tus ficheros");
					System.out.println("6. Listar clientes del sistema");
					System.out.println("7. Cerrar Sesion");
					System.out.println("8. Eliminar perfil");
					System.out.println("9. Cerrar sesion y salir");
					int opcion_gestion = in.nextInt();
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
						salir = true;
						break;
					case 8:
						servicio_autenticacion.deleteObjeto(nombre, 0);
						System.out.println("Cliente " + nombre + " eliminado");
						salir = true;
						break;
					case 9:
						servicio_autenticacion.cerrarSesion(nombre, 0);
						System.out.println("Sesion cerrada del cliente " + nombre);
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						salir = true;
						break;
					default:
						System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-9).");
						break;
					}
				}
			}	
		}
		registry.unbind("sdc_remoto");
		UnicastRemoteObject.unexportObject(sdc, true);
		in.close();
	}
}