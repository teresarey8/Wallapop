package org.example.wallapop.Service;



import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Entity.FotoAnuncio;
import org.example.wallapop.repository.AnuncioRepository;
import org.example.wallapop.repository.FotoAnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FotoService {

    private static final List<String> PERMITTED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/avif", "image/webp");
    private static final long MAX_FILE_SIZE = 10000000;
    private static final String UPLOADS_DIRECTORY = "uploads/imagesAnuncios";

    @Autowired
    AnuncioRepository anuncioRepository;
    @Autowired
    FotoAnuncioRepository fotoAnuncioRepository;

    public List<FotoAnuncio> guardarFotos(List<MultipartFile> fotos, Anuncio anuncio) {
        List<FotoAnuncio> listaFotos = new ArrayList<>();

        for (MultipartFile foto : fotos) {
            if (!foto.isEmpty()) {
                validarArchivo(foto);
                String nombreFoto = generarNombreUnico(foto);
                guardarArchivo(foto, nombreFoto);

                FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                        .nombre(nombreFoto)
                        .anuncio(anuncio) // Vincula el anuncio
                        .build();

                listaFotos.add(fotoAnuncio);
            }
        }

        anuncio.getFotos().addAll(listaFotos); // Añade las fotos al anuncio
        anuncioRepository.save(anuncio); // Guarda el anuncio con las fotos
        return listaFotos;
    }


    public void addFoto(MultipartFile foto, Anuncio anuncio) {

        if (!foto.isEmpty()) {
            validarArchivo(foto);
            String nombreFoto = generarNombreUnico(foto);
            guardarArchivo(foto, nombreFoto);

            FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                    .nombre(nombreFoto)
                    .anuncio(anuncio).build();


            anuncio.getFotos().add(fotoAnuncio);
            anuncioRepository.save(anuncio);
        }
    }

    public void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo no seleccionado");
        }
        if (!PERMITTED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Archivo demasiado grande. Sólo se admiten archivos < 10MB");
        }
    }

    public String generarNombreUnico(MultipartFile file) {
        UUID nombreUnico = UUID.randomUUID();
        String extension;
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } else {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        return nombreUnico + extension;
    }

    public void guardarArchivo(MultipartFile file, String nuevoNombreFoto) {
        Path ruta = Paths.get(UPLOADS_DIRECTORY + File.separator + nuevoNombreFoto);

        try {
            // Redimensionar imagen antes de guardarla
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = 1000;
            int height = (originalImage.getHeight() * width) / originalImage.getWidth();

            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resizedBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedBufferedImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            // Guardar imagen redimensionada
            ImageIO.write(resizedBufferedImage, "jpg", ruta.toFile());

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo redimensionado", e);
        }
    }

    public void deleteFoto(Long idFoto) {
        Optional<FotoAnuncio> fotoAnuncioOptional = fotoAnuncioRepository.findById(idFoto);
        if (fotoAnuncioOptional.isPresent()) {
            FotoAnuncio fotoAnuncio = fotoAnuncioOptional.get();
            Path archivoFoto = Paths.get(fotoAnuncio.getNombre());
            try {
                Files.deleteIfExists(archivoFoto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fotoAnuncioRepository.deleteById(idFoto);
        }
    }
}