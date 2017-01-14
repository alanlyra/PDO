package br.com.anso.pdo.rota;


public interface RecursoMapaObject {
    void destacarMarcadorMapa(int pos);
    void resetarMarcadorMapa(int pos);
    void resetStyles();
    void centralizarNoMapa();
    void tratarCliqueNoBotaoTransbordo(boolean centralizarPelaPolyline);
}

