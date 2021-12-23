package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.ClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home")
public class PortalController {

    @Autowired
    ClienteServicio clienteServicio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model modelo) {
        if (error != null) {
            modelo.addAttribute("error", "Usuario o clave incorrectos.");
        }
        return "login.html";
    }

    @GetMapping("/logout")
    public String logout(@RequestParam(required = false) String error, Model modelo) {

        return "redirect:/home/";
    }

    @PostMapping("/logincheck")
    public String loginCheck(@RequestParam String usuario, @RequestParam String password, Model model) throws Exception {

        try {
            if (clienteServicio.validarLogin(usuario, password)) {

                Cliente cliente = clienteServicio.buscarClientePorDni(usuario).get(0);

                if (cliente.getNombre().equalsIgnoreCase("ADMIN")) {
                    return "redirect:/home/inicio";
                } else {
                    return "redirect:/usuario/";
                }
            } else {
                return "redirect:/home/login?error=Usuario o clave incorrectos";
            }
        } catch (Exception e) {
            return "redirect:/home/login?error=Usuario o contraseña incorrectos" + e.getMessage();
        }
    }

    @GetMapping("/registro")
    public String registro(@RequestParam(required = false) String error, Model modelo) {

        modelo.addAttribute("error", error);

        return "registro.html";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam(required = false) String error, @RequestParam String nombre, @RequestParam String dni, @RequestParam String domicilio, @RequestParam String telefono, @RequestParam String password, @RequestParam String password2, Model modelo) throws ErrorServicio {

        modelo.addAttribute("error", error);

        if (!password.equalsIgnoreCase(password2)) {
            modelo.addAttribute("dni", dni);
            modelo.addAttribute("nombre", nombre);
            modelo.addAttribute("domicilio", domicilio);
            modelo.addAttribute("telefono", telefono);
            error = "Las claves no coinciden.";
            modelo.addAttribute("error", error);
            return "registro.html";
        }
        
        if(nombre.equalsIgnoreCase("ADMIN")){
            error = "No se puede utilizar ese nombre.";
            modelo.addAttribute("error",error);
            modelo.addAttribute("dni", dni);
            modelo.addAttribute("nombre", nombre);
            modelo.addAttribute("domicilio", domicilio);
            modelo.addAttribute("telefono", telefono);
            return "registro.html";
        }

        try {
            clienteServicio.registrar(dni, nombre, domicilio, telefono, password);
        } catch (ErrorServicio e) {
            modelo.addAttribute("dni", dni);
            modelo.addAttribute("nombre", nombre);
            modelo.addAttribute("domicilio", domicilio);
            modelo.addAttribute("telefono", telefono);
            return "registro.html";
        }

        return "registrado.html";
    }
}
