
package LibreriaWeb.libreria.entidades;

import LibreriaWeb.libreria.errores.ErrorServicio;
import java.io.Serializable;
import java.time.LocalDate;
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
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Editorial implements Serializable {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @NotBlank(message="No puede ser una cadena vacía.")
    @Size(min=8,max=15, message="El nombre debe contener entre 8 y 15 caracteres.")
    private String nombre;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate baja;

    @OneToMany ( cascade = CascadeType.ALL, mappedBy = "editorial")
    private List<Libro> libros = new ArrayList<>();

    public void addLibro(Libro libro)throws ErrorServicio{
        if (libro == null){
            throw new ErrorServicio("El libro no puede ser nulo");
        }
        if (!libros.contains(libro)){
            libros.add(libro);
            libro.addEditorial(this);
        }
    }
    
    public void removeLibro(Libro libro)throws ErrorServicio{
        if (libro == null){
            throw new ErrorServicio("El libro no puede ser nulo");
        }
        if (libros.contains(libro)){
            libros.remove(libro);
            libro.removeEditorial();
        }
    }
    
    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the baja
     */
    public LocalDate getBaja() {
        return baja;
    }

    /**
     * @param baja the baja to set
     */
    public void setBaja(LocalDate baja) {
        this.baja = baja;
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
        final Editorial other = (Editorial) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.baja, other.baja)) {
            return false;
        }
        if (!Objects.equals(this.libros, other.libros)) {
            return false;
        }
        return true;
    }
    
    
}
