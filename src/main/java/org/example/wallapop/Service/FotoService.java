package org.example.wallapop.Service;



import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Entity.FotoAnuncio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FotoService {
    //Este servicio FotoAnuncioService en una aplicación Java Spring se encarga de procesar y
    // almacenar archivos de imagen asociados a Anuncios en un sistema de gestión de Anuncios.
    //CONSTANTES DE CONFIGURACIÓN
    //PERMITTED_TYPES define los tipos MIME permitidos para las imágenes.
    private static final List<String> PERMITTED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/avif", "image/webp");
    //MAX_FILE_SIZE establece el tamaño máximo de archivo permitido (10 MB).
    private static final long MAX_FILE_SIZE = 10000000;
    //UPLOADS_DIRECTORY especifica el directorio donde se guardarán las imágenes de Anuncios.
    private static final String UPLOADS_DIRECTORY = "uploads/imagesAnuncios";
    //metodos
    //Recibe una lista de archivos (List<MultipartFile> fotos) y un objeto Anuncio.
    public List<FotoAnuncio> guardarFotos(List<MultipartFile> fotos, Anuncio anuncio){
        //1.Itera sobre cada archivo en la lista, lo valida, genera un nombre único y guarda la imagen en el sistema de archivos.
        List<FotoAnuncio> listaAnuncio = new ArrayList<>();

        for (MultipartFile foto : fotos) {
            validarArchivo(foto);
            String nombreFoto = generarNombreUnico(foto);
            guardarArchivo(foto, nombreFoto);
            //2.Crea un objeto FotoAnuncio para cada imagen, que se vincula al Anuncio recibido.
            FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                    .nombre(nombreFoto)
                    .anuncio(anuncio).build();

            listaAnuncio.add(fotoAnuncio);
        }
        //Devuelve una lista de FotoAnuncio asociada al Anuncio.
        anuncio.setFotos(listaAnuncio);
        return listaAnuncio;
    }

    //valoidamos si los archivos seleccionados son validos
    public void validarArchivo(MultipartFile file) {
        //1.si no es nulo oa esta vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo no seleccionado");
        }
        //confirma que el tipo mime del archivo este dentro de los requisitos que pusimos al principio
        if (!PERMITTED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        //y que sea de un tamaño permitido
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Archivo demasiado grande. Sólo se admiten archivos < 10MB");
        }
    }

    public String generarNombreUnico(MultipartFile file) {
        //Genera un nombre único para el archivo mediante UUID, lo que ayuda a evitar nombres duplicados en el sistema.
        UUID nombreUnico = UUID.randomUUID();
        //Extrae la extensión del nombre original del archivo y la añade al nombre único generado.
        String extension;
        //Lanza una excepción si el nombre del archivo es nulo o vacío.
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } else {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        return nombreUnico + extension;
    }
    //Toma el archivo y lo guarda en el sistema de archivos con el nombre único generado.
    public void guardarArchivo(MultipartFile file, String nuevoNombreFoto) {
        Path ruta = Paths.get(UPLOADS_DIRECTORY + File.separator + nuevoNombreFoto);
        //Movemos el archivo a la carpeta y guardamos su nombre en el objeto catgoría
        try {
            byte[] contenido = file.getBytes();
            Files.write(ruta, contenido);
        } catch (
                IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }
}
//Este servicio está diseñado para manejar imágenes de Anuncios, validar que cumplan con ciertos requisitos y
// guardarlas en un directorio específico, vinculando cada imagen a un Anuncio determinado.
