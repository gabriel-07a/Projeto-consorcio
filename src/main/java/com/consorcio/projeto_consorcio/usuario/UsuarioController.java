package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.usuario.dto.AtualizaUsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public UsuarioResponseDTO cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return usuarioService.cadastrarUsuario(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuario(@PathVariable Long id){
        UsuarioResponseDTO response = usuarioService.buscaUsuario(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizaUsuario(@PathVariable Long id, @RequestBody AtualizaUsuarioRequestDTO requestDTO){
        UsuarioResponseDTO response = usuarioService.atualizaUsuario(id, requestDTO);

        return ResponseEntity.ok(response);
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
