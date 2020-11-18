package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface{

	private static int identificador = 0;
	
	private static ServicioDatosInterface bd;
	private static ServicioSrOperadorInterface sg;
	private static Registry registry;
	
	public ServicioAutenticacionImpl() throws Exception {
		registry = LocateRegistry.getRegistry(7777);
		ServicioAutenticacionImpl.bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
		ServicioAutenticacionImpl.sg = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
	}
	
	public boolean registrarObjeto(String nombre, int tipo) throws RemoteException {
		if(tipo == 0)	
			if(!bd.getListaClientesRegistrados().contains(nombre)) {
				bd.registrarCliente(nombre);
				return true;
			}
			else {
				return false;
			}
		if(tipo == 1)	
			if(!bd.getListaRepositoriosRegistrados().contains(nombre)) {
				bd.registrarRepositorio(nombre);
				return true;
			}
			else {
				return false;
			}
		return false;
	}

	public int iniciarSesion(String nombre, int tipo) throws RuntimeException, Exception {
		int sesion = -1;
		if(tipo == 0) {
			try {
				if(bd.getListaClientesRegistrados().contains(nombre)) {
					sesion = getIdentificador();
					System.out.println("tratando de iniciar sesion cliente");
					Repositorio repo = bd.linkRepositorio(nombre);
					System.out.println(repo.getNombre());
					sg.crearCarpeta(repo.getPath(), nombre);
					bd.addId(nombre, sesion, 0);
				}
				else {
					bd.getListaClientesActivos().remove(nombre);
					throw new RuntimeException ("Usuario no registrado");
				}
			} 
			catch (CustomExceptions.NoHayRepositoriosLibres e) {
				throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
			}
			catch (CustomExceptions.ObjetoNoRegistrado e) {
				throw new CustomExceptions.ObjetoNoRegistrado("Usuario no registrado");
			}
			catch (CustomExceptions.NoHayRepositoriosRegistrados e) {
				throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
			}
		}
		else if(tipo == 1) {
			if(bd.getListaRepositoriosRegistrados().contains(nombre)) {
				sesion = getIdentificador();
				bd.addId(nombre, sesion, 1);
			}
			else
				throw new RuntimeException ("Repositorio no registrado");
		}
		return sesion;
	}

	public int getIdSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
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
					sg.borrarCarpetaCliente(repo, nombre);
				}
				bd.deleteCliente(nombre);
			}
			else
				throw new RuntimeException ("Cliente no registrado");
		}
		if(tipo==1) {
			try {
			if(bd.getListaRepositoriosRegistrados().contains(nombre)) {
				bd.deleteRepositorio(nombre);
				if(bd.getRepositorioActivo(nombre) != null) {
					sg.borrarCarpetaRepositorio(bd.getRepositorioActivo(nombre).getNombre());
				}
				else
					throw new CustomExceptions.RepositorioTodaviaNoUtilizado("RepositorioTodaviaNoUtilizado");
			}
			else
				throw new CustomExceptions.ObjetoNoRegistrado("Repositorio no registrado");
			}
			catch (CustomExceptions.RepositorioTodaviaNoUtilizado e) {
				throw new CustomExceptions.RepositorioTodaviaNoUtilizado("RepositorioTodaviaNoUtilizado");
			}
			catch (CustomExceptions.ObjetoNoRegistrado e){
				throw new CustomExceptions.ObjetoNoRegistrado("Repositorio no registrado");
			}
		}
	}
	
	public int cerrarSesion(String nombre, int tipo) throws RemoteException {
		int identificador = -1;
		if(tipo==0) {
			if(bd.getListaClientesActivos().containsKey(nombre)) {
				identificador = bd.getListaClientesActivos().get(nombre);
				bd.getListaClientesActivos().remove(nombre);
				bd.unlinkRepositorio(nombre);
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
	
	public boolean comprobarCliente (String nombre_cliente) throws RemoteException{
		return bd.getListaClientesActivos().containsKey(nombre_cliente);
	}
	
	public boolean comprobarRepositorio (String nombre_repositorio) throws RemoteException{
		return bd.getListaRepositoriosLogueados().containsKey(nombre_repositorio);
	}
}
