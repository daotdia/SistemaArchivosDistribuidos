package es.uned.sisdist.repositorio;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioGestorInterface;

public class RepositorioMain {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	private static ServicioGestorInterface servicio_gestor; 
	
	public static void main (String [] args) throws Exception {
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre_repositorio = "";
		Scanner in = new Scanner(System.in);
		boolean salir = false;
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("autenticacion_remota");
		servicio_gestor = (ServicioGestorInterface) registry.lookup("sg_remoto");
		
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
			if(opcion == 2 && servicio_autenticacion.comprobarRepositorio(nombre_repositorio)) {
				while(!salir && opcion != 3) {
					System.out.println("Elige la operación de repositorio");
					System.out.println("1. Listar clientes");
					System.out.println("2. Listar ficheros del cliente");
					System.out.println("3. Cerrar Sesion");
					System.out.println("4. Cerrar sesion y salir");
					System.out.println("5. Eliminar Repositorio y salir");
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
							System.out.println("");
							break;
						case 4:
							servicio_autenticacion.cerrarSesion(nombre_repositorio, 1);
							System.out.println("Sesion cerrada del repositorio " + nombre_repositorio);
							salir = true;
							System.out.println("Gracias por usar el sistema, vuelve pronto!");
							System.out.println("");
							break;
						case 5:
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
							}
							catch (CustomExceptions.ObjetoNoRegistrado e) {
								System.out.println("No se puede eliminar un objeto no registrado");
							}
						default:
							System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-5).");
							System.out.println("");
							break;		
					}	
				}
			}	
		}
		in.close();
	}
}
