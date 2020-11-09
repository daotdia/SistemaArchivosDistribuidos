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
	private HashMap<String, Integer> repositorios_activos;
	private HashMap<Integer,String> repositorio_usuario;
	private HashMap<Integer, List<MetaFichero>> ficheros_usuario;
	
	public ServicioDatosImpl () {
		usuarios_registrados = new ArrayList<String>();
		repositorios_registrados = new ArrayList<String>();
		usuarios_activos = new HashMap<String,Integer>();
		repositorios_activos = new HashMap<String,Integer>();
		repositorio_usuario = new HashMap<Integer,String>();
		ficheros_usuario = new HashMap<Integer,List<MetaFichero>>();
	}

	public void registrarCliente(String nombre) throws RemoteException {
		usuarios_registrados.add(nombre);
	}

	public void registrarRepositorio(String  nombre) throws RemoteException {
		repositorios_registrados.add(nombre);
	}

	public void deleteCliente(String nombre) throws RemoteException {
		usuarios_registrados.remove(usuarios_registrados.indexOf(nombre));
	}

	public void deleteRepositorio(String nombre) throws RemoteException {
		repositorios_registrados.remove(repositorios_registrados.indexOf(nombre));
	}

	//Para añadir repositorio o usuario al Map de activos.
	public void addId(String nombre, int identificador, int tipo) throws RemoteException {
		int index = -1;
		if(tipo == 0) {
			usuarios_activos.put(nombre, identificador);
		}
		else
			repositorios_activos.put(nombre,identificador);
	}

	@Override
	public int linkRepositorio(int id_cliente) throws RemoteException {
		int identificador = -1;
		if(!repositorios_activos.isEmpty()) {
			//Añado un repositorio aleatorio a la lista de repositorios del usuario.
			int ialea = (int) Math.random()*repositorios_activos.size();
			String [] keys = repositorios_activos.keySet().toArray(new String[repositorios_activos.keySet().size()]);
			String nombre = keys[ialea];
			repositorio_usuario.put(id_cliente,nombre);
			//Asocio el identificador con el identificador del repositorio añadido.
			identificador = repositorios_activos.get(keys[ialea]);
		}
		else 
			throw new RuntimeException ("No quedan repositorios libres, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
		return identificador;
	}

	public void unlinkRepositorio(int id_cliente) throws RuntimeException {
		if(!repositorio_usuario.containsKey(id_cliente)) {
			repositorio_usuario.remove(id_cliente);
		}
	}

	public Map<Integer, String> getRepositorioCliente(int id_cliente) throws RemoteException {
		return new HashMap<Integer,String>(){{
			put(id_cliente, repositorio_usuario.get(id_cliente));
		}};
	}
	
	public List<String> getListaRepositoriosLinkados() throws RemoteException {
		List<String> repositorios_linkados = new ArrayList<String>();
		repositorios_linkados.addAll(repositorio_usuario.values());
		return repositorios_linkados;
	}
	
	public List<String> getListaRepositoriosRegistrados() throws RemoteException {
		return repositorios_registrados;
	}
	
	public  HashMap<String, Integer> getListaRepositoriosActivos() throws RemoteException {
		return repositorios_activos;
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
}
