package es.uned.sisdist.servidor;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.URL;

public class ServiciosSrOperadorImpl implements ServicioSrOperadorInterface {

	private ServicioDatosInterface bd;
	private ServicioDiscoClienteInterface dc;
	
	
	public ServiciosSrOperadorImpl() throws Exception{
		Registry registry = LocateRegistry.getRegistry(7777);
		this.bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
		this.dc = (ServicioDiscoClienteInterface) registry.lookup("sdc_remoto");
	}
	
	@Override
	public void crearCarpeta(Repositorio repo, String nombre) throws RemoteException {
		//Obtengo el repositorio y creo la carpeta del usuario según el path del mismo.
		File directorio=new File(repo.getPath() + "/" + nombre);
		directorio.mkdir();
		System.out.println("Carpeta creada en path " + directorio);
	}

	@Override
	public void borrarCarpeta(Repositorio repo, String nombre_cliente) throws RemoteException {
		File directorio=new File(repo.getPath() + "/" + nombre_cliente);
		directorio.delete();
		System.out.println("Carpeta eleminada en path " + directorio);
	}

	//Baja todos los archivos del nombre indicado, serán varios en el caso de que se encuentren archivos del mismo
	//nombre en varios repositorios.
	@Override
	public void bajarArchivo(String nombre_fichero, String nombre_cliente, String path_local) throws RemoteException, Exception {
		Repositorio repo;
		boolean bandera = false;
		HashMap<String,List<MetaFichero>> ficheros_usuario = bd.getListaFicheros(nombre_cliente);
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.entrySet()) {
			for(MetaFichero fichero : entrada.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					repo = bd.getRepositorioActivo(entrada.getKey());
					dc.bajarFichero(repo, nombre_fichero, path_local, nombre_cliente);
					bandera = true;
					break;
				}
			}
		}
		if (!bandera) {
			throw new RuntimeException ("Fichero de nombre " + nombre_fichero + " no encontrado");
		}
	}

}
