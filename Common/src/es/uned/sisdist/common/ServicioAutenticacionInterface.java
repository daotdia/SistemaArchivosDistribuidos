/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Interfaz del Servicio de Autenticación.
 * 
 * */
package es.uned.sisdist.common;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioAutenticacionInterface extends Remote {
	
	public boolean registrarObjeto (String nombre, int tipo) throws RemoteException;
		
	public int iniciarSesion (String nombre, int tipo) throws RemoteException, Exception;
	
	public void cerrarSesion (String nombre, int tipo) throws RemoteException;
	
	public int getIdSesion (String nombre, int tipo) throws RemoteException;
	
	public void deleteObjeto (String nombre, int tipo) throws RemoteException, NotBoundException;
	
	public boolean comprobarCliente (String nombre_cliente) throws RemoteException;
	
	public boolean comprobarRepositorio (String nombre_repositorio) throws RemoteException;
}
