package es.uned.sisdist.servidor;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
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
import es.uned.sisdist.common.URL;

public class ServicioGestorImpl implements ServicioGestorInterface{
	private ServicioDatosInterface servicio_datos;
	private ServicioSrOperadorInterface sso;
	private ServicioClOperadorInterface sco;
	
	public ServicioGestorImpl () throws RemoteException, NotBoundException {
		Registry registry =  LocateRegistry.getRegistry(7777);
		
		servicio_datos = (ServicioDatosInterface) registry.lookup("datos_remotos"); 
		sso = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
		sco = (ServicioClOperadorInterface) registry.lookup("sco_remoto");
	}
	@Override
	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception {
		Repositorio repo = servicio_datos.getRepositoriosUsuario(nombre_cliente).get(new Random().nextInt(servicio_datos.getRepositoriosUsuario(nombre_cliente).size()));
		System.out.println("Creando Fichero");
		Fichero fichero = new Fichero(path_local, nombre_fichero, nombre_cliente);
		System.out.println("Fichero creado");
		servicio_datos.addMetaFichero(repo.getNombre(),new MetaFichero(fichero), nombre_cliente);
		System.out.println("Tratando de iniciar subida");
		sco.subirArchivo(fichero, repo, nombre_cliente, nombre_fichero);
		
	}

	@Override
	public void bajarFichero(String nombre_cliente, String nombre_fichero, String path_local) throws RemoteException, Exception{
		Repositorio repo;
		boolean bandera = false;
		HashMap<String,List<MetaFichero>> ficheros_usuario = servicio_datos.getListaRepositorioFicheros(nombre_cliente);
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.entrySet()) {
			for(MetaFichero fichero : entrada.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					repo = servicio_datos.getRepositorioActivo(entrada.getKey());
					sso.bajarFichero(repo, nombre_fichero, path_local, nombre_cliente);
					bandera = true;
					break;
				}
			}
		}
		if (!bandera) {
			throw new RuntimeException ("Fichero de nombre " + nombre_fichero + " no encontrado");
		}
	}

	@Override
	public void borrarFichero(String nombre_cliente, String nombre_fichero) throws RemoteException {
		Repositorio repo;
		boolean bandera = false;
		HashMap<String,List<MetaFichero>> ficheros_usuario = servicio_datos.getListaRepositorioFicheros(nombre_cliente);
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.entrySet()) {
			for(MetaFichero fichero : entrada.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					repo = servicio_datos.getRepositorioActivo(entrada.getKey());
					sco.deleteArchivo(repo,nombre_fichero, nombre_cliente);
					File file = new File(repo.getPath() + "/" + nombre_cliente + "/" + nombre_fichero);
					file.delete();
					bandera = true;
				}
			}
		}
		if (bandera == false) {
			throw new RuntimeException ("Fichero de nombre " + nombre_fichero + " no encontrado");		}
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
	public List<String> getListaClientes(String nombre_cliente) throws RemoteException {
		return servicio_datos.getListaClientesRegistrados();
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

}