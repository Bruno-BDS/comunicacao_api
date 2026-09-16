Endpoints:

POST: Registra um agendamento de mensagem,com a data e hora do agendamento,enviando os dados de quem vai receber, o tipo de mensagem via email, WhatsSapp, SMS, e a mensagem em si,
também o status da mensagem.

GET: Retorna o status do envio da mensagem.

PATCH: Cancela o envio da mensagem, retornando o status como cancelado.

IN DTO:Recebe as informaçoes como nome, email, data e hora,mensagem e faz a validação de email.

OUT DTO: pega os dado do DTO IN formata para que o cliente consiga ver.

preenchimento obrigatorios no DTO IN, dataHoraenvio, nomeDestinatario, emailDestinatario e mensagem.
