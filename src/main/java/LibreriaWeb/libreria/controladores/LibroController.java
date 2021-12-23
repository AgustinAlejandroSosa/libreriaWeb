package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Autor;
import LibreriaWeb.libreria.entidades.Editorial;
import LibreriaWeb.libreria.entidades.Libro;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.AutorServicio;
import LibreriaWeb.libreria.servicios.EditorialServicio;
import LibreriaWeb.libreria.servicios.LibroServicio;
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
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/libro")
public class LibroController {

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private AutorServicio autorServicio;

    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String nombre, @RequestParam(required = false) String error, Model modelo) throws ErrorServicio {
        
        if (nombre != null) {

            List<Libro> libros = libroServicio.buscarLibros(nombre);
            if (libros.isEmpty()) {
                libros = null;
                modelo.addAttribute("libros", libros);
            } else {
                modelo.addAttribute("libros", libros);
            }
        } else {
            List<Libro> libros = libroServicio.buscarLibros(true);
            if (libros.isEmpty()) {
                libros = null;
                modelo.addAttribute("libros", libros);
            } else {
                modelo.addAttribute("libros", libros);
            }
        }
        if (error != null) {
            modelo.addAttribute("error", error);
        }

        return "libro.html";
    }

    @GetMapping("nuevo-libro")
    public String nuevoLibro(Model modelo) throws ErrorServicio {

        List<Autor> autores = autorServicio.buscarAutores();

        List<Editorial> editoriales = editorialServicio.buscarEditoriales(false);

        modelo.addAttribute("autores", autores);

        modelo.addAttribute("editoriales", editoriales);

        return "añadir-libro.html";
    }

    @PostMapping("/crear-libro")
    public String crearLibro(@ModelAttribute("libro") Libro libro,MultipartFile archivo, @RequestParam("idEditorial") String idEditorial, @RequestParam("idAutor") String idAutor) throws Exception {

        try {
            libroServicio.registrar(archivo,libro.getTitulo(), libro.getAnio(), libro.getEjemplares(), idEditorial, idAutor);
        } catch (ErrorServicio e) {
            return "redirect:/libro/listado?error=" + e.getMessage();
        }
        return "redirect:/libro/listado";
    }

    @GetMapping("/actualizar/{isbn}")
    public String actualizar(@PathVariable("isbn") String isbn, Model modelo) throws ErrorServicio {

        try {
            Libro libro = libroServicio.buscarLibro(isbn);
            modelo.addAttribute("libro", libro);
        } catch (Exception e) {
            return "redirect:/libro/listado?error=" + e.getMessage();
        }
        return "actualizar-libro.html";
    }

    @PostMapping("/actualizacion")
    public String actualizacion(@ModelAttribute("libro") Libro libro,MultipartFile archivo) throws Exception {

        libroServicio.modificar(archivo,libro.getTitulo(), libro.getIsbn(), libro.getAnio(), libro.getEjemplares(), libro.getPrestados());

        return "redirect:/libro/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") String id) throws Exception {
        
        Libro libro = libroServicio.buscarLibro(id);
        
        if(libro.getBaja()==null){
            libroServicio.deshabilitar(id);
        }else{
            libroServicio.habilitar(id);
        }
        
        
        
        return "redirect:/libro/listado";
    }

}
