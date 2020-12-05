package es.uned.sisdist.repositorio;

import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class ServicioSrOperadorImpl implements ServicioSrOperadorInterface {

	private ServicioDiscoClienteInterface dc;
	private Registry registry;
	private String ip;
	
	
	public ServicioSrOperadorImpl() throws Exception{
		registry = LocateRegistry.getRegistry(7777);
		InetAddress IP=InetAddress.getLocalHost();
		ip = IP.getHostAddress();
	}
	
	@Override
	public void crearCarpeta(String path, String nombre_carpeta) throws RemoteException {
		//Obtengo el repositorio y creo la carpeta del usuario según el path del mismo.
		File directorio=new File(path + "/" + nombre_carpeta);
		directorio.mkdir();
		System.out.println("Carpeta creada en path " + directorio);
	}

	@Override
	public void borrarCarpetaCliente(Repositorio repo, String nombre_cliente) throws RemoteException {
		File directorio=new File(repo.getPath() + "/" + nombre_cliente);
		if(directorio.listFiles().length != 0) {
			for(File file : directorio.listFiles()) {
				file.delete();
			}
		}
		directorio.delete();
		System.out.println("Carpeta eleminada en path " + directorio);
	}
	
	public void borrarCarpetaRepositorio(String path) throws RemoteException {
		try {
			File directorio=new File(path);
			if(directorio.listFiles() != null && directorio.listFiles().length != 0) {
				for(File hijo : directorio.listFiles()) {
					if(hijo.listFiles() != null && hijo.listFiles().length != 0) {
						for (File nieto : hijo.listFiles()) {
							nieto.delete();
						}
					}
					hijo.delete();
				}
			}
			directorio.delete();
			System.out.println("Carpeta eleminada en path " + directorio);
		} catch (Exception e) {
			throw new CustomExceptions.RepositorioTodaviaNoUtilizado("RepositorioTodaviaNoUtilizado");
		}
	}

	//Baja todos los archivos del nombre indicado, serán varios en el caso de que se encuentren archivos del mismo
	//nombre en varios repositorios.
	@Override
	public void bajarFichero(Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente, int port) throws RemoteException, Exception {
		dc = (ServicioDiscoClienteInterface) registry.lookup("rmi://"+ ip + ":3434/sdc_remoto/" + port);
		dc.bajarFichero(repo, nombre_fichero, path_local, nombre_cliente);
	}

}
