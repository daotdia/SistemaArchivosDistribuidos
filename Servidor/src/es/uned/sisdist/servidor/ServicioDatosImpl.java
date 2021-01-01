/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el servicio de datos del sistema de archivos distribuido, es la clase que es responsable de mantener y actualizar toda la información de los clientes
 * y repositorios del sistema.
 * 
 * */
package es.uned.sisdist.servidor;

import java.rmi.RemoteException;
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

	//Variables necesarias para guardar la información del sistema.
	//Lista de usuarios registrados.
	private List<String> usuarios_registrados;
	//Lista de repositorios registrados.
	private List<String> nombre_repositorios_registrados;
	//Map de usuarios logueados junto a su identificador de sesión.
	private HashMap<String, Integer> usuarios_activos;
	//Map de repositorios logueados junto a su identificador de sesión.
	private HashMap<String, Integer> repositorios_logueados;
	//Map de repositorios que ya han sido utilizados por añgún usuario.
	private HashMap<String, Repositorio> repositorios_activos;
	//MAp de los repositorios de cada usuario (sus nombres).
	private HashMap<String,List<String>> repositorio_usuario;
	//HashMao para cada usuario, con sus repositorios activos linkados que tienen archivos.
	private HashMap<String, HashMap<String,List<MetaFichero>>> ficheros_usuario;
	//HashMao para cada usuario, con sus repositorios  activos linkados que tienen archivos compartidos.
	private HashMap<String, HashMap<String,List<MetaFichero>>> ficheros_compartidos;
	
	//Variables auxiliares necesarias.
	private static int siguiente_repositorio;
	private static int port_cliente = 2100;
	private static int port_repositorio = 5555;
	
	public ServicioDatosImpl () throws RemoteException {
		//Inicializo las estructuras de datos declaradas anteriormente.
		usuarios_registrados = new ArrayList<String>();
		nombre_repositorios_registrados = new ArrayList<String>();
		usuarios_activos = new HashMap<String,Integer>();
		repositorios_logueados = new HashMap<String,Integer>();
		repositorio_usuario = new HashMap<String,List<String>>();
		ficheros_usuario = new HashMap<String, HashMap<String,List<MetaFichero>>>();
		repositorios_activos = new HashMap<String, Repositorio>();
		ficheros_compartidos = new HashMap<String, HashMap<String,List<MetaFichero>>>();
		siguiente_repositorio = 0;
	}
	
	private void deleteFicheroCompartido(String nombre_propietario, String nombre_fichero) {
		//Ver en cada usuario con ficheros compartidos.
		for(Map.Entry<String, HashMap<String, List<MetaFichero>>> usuario_comp : ficheros_compartidos.entrySet()) {
			//En cada uno de sus repoitorios.
			for(Map.Entry<String, List<MetaFichero>> repo_comp : usuario_comp.getValue().entrySet()) {
				//Sus ficheros compartidos.
				Iterator<MetaFichero> it_comp = repo_comp.getValue().iterator(); 
				while(it_comp.hasNext()) {
					//Comprobar para cada uno de ellos si se corresponde con el fichero a eliminar.
					if(it_comp.next().getNombre().equals(nombre_fichero)) {
						it_comp.remove();
					}
				}
			}
		}
	}			
	//Método para añadir un cliente en la lista de clientes registrados.
	public void registrarCliente(String nombre) throws RemoteException {
		usuarios_registrados.add(nombre);
	}

	//Método para añadir un repositorio a la lista de repositorios registrados.
	public void registrarRepositorio(String  nombre) throws RemoteException {
		nombre_repositorios_registrados.add(nombre);
	}
	
	//Para añadir repositorio o usuario al Map de activos (iniciar sesión).
	public void addId(String nombre, int identificador, int tipo) throws RemoteException {
		//Si es un usuario.
		if(tipo == 0) {
			usuarios_activos.put(nombre, identificador);
		}
		//Si es un repositorio.
		else
			repositorios_logueados.put(nombre,identificador);
	}

	//Método para eliminar a un usuario de los usuarios activos.
	public void cerrarSesionCliente (String nombre) throws RemoteException {
		usuarios_activos.remove(nombre);
	}
	
	//Método para eliminar a un repositorio de los repositorios logueados.
	public void cerraSesionRepositorio (String nombre) throws RemoteException {
		repositorios_logueados.remove(nombre);
	}
	
	//Método para eliminar un cliente de la lista de clientes registrados. 
	public void deleteCliente(String nombre) throws RemoteException {
		Iterator<String> it = usuarios_registrados.iterator();
		while(it.hasNext()) {
			if(it.next().equals(nombre)) {
				it.remove();
			}
		}
		//Cuando se elimina el cliente de la lista de registrados, también se debe deslinkar sus repositorios. 
		unlinkRepositorios(nombre);
		
		if(ficheros_usuario.get(nombre) != null) {
		//Hay que eliminar también los archivos compartidos a los usuarios con los que se haya compartido.
			for(Map.Entry<String, List<MetaFichero>> repo_prop : ficheros_usuario.get(nombre).entrySet()) {
				//Por cada fichero propio del usuario.
				for(MetaFichero fichero_prop : repo_prop.getValue()) {
					deleteFicheroCompartido(nombre, fichero_prop.getNombre());
				}
			}		
			
			//También hay que eliminar los ficheros del usuario.
			ficheros_usuario.remove(nombre);
		}
		//Y los ficheros que está compartiendo.
		if(ficheros_compartidos != null) {
			ficheros_compartidos.remove(nombre);
		}
	}

	//Método para eliminar un repositorio de la lista de repositorios registrados.
	public void deleteRepositorio(String nombre) throws RemoteException {
		//Se tiene que comprobar los ficheros que se van a eliminar para borrarlos de los usuaios afectados (también sus posibles compaticiones).
		for(Map.Entry<String, HashMap<String, List<MetaFichero>>> usuario : ficheros_usuario.entrySet()) {
			for(Map.Entry<String, List<MetaFichero>> repo : usuario.getValue().entrySet()){
				if(repo.getKey().equals(nombre)) {
					Iterator<MetaFichero> it = repo.getValue().iterator();
					while(it.hasNext()) {
						deleteFicheroCompartido(usuario.getKey(), it.next().getNombre());
					}
					repo.setValue(new ArrayList<MetaFichero>());
				}
			}
		}
		//si está áctivo se elimina también de los repositorios activos.
		nombre_repositorios_registrados.remove(nombre_repositorios_registrados.indexOf(nombre));
		//si está áctivo se elimina también de los repositorios activos.
		if(repositorios_activos.containsKey(nombre)) {
			repositorios_activos.remove(nombre);
		}
		//Se elimina también del Map de repositorios de los usiarios. 
		for (Map.Entry<String, List<String>> entrada : repositorio_usuario.entrySet()) {
			List<String> repos = entrada.getValue();
			if(repos.contains(nombre)) {
				repos.remove(nombre);
			}
		}
	}
	
	//Método para eliminar un fichero de un repositorio de un cliente.
	public void deleteFicheroCliente(String nombre_cliente,String nombre_repo, String nombre_fichero) throws RemoteException{
		List<MetaFichero> ficheros = ficheros_usuario.get(nombre_cliente).get(nombre_repo);
		Iterator<MetaFichero> it = ficheros.iterator();
		while(it.hasNext()) {
			if(it.next().getNombre().equals(nombre_fichero)) {
				it.remove();
			}
		}
		//Elimino la compartición con dicho archivo de todos los usuarios.
		deleteFicheroCompartido(nombre_cliente, nombre_fichero);
	}	

	//Método para linkar en repositorio a un cliente dado por parámetro.
	public Repositorio linkRepositorio(String nombre_cliente) throws CustomExceptions, RemoteException {
		Repositorio repositorio;
		String nombre_repositorio;
		//Si existen repositorios logueados.
		if(!repositorios_logueados.isEmpty()) {
			//Si el usuario no tiene todavía repositorios linkados.
			if(repositorio_usuario.get(nombre_cliente) == null) {
				//Se le asigna el siguiente repositorio.
				if(siguiente_repositorio == repositorios_logueados.size() - 1) {
					siguiente_repositorio = 0;
				} else { 
					siguiente_repositorio++;
				}
				String [] keys = repositorios_logueados.keySet().toArray(new String[repositorios_logueados.keySet().size()]);
				nombre_repositorio = keys[siguiente_repositorio];
				//Se le añade una nueva lista de repositorios y se añade el repositorio seleccionado.
				repositorio_usuario.put(nombre_cliente,new ArrayList<String>());
				repositorio_usuario.get(nombre_cliente).add(nombre_repositorio);
				//Devuelve el repositorio linkado.
				repositorio = repositorios_activos.get(nombre_repositorio);
				return repositorio;
			}
			//Si el usuario ya tiene repositorios linkados.
			else {
				//Obtiene los repositorios logueados.
				for (Map.Entry<String, Integer> repo: repositorios_logueados.entrySet()) {
					nombre_repositorio = repo.getKey();
					//Si el repositorio no está linkado ya al cliente.
					if(!repositorio_usuario.get(nombre_cliente).contains(nombre_repositorio)) {
						//Añade el repositorio al usuario.
						repositorio_usuario.get(nombre_cliente).add(nombre_repositorio);
						//Devuelve el repositorio.
						repositorio = repositorios_activos.get(nombre_repositorio);
						return repositorio;
					}
				
				}	
			}
			//Si se llega hasta aqui no hay repositorios logueados disponibles para el usuario.
			throw new CustomExceptions.NoHayRepositoriosLibres("No existen más repositorios logueados de los que ya tiene linkados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio ");
		}
		//Si no existen repositorios logueados.
		else {
			throw new CustomExceptions.NoHayRepositoriosRegistrados("No existen repositorios logueados, vuelva a intentarlo más tarde o inicialice un nuevo repositorio");
		}
	}

	//Método para deslinkar los repositorios de un cliente.
	public void unlinkRepositorios(String nombre_cliente) throws RuntimeException {
		if(repositorio_usuario.containsKey(nombre_cliente)) {
			repositorio_usuario.remove(nombre_cliente);
		}
	}
	
	//Método para añadir un repositorio al Map de repositorios activos.
	public void addRepositorioActivo(Repositorio repo, String nombre_repositorio) throws RemoteException {
		repositorios_activos.put(nombre_repositorio, repo);
	}
	
	//Método para añadir un metafichero en un repositorio determinado de un cliente. 
	public void addMetaFichero(String nombre_repositorio, MetaFichero metafichero, String nombre_cliente) throws RemoteException {
		//Si el cliente todavía no tiene ficheros a su nombre en dicho repositorio, se añade una nueva lista de ficheros y se añade el metafichero.
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
		//Si ya tiene ficheros pero no los tiene en el repositorio.
		else if (!ficheros_usuario.get(nombre_cliente).containsKey(nombre_repositorio)){
			ficheros_usuario.get(nombre_cliente).put(nombre_repositorio, new ArrayList<MetaFichero>() {
				private static final long serialVersionUID = 1L;
			{
				add(metafichero);
			}});
		}
		//En caso de que ya tenga ficheros y tenga parte de ellos en el repositorio, añade el fichero a la lista.
		else
			ficheros_usuario.get(nombre_cliente).get(nombre_repositorio).add(metafichero);
	}
	
	//Método para añadir los ficheros comàrtidos a un usuario destintatario.
	public void addMetaFicheroCompartido (String nombre_repositorio, MetaFichero fichero, String nombre_destinatario) throws RemoteException {
		//Si no tiene fichros compartidos.
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
		//Si ya tiene ficheros pero no los tiene en el repositorio.
		else if (!ficheros_compartidos.get(nombre_destinatario).containsKey(nombre_repositorio)){
			ficheros_compartidos.get(nombre_destinatario).put(nombre_repositorio, new ArrayList<MetaFichero>() {
				private static final long serialVersionUID = 1L;
			{
				add(fichero);
			}});
		}
		//Si tiene ficheros compartidos y parte en el repositorio.
		else
			ficheros_compartidos.get(nombre_destinatario).get(nombre_repositorio).add(fichero);
	}
	
	
	//Método para obtener los repositorios registrados.
	public List<String> getListaRepositoriosRegistrados() throws RemoteException {
		return nombre_repositorios_registrados;
	}
	
	//Método para obtener los repositorios activos.
	public  HashMap<String, Repositorio> getListaRepositoriosActivos() throws RemoteException {
		return repositorios_activos;
	} 
	
	//Método para obtener los repositorios logueados,
	public  HashMap<String, Integer> getListaRepositoriosLogueados() throws RemoteException {
		return repositorios_logueados;
	} 
	
	//Método para obtener los ficheros de un cliente.
	public List<MetaFichero> getListaFicheros(String nombre_cliente) throws RemoteException {
		List<MetaFichero> ficheros = new ArrayList<MetaFichero>();
		//Si tiene ficheros.
		if(ficheros_usuario.get(nombre_cliente) != null) {
			//Obtengo todos los fichero y devuelvo una lista con los mismos.
			for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.get(nombre_cliente).entrySet()) {
				for(MetaFichero fichero : entrada.getValue()) {	
				ficheros.add(fichero);
				}
			}
		}
		return ficheros;
	}

	//Método para obtener todos los repositorios de un fichero de un usuario (se utilizará para eliminar un fichero del sistema).
	public List<Repositorio> getRepositoriosFichero(String nombre_fichero, String nombre_cliente) throws RemoteException {
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
		//Itero por todos los repositorios con ficheros del cliente, en el caso de que coincida el nombre del fichero, el repositorio se añade a la lista de repositorios a devolver.
		for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_usuario.get(nombre_cliente).entrySet()) {
			for(MetaFichero fichero : entrada.getValue()) {
				if(fichero.getNombre().equals(nombre_fichero)) {
					repositorios.add(repositorios_activos.get(entrada.getKey()));
				}
			}
		}
		//Si no hay repositorios añadidos es que el archivo a eliminar no se ha encontrado en el sistema.
		if (repositorios.isEmpty()) {
			throw new RuntimeException ("No se ha encontrado archivo a eliminar en nignún repositorio activo");
		}
		return repositorios;
	}
	
	//Método para obtener de un fichero de un cliente uno de sus repositorios
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
	
	//Devuelve todos los ficheros de un cliente, incluidos los compartidos.
	public List<MetaFichero> getListaFicherosCliente (String nombre_cliente) throws RemoteException, RuntimeException {
		List<MetaFichero> ficheros = new ArrayList<MetaFichero>();
		//Si tiene ficheros.
		if(ficheros_usuario.get(nombre_cliente) != null) {
			HashMap<String,List<MetaFichero>> ficheros_cliente = ficheros_usuario.get(nombre_cliente);
			//Itero por cad auno de los repositorios con ficheros del cliente y añado los ficheros a la lista de metaficheros.
			for(Map.Entry<String,List<MetaFichero>> entrada : ficheros_cliente.entrySet()) {
				if(entrada.getValue() != null) {
					for(MetaFichero meta : entrada.getValue()) {
						ficheros.add(meta);
					}
				}
			}
		}
		//Si tiene ficheros compartidos.
		if(ficheros_compartidos.get(nombre_cliente) != null) {
			HashMap<String,List<MetaFichero>> archivos_compartidos =  ficheros_compartidos.get(nombre_cliente);
			//Itero por cad auno de los repositorios con ficheros compartidos del cliente y añado los ficheros a la lista de metaficheros.
			for(Map.Entry<String,List<MetaFichero>> entrada : archivos_compartidos.entrySet()) {
				if(entrada.getValue() != null) {
					for(MetaFichero meta : entrada.getValue()) {
						ficheros.add(meta);
					}
				}
			}
		}
		//Devuelvo los ficheros.
		return ficheros;
	}
	
	//Método para obtener los repositorio de un usuario.
	public List<Repositorio> getRepositoriosUsuario(String nombre_cliente) throws RemoteException {
		List<String> nombres_repositorios = repositorio_usuario.get(nombre_cliente);
		List<Repositorio> repositorios_usuario = new ArrayList<Repositorio>();
		for(String nombre_repo : nombres_repositorios) {
			repositorios_usuario.add(repositorios_activos.get(nombre_repo));
		}
		return repositorios_usuario;
	}
	
	//Método para obtener los clientes de un repositorio.
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
	
	//Método para obtener los ficheros de un cliente en un repositorio.
	public List<String> getFicherosClienteRepositorio(String nombre_cliente, String nombre_repositorio) throws RemoteException{
		List<MetaFichero> ficheros = ficheros_usuario.get(nombre_cliente).get(nombre_repositorio);
		List<String> nombre_ficheros = new ArrayList<String>();
		for(MetaFichero fichero : ficheros) {
			nombre_ficheros.add(fichero.getNombre());
		}
		return nombre_ficheros;
	}	
	
	//Método que devuelve la lista de usuarios registrados.
	public List<String> getListaClientesRegistrados() throws RemoteException {
		return usuarios_registrados;
	}

	//Método para obtener la lista de clientes logueados.
	public HashMap<String, Integer> getListaClientesActivos() throws RemoteException {
		return usuarios_activos;
	}
	
	//Método para obtener el identificador de sesión de un cliente.
	public int getIdCliente(String nombre) throws RemoteException {
		return usuarios_activos.get(nombre);
	}
	
	//Método para obtener el identificador de sesión de un repositorio.
	public int getIdRepositorio(String nombre) throws RemoteException {
		return repositorios_logueados.get(nombre);
	}
	
	//Método para obtener el repositorio logueado con nombre nombre.
	public Repositorio getRepositorioActivo (String nombre) throws RemoteException {
		return repositorios_activos.get(nombre);
	}
	
	//Método para obtener el identificador de puerto de un objeto remoto cliente, lo usaré al inicializar un cliente.
	public int getPortCliente() throws RemoteException {
		return port_cliente++;
	}
	
	//Método para obtener el identificador de puerto de un objeto remoto repositorio, lo usaré al inicialziar un repositorio.
	public int getPortRepositorio(String nombre_repositorio) throws RemoteException {
		//Como los repositorios tienen dos servicios remotos, es necesario guardar dos puertos por cada repositorio. 
		port_repositorio = port_repositorio + 2;
		Repositorio repositorio = new Repositorio(nombre_repositorio, port_repositorio);
		//El repositorio pasa de estar logueado a activo, listo para usarse remotamente.
		repositorios_activos.put(nombre_repositorio, repositorio);
		repositorio.setId(repositorios_logueados.get(nombre_repositorio)); 
		return port_repositorio;
	}

}
