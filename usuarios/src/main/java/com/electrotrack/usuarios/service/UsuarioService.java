package com.electrotrack.usuarios.service;

import com.electrotrack.usuarios.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public synchronized List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public synchronized Usuario registrarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Los datos del usuario son obligatorios"
            );
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El nombre es obligatorio"
            );
        }

        if (!validarRut(usuario.getRut())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El RUT ingresado no es valido"
            );
        }

        if (usuario.getCorreo() == null ||
            !usuario.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El correo no es valido"
            );
        }

        if (usuario.getRol() == null ||
            (!usuario.getRol().equals("TECNICO") &&
             !usuario.getRol().equals("ADMINISTRADOR"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El rol debe ser TECNICO o ADMINISTRADOR"
            );
        }

        String rutNormalizado = usuario.getRut()
                .replace(".", "")
                .replace("-", "")
                .trim()
                .toUpperCase();

        for (Usuario existente : usuarios) {

            String rutExistente = existente.getRut()
                    .replace(".", "")
                    .replace("-", "")
                    .trim()
                    .toUpperCase();

            if (rutExistente.equals(rutNormalizado)) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El RUT ya esta registrado"
                );
            }

            if (existente.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo ya esta registrado"
                );
            }
        }

        usuario.setRut(rutNormalizado);
        usuario.setId(contador.getAndIncrement());
        usuarios.add(usuario);

        return usuario;
    }

    
    public synchronized Usuario buscarPorId(Long id) {

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Usuario no encontrado"
        );
    }
    
    public synchronized Usuario actualizarUsuario(Long id, Usuario datos) {

        Usuario usuario = buscarPorId(id);

        if (datos == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Los datos son obligatorios"
            );
        }

        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El nombre es obligatorio"
            );
        }

        if (!validarRut(datos.getRut())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El RUT ingresado no es valido"
            );
        }

        if (datos.getCorreo() == null ||
            !datos.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El correo no es valido"
            );
        }

        if (datos.getRol() == null ||
            (!datos.getRol().equals("TECNICO") &&
            !datos.getRol().equals("ADMINISTRADOR"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El rol no es valido"
            );
        }

        String rutNormalizado = datos.getRut()
                .replace(".", "")
                .replace("-", "")
                .trim()
                .toUpperCase();

        for (Usuario existente : usuarios) {

            if (existente.getId().equals(id)) {
                continue;
            }

            if (existente.getRut().equals(rutNormalizado)) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El RUT ya esta registrado"
                );
            }

            if (existente.getCorreo().equalsIgnoreCase(datos.getCorreo())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo ya esta registrado"
                );
            }
        }

        usuario.setNombre(datos.getNombre());
        usuario.setRut(rutNormalizado);
        usuario.setCorreo(datos.getCorreo());
        usuario.setRol(datos.getRol());

        return usuario;
    }
    
    public synchronized void eliminarUsuario(Long id) {

        Usuario usuario = buscarPorId(id);
        usuarios.remove(usuario);
    }

    private boolean validarRut(String rut) {

        if (rut == null || rut.isBlank()) {
            return false;
        }

        String rutLimpio = rut.replace(".", "")
                              .replace("-", "")
                              .trim()
                              .toUpperCase();

        if (!rutLimpio.matches("^[0-9]{7,8}[0-9K]$")) {
            return false;
        }

        String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
        char digitoVerificador = rutLimpio.charAt(rutLimpio.length() - 1);

        int suma = 0;
        int multiplicador = 2;

        for (int i = numero.length() - 1; i >= 0; i--) {

            int digito = Character.getNumericValue(numero.charAt(i));

            suma += digito * multiplicador;

            multiplicador++;

            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resultado = 11 - (suma % 11);

        char digitoCalculado;

        if (resultado == 11) {
            digitoCalculado = '0';
        } else if (resultado == 10) {
            digitoCalculado = 'K';
        } else {
            digitoCalculado = Character.forDigit(resultado, 10);
        }

        return digitoVerificador == digitoCalculado;
    }
}
