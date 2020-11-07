package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ServicioGestorInterface extends Remote{

	//El servidor le devuelve al cliente la URL del Servicio Cliente-Operador.
	public URL subirFichero(int sesion) 
			throws RemoteException;
	
	//El servidor le devuelve al cliente la URL del Servicio Servidor-Operador.
	public URL bajarFichero (int sesion, int identificador)
			throws RemoteException;
	
	//El servidor le devuelve al cliente la URL del Servicio Cliente-Operador.
	public URL borrarFichero (int sesion, int identificador)
			throws RemoteException;
	
	public List<Integer> compartirFichero (int sesion, int identificador, 
			List<Integer> destinatarios) throws RemoteException;
	
	public List<String> getListaFicheros (int sesion) throws RemoteException;
	
	public List<String> getListaClientes (int sesion) throws RemoteException;
	
	public List<Integer> getListaRepositorios (int sesion) throws RemoteException;
	
	public Map<Integer, List<Integer>> getRepositoriosCliente (int sesion)
			throws RemoteException;
	
	public void salir() throws RemoteException;
}
