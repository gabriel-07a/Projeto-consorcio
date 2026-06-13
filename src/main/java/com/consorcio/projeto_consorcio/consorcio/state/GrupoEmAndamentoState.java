package com.consorcio.projeto_consorcio.consorcio.state;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.cota.enums.StatusCota;
import com.consorcio.projeto_consorcio.usuario.Usuario;

public class GrupoEmAndamentoState implements GrupoState{
    @Override
    public void validarNovoParticipante(GrupoConsorcio grupoConsorcio, Usuario usuario) {
        throw new RuntimeException("Erro: O grupo já começou. Não é possível entrar num consórcio em andamento.");
    }

    @Override
    public void validarCancelamento(GrupoConsorcio grupoConsorcio, Cota cota) {
        if(cota.getStatus() == StatusCota.CONTEMPLADA) throw new RuntimeException("Erro: Uma cota contemplada não pode ser cancelada!");

    }

    @Override
    public void validarSorteio() {
        //posso ver futuramente se tem dinheiro para o sorteio
    }

    @Override
    public void comecarConsorcio() {
        throw new RuntimeException("Erro: Esse grupo já está em andamento!");
    }

    @Override
    public void encerrarConsorcio() {

    }

    @Override
    public void validaExclusaoDeConsorcio(GrupoConsorcio grupoConsorcio, String EnderecoContrato) {
        throw new RuntimeException("Erro: Um grupo em andamento não pode ser apagado!");
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
