package org.example.wallapop.Controller;

import jakarta.validation.Valid;
import org.example.wallapop.Entity.Usuario;
import org.example.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes redirect) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            result.rejectValue("email", "error.email", "El email ya está en uso");
        }

        if (result.hasErrors()) {
            return "usuario-registro";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        redirect.addFlashAttribute("success", "Usuario registrado con éxito");
        return "redirect:/login";
    }
}
