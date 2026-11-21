package com.salgaderia.app.service;

import com.salgaderia.app.model.*;
import com.salgaderia.app.model.PersonalizacaoItem.Tipo;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/** Sorteia caixas e revalida as composições com os produtos vigentes do cardápio. */
public final class PersonalizacaoService {
    private static final BigDecimal LIMITE=new BigDecimal("1.15");
    private final RandomGenerator sorteio;
    public PersonalizacaoService(){this(RandomGenerator.getDefault());}
    public PersonalizacaoService(RandomGenerator sorteio){this.sorteio=Objects.requireNonNull(sorteio);}

    public static Map<Long,BigDecimal> quantidades(List<PedidoItem> itens){
        Map<Long,BigDecimal> quantidades=new LinkedHashMap<>();
        for(PedidoItem i:itens) quantidades.merge(i.getProduto().getId(),i.getQuantidade(),BigDecimal::add);
        return quantidades;
    }

    private record Opcao(Produto produto,int preco,int custo) { }

    public List<PedidoItem> sortearCaixa(Tipo tipo,List<Produto> catalogo,Set<Long> categorias,Set<Long> produtos){
        if(tipo==null||tipo.orcamento()==null)throw new IllegalArgumentException("Selecione um orçamento para a caixa.");
        var personalizacao=new PersonalizacaoItem(UUID.randomUUID(),tipo,Set.of(),categorias,produtos);
        BigDecimal teto=tipo.orcamento().multiply(LIMITE);
        int pago=centavos(tipo.orcamento()), maximo=centavos(teto);
        List<Opcao> opcoes=catalogo.stream().filter(p->p.isDisponivelVenda()&&permitido(p,personalizacao))
                .filter(p->p.getPrecoVenda().signum()>0&&p.getPrecoVenda().compareTo(teto)<=0&&
                        p.getCustoProducao().signum()>0&&p.getCustoProducao().compareTo(tipo.orcamento())<=0)
                .map(p->new Opcao(p,centavos(p.getPrecoVenda()),centavos(p.getCustoProducao()))).toList();
        // Para cada subtotal de catálogo, guarda o menor custo possível. Uma escolha aleatória só é
        // aceita se o saldo ainda puder ser completado dentro do custo máximo que o cliente pagará.
        int inviavel=pago+1;
        int[] menorCusto=new int[maximo+1];Arrays.fill(menorCusto,inviavel);menorCusto[0]=0;
        List<Integer> alvos=new ArrayList<>();
        for(int valor=1;valor<=maximo;valor++){
            for(Opcao o:opcoes)if(o.preco<=valor)
                menorCusto[valor]=Math.min(menorCusto[valor],o.custo+menorCusto[valor-o.preco]);
            if(valor>=pago&&menorCusto[valor]<=pago)alvos.add(valor);
        }
        if(alvos.isEmpty())throw new IllegalArgumentException("Não há combinação segura para este orçamento e filtros. Ajuste as exclusões ou escolha outra faixa.");
        int saldo=alvos.get(sorteio.nextInt(alvos.size())), custoRestante=pago;
        Map<Produto,Integer> escolhidos=new LinkedHashMap<>();
        while(saldo>0){
            Opcao escolha=null;int candidatos=0;
            for(Opcao o:opcoes)if(o.preco<=saldo&&o.custo+menorCusto[saldo-o.preco]<=custoRestante){
                if(sorteio.nextInt(++candidatos)==0)escolha=o;
            }
            if(escolha==null)throw new IllegalStateException("Composição de caixa sem continuação válida.");
            escolhidos.merge(escolha.produto,1,Integer::sum);saldo-=escolha.preco;custoRestante-=escolha.custo;
        }
        return descrever(escolhidos.entrySet().stream().map(e->new PedidoItem(null,e.getKey(),BigDecimal.valueOf(e.getValue()),
                e.getKey().getPrecoVenda(),null,personalizacao)).toList());
    }

