package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ServicioGestorInterface extends Remote{

	//El servidor le devuelve al cliente la URL del Servicio Cliente-Operador.
	public void subirFichero(String nombre_cliente, String nombre_fichero, String path_local)
			throws RemoteException, Exception;
	
	//El servidor le devuelve al cliente la URL del Servicio Servidor-Operador.
	public void bajarFichero(String nombre_cliente, String nombre_fichero, String path_local)
			throws RemoteException, Exception;
	
	//El servidor le devuelve al cliente la URL del Servicio Cliente-Operador.
	public void borrarFichero(String nombre_cliente, String nombre_fichero)
			throws RemoteException;
	
	public List<Integer> compartirFichero (int sesion, int identificador, 
			List<Integer> destinatarios) throws RemoteException;
	
	public List<String> getListaFicheros (String nombre_cliente) throws RemoteException;
	
	public List<String> getListaClientes (String nombre_cliente) throws RemoteException;
	
	public List<Integer> getListaRepositorios (int sesion) throws RemoteException;
	
	public Map<Integer, List<Integer>> getRepositoriosCliente (int sesion)
			throws RemoteException;
	
	public void salir() throws RemoteException;
}
