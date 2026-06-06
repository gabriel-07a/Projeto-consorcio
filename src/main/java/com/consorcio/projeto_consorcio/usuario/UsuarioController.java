package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioService usuarioService){
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;

    }

    //rota de registro de usuario
    @PostMapping
    public UsuarioResponseDTO cadastrar(@RequestBody @Valid UsuarioRequestDTO dto){
        //o valid é para passar apenas as requisições validades pelos requestDTO
        //o Controller só repassa o DTO para o Service e devolve a resposta segura.
        return usuarioService.cadastrarUsuario(dto);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos(){
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getCarteiraWeb3(),
                        usuario.getCountryCode()
                ))
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id){
        usuarioService.deletarUsuario(id);
    }
}
