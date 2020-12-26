/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que se encarga de registrar y autenticar un cliente, cuando se loguea
 * un cliente inicializa su actividad.
 * 
 * */
package Cliente;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.ServicioAutenticacionInterface;

public class AutenticacionCliente {
	private static ServicioAutenticacionInterface servicio_autenticacion;
	
	
	public static void main(String[] args) throws Exception {
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		int opcion = -1;
		String nombre = "";
		
		Scanner in = new Scanner(System.in);
		boolean salir_autenticacion = false;	

		//Obtengo el seervicio remoto de autenticación.
		servicio_autenticacion = (ServicioAutenticacionInterface) registry.lookup("rmi://"+ ip + ":6666/autenticacion_remota/1");
		
			while(!salir_autenticacion) {
				System.out.println("Elige la opción de autenticacion");
				System.out.println("1. Registrar usuario");
				System.out.println("2. Iniciar sesion usuario");
				System.out.println("3. Exit");
				
				//Obtengo la opción elegida por el usuario.
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
						//Rgistro el cliente si no ha sido registrado ya.
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
						//Inicio sesión del cliente.
						System.out.println("Indique el nombre del usuario");
						nombre = in.nextLine();					
						servicio_autenticacion.iniciarSesion(nombre, 0);
						System.out.println("Usuario con nombre " + nombre + " conectado");
						System.out.println("");
						}
						catch (CustomExceptions.NoHayRepositoriosLibres e) {
							//Si no hay repositorio libres, no tiene sentido que un cliente pueda estar activo en el sistema.
							throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
						}
						catch (CustomExceptions.ObjetoNoRegistrado e) {
							throw new CustomExceptions.ObjetoNoRegistrado("Usuario no registrado");
						}
						catch (CustomExceptions.NoHayRepositoriosRegistrados e) {
							throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
						}
						//Iniicio la funcionalidad del cliente pasando por argumentos su nombre.
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
		//Cierro el escaner.
		in.close();
	}
}