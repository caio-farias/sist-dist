# sist-dist: O pior Protótipo de Sistema Distribuído em Java

## Visão Geral do Projeto
Este é um protótipo básico de um sistema distribuído usando `java.net`. Espere um código terrível.
- Servidores: Implementados com os executores do `java.util.concurrent`;
- Proxy+LoadBalancer: Gerencia o tráfego e distribuem as solicitações, mas não muito bem.
- Banco de Dados em Memória
- Protocolo CRUD/TCP: Implementado por mim. Este é um protocolo baseado em TCP personalizado que implementa operações básicas de CRUD (Create, Read, Update, Delete) entre os componentes do sistema. É simples, mas serve ao propósito de se comunicar com o banco de dados em memória.

## Protocolos Suportados
- HTTP: Funciona na maioria das vezes, mas não em todas.
- UDP (instável)
- CRUD/TCP
