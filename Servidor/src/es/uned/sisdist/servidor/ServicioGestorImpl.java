package es.uned.sisdist.servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	
	public ServicioGestorImpl () throws RemoteException, NotBoundException, UnknownHostException {
		registry =  LocateRegistry.getRegistry(7777);
		
		InetAddress IP=InetAddress.getLocalHost();
		String ip = IP.getHostAddress();
		
		servicio_datos = (ServicioDatosInterface) registry.lookup("rmi://"+ ip + ":8888/datos_remotos/1"); 
		sso = (ServicioSrOperadorInterface) registry.lookup("rmi://"+ ip + ":5555/sso_remoto/1");
		sco = (ServicioClOperadorInterface) registry.lookup("rmi://"+ ip + ":2222/sco_remoto/1");
	}
	@Override
	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception {
		Repositorio repo;
		if(!servicio_datos.getRepositoriosUsuario(nombre_cliente).isEmpty()) {
			repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
		}
		else {
			try {
				servicio_datos.linkRepositorio(nombre_cliente);
				repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
				sso.crearCarpeta(repo.getPath(), nombre_cliente);
			} catch (Exception e) {
				throw new CustomExceptions.NoHayRepositoriosLibres("No hay repositorios disponibles para linkar al usuario, inicializar nuevos");
			}
		}
		System.out.println("Creando Fichero");
		Fichero fichero = new Fichero(path_local, nombre_fichero, nombre_cliente);
		System.out.println("Fichero creado");
		MetaFichero metafichero = new MetaFichero(fichero);
		servicio_datos.addMetaFichero(repo.getNombre(), metafichero, nombre_cliente);
		System.out.println("Tratando de iniciar subida");
		sco.subirArchivo(fichero, repo, nombre_cliente, nombre_fichero);
	}

	@Override
	public void bajarFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception{
		Repositorio repo;
		boolean bandera = false;
		List<MetaFichero> ficheros = servicio_datos.getListaFicherosCliente(nombre_cliente);
 		for(MetaFichero fichero : ficheros) {
 			if(fichero.getNombre().equals(nombre_fichero)) {
 				System.out.println("Fichero encontrado");
 				//No me immporta de cuál se baje, aunque se podría modificar esta decisión.
 				repo = servicio_datos.getRepositorioFichero(nombre_fichero, fichero.getPropietario());
 				sso.bajarFichero(repo, nombre_fichero, path_local, fichero.getPropietario());
 				System.out.println("Fichero bajado");
 				bandera = true;
 				break;
 			}
 		}
		if (!bandera) {
			throw new RuntimeException ("Fichero de nombre " + nombre_fichero + " no encontrado");
		}
	}

	@Override
	public void borrarFichero(String nombre_cliente, String nombre_fichero) throws Exception, RemoteException {
		if(comprobarPropiedad(nombre_cliente, nombre_fichero)) {
			List<Repositorio> repositorios = servicio_datos.getRepositoriosFichero(nombre_fichero, nombre_cliente);
			try {
				for(Repositorio repo : repositorios) {
					sco.deleteArchivo(repo,nombre_fichero, nombre_cliente);
					servicio_datos.deleteFicheroCliente(nombre_cliente, repo.getNombre(), nombre_fichero);
				}
			} catch(Exception e) {
				throw new Exception();
			}
		}
		else 
			throw new CustomExceptions.PermisoDenegado();
	}

	@Override
	public List<String> getListaFicheros(String nombre_cliente) throws RemoteException {
		List<MetaFichero> ficheros = servicio_datos.getListaFicherosCliente(nombre_cliente);
		List<String> nombre_ficheros = new ArrayList<String>();
		for(MetaFichero fichero : ficheros) {
			nombre_ficheros.add(fichero.getNombre());
		}
		return nombre_ficheros;
	}

	@Override
	public List<String> getListaClientesSistema() throws RemoteException {
		return servicio_datos.getListaClientesRegistrados();
	}
	
	public List<String> getListaClientesRepositorio(String nombre_repositorio) throws RemoteException {
		return servicio_datos.getListaClientesRepositorio(nombre_repositorio);
	}

	@Override
	public List<Integer> getListaRepositorios(int sesion) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, List<Integer>> getRepositoriosCliente(int sesion) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void salir() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException{
		return servicio_datos.getFicherosClienteRepositorio(nombre_cliente, nombre_repositorio);
	}
	
	public void compartirFichero(String nombre_propietario, String nombre_destinatario, String nombre_fichero) throws Exception, RemoteException {
		if(comprobarPropiedad(nombre_propietario, nombre_fichero)) {
			Repositorio repo = servicio_datos.getRepositorioFichero(nombre_fichero, nombre_propietario);
			servicio_datos.addMetaFicheroCompartido(repo.getNombre(), new MetaFichero(nombre_propietario, nombre_fichero), nombre_destinatario);
		}
		else 
			throw new CustomExceptions.PermisoDenegado();
	}

	public boolean comprobarPropiedad(String nombre_cliente, String nombre_fichero) throws RemoteException {
		for(MetaFichero fichero: servicio_datos.getListaFicherosCliente(nombre_cliente)) {
			if(fichero.getNombre().equals(nombre_fichero)) {
				if (fichero.getPropietario().equals(nombre_cliente)) {
						return true;
				}
			}
		}
		return false;
	}
}
