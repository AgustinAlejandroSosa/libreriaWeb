
package LibreriaWeb.libreria.controladores;

import LibreriaWeb.libreria.entidades.Libro;
import LibreriaWeb.libreria.errores.ErrorServicio;
import LibreriaWeb.libreria.servicios.LibroServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/foto")
public class FotoController {
    
    @Autowired
    LibroServicio libroServicio;
    
    @GetMapping("/libro/{id}")
    public ResponseEntity<byte[]> fotoLibro(@PathVariable("id") String id){
        
        try {
            Libro libro = libroServicio.buscarLibro(id);
            if(libro.getFoto()==null){
                throw new ErrorServicio("El libro no tiene una foto asignada.");
            }
            byte[] foto = libro.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            return new ResponseEntity<>(foto,headers,HttpStatus.OK);
            
        } catch (ErrorServicio e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }
}
