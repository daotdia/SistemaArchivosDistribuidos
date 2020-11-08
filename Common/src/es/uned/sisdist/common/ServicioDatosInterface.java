package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ServicioDatosInterface extends Remote{
	
	public void registrarCliente (String nombre) throws RemoteException;
	
	public void registrarRepositorio (String nombre) throws RemoteException;
	
	public void deleteCliente (String nombre) throws RemoteException;
	
	public void deleteRepositorio (String nombre) throws RemoteException;
	 
	public void addId(String nombre, int identificador, int tipo) throws RemoteException;
	
	public int linkRepositorio (int id_cliente) throws RemoteException;
	
	public void unlinkRepositorio(int id_cliente) throws RemoteException;
		
	public Map<Integer,String> getRepositorioCliente (int id_cliente)
			throws RemoteException;
	
	public List<String> getListaRepositoriosLinkados () throws RemoteException;
	
	public List<String> getListaRepositoriosRegistrados () throws RemoteException;
	
	public HashMap<String, Integer> getListaRepositoriosActivos () throws RemoteException;
	
	public List<MetaFichero> getListaFicheros (int sesion) throws RemoteException;
	
	public List<String> getListaClientesRegistrados () throws RemoteException;
	
	public HashMap<String, Integer> getListaClientesActivos()
			throws RemoteException;
}
