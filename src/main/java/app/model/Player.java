package app.model;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * class which represent the parent of PlayerGUI and PlayerTUI. Immutable
 * @author Ettori Giammusso
 */
public abstract class Player implements Serializable {

    //Questi che seguono sono gli attributi necessari per i metodi che ho spostato da PlayerTUI a qui
    protected String name;
    protected PrivateObjective objective;
    /** list of the libraries of all the players in the game */
    public ArrayList<Library> librariesOfOtherPlayers = new ArrayList<>();
    /** the board seen and used by this player */
    public Board board;
    /** the personal library of this player */
    public Library library;
    protected State state;
    protected boolean isChairMan;
    protected transient ObjectInputStream inStream;
    protected String fullChat = "";

    //questi che seguono sono gli attributi condivisi da playerTUI e PlayerGUI
    //...da finire...

    /**
     * Check if the input by the user is correct
     * @param s array of the coordinates
     * @return true if the input is correct
     * @author Faccincani
     */
    protected boolean checkRawCoords(String[] s) {
        if (s.length % 2 == 1)
            return false;
        else {
            for (int i = 0; i < s.length; i++) {
                if (Integer.parseInt(s[i]) < 0 || Integer.parseInt(s[i]) > 9)
                    return false;
            }
        }
        return true;
    }
    /**
     * check if the name of a player exists in the game (used by the chat)
     * @author Ettori
     * @param name the name to check in this game
     * @return true iff that player actually exists in the current game
     */
    protected boolean doesPlayerExists(String name){
        for(Library lib : librariesOfOtherPlayers){
            if(lib.name.equals(name))
                return true;
        }
        return false;
    }
    /**
     * getter for the name
     * @author Ettori
     * @return the name of the player
     */
    public String getName() {
        return name;
    }
    /**
     * Getter for the private objective
     * @author Ettori
     * @return the private objective of the player
     */
    public PrivateObjective getPrivateObjective(){return objective;}
    /**
     * setter for the PO
     * @author Ettori
     * @param obj  the PO that needs to be set
     */
    public void setPrivateObjective(PrivateObjective obj) {objective = obj;}
    /**
     * take the cards from the board and transfer them in the player library
     * @author Ettori
     * @param coord the list of coupled coordinates of the cards that the player want to take from the board
     */
    protected void pickCards(ArrayList<Integer> coord, int col) { // Coordinate accoppiate. Questo metodo verrà chiamato quando la GUI o la CLI rilevano una scelta dall'utente
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < coord.size(); i += 2) {
            cards.add(new Card(board.getGameBoard()[coord.get(i)][coord.get(i + 1)]));
            board.getGameBoard()[coord.get(i)][coord.get(i + 1)] = new Card(); // dopo che hai preso una carta, tale posto diventa EMPTY
        }

        deployCards(col, cards);
    }
    /**
     * physically position the cards in the chosen column
     * @author Ettori
     * @param col column
     * @param cards list of the chosen cards
     */
    private void deployCards(int col, ArrayList<Card> cards) {
        library.insertCards(col, cards);
    }

    /**
     * find the current state of the player (ACTIVE, NOT_ACTIVE, DISCONNECTED)
     @author Ettori
      * @return the state of the player (enum value)
     */
    public State getState(){return state;}
    /**
     * set the current state of the player
     @author Ettori
      * @param s the state that must be set
     */
    public void setState(State s){state = s;}
    /**
     * setter for the attribute name
     @author Ettori
      * @param n the name to set
     */
    public void setName(String n){name = n;}
    /**
     * setter for the attribute isChairMan
     @author Ettori
      * @param b the boolean to set
     */
    public void setIsChairMan(boolean b){isChairMan = b;}
    /**
     * getter for the socket input stream (from the server)
     @author Ettori
      * @return the input stream of this player
     */
    public ObjectInputStream getInStream(){return inStream;}
    /**
     * add a string (chat message) to the full chat of the game
     @author Ettori
      * @param s the message received, it will be added to the fullChat attribute
     */
    public void addToFullChat(String s){fullChat += s;}

}
