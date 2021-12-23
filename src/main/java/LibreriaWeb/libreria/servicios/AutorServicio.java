package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.entidades.Autor;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.repositorios.AutorRepositorio;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorServicio {
    
    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private AutorRepositorio autorRepositorio;
    
    @Transactional
    public void registarAutor(String nombre, String apellido)throws ErrorServicio{
        validarNombres(nombre,apellido);
        Autor autor = new Autor();
        
        autor.setNombre(nombre);
        autor.setApellido(apellido);
        
        autorRepositorio.save(autor);
    }
    
    @Transactional
    public void actualizarAutor(String id, String nombre, String apellido) throws ErrorServicio{
        validarId(id);
        validarNombres(nombre,apellido);
        
        Optional<Autor> resultado = autorRepositorio.findById(id);
        
        if (resultado.isPresent()){
            Autor autor = resultado.get();
            autor.setApellido(apellido);
            autor.setNombre(nombre);
            autorRepositorio.save(autor);
        }else{
            throw new ErrorServicio("El autor solicitado no se encontró");
        }
        
    }
    
    @Transactional
    public void eliminar(String id) throws ErrorServicio{
            validarId(id);
            Optional<Autor> resultado = autorRepositorio.findById(id);
            
            if(resultado.isPresent()){
                Autor autor = resultado.get();
                autorRepositorio.deleteById(id);
            }else{
                throw new ErrorServicio("No se encontró el autor solicitado");
            }
    }
    
    public Autor buscarAutor(String id){
        return (Autor) em.createQuery("Select c FROM Autor c WHERE c.id LIKE :id").setParameter("id", id).getSingleResult();
    }
    
    public List<Autor> buscarAutores(String q){
        
        return em.createQuery("SELECT c FROM Autor c WHERE c.nombre LIKE :q OR c.apellido LIKE :q").setParameter("q", "%" + q + "%") .getResultList();
    }
    
    public List<Autor> buscarAutores(){
        return em.createQuery("SELECT c FROM Autor c").getResultList();
    }
    
    public void validarNombres(String nombre, String apellido)throws ErrorServicio{
        if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()){
            throw new ErrorServicio("El nombre no puede estar vacío");
        }
        
        if (apellido == null || nombre.isEmpty() || nombre.trim().isEmpty()){
            throw new ErrorServicio("El apellido no puede estar vacío");
        }
        
        String caracteresEspeciales = "1234567890°!#$%&/()=?¡|¿'*[´+{^~¬;:<>]¨`}-";
        
        for (int i = 0; i < nombre.length(); i++){
            for (int j = 0; j < caracteresEspeciales.length(); j++) {
                if (nombre.substring(i, i+1).equalsIgnoreCase(caracteresEspeciales.substring(j, j+1))){
                    throw new ErrorServicio("El nombre tiene que estar compuesto de letras,espacios o guiones solamente.");
                }
            }
        }
        for (int i = 0; i < apellido.length(); i++){
            for (int j = 0; j < caracteresEspeciales.length(); j++) {
                if (apellido.substring(i, i+1).equalsIgnoreCase(caracteresEspeciales.substring(j, j+1))){
                    throw new ErrorServicio("El apellido tiene que estar compuesto de letras,espacios o guiones solamente.");
                }
            }
        }
    }
    
    public void validarId(String id)throws ErrorServicio{
        
        if (id == null || id.isEmpty()){
            throw new ErrorServicio("El id no puede ser nulo");
        }
        
    }
}
