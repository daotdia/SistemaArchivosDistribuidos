/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Interfaz del Servicio Gestor.
 * 
 * */
package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ServicioGestorInterface extends Remote{

	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local)
			throws RemoteException, Exception;
	
	public void bajarFichero(String nombre_cliente, String nombre_fichero, String path_local, int port)
			throws RemoteException, Exception;
	
	public void borrarFichero(String nombre_cliente, String nombre_fichero)
			throws RemoteException, Exception;
	
	public void compartirFichero(String nombre_propietario, String nombre_destinatario, String nombre_fichero) throws RemoteException, Exception;
	
	public List<String> getListaFicheros (String nombre_cliente) throws RemoteException;
	
	public List<String> getListaClientesSistema () throws RemoteException;
	
	public List<String> getListaClientesRepositorio (String nombre_repositorio) throws RemoteException;

	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException;
	
	
	public int getPortCliente() throws RemoteException;
	
	public int getPortRepositorio(String nombre_repositorio) throws RemoteException;
}
