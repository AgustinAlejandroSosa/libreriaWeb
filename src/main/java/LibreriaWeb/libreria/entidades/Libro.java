/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Libro implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String isbn;
    
    @NotBlank(message="No puede ser una cadena vacía")
    @Size(min=2,max=25, message="El titulo debe contener entre 2 y 25 caracteres.")
    private String titulo;
    
    @Min(1)
    private Integer anio;
    
    @Min(1)
    private Integer ejemplares;
    
    @Min(0)
    private Integer prestados;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate baja;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "libro")
    private List<Prestamos> prestamos = new ArrayList<>();

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @Valid
    private Autor autor;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @Valid
    private Editorial editorial;
    
    @OneToOne
    private Foto foto;
    
    public void addPrestamo(Prestamos prestamo)throws ErrorServicio {
        if (prestamo == null){
            throw new ErrorServicio("El prestamo no puede ser nulo");
        }
        if (!prestamos.contains(prestamo)){
            prestamos.add(prestamo);
            prestamo.addLibro(this);
        }
    }
    
    public void removePrestamo(Prestamos prestamo)throws ErrorServicio{
        if (prestamo == null){
            throw new ErrorServicio("El prestamo no puede ser nulo");
        }
        if (prestamos.contains(prestamo)){
            prestamos.remove(prestamo);
            prestamo.removeLibro();
        }
    }
    
    public void addAutor(Autor autor) throws ErrorServicio {
        if (autor == null) {
            throw new ErrorServicio("El autor no puede ser nulo");
        }
        if (this.autor != autor) {
            removeAutor();
            this.autor = autor;
            autor.addLibro(this);
        }
    }

    public void removeAutor() throws ErrorServicio {
        if (this.autor != null) {
            Autor autor = this.autor;
            this.autor = null;
            autor.removeLibro(this);
        }
    }

    public void addEditorial(Editorial editorial) throws ErrorServicio {
        if (editorial == null) {
            throw new ErrorServicio("La editorial no puede ser nula");
        }
        if (this.editorial != editorial){
            removeEditorial();
            this.editorial = editorial;
            editorial.addLibro(this);
        }
    }
    
    public void removeEditorial() throws ErrorServicio{
        
        if (this.editorial != null){
            Editorial editorial = this.editorial;
            this.editorial = null;
            editorial.removeLibro(this);
        }
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
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
        final Libro other = (Libro) obj;
        if (!Objects.equals(this.isbn, other.isbn)) {
            return false;
        }
        if (!Objects.equals(this.titulo, other.titulo)) {
            return false;
        }
        if (!Objects.equals(this.anio, other.anio)) {
            return false;
        }
        if (!Objects.equals(this.ejemplares, other.ejemplares)) {
            return false;
        }
        if (!Objects.equals(this.prestados, other.prestados)) {
            return false;
        }
        if (!Objects.equals(this.baja, other.baja)) {
            return false;
        }
        if (!Objects.equals(this.prestamos, other.prestamos)) {
            return false;
        }
        if (!Objects.equals(this.autor, other.autor)) {
            return false;
        }
        if (!Objects.equals(this.editorial, other.editorial)) {
            return false;
        }
        return true;
    }
    
    /**
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @param isbn the isbn to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the ejemplares
     */
    public Integer getEjemplares() {
        return ejemplares;
    }

    /**
     * @param ejemplares the ejemplares to set
     */
    public void setEjemplares(Integer ejemplares) {
        this.ejemplares = ejemplares;
    }

    /**
     * @return the prestados
     */
    public Integer getPrestados() {
        return prestados;
    }

    /**
     * @param prestados the prestados to set
     */
    public void setPrestados(Integer prestados) {
        this.prestados = prestados;
    }

    /**
     * @return the autor
     */
    public Autor getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public List<Prestamos> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamos> prestamos) {
        this.prestamos = prestamos;
    }

    /**
     * @return the editorial
     */
    public Editorial getEditorial() {
        return editorial;
    }

    /**
     * @param editorial the editorial to set
     */
    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
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

}
