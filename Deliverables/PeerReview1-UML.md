# Peer-Review 1: UML

Albertini Federico, Betti Leonardo, Sassi Alessandro

Gruppo AM26

Valutazione del diagramma UML delle classi del gruppo AM56.

## Lati positivi

- Rappresentare le diverse fasi di gioco trmite appositi Enum gestiti direttamente dal Model, per permettere al RoundHandler di sapere in ogni momento in quale fase del gioco si trova;
- AdvancedGame sottoclasse di Game per aggiungere la funzionalità aggiuntiva dele carte personaggio al di sopra del gioco base.

## Lati negativi

- Basso riuso di codice dato dall'utilizzo di semplici array di `int` per gli Studenti: ogni volta in cui è necessario aggiungere/rimuovere studenti si è obbligati a copiare il codice relativo a tali azioni in ogni oggetto che può tenere studenti;
- Mancano metodi in Player per ottenere/impostare/rimuovere i Professori controllati, in quanto l'attributo Professors è private, e per modificare la School (vedi sotto);
- In School manca addToEntrance (gli Studenti possono essere aggiunti in Ingresso ad esempio a seguito di un'azione Personaggio), removeFromClassroom (necessaria ad esempio per la carta Thief) e addTower (necessaria se una Torre viene sostituita da quella di un'altro giocatore);
- Game richiede Player per ogni metodo, ma il Controller non può sapere quale è il Player corrente (roundHandler privato) e porterebbe a problemi di sicurezza nel caso in cui un giocatore "forzasse" se stesso come giocatore passato come parametro. Il RoundHandler, invece, espone già al Game un metodo public che ritorna il giocatore corrente;
- Mancano i metodi di modifica delle torri sia su Island che su Islands (utile nel caso in cui le isole siano accorpate);
- AggregateIslands() dovrebbe accettare come parametro l'Island che viene accorpata per permettere di effettuare l'aggregazione;
- Bag non permette di capire se è vuota (condizione di fine partita) e di aggiungere Studenti (operazione di Character);
- Character espone solamente activateEffect, ma per poter compiere l'azione tale metodo deve almeno ritornare un intero, per permettere alle carte che modificano l'influenza di ritornare il modificatore;
- Island non permette di impostare/togliere BanCard, richiesto dalla carta personaggio Apothecary;
- Il setup delle carte non è permesso in quanto le carte non possono accedere alla Bag (privata per GameBoard), nè AdvancedGame può accedere a GameBoard (privata per Game);
- 12 sottoclassi di Character possono essere evitate, in quanto le loro funzionalità possono essere ricondotte a poche macro-funzionalità parametriche.

## Confronto tra le architetture

- Avere fasi di gioco gestite a interno di model anzichè da controller: permettono di filtrare meglio i messaggi di rete che arrivano, le azioni non possibili e lo stato del gioco attuale per passare allo stato successivo. Le azioni così non sono chiamate direttamente dal controller a seconda del messaggio di rete che arriva al server ma dal model stesso, che con un singolo metodo permette di aggiornare tutto lo stato del gioco e di effettuare l'azione appropriata.