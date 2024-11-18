package org.example.wallapop.Controller;

import jakarta.validation.Valid;
import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Service.AnuncioService;
import org.example.wallapop.Service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final FotoService fotoService;

    @Autowired
    public AnuncioController(AnuncioService anuncioService, FotoService fotoService) {
        this.anuncioService = anuncioService;
        this.fotoService = fotoService;
    }

    @GetMapping("/")
    public String findAll(Model model) {
        model.addAttribute("anuncios", anuncioService.obtenerTodosAnuncios());
        // Retornar el nombre de la vista a la que se enviarán los datos
        return "anuncio-list";
    }
    @GetMapping("/anuncios/new")
    public String newAnuncio(Model model) {
        model.addAttribute("producto", new Anuncio());
        //model.addAttribute("categorias", productoService.findAllCategorias());
        return "anuncio-new";
    }

    @PostMapping("/anuncios/new")
    public String newAnuncioInsert(Model model, @Valid Anuncio anuncio,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "archivosFotos", required = false) List<MultipartFile> fotos) {
        /*if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", productoService.findAllCategoriasSorted());
            return "anuncio-new";
        }*/

        //Guardar fotos
        try {
            fotoService.guardarFotos(fotos, anuncio);
        }catch (IllegalArgumentException ex) {
            //model.addAttribute("categorias", productoService.findAllCategoriasSorted());
            model.addAttribute("mensaje", ex.getMessage());
            return "anuncio-new";
        }

        //Guardar producto
        anuncioService.saveAnuncio(anuncio);
        return "redirect:/productos";
    }
}