# formation-refactoring

Exercices pour la formation refactoring

## GildedRose Refactoring Kata

Hi and welcome to team Gilded Rose. As you know, we are a small inn with a prime location in a prominent city ran by a friendly innkeeper named Allison. We also buy and sell only the finest goods. Unfortunately, our goods are constantly degrading in quality as they approach their sell by date. We have a system in place that updates our inventory for us. It was developed by a no-nonsense type named Leeroy, who has moved on to new adventures. Your task is to add the new feature to our system so that we can begin selling a new category of items. First an introduction to our system:

* All items have a SellIn value which denotes the number of days we have to sell the item.
* All items have a Quality value which denotes how valuable the item is
* At the end of each day our system lowers both values for every item


Pretty simple, right? Well this is where it gets interesting:

* Once the sell by date has passed, Quality degrades twice as fast
* The Quality of an item is never negative
* “Aged Brie” actually increases in Quality the older it gets
* The Quality of an item is never more than 50
* “Sulfuras”, being a legendary item, never has to be sold or decreases in Quality
* “Backstage passes”, like aged brie, increases in Quality as it’s SellIn value approaches; Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but Quality drops to 0 after the concert

We have recently signed a supplier of conjured items. This requires an update to our system:

“Conjured” items degrade in Quality twice as fast as normal items
Feel free to make any changes to the UpdateQuality method and add any new code as long as everything still works correctly. However, do not alter the Item class or Items property as those belong to the goblin in the corner who will insta-rage and one-shot you as he doesn’t believe in shared code ownership (you can make the UpdateQuality method and Items property static if you like, we’ll cover for you).

## Exercice 1

Cet exercice est un cas réaliste tiré de l'application ForHRM. Il est donc assez conséquent et il est conseillé de l'aborder après les autres exercices...

La classe à refactorer s'appelle `ExcellAnomalie`.
Son objectif est de fournir un fichier Excel qui reprend toutes les anomalies de tous les dossiers du portefeuille dans le cadre des processus liés aux déclarations DMF d'un trimestre fourni en paramètre.

Aucune classe de test n'est fournie, mais vous pouvez exécuter la méthode `main` de la classe `ExcellAnomalie` et observer ce qu'elle produit.

Le fichier contient une ligne par anomalie constatée.

Lorsque l'anomalie peut être associée à un dossier, les premières colonnes du fichier Excel contiennent la succursale où est géré le dossier, le numéro de l'équipe
et le gestionnaire en charge du dossier.

Ces anomalies peuvent se trouver à différents niveaux :
* au niveau d'un lot de déclarations (problèmes de transmission ou de signature digitale),
* au niveau d'un dossier repris dans un lot (pas de numéro ONSS ou numéro d'entreprise, présence de paies alors qu'on a annulé l'affiliation...)
* au niveau des travailleur d'un dossier (niss inconnu)
* au niveau du contrat d'un travailleur...

Pour les besoins de l'exercice, les modifications suivantes ont été apportées : 
* Le traitement complexe des paramètres du batch fournis à la ligne de commande ainsi que certaines propriétés récupérées d'un fichier properties
a été remplacé par une liste de valeurs codées en dur et qui permettent d'exécuter l'application.
* La requête SQL qui effectue la recherche des anomalie se compose d'une union (au sens SQL) de requêtes. Seule la partie relative au niveau dossier a été conservée.
* L'application originale envoie les fichiers Excell par mail au demandeur. Ici, les fichiers sont produits dans le répertoire C:\Temp et y restent.
* Pour éviter d'avoir besoin de toute l'application, la classe `ForException` a été remplacée par la classe `Exercice1Exception` et toutes les classes du package `java.sql`
on été implémentées par un mock (toutes les classes qui se trouvent dans le package internal). Si une requête SQL change suite à votre refactoring, vous devrez
adapter la classe internal.`Statement`.
* Quelques petites dépendances à d'autres classes ou modules ForHRM ont été reprises dan les classes `Util` et `Connection`.
* La classe `Trimestre` qui est présente dans un autre module de l'application vous est fournie car elle est susceptible de vous être utile.


## Exercice 2

C'est un extrait d'un programme de gestion d'une école.

Des élèves suivent des cours et passent des interrogations.

Il n'y a pas de dépendances vers JPA/Hibernate ou Spring pour simplifier la configuration du projet. Ce n'est pas l'objet de l'exercice.

Les entités ne sont pas non plus bien écrites, mais elles ne sont pas le sujet de l'exercice. Par contre, elles pourraient être modifiées dans le cadre de ce qui est visé ici: le refactoring de la méthode coursReussi de CoursServiceImpl.

### Que fait cette méthode?

Elle parcourt toutes les interrogations d'un élève pour un cours donné et dit si l'élève a réussi.

Ce calcul dépend du type de cours (propriété typeCours dans Cours):

- pour les cours principaux, il faut une moyenne de 60% sur l'ensemble des interros et 50% dans toutes le interros
- pour les cours secondaires, il faut avoir réussi toutes les interros avec 50% au moins
- les labos, il faut une moyenne de 50%

