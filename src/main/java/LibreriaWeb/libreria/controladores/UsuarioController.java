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
import javax.servlet.http.HttpSession;
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

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Autowired
    private LibroServicio libroServicio;

    @GetMapping("/")
    public String usuario(@RequestParam(required = false) String error, HttpSession session, Model modelo) throws ErrorServicio {

        Cliente cliente = (Cliente) session.getAttribute("usuariosession");

        String nombre = cliente.getNombre();

        modelo.addAttribute("nombre", nombre);

        return "usuario.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/libros")
    public String libros(@RequestParam(required = false) String error, Model modelo) throws ErrorServicio {

        List<Libro> libros = libroServicio.buscarLibros(false);

        modelo.addAttribute("libros", libros);

        if (libros.size() <= 0 || libros.isEmpty()) {

            error = "Aún no se han registrado libros.";
            modelo.addAttribute("error", error);
        }

        return "biblioteca.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/prestamos")
    public String prestamos(HttpSession session, @RequestParam(required = false) String error, Model modelo) throws ErrorServicio {

        Cliente cliente = (Cliente) session.getAttribute("usuariosession");
        
        List<Prestamos> prestamos = prestamoServicio.buscarPrestamos(cliente);
        
        modelo.addAttribute("cliente", cliente);

        modelo.addAttribute("prestamos", prestamos);

        return "usuario-prestamos.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/solicitar-prestamo/{id}")
    public String solicitarPrestamo(HttpSession session, @PathVariable("id") String id, @RequestParam(required = false) String error, Model modelo) {

        Cliente cliente = (Cliente) session.getAttribute("usuariosession");

        Libro libro = libroServicio.buscarLibro(id);

        modelo.addAttribute("libro", libro);
        modelo.addAttribute("cliente", cliente);

        if (error != null) {
            modelo.addAttribute("error", error);
        }

        return "solicitar-prestamo.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registrar-prestamo")
    public String registrarPrestamo(@RequestParam String idLibro, @RequestParam String idCliente, @RequestParam("devolucion") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate devolucion, Model modelo) throws ErrorServicio {

        prestamoServicio.registrar(LocalDate.now(), devolucion, idCliente, idLibro);

        String prestamo = "Se ha registrado un nuevo prestamo";

        modelo.addAttribute("prestamo", prestamo);

        return "redirect:/usuario/perfil?id=" + idCliente + "&prestamo=" + prestamo;
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/perfil")
    public String perfil(@RequestParam(required = false) String prestamo, @RequestParam String id, @RequestParam(required = false) String actualizado, Model modelo) throws ErrorServicio {

        Cliente cliente = clienteServicio.buscarCliente(id);

        modelo.addAttribute("prestamo", prestamo);
        modelo.addAttribute("actualizado", actualizado);
        modelo.addAttribute("cliente", cliente);
        modelo.addAttribute("prestamos", cliente.getPrestamos().size());

        return "perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(@RequestParam(required = false) String error, HttpSession session, @RequestParam String id, Model modelo) throws ErrorServicio {

        Cliente logeado = (Cliente) session.getAttribute("usuariosession");
        if (logeado == null || !logeado.getId().equals(id)) {
            return "redirect:/usuario/";
        }
        if (error != null) {
            modelo.addAttribute("error", error);
        }
        Cliente usuario = clienteServicio.buscarCliente(id);

        modelo.addAttribute("usuario", usuario);

        return "actualizar-perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/editar-check")
    public String editarCheck(@RequestParam(required = false) String password2, @RequestParam(required = false) String newpassword, HttpSession session, @ModelAttribute("usuario") Cliente usuario) throws ErrorServicio {

        Cliente logeado = (Cliente) session.getAttribute("usuariosession");
        if (logeado == null || !logeado.getId().equals(usuario.getId())) {
            return "redirect:/home/logout";
        }

        try {

            if (usuario.getNombre().equalsIgnoreCase("ADMIN")) {
                return "redirect:/usuario/editar-perfil?id=" + usuario.getId() + "&error=No se permite utilizar ese nombre.";
            }

            if (newpassword.isEmpty() || newpassword.trim().isEmpty() || newpassword == null) {
                clienteServicio.modificar(usuario.getId(), usuario.getDni(), usuario.getNombre(), usuario.getDomicilio(), usuario.getTelefono(), usuario.getBaja());
                return "redirect:/usuario/perfil?id=" + usuario.getId() + "&actualizado=Perfil actualizado correctamente.";
            } else {
                if (!password2.equalsIgnoreCase(newpassword)) {
                    return "redirect:/usuario/editar-perfil?id=" + usuario.getId() + "&error=Las claves no coinciden";

                } else {
                    clienteServicio.modificar(usuario.getId(), usuario.getDni(), usuario.getNombre(), usuario.getDomicilio(), usuario.getTelefono(), newpassword, usuario.getBaja());
                    return "redirect:/usuario/perfil?id=" + usuario.getId() + "&actualizado=Perfil actualizado correctamente.";
                }
            }

        } catch (Exception e) {
            return "redirect:/usuario/editar-perfil?id=" + usuario.getId() + "&error=" + e.getMessage();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/solicitar-prestamo")
    public String solicitarPrestamo(@RequestParam(required = false) String error, Model model, HttpSession session) {

        return "solicitar-prestamo.html";
    }
}
