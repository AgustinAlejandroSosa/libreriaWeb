package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Autor;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.AutorServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorServicio autorService;
    
    @GetMapping("/listado")
    public String autor(@RequestParam(required = false) String nombre,@RequestParam(required = false) String error, Model model) {

        if (nombre != null) {
            List<Autor> autores = autorService.buscarAutores(nombre);
            model.addAttribute("autores", autores);
        } else {
            List<Autor> autores = autorService.buscarAutores();
            model.addAttribute("autores", autores);
        }
        if(error != null ){
            model.addAttribute("error", error);
        }
        
        return "autor.html";
    }
    
  
    @GetMapping("/nuevo-autor")
    public String nuevoAutor()throws ErrorServicio{
        
        return "añadir-autor.html";
    }
    
    @PostMapping("/crear-autor")
    public String crearAutor(@RequestParam String nombre,@RequestParam String apellido)throws ErrorServicio {
        
        try{
        autorService.registarAutor(nombre, apellido);
        }catch(Exception e){
            return "redirect:/autor/listado?error=" + e.getMessage();
        }
        
        return "redirect:/autor/listado" ;
    }

    @GetMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id")String id, Model model)throws ErrorServicio{
        
        try{
        model.addAttribute("autor", autorService.buscarAutor(id));
        }catch(Exception e){
            return "redirect:/autor/listado?error=No se pudo modificar el autor.";
        }
        return "actualizar-autor.html";
    }
    
    @PostMapping("/actualizacion")
    public String actualizado(@ModelAttribute("autor")Autor autor)throws ErrorServicio{
        
        Autor autorActualizado = autorService.buscarAutor(autor.getId());
        autorActualizado.setNombre(autor.getNombre());
        autorActualizado.setApellido(autor.getApellido());
        autorService.actualizarAutor(autor.getId(), autorActualizado.getNombre(), autorActualizado.getApellido());
        
        return "redirect:/autor/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") String id,Model model)throws ErrorServicio {
        
        try {
            autorService.eliminar(id);
            return "redirect:/autor/listado";
        } catch (Exception ex) {
            return "redirect:/autor/listado?error=No se pudo eliminar el autor.";
        }
    }

}
