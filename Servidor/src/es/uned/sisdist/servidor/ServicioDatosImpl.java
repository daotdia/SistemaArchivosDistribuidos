package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	//HashMao para cada usuario, con sus repositorios  activos linkados que tienen archivos compartidos.
	private HashMap<String, HashMap<String,List<MetaFichero>>> ficheros_compartidos;
	private static int siguiente_repositorio;
	private static int port_cliente = 2100;
	
	public ServicioDatosImpl () throws RemoteException {
		usuarios_registrados = new ArrayList<String>();
		repositorios_registrados = new ArrayList<String>();
		usuarios_activos = new HashMap<String,Integer>();
		repositorios_logueados = new HashMap<String,Integer>();
		repositorio_usuario = new HashMap<String,List<String>>();
		ficheros_usuario = new HashMap<String, HashMap<String,List<MetaFichero>>>();
		repositorios_activos = new HashMap<String, Repositorio>();
		ficheros_compartidos = new HashMap<String, HashMap<String,List<MetaFichero>>>();
		siguiente_repositorio = 0;
		
	}

	public void registrarCliente(String nombre) throws RemoteException {
		usuarios_registrados.add(nombre);
	}

	public void registrarRepositorio(String  nombre) throws RemoteException {
		repositorios_registrados.add(nombre);
	}

	public void deleteCliente(String nombre) throws RemoteException {
		Iterator<String> it = usuarios_registrados.iterator();
		while(it.hasNext()) {
			if(it.next().equals(nombre)) {
				it.remove();
			}
		}
		unlinkRepositorio(nombre);
	}

	public void deleteRepositorio(String nombre) throws RemoteException {
		repositorios_activos.remove(nombre);
		repositorios_registrados.remove(repositorios_registrados.indexOf(nombre));
		for (Map.Entry<String, List<String>> entrada : repositorio_usuario.entrySet()) {
			List<String> repos = entrada.getValue();
			if(repos.contains(nombre)) {
				repos.remove(nombre);
			}
		}
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
	public Repositorio linkRepositorio(String nombre_cliente) throws CustomExceptions, RemoteException {
		Repositorio repositorio;
		String nombre_repositorio;
		if(!repositorios_logueados.isEmpty()) {
			if(repositorio_usuario.get(nombre_cliente) == null) {
				if(siguiente_repositorio == repositorios_logueados.size() - 1) {
					siguiente_repositorio = 0;
				} else { 
					siguiente_repositorio++;
				}
				String [] keys = repositorios_logueados.keySet().toArray(new String[repositorios_logueados.keySet().size()]);
				nombre_repositorio = keys[siguiente_repositorio];
				repositorio_usuario.put(nombre_cliente,new ArrayList<String>());
				repositorio_usuario.get(nombre_cliente).add(nombre_repositorio);
				if(!repositorios_activos.containsKey(nombre_repositorio)) {
					repositorio = new Repositorio(nombre_repositorio);
					repositorios_activos.put(nombre_repositorio, repositorio);
					repositorio.setId(repositorios_logueados.get(nombre_repositorio)); 
				}
				else {
					repositorio = repositorios_activos.get(nombre_repositorio);
				}
				System.out.println("Repositorios inicializados para cliente y linkado repositorio");
				return repositorio;
			}
			else {
				for (Map.Entry<String, Integer> repo: repositorios_logueados.entrySet()) {
					nombre_repositorio = repo.getKey();
					if(!repositorio_usuario.get(nombre_cliente).contains(nombre_repositorio)) {
						if(!repositorios_activos.containsKey(nombre_repositorio)) { 				
							repositorio = new Repositorio(nombre_repositorio);
							repositorio.setId(repositorios_logueados.get(nombre_repositorio)); 
							repositorios_activos.put(repo.getKey(), repositorio);
							System.out.println("Repositorio inicializado " + nombre_repositorio);
						}
						else {
							repositorio = repositorios_activos.get(nombre_repositorio);
						}
						return repositorio;
					}	
				}
			}
			throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio ");
		}
		else {
			throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
		}
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

	public List<Repositorio> getRepositoriosFichero(String nombre_fichero, String nombre_cliente) throws RemoteException {
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
		System.out.println("El numero de repositorios activos son: " + repositorios_activos.size());
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.get(nombre_cliente).entrySet()) {
			for(MetaFichero fichero : entrada.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					repositorios.add(repositorios_activos.get(entrada.getKey()));
				}
			}
		}
		if (repositorios.isEmpty()) {
			throw new RuntimeException ("No se ha encontrado archivo a eliminar en nignún repositorio activo");
		}
		return repositorios;
	}
	
	public List<MetaFichero> getListaFicherosCliente (String nombre_cliente) throws RemoteException, RuntimeException {
		List<MetaFichero> ficheros = new ArrayList<MetaFichero>();
		System.out.println("Lista de Repositorios y ficheros del cliente en obtención");
		if(ficheros_usuario.get(nombre_cliente) != null) {
			HashMap<String,List<MetaFichero>> ficheros_cliente = ficheros_usuario.get(nombre_cliente);
			for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_cliente.entrySet()) {
				if(entrada.getValue() != null) {
					for(MetaFichero meta : entrada.getValue()) {
						ficheros.add(meta);
					}
				}
			}
		}
		if(ficheros_compartidos.get(nombre_cliente) != null) {
			HashMap<String,List<MetaFichero>> archivos_compartidos =  ficheros_compartidos.get(nombre_cliente);
			for(Map.Entry<String,List<MetaFichero>> entrada : archivos_compartidos.entrySet()) {
				if(entrada.getValue() != null) {
					for(MetaFichero meta : entrada.getValue()) {
						ficheros.add(meta);
					}
				}
			}
		}
		System.out.println("Lista de Repositorios y ficheros del cliente obtenido");
		return ficheros;
	}
	
	public Repositorio getRepositorioFichero (String nombre_fichero, String nombre_cliente) throws RemoteException {
		if(ficheros_usuario.get(nombre_cliente) != null) {
			for(Map.Entry<String, List<MetaFichero>> ficheros_repositorio : ficheros_usuario.get(nombre_cliente).entrySet()) {	
				if (ficheros_repositorio.getValue() != null) {
					for(MetaFichero fichero : ficheros_repositorio.getValue()) {
						if(fichero.getNombre().equals(nombre_fichero)) {
							return repositorios_activos.get(ficheros_repositorio.getKey());
						}
					}
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
		if(!ficheros_usuario.containsKey(nombre_cliente)) {
			ficheros_usuario.put(nombre_cliente, new HashMap<String,List<MetaFichero>>() {
				private static final long serialVersionUID = 1L;
			{
				put(nombre_repositorio, new ArrayList<MetaFichero>() {
					private static final long serialVersionUID = 1L;
				{
					add(metafichero);
				}});
			}});
		}
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
	
	public void deleteFicheroCliente(String nombre_cliente,String nombre_repo, String nombre_fichero) throws RemoteException{
		List<MetaFichero> ficheros = ficheros_usuario.get(nombre_cliente).get(nombre_repo);
		Iterator<MetaFichero> it = ficheros.iterator();
		while(it.hasNext()) {
			if(it.next().getNombre().equals(nombre_fichero)) {
				it.remove();
			}
		}
	}
	
	public void cerrarSesionCliente (String nombre) throws RemoteException {
		usuarios_activos.remove(nombre);
	}
	
	public void cerraSesionRepositorio (String nombre) throws RemoteException {
		repositorios_logueados.remove(nombre);
	}
	
	public void addMetaFicheroCompartido (String nombre_repositorio, MetaFichero fichero, String nombre_destinatario) throws RemoteException {
		if(!ficheros_compartidos.containsKey(nombre_destinatario)) {
			ficheros_compartidos.put(nombre_destinatario, new HashMap<String,List<MetaFichero>>() {
				private static final long serialVersionUID = 1L;
			{
				put(nombre_repositorio, new ArrayList<MetaFichero>() {
					private static final long serialVersionUID = 1L;
				{
					add(fichero);
				}});
			}});
		}
		else
			ficheros_compartidos.get(nombre_destinatario).get(nombre_repositorio).add(fichero);
	}
	
	public int getPortCliente() throws RemoteException {
		return port_cliente++;
	}
}
