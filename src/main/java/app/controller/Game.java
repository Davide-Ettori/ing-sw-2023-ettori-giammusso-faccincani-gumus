package app.controller;

import app.model.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static app.controller.MessageType.*;
import static app.controller.NameStatus.*;
import static app.view.UIMode.*;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * class which represent the instance of the current game
 * @author Ettori Faccincani
 * in theory it is mutable, but it is only instanced one time, at the start of the server
 */
public class Game extends UnicastRemoteObject implements Serializable, GameI {
    private final double standardTimer = 2.5;
    public static boolean showErrors = false;
    private final int targetPlayers;
    private int numPlayers;
    private int activePlayer = 0;
    private ArrayList<PlayerSend> players = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private final transient ArrayList<Socket> playersSocket = new ArrayList<>();
    private final transient ArrayList<ObjectOutputStream> outStreams = new ArrayList<>();
    private final transient ArrayList<ObjectInputStream> inStreams = new ArrayList<>();
    private final ArrayList<CommonObjective> bucketOfCO = Initializer.setBucketOfCO();
    private final ArrayList<PrivateObjective> bucketOfPO = Initializer.setBucketOfPO();
    private boolean endGameSituation = false;
    private boolean timeExp = true;
    private transient ArrayList<Thread> chatThreads = new ArrayList<>();
    private transient ServerSocket serverSocket; // Questa è l'unica socket del server. Potresti aver bisogno di passarla come argomento a Board
    private transient boolean closed = false;
    private final transient HashMap<String, PlayerI> rmiClients = new HashMap<>();
    private transient Game gameTemp = null;
    private final transient ArrayList<String> disconnectedPlayers = new ArrayList<>();
    private boolean advance = false;
    /**
     * normal constructor for this type of object, this class is also the main process on the server
     * @param maxP the number of players for this game, chosen before by the user
     * @param old contains yes/no, used to determine if the player wants to load and older game
     */
    public Game(int maxP, String old) throws RemoteException {
        super();

        LocateRegistry.createRegistry(Initializer.PORT_RMI).rebind("Server", this); // hosto il server sulla rete

        targetPlayers = maxP;
        if(old.equals("yes")){
            if(FILEHelper.havaCachedServer()) {// per prima cosa dovresti controllare che ci sia un server nella cache, nel caso lo carichi
                gameTemp = FILEHelper.loadServer();
                FILEHelper.writeFail();
                if(gameTemp.numPlayers != maxP) {
                    System.out.println("\nThe old game is not compatible, starting a new game...");
                    gameTemp = null;
                }
                else {
                    //clone(gameTemp);
                    System.out.println("\nLoading the old game...");
                    //System.exit(0); // continuare da qui in poi per fare la FA persistenza del server
                    // chiama la funzione che si occupa di riprendere la vecchia partita in corso
                }
            }// da qui in poi fai continuare il server che hai caricato dalla cache
            else
                System.out.println("\nThere is no game to load, starting a new game...");
        }
        FILEHelper.writeFail();
        shuffleObjBucket();
        numPlayers = 0;
        new Thread(() -> { // imposto un timer di un minuto per aspettare le connessioni dei client
            double minutes = 2;
            Game.waitForSeconds(60 * minutes);
            if(!timeExp)
                return;
            System.out.println("\nTime limit exceeded, not enough players connected");
            System.exit(0);
        }).start();

        try{serverSocket = new ServerSocket(Initializer.PORT);}
        catch(Exception e){connectionLost(e);}
        System.out.println("\nServer listening...");

        listenForPlayersConnections();

        if(gameTemp != null){
            if(gameTemp.names.containsAll(names)) {
                initializeOldClients();
                if(gameTemp.endGameSituation){
                    Game.waitForSeconds(standardTimer / 2.5);
                    sendFinalScoresToAll();
                }
            }
            else{
                System.out.println("\nThe names of the clients do not match the old ones, starting a new game...");
                initializeAllClients();
            }
            gameTemp = null;
        }
        else
            initializeAllClients();
        Game.waitForSeconds(standardTimer / 2.5);
        //System.out.println(names.get(0));
        for(int i = 0; i < numPlayers; i++){
            if(rmiClients.containsKey(names.get(i)))
                continue;
            try {
                playersSocket.get(i).setSoTimeout(Player.pingTimeout);
            } catch (SocketException e) {
                connectionLost(e);
            }
        }
        new Thread(this::pingRMI).start();
        new Thread(this::listenForReconnection).start();
        if(!rmiClients.containsKey(names.get(0)))
            waitMoveFromClient();
        else
            startChatServerThread();
    }
    /**
     * method that get an old client status by his name
     * @param n the name of the old client
     * @param playerList the list of all the old clients
     * @author Ettori
     * @return the client that was playing previously and needs to be alive again
     */
    private Player getClientByName(String n, ArrayList<PlayerSend> playerList){
        for(int i = 0; i < playerList.size(); i++){
            if(playerList.get(i).name.equals(n)) {
                try {
                    return new Player(playerList.get(i));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("\nAncient client not found...");
        System.exit(0);
        try {
            return new Player();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * helper method for initializing the old clients that were playing in the previous game
     * @author Ettori
     */
    private void initializeOldClients(){
        for(int i = 0; i < numPlayers; i++){
            try {
                gameTemp.players.get(i).activeName = gameTemp.names.get(0);
                outStreams.get(i).writeObject(getClientByName(names.get(i), gameTemp.players));
                players.add(new PlayerSend(getClientByName(names.get(i), gameTemp.players)));
            }catch (Exception e){connectionLost(e);}
        }
        int temp = names.indexOf(gameTemp.names.get(0));
        String n = names.get(0);
        names.set(0, names.get(temp));
        names.set(temp, n);

        ObjectOutputStream outTemp = outStreams.get(0);
        outStreams.set(0, outStreams.get(temp));
        outStreams.set(temp, outTemp);

        ObjectInputStream inTemp = inStreams.get(0);
        inStreams.set(0, inStreams.get(temp));
        inStreams.set(temp, inTemp);
    }
    /**
     * helper method for initializing all the clients (players) with the same board state
     * @author Ettori
     */
    private void initializeAllClients(){
        randomizeChairman();
        Player p;
        for(int i = 0; i < names.size(); i++){
            try {
                p = new Player();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            p.setName(names.get(i));
            p.setIsChairMan(i == 0);
            p.board = new Board(numPlayers, bucketOfCO.get(0), bucketOfCO.get(1));
            //p.board = new Board(numPlayers, new CommonObjective(new Algo_CO_13_FAKE(), 13), new CommonObjective(new Algo_CO_14_FAKE(), 14));
            p.board.name = names.get(i);
            if(i == 0)
                p.board.initBoard(numPlayers);
            else
                p.board = new Board(getChairman().board);
            p.library = new Library(names.get(i));
            p.setPrivateObjective(getPrivateObjective());
            p.pointsUntilNow = 0;
            p.activeName = getChairmanName();
            p.chairmanName = getChairmanName();
            for(int j = 0; j < numPlayers; j++) {
                if(!names.get(j).equals(names.get(i)))
                    p.librariesOfOtherPlayers.add(new Library(names.get(j)));
            }
            p.numPlayers = numPlayers;
            try {
                outStreams.get(i).writeObject(p);
            }catch (Exception e){connectionLost(e);}
            players.add(new PlayerSend(p));
        }
    }
    /**
     * choose a random chairman from all the players who connected to the game
     * @author Ettori
     */
    private void randomizeChairman(){
        int temp = new Random().nextInt(numPlayers);
        //temp = 1; // ELIMINA --> usata solo per il testing
        //temp = 0; // ELIMINA --> usata solo per il testing
        String n = names.get(0);
        names.set(0, names.get(temp));
        names.set(temp, n);

        ObjectOutputStream outTemp = outStreams.get(0);
        outStreams.set(0, outStreams.get(temp));
        outStreams.set(temp, outTemp);

        ObjectInputStream inTemp = inStreams.get(0);
        inStreams.set(0, inStreams.get(temp));
        inStreams.set(temp, inTemp);
    }
    /**
     * helper function which waits for client's connection to the server socket, when all are connected the game starts
     * @author Ettori
     */
    private void listenForPlayersConnections(){
        ArrayList<Thread> ths = new ArrayList<>();
        ObjectInputStream clientIn;
        ObjectOutputStream clientOut;
        Thread th;

        while(numPlayers < targetPlayers){
            try{
                playersSocket.add(serverSocket.accept());
                clientOut = new ObjectOutputStream(playersSocket.get(playersSocket.size() - 1).getOutputStream());
                clientIn = new ObjectInputStream(playersSocket.get(playersSocket.size() - 1).getInputStream());
                boolean isFake = (boolean) clientIn.readObject();
                if(isFake) {
                    playersSocket.remove(playersSocket.size() - 1);
                    continue;
                }
                ObjectInputStream finalClientIn = clientIn;
                ObjectOutputStream finalClientOut = clientOut;
                th = new Thread(() ->{
                    try{getUserName(finalClientIn, finalClientOut);}
                    catch(Exception e){System.out.println(e);}
                });
                th.start();
                ths.add(th);
                numPlayers++;
            }
            catch(Exception e){connectionLost(e);}
        }
        timeExp = false;
        for(Thread t: ths){
            try{t.join();}
            catch(Exception e){connectionLost(e);}
        }
        if(numPlayers < targetPlayers){
            System.out.println("\nPlayer number not sufficient");
            System.exit(0);
        }
        System.out.println("\nThe game started");
    }
    /**
     * method that listen for an old client to restart his previous game, in tha same old state
     * @param s the socket of the player
     * @param out the output stream of the player
     * @param in the input stream of the player
     * @author Ettori
     */
    synchronized private void tryToReconnectClient(Socket s, ObjectOutputStream out, ObjectInputStream in){
        try {
            String name = (String) in.readObject();
            if(disconnectedPlayers.contains(name)){
                rmiClients.remove(name);
                out.writeObject(FOUND);
                PlayerSend p = new PlayerSend(players.get(names.indexOf(name)));
                p.activeName = names.get(activePlayer);
                out.writeObject(new Player(p));
                inStreams.set(names.indexOf(name), in);
                outStreams.set(names.indexOf(name), out);
                playersSocket.set(names.indexOf(name), s);
                disconnectedPlayers.remove(name);
                new Thread(() ->{
                    Game.waitForSeconds(standardTimer / 2.5);
                    if(rmiClients.containsKey(name))
                        return;
                    try {
                        s.setSoTimeout(Player.pingTimeout);
                    } catch (SocketException e) {
                        connectionLost(e);
                    }
                    if(getActivePlayersNumber() >= 3) // se ci sono solo 2 player il turno cambierà quindi non devo ascoltare la chat
                        new ChatBroadcast(this, names.indexOf(name)).start();
                }).start();
                if(getActivePlayersNumber() == 2){
                    if(Client.uiModeCur == GUI)
                        showMessageDialog(null, "The game is now resuming and the next turn is starting...");
                    else
                        System.out.println("\nThe game is now resuming and the next turn is starting...");
                    if(advance){
                        advance = false;
                        advanceTurn();
                    }
                }
            }
            else
                out.writeObject(NOT_FOUND);
        }catch (Exception e){
            try {
                s.close();
                out.close();
                in.close();
            } catch (IOException ex) {
                return;
            }
        }
    }
    /**
     * method that wait permanently for a new client to connect to the existing game
     * @author Ettori
     */
    private void listenForReconnection(){
        Socket s = null;
        while(true){
            try {
                s = serverSocket.accept();
            } catch (IOException e) {
                connectionLost(e);
            }
            Socket finalS = s;
            new Thread(() -> {
                ObjectOutputStream out = null;
                ObjectInputStream in = null;
                try {
                    out = new ObjectOutputStream(finalS.getOutputStream());
                    in = new ObjectInputStream(finalS.getInputStream());
                } catch (IOException ignored) {}
                tryToReconnectClient(finalS, out, in);
            }).start();
        }
    }
    /**
     * Check if the name that the client choose is already TAKEN
     * @param in the input stream of the socket
     * @param out the output stream of the socket
     * @author Ettori
     */
    synchronized private void getUserName(ObjectInputStream in, ObjectOutputStream out){
        try {
            outStreams.add(out);
            inStreams.add(in);
            while (true) {
                String name = (String) inStreams.get(inStreams.size() - 1).readObject();
                if (isNameTaken(name)) {
                    outStreams.get(outStreams.size() - 1).writeObject(TAKEN);
                    continue;
                }
                if(gameTemp != null && gameTemp.names.contains(name))
                    outStreams.get(outStreams.size() - 1).writeObject(OLD);
                else
                    outStreams.get(outStreams.size() - 1).writeObject(NOT_TAKEN);
                names.add(name);
                break;
            }
        }catch(Exception e){
            try {
                playersSocket.get(playersSocket.size() - 1).close();
                outStreams.get(outStreams.size() - 1).close();
                inStreams.get(inStreams.size() - 1).close();
            } catch (IOException ignored) {}
            outStreams.remove(outStreams.size() - 1);
            inStreams.remove(inStreams.size() - 1);
            playersSocket.remove(playersSocket.size() - 1);
        }
    }
    /**
     * Make a clone of the server, needed for the persistence functionality
     * @param g server status
     * @author Ettori
     */
    private void clone(Game g){
        endGameSituation = g.endGameSituation;
        players = g.players;
        names = g.names;
    }
    /**
     * Make a random choose of the objective (Common and Private)
     * @author Ettori
     */
    private void shuffleObjBucket(){
        Random rand = new Random();
        CommonObjective temp_1;
        PrivateObjective temp_2;
        int j;
        for(int i = 0; i < bucketOfCO.size(); i++){
            j = rand.nextInt(bucketOfCO.size());
            temp_1 = bucketOfCO.get(i);
            bucketOfCO.set(i, bucketOfCO.get(j));
            bucketOfCO.set(j, temp_1);
        }
        for(int i = 0; i < bucketOfPO.size(); i++){
            j = rand.nextInt(bucketOfPO.size());
            temp_2 = bucketOfPO.get(i);
            bucketOfPO.set(i, bucketOfPO.get(j));
            bucketOfPO.set(j, temp_2);
        }
    }
    /**
     * Wait the move of the client that are playing and set the chat,
     * when the client made the move and send it to server update Board and Library
     * @author Ettori Faccincani
     */
    private void waitMoveFromClient(){
        //System.out.println("STARTO I CHAT THREAD, dalla funzione");
        startChatServerThread();
        while(true){
            Message msg = null;
            try {
                msg = (Message) inStreams.get(activePlayer).readObject();
            } catch (IOException | ClassNotFoundException e) {
                playerDisconnected(activePlayer);
                return;
            }
            try {
                if(msg.getType() == PING)
                    continue;
                if(msg.getType() == CHAT){
                    sendChatToClients(names.get(activePlayer), msg.getAuthor(), (String)msg.getContent());
                    continue;
                }
                for (int i = 0; i < numPlayers; i++) { // broadcast a tutti tranne a chi ha mandato il messaggio
                    if (i != activePlayer)
                        sendToClient(i,msg);
                }
                if(msg.getType() == UPDATE_GAME) {
                    //System.out.println(activePlayer + " - " + names.get(activePlayer));
                    if(!rmiClients.containsKey(names.get(activePlayer)))
                        sendToClient(activePlayer, new Message(STOP, null, null));
                    //Game.waitForSeconds(1);
                    //chatThreads = new ArrayList<>();
                    break;
                }
            }catch(Exception e){connectionLost(e);}
        }
        waitForEndTurn();
    }
    /**
     * Wait the end of the turn of the client and check if the library is full
     * @author Ettori Faccincani
     */
    private void waitForEndTurn(){
        Message msg = null;
        try {
            msg = (Message) inStreams.get(activePlayer).readObject();
        } catch (IOException | ClassNotFoundException e) {
            playerDisconnected(activePlayer);
            return;
        }
        try {
            //System.out.println("aspetto la FINE");
            if(msg.getType() == PING) {
                waitForEndTurn();
                return;
            }
            if(msg.getType() != END_TURN)
                throw new RuntimeException();
            //System.out.println("ecco la fine - socket");
            JSONObject jsonObject = (JSONObject) msg.getContent();
            players.set(activePlayer, (PlayerSend) jsonObject.get("player"));
            PlayerSend p = (PlayerSend) jsonObject.get("player");
            for(int i = 0; i < numPlayers; i++){
                if(i == activePlayer)
                    continue;
                for(int j = 0; j < numPlayers - 1; j++){
                    if(players.get(i).librariesOfOtherPlayers.get(j).name.equals(names.get(activePlayer))) {
                        players.get(i).librariesOfOtherPlayers.set(j, p.library);
                        players.get(i).board = p.board;
                        //players.get(i).fullChat = p.fullChat;
                    }
                }
            }
            if(players.get(activePlayer).library.isFull() && !endGameSituation) { // se la library ricevuta è piena entro nella fase finale del gioco
                endGameSituation = true;
                for(int i = 0; i < names.size(); i++){
                    if(i != activePlayer)
                        sendToClient(i, new Message(LIB_FULL, names.get(activePlayer), null));
                }
            }
            advanceTurn();
        }catch(Exception e){connectionLost(e);}
    }
    /**
     * Set the status of the players for the next turn and assign activePlayer to who will play this turn
     * @author Ettori Faccincani
     */
    public void advanceTurn(){
        /*
        System.out.print("Player disconnessi: ");
        for (String n: disconnectedPlayers)
            System.out.println(n);
         */
        if(getActivePlayersNumber() == 1 && disconnectedPlayers.size() > 0){
            if(Client.uiModeCur == GUI)
                showMessageDialog(null, "The game is temporarily paused because you are the only connected player");
            else
                System.out.println("\nThe game is temporarily paused because you are the only connected player");
            advance = true;
            return;
        }
        do{
            activePlayer = (activePlayer + 1) % numPlayers;
        }
        while(disconnectedPlayers.contains(names.get(activePlayer)));
        if(activePlayer == 0 && endGameSituation) {
            System.out.println("\nThe game is ending...");
            sendFinalScoresToAll();
        }
        //System.out.println(names.get(activePlayer));
        notifyNewTurn();
    }
    /**
     * Send the message to the client that a new turn start (two cases, if is the turn of the client or is the turn of another client)
     * @author Ettori Faccincani
     */
    private void notifyNewTurn(){
        //System.out.println("notify - " + numPlayers);
        for(int i = 0; i < numPlayers; i++){
            //System.out.println("\nnotify nome: " + names.get(i));
            try {
                if (i != activePlayer) {
                    //System.out.println(names.get(i));
                    sendToClient(i, new Message(CHANGE_TURN, "server", names.get(activePlayer)));
                }
            }catch (Exception e){connectionLost(e);}
        }
        new Thread(() -> {
            Game.waitForSeconds(standardTimer / 2.5);
            sendToClient(activePlayer, new Message(YOUR_TURN, "server", ""));
        }).start();
        FILEHelper.writeServer(this); // salvo lo stato della partita
        //System.out.println("PRIMA -" + names.get(activePlayer));
        if(!rmiClients.containsKey(names.get(activePlayer)))
            waitMoveFromClient();
        else {
            //System.out.println("STARTO I CHAT THREAD");
            startChatServerThread();
        }
    }
    /**
     * start all the threads that listen for chat messages from the clients (and sends the messages back to the players)
     * @author Ettori
     */
    private void startChatServerThread(){
        //if(chatThreads.size() != 0) // se non ci sono, inizializzo i thread che leggono un eventuale chat message dai client NON_ACTIVE (quello active non ne ha bisogno)
        //    return;
        chatThreads = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++){
            if(i == activePlayer || rmiClients.containsKey(names.get(i)) || disconnectedPlayers.contains(names.get(i)))
                continue;
            chatThreads.add(new ChatBroadcast(this, i));
            chatThreads.get(chatThreads.size() - 1).start();
        }
    }
    /**
     * Send message in the chat to other client
     * @param from who send the message
     * @param to who receive the message
     * @param msg text inside the message
     * @author Ettori
     */
    public void sendChatToClients(String from, String to, String msg){
        try {
            if (to.equals("all")) {
                for (int i = 0; i < numPlayers; i++) {
                    if (!names.get(i).equals(from))
                        sendToClient(i, new Message(CHAT, "", msg));
                }
            }
            else if(getNameIndex(to) != -1){
                sendToClient(getNameIndex(to) ,new Message(CHAT, "", msg));
            }
        }catch (Exception e){connectionLost(e);}
    }
    /**
     * find and return the name of the chairman of this game
     * @return the name of the chairman (String)
     */
    private String getChairmanName(){return names.get(0);}
    /**
     * find and return the chairman Player
     * @return the chairman Object (Player)
     */
    private PlayerSend getChairman(){return players.get(0);}

    /**
     * check if the name is already taken by other players
     * @param name the name to check
     * @return true iff the name is already taken
     */
    private boolean isNameTaken(String name){return names.contains(name);}

    /**
     * get the index of a certain player, by the name
     * @param name the name of the player
     * @return the index of the player having that name, -1 if not found
     */
    private int getNameIndex(String name){
        for(int i = 0; i < names.size(); i++){
            if(names.get(i).equals(name))
                return i;
        }
        return -1;
    }
    /**
     * choose the private objective, one for every player
     * @return the chosen private objective
     */
    private PrivateObjective getPrivateObjective(){
        PrivateObjective res = bucketOfPO.get(0);
        bucketOfPO.remove(0);
        return res;
    }
    /**
     * Count the points at the end of the game (not private or common objective)
     * and sum to the points made until now
     * @return the order of the player each one with his score
     * @author Ettori
     */
    private String getFinalScore(){
        ArrayList<Integer> scores = new ArrayList<>();
        StringBuilder res = new StringBuilder();
        PlayerSend p;
        for(int i = 0; i < numPlayers; i++){
            p = players.get(i);
            scores.add(p.pointsUntilNow + p.library.countGroupedPoints() + p.getPrivateObjective().countPoints(p.library.gameLibrary));
        }
        names.sort((a, b) -> {
            int n, m;
            n = scores.get(names.indexOf(a));
            m = scores.get(names.indexOf(b));
            if (n == m)
                return 0;
            if (n > m)
                return 1;
            return -1;
        });
        scores.sort(null);
        Collections.reverse(names);
        Collections.reverse(scores);
        for(int i = 0; i < numPlayers; i++)
            res.append("Place number ").append(i + 1).append(": ").append(names.get(i)).append(" with ").append(scores.get(i)).append(" points\n");
        return res.toString();
    }
    /**
     * Send the final score to all the clients
     * @author Ettori
     */
    private void sendFinalScoresToAll(){
        String finalScores = getFinalScore();
        ArrayList<Thread> ths = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++) {
            try {
                int finalI = i;
                ths.add(new Thread(() -> {
                    Game.waitForSeconds(standardTimer / 2.5);
                    sendToClient(finalI, new Message(FINAL_SCORE, "server", finalScores));
                }));
                ths.get(i).start();
            } catch (Exception e) {connectionLost(e);}
        }
        for(int i = 0; i < numPlayers; i++) {
            try {
                ths.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        FILEHelper.writeSucc(); // server uscito con successo, non hai messo niente nella cache
        while (true){}
    }
    /**
     * getter for the input streams from the server to all the clients
     * @return the ArrayList containing all the input streams
     */
    public ArrayList<ObjectInputStream> getInStreams(){return inStreams;}
    /**
     * getter for the list of names of the players active in this game
     * @return the ArrayList containing all the names of the connected players
     */
    public ArrayList<String> getNames(){return names;}
    /**
     * shortcut for the Thread.sleep(int) function, it accepts SECONDS, NOT MILLISECONDS
     * @param n the (decimal) number of seconds to wait
     */
    public static void waitForSeconds(double n){
        try {
            Thread.sleep((long) (n * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * function that handle the eventual disconnection
     * @param e the exception to throw
     * @author Ettori
     */
    public void connectionLost(Exception e){
        if(closed)
            return;
        if(Game.showErrors)
            throw new RuntimeException(e);
        else{
            closed = true;
            System.out.println("\nConnection lost, the server is closing...");
            try {
                serverSocket.close();
                for(Socket s: playersSocket)
                    s.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            while (true){}
        }
    }
    /**
     * method which acknowledge that one of the client disconnected and set the game to continue without the lost client
     * @param i the index of the lost client
     * @author Ettori
     */
    public void playerDisconnected(int i) {
        if (disconnectedPlayers.contains(names.get(i)))
            return;
        //System.out.println("\n" + names.get(i) + " disconnected from the game\n");
        try {
            playersSocket.get(i).setSoTimeout(0);
        } catch (SocketException e) {
            System.out.println("Errore della socket");
        }
        disconnectedPlayers.add(names.get(i));
        rmiClients.remove(names.get(i));
        if (getActivePlayersNumber() == 1)
            new Thread(this::disconnectedTimer).start();
        if (i == activePlayer) {
            // manda evento di player disconnesso
            for (int j = 0; j < numPlayers; j++)
                sendToClient(j, new Message(DISCONNECTED, names.get(activePlayer), null));
            advanceTurn();
        }
    }
    /**
     * method that checks if one player has been alone for more than 1 minute, in that case that player is declared winner and the game end
     * @author Ettori
     */
    private void disconnectedTimer(){
        double minutes = 1.5;
        Game.waitForSeconds(60 * minutes);
        if(getActivePlayersNumber() == 1){
            if(Client.uiModeCur == GUI)
                showMessageDialog(null, "You have won because all the other players have disconnected");
            else
                System.out.println("\nYou have won because all the other players have disconnected");
            System.exit(0);
        }
    }

    /**
     * method that find the number of players which are currently connected to the game
     * @author Ettori
     * @return the number of connected players
     */
    private int getActivePlayersNumber(){return numPlayers - disconnectedPlayers.size();}
    /**
     * general method to respond to a client, it chooses the right network connection of the player
     * @author Ettori
     * @param i the index of the player to contact
     * @param msg the message that must be sent
     */
    public void sendToClient(int i, Message msg){
        //System.out.println(names.get(i) + " - " + msg.getType() + " - " + msg.getAuthor());
        if(disconnectedPlayers.contains(names.get(i)))
            return;
        if(!rmiClients.containsKey(names.get(i))){
            try {
                outStreams.get(i).writeObject(msg);
            } catch (IOException e) {
                //playerDisconnected(i);
                return;
            }
        }
        else{
            try {
                rmiClients.get(names.get(i)).receivedEventRMI(msg);
            } catch (RemoteException e) {
                //playerDisconnected(i);
                return;
            }
        }
    }
    /******************************************** RMI ***************************************************************/
    /**
     * method called from remote used to add a client to the store of all the RMI clients
     * @author Ettori
     * @param name the nickname of the player
     * @param p the player object, passed as the remote interface
     */
    public void addClient(String name, PlayerI p){
        //System.out.println("Aggiungo: " + name);
        rmiClients.put(name, p);
    }

    /**
     * method called from remote which is equivalent to the waitMoveFromClient() method for the socket
     * @author Ettori
     * @param msg the message that the client want to send to the remote server
     */
    public void redirectToClientRMI(Message msg){
        switch (msg.getType()){
            case CHAT -> {
                String from = (String)msg.getContent();
                from = from.substring(0, from.indexOf(" "));
                sendChatToClients(from, msg.getAuthor(), (String)msg.getContent());
            }
            case END_TURN -> {
                JSONObject jsonObject = (JSONObject) msg.getContent();
                players.set(activePlayer, (PlayerSend) jsonObject.get("player"));
                PlayerSend p = (PlayerSend) jsonObject.get("player");
                for(int i = 0; i < numPlayers; i++){
                    if(i == activePlayer)
                        continue;
                    for(int j = 0; j < numPlayers - 1; j++){
                        if(players.get(i).librariesOfOtherPlayers.get(j).name.equals(names.get(activePlayer))) {
                            players.get(i).librariesOfOtherPlayers.set(j, p.library);
                            players.get(i).board = p.board;
                            //players.get(i).fullChat = p.fullChat;
                        }
                    }
                }
                if(players.get(activePlayer).library.isFull() && !endGameSituation) { // se la library ricevuta è piena entro nella fase finale del gioco
                    endGameSituation = true;
                    for(int i = 0; i < names.size(); i++){
                        if(i != activePlayer)
                            sendToClient(i, new Message(LIB_FULL, names.get(activePlayer), null));
                    }
                }
                advanceTurn();
            }
            case UPDATE_GAME -> {
                for (int i = 0; i < numPlayers; i++) { // broadcast a tutti tranne a chi ha mandato il messaggio
                    if (i != activePlayer)
                        sendToClient(i,msg);
                }
                sendToClient(activePlayer, new Message(STOP, null, null));
                //chatThreads = new ArrayList<>();
            }
            default -> {
                for (int i = 0; i < numPlayers; i++) { // broadcast a tutti tranne a chi ha mandato il messaggio
                    if (i != activePlayer)
                        sendToClient(i,msg);
                }
            }
        }
    }
    /**
     * method that allow the server to be pinged from an RMI client
     * @author Ettori
     */
    public void ping(){}

    /**
     * method that periodically pings all the current client connected with RMI
     * @author Ettori
     */
    public void pingRMI(){
        while(true){
            waitForSeconds(standardTimer * 2);
            for(String n: names){
                if(!rmiClients.containsKey(n) || disconnectedPlayers.contains(n))
                    continue;
                try {
                    rmiClients.get(n).pingClient();
                } catch (RemoteException e) {
                    //System.out.println("presoo");
                    playerDisconnected(names.indexOf(n));
                }
            }
        }
    }
}