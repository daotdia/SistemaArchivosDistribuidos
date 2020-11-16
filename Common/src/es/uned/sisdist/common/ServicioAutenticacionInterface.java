package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioAutenticacionInterface extends Remote {
	
	public boolean registrarObjeto (String nombre, int tipo) throws RemoteException;
		
	public int iniciarSesion (String nombre, int tipo) throws RemoteException, Exception;
	
	public int cerrarSesion (String nombre, int tipo) throws RemoteException;
	
	public int getIdSesion (String nombre, int tipo) throws RemoteException;
	
	public void deleteObjeto (String nombre, int tipo) throws RemoteException;
	
	public boolean comprobarCliente (String nombre_cliente) throws RemoteException;
	
	public boolean comprobarRepositorio (String nombre_repositorio) throws RemoteException;
}
