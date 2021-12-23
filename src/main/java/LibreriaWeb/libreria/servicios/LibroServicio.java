/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.entidades.Autor;
import LibreriaWeb.libreria.entidades.Editorial;
import LibreriaWeb.libreria.entidades.Foto;
import LibreriaWeb.libreria.entidades.Libro;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.repositorios.AutorRepositorio;
import LibreriaWeb.libreria.repositorios.EditorialRepositorio;
import LibreriaWeb.libreria.repositorios.LibroRepositorio;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Autowired
    private EditorialRepositorio editorialRepositorio;
    
    @Autowired
    private FotoServicio fotoServicio;
    
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void registrar(MultipartFile archivo, String titulo, Integer anio, Integer ejemplares, String idEditorial, String idAutor) throws ErrorServicio {
        validar(titulo, anio, ejemplares);

        Optional<Editorial> resultadoEditorial = editorialRepositorio.findById(idEditorial);
        Optional<Autor> resultadoAutor = autorRepositorio.findById(idAutor);

        if (resultadoAutor.isPresent() && resultadoEditorial.isPresent()) {
            Autor autor = resultadoAutor.get();
            Editorial editorial = resultadoEditorial.get();
            Libro libro = new Libro();

            libro.setTitulo(titulo);
            libro.addEditorial(editorial);
            libro.addAutor(autor);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setPrestados(0);
            
            Foto foto = fotoServicio.guardar(archivo);
            libro.setFoto(foto);
            
            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No hay registros de la editorial o autor asociado al libro.");
        }
    }

    @Transactional
    public void modificar(MultipartFile archivo,String titulo, String isbn, Integer anio, Integer ejemplares, Integer prestados, String idEditorial, String idAutor) throws ErrorServicio {
        validar(titulo, anio, ejemplares);

        Optional<Editorial> resultadoEditorial = editorialRepositorio.findById(idEditorial);
        Optional<Autor> resultadoAutor = autorRepositorio.findById(idAutor);

        if (!(resultadoEditorial.isPresent() && resultadoAutor.isPresent())) {
            throw new ErrorServicio("El autor o editorial asociados al libro no está registrado.");
        }

        Optional<Libro> respuesta = libroRepositorio.findById(isbn);
        if (respuesta.isPresent()) {

            Editorial editorial = resultadoEditorial.get();
            Autor autor = resultadoAutor.get();
            Libro libro = respuesta.get();

            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setPrestados(prestados);
            libro.addAutor(autor);
            libro.addEditorial(editorial);

            String idFoto = null;
            if (libro.getFoto()!=null){
                idFoto = libro.getFoto().getId();
            }
            
            Foto foto = fotoServicio.actualizarFoto(idFoto, archivo);
            libro.setFoto(foto);
            
            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontró el libro indicado.");
        }
    }
    
    @Transactional
    public void modificar(MultipartFile archivo,String titulo, String isbn, Integer anio, Integer ejemplares, Integer prestados) throws ErrorServicio {
        validar(titulo, anio, ejemplares);

        Optional<Libro> respuesta = libroRepositorio.findById(isbn);
        if (respuesta.isPresent()) {

            Libro libro = respuesta.get();

            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setPrestados(prestados);
            
            String idFoto = null;
            if (libro.getFoto()!=null){
                idFoto = libro.getFoto().getId();
            }
            
            Foto foto = fotoServicio.actualizarFoto(idFoto, archivo);
            libro.setFoto(foto);

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontró el libro indicado.");
        }
    }

    @Transactional
    public void deshabilitar(String isbn) throws Exception {
        Optional<Libro> respuesta = libroRepositorio.findById(isbn);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            if(libro.getBaja() == null){
            libro.setBaja(LocalDate.now());
            libroRepositorio.save(libro);
            }
        } else {
            throw new ErrorServicio("No se encontró el libro solicitado");
        }
    }

    @Transactional
    public void habilitar(String isbn) throws Exception {
        Optional<Libro> respuesta = libroRepositorio.findById(isbn);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setBaja(null);
            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontró el libro solicitado");
        }
    }
    
    @Transactional
    public List<Libro> buscarLibros(boolean conBaja) throws ErrorServicio{
        List<Libro> libros = libroRepositorio.findAll();
        List<Libro> librosInvalidos = new ArrayList<>();
        Iterator it = libros.iterator();
        
        if (conBaja){

        } else {
            while (it.hasNext()) {
                Libro libro = (Libro) it.next();
                if (libro.getBaja() != null) {
                    librosInvalidos.add(libro);
                }
            }
            libros.removeAll(librosInvalidos);
        }
        
        return libros;
    }
    
    @Transactional
    public List<Libro> buscarLibros(String nombre){
        return em.createQuery("SELECT l FROM Libro l WHERE l.titulo LIKE :nombre OR l.autor LIKE :nombre OR l.editorial LIKE :nombre").setParameter("nombre","%" + nombre + "%").getResultList();
    }
    
    @Transactional
    public Libro buscarLibro(String id){
        return em.find(Libro.class,id);
    }

    public void validar(String titulo, Integer anio, Integer ejemplares) throws ErrorServicio {
        if (titulo == null) {
            throw new ErrorServicio("El título no puede estar vacío.");
        }

        if (anio <= 0) {
            throw new ErrorServicio("El año debe ser un número válido.");
        }

        if (ejemplares <= 0) {
            throw new ErrorServicio("La cantidad de ejemplares no puede ser menor a la unidad (1).");
        }
    }

    public void validar(String titulo, Integer anio, Integer ejemplares, Integer prestados) throws Exception {
        if (titulo == null) {
            throw new ErrorServicio("El título no puede estar vacío.");
        }

        if (anio <= 0) {
            throw new ErrorServicio("El año debe ser un número válido.");
        }

        if (ejemplares <= 0) {
            throw new ErrorServicio("La cantidad de ejemplares no puede ser menor a la unidad (1).");
        }

        if (prestados < 0 || prestados > ejemplares) {
            throw new ErrorServicio("Valor inválido.");
        }
    }
}
