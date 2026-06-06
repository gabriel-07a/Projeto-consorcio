package com.consorcio.projeto_consorcio.consorcio.state;

import com.consorcio.projeto_consorcio.consorcio.GrupoConsorcio;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.usuario.Usuario;

public interface GrupoState {
    public void validarNovoParticipante(GrupoConsorcio grupoConsorcio, Usuario usuario);
    public void validarCancelamento(GrupoConsorcio grupoConsorcio, Cota cota);
    public void validarSorteio();
    public void comecarConsorcio();
    public void encerrarConsorcio();
    public void cancelarGrupo();
    public void transferirCota();
    public void ofertarLance();
    public void pagarParcela();
}