    private static int centavos(BigDecimal valor){return valor.movePointRight(2).intValueExact();}
    private static boolean permitido(Produto p,PersonalizacaoItem escolha){
        return p.getId()!=null&&!escolha.produtosExcluidos().contains(p.getId())&&
                (p.getCategoria()==null||p.getCategoria().getId()==null||!escolha.categoriasExcluidas().contains(p.getCategoria().getId()));
    }
    public static boolean bebida(Produto produto){
        return produto.getCategoria()!=null&&Normalizer.normalize(produto.getCategoria().getNome(),Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).contains("bebid");
    }

    public static Map<UUID,List<PedidoItem>> grupos(List<PedidoItem> itens){
        return itens.stream().filter(i->i.getPersonalizacao()!=null)
                .collect(Collectors.groupingBy(i->i.getPersonalizacao().grupo(),LinkedHashMap::new,Collectors.toList()));
    }

    private static String descricao(List<PedidoItem> grupo){
        var escolha=grupo.get(0).getPersonalizacao();
        if(escolha.tipo().legado())throw new IllegalArgumentException("Este combo ou caixa é de um cardápio anterior. Monte uma nova opção na vitrine.");
        if(grupo.stream().anyMatch(i->!escolha.equals(i.getPersonalizacao())||i.getQuantidade().stripTrailingZeros().scale()>0))
            throw new IllegalArgumentException("Composição do combo ou da caixa inválida.");
        grupo.forEach(i->i.getProduto().exigirDisponivelParaVenda());
        if(escolha.tipo().caixa()){
            BigDecimal pago=escolha.tipo().orcamento(), vendido=subtotal(grupo);
            BigDecimal custo=grupo.stream().map(i->i.getProduto().getCustoProducao().multiply(i.getQuantidade())).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(vendido.compareTo(pago)<0||vendido.compareTo(pago.multiply(LIMITE))>0||custo.compareTo(pago)>0||
                    grupo.stream().anyMatch(i->!permitido(i.getProduto(),escolha)||i.getPrecoUnitario().signum()<=0||i.getProduto().getCustoProducao().signum()<=0))
                throw new IllegalArgumentException("A caixa não atende ao orçamento ou às exclusões atuais. Sorteie novamente.");
            return "Caixa Misteriosa — "+escolha.tipo();
        }
        if(grupo.size()!=3||grupo.stream().anyMatch(i->i.getQuantidade().compareTo(BigDecimal.ONE)!=0||i.getProduto().getId()==null)||
                grupo.stream().map(i->i.getProduto().getId()).distinct().count()!=3||
                grupo.stream().filter(i->bebida(i.getProduto())).count()!=1)
            throw new IllegalArgumentException("Escolha dois produtos diferentes e uma bebida para montar o combo.");
        return "Combo — "+grupo.stream().map(i->i.getProduto().getNome()).collect(Collectors.joining(" + "));
    }

    public static List<PedidoItem> descrever(List<PedidoItem> itens){
        Map<UUID,String> descricoes=new HashMap<>();
        grupos(itens).forEach((id,grupo)->descricoes.put(id,descricao(grupo)));
        return itens.stream().map(i->i.getPersonalizacao()==null?i:
                new PedidoItem(null,i.getProduto(),i.getQuantidade(),i.getPrecoUnitario(),
                        descricoes.get(i.getPersonalizacao().grupo()),i.getPersonalizacao())).toList();
    }
    public static void validarComposicao(List<PedidoItem> itens){
        grupos(itens).values().forEach(grupo->{
            String descricao=descricao(grupo);
            if(grupo.stream().anyMatch(i->!descricao.equals(i.getObservacaoPersonalizada())))
                throw new IllegalArgumentException("O cardápio mudou. Monte novamente o combo ou a caixa.");
        });
    }
    public static BigDecimal subtotal(List<PedidoItem> itens){
        return itens.stream().map(PedidoItem::calcularSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
