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

## Exercice 2

C'est un extrait d'un programme de gestion d'une école.

Dans cet exemple, des élèves suivent des cours

Il n'y a pas de dépendances vers JPA/Hibernate ou Spring pour simplifier la configuration du projet. Ce n'est pas l'objet de l'exercice.

Par ailleurs, les entités ne sont pas particulièrement bien écrites. Elles peuvent être modifiées dans le cadre de cet exercice, mais ce n'est LEUR refactoring qui est visé.

Ce qui est visé, c'est le refactoring de la méthode coursReussi de CoursServiceImpl. Néanmoins, dans le cadre de ce refactoring, les autres classes pourraient être impactées.

### Que fait cette méthode?

Elle va parcourir toutes les interrogations d'un élève pour un cours donné et vérifier si l'élève a réussi.

Le calcul dépend du type de cours (propriété typeCours):

- pour les cours principaux, il faut une moyenne de 60% sur l'ensemble des interros et 50% dans toutes le interros
- pour les cours secondaires, il faut avoir réussi toutes les interros avec 50% au moins
- les labos, il faut une moyenne de 50%

