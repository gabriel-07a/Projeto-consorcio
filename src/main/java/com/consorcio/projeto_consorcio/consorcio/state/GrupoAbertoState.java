package com.consorcio.projeto_consorcio.consorcio.state;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.usuario.Usuario;

public class GrupoAbertoState implements GrupoState{


    @Override
    public void validarNovoParticipante(GrupoConsorcio grupoConsorcio, Usuario usuario) {
        long totalCotasAtual = grupoConsorcio.getCotas().size();
        if(totalCotasAtual >= grupoConsorcio.getVagasMaximas()) throw new RuntimeException("Erro: Esse grupo de consórcio já está lotado");
        if(totalCotasAtual+1 == grupoConsorcio.getVagasMaximas()) grupoConsorcio.setStatus(StatusGrupo.EM_ANDAMENTO);
    }

    @Override
    public void validarCancelamento(GrupoConsorcio grupoConsorcio, Cota cota) {
        //como aqui o grupo ainda não começou o participante pode sair livremente
    }

    @Override
    public void validarSorteio() {
        throw new RuntimeException("Erro: Esse grupo ainda não está em andamento!");
    }

    @Override
    public void comecarConsorcio() {

    }

    @Override
    public void encerrarConsorcio() {

    }

    @Override
    public void validaExclusaoDeConsorcio(GrupoConsorcio grupoConsorcio, String enderecoContrato) {
        if(!grupoConsorcio.getCotas().isEmpty()) throw new RuntimeException("Erro: Um grupo com participantes não pode ser excluído!");
        if(!grupoConsorcio.getEnderecoContrato().equalsIgnoreCase(enderecoContrato)) throw new RuntimeException("Erro: O endereço do Smart Contract do grupo está errado!");

    }

    @Override
    public void transferirCota() {

    }

    @Override
    public void ofertarLance() {

    }

    @Override
    public void pagarParcela() {

    }
}
