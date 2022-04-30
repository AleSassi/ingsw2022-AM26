# Eriantys AM26 Communication Protocol

The following document presents an overview of the communication protocol implemented in the project, that allows it to run as a multi-client online game.

## Protocol Overview
### Connection Initialization
The project uses TCP Sockets. The Game Server (or simply Server) is where the main Game logic (Model & Controller) resides, while the Clients are where each Player executes their actions to play the game.

When a Client wants to connect to the *Server*, it creates a new *Socket* instance and connects to the server. The server, upon receiving a connection request, will instantiate a new *Virtual Client*, which will serve as the main point for sending/receiving data from a specific Client.
After receiving the request, the *Server* expects the Client to send the initial Player data (nickname and number of desired players). One of two thigs may happen:
- The *Server* does not receive such message within a set time frame (e.g.: 5s): the *Virtual Client* will be invalidated and its connection discarded;
- The *Server* received the desired message: it will perform the necessary Player initialization operations (nickname validation, lobby creation) and broadcast the status of these operation to all Clients in the same Lobby as the new Player, both in case they succeeded or not (see **Message Structure** for more info on the messages exchanged). The same happens if the lobby is filled by this last player and the match has to begin.

### Game Loop
As soon as the *Server* receives the player data message for the last player in the lobby and responds with the appropriate status (see above), the Match is initialized with the logged-in Players.
The Server will then broadcast the following messages to all the connected Players in the new lobby:
- The *Table State Message*, that represents a description of the current state of the Table and of objects contained in it. This will be used by View objects to initialize their appearance;
- The *Player Data Message*, containing the necessary info about the Player so that Clients will be able to present them to the user and let them play (e.g.: which Students are in the Entrance space, their Assistant Cards deck...);
- The *Active Player Message* with the nickname of the Player that has to play the first turn: this will be used to disable all actions on non-active clients;
- The *Match State Message* with the State in which the Match resides, so that unneeded objects can be disabled on the active Client.

In order to modify the State of the game and make it progress to the subsequent turns, the Server expects a message from the active Client with a full description of the Action the Player has performed (e.g.: move a Student, play a Character card). This Action message will then be parsed and (if valid) executed on the Server, which responds with the following messages:
- *Action Response Message*: reports to the Client that performed the action the status of the such action (performed, invalid) and an optional error message, so that it can prompt the user accordingly;
- *Table State Message*, broadcast to all Clients in the lobby to let them update their visual objects (see above);
- *Player Data Message*, sent to the active Client to let it update the visual objects for its Player (e.g. Students in the Entrance space) (see above);
- *Active Player Message* with the nickname of the Player that has to play the next turn;
- *Match State Message* with the State in which the Match resides, so that unneeded objects can be disabled on the active Client.
This loop will continue for each Action message received by the Server, and until the End of a Match

### Match End
If an Action brings the Match to an end, or if the Round ends with a Victory condition satisfied, then the Server will broadcast to all Clients in a lobby the *Victory Message*, containing the nickname of the Player (or Players in case of a Team Match) that won the game. This message will be broadcast as soon as the Victory condition is checked and met, so other messages related to the current turn may reach the Client after the *Victory Message*, and will be adequately discarded.

### Diagnostics & Disconnections
After a Client is connected to the Server (and the *Virtual Client* is created), the Server will periodically (at an interval of about 5s) broadcast a simple *PING Message* to all connected Clients. A Client will then respond with a *PONG Message*. The server, once a *PONG Message* is received from a Client, will register such Client as active in the current PING-PONG loop. At the next PING event, each Client which has not been reported as active will be disconnected, the *Active Clients* list is purged and the loop begins.

If a Client does not respond to a *PING Message* within the required time frame and is therefore disconnected, the game must be terminated for all Players in the same lobby: the Server will broadcast them a *Match Termination Message* with a message regarding the termination, and clients must act accordingly.

