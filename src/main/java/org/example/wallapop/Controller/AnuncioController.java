package org.example.wallapop.Controller;

import jakarta.validation.Valid;
import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Entity.Categoria;
import org.example.wallapop.Entity.Usuario;
import org.example.wallapop.Service.AnuncioService;
import org.example.wallapop.Service.CategoriaService;
import org.example.wallapop.Service.FotoService;
import org.example.wallapop.Service.UsuarioService;
import org.example.wallapop.repository.AnuncioRepository;
import org.example.wallapop.repository.CategoriaRepository;
import org.example.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final FotoService fotoService;
    private final AnuncioRepository anuncioRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;

    @Autowired
    public AnuncioController(AnuncioService anuncioService, FotoService fotoService, AnuncioRepository anuncioRepository, UsuarioRepository usuarioRepository, UsuarioService usuarioService, CategoriaRepository categoriaRepository, CategoriaService categoriaService) {
        this.anuncioService = anuncioService;
        this.fotoService = fotoService;
        this.anuncioRepository = anuncioRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.categoriaRepository = categoriaRepository;
        this.categoriaService = categoriaService;
    }


    @GetMapping("/")
    public String findAll(@RequestParam(required = false) Long categoriaId, Model model) {
        List<Anuncio> anuncios;
        if (categoriaId != null && categoriaId > 0) {
            anuncios = anuncioService.obtenerAnunciosPorCategoria(categoriaId);
        } else {
            anuncios = anuncioService.obtenerTodosAnuncios();
        }

        model.addAttribute("anuncios", anuncios);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("totalAnuncios", anuncios.size());
        return "anuncio-list";
    }



    @GetMapping("/anuncios/new")
    public String newAnuncio(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        model.addAttribute("categorias", categoriaRepository.findAll()); // Añadimos las categorías
        return "anuncio-new";
    }

    @PostMapping("/anuncios/new")
    public String newAnuncioInsert(Model model, @Valid Anuncio anuncio,
                                   BindingResult bindingResult,
                                   @RequestParam(value = "archivosFotos", required = false) List<MultipartFile> fotos,
                                   @RequestParam(value = "categorias", required = false) List<Long> categoriasIds) {
        if (bindingResult.hasErrors()) {
            return "anuncio-new"; // Devuelve el formulario con los errores
        }

        try {
            // Obtener el usuario autenticado
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            // Buscar el usuario por su email
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Asignar el usuario al anuncio
            anuncio.setUsuario(usuario);
            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                List<Categoria> categorias = categoriaRepository.findAllById(categoriasIds);
                //solo me funcionaba con set, no se porque
                anuncio.setCategorias(new HashSet<>(categorias)); // Convertimos la lista en un Set
            }

            // Guardar las fotos asociadas al anuncio
            fotoService.guardarFotos(fotos, anuncio);

            // Guardar el anuncio en la base de datos
            anuncioService.saveAnuncio(anuncio);

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensaje", ex.getMessage());
            return "anuncio-new"; // Si ocurre un error al guardar las fotos, se muestra el mensaje de error
        }

        return "redirect:/"; // Redirigir al listado de anuncios
    }

    @GetMapping("/anuncios/ver/{id}")
    public String verAnuncio(@PathVariable Long id, Model model) {
        Optional<Anuncio> anuncio = anuncioService.findAnuncioById(id);
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(anuncio.get().getUsuario().getEmail());
        if (anuncio.isPresent()) {
            model.addAttribute("anuncio", anuncio.get());
            model.addAttribute("fotos", anuncio.get().getFotos());
            return "anuncio-ver";
        }
        model.addAttribute("mensaje", "El anuncio no existe.");
        return "redirect:/";
    }
    @GetMapping("/anuncios/del/{id}")
    public String delete(@PathVariable Long id) {
        anuncioService.deleteAnuncioById(id);
        return "redirect:/";
    }
    @GetMapping("/mis-anuncios")
    public String verMisAnuncios(Model model) {
        // Obtiene el email del usuario autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Anuncio> misAnuncios = anuncioService.obtenerAnunciosPorUsuario(email);
        model.addAttribute("misAnuncios", misAnuncios);
        return "mis-anuncios"; // Nombre de la vista para mis anuncios
    }
    @GetMapping("/anuncios/editar/{id}")
    public String editarAnuncio(@PathVariable Long id, Model model) {
        Anuncio anuncio = anuncioService.findAnuncioById(id).orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));
        if (!anuncio.getUsuario().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("No puedes editar este anuncio");
        }
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("categorias", categoriaRepository.findAll()); // Añadir las categorías
        return "editar-anuncio"; // Vista para editar el anuncio
    }
    @PostMapping("/anuncios/editar/{id}")
    public String editarAnuncio(@PathVariable Long id, @ModelAttribute Anuncio anuncioForm,
                                @RequestParam(value = "categorias", required = false) List<Long> categoriasIds,
                                RedirectAttributes redirectAttributes) {
        // Obtiene el email del usuario autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca al usuario por su email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {

            // Llama al servicio de anuncio para editar el anuncio
            anuncioService.editarAnuncio(id, anuncioForm, usuario);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Anuncio editado correctamente.");
        } catch (RuntimeException e) {
            // Mensaje de error en caso de excepción
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Redirige a la lista de anuncios
        return "redirect:/";
    }

    @GetMapping("/anuncios/borrar/{id}")
    public String borrarAnuncio(@PathVariable Long id) {
        Anuncio anuncio = anuncioService.findAnuncioById(id).orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));
        if (!anuncio.getUsuario().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("No puedes borrar este anuncio");
        }
        anuncioService.deleteAnuncioById(id);
        return "redirect:/mis-anuncios";  // Redirige a Mis anuncios después de eliminar
    }


}
