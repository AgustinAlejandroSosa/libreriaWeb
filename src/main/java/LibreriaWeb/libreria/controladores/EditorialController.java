
package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Cliente;
import LibreriaWeb.libreria.entidades.Editorial;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.ClienteServicio;
import LibreriaWeb.libreria.servicios.EditorialServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/editorial")
public class EditorialController {
    

    @Autowired
    EditorialServicio editorialServicio;
    
    @GetMapping("/listado")
    public String listado(@RequestParam(required = false)String nombre,@RequestParam(required = false)String error,Model modelo)throws ErrorServicio{
        
        if(nombre != null){
            
            List<Editorial> editoriales = editorialServicio.buscarEditoriales(nombre);
            modelo.addAttribute("editoriales",editoriales);
        }else{
            List<Editorial> editoriales = editorialServicio.buscarEditoriales(true);
            modelo.addAttribute("editoriales",editoriales);
        }
        if(error != null){
            modelo.addAttribute("error",error);
        }
        
        return "editorial.html";
    }
    
    @GetMapping("nueva-editorial")
    public String nuevaEditorial()throws ErrorServicio{
        return "añadir-editorial.html";
    }
    
    @PostMapping("/crear-editorial")
    public String crearEditorial(@ModelAttribute ("editorial")Editorial editorial)throws Exception
    {
        try{
        editorialServicio.registrar(editorial.getNombre());
        }catch(Exception e){
            return "redirect:/editorial/listado?error=" + e.getMessage();
        }
        
        return "redirect:/editorial/listado";
    }
    
    @GetMapping("/actualizar/{id}")
    public String actualizar(@PathVariable ("id")String id,Model modelo)throws ErrorServicio{
        
        try {
            Editorial editorial = editorialServicio.buscarEditorial(id);
            modelo.addAttribute("editorial", editorial);
        } catch (Exception e) {
            return "redirect:/editorial/listado?error=No se pudo modificar la editorial.";
        }
        return "actualizar-editorial.html";
    }
    
    @PostMapping("/actualizacion")
    public String actualizacion(@ModelAttribute("editorial")Editorial editorial)throws Exception{
        
        try{
        editorialServicio.modificar(editorial.getId(), editorial.getNombre());
        }catch(Exception e){
            return "redirect:/editorial/listado?error=" + e.getMessage();
        }
        return "redirect:/editorial/listado";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable ("id") String id)throws Exception{
        
        editorialServicio.deshabilitar(id);
        
        return "redirect:/editorial/listado";
    }
}
