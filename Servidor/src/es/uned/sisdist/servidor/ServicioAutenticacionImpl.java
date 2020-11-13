package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface{

	private static int identificador = 0;
	
	private ServicioDatosInterface bd;
	private ServicioSrOperadorInterface sg;
	
	public ServicioAutenticacionImpl() throws Exception {
		Registry registry = LocateRegistry.getRegistry(7777);
		this.bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
		this.sg = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
	}
	
	public void registrarObjeto(String nombre, int tipo) throws RemoteException {
		if(tipo == 0)	
			if(!bd.getListaClientesRegistrados().contains(nombre)) {
				bd.registrarCliente(nombre);
				System.out.println("Se ha registrado el cliente " + nombre);
			}
			else 
				System.out.println("El nombre proporcionado ya está en uso, modifíquelo");
		if(tipo == 1)	
			if(!bd.getListaRepositoriosRegistrados().contains(new Repositorio(nombre))) {
				bd.registrarRepositorio(nombre);
				System.out.println("Se ha registrado el repositorio " + nombre);
			}
			else 
				System.out.println("El nombre proporcionado ya está en uso, modifíquelo");
	}

	public int iniciarSesion(String nombre, int tipo) throws RemoteException {
		int sesion = -1;
		int id_repositorio;
		if(tipo == 0) {
			if(bd.getListaClientesRegistrados().contains(nombre)) {
				sesion = getIdentificador();
				bd.addId(nombre, sesion, 0);
				Repositorio repo = bd.linkRepositorio(nombre);
				sg.crearCarpeta(repo, nombre);
				System.out.println("Se ha logueado el cliente " + nombre);
			}
			else
				throw new RuntimeException ("Usuario no registrado");
		}
		else if(tipo == 1) {
			if(bd.getListaRepositoriosRegistrados().contains(nombre)) {
				sesion = getIdentificador();
				bd.addId(nombre, sesion, 1);
				System.out.println("Se ha logeado el repositorio " + nombre);
			}
			else
				throw new RuntimeException ("Repositorio no registrado");
		}
		return sesion;
	}

	public int getIdSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
		Repositorio[] repos = null;
		if(tipo == 0) {
			if(bd.getListaClientesActivos().containsKey(nombre))
				identificador = bd.getListaClientesActivos().get(nombre);
			else
				throw new RuntimeException ("Usuario no logueado");
		}
		if (tipo == 1) {
			if(bd.getListaRepositoriosActivos().containsKey(nombre))
				identificador = bd.getListaRepositoriosLogueados().get(nombre);
			else
				throw new RuntimeException ("Repositorio no logueado");
		}
		return identificador;
	}

	//Permito que un usuario logueado pueda eliminar su registro pero quedar logueado hasta
	//que cierre sesión.
	public void deleteObjeto(String nombre, int tipo) throws RemoteException {
		if(tipo == 0) {
			if(bd.getListaClientesRegistrados().contains(nombre)) {
				for (Repositorio repo : bd.getRepositoriosUsuario(nombre)) {
					sg.borrarCarpeta(repo, nombre);
				}
				bd.deleteCliente(nombre);
			}
			else
				throw new RuntimeException ("Cliente no registrado");
		}
		if(tipo==1) {
			if(bd.getListaRepositoriosRegistrados().contains(nombre))
				bd.deleteRepositorio(nombre);
			else
				throw new RuntimeException ("Repositorio no registrado");
		}
	}
	
	public int cerrarSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
		if(tipo==0) {
			if(bd.getListaClientesActivos().containsKey(nombre)) {
				identificador = bd.getListaClientesActivos().get(nombre);
				bd.getListaClientesActivos().remove(nombre);
			}
			else 
				throw new RuntimeException ("Cliente no logueado");
		}
		if(tipo==1) {
			if(bd.getListaRepositoriosLogueados().containsKey(nombre)) {
				bd.getListaRepositoriosLogueados().remove(nombre);
				if(bd.getListaRepositoriosActivos().containsKey(nombre))
					bd.getListaRepositoriosActivos().remove(nombre);
			}
			else 
				throw new RuntimeException ("Repositorio no logueado");
		}
		return identificador;
	}

	public static int getIdentificador() {
		return identificador++;
	}
	
	public boolean comprobarCliente (String nombre) throws RemoteException{
		return bd.getListaClientesActivos().containsKey(nombre);
	}
}
