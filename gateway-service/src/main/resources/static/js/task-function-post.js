document.addEventListener("DOMContentLoaded", function () {

    // Criar tarefa
    const btnCriar = document.getElementById('btnCriarTarefa');

    if (btnCriar) {
        btnCriar.addEventListener('click', criarTarefa);
    }

    // Evento da aba listar
    const tabListar = document.getElementById("tabListar");

    if (tabListar) {
        tabListar.addEventListener("shown.bs.tab", listarTarefas);
    }

    const tabExecute = document.getElementById("")

});

// Função para criação de tarefas
function criarTarefa() {
    const titulo = document.getElementById('titulo').value;
    const descricao = document.getElementById('descricao').value;
    const usuarioId = document.getElementById('usuarioId').value;

    if (!titulo || !descricao || !usuarioId) {
        alert("Preencha todos os campos.");
        return;
    }

    const payload = {
        title: titulo,
        description: descricao,
        userId: parseInt(usuarioId)
    };

    fetch('/api/v1/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao criar tarefa: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            alert('Tarefa criada com sucesso! ID: ' + data.id);
            document.getElementById('titulo').value = '';
            document.getElementById('descricao').value = '';
            document.getElementById('usuarioId').value = '';
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao criar tarefa.');
        });
}

/*
 * Função responsável por buscar e renderizar as tarefas cadastradas.
 *
 * Fluxo:
 * 1. Verifica a flag 'tarefasCarregadas' para evitar múltiplas requisições desnecessárias.
 *
 * 2. Envia uma requisição HTTP GET para:
 *    /api/v1/tasks?page=0&size=10&sort=id,asc
 *    buscando até 10 tarefas ordenadas por id crescente.
 *
 * 3. Valida se a resposta HTTP foi bem-sucedida (status 2xx).
 *
 * 4. Converte a resposta para JSON e acessa a propriedade 'content'
 *    (estrutura paginada do Spring Boot).
 *
 * 5. Limpa o container HTML antes de renderizar novamente.
 *
 * 6. Percorre a lista de tarefas retornadas e:
 *    - Cria dinamicamente elementos HTML (cards)
 *    - Exibe título e descrição
 *    - Exibe badge com status (OPEN, IN_PROGRESS, COMPLETED, CANCELED)
 *    - Adiciona botões de ação (Executar, Concluir, Cancelar)
 *
 * 7. Em caso de erro na requisição, registra no console.
 *
 * Observação:
 * Esta função realiza atualização dinâmica da interface (DOM),
 * sem recarregar a página inteira.
 */
let paginaAtual = 0;
let filtroAtual = null;
function listarTarefas(status = null, page = 0) {

    filtroAtual = status;
    paginaAtual = page;

    let url = `/api/v1/tasks?page=${page}&size=10&sort=id,asc`;

    if (status) {
        url += `&status=${status}`;
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao buscar tarefas");
            }
            return response.json();
        })
        .then(data => {

            const lista = document.getElementById("listaTarefas");
            lista.innerHTML = "";

            data.content.forEach(task => {

                const col = document.createElement("div");
                col.className = "col-md-6";

                col.innerHTML = `
                    <div class="card shadow-sm border-0 p-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">${task.title}</h5>
                            <span class="badge ${getBadgeColor(task.status)}">
                                ${task.status}
                            </span>
                        </div>
                        <p class="text-muted mt-2">
                            ${task.description}
                        </p>
                        <div class="d-flex gap-2 flex-wrap">
                            ${renderizarBotoes(task)}
                        </div>
                    </div>
                `;

                lista.appendChild(col);
            });

            renderizarPaginacao(data);
        })
        .catch(error => console.error("Erro:", error));
}

/*
 * Retorna a classe CSS da badge de acordo com o status da tarefa.
 *
 * Essa função é responsável por mapear o status da tarefa
 * (OPEN, IN_PROGRESS, COMPLETED, CANCELED)
 * para uma cor visual do Bootstrap.
 *
 * Isso mantém a lógica de estilo separada da renderização HTML.
 */
function getBadgeColor(status) {
    switch (status) {
        case "OPEN": return "badge bg-secondary";
        case "IN_PROGRESS": return "badge bg-primary";
        case "COMPLETED": return "badge bg-success";
        case "CANCELED": return "badge bg-danger";
        default: return "badge bg-dark";
    }
}

