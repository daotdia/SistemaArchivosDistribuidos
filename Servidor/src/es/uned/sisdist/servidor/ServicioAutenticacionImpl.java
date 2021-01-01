/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el servicio de autenticación del sistema de archivos distribuido. 
 * He considerado que formar parte de los servicios del Servidor.
 * 
 * ACLARACIÓN: aunque como indica el enunciado a la hora de autenticarse les asigno a cada usuario un identificador numérico único,
 * sólo lo he realizado para cumplir con los requirimientos del enunciado. Una vez autenticado el usuario puede gestionar el sistema sólo
 * teniendo en cuenta los nombres de usuarios y fichero, he considerado que de esta manera es mucho más cómodo e intuitivo el funcionamiento.
 * Como contrapartida, este sistema no permite usuarios con el mismo nombre.
 * 
 * */
package es.uned.sisdist.servidor;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface{

	//Declaro las variables estáticas necesarias.
	private static int identificador = 0;
	
	private static ServicioDatosInterface servicio_datos;
	private static ServicioSrOperadorInterface servicio_gestor;
	private static Registry registry;
	private static String ip;
	
	//Constructor del servicio de autenticación, obtiene el registro del sistema distribuido, la ip y el servicio de datos.
	public ServicioAutenticacionImpl() throws Exception {
		registry = LocateRegistry.getRegistry(7777);
		
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		ServicioAutenticacionImpl.servicio_datos = (ServicioDatosInterface) registry.lookup("rmi://"+ ip + ":8888/datos_remotos/1"); 
	}
	
	//Método que registra un objeto del sistema distribuido, un repositorio o un usuario.
	public boolean registrarObjeto(String nombre, int tipo) throws RemoteException {
		//Si es un usuario.
		if(tipo == 0)	
			//Regitra el cliente en el servicio de datos si no está registrado ya.
			if(!servicio_datos.getListaClientesRegistrados().contains(nombre)) {
				servicio_datos.registrarCliente(nombre);
				return true;
			}
			else {
				return false;
			}
		//Si es  un repositorio.
		if(tipo == 1)	
			//Registra el repositorio si no está registrado ya.
			if(!servicio_datos.getListaRepositoriosRegistrados().contains(nombre)) {
				servicio_datos.registrarRepositorio(nombre);
				return true;
			}
			else {
				return false;
			}
		return false;
	}

	//Método que inicia sesión bien de un repositorio o bien de un cliente.
	public int iniciarSesion(String nombre, int tipo) throws RuntimeException, Exception {
		int sesion = -1;
		//Si es un cliente.
		if(tipo == 0) {
			try {
				//Si el cliente está registrado, entonces procede a iniciar sesión.
				if(servicio_datos.getListaClientesRegistrados().contains(nombre)) {
					//Obtengo el identificador de usuario único.
					sesion = getIdentificador();
					//Linkeo un repositorio al usuario.
					Repositorio repo = servicio_datos.linkRepositorio(nombre);
					//Añado el repositorio como repositorio activo.
					servicio_datos.addRepositorioActivo(repo, nombre);
					//Obtengo el servicio gestor de dicho repositorio concreto (gracias a su puerto). 
					ServicioAutenticacionImpl.servicio_gestor = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/" + repo.getPortSso());
					//Creo la carpeta del usuario en el repositorio.
					servicio_gestor.crearCarpeta(repo.getPath(), nombre);
					//Añado al usuario en el servicio de datos como usuario activo.
					servicio_datos.addId(nombre, sesion, 0);
				}
				//Si no está registrado lanza una excepción e informa de que el cliente todavía no está registrado.
				else {
					servicio_datos.getListaClientesActivos().remove(nombre);
					throw new RuntimeException ("Usuario no registrado");
				}
			} 
			catch (CustomExceptions.NoHayRepositoriosLibres e) {
				//En el caso de que no se pueda linkar un repositorio nuevo.
				throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
			}
			catch (CustomExceptions.ObjetoNoRegistrado e) {
				//En el caso de que el usuario no esté registrado.
				throw new CustomExceptions.ObjetoNoRegistrado("Usuario no registrado");
			}
			catch (CustomExceptions.NoHayRepositoriosRegistrados e) {
				//En el caso de que no existan repositorios registrados.
				throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
			}
		}
		//Si es un repositorio.
		else if(tipo == 1) {
			//Si el repositorio está registrado, se procede a iniciar sesión del mismo.
			if(servicio_datos.getListaRepositoriosRegistrados().contains(nombre)) {
				sesion = getIdentificador();
				servicio_datos.addId(nombre, sesion, 1);
			}
			else
				//Si no está registrado lanza una excepción e informa de que el repositorio todavía no está registrado.
				throw new RuntimeException ("Repositorio no registrado");
		}
		//Devuelve el identificador del cliente.
		return sesion;
	}
	
	//Método para obtener el ID de sesión de un usuario o un repositorio, no lo utilizo en esta práctica, aunque en futuras reimplementaciones podría ser utilizado.
	public int getIdSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
		//Si es un usuario.
		if(tipo == 0) {
			//Si está registrado, obtiene el ID del servicio de datos..
			if(servicio_datos.getListaClientesActivos().containsKey(nombre))
				identificador = servicio_datos.getListaClientesActivos().get(nombre);
			//Si no, lanza excepción.
			else
				throw new RuntimeException ("Usuario no logueado");
		}
		//Si es un repositorio,
		if (tipo == 1) {
			//Si está resgistrado, obtiene el ID del servicio de datos.
			if(servicio_datos.getListaRepositoriosActivos().containsKey(nombre))
				identificador = servicio_datos.getListaRepositoriosLogueados().get(nombre);
			//Si no, lanza excepción.
			else
				throw new RuntimeException ("Repositorio no logueado");
		}
		//Devuelve el identificador.
		return identificador;
	}
	
	//Método que cierra la sesión de un cliente y un repositorio. 
	public void cerrarSesion(String nombre, int tipo) throws RemoteException {
		//Si es un cliente.
		if(tipo==0) {
			//Si el cliente es un cliente logueado.
			if(servicio_datos.getListaClientesActivos().containsKey(nombre)) {
				//Cierra la sesión del cliente y deslinkea sus repositorios.
				servicio_datos.cerrarSesionCliente(nombre);
				servicio_datos.unlinkRepositorios(nombre);
			}
			//Si no devuelve una excepción.
			else 
				throw new RuntimeException ("Cliente no logueado");
		}
		//Si e sun repositorio.
		if(tipo==1) {
			//Si el repositorio estña logueado.
			if(servicio_datos.getListaRepositoriosLogueados().containsKey(nombre)) {
				//Cierra la sesión del repositorio.
				servicio_datos.cerraSesionRepositorio(nombre);
			}
			//Si no devuelve una excepción.
			else 
				throw new RuntimeException ("Repositorio no logueado");
		}
	}
	
	//Método que elimina del sistema un repositorio o un cliente. 
	public void deleteObjeto(String nombre, int tipo) throws RemoteException, NotBoundException {
		//Si es un cliente.
		if(tipo == 0) {
			//Si está registrado.
			if(servicio_datos.getListaClientesRegistrados().contains(nombre)) {
				//Obtiene el servicio gestor de los repositorios del cliente y elimina sus carpetas de usuarios.
				for (Repositorio repo : servicio_datos.getRepositoriosUsuario(nombre)) {
					System.out.println(servicio_datos.getRepositoriosUsuario(nombre).size());
					ServicioAutenticacionImpl.servicio_gestor = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/" + repo.getPortSso());
					servicio_gestor.borrarCarpetaCliente(repo, nombre);
				}
				//Elimina ek cliente del servicio de datos. 
				servicio_datos.deleteCliente(nombre);
			}
			//Si no devuelve excepción.
			else
				throw new RuntimeException ("Cliente no registrado");
		}
		//Si es un repositorio.
		if(tipo==1) {
			try {
				//Si está registrado, elimina la carpeta del repositorio (con las carpetas de usuario que contenga) y elimina le repositorio del servico de datos.
				if(servicio_datos.getListaRepositoriosRegistrados().contains(nombre)) {
					Repositorio repo = servicio_datos.getRepositorioActivo(nombre);
					ServicioAutenticacionImpl.servicio_gestor = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/" + repo.getPortSso());
					servicio_gestor.borrarCarpetaRepositorio(repo.getPath());
					servicio_datos.deleteRepositorio(nombre);
				}
			}
			catch (CustomExceptions.RepositorioTodaviaNoUtilizado e) {
				//Si el repositorio no se ha utilizado, todavía no se ha registrado en el sistema.
				throw new CustomExceptions.RepositorioTodaviaNoUtilizado("RepositorioTodaviaNoUtilizado");
			}
			catch (CustomExceptions.ObjetoNoRegistrado e){
				//Si el repositorio no está registrado.
				throw new CustomExceptions.ObjetoNoRegistrado("Repositorio no registrado");
			}
			catch (NullPointerException e) {
				//Si el repositorio no se ha linkado a ningún cliente.
				servicio_datos.deleteRepositorio(nombre);
				throw new CustomExceptions.RepositorioTodaviaNoUtilizado("El repositorio todavía no ha sido linkado a ningún cliente");
			}
		}
	}
	
	//Método para obtener el identificador único para cada objeto.
	public static int getIdentificador() {
		return identificador++;
	}
	
	//Método para comprobar que un cliente está logueado.
	public boolean comprobarCliente (String nombre_cliente) throws RemoteException{
		return servicio_datos.getListaClientesActivos().containsKey(nombre_cliente);
	}
	
	//Método para comprobar que un repositorio está logueado.
	public boolean comprobarRepositorio (String nombre_repositorio) throws RemoteException{
		return servicio_datos.getListaRepositoriosLogueados().containsKey(nombre_repositorio);
	}
}
