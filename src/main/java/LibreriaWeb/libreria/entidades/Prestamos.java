package LibreriaWeb.libreria.entidades;

import LibreriaWeb.libreria.errores.ErrorServicio;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Prestamos implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate devolucion;
    
    private double multa;
    
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @Valid
    private Cliente cliente;
    
    @ManyToOne (cascade = CascadeType.ALL, optional = false)
    @Valid
    private Libro libro;
    
    public void addLibro(Libro libro)throws ErrorServicio{
        if (libro == null){
            throw new ErrorServicio("El libro no puede ser nulo");
        }
        if (this.libro != libro){
            removeLibro();
            this.libro = libro;
            libro.addPrestamo(this);
        }
    }
    
    public void removeLibro()throws ErrorServicio{
        if (this.libro != null){
            Libro libro = this.libro;
            this.libro = null;
            libro.removePrestamo(this);
        }
    }
    
    public void addCliente(Cliente cliente)throws ErrorServicio{
        if(cliente == null){
            throw new ErrorServicio("El cliente no puede ser nulo");
        }
        if (this.cliente != cliente){
            removeCliente();
            this.cliente = cliente;
            cliente.addPrestamo(this);
        }
    }
    
    public void removeCliente()throws ErrorServicio{
        if (cliente != null){
            Cliente cliente = this.cliente;
            this.cliente = null;
            cliente.removePrestamo(this);
        }
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
     * @return the fecha
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the devolucion
     */
    public LocalDate getDevolucion() {
        return devolucion;
    }

    /**
     * @param devolucion the devolucion to set
     */
    public void setDevolucion(LocalDate devolucion) {
        this.devolucion = devolucion;
    }

    /**
     * @return the multa
     */
    public double getMulta() {
        return multa;
    }

    /**
     * @param multa the multa to set
     */
    public void setMulta(double multa) {
        this.multa = multa;
    }

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    /**
     * @return the libro
     */
    public Libro getLibro() {
        return libro;
    }
    
    /**
     * @param libro the libro to set
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final Prestamos other = (Prestamos) obj;
        if (Double.doubleToLongBits(this.multa) != Double.doubleToLongBits(other.multa)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.fecha, other.fecha)) {
            return false;
        }
        if (!Objects.equals(this.devolucion, other.devolucion)) {
            return false;
        }
        if (!Objects.equals(this.cliente, other.cliente)) {
            return false;
        }
        if (!Objects.equals(this.libro, other.libro)) {
            return false;
        }
        return true;
    }
    
    
}
