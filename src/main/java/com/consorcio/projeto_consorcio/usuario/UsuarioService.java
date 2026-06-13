package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.usuario.dto.AtualizaUsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    //declaro o repositorio que vou usar
    //fazendo uma injeção de depemdencia
    private final UsuarioRepository usuarioRepository;

    //crio um construtor para o spring injetar o repositorio automaticamente
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    //método da regra de négocio
    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO dto){
        boolean emailExiste = usuarioRepository.existsByEmail(dto.email());
        boolean taxIdExiste = usuarioRepository.existsByTaxId(dto.taxId());
        boolean carteiraExiste = usuarioRepository.existsByCarteiraWeb3(dto.carteiraWeb3());

        //verificações de duplicidade de cadastros
        if(emailExiste) throw new RuntimeException("Erro: Email já usado por outro usuário!");
        if(taxIdExiste) throw new RuntimeException("Erro: Id já usado no sistema!");
        if(carteiraExiste) throw new RuntimeException("Erro: Essa carteira já está sendo utilizada!");

        //traduzindo dto para a entidade
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCarteiraWeb3(dto.carteiraWeb3());
        usuario.setTaxId(dto.taxId());
        usuario.setCountryCode(dto.countryCode());

        // Como o Security está desligado, vamos colocar uma string simulando o hash.
        // Futuramente será: passwordEncoder.encode(dto.senhaLimpa());
        String hashSimulado = dto.senhaLimpa() + "_HASH_SEGURO";
        usuario.setSenhaHash(hashSimulado);

        //salvando no banco
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        //TRADUÇÃO FINAL (Entidade -> ResponseDTO)
        // Montamos a resposta bonita para devolver à internet, SEM A SENHA.
        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCarteiraWeb3(),
                usuarioSalvo.getCountryCode()
        );
        //return usuarioRepository.save(novoUsuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizaUsuario(Long id, AtualizaUsuarioRequestDTO dadosAtualizados){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Esse usuário não existe!"));

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
                .orElseThrow(() -> new RuntimeException("Erro: Esse usuário não existe!"));

        return new UsuarioResponseDTO(
                usuarioEncontrado.getId(),
                usuarioEncontrado.getNome(),
                usuarioEncontrado.getEmail(),
                usuarioEncontrado.getCarteiraWeb3(),
                usuarioEncontrado.getCountryCode()
        );
    }

    public void deletarUsuario(Long id){
        if(!usuarioRepository.existsById(id)) throw new RuntimeException("Erro: Esse usuário não existe!");

        usuarioRepository.deleteById(id);//trocar isso, não posso deletar nenhum registro
    }
}
