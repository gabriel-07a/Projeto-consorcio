package com.consorcio.projeto_consorcio.consorcio.state;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.usuario.Usuario;

public class GrupoFinalizadoState implements GrupoState{
    @Override
    public void validarNovoParticipante(GrupoConsorcio grupoConsorcio, Usuario usuario) {
        throw new RegraDeNegocioException("Erro: Este grupo já foi encerrado.");
    }

    @Override
    public void validarCancelamento(GrupoConsorcio grupoConsorcio, Cota cota) {
        throw new RegraDeNegocioException("Erro: Uma cota não pode ser cancelada de uma consórcio já Finalizado!");
    }

    @Override
    public void validarSorteio() {
        throw new RegraDeNegocioException("Erro: Este grupo já foi encerrado!");
    }

    @Override
    public void comecarConsorcio() {
        throw new RegraDeNegocioException("Erro: Esse grupo já foi encerrado!");
    }

    @Override
    public void encerrarConsorcio() {
        throw new RegraDeNegocioException("Erro: Esse grupo já foi encerrado!");
    }

    @Override
    public void validaExclusaoDeConsorcio(GrupoConsorcio grupoConsorcio, String EnderecoContrato) {
        throw new RegraDeNegocioException("Erro: Um grupo encerrado não pode ser apagado!");
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
