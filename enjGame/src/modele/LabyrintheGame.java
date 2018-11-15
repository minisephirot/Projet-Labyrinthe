package modele;

import engine.Commande;
import engine.Game;
import engine.TextureFactory;
import modele.elements.Arrive;
import modele.elements.Case;
import modele.elements.Mur;
import modele.elements.Sol;
import modele.elements.*;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LabyrintheGame implements Game {

    private Niveau level;
    private boolean gameWin;
    private int floor;
    private final static int HAUT = 0;
    private final static int BAS = 1;
    private final static int GAUCHE = 2;
    private final static int DROITE = 3;

    public LabyrintheGame(){
        this.gameWin = false;
        this.level = new Niveau();
        genLabyrinth(false);
    }

    public void genLabyrinth(boolean useGenerator){
        TextureFactory.genererCombinaison();
       if (useGenerator){
            this.level.genererNiveau();
        }else{
            this.level.chargerNiveau("Labyrinthe1");
        }
    }

    @Override
    public String toString(){
        return this.level.toString();
    }

    @Override
    public void evolve(Commande cmd, boolean[] tab) {
    public void evolve(Commande cmd) {
        if (teleport1() && getTp1().getActive()){
            this.level.setPlayerX(getTp1().getVoisinX());
            this.level.setPlayerY(getTp1().getVoisinY());
            getTp1().setActive(false);
            getTp2().setActive(false);
            System.out.println(getTp1().getActive());
        }else if (teleport2() && getTp2().getActive()){
            this.level.setPlayerX(getTp2().getVoisinX());
            this.level.setPlayerY(getTp2().getVoisinY());
            getTp1().setActive(false);
            getTp2().setActive(false);
        }
        if (gameWon()){
            this.gameWin = true;
            this.incrementFloor();
            new Timer(5000, e -> resetGame()).start();
            this.genLabyrinth(true);
        }
        int sprint = (tab[4] == true)?2:1;
        this.level.sprintHandler(tab[4]);
        if (!this.level.heroSprint())
            sprint = 1;
        if (gestionCollision(getMur(), cmd, tab)) {
            if (cmd == Commande.UP || tab[0]) this.level.deplacerHero(-1*sprint, 0);
            else if (cmd == Commande.DOWN || tab[1]) this.level.deplacerHero(1*sprint, 0);
            else if (cmd == Commande.LEFT || tab[2]) this.level.deplacerHero(0, -1*sprint);
            else if (cmd == Commande.RIGHT || tab[3]) this.level.deplacerHero(0, 1*sprint);
        }
    }

    private boolean teleport1(){
        int herox = getHeroX();
        int heroy = getHeroY();

        Teleporteur tp1 = this.getTp1();
        Rectangle hero = new Rectangle(heroy, herox, 20, 20);
        return tp1.getRectangle().intersects(hero);
    }

    private boolean teleport2(){
        int herox = getHeroX();
        int heroy = getHeroY();

        Teleporteur tp2 = this.getTp2();
        Rectangle hero = new Rectangle(heroy, herox, 20, 20);
        return tp2.getRectangle().intersects(hero);
    }

    private boolean gameWon() {
        int herox = getHeroX();
        int heroy = getHeroY();
        Arrive arrive = this.getArrive();
        Rectangle hero = new Rectangle(heroy, herox, 20, 20);
        return arrive.getRectangle().intersects(hero);
    }

    public boolean gestionCollision(Mur mur, Commande cmd, boolean[] tab){
        int herox = getHeroX();
        int heroy = getHeroY();
        int sprint = (tab[4] == true)?2:1;
        boolean avancer = true;
        if (cmd == Commande.UP || tab[0]) {
            herox -= 1*sprint;
            changerDirection(HAUT);
        } else if (cmd == Commande.DOWN || tab[1]) {
            herox += 1*sprint;
            changerDirection(BAS);
        }else if (cmd == Commande.LEFT || tab[2]) {
            heroy -= 1*sprint;
            changerDirection(GAUCHE);
        }else if (cmd == Commande.RIGHT || tab[3]) {
            heroy += 1*sprint;
            changerDirection(DROITE);
        }
        Rectangle hero = new Rectangle(heroy+2, herox, 16, 20);
        for (Case c : mur){
            if (c.getRectangle().intersects(hero))
                avancer = false;
        }
        return avancer;
    }

    public int[][] getLabyrinthe(){
        return level.getLabyrinthe();
    }

    public Hero getHero(){ return level.getHero();}
    public Mur getMur(){
        return level.getMur();
    }

    public void changerDirection(int dir){
        level.changerDirection(dir);
    }

    public ArrayList<Sol> getChemin(){return level.getChemin();}

    public Arrive getArrive(){
        return this.level.getArrive();
    }

    public Teleporteur getTp1(){
        return this.level.getTp1();
    }

    public Teleporteur getTp2(){
        return this.level.getTp2();
    }


    public int getHeroX(){
        return level.getPlayerX();
    }

    public int getHeroY(){
        return level.getPlayerY();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public boolean getGameWin() {
        return this.gameWin;
    }

    public void resetGame(){
        this.gameWin = false;
    }

    public String getFloor() {
        return this.floor+"";
    }

    public void incrementFloor(){
        this.floor++;
    }

    public int getStamina(){
        return this.level.getStamina();
    }
}
