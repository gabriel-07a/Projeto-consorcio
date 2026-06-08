package com.consorcio.projeto_consorcio.consorcio.state;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.usuario.Usuario;

public class GrupoFinalizadoState implements GrupoState{
    @Override
    public void validarNovoParticipante(GrupoConsorcio grupoConsorcio, Usuario usuario) {
        throw new RuntimeException("Erro: Este grupo já foi encerrado.");
    }

    @Override
    public void validarCancelamento(GrupoConsorcio grupoConsorcio, Cota cota) {
        throw new RuntimeException("Erro: Uma cota não pode ser cancelada de uma consórcio já Finalizado!");
    }

    @Override
    public void validarSorteio() {
        throw new RuntimeException("Erro: Este grupo já foi encerrado!");
    }

    @Override
    public void comecarConsorcio() {

    }

    @Override
    public void encerrarConsorcio() {

    }

    @Override
    public void validaExclusaoDeConsorcio(GrupoConsorcio grupoConsorcio, String EnderecoContrato) {
        throw new RuntimeException("Erro: Um grupo encerrado não pode ser apagado!");
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
