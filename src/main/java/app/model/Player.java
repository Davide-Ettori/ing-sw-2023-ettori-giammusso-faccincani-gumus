package app.model;

import app.controller.Game;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import static app.model.State.*;
/**
 * class which represent the player on the client side
 * @author Ettori Faccincani
 * mutable
 * implements Serializable because it will be sent in the soket network
 */
public class Player implements Serializable {
    private String name;
    private boolean isChairMan;
    public Library library;
    private PrivateObjective objective;
    public int pointsUntilNow;
    private State state;
    public Board board;
    private ArrayList<Library> librariesOfOtherPlayers;
    private Socket mySocket;
    private Game gameRMI;

    public static void main(String[] args){
        // prendi da input il nickname del utente
        // apri una connessione socket TCP con il server
        // prendi il RMI del server usando il naming e l'ip giusto
        // controlla se è già stato preso e nel caso chiedilo di nuovo
        // aspetta che il server gli mandi le informazioni iniziali
        // deserializza la classe Player che ti è arrivata tramite il buffer della socket
        Socket socket = null; // questa dovrebbe essere la socket TCP che ho inizializzato poco prima
        Game gameRMIInstance = null; // questo dovrebbe essere l'oggetto remoto che sta sul server
        Player player = new Player(false, "pluto"); // questo oggetto dovrebbe venire dal server, questo è un esempio
        player.setSocket(socket); // setto la socket di questo player con quella che ho creato prima
        new Player(player, gameRMIInstance); // crea la classe Player effettiva con cui l'utente giocherà, tengo il riferimento al RMI del server
        // ovviamente, per adesso intellij da molti errore di NullPointer, spariranno quando implementeremo la network
    }

    public Player(boolean isChair, String namePlayer) { // Costruttore semplice, di base. Ancora non so se servirà per davvero
        name = namePlayer;
        isChairMan = isChair;
        library = new Library();
        pointsUntilNow = 0;
        state = NOT_ACTIVE;
    }
    public Player(Player p, Game rmi){
        name = p.name;
        isChairMan = p.isChairMan;
        library = new Library(p.library);
        objective = p.objective;
        pointsUntilNow = p.pointsUntilNow;
        state = p.state;
        board = new Board(p.board);
        gameRMI = rmi;
        librariesOfOtherPlayers = new ArrayList<>(p.librariesOfOtherPlayers);
        mySocket = p.mySocket;
        startGame();
    }
    public Player(Player p){ // copy constructor
        name = p.name;
        isChairMan = p.isChairMan;
        library = new Library(p.library);
        objective = p.objective;
        pointsUntilNow = p.pointsUntilNow;
        state = p.state;
        board = new Board(p.board);
        gameRMI = p.gameRMI;
        librariesOfOtherPlayers = new ArrayList<>(p.librariesOfOtherPlayers);
        mySocket = p.mySocket;
    }
    /**
     * getter per il nome
     * @author Ettori
     * @param: void
     * @return: il nome
     */
    public String getName() {
        return name;
    }
    /**
     * setter per il PO
     * @author Ettori
     * @param: il PO da settare
     * @return: void
     */
    public void setPrivateObjective(PrivateObjective obj) {
        objective = obj;
    }
    /**
     * prende le carte dalla board e le trasferisce sulla library del giocatore
     * @author Ettori
     * @param: la lista di coordinate accoppiate delle carte da prendere
     * @return: true sse il trasferiemento è andato a buon fine (carte in posizione corretta)
     */
    private boolean pickCards(ArrayList<Integer> coord) { // Coordinate accoppiate. Questo metodo verrà chiamato quando la GUI o la CLI rilevano una scelta dall'utente
        if(!board.areCardsPickable(coord))
            return false; // hai scelto delle carte che non possono essere prese
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < coord.size(); i += 2) {
            cards.add(new Card(board.getGameBoard()[coord.get(i)][coord.get(i + 1)]));
            board.getGameBoard()[coord.get(i)][coord.get(i + 1)] = new Card(coord.get(i), coord.get(i + 1)); // dopo che hai preso una carta, tale posto diventa EMPTY
        }

