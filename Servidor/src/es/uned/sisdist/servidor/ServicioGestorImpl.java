package es.uned.sisdist.servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	
	public ServicioGestorImpl () throws RemoteException, NotBoundException {
		registry =  LocateRegistry.getRegistry(7777);
		
		servicio_datos = (ServicioDatosInterface) registry.lookup("datos_remotos"); 
		sso = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
		sco = (ServicioClOperadorInterface) registry.lookup("sco_remoto");
	}
	@Override
	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception {
		Repositorio repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
		System.out.println("Creando Fichero");
		System.out.println("El objeto repositorio en getRepositoriosUsuario es: " +  repo.toString() + "con nombre " + repo.getNombre());
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
		List<String> nombres_ficheros = servicio_datos.getListaFicherosCliente(nombre_cliente);
 		for(String nombre : nombres_ficheros) {
 			if(nombre.equals(nombre_fichero)) {
 				System.out.println("Fichero encontrado");
 				//No me immporta de cuál se baje, aunque se podría modificar esta decisión.
 				repo = servicio_datos.getRepositorioFichero(nombre_fichero, nombre_cliente);
 				sso.bajarFichero(repo, nombre_fichero, path_local, nombre_cliente);
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
	public void borrarFichero(String nombre_cliente, String nombre_fichero) throws RemoteException {
		List<Repositorio> repositorios = servicio_datos.getRepositoriosFichero(nombre_fichero, nombre_cliente);
		for(Repositorio repo : repositorios) {
			sco.deleteArchivo(repo,nombre_fichero, nombre_cliente);
			servicio_datos.deleteFicheroCliente(nombre_cliente, repo.getNombre(), nombre_fichero);
		}
	}

	@Override
	public List<Integer> compartirFichero(int sesion, int identificador, List<Integer> destinatarios)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getListaFicheros(String nombre_cliente) throws RemoteException {
		List<MetaFichero> ficheros = servicio_datos.getListaFicheros(nombre_cliente);
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

}
