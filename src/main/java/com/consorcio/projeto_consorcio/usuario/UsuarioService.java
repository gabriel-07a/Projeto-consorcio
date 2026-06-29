package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.core.exception.EntidadeNaoEncontradaException;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.usuario.dto.AtualizaUsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO dto){
        boolean emailExiste = usuarioRepository.existsByEmail(dto.email());
        boolean taxIdExiste = usuarioRepository.existsByTaxId(dto.taxId());
        boolean carteiraExiste = usuarioRepository.existsByCarteiraWeb3(dto.carteiraWeb3());

        if(emailExiste) throw new RegraDeNegocioException("Erro: Email já usado por outro usuário!");
        if(taxIdExiste) throw new RegraDeNegocioException("Erro: Id já usado no sistema!");
        if(carteiraExiste) throw new RegraDeNegocioException("Erro: Essa carteira já está sendo utilizada!");

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCarteiraWeb3(dto.carteiraWeb3());
        usuario.setTaxId(dto.taxId());
        usuario.setCountryCode(dto.countryCode());

        String hashSimulado = dto.senhaLimpa() + "_HASH_SEGURO";
        usuario.setSenhaHash(hashSimulado);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCarteiraWeb3(),
                usuarioSalvo.getCountryCode()
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizaUsuario(Long id, AtualizaUsuarioRequestDTO dadosAtualizados){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Esse usuário não existe!"));

        if(dadosAtualizados.nome() != null && !dadosAtualizados.nome().isBlank()){
            usuario.setNome(dadosAtualizados.nome());
        }
        if(dadosAtualizados.carteiraWeb3() != null && !dadosAtualizados.carteiraWeb3().isBlank()){
            usuario.setCarteiraWeb3(dadosAtualizados.carteiraWeb3());
        }

        usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCarteiraWeb3(),
                usuario.getCountryCode()
        );
    }

    public UsuarioResponseDTO buscaUsuario(Long id){
        Usuario usuarioEncontrado =  usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Erro: Esse usuário não existe!"));

        return new UsuarioResponseDTO(
                usuarioEncontrado.getId(),
                usuarioEncontrado.getNome(),
                usuarioEncontrado.getEmail(),
                usuarioEncontrado.getCarteiraWeb3(),
                usuarioEncontrado.getCountryCode()
        );
    }

    public void deletarUsuario(Long id){
        if(!usuarioRepository.existsById(id)) throw new EntidadeNaoEncontradaException("Erro: Esse usuário não existe!");

        usuarioRepository.deleteById(id);
    }
}
