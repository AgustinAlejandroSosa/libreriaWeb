package LibreriaWeb.libreria.servicios;

import LibreriaWeb.libreria.entidades.Foto;
import LibreriaWeb.libreria.repositorios.FotoRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fotoRepositorio;

    public Foto guardar(MultipartFile archivo) {

        if (archivo != null) {
            try {
                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());

                return fotoRepositorio.save(foto);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    public Foto actualizarFoto(String idFoto,MultipartFile archivo){
        if (archivo != null) {
            try {
                Foto foto = new Foto();
                if (idFoto != null){
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    if (respuesta.isPresent()){
                        foto = respuesta.get();
                    }
                }
                
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());

                return fotoRepositorio.save(foto);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
}