        deployCards(chooseCol(cards.size()), chooseCardsOrder(cards));
        return true;
    }
    /**
     * posizione fisicamente le carte nella colonna scelta
     * @author Ettori
     * @param: colonna
     * @param: lista delle caret scelte
     * @return: true sse va a buon fine
     */
    private boolean deployCards(int col, ArrayList<Card> cards) {
        library.insertCards(col, cards);
        return true;
    }
    /**
     * fa scegliere all'utente la colonna dove vuole mettere le carte
     * @author Ettori
     * @param: numero delle carte
     * @return: la colonna scelta (controlla anche che la colonna sia valida)
     */
    private int chooseCol(int numCards) {
        int col;
        while (true) { // questo va fino a che l'utente sceglie la colonna, per ora lo forziamo a mano
            col = 2;
            if (col <= 0 || col > library.COLS) // scelta non valida
                continue;
            if (!library.checkCol(col, numCards)) // scelta non valida
                continue;
            break;
        }
        return col;
    }
    /**
     * funzione di helper per scambiare le carte nella lista scelta dall'utente
     * @author Ettori
     * @param: lista delle carte
     * @param: indice numero 1
     * @param: indice numero 2
     * @return: void
     */
    private void swapCards(ArrayList<Card> cards, int i, int j) {
        Card temp = cards.get(i);
        cards.set(i, cards.get(j));
        cards.set(j, temp);
        return;
    }
    /**
     * fa scegliere all'utente l'ordine con cui posizionare le carte
     * @author Ettori
     * @param: lista delle carte
     * @return: lista nell'ordine scelto dall'utente
     */
    private ArrayList<Card> chooseCardsOrder(ArrayList<Card> cards) {
        while (true) { // questo va fino a che l'utente sceglie l'ordine delle carte, per ora lo forziamo a mano
            swapCards(cards, 0, 1); // scambio 2 carte a caso
            break;
        }
        return cards;
    }
    /** ------------------------------------------------------------------------------------------------------------- */
    public void clone(Player p){ // copia la versione sul server dentro a quella del client
        name = p.name;
        isChairMan = p.isChairMan;
        library = new Library(p.library);
        objective = p.objective;
        pointsUntilNow = p.pointsUntilNow;
        state = p.state;
        board = new Board(p.board);
        gameRMI = p.gameRMI;
        librariesOfOtherPlayers = new ArrayList<>(p.librariesOfOtherPlayers);
        mySocket = p.mySocket;
    }
    public void startGame(){
        startRedrawThread();
        startUpdatePlayerFromRemoteThread();
        // aspetta che il server ti faccia iniziare la partita, ovvero aspetta il tuo primo turno
        startTurn();
        return;
    }
    private void startTurn(){
        if(board.isBoardUnplayable()) {
            board.fillBoard(librariesOfOtherPlayers.size() + 1); // così il player sa quanti giocatori ci sono e puo' refillare la board
            // poi devi anche mandare la nuova board refillata al server
        }
        if(state == DISCONNECTED){
            endTurn();
            return;
        }
        setState(ACTIVE);
        // esegui le operazioni del tuo turno
        endTurn();
    }
    private void endTurn(){
        state = NOT_ACTIVE;
        gameRMI.updatePlayersBoardAfterEndTurn(this, name); // In realtà qui dentro stai anche già mandando la library. Pensa a possibile ridondanza
        // manda al server la notifica che hai finito il turno
    }
    public State getState() {
        return state;
    }
    public void setState(State newState) {
        state = newState;
    }
    public void setSocket(Socket s){mySocket = s;}
    public void setObjective(PrivateObjective obj){objective = obj;}
    private void startRedrawThread(){return;} // funzione che start il thread che andrà ad aggiornare la GUI ogni x millisecondi.
    private void startUpdatePlayerFromRemoteThread(){return;}
    // Mentre non è il tuo turno (NOT_ACTIVE) devi chiedere al server ogni x ms la nuova versione per aggiornarla. Avremo un Thread dedicato a parte
}