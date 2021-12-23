package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.ClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String nombre, @RequestParam(required = false) String error, Model modelo) throws ErrorServicio {

        if (nombre != null) {

            List<Cliente> clientes = clienteServicio.buscarClientes(nombre);
            modelo.addAttribute("clientes", clientes);
        } else {
            List<Cliente> clientes = clienteServicio.buscarClientes();
            modelo.addAttribute("clientes", clientes);
        }
        if (error != null) {
            modelo.addAttribute("error", error);
        }

        return "cliente.html";
    }

    @GetMapping("nuevo-cliente")
    public String nuevoCliente() throws ErrorServicio {
        return "añadir-cliente.html";
    }

    @PostMapping("/crear-cliente")
    public String crearCliente(@ModelAttribute("cliente") Cliente cliente) throws ErrorServicio {
        clienteServicio.registrar(cliente.getDni(), cliente.getNombre(), cliente.getDomicilio(), cliente.getTelefono(), cliente.getClave());

        return "redirect:/cliente/listado";
    }

    @GetMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") String id, Model modelo, @RequestParam(required = false) String error) throws ErrorServicio {

        try {
            Cliente cliente = clienteServicio.buscarCliente(id);
            modelo.addAttribute("cliente", cliente);
        } catch (Exception e) {
            return "redirect:/cliente/listado?error=No se pudo modificar el cliente.";
        }
        if (error != null) {
            modelo.addAttribute("error", error);
        }
        return "actualizar-cliente.html";
    }

    @PostMapping("/actualizacion")
    public String actualizacion(@ModelAttribute("cliente") Cliente cliente, @RequestParam(required = false) String password2) throws ErrorServicio {

        try {
            if (cliente.getClave().isEmpty() || cliente.getClave().trim().isEmpty() || cliente.getClave() == null) {
                clienteServicio.modificar(cliente.getId(), cliente.getDni(), cliente.getNombre(), cliente.getDomicilio(), cliente.getTelefono(), cliente.getBaja());
                return "redirect:/cliente/listado";
            } else {
                if (!password2.equalsIgnoreCase(cliente.getClave())) {
                    return "redirect:/cliente/actualizar/" + cliente.getId() + "&error=Las claves no coinciden";

                } else {
                    clienteServicio.modificar(cliente.getId(), cliente.getDni(), cliente.getNombre(), cliente.getDomicilio(), cliente.getTelefono(), cliente.getClave(), cliente.getBaja());
                    return "redirect:/cliente/listado";
                }
            }
        } catch (Exception e) {
            return "redirect:/cliente/listado?error=No se pudo modificar el cliente.";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String darDeBaja(@PathVariable("id") String id) throws ErrorServicio {

        Cliente cliente = clienteServicio.buscarCliente(id);
        
            clienteServicio.deshabilitar(id);
        
        return "redirect:/cliente/listado";
    }

}
