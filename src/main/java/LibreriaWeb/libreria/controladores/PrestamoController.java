package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.entidades.Libro;
import LibreriaWeb.libreria.entidades.Prestamos;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.ClienteServicio;
import LibreriaWeb.libreria.servicios.LibroServicio;
import LibreriaWeb.libreria.servicios.PrestamoServicio;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/prestamo")
public class PrestamoController {
    
    @Autowired
    PrestamoServicio prestamoServicio;
    
    @Autowired
    LibroServicio libroServicio;
    
    @Autowired
    ClienteServicio clienteServicio;
    
    @GetMapping("/listado")
    public String listado(@RequestParam(required = false)String error,Model modelo)throws ErrorServicio{
        
        
            prestamoServicio.comprobarMultas();
            List<Prestamos> prestamos = prestamoServicio.buscarPrestamos();
            modelo.addAttribute("prestamos",prestamos);
        
        if(error != null){
            modelo.addAttribute("error",error);
        }
        
        return "prestamo.html";
    }
    
    @GetMapping("nuevo-prestamo")
    public String nuevoPrestamo(Model modelo)throws ErrorServicio{
        
        try{
        List<Libro> libros = libroServicio.buscarLibros(false);
        
        List<Cliente> clientes = clienteServicio.buscarClientes();
        
        modelo.addAttribute("clientes",clientes);
        modelo.addAttribute("libros",libros);
        
        }catch(Exception e){
            return "redirect:/prestamo/listado?error=" + e.getMessage();
        }
        return "añadir-prestamo.html";
    }
    
    @PostMapping("/crear-prestamo")
    public String crearPrestamo(@RequestParam ("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,@RequestParam ("devolucion") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate devolucion,
    @RequestParam String idLibro,@RequestParam String idCliente)throws Exception{
        
        try{
        prestamoServicio.registrar(fecha, devolucion, idCliente, idLibro);
        
        }catch(ErrorServicio e){
            return "redirect:/prestamo/listado?error=" + e.getMessage();
        }
        return "redirect:/prestamo/listado";
    }
    
    @GetMapping("/actualizar/{id}")
    public String actualizar(@PathVariable ("id")String id,Model modelo)throws ErrorServicio{
        
        try {
            Prestamos prestamo = prestamoServicio.buscarPrestamo(id);
            modelo.addAttribute("prestamo", prestamo);
        } catch (Exception e) {
            return "redirect:/prestamo/listado?error=No se pudo modificar el cliente.";
        }
        return "actualizar-prestamo.html";
    }
    
    @PostMapping("/actualizacion")
    public String actualizacion(@ModelAttribute("prestamo")Prestamos prestamo)throws Exception{
        
        try{
        prestamoServicio.renovarDevolucion(prestamo.getId(), prestamo.getDevolucion());
        }catch(Exception e){
            return "redirect:/prestamo/listado?error=No se pudo actualizar el prestamo.";
        }
        return "redirect:/prestamo/listado";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable ("id") String id)throws Exception{
        
        prestamoServicio.eliminar(id);
        
        return "redirect:/prestamo/listado";
    }
}
