package LibreriaWeb.libreria.entidades;

import LibreriaWeb.libreria.errores.ErrorServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Autor {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @NotBlank(message="No puede ser una cadena vacía")
    @Size(min=2,max=15, message="El nombre debe contener entre 2 y 15 caracteres.")
    private String nombre;
    
    @NotBlank(message="No puede ser una cadena vacía")
    @Size(min=2,max=15, message="El nombre debe contener entre 2 y 15 caracteres.")
    private String apellido;
    
    @OneToMany (cascade = CascadeType.ALL, mappedBy = "autor")
    private List<Libro> libros = new ArrayList<>();

    public void addLibro(Libro libro)throws ErrorServicio{
        if (!libros.contains(libro)){
            libros.add(libro);
        }
    }
    
    public void removeLibro (Libro libro)throws ErrorServicio{
        if (libros.contains(libro)){
            libros.remove(libro);
            libro.removeAutor();
        }
    }
    
    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Autor other = (Autor) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.apellido, other.apellido)) {
            return false;
        }
        if (!Objects.equals(this.libros, other.libros)) {
            return false;
        }
        return true;
    }
    
    
}
