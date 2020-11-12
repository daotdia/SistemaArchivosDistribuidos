import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import es.uned.sisdist.common.ServidorInterface;

public class Cliente {
	private static ServidorInterface servidor;
	
	public static void main(String[] args) throws Exception {
		Registry registry =  LocateRegistry.getRegistry(7777);
		int opcion = -1;
		String nombre;
		Scanner in = new Scanner(System.in);
		ServidorInterface servidor = (ServidorInterface) registry.lookup("servidor_remoto");
		
		while(opcion != 3) {
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
					
			}
			
		}
	}
