package org.example.wallapop.Controller;

import jakarta.validation.Valid;
import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Entity.Usuario;
import org.example.wallapop.Service.AnuncioService;
import org.example.wallapop.Service.FotoService;
import org.example.wallapop.repository.AnuncioRepository;
import org.example.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final FotoService fotoService;
    private final UsuarioRepository usuarioRepository;
    private final AnuncioRepository anuncioRepository;

    @Autowired
    public AnuncioController(AnuncioService anuncioService, FotoService fotoService, UsuarioRepository usuarioRepository, AnuncioRepository anuncioRepository) {
        this.anuncioService = anuncioService;
        this.fotoService = fotoService;
        this.usuarioRepository = usuarioRepository;
        this.anuncioRepository = anuncioRepository;
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<Anuncio> anuncios = anuncioService.obtenerTodosAnuncios();
        model.addAttribute("anuncios",anuncios);
        // Retornar el nombre de la vista a la que se enviarán los datos
        model.addAttribute("totalAnuncios", anuncios.size());
        return "anuncio-list";
    }
    @GetMapping("/anuncios/new")
    public String newAnuncio(Model model) {
        model.addAttribute("anuncio", new Anuncio());
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
        anuncioService.saveAnuncio(anuncio);
        return "redirect:/";
    }
    @GetMapping("/anuncios/ver/{id}")
    public String verAnuncio(@PathVariable Long id, Model model) {
        Optional<Anuncio> anuncio = anuncioService.findAnuncioById(id);
        if (anuncio.isPresent()) {
            model.addAttribute("anuncio", anuncio.get());
            return "anuncio-ver";
        }
        return "redirect:/";
    }
    @GetMapping("/anuncios/del/{id}")
    public String delete(@PathVariable Long id) {
        anuncioService.deleteAnuncioById(id);
        return "redirect:/";
    }
    @GetMapping("/mis-anuncios")
    public String misAnuncios(Model model, Authentication auth) {
        // Obtener usuario conectado
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Pasar la lista de anuncios al modelo
        model.addAttribute("anuncios", anuncioRepository.findByUsuario(usuario));

        return "mis-anuncios"; // Nombre del template HTML
    }
    @PostMapping("/anuncios/borrar/{id}")
    public String borrarAnuncio(@PathVariable Long id, Authentication auth, RedirectAttributes redirect) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));

        if (!anuncio.getUsuario().getEmail().equals(auth.getName())) {
            redirect.addFlashAttribute("error", "No tienes permiso para borrar este anuncio");
            return "redirect:/mis-anuncios";
        }

        anuncioRepository.delete(anuncio);
        redirect.addFlashAttribute("success", "Anuncio borrado con éxito");
        return "redirect:/mis-anuncios";
    }

}
