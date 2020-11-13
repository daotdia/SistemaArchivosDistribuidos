import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServidorInterface;

public class Cliente {
	private static ServidorInterface servidor;
	
	public static void main(String[] args) throws Exception {
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre = "";
		Scanner in = new Scanner(System.in);
		
		ServicioDiscoClienteInterface sdc = new ServicioDiscoClienteImpl();
		Remote sdc_remoto = UnicastRemoteObject.exportObject(sdc, 3434);
		registry.rebind("sdc_remoto", sdc_remoto);
		
		ServidorInterface servidor = (ServidorInterface) registry.lookup("servidor_remoto");
		
		while(opcion < 3) {
			switch(args[0]) {
				case "cliente":
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
							servidor.menu_inicial(nombre, 0, opcion-1);
							break;
						case 2: 
							System.out.println("Indique el nombre del usuario");
							nombre = in.nextLine();					
							servidor.menu_inicial(nombre, 0, opcion-1);
							break;
						case 3:
							break;
					}
					break;
					case "repositorio":
						System.out.println("Elige la opción de autenticacion");
						System.out.println("1. Registrar repositorio");
						System.out.println("2. Iniciar sesion repositorio");
						System.out.println("3. Exit");
						opcion = in.nextInt();
						in.nextLine();
						switch(opcion) {
							case 1:
								System.out.println("Indique el nombre del repositorio");
								nombre = in.nextLine();					
								servidor.menu_inicial(nombre, 1, opcion-1);
								break;
							case 2: 
								System.out.println("Indique el nombre del repositorio");
								nombre = in.nextLine();					
								servidor.menu_inicial(nombre, 1, opcion-1);
								break;
							case 3:
								break;
						}
						break;
				}
				if(args[0].equals("cliente") && opcion == 2 && servidor.comprobarCliente(nombre)) {
					while(opcion != 6 || opcion != 7) {
						System.out.println("Elige la opción de gestión de archivos que considere");
						System.out.println("1. Subir Archivo");
						System.out.println("2. Bajar Archivo");
						System.out.println("3. Compartir Fichero");
						System.out.println("4. Listar tus ficheros");
						System.out.println("5. Listar clientes del sistema");
						System.out.println("6. Cerrar Sesion");
						System.out.println("7. Eliminar perfil");
						opcion = in.nextInt();
						in.nextLine();
						String path = null;
						String nombre_fichero = null;
						switch(opcion) {
						case 1:
							//Subir fihchero en path indicado.
							System.out.println("Indique el path del archivo");
							path = in.nextLine();
							System.out.println("Indique el nombre del archivo");
							nombre_fichero = in.nextLine();
							servidor.gestion_archivos(nombre, nombre_fichero, path, 0);
							System.out.println("Indique el nombre del archivo");
							break;
						case 2: 
							System.out.println("Indique el path local donde bajar el archivo");
							nombre = in.nextLine();					
							servidor.menu_inicial(nombre, 1, opcion-1);
							break;
						case 6:
							opcion = 6;
							break;
						}
					}
				}
					
			}
			
		}
	}
