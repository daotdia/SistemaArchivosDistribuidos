package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDatosInterface;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface{

	private static int identificador = 0;
	
	private ServicioDatosInterface bd;
	private List<String> clientes_registrados;
	private List<String> repositorios_registrados;
	private HashMap<String, Integer> clientes_activos;
	private HashMap<String, Integer> repositorios_activos;
	
	public ServicioAutenticacionImpl(ServicioDatosInterface bd) throws RemoteException {
		this.bd = bd;
		clientes_registrados = bd.getListaClientes();
		repositorios_registrados = bd.getListaRepositorios();
		clientes_activos =  bd.getListaClientesActivos();
		repositorios_activos = bd.getListaRepositoriosActivos();
	}
	
	public void registrarObjeto(String nombre, int tipo) throws RemoteException {
		if(tipo == 0)	
			if(!clientes_registrados.contains(nombre)) {
				bd.registrarCliente(nombre);
				System.out.println("Se ha registrado el cliente " + nombre);
			}
			else 
				System.out.println("El nombre proporcionado ya está en uso, modifíquelo");
		if(tipo == 1)	
			if(!repositorios_registrados.contains(nombre)) {
				bd.registrarRepositorio(nombre);
				System.out.println("Se ha registrado el repositorio " + nombre);
			}
			else 
				System.out.println("El nombre proporcionado ya está en uso, modifíquelo");
	}

	public int iniciarSesion(String nombre, int tipo) throws RemoteException {
		int sesion = -1;
		if(tipo == 0) {
			if(clientes_registrados.contains(nombre)) {
				sesion = getIdentificador();
				bd.addId(nombre, 0);
				System.out.println("Se ha logueado el cliente " + nombre);
			}
			else
				throw new RuntimeException ("Usuario no registrado");
		}
		else if(tipo == 1) {
			if(repositorios_registrados.contains(nombre)) {
				sesion = getIdentificador();
				bd.addId(nombre, 1);
				System.out.println("Se ha logeado el cliente " + nombre);
			}
			else
				throw new RuntimeException ("Repositorio no registrado");
		}
		return sesion;
	}

	public int getIdSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
		if(tipo == 0) {
			if(clientes_activos.containsKey(nombre))
				identificador = clientes_activos.get(nombre);
			else
				throw new RuntimeException ("Usuario no logueado");
		}
		if (tipo == 1) {
			if(repositorios_activos.containsKey(nombre))
				identificador = repositorios_activos.get(nombre);
			else
				throw new RuntimeException ("Repositorio no logueado");
		}
		return identificador;
	}

	//Permito que un usuario logueado pueda eliminar su registro pero quedar logueado hasta
	//que cierre sesión.
	public void deleteObjeto(String nombre, int tipo) throws RemoteException {
		if(tipo == 0) {
			if(clientes_registrados.contains(nombre))
				bd.deleteCliente(nombre);
			else
				throw new RuntimeException ("Cliente no registrado");
		}
		if(tipo==1) {
			if(repositorios_registrados.contains(nombre))
				bd.deleteRepositorio(nombre);
			else
				throw new RuntimeException ("Repositorio no registrado");
		}
	}
	
	public void cerrarSesion(String nombre, int tipo) throws RemoteException {
		if(tipo==0) {
			if(clientes_activos.containsKey(nombre)) {
				clientes_activos.remove(nombre);
			}
			else 
				throw new RuntimeException ("Cliente no logueado");
		}
		if(tipo==1) {
			if(repositorios_activos.containsKey(nombre)) {
				repositorios_activos.remove(nombre);
			}
			else 
				throw new RuntimeException ("Repositorio no logueado");
		}
	}

	public static int getIdentificador() {
		return identificador++;
	}

	
	
}
