package es.uned.sisdist.servidor;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.SourcePath;

public class Servidor implements Remote{
	
	private static ServicioDatosInterface bd;
	private static Registry registry;
	
	public static void main (String[] Args) throws Exception{
		SourcePath.setCodebase(ServicioAutenticacionInterface.class);
		
		registry = LocateRegistry.createRegistry(7777);
		
		int opcion = -1;
		Scanner in = new Scanner(System.in);
		
		ServicioDatosInterface datos = new ServicioDatosImpl();
		ServicioDatosInterface datos_remotos = (ServicioDatosInterface) UnicastRemoteObject.exportObject(datos,8888);
		registry.rebind("datos_remotos", datos_remotos);
		
		ServicioSrOperadorInterface sso = new ServicioSrOperadorImpl();
		ServicioSrOperadorInterface sso_remoto = (ServicioSrOperadorInterface) UnicastRemoteObject.exportObject(sso, 5555);
		registry.rebind("sso_remoto", sso_remoto);
		
		ServicioClOperadorInterface sco = new ServicioClOperadorImpl();
		ServicioClOperadorInterface sco_remoto = (ServicioClOperadorInterface) UnicastRemoteObject.exportObject(sco, 2222);
		registry.rebind("sco_remoto", sco_remoto);
		
		ServicioGestorInterface sg = new ServicioGestorImpl();
		ServicioGestorInterface sg_remoto = (ServicioGestorInterface) UnicastRemoteObject.exportObject(sg, 2323);
		registry.rebind("sg_remoto", sg_remoto);
		
		ServicioAutenticacionInterface autenticacion = new ServicioAutenticacionImpl();
		ServicioAutenticacionInterface autenticacion_remota = (ServicioAutenticacionInterface) UnicastRemoteObject.exportObject(autenticacion, 6666);
		registry.rebind("autenticacion_remota", autenticacion_remota);
		
		System.out.println("Servidor remoto listo, con los servicios remotos listos");
		System.out.println("");
		
		bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
		
		while (opcion != 3) {
			System.out.println("-------------------------------------------");
			System.out.println("Elige la operación de repositorio");
			System.out.println("1. Listar clientes registrados del sistema");
			System.out.println("2. Listar repositorios del sistema");
			System.out.println("3. Salir");
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
					System.out.println("");
					System.out.println("Gracias por utilizar el sistema, vuelva pronto!");
					System.out.println("");
					opcion = 3;
					break;
				default:
					System.out.println("");
					System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-3).");
					System.out.println("");
					break;
			}
		}
	
		in.close();
		
		registry.unbind("datos_remotos");
		UnicastRemoteObject.unexportObject(datos, true);
		registry.unbind("autenticacion_remota");
		UnicastRemoteObject.unexportObject(autenticacion, true);
		registry.unbind("sso_remoto");
		UnicastRemoteObject.unexportObject(sso, true);
		registry.unbind("sg_remoto");
		UnicastRemoteObject.unexportObject(sg, true);
		registry.unbind("sco_remoto");
		UnicastRemoteObject.unexportObject(sco, true);
	}
}
