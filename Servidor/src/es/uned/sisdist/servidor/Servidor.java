/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el funcionamiento de un servidor de un sistema de archivos distribuidos. 
 * Carga los servciios necesarios para gestionar el funcionamiento de dicho sistema.
 * 
 * */

package es.uned.sisdist.servidor;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.SourcePath;

//Implementa Remote de JavaRMI
public class Servidor implements Remote{
	
	//Varuiables estáticas necesarias.
	private static ServicioDatosInterface servicio_datos;
	private static Registry registry;
	public static String ip;
	
	//Método principal Servidor. 
	public static void main (String[] Args) throws Exception {
		//Obtengo la IP local de la máquina dónde se ejecuta el Servidor, servirá para especificar las URL de los servicios remotos.
		SourcePath.setCodebase(ServicioAutenticacionInterface.class);
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		//Creo el registro del sistema remoto RMI.
		registry = LocateRegistry.createRegistry(7777);
		
		//Inicializo la opción de servidor y el scanner para comunicarse con el Servidor.
		int opcion = -1;
		Scanner in = new Scanner(System.in);
		
		//Iniicalizo y creo los objetos remotos.
		//Creación y bind al registo del objeto remoto ServicioDatos.
		ServicioDatosInterface datos = new ServicioDatosImpl();
		ServicioDatosInterface datos_remotos = (ServicioDatosInterface) UnicastRemoteObject.exportObject(datos,8888);
		registry.rebind("rmi://"+ ip + ":8888/datos_remotos/1", datos_remotos);
		
		//Creación y bind al registo del objeto remoto ServicioGestor.
		ServicioGestorInterface sg = new ServicioGestorImpl();
		ServicioGestorInterface sg_remoto = (ServicioGestorInterface) UnicastRemoteObject.exportObject(sg, 2323);
		registry.rebind("rmi://"+ ip + ":2323/sg_remoto/1", sg_remoto);
		
		//Creación y bind al registo del objeto remoto ServicioAutenticación.
		ServicioAutenticacionInterface autenticacion = new ServicioAutenticacionImpl();
		ServicioAutenticacionInterface autenticacion_remota = (ServicioAutenticacionInterface) UnicastRemoteObject.exportObject(autenticacion, 6666);
		registry.rebind("rmi://"+ ip + ":6666/autenticacion_remota/1", autenticacion_remota);
		
		System.out.println("Servidor remoto listo, con los servicios remotos listos");
		System.out.println("");
		
		//Obtengo el objeto remoto ServicioDatos que acabo de inicializar.
		servicio_datos = (ServicioDatosInterface) registry.lookup("rmi://"+ ip + ":8888/datos_remotos/1");
		
		//Bucle de funcionamiento del servidor, hay cuatro opciones: (1) listar clientes, (2) Listar repositorios, (3) Listar los repositorios de cada usuairo y (4) salir.
		while (opcion != 4) {
			System.out.println("-------------------------------------------");
			System.out.println("Elige la operación de servidor");
			System.out.println("1. Listar clientes registrados del sistema");
			System.out.println("2. Listar repositorios del sistema");
			System.out.println("3. Listar Repositorios de cada ususario");
			System.out.println("4. Salir");
		
			//Obtengo la opcíón elegida por el usuario, en el caso de que no sea un número la opción es la predeterminada (elección de opción errónea).
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
						System.out.println("");
						System.out.println("Los clientes registrados en el sistema son:");
						//Obtengo los clientes registrados del servicio de datos y los imprimo por pantalla.
						System.out.println(Arrays.toString(servicio_datos.getListaClientesRegistrados().toArray()));
						break;
					}
					catch (NullPointerException e){
						//En el caso de que la lista de clientes no esté inicializada; no hay clientes todavía registrados.
						System.out.println("No hay clientes registrados en el sistema todavía");
						System.out.println("");
						break;
					}
				case 2:
					try {
						System.out.println("");
						System.out.println("Los Repositorios registrados en el sistema son:");
						//Obtengo los repositorios registrados del servicio de datos y los imprimo por pantalla.
						System.out.println(Arrays.toString(servicio_datos.getListaRepositoriosRegistrados().toArray()));
						break;
					}
					catch (NullPointerException e){
						//En el caso de que la lista de repositorios no esté inicializada; no hay repositorios todavía registrados.
						System.out.println("No hay repositorios registrados en el sistema todavía");
						System.out.println("");
						break;
					}
				case 3:
					System.out.println("Los repositorios de cada usuario son: ");
					//Obtengo los repositorios de cada cliente.
					for(Map.Entry<String,Integer> entrada : servicio_datos.getListaClientesActivos().entrySet()) {
						//Obtengo los nombres de cada cliente.
						System.out.println(entrada.getKey() + ":");
						//Por cada cliente, obtengo sus repositorios. 
						for(Repositorio repo : servicio_datos.getRepositoriosUsuario(entrada.getKey())){
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
	
		//Cierro el escaner de entrada.
		in.close();
		
		// unbind de los servicios remotos utilizados en el sistema.
		registry.unbind("rmi://"+ ip + ":8888/datos_remotos/1");
		UnicastRemoteObject.unexportObject(datos, true);
		registry.unbind("rmi://"+ ip + ":6666/autenticacion_remota/1");
		UnicastRemoteObject.unexportObject(autenticacion, true);
		registry.unbind("rmi://"+ ip + ":2323/sg_remoto/1");
		UnicastRemoteObject.unexportObject(sg, true);
	}
}
