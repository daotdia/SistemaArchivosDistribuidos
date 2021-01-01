/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el servicio gestor del Servidor, se encarga de gestionar la subida, bajada, borrado y compartición
 * de los ficheros que el cliente le indique al servidor.
 * 
 * */
package es.uned.sisdist.servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.Fichero;
import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class ServicioGestorImpl implements ServicioGestorInterface{
	private static ServicioDatosInterface servicio_datos;
	private static ServicioSrOperadorInterface sso;
	private static ServicioClOperadorInterface sco;
	private static Registry registry;
	String ip;
	
	public ServicioGestorImpl () throws RemoteException, NotBoundException, UnknownHostException {
		//Obtengo el registro.
		registry =  LocateRegistry.getRegistry(7777);
		
		//Obtengo la ip.
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
		
		//Obtengo el servicio de datos remoto.
		servicio_datos = (ServicioDatosInterface) registry.lookup("rmi://"+ ip + ":8888/datos_remotos/1"); 
	}
	
	//Método que permite subir un fichero dado un path local y el nombre del fichero.
	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception {
		Repositorio repo;
		for(MetaFichero fichero : servicio_datos.getListaFicherosCliente(nombre_cliente)){
			if(fichero.getNombre().equals(nombre_fichero)) {
				throw new CustomExceptions.ElementoDuplicado("Este archivo ya ha sido subido con anterioridad o ya lo está compartiendo, si quiere volver a subir su contenido cambie su nombre");
			}
		}
		//En el caso de que el cliente tenga un repositorio asignado.
		if(!servicio_datos.getRepositoriosUsuario(nombre_cliente).isEmpty()) {
			//Se obtiene un repositorio al azar.
			repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
		}
		//Si no tiene repositorio asignados.
		else {
			try {
				//Se linkea uno.
				servicio_datos.linkRepositorio(nombre_cliente);
				//Se obtiene uno al azar.
				repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
				//Se obtiene el servicio remoto SSO de dicho repositorio.
				sso = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/" + repo.getPortSso());
				//Se crea la carpeta del usuario en dicho repositorio.
				sso.crearCarpeta(repo.getPath(), nombre_cliente);
			} catch (Exception e) {
				//Si no hay repositorios libres se lanza una excepción.
				throw new CustomExceptions.NoHayRepositoriosLibres("No hay repositorios disponibles para linkar al usuario, inicializar nuevos");
			}
		}
		//Se crea un fichero a partir del nombre fichero y el path indicados.
		Fichero fichero = new Fichero(path_local, nombre_fichero, nombre_cliente);
		//Se crea el metafichero del fichero para guardar la información del mismo en el servicio de datos.
		MetaFichero metafichero = new MetaFichero(fichero);
		servicio_datos.addMetaFichero(repo.getNombre(), metafichero, nombre_cliente);
		//Se obtiene el servicio SCO del repositorio para subir el fichero.
		sco = (ServicioClOperadorInterface) registry.lookup("rmi://"+ ip + ":2222/sco_remoto/" + repo.getPortSco());
		sco.subirArchivo(fichero, repo, nombre_cliente, nombre_fichero);
	}

	//Método para bajar un fichero que esté en un repositorio de un cliente.
	public void bajarFichero(String nombre_cliente, String nombre_fichero, String path_local, int port) throws RemoteException, Exception{
		Repositorio repo;
		boolean bandera = false;
		//Obtengo los ficheros del cliente.
		List<MetaFichero> ficheros = servicio_datos.getListaFicherosCliente(nombre_cliente);
 		//Por cada fichero del cliente busco el que tenga el mismo nombre.
		for(MetaFichero fichero : ficheros) {
 			if(fichero.getNombre().equals(nombre_fichero)) {
 				//No me immporta del repositorio que se baje, aunque se podría modificar esta decisión.
 				repo = servicio_datos.getRepositorioFichero(nombre_fichero, fichero.getPropietario());
 				//Obtengo el SSO del repositorio con el ficheor a bajar, para poder bajarllo.
 				sso = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/" + repo.getPortSso());
 				sso.bajarFichero(repo, nombre_fichero, path_local, fichero.getPropietario(), port);
 				//No es necesario que se siga buscando arhivos coincidentes.
 				bandera = true;
 				break;
 			}
 		}
		//En el caso de que no se haya encontrado ningún archivo coincidente se lanza excepción.
		if (!bandera) {
			throw new RuntimeException ("Fichero de nombre " + nombre_fichero + " no encontrado");
		}
	}

	//Método para borrar un fichero de un cliente.
	public void borrarFichero(String nombre_cliente, String nombre_fichero) throws Exception, RemoteException {
		//Se comprueba que el cliente sea propietario del fichero.
		if(comprobarPropiedad(nombre_cliente, nombre_fichero)) {
			//Obtengo los repositorios con el fichero a eliminar.
			List<Repositorio> repositorios = servicio_datos.getRepositoriosFichero(nombre_fichero, nombre_cliente);
			try {
				//Por cada uno de los repositorios, obtengo su SSO y elimino el archivo.
				for(Repositorio repo : repositorios) {
					sco = (ServicioClOperadorInterface) registry.lookup("rmi://"+ ip + ":2222/sco_remoto/" + repo.getPortSco());
					sco.deleteArchivo(repo,nombre_fichero, nombre_cliente);
					//También elimino del servicio datos la propiedad del fichero por parte del cliente.
					servicio_datos.deleteFicheroCliente(nombre_cliente, repo.getNombre(), nombre_fichero);
				}
			} catch(Exception e) {
				throw new Exception();
			}
		}
		//Si no es proopietario devuelve excepción.
		else 
			throw new CustomExceptions.PermisoDenegado();
	}

	//Método que obtiene la lista de fichero de un cliente (convierte una lista de metaficheros en una lista de nombre de ficheros). 
	public List<String> getListaFicheros(String nombre_cliente) throws RemoteException {
		List<MetaFichero> ficheros = servicio_datos.getListaFicherosCliente(nombre_cliente);
		List<String> nombre_ficheros = new ArrayList<String>();
		for(MetaFichero fichero : ficheros) {
			nombre_ficheros.add(fichero.getNombre());
		}
		return nombre_ficheros;
	}

	//Método que obtiene la lista de clientes registrados en el sistema.
	public List<String> getListaClientesSistema() throws RemoteException {
		return servicio_datos.getListaClientesRegistrados();
	}
	
	//Método para obtener los clientes de un repositorio.
	public List<String> getListaClientesRepositorio(String nombre_repositorio) throws RemoteException {
		return servicio_datos.getListaClientesRepositorio(nombre_repositorio);
	}

	//Método para obtener los ficheros de un cliente en un repositorio.
	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException{
		return servicio_datos.getFicherosClienteRepositorio(nombre_cliente, nombre_repositorio);
	}
	
	//Método para compartir un fichero con un destinatario.
	public void compartirFichero(String nombre_propietario, String nombre_destinatario, String nombre_fichero) throws Exception, RemoteException {
		//Compruebo que el archivo que quiere compartir es de su propiedad.
		if(comprobarPropiedad(nombre_propietario, nombre_fichero)) {
			for(MetaFichero fichero : servicio_datos.getListaFicherosCliente(nombre_destinatario)) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					throw new CustomExceptions.ElementoDuplicado();
				}
			}
			//Añado el fichero compartido al cliente destintario en el servicio de datos.
			Repositorio repo = servicio_datos.getRepositorioFichero(nombre_fichero, nombre_propietario);
			servicio_datos.addMetaFicheroCompartido(repo.getNombre(), new MetaFichero(nombre_propietario, nombre_fichero), nombre_destinatario);
		}
		else 
			//Si no es propietario lanza excepción.
			throw new CustomExceptions.PermisoDenegado();
	}

	//Método para comprobar la propiedad de un fichero de un cliente.
	public boolean comprobarPropiedad(String nombre_cliente, String nombre_fichero) throws RemoteException {
		//Busco fichero coincidente con el nombre del fichero a compartir.
		for(MetaFichero fichero: servicio_datos.getListaFicherosCliente(nombre_cliente)) {
			if(fichero.getNombre().equals(nombre_fichero)) {
				//Compruebo que el propietario del fichero sea el cliente.
				if (fichero.getPropietario().equals(nombre_cliente)) {
						return true;
				}
			}
		}
		return false;
	}
	
	//Método para obtener el puerto del cliente, lo uso para inicializar cliente.
	public int getPortCliente() throws RemoteException{
		return servicio_datos.getPortCliente();
	}
	
	//Método para obtener el puerto del repositorio, lo uso para inicializar el repositorio.
	public int getPortRepositorio(String nombre_repositorio) throws RemoteException {
		return servicio_datos.getPortRepositorio(nombre_repositorio);
	}
}
