package es.uned.sisdist.servidor;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.SourcePath;

public class Servidor implements Remote{
	
	private static ServicioDatosInterface bd;
	private static Registry registry;
	public static String ip;
	
	public static void main (String[] Args) throws Exception{
		SourcePath.setCodebase(ServicioAutenticacionInterface.class);
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		registry = LocateRegistry.createRegistry(7777);
		
		int opcion = -1;
		Scanner in = new Scanner(System.in);
		
		ServicioDatosInterface datos = new ServicioDatosImpl();
		ServicioDatosInterface datos_remotos = (ServicioDatosInterface) UnicastRemoteObject.exportObject(datos,8888);
		registry.rebind("rmi://"+ ip + ":8888/datos_remotos/1", datos_remotos);
		
		ServicioGestorInterface sg = new ServicioGestorImpl();
		ServicioGestorInterface sg_remoto = (ServicioGestorInterface) UnicastRemoteObject.exportObject(sg, 2323);
		registry.rebind("rmi://"+ ip + ":2323/sg_remoto/1", sg_remoto);
		
		ServicioAutenticacionInterface autenticacion = new ServicioAutenticacionImpl();
		ServicioAutenticacionInterface autenticacion_remota = (ServicioAutenticacionInterface) UnicastRemoteObject.exportObject(autenticacion, 6666);
		registry.rebind("rmi://"+ ip + ":6666/autenticacion_remota/1", autenticacion_remota);
		
		System.out.println("Servidor remoto listo, con los servicios remotos listos");
		System.out.println("");
		
		bd = (ServicioDatosInterface) registry.lookup("rmi://"+ ip + ":8888/datos_remotos/1");
		
		while (opcion != 4) {
			System.out.println("-------------------------------------------");
			System.out.println("Elige la operación de servidor");
			System.out.println("1. Listar clientes registrados del sistema");
			System.out.println("2. Listar repositorios del sistema");
			System.out.println("3. Listar Repositorios de cada ususario");
			System.out.println("4. Salir");
		
			opcion = in.nextInt();
			in.nextLine();
			
			switch(opcion) {
				case 1:
					try {
						System.out.println("");
						System.out.println("Los clientes registrados en el sistema son:");
						System.out.println(Arrays.toString(bd.getListaClientesRegistrados().toArray()));
						break;
					}
					catch (NullPointerException e){
						System.out.println("No hay clientes registrados en el sistema todavía");
						System.out.println("");
						break;
					}
				case 2:
					try {
						System.out.println("");
						System.out.println("Los Repositorios registrados en el sistema son:");
						System.out.println(Arrays.toString(bd.getListaRepositoriosRegistrados().toArray()));
						break;
					}
					catch (NullPointerException e){
						System.out.println("No hay repositorios registrados en el sistema todavía");
						System.out.println("");
						break;
					}
				case 3:
					System.out.println("Los repositorios de cada usuario son: ");
					for(Map.Entry<String,Integer> entrada : bd.getListaClientesActivos().entrySet()) {
						System.out.println(entrada.getKey() + ":");
						for(Repositorio repo : bd.getRepositoriosUsuario(entrada.getKey())){
							System.out.print(repo.getNombre() + "    ");
						}
						System.out.println("");
					}
					break;
				case 4: 
					System.out.println("");
					System.out.println("Gracias por utilizar el sistema, vuelva pronto!");
					System.out.println("");
					opcion = 4;
					break;
				default:
					System.out.println("");
					System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-3).");
					System.out.println("");
					break;
			}
		}
	
		in.close();
		
		registry.unbind("rmi://"+ ip + ":8888/datos_remotos/1");
		UnicastRemoteObject.unexportObject(datos, true);
		registry.unbind("rmi://"+ ip + ":6666/autenticacion_remota/1");
		UnicastRemoteObject.unexportObject(autenticacion, true);
		registry.unbind("rmi://"+ ip + ":2323/sg_remoto/1");
		UnicastRemoteObject.unexportObject(sg, true);

	}
}
