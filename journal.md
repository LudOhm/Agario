## Suivi 30/01

Tout le monde est là, sauf Jérémy Pitel (retard RER C).

Objectif pour semaine 1 : projet minimal

- doit compiler en une simple commande
- doit afficher une fenêtre avec une cellule qu'on peut déplacer à l'aide du clavier ou de la souris

## Suivi 6/02 : 

### Rempli par les étudiants

ce qu'on a fait :

-fenêtre graphique et implementation de la balle en mouvement avec la souris (Qian En)

-agrandir la map plus que taille de l'écran et début de faire suivre la balle le long de la map(Ludivine)

-Diviser la balle en 2(Jérémy)

-l'interface du jeu(bouton back et une image de fond)(Shiqi)

ce qu'on veut faire pour le mardi 13/02 :

-les petites billes qui serviront a faire agrandir la balle(Amel)

## Suivi 13 /02 :

### Rempli par les étudiants

ce qu'on a fait :

- mettre en place gradle(Qian En)

- résoudre les problèmes de limites du map(Qian En)

- clique gauche pour diviser la balle en deux avec le nouveau code(Ludivine) 

- initialisation des billes qui disparaissent lorsqu'une collision est détectée et font grossir la balle du joueur (Amel)

- résoudre le bug sur le timer qui s'arrête pas, Implémenter l'initialisation de couleur et position aléatoire pour la Ball, et avoir un contour pour les Ball(Shiqi)

- Mise en place des bots avec déplacements simples(rebonds sur les murs), interactions des bots avec les billes(peuvent grossir) et peuvent mettre fin au joueur.(Jérémy) 

ce qu'on veut faire pour le mardi 20/02 :

- Améliorer les bots (détection du jouer / des autres bots ; détection des billes; peut se nourrir des autres bots ; joueur peut manger bot)(Jérémy)

### Commentaires de l'enseignant

- il faut un script de compilation
- il faut du MVC
- Jérémy absent (RER encore)
- ajouter bots aux objectifs

## Suivi 20/02

### Rempli par les étudiants

ce qu'on a fait :

- modification minimap (Qian En)

- ajout de l'écran de gameover (Ludivine)


### À faire

- cloner git protocole, exécuter et comprendre les exemples
- commencer à intégrer TCP (changement architecture ?) et implémenter le protocole
- ajouter progressivement les aspects manquants du jeu (virus, projectiles, ...)


## Suivi 27/02

### Rempli par les étudiants

ce qu'on a fait :

- mettre en place une base du protocole TCP (Qian En et Shiqi)
- initialisation des virus sur la map + correction apparence des billes + grossir ralentit les balles (Amel)
- amelioration des bots (detection billes, joueurs et autres bots; nombre constant de bots)

### À faire

- intégrer GSON
- implémenter le protocole (commencer par le protocole d'initialisation, plus simple)


## Suivi 05/03

### Rempli par les étudiants

en cours:

- améliorer virus et la superposition des balles -> début d'un playerRank (Amel)
- ajouter le fait de lancer un projectile puis lancer ce projectile(Ludivine)
- intégré Gson et début pour le mode online(bcp de bug a résoudre)(Qian En et Shiqi)

ce qu'on a fait :

- avoir une bouton pour quitter le serveur（Shiqi）
- les virus peuvent split la balle + premier tri des balles selon leur taille (problème ralentissement du jeu)


### À faire

Découper et planifier le travail pour écrire le client et le serveur

- modifier le serveur fourni pour qu'il envoie du json (entré à la main via un Scanner) et affiche le json reçu
- faire que le programme client (ex-jeu solo) s'y connecte
- ajouter ce qu'il faut pour que le client réagisse aux json reçu
- ajouter ce qu'il faut au client (Controller) pour que les actions du joueur soient envoyées sur le réseau au format requis
- ... et ainsi de suite (petit objectif par petit objectif)


## Suivi 12/03

### Rempli par les étudiants

en cours : 

- implémente le système de bille et virus dans le mode online
- Implémentation playerRank (Amel)
- Implémentation sélection skin (Ludivine)

ce qu'on a fait: 

- quand plusieurs balles après split, défaite quand elles sont toutes mangés (Ludivine)
- Finalisation des virus (Amel)
- chaque joueur peut déplacé sa ball(mode online) (Qian En et Shiqi)
- Lorsque le joueur quit le jeu, leurs info soit supprimer dans la liste gson, ainsi la ball du joueur soit effacer dans le jeu(Qian En et Shiqi)


## Suivi 19/03

### Rempli par les étudiants

en cours : 

- implémente les billes et les virus dans online(Qian En et Shiqi)
- Appliquer le split aux bots(Amel)


ce qu'on a fait: 

- selection d'une couleur pour le joueur possible (Ludivine)
- modifie ui du jeu, et avoir une page pour entrer l'adresse ip(Qian En et Shiqi)
- Taille unique pour les virus, début de l'affichage du classement(Amel)
- correction bug bots (sur place)

### Absences

Luidivine malade
Jérémy en retard

### À faire

- ajouter ce qui manque dans le message de gameupdate
- relire et refactoriser (penser à MVC)


## Suivi 26/03

### Rempli par les étudiants

en cours : 

- résoudre les bugs des joueurs "mange" un autre joueur

ce qu'on a fait : 

- pouvoir mettre des skin de fichier image (Ludivine)
- correction problème split de la balle joueur par les virus(Amel)
- implémente les billes en mode online (Qian En et Shiqi)


à faire :

- comprendre pourquoi ce n'est pas fluide (instrumenter le code et regarder combien de message sont effectivement envoyés et reçus chaque seconde)
- résoudre le bug
- virus


## Suivi 02/04

### Rempli par les étudiants

en cours :

- finalise le virus en online

ce qu'on a fait : 

- correction bug split skin solo + skin sans image (ludivine)
- résoudre le bug du Ball n'est pas fluide (Qian En)
- résoudre le bug des joueurs ne peut pas manger un autre joueur(Shiqi et Qian En)
- affichage du classement en online(Qian En et Shiqi)
- finalisation du classement en solo + changement apparence gameOver (Amel)
- début de virus en online(Qian En)
- début split virus bots (Jérémy)


## Suivi 23/04

### Rempli par les étudiants

en cours :

- résoudre le bug du split la balle(Qian En et Shiqi)
- Ajout animation projectile lancé (Ludivine + Amel)

ce qu'on a fait : 

- Correction problème projectiles qui n'apparaissaient pas + touche espace pour lancer les projectiles (Amel)
- début du rapport sur google doc (Ludivine)
- résoudre bug des joueurs mange un autre joueur(Qian En)
- début split(Qian En et Shiqi)
- Changement architecture bots pour split(Jérémy)

à faire:
- réfléchir à soutenance (explication modélisation/conception, échanges de message et mises à jour du modèle)
- préparer belle démo du produit quasi-fini
- instrumenter pour visualiser les échanges sur le réseau

## Suivi 30/04

### Rempli par les étudiants

en cours :

- amélioration de la structure du code (Amel)
- Ajuster Caméra quand Balle très grosse(Qian En)
- amélioration la logique des balles après split qui suivrent la balle parent(Qian En)
- menu paramètre pour ajuster le son (Ludivine)


ce qu'on a fait : 

- ajout d'une musique de fond dans le jeu (Ludivine)
- modif gameover depuis online + changement README (Ludivine)
- modif gestion billemangee en solo (Amel)
- Ajout message "Tuto"  (Amel)
- split et virus online(Qian En et Shiqi) 