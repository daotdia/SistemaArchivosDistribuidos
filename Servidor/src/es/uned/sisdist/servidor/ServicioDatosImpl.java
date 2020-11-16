package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uned.sisdist.common.CustomExceptions;
import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDatosInterface;

public class ServicioDatosImpl implements ServicioDatosInterface{

	private List<String> usuarios_registrados;
	private List<String> repositorios_registrados;
	private HashMap<String, Integer> usuarios_activos;
	private HashMap<String, Integer> repositorios_logueados;
	private HashMap<String, Repositorio> repositorios_activos;
	private HashMap<String,List<String>> repositorio_usuario;
	//HashMao para cada usuario, con sus repositorios activos linkados que tienen archivos.
	private HashMap<String, HashMap<String,List<MetaFichero>>> ficheros_usuario;
	
	public ServicioDatosImpl () {
		usuarios_registrados = new ArrayList<String>();
		repositorios_registrados = new ArrayList<String>();
		usuarios_activos = new HashMap<String,Integer>();
		repositorios_logueados = new HashMap<String,Integer>();
		repositorio_usuario = new HashMap<String,List<String>>();
		ficheros_usuario = new HashMap<String, HashMap<String,List<MetaFichero>>>();
		repositorios_activos = new HashMap<String, Repositorio>();
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
		if(tipo == 0) {
			usuarios_activos.put(nombre, identificador);
		}
		else
			repositorios_logueados.put(nombre,identificador);
	}

	@Override
	public Repositorio linkRepositorio(String nombre_cliente) throws CustomExceptions {
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
			if(repositorio_usuario.get(nombre_cliente) == null) {
				repositorio_usuario.put(nombre_cliente,new ArrayList<String>() {
					private static final long serialVersionUID = 1L;
				{
					add(nombre_repositorio);
				}});
				repo = repositorios_activos.get(nombre_repositorio);
				System.out.println("Repositorios inicializados para cliente y linkado repositorio");
			}
			else
				if(!repositorio_usuario.get(nombre_cliente).contains(nombre_repositorio)) {
					repositorio_usuario.get(nombre_cliente).add(nombre_repositorio);
					repo = repositorios_activos.get(nombre_repositorio);
				}
				else {
					boolean bandera = false;
					for(Map.Entry<String, Repositorio> entrada : repositorios_activos.entrySet()) {
						if(!repositorio_usuario.get(nombre_cliente).contains(entrada.getKey())) {
							repositorio_usuario.get(nombre_cliente).add(entrada.getKey());
							repo = repositorios_activos.get(entrada.getKey());
							bandera = true;
						}
					}
					if (bandera == false) {
						throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio ");
					}
				}	   	
		}
		else 
			throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
		return repo;
	}

	public void unlinkRepositorio(String nombre_cliente) throws RuntimeException {
		if(repositorio_usuario.containsKey(nombre_cliente)) {
			repositorio_usuario.remove(nombre_cliente);
		}
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
	
	public List<MetaFichero> getListaFicheros(String nombre_cliente) throws RemoteException {
		List<MetaFichero> ficheros = new ArrayList<MetaFichero>();
		if(ficheros_usuario.get(nombre_cliente) != null) {
			for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.get(nombre_cliente).entrySet()) {
				for(MetaFichero fichero : entrada.getValue()) {	
				ficheros.add(fichero);
				}
			}
		}
		return ficheros;
	}

	public List<String> getListaFicherosCliente (String nombre_cliente) throws RemoteException, RuntimeException {
		List<String> nombres_ficheros = new ArrayList<String>();
		String nombre_fichero;
		System.out.println("Lista de Repositorios y ficheros del cliente en obtención");
		HashMap<String,List<MetaFichero>> ficheros_cliente = ficheros_usuario.get(nombre_cliente);
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_cliente.entrySet()) {
			for(MetaFichero meta : entrada.getValue()) {
				nombre_fichero = meta.getNombre();
				if(!nombres_ficheros.contains(nombre_fichero)) {
					nombres_ficheros.add(nombre_fichero);
				}
			}
		}
		System.out.println("Lista de Repositorios y ficheros del cliente obtenido");
		return nombres_ficheros;
	}
	
	public Repositorio getRepositorioFichero (String nombre_fichero, String nombre_cliente) throws RemoteException {
		for(Map.Entry<String, List<MetaFichero>> ficheros_repositorio : ficheros_usuario.get(nombre_cliente).entrySet()) {	
			for(MetaFichero fichero : ficheros_repositorio.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					return repositorios_activos.get(ficheros_repositorio.getKey());
					}
				}
			}
		throw new RuntimeException ("No se ha encontrado el archivo");
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
	
	public List<Repositorio> getRepositoriosUsuario(String nombre_cliente) throws RemoteException {
		List<String> nombres_repositorios = repositorio_usuario.get(nombre_cliente);
		List<Repositorio> repositorios_usuario = new ArrayList<Repositorio>();
		for(String nombre_repo : nombres_repositorios) {
			repositorios_usuario.add(repositorios_activos.get(nombre_repo));
		}
		return repositorios_usuario;
	}
	
	public void addMetaFichero(String nombre_repositorio, MetaFichero metafichero, String nombre_cliente) throws RemoteException {
		if(!ficheros_usuario.containsKey(nombre_cliente))
			ficheros_usuario.put(nombre_cliente, new HashMap<String,List<MetaFichero>>() {
				private static final long serialVersionUID = 1L;
			{
				put(nombre_repositorio, new ArrayList<MetaFichero>() {
					private static final long serialVersionUID = 1L;
				{
					add(metafichero);
				}});
			}});
		else
			ficheros_usuario.get(nombre_cliente).get(nombre_repositorio).add(metafichero);
	}
	
	public List<String> getListaClientesRepositorio (String nombre_repositorio) throws RemoteException{
		List<String> usuarios_repositorio = new ArrayList<String>();
		for(Map.Entry<String, List<String>> entrada : repositorio_usuario.entrySet()){
			for(String nombre_repo : entrada.getValue()) {
				if(nombre_repo.equals(nombre_repositorio))
					usuarios_repositorio.add(entrada.getKey());
			}
		}
		return usuarios_repositorio;
	}
	
	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException{
		List<MetaFichero> ficheros = ficheros_usuario.get(nombre_cliente).get(nombre_repositorio);
		List<String> nombre_ficheros = new ArrayList<String>();
		for(MetaFichero fichero : ficheros) {
			nombre_ficheros.add(fichero.getNombre());
		}
		return nombre_ficheros;
	}
}
