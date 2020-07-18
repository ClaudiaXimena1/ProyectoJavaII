package co.com.udem.inmobiliaria.rest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.udem.inmobiliaria.dto.TipoIdentificacionDTO;
import co.com.udem.inmobiliaria.dto.UsuarioDTO;
import co.com.udem.inmobiliaria.entities.Usuario;
import co.com.udem.inmobiliaria.repositories.IUsuario;
import co.com.udem.inmobiliaria.util.Constantes;
import co.com.udem.inmobiliaria.util.ConvertTipoIdentificacion;
import co.com.udem.inmobiliaria.util.ConvertUsuario;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {

	@Autowired
	private IUsuario usuarioRepo;

	@Autowired
	private ConvertUsuario convertUsuario;

	@Autowired
	private ConvertTipoIdentificacion convertTipoIden;

	@PostMapping("/crearUsuario")
	public Map<String, String> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		Map<String, String> response = new HashMap<>();

		try {
			usuarioRepo.save(convertUsuario.convertToEntity(usuarioDTO));
			response.put(Constantes.CODIGO_HTTP, "200");
			response.put(Constantes.MENSAJE_EXITO, "Registrado insertado exitosamente");
			return response;
		} catch (Exception e) {
			response.put(Constantes.CODIGO_HTTP, "500");
			response.put(Constantes.MENSAJE_ERROR, "Ocurrió un problema al insertar");
			return response;

		}

	}

	@DeleteMapping("/borrarUsuario/{id}")
	public Map<String, String> borrarUsuario(@PathVariable Long id) {
		Map<String, String> response = new HashMap<>();

		try {
			usuarioRepo.deleteById(id);
			response.put(Constantes.CODIGO_HTTP, "200");
			response.put(Constantes.MENSAJE_EXITO, "Registrado eliminado exitosamente");
			return response;
		} catch (Exception e) {
			response.put(Constantes.CODIGO_HTTP, "500");
			response.put(Constantes.MENSAJE_ERROR, "Ocurrió un problema al eliminar");
			return response;

		}

	}

	@PutMapping("/actualizarUsuario/{id}")
	public Map<String, String> actualizarUsuario(@RequestBody UsuarioDTO usuarioDTO, @PathVariable Long id) {
		Map<String, String> response = new HashMap<>();

		try {

			Optional<Usuario> usuario = usuarioRepo.findById(id);
			if (usuario.isPresent()) {
				Usuario usuarioNew = usuario.get();
				TipoIdentificacionDTO tipoIdentificacionDTO = null;
				
				usuarioNew.setNumeroIdentificacion(usuarioDTO.getNumeroIdentificacion());
				usuarioNew.setNombres(usuarioDTO.getNombres());
				usuarioNew.setApellidos(usuarioDTO.getApellidos());
				usuarioNew.setDireccion(usuarioDTO.getDireccion());
				usuarioNew.setTelefono(usuarioDTO.getTelefono());
				usuarioNew.setEmail(usuarioDTO.getEmail());
				usuarioNew.setPassword(usuarioDTO.getPassword());
				tipoIdentificacionDTO = usuarioDTO.getTipoIdentificacionDTO();
				usuarioNew.setTipoIdentificacion(convertTipoIden.convertToEntity(tipoIdentificacionDTO));

				usuarioRepo.save(usuarioNew);

			}

			response.put(Constantes.CODIGO_HTTP, "200");
			response.put(Constantes.MENSAJE_EXITO, "Registrado actualizado exitosamente");
			return response;

		} catch (Exception e) {

			response.put(Constantes.CODIGO_HTTP, "500");
			response.put(Constantes.MENSAJE_ERROR, "Ocurrió un problema al actualizar");
			return response;

		}

	}

	@GetMapping("/consultarUsuarios")
	public List<UsuarioDTO> consultarUsuarios() {

		Iterable<Usuario> usuarios = usuarioRepo.findAll();
		List<UsuarioDTO> listaUsuariosDTO = new ArrayList<>();
		List<Usuario> listaUsuarios = new ArrayList<>();
		usuarios.iterator().forEachRemaining(listaUsuarios::add);

		for (int i = 0; i < listaUsuarios.size(); i++) {

			try {

				TipoIdentificacionDTO tipoIdentificacionDTO = null;
				if (listaUsuarios.get(i).getTipoIdentificacion() != null) {
					tipoIdentificacionDTO = convertTipoIden.convertToDTO(listaUsuarios.get(i).getTipoIdentificacion());
				}

				UsuarioDTO usuarioDTO = convertUsuario.convertToDTO(listaUsuarios.get(i));
				usuarioDTO.setTipoIdentificacionDTO(tipoIdentificacionDTO);
				listaUsuariosDTO.add(usuarioDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return listaUsuariosDTO;

	}

}
