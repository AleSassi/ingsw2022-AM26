# Peer-Review 1: UML

Albertini Federico, Betti Leonardo, Sassi Alessandro

Gruppo AM26

Valutazione del protocollo di comunicazione del gruppo AM56.

## Lati positivi

- La presenza di una prima fase in cui il server invia al client appena connesso se è il primo o meno nella Lobby permette di migliorare la User Experience: anzichè chiedere a tutti i client le impostazioni della partita e poi ignorarle per i client non-primi, queste vengono nascoste sin da subito
- Il campo time sul messaggio di ping permette di capire la latenza di rete sia lato client sia lato server, evitando di utilizzare timer e callback specifici per capire quato tempo è passato dall'invio alla ricezione del messaggio di ping

## Lati negativi

- Il protocollo, per specifiche della prova finale, deve essere realizzato interamente con Socket TCP. Pertanto, non dovrebbe essere consentito l'impiego di UDP per la fase iniziale di "handshake" che, se le Socket venissero realizzate in UDP, sarebbe del tutto ridondante (così come l'invio di IP e Porta nei primi messaggi di login);
- Anche in setup della connessione, la presenza dell'attributo `time` nei messaggi potrebbe essere superflua, in quanto TCP garantisce la consegna dei pacchetti inviati sulla rete in ordine;
- L'idea di inserire uno `stringMessage` come attributo identificatore del tipo di messaggi permette troppa libertà nel suo contenuto. Forse sarebbe meglio avere un campo separato (e.g `messageType`) il cui contenuto è una breve stringa che rappresenta univocamente il tipo del messaggio (e.g FirstClientMessage anzichè "I'm the Eriantys client, sended login")
- La struttura dei messaggi di rete non permette al Client di sapere quanti giocatori mancano nella propria lobby prima che il gioco cominci
- L'invio di alcuni attributi assieme al model (ad es l'ID del giocatore vincitore) potrebbe essere scorporato dal messaggio che invia il Model e inviato una sola volta a fine partita. Così si risparmierebbe (seppur lievemente) in banda consumata e renderebbe il protocollo più flessibile (ad esempio nel caso in cui la codifica "-1" per `winnerID` non fosse più disponibile come flag ma dovesse essere utilizzata per altri scopi)
- I giocatori non attivi potrebbero evitare di mandare al Server, ad ogni ricezione del model, una risposta in cui ribadiscono di non essere attivi (dato che il Server lo sa già)
- Le impostazioni di gioco possono essere inviate una volta sola e memorizzate dai client, così da evitare di inviarle continuamente ad ogni azione di un giocatore
- Non siamo riusciti a capire con esattezza come intendete codificare i messaggi (JSON, Java Serializable, Custom String Serialization). Abbiamo ipotizzato JSON, data la struttura dei messaggi del Model

## Confronto tra le architetture

- Nell'architettura di AM56 si è previsto un generico messaggio di errore che, anzichè contenere una descrizione dell'errore, invia solamente un error code; invece nella nostra architettura i messaggi che il Client manda al Server e che possono generare errori o cambi di stato prevedono una specifica risposta con, assieme ad altri attributi, una stringa che descrive interamente la casua dell'error. Il formato di AM56 quindi permette di rimanere più generico (evitando sottoclassi di messaggi simili) e più leggero a livello di rete (un error code sono pochi byte rispetto a un'intera stringa)
- Il campo `time` nei messaggi di ping-pong permette in ricezione di capire quanto tempo è trascorso dall'invio senza l'utilizzo di timer specifici per l'evento in questione
- Nella nostra architettura i messaggi relativi alla struttura del model sono stati separati (TableStateMessage, PlayerStateMessage) mentre AM56 li ha mantenuti uniti. Quest'ultima scelta potrebbe rendere più difficile l'implementazione di "messaggi-delta" in cui si invia soltanto la differenza (il delta, appunto) tra lo stato precedente e quello corrente. Infatti se lo stato di ogni giocatore fosse inviato come singolo messaggio basterebbe identificare per tale giocatore quali sono le differenze di stato e iviarle immediatamente. Invece con un messaggio "monolitico" andrebbe ripetuta l'operazione per ogni giocatore, anche se lo stato di tale giocatore non è sicuramente cambiato (perchè, ad esempio, è inattivo)