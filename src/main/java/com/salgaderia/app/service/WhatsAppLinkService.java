package com.salgaderia.app.service;

import com.salgaderia.app.model.Pedido;
import com.salgaderia.app.model.enums.StatusPedido;
import com.salgaderia.app.model.enums.TipoEntrega;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/** Cria links wa.me; abrir o link não envia mensagens automaticamente. */
public final class WhatsAppLinkService {
    public static final String LOJA = "5585989095223";
    private final String telefoneLoja;
    public WhatsAppLinkService() { this(carregarTelefoneLoja(WhatsAppLinkService.class.getClassLoader())); }
    public WhatsAppLinkService(String telefoneLoja) { this.telefoneLoja = normalizarTelefone(telefoneLoja); }

    static String carregarTelefoneLoja(ClassLoader loader) {
        try (var arquivo=loader.getResourceAsStream("database.local.properties")) {
            if(arquivo==null)return LOJA;
            var propriedades=new java.util.Properties();propriedades.load(arquivo);
            String telefone=propriedades.getProperty("whatsapp.loja");
            return telefone==null||telefone.isBlank()?LOJA:telefone;
        } catch(IOException e) { throw new IllegalStateException("Não foi possível ler a configuração do WhatsApp",e); }
    }
    public enum ResultadoAbertura { ABERTO, COPIADO, INDISPONIVEL }

    public static String normalizarTelefone(String numero) {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Telefone não cadastrado");
        String digitos = numero.replaceAll("[^0-9]", "");
        if (digitos.length() == 10 || digitos.length() == 11) digitos = "55" + digitos;
        if (!digitos.matches("[1-9][0-9]{9,14}")) throw new IllegalArgumentException("Telefone inválido: informe DDD e número");
        return digitos;
    }

    public static final class Builder {
        private final String telefone;
        private final StringBuilder texto = new StringBuilder();
        public Builder(String telefone) { this.telefone = normalizarTelefone(telefone); }
        public Builder linha(String linha) {
            if (texto.length() > 0) texto.append('\n');
            texto.append(Objects.requireNonNull(linha));
            return this;
        }
        public URI construir() {
            return URI.create("https://wa.me/" + telefone + "?text=" + URLEncoder.encode(texto.toString(), StandardCharsets.UTF_8).replace("+", "%20"));
        }
    }

    public URI acompanhar(Pedido pedido) {
        if (pedido.getId() == null) throw new IllegalArgumentException("Pedido ainda não registrado");
        Builder builder = new Builder(telefoneLoja).linha("Olá! Gostaria de acompanhar o pedido #" + pedido.getId() + ".").linha("Itens:");
        pedido.getItens().forEach(i -> builder.linha("- " + i.getQuantidade().stripTrailingZeros().toPlainString() + " x " + i.getProduto().getNome()));
        builder.linha("Entrega: " + (pedido.getTipoEntrega() == TipoEntrega.ENTREGA ? "Entrega - " + pedido.getEnderecoEntrega() : "Retirada"));
        builder.linha("Pagamento: " + (pedido.getFormaPagamento()==null?"não informado":pedido.getFormaPagamento().descricao())).linha("Total: " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(pedido.calcularTotal()));
        return builder.construir();
    }
    public URI status(String telefone, long pedido, StatusPedido status) {
        return new Builder(telefone).linha("Olá! Seu pedido #" + pedido + " está com status: " + status.name() + ".").linha("Equipe Ui K'fomi").construir();
    }
    public URI contato(String telefone, String nome) {
        return new Builder(telefone).linha("Olá, " + nome + "! Aqui é da Ui K'fomi.").construir();
    }
    public ResultadoAbertura abrir(URI uri) {
        Objects.requireNonNull(uri);
        try {
            if (!GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return ResultadoAbertura.ABERTO;
            }
        } catch (IOException | UnsupportedOperationException | SecurityException ignored) { }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uri.toASCIIString()), null);
            return ResultadoAbertura.COPIADO;
        } catch (RuntimeException ignored) {
            return ResultadoAbertura.INDISPONIVEL;
        }
    }
}
