/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package LibreriaWeb.libreria.repositorios;

import LibreriaWeb.libreria.repositorios.*;
import LibreriaWeb.libreria.entidades.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String>{
    @Query("SELECT c FROM Autor c WHERE nombre LIKE :nombre OR apellido LIKE :nombre")
    public Autor buscarAutor(@Param("nombre") String nombre);
}