/*
 * Executa uma tarefa específica.
 *
 * Fluxo:
 * 1. Envia uma requisição PATCH para o endpoint /tasks/{id}/execute
 *    solicitando alteração do status para IN_PROGRESS.
 *
 * 2. Verifica se a resposta foi bem-sucedida.
 *
 * 3. Reseta a flag de controle de carregamento.
 *
 * 4. Recarrega a listagem de tarefas para refletir o novo status.
 */
function executarTarefa(id) {
    // Envia requisição PATCH
    fetch(`/api/v1/tasks/${id}/execute`, {
        method: 'PATCH'
    })
        .then(response => {
            // Verifica se deu erro
            if (!response.ok) {
                throw new Error("Erro ao executar tarefa");
            }
            // Força recarregamento da lista
            //tarefasCarregadas = false; // permite recarregar
            listarTarefas();
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao executar tarefa");
        });
}

/*
 * Marca a tarefa como concluída.
 *
 * Fluxo:
 * 1. Envia uma requisição HTTP PATCH para o endpoint
 *    /api/v1/tasks/{id}/complete
 *    solicitando a alteração do status para COMPLETED.
 *
 * 2. Verifica se a resposta HTTP foi bem-sucedida
 *    (status entre 200 e 299).
 *
 * 3. Caso sucesso:
 *    - Reseta a flag de controle de carregamento.
 *    - Chama listarTarefas() para atualizar a interface.
 *
 * 4. Caso erro:
 *    - Captura a exceção da Promise.
 *    - Exibe mensagem no console e alerta para o usuário.
 */
function concluirTarefa(id) {
    // Envia requisição PATCH para alterar parcialmente o recurso
    fetch(`/api/v1/tasks/${id}/complete`, {
        method: 'PATCH'
    })
        .then(response => {
            // Verifica se a resposta HTTP foi bem-sucedida (2xx)
            if (!response.ok) {
                throw new Error("Erro ao concluir tarefa");
            }
            // Permite recarregar novamente a listagem
            //tarefasCarregadas = false;
            // Atualiza a lista de tarefas
            listarTarefas();
        })
        .catch(error => {
            // Captura erros da requisição ou do throw acima
            console.error(error);
            // Feedback simples ao usuário
            alert("Erro ao concluir tarefa");
        });
}

/*
 * Cancela uma tarefa.
 *
 * Envia requisição PATCH para /tasks/{id}/cancel.
 * Caso a operação seja bem-sucedida, atualiza a listagem.
 */
function cancelarTarefa(id) {
    fetch(`/api/v1/tasks/${id}/cancel`, {
        method: 'PATCH'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao cancelar tarefa");
            }
            //tarefasCarregadas = false;
            listarTarefas();
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao cancelar tarefa");
        });
}

function renderizarBotoes(task) {

    switch (task.status) {

        case "OPEN":
            return `
                <button class="btn btn-sm btn-info text-white"
                    onclick="executarTarefa(${task.id})">
                    Executar
                </button>
                <button class="btn btn-sm btn-danger"
                    onclick="cancelarTarefa(${task.id})">
                    Cancelar
                </button>
            `;

        case "IN_PROGRESS":
            return `
                <button class="btn btn-sm btn-success"
                    onclick="concluirTarefa(${task.id})">
                    Concluir
                </button>
                <button class="btn btn-sm btn-danger"
                    onclick="cancelarTarefa(${task.id})">
                    Cancelar
                </button>
            `;

        default:
            return `<span class="text-muted">Sem ações disponíveis</span>`;
    }
}

function renderizarPaginacao(data) {

    const paginacao = document.getElementById("paginacao");
    paginacao.innerHTML = "";

    // ANTERIOR
    if (!data.first) {
        const prev = document.createElement("button");
        prev.className = "btn btn-outline-primary mx-1";
        prev.innerHTML = '<i class="bi bi-arrow-left"></i> Anterior';
        prev.onclick = () => listarTarefas(filtroAtual, data.number - 1);
        paginacao.appendChild(prev);
    }

    // NÚMEROS DAS PÁGINAS
    for (let i = 0; i < data.totalPages; i++) {

        const botao = document.createElement("button");

        botao.className = `btn ${
            i === data.number ? "btn-primary" : "btn-outline-primary"
        } mx-1`;

        botao.innerText = i + 1;

        botao.onclick = () => listarTarefas(filtroAtual, i);

        paginacao.appendChild(botao);
    }

    // PRÓXIMO
    if (!data.last) {
        const next = document.createElement("button");
        next.className = "btn btn-outline-primary mx-1";
        next.innerHTML = 'Próximo <i class="bi bi-arrow-right"></i>';
        next.onclick = () => listarTarefas(filtroAtual, data.number + 1);
        paginacao.appendChild(next);
    }
}