import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;

public class AutenticacionCliente {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	public static String ip;
	
	public static void main(String[] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		int opcion = -1;
		String nombre = "";
		
		Scanner in = new Scanner(System.in);
		boolean salir_autenticacion = false;
		
		ServicioDiscoClienteInterface sdc = new ServicioDiscoClienteImpl();
		Remote sdc_remoto = UnicastRemoteObject.exportObject(sdc, 3434);
		registry.rebind("rmi://"+ ip + ":3434/sdc_remoto/1", sdc_remoto);
		
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		
			while(!salir_autenticacion) {
				System.out.println("Elige la opción de autenticacion");
				System.out.println("1. Registrar usuario");
				System.out.println("2. Iniciar sesion usuario");
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
						}
						catch (CustomExceptions.NoHayRepositoriosLibres e) {
							throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
						}
						catch (CustomExceptions.ObjetoNoRegistrado e) {
							throw new CustomExceptions.ObjetoNoRegistrado("Usuario no registrado");
						}
						catch (CustomExceptions.NoHayRepositoriosRegistrados e) {
							throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
						}
						String [] array = new String[1];
						array[0] = nombre;
						Process proc = Runtime.getRuntime().exec("java -jar Cliente.jar " + nombre);
						InputStream input = proc.getInputStream();
						InputStream err = proc.getErrorStream();
						break;
					case 3:
						System.out.println("Gracias por usar el sistema, vuelve pronto!");
						salir_autenticacion = true;
						break;
					default:
						System.out.println("No ha elegido una opción correcta, indique el número de la opción que le interese (1-3).");
						break;
				}
			}
		registry.unbind("rmi://"+ ip + ":3434/sdc_remoto/1");
		UnicastRemoteObject.unexportObject(sdc, true);
		in.close();
	}
}