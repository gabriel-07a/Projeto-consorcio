package com.consorcio.projeto_consorcio.usuario;

import com.consorcio.projeto_consorcio.usuario.dto.UsuarioRequestDTO;
import com.consorcio.projeto_consorcio.usuario.dto.UsuarioResponseDTO;
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

    public void deletarUsuario(Long id){
        if(!usuarioRepository.existsById(id)) throw new RuntimeException("Erro: Esse usuário não existe!");

        usuarioRepository.deleteById(id);
    }
}
