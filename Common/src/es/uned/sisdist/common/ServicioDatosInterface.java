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
	
	public Repositorio linkRepositorio (String nombre_cliente) throws RemoteException;
	
	public void unlinkRepositorio(String Nombre) throws RemoteException;
	
	public List<String> getListaRepositoriosRegistrados () throws RemoteException;
	
	public HashMap<String, Integer> getListaRepositoriosLogueados () throws RemoteException;
	
	public HashMap<String, Repositorio> getListaRepositoriosActivos () throws RemoteException;
	
	public Repositorio getRepositorioActivo (String nombre) throws RemoteException;
	
	public List<MetaFichero> getListaFicheros (String nombre_cliente) throws RemoteException;
	
	public List<String> getListaClientesRepositorio (String nombre_repositorio) throws RemoteException;
	
	public HashMap<String, List<MetaFichero>> getListaRepositorioFicheros (String nombre_cliente) throws RemoteException;
	
	public List<String> getListaClientesRegistrados () throws RemoteException;
	
	public int getIdCliente(String nombre) throws RemoteException;
	
	public HashMap<String, Integer> getListaClientesActivos()
			throws RemoteException;
	
	public int getIdRepositorio(String nombre) throws RemoteException;
	
	public List<Repositorio> getRepositoriosUsuario(String nombre_cliente) throws RemoteException;
	
	public void addRepositorioActivo(Repositorio repo, String nombre) throws RemoteException;
	
	public void addMetaFichero(String nombre_repositorio,MetaFichero metafichero, String nombre_cliente) throws RemoteException;
	
	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException;
	
}
