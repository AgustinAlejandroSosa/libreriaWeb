
package LibreriaWeb.libreria.entidades;

import LibreriaWeb.libreria.errores.ErrorServicio;
import java.io.Serializable;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @NotBlank(message="No puede ser una cadena vacía.")
    @Size(min=5,message="El dni no puede tener menos de 5 caracteres.")
    private String dni;
    
    @NotBlank(message="No puede ser una cadena vacía.")
    @Size(min=8,max=15, message="El nombre debe contener entre 8 y 15 caracteres.")
    private String nombre;
    
    @NotBlank(message="No puede ser una cadena vacía.")
    @Size(min=8,max=25, message="La clave debe contener entre 8 y 25 caracteres.")
    private String clave;
    
    @NotBlank(message="No puede ser una cadena vacía.")
    @Size(min=4,max=25, message="La clave debe contener entre 4 y 25 caracteres.")
    private String domicilio;
    
    @NotEmpty
    @Size(min=2,max=25, message="El telefono debe contener entre 8 y 25 caracteres.")
    private String telefono;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente",fetch = FetchType.EAGER )
    private List<Prestamos> prestamos = new ArrayList<>();
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate baja;

    public List<Prestamos> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamos> prestamos) {
        this.prestamos = prestamos;
    }
    
    public void addPrestamo(Prestamos prestamo)throws ErrorServicio{
        if (prestamo == null){
            throw new ErrorServicio("El prestamo no puede ser nulo");
        }
        if (!prestamos.contains(prestamo)){
            prestamos.add(prestamo);
            prestamo.addCliente(this);
        }
    }
    
    public void removePrestamo(Prestamos prestamo)throws ErrorServicio{
        if (prestamo == null){
            throw new ErrorServicio("El prestamo no puede ser nulo");
        }
        if (prestamos.contains(prestamo)){
            prestamos.remove(prestamo);
            prestamo.removeCliente();
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
     * @return the dni
     */
    public String getDni() {
        return dni;
    }

    /**
     * @param dni the dni to set
     */
    public void setDni(String dni) {
        this.dni = dni;
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
     * @return the domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * @param domicilio the domicilio to set
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        int hash = 3;
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
        final Cliente other = (Cliente) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.dni, other.dni)) {
            return false;
        }
        if (!Objects.equals(this.telefono, other.telefono)) {
            return false;
        }
        if (!Objects.equals(this.prestamos, other.prestamos)) {
            return false;
        }
        if (!Objects.equals(this.baja, other.baja)) {
            return false;
        }
        return true;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
    
}
