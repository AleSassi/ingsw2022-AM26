package it.polimi.ingsw.model;


import java.util.ArrayList;

public class PlayerTeam {
    private ArrayList<Player> playerlist = new ArrayList<>();
    private int lead;
    private int Cont;


    public void addplayer(Player Play){
        playerlist.add(Play);
        this.Cont++;
        if(Play.getAvaibletowercount()!=0){
            lead=Cont-1;
        }
    }

    public ArrayList<Player> getPlayerlist() {
        return playerlist;
    }

    public boolean containsPlayer(Player Play){
      Boolean bool;
      bool=playerlist.contains(Play);
      return bool;
    }

    public Player getLead() {
        return playerlist.get(lead);
    }
}



