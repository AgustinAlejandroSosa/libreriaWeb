package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.entidades.Libro;
import LibreriaWeb.libreria.entidades.Prestamos;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.repositorios.ClienteRepositorio;
import LibreriaWeb.libreria.repositorios.LibroRepositorio;
import LibreriaWeb.libreria.repositorios.PrestamoRepositorio;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PrestamoServicio {

    @Autowired
    PrestamoRepositorio prestamoRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    LibroRepositorio libroRepositorio;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void renovarDevolucion(String idPrestamo, LocalDate devolucion) throws ErrorServicio {
        if (devolucion.isBefore(LocalDate.now())) {
            throw new ErrorServicio("La fecha de devolución debe ser válida");
        }
        
        Optional<Prestamos> resultado = prestamoRepositorio.findById(idPrestamo);
        if (resultado.isPresent()) {
            Prestamos prestamo = resultado.get();
            prestamo.setDevolucion(devolucion);
        } else {
            throw new ErrorServicio("El préstamo solicitado no está registrado.");
            
        }
    }

    @Transactional
    public void comprobarMultas() throws ErrorServicio {
        List<Prestamos> prestamos = prestamoRepositorio.findAll();
        Iterator<Prestamos> it = prestamos.iterator();
        
        while(it.hasNext()){
            Prestamos prestamo = it.next();
            if(prestamo.getDevolucion().isBefore(LocalDate.now())){
                prestamo.setMulta(500);
            }
        }
    }

    @Transactional
    public void registrar(LocalDate fecha, LocalDate devolucion, String idCliente, String idLibro) throws ErrorServicio {
        validarFechas(fecha, devolucion);
        validarCliente(idCliente);
        validarLibro(idLibro);

        Cliente cliente = clienteRepositorio.getById(idCliente);
        Libro libro = libroRepositorio.getById(idLibro);
        
        libro.setEjemplares(libro.getEjemplares()-1);
        libro.setPrestados(libro.getPrestados()+1);
        
        Prestamos prestamo = new Prestamos();
        prestamo.setMulta(0);
        prestamo.setFecha(fecha);
        prestamo.setDevolucion(devolucion);
        prestamo.addCliente(cliente);
        prestamo.addLibro(libro);
    }

    @Transactional
    public void modificar(String idPrestamo, LocalDate devolucion, double multa, String idCliente, String idLibro) throws ErrorServicio {
        Optional<Prestamos> resultado = prestamoRepositorio.findById(idPrestamo);
        if (resultado.isPresent()) {

            validarLibro(idLibro);
            validarCliente(idCliente);
            Libro libro = libroRepositorio.getById(idLibro);
            Cliente cliente = clienteRepositorio.getById(idCliente);

            if (devolucion.isBefore(LocalDate.now())) {
                throw new ErrorServicio("La fecha de devolución debe ser posterior a la fecha actual.");
            }

            Prestamos prestamo = resultado.get();
            prestamo.setDevolucion(devolucion);
            prestamo.setMulta(multa);
            prestamo.addCliente(cliente);
            prestamo.addLibro(libro);
        } else {
            throw new ErrorServicio("El prestamo solicitado no está registrado.");
        }
    }

    @Transactional
    public void eliminar(String idPrestamo) throws ErrorServicio {
        Optional<Prestamos> resultado = prestamoRepositorio.findById(idPrestamo);
        if (resultado.isPresent()) {
            Prestamos prestamo = resultado.get();
            prestamo.removeCliente();
            prestamo.removeLibro();
            prestamoRepositorio.deleteById(idPrestamo);
        } else {
            throw new ErrorServicio("No se encontró el préstamo solicitado");
        }
    }

    public void validarFechas(LocalDate fecha, LocalDate devolucion) throws ErrorServicio {

        if (fecha == null || devolucion == null) {
            throw new ErrorServicio("Las fechas no pueden ser nulas");
        }

        if (fecha.isBefore(LocalDate.now())){
            throw new ErrorServicio("La fecha del préstamo no puede ser anterior a la fecha actual");
        }
        
        if (devolucion.isBefore(fecha)){
            throw new ErrorServicio("La fecha de devolución no puede ser anterior a la fecha de préstamo");
        }
        
        if (devolucion.isBefore(LocalDate.now())){
            throw new ErrorServicio("La fecha de devolución no puede ser anterior a hoy");
        }
    
    }

    public void validarCliente(String idCliente) throws ErrorServicio {
        Optional<Cliente> resultado = clienteRepositorio.findById(idCliente);
        if (!resultado.isPresent()) {
            throw new ErrorServicio("El cliente solicitado no está registrado.");
        }
    }

    public void validarLibro(String idLibro) throws ErrorServicio {
        Optional<Libro> resultado = libroRepositorio.findById(idLibro);
        if (!resultado.isPresent()) {
            throw new ErrorServicio("El libro solicitado no está registrado.");
        }else{
            Libro libro = resultado.get();
            if(libro.getEjemplares() == 0){
                throw new ErrorServicio ("No hay ejemplares disponibles");
            }
        }
    }

    public List<Prestamos> buscarPrestamos(Cliente cliente) {
        
        return em.createQuery("SELECT p FROM Prestamos p WHERE p.cliente LIKE :cliente").setParameter("cliente", cliente).getResultList();
    }

    public List<Prestamos> buscarPrestamos() {
        return prestamoRepositorio.findAll();
    }

    public Prestamos buscarPrestamo(String id) {
        return em.find(Prestamos.class, id);
    }
}
