package com.salgaderia.app.util;

import com.salgaderia.app.service.FotoAvaliacaoStorage;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** Miniatura JavaFX de uma foto local de avaliação. */
public final class FotoAvaliacaoUi {
    private FotoAvaliacaoUi() { }

    public static Optional<ImageView> miniatura(String referencia, double largura, double altura, String descricao) {
        return new FotoAvaliacaoStorage().resolver(referencia).map(arquivo -> {
            ImageView miniatura = new ImageView(new Image(arquivo.toUri().toString(), largura, altura, true, true, true));
            miniatura.setFitWidth(largura);
            miniatura.setFitHeight(altura);
            miniatura.setPreserveRatio(true);
            miniatura.setAccessibleText(descricao);
            return miniatura;
        });
    }
}
