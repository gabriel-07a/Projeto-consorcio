package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.usuario.dto.AtualizaUsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public UsuarioResponseDTO cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        //o valid é para passar apenas as requisições validadas pelos requestDTO
        //o Controller só repassa o DTO para o Service e devolve a resposta segura.
        return usuarioService.cadastrarUsuario(dto);
    }
    //busca 1 usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuario(@PathVariable Long id){
        UsuarioResponseDTO response = usuarioService.buscaUsuario(id);

        return ResponseEntity.ok(response);
    }

    //rota que permite atualizar nome e carteira do usuario
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizaUsuario(@PathVariable Long id, @RequestBody AtualizaUsuarioRequestDTO requestDTO){
        UsuarioResponseDTO response = usuarioService.atualizaUsuario(id, requestDTO);

        return ResponseEntity.ok(response);
    }

    //rota de listar resgistros
    //aq futuramente tem que ser feito paginação
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

    //rota para deletar resgistros
    //apenas inativa, nada é apagado
    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id){
        usuarioService.deletarUsuario(id);
    }
}
