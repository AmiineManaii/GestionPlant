# PlantManager - Application de Gestion de Plantes

**PlantManager** est une application Android moderne conçue pour aider les amateurs de plantes à prendre soin de leurs compagnons verts. L'application permet de suivre les cycles d'arrosage, de consulter la météo pour adapter les soins, et de centraliser toutes les informations nécessaires à la santé des plantes.

## 🏗️ Architecture du Projet

L'application suit les principes de l'architecture **MVVM (Model-View-ViewModel)** recommandée par Google pour assurer une séparation claire des responsabilités, une testabilité accrue et une maintenance facilitée.

-   **Model (Données)** : Utilisation de **Room Database** pour la persistance locale (plantes, utilisateurs, sessions, historique d'arrosage) et de **Retrofit** pour la récupération des données météo via l'API OpenWeatherMap.
-   **View (Interface Utilisateur)** : Développée entièrement avec **Jetpack Compose (Material 3)** pour une interface moderne, réactive et fluide.
-   **ViewModel (Logique métier)** : Gère l'état de l'interface utilisateur et communique avec la couche de données via des **StateFlow** et des **Coroutines**.

## 🌟 Fonctionnalités Principales

-   **Tableau de Bord Météo** : Affiche les conditions actuelles et les prévisions détaillées avec des conseils d'entretien adaptés à la météo (ex: éviter d'arroser s'il pleut).
-   **Gestion des Plantes** : Ajout, modification et suppression de plantes avec des informations spécifiques (type, fréquence d'arrosage, image).
-   **Suivi de l'Arrosage** :
    -   Calcul automatique de la prochaine date d'arrosage.
    -   Historique complet des arrosages effectués.
    -   Système de notifications locales pour les rappels.
-   **Calendrier d'Arrosage** : Vue globale mensuelle pour visualiser tous les arrosages passés et à venir.
-   **Système d'Authentification** : 
    -   Inscription et connexion sécurisées.
    -   Persistance de la session utilisateur via Room (remplace SharedPreferences).
    -   Hachage des mots de passe en SHA-256.
-   **Statistiques et Conseils** : Visualisation des performances d'entretien et accès à une base de connaissances pour le soin des plantes.
-   **Filtres Avancés** : Recherche et filtrage horizontal par catégorie (Toutes, À arroser aujourd'hui, etc.).

## 🛠️ Technologies Utilisées

-   **Langage** : Kotlin
-   **UI** : Jetpack Compose (Material 3)
-   **Base de Données** : Room (Version 5)
-   **Réseau** : Retrofit & OkHttp
-   **Navigation** : Jetpack Navigation Compose
-   **Gestion d'État** : ViewModel, LiveData, StateFlow
-   **Asynchronisme** : Coroutines & Flow
-   **Notifications** : AlarmManager & NotificationCompat
-   **Sécurité** : Hachage SHA-256 pour les mots de passe

## 💾 Structure de la Base de Données

L'application utilise une base de données Room centralisée ([PlantDatabase](app/src/main/java/com/example/plantmanager/data/local/PlantDatabase.kt)) avec les entités suivantes :

1.  **Plants** : Stocke les informations sur chaque plante.
2.  **Users** : Gère les comptes utilisateurs et les paramètres de rappel.
3.  **Sessions** : Gère l'état de connexion persistant de l'utilisateur.
4.  **WateringEvents** : Enregistre chaque action d'arrosage pour l'historique et les statistiques.

## 🚀 Installation et Configuration

1.  Cloner le dépôt.
2.  Ouvrir le projet dans **Android Studio**.
3.  Ajouter votre clé API OpenWeatherMap dans le `WeatherViewModel` (ou via une variable d'environnement).
4.  Compiler et lancer l'application sur un émulateur ou un appareil physique (Android 8.0+ recommandé).

---
Développé avec ❤️ pour les amoureux de la nature.
