package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDatosInterface;

public class ServicioDatosImpl implements ServicioDatosInterface{

	private List<String> usuarios_registrados;
	private List<String> repositorios_registrados;
	private HashMap<String, Integer> usuarios_activos;
	private HashMap<String, Integer> repositorios_logueados;
	private HashMap<String, Repositorio> repositorios_activos;
	private HashMap<String,String> repositorio_usuario;
	private HashMap<String, List<MetaFichero>> ficheros_usuario;
	
	public ServicioDatosImpl () {
		usuarios_registrados = new ArrayList<String>();
		repositorios_registrados = new ArrayList<String>();
		usuarios_activos = new HashMap<String,Integer>();
		repositorios_logueados = new HashMap<String,Integer>();
		repositorio_usuario = new HashMap<String,String>();
		ficheros_usuario = new HashMap<String,List<MetaFichero>>();
	}

	public void registrarCliente(String nombre) throws RemoteException {
		usuarios_registrados.add(nombre);
	}

	public void registrarRepositorio(String  nombre) throws RemoteException {
		repositorios_registrados.add(nombre);
	}

	public void deleteCliente(String nombre) throws RemoteException {
		usuarios_registrados.remove(usuarios_registrados.indexOf(nombre));
		unlinkRepositorio(nombre);
	}

	public void deleteRepositorio(String nombre) throws RemoteException {
		//Mantengo el repositorio activo por si durante la sesión el cliente quiere hacer algo más con él.
		repositorios_registrados.remove(repositorios_registrados.indexOf(nombre));
	}

	//Para añadir repositorio o usuario al Map de activos.
	public void addId(String nombre, int identificador, int tipo) throws RemoteException {
		int index = -1;
		if(tipo == 0) {
			usuarios_activos.put(nombre, identificador);
		}
		else
			repositorios_logueados.put(nombre,identificador);
	}

	@Override
	public Repositorio linkRepositorio(String nombre_cliente) throws RemoteException {
		Repositorio repo;
		if(!repositorios_logueados.isEmpty()) {
			//Añado un repositorio aleatorio a la lista de repositorios del usuario.
			int ialea = (int) Math.random()*repositorios_logueados.size();
			String [] keys = repositorios_logueados.keySet().toArray(new String[repositorios_logueados.keySet().size()]);
			String nombre_repositorio = keys[ialea];
			//No inicializo hasta que es necesario el repositorio, hasta este momento el usuario puede haber dado de alta e
			//iniciado sesion muchos repositorios pero cada objeto repositorio no se habrá inicializado hasta que un cliente lo
			//necesite.
			if(!repositorios_activos.containsKey(nombre_repositorio)) {				
				repo = new Repositorio(nombre_repositorio);
				repo.setId(repositorios_logueados.get(nombre_repositorio));
				repositorios_activos.put(nombre_repositorio, repo);
				System.out.println("Repositorio inicializado " + nombre_repositorio);
			}
			else
				repo = repositorios_activos.get(nombre_repositorio);
			repositorio_usuario.put(nombre_cliente,nombre_repositorio);
		}
		else 
			throw new RuntimeException ("No quedan repositorios libres, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
		return repo;
	}

	public void unlinkRepositorio(String nombre_cliente) throws RuntimeException {
		if(repositorio_usuario.containsKey(nombre_cliente)) {
			repositorio_usuario.remove(nombre_cliente);
		}
	}
	
	public List<String> getListaRepositoriosLinkados() throws RemoteException {
		List<String> repositorios_linkados = new ArrayList<String>();
		repositorios_linkados.addAll(repositorio_usuario.values());
		return repositorios_linkados;
	}
	
	public List<String> getListaRepositoriosRegistrados() throws RemoteException {
		return repositorios_registrados;
	}
	
	public  HashMap<String, Repositorio> getListaRepositoriosActivos() throws RemoteException {
		return repositorios_activos;
	} 
	
	public  HashMap<String, Integer> getListaRepositoriosLogueados() throws RemoteException {
		return repositorios_logueados;
	} 
	
	//PENDIENTE DE VER COMO SON LOS ARCHIVOS DEL EQUIPO DOCENTE 
	public List<MetaFichero> getListaFicheros(int sesion) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getListaClientesRegistrados() throws RemoteException {
		return usuarios_registrados;
	}

	public HashMap<String, Integer> getListaClientesActivos() throws RemoteException {
		return usuarios_activos;
	}
	
	public int getIdCliente(String nombre) throws RemoteException {
		return usuarios_activos.get(nombre);
	}
	
	public int getIdRepositorio(String nombre) throws RemoteException {
		return repositorios_logueados.get(nombre);
	}
	
	public void addRepositorioActivo(Repositorio repo, String nombre_repositorio) throws RemoteException {
		repositorios_activos.put(nombre_repositorio, repo);
	}
	
	public Repositorio getRepositorioActivo (String nombre) throws RemoteException {
		return repositorios_activos.get(nombre);
	}
	
	public Repositorio getRepositorioUsuario(String nombre_cliente) throws RemoteException {
		String nombre_repositorio = repositorio_usuario.get(nombre_cliente);
		return repositorios_activos.get(nombre_repositorio);
	}
}
