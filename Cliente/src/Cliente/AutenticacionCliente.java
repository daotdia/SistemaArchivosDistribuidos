package Cliente;
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
	private static int port = 2100;
	
	public static void main(String[] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		int opcion = -1;
		String nombre = "";
		ServicioDiscoClienteInterface sdc;
		
		Scanner in = new Scanner(System.in);
		boolean salir_autenticacion = false;	
		
		

		try {
			sdc = new ServicioDiscoClienteImpl();
			Remote sdc_remoto = UnicastRemoteObject.exportObject(sdc, getPort());
			registry.rebind("rmi://"+ ip + ":3434/sdc_remoto/" + getPort() , sdc_remoto);
			port++;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			sdc = (ServicioDiscoClienteInterface) registry.lookup("rmi://"+ ip + ":3434/sdc_remoto/" + port);
			
		}
		
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
				if(in.hasNextLine()) {
					in.nextLine();
				}
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
						Cliente.main(array);
						salir_autenticacion = true;
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
		registry.unbind("rmi://"+ ip + ":3434/sdc_remoto/" + port);
		try {
			UnicastRemoteObject.unexportObject(sdc, true);
		}
		catch (Exception e) {
			in.close();
		}
	}
	public static int getPort() {
		return port + 1;
	}
}