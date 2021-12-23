package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.servicios.*;
import LibreriaWeb.libreria.entidades.Editorial;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.repositorios.EditorialRepositorio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void registrar(String nombre) throws Exception {

        validar(nombre);

        Editorial editorial = new Editorial();

        editorial.setNombre(nombre);

        editorialRepositorio.save(editorial);
    }

    @Transactional
    public void modificar(String id, String nombre) throws Exception {

        validar(nombre);

        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();

            editorial.setNombre(nombre);

            editorialRepositorio.save(editorial);
        } else {
            throw new ErrorServicio("La editorial solicitada no se encontró.");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws Exception {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            if (editorial.getBaja() == null){
            editorial.setBaja(LocalDate.now());
            editorialRepositorio.save(editorial);
            }
        } else {
            throw new ErrorServicio("No se encontró el libro solicitado");
        }
    }

    @Transactional
    public void habilitar(String id) throws Exception {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setBaja(null);
            editorialRepositorio.save(editorial);
        } else {
            throw new ErrorServicio("No se encontró el libro solicitado");
        }
    }

    public void validar(String nombre) throws Exception {
        if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser un campo vacío.");
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

    public List<Editorial> buscarEditoriales(boolean conBaja) {
        List<Editorial> editoriales = editorialRepositorio.findAll();
        List<Editorial> editorialesInvalidas = new ArrayList<>();

        Iterator it = editoriales.iterator();

        if (conBaja){

        } else {
            while (it.hasNext()) {
                Editorial editorial = (Editorial) it.next();
                if (editorial.getBaja() != null) {
                    editorialesInvalidas.add(editorial);
                }
            }
            editoriales.removeAll(editorialesInvalidas);
        }
        return editoriales;
    }

    public List<Editorial> buscarEditoriales(String nombre) {
        List<Editorial> editoriales = em.createQuery("SELECT e from EDITORIAL e WHERE e.nombre LIKE :nombre").setParameter("nombre", "%" + nombre + "%").getResultList();
        List<Editorial> editorialesInvalidas = new ArrayList<>();

        Iterator it = editoriales.iterator();

        while (it.hasNext()) {
            Editorial editorial = (Editorial) it.next();
            if (editorial.getBaja() != null) {
                editorialesInvalidas.add(editorial);
            }
        }
        editoriales.removeAll(editorialesInvalidas);

        return editoriales;
    }

    public Editorial buscarEditorial(String id) {
        return em.find(Editorial.class, id);
    }

}
