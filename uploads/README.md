[🏠 Início](../README.md) / 📂 uploads

# 📷 Fotos de avaliações

Guarda arquivos anexados pelos clientes da Ui K'Fomi. A aplicação valida o conteúdo, limita as dimensões e grava uma cópia PNG com nome gerado. O banco guarda apenas o caminho relativo.

## Explore as subpastas e arquivos

| Caminho | Conteúdo e responsabilidade |
|---|---|
| [avaliacoes/](avaliacoes/) | Fotos em tempo de execução, fora do Git, e o marcador [`.gitkeep`](avaliacoes/.gitkeep). |
| [README.md](README.md) | Guia de armazenamento das fotos. |

## Árvore estrutural comentada

```text
uploads/  # Armazena os anexos locais fora do código-fonte.
├── avaliacoes/  # Guarda as fotos localmente; apenas o marcador é versionado.
│   └── .gitkeep  # Preserva a pasta vazia no repositório.
└── README.md  # Guia de armazenamento das fotos.
```

## 📌 Armazenamento e backup

- Rode a aplicação a partir da mesma pasta de trabalho em cada execução. A galeria e a moderação não consultam URLs externas; instâncias em máquinas distintas só veem as fotos se tiverem acesso aos mesmos arquivos.
- Faça backup privado de `uploads/avaliacoes/` junto com o banco e restaure ambos para preservar as fotos. Não publique anexos de clientes no Git.

[🏠 Voltar ao README principal](../README.md)
