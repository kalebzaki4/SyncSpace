# Security Policy

## Supported Versions

O SyncSpace está atualmente em desenvolvimento ativo (versão `0.0.1-SNAPSHOT`).
Enquanto o projeto não atingir uma primeira versão estável (`1.0.0`), apenas a
branch `main` recebe atualizações de segurança.

| Versão          | Suportada          |
| --------------- | ------------------ |
| main (latest)    | :white_check_mark: |
| versões antigas  | :x:                |

## Reportando uma vulnerabilidade

Se você encontrar uma vulnerabilidade de segurança no SyncSpace (ex: falha de
autenticação/autorização, exposição de dados, injeção de SQL, problemas na
geração/validação do JWT, etc.), por favor **não abra uma issue pública**.

Em vez disso, entre em contato diretamente:

- **E-mail:** zsantoskaleb@exemplo.com
- **Assunto sugerido:** `[SECURITY] SyncSpace - <breve descrição>`

Ao reportar, inclua sempre que possível:

- Descrição da vulnerabilidade e impacto potencial;
- Passos para reproduzir (endpoint, payload, cenário);
- Versão/commit em que foi identificada.

**O que esperar:**

- Confirmação de recebimento em até **3 dias úteis**;
- Uma avaliação inicial (aceite ou recusa) em até **7 dias úteis**;
- Se aceita, a correção será priorizada e uma nova versão será publicada assim
  que possível, com crédito ao reportante (caso deseje) no changelog.
- Se recusada, você receberá uma justificativa do porquê não foi considerada
  uma vulnerabilidade válida.

Por se tratar de um projeto em fase de aprendizado/evolução, ainda não há um
programa formal de bug bounty — mas todo relato responsável é bem-vindo e
levado a sério.
