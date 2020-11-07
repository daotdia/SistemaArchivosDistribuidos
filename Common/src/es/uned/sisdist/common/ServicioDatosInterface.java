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
	 
	public void addId(String nombre, int tipo) throws RemoteException;
	
	public int linkRepositorio (int id_cliente) throws RemoteException;
	
	public boolean unlinkRepositorio (int id_cliente) throws RemoteException;
	
	public Map<Integer, List<Integer>> getRepositoriosCliente (int id_cliente)
			throws RemoteException;
	
	public List<String> getListaRepositorios () throws RemoteException;
	
	public List<String> getListaFicheros (int sesion) throws RemoteException;
	
	public List<String> getListaClientes () throws RemoteException;
	
	public HashMap<String, Integer> getListaClientesActivos()
			throws RemoteException;
	
	public HashMap<String, Integer> getListaRepositoriosActivos()
			throws RemoteException;
	
}
