# Sistema de Controle de Vendas

Aplicacao Java de terminal que implementa os requisitos da ERS PI 2026/1 usando
arquivos CSV em `data/`.

## Credenciais iniciais

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `admin@email.com` | `123456` |
| Gerente | `manager@email.com` | `654321` |
| Vendedor | `seller@email.com` | `111111` |

As senhas sao persistidas como hash SHA-256. Usuarios inativos nao conseguem
entrar e a sessao expira apos 15 minutos sem atividade.

## Funcionalidades

- CRUD de clientes, com CPF unico e registro obrigatorio da origem.
- CRUD de produtos, consulta e reposicao de estoque.
- CRUD de fornecedores com CNPJ unico.
- CRUD de usuarios, perfis de acesso e estado ativo/inativo.
- Registro de venda com validacao e baixa automatica do estoque.
- Pagamento em dinheiro, Pix, credito ou debito.
- Emissao de comprovante em `data/receipts/`.
- Historico, relatorio por periodo, faturamento e origem dos clientes.
- Cancelamento de venda com devolucao do estoque.
- Auditoria de pagamentos e cancelamentos em `data/Audit.csv`.
- Backup diario dos CSVs, criado ao iniciar a aplicacao em `data/backups/`.

## Perfis

- `ADMIN`: produtos, fornecedores, usuarios e estoque.
- `MANAGER`: relatorios, faturamento, historico, cancelamento e estoque.
- `SELLER`: clientes, estoque, vendas, pagamentos, comprovantes e historico.