## Message Structure
Messages are represented on both the Server and Client as Java Objects, which must expose a common interface for serialization-deserialization. These Messages will be encoded and decoded as JSON objects.
The following Messages are present in the Game:
- *Player Connection Request*: this message contains the Player nickname and the desired number of Players in the Lobby;
- *Player Login Response*: contains the nickname sent in the Request and a `boolean` value to tell whether the login was successful or not. If the login did not succeed, an error string is also sent as part of this message. It will also contain the number of players remaining to fill the lobby;
- *Table State Message*: contains a serialized description of the current state of the Table (e.g.: a list of Key-Value Pairs that represent the Island itself, the representation of the Clouds on the table, the number of coins available to pick up, the list of the Character card name and state). These values will be queried from the Table object itself using an appropriate method that returns the pre-formatted Message;
- *Player Data Message*: contains a set of Key-Value pairs representing the Player state in a similar way to the *Table State Message* (such as `nickname` with the String containing the Player nickname, `availableCoins` with the number of coins owned by the Player, `controlledProfessors` with a List of Strings describing the controlled Professors, `studentsInEntrance`, a KVP containing for each Student string the number of Students in the Entrance, `studentsAtTable`, a KVP similar to `studentsInEntrance` but for the Students in the Dining Room, `availableTowersCount` with the number of available Towers, `towerType`, a String describing the Tower color);
- *Active Player Message*: a simple message with the key `activeNickname` and as value the String containing the nickname of the currently active Player;
- *Match State Message*: a simple message with the key `matchState` and as value a String describing the current state/phase of the Match;
- *Player Action Message*: a message containing a String describing the Action Type based on an Enum (`playerActionType`) and a set of KVP with the parameters of such action (e.g.: if the Action is MoveStudentToIsland, the message will contain its type and the keys `student` with the String of the Student type, and `islandIndex` with the index of the destination Island). The message will vary its fields (except for `playerActionType` which will always be present) depending on the action type to only send what is appropriate and relevant to the action. The decoded message will then initialize the attributes corresponding to these fields to a default, meaningless value, as only the ones relevant to the ActionType are guaranteed to be valid;
- *Victory Message*: contains a key `winners` and as value the list of Strings containing the nicknames of the winners;
- *Match Termination Message*: a message with a key `terminationReason` and as value a descriptive string that informs the Client why the match has ended;
- *PING-PONG Message*: a message with key `ping` and value 0 if PING, 1 if PONG;

These messages are proper objects, and all expose:
- A method that lets the callee get a JSON description of the message object ready to be sent over the network;
- A method that attempts to decode the object from a decoded JSON representation, raising an error if the representation does not match the message specifications.

Below is a visual representation of the interactions between a Client and the Server:
<pre>
  ________                                  ________
 | Client |                                | Server | 
  ‾‾‾‾|‾‾‾                                  ‾‾‾‾|‾‾‾
      |----------Connection Request------------>|   
      |<--------Connection Response-------------|   
      |-------Nickname & Match Params---------->|   
      |<------------Login Status----------------|   
      |                                         |
      |<----------------PING--------------------|
      |-----------------PONG------------------->|  
      |                                         |
      |<----------Table Description-------------|   
      |<---------Player Description-------------|   
      |<---------Active Player Name-------------|   
      |<------------Match Phase-----------------|   
      |                                         |
      |<----------------PING--------------------|
      |-----------------PONG------------------->|
      |                                         |
      |----------Player Action Data------------>|   
      |<-----------Action Response--------------|   
      |<----------Table Description-------------| -> Every time the Player performs an Action, the description of what the Table contains (Students, cards, clouds, islands...) is sent to all Clients part of the same Lobby so that they can update their Views
      |<---------Player Description-------------| -> Every time the Player performs an Action, the description of what the Player owns is sent to the Client controlling that Player
      |<---------Active Player Name-------------| -> Broadcasts the nickname of the Player who has to perform the next action
      |<------------Match Phase-----------------| -> Broadcasts the current match phase
      |                                         |
      |<----------------PING--------------------|
      |-----------------PONG------------------->| -> If we don't receive a PONG response the Client will be disconnected and the match ends immediately
      |                                         |
      |                    .                    |
      |                    .                    |
      |                    .                    |
      |                                         |
      |<-----------Victory Message--------------| -> The Server also disconnects all Clients so that they can start a new Match by reconnecting to the server
</pre>