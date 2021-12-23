package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.repositorios.ClienteRepositorio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class ClienteServicio implements UserDetailsService {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void registrar(String dni, String nombre, String domicilio, String telefono, String clave) throws ErrorServicio {

        validar(dni, nombre, domicilio, telefono);
        validarNombre(nombre);

        Cliente cliente = new Cliente();

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        cliente.setClave(encriptada);
        cliente.setDni(dni);
        cliente.setDomicilio(domicilio);
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);

        clienteRepositorio.save(cliente);
    }

    @Transactional
    public void modificar(String id, String dni, String nombre, String domicilio, String telefono, String clave, LocalDate baja) throws ErrorServicio {

        validar(dni, nombre, domicilio, telefono);
        validarNombre(nombre);

        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            cliente.setClave(encriptada);
            cliente.setDni(dni);
            cliente.setDomicilio(domicilio);
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setBaja(baja);

            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("El cliente solicitado no se encontró.");
        }
    }

    @Transactional
    public void modificar(String id, String dni, String nombre, String domicilio, String telefono, LocalDate baja) throws ErrorServicio {

        validar(dni, nombre, domicilio, telefono);
        validarNombre(nombre);

        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();

            cliente.setDni(dni);
            cliente.setDomicilio(domicilio);
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setBaja(baja);

            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("El cliente solicitado no se encontró.");
        }
    }

    @Transactional
    public void deshabilitar(String dni) throws ErrorServicio {
        Optional<Cliente> respuesta = clienteRepositorio.findById(dni);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            if (cliente.getBaja() == null) {
                cliente.setBaja(LocalDate.now());
                clienteRepositorio.save(cliente);
            }else{
                cliente.setBaja(null);
                clienteRepositorio.save(cliente);
            }
        } else {
            throw new ErrorServicio("No se encontró el cliente solicitado");
        }
    }

    @Transactional
    public void habilitar(String dni) throws Exception {
        Optional<Cliente> respuesta = clienteRepositorio.findById(dni);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setBaja(null);
            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("No se encontró el cliente solicitado");
        }
    }

    @Transactional
    public List<Cliente> buscarClientes() throws ErrorServicio {
        return clienteRepositorio.findAll();
    }

    @Transactional
    public List<Cliente> buscarClientes(String nombre) throws ErrorServicio {
        return em.createQuery("SELECT c FROM Cliente c WHERE c.nombre LIKE :nombre OR c.dni LIKE :nombre OR c.domicilio LIKE :nombre OR c.telefono LIKE :nombre").setParameter("nombre", "%" + nombre + "%").getResultList();
    }

    @Transactional
    public List<Cliente> buscarClientePorDni(String dni) {
        List<Cliente> cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.dni LIKE :dni").setParameter("dni", "%" + dni + "%").getResultList();
        if (cliente.size() > 0) {
            return cliente;
        } else {
            return null;
        }
    }

    public void validar(String dni, String nombre, String domicilio, String telefono) throws ErrorServicio {
        if (dni == null || dni.trim().isEmpty()) {
            throw new ErrorServicio("El DNI no puede ser un campo vacío.");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser un campo vacío.");
        }

        if (domicilio == null || domicilio.trim().isEmpty()) {
            throw new ErrorServicio("El domicilio no puede ser un campo vacío.");
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            throw new ErrorServicio("Su número de teléfono no puede ser un campo vacío.");
        }
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Cliente cliente = buscarClientePorDni(dni).get(0);

        if (cliente != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            if (cliente.getNombre().equalsIgnoreCase("ADMIN")) {
                GrantedAuthority permiso1 = new SimpleGrantedAuthority("ROLE_ADMIN");
                permisos.add(permiso1);
            } else {
                GrantedAuthority permiso2 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
                permisos.add(permiso2);
            }
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            session.setAttribute("usuariosession", cliente);

            User user = new User(cliente.getDni(), cliente.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }

    public void validarNombre(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede estar vacío");
        }

        String caracteresEspeciales = "1234567890°!#$%&/()=?¡|¿'*[´+{^~¬;:<>]¨`}-";

        for (int i = 0; i < nombre.length(); i++) {
            for (int j = 0; j < caracteresEspeciales.length(); j++) {
                if (nombre.substring(i, i + 1).equalsIgnoreCase(caracteresEspeciales.substring(j, j + 1))) {
                    throw new ErrorServicio("El nombre tiene que estar compuesto de letras,espacios o guiones solamente.");
                }
            }
        }
    }

    public Cliente buscarCliente(String id) throws ErrorServicio {

        if (id == null || id.isEmpty()) {
            throw new ErrorServicio("El id no puede ser nulo");
        }
        return em.find(Cliente.class, id);
    }

    @Transactional
    public boolean validarLogin(String usuario, String password) throws ErrorServicio {

        List<Cliente> cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni AND c.clave = :password").setParameter("dni", usuario).setParameter("password", password).getResultList();
        return cliente.size() > 0;
    }
}
