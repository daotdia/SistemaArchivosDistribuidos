/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el el servicio servidor operador perteneciente acada repositorio encargado de crear las carpetas 
 * de los clientes y borrarlas, al igual que la propia carpeta del repositorio. También indica al servicio disco del cliente
 * qué fichero bajar.
 * 
 * */
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
	
	//Método que crea una carpeta en el path indicado.
	public void crearCarpeta(String path, String nombre_carpeta) throws RemoteException {
		//Obtengo el repositorio y creo la carpeta del usuario según el path del mismo.
		File directorio=new File(path + File.separator + nombre_carpeta);
		directorio.mkdir();
		System.out.println("Carpeta creada en path " + directorio);
	}

	//Método para borrar la carpeta del repositorio con el nombre indicado.
	public void borrarCarpetaCliente(Repositorio repo, String nombre_cliente) throws RemoteException {
		File directorio=new File(repo.getPath() + File.separator + nombre_cliente);
		//En el caso de que la carpeta tenga ficheros, elimino primero los mismos.
		if(directorio.listFiles().length != 0) {
			for(File file : directorio.listFiles()) {
				file.delete();
			}
		}
		directorio.delete();
		System.out.println("Carpeta eleminada en path " + directorio);
	}
	
	//Método para eliminar la carpeta raiz del repositorio.
	public void borrarCarpetaRepositorio(String path) throws RemoteException {
		try {
			File directorio=new File(path);
			//Si la carpeta del repositorio tiene carpetas de cliente con o sin archivos, primero elimina la carpeta de los usuarios.
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

	//Método que gestiona la bajada de un fichero de un cliente de un repositorio concreto en el path indicado, la ejecución final la realizará el servicio DC del cliente.
	public void bajarFichero(Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente, int port) throws RemoteException, Exception {
		dc = (ServicioDiscoClienteInterface) registry.lookup("rmi://"+ ip + ":3434/sdc_remoto/" + port);
		dc.bajarFichero(repo, nombre_fichero, path_local, nombre_cliente);
	}

}
