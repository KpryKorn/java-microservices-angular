# Java Microservices + Angular

Ce projet est un bac à sable pour reproduire une architecture microservices moderne autour d'un cas d'usage simple: authentification, profil utilisateur et messagerie temps réel.

-> comprendre comment découper une application en services indépendants, faire circuler l'identité utilisateur entre services, et exposer une interface front propre au-dessus de tout ça.

## Ce que ce repo démontre

- Une architecture microservices avec séparation claire des responsabilités
- Une gateway centrale comme point d'entrée unique
- Une authentification centralisée via un serveur d'identité
- Un frontend Angular consommant les APIs de la gateway
- Un service de chat avec websocket pour le temps réel
- Des bases de données séparées par service

## Vue d'ensemble des services

- api-gateway: routage, sécurité, gestion de session, relais vers les services internes
- user-service: gestion du profil utilisateur et données liées
- chat-service: gestion des conversations et échanges en temps réel
- keycloak: gestion des comptes, login et rôles
- frontend-app: interface utilisateur Angular

## Pourquoi ce projet existe

Ce repo sert à expérimenter une architecture proche de ce qu'on rencontre en production:

- séparation frontend / gateway / services métier
- authentification fédérée
- communications HTTP + websocket
- services isolés et évolutifs individuellement

Il peut servir de base d'apprentissage, de référence d'architecture, ou de point de départ pour un projet plus avancé.

## Suivi

Le backlog courant et les idées d'amélioration sont dans [TODO.md](TODO.md).
