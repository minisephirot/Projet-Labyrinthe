package modele;

import engine.GamePainter;
import engine.TextureFactory;
import modele.elements.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LabyrinthePainter implements GamePainter {

    /**
     * la taille des cases
     */
    private static final int WIDTH = 736;
    private static final int HEIGHT = 736;
    private static int camX,camY;
    private Font font;
    private Font font2;
    private Font font3;

    /**
     * Le modele du jeu Labyrinthe
     */
    private LabyrintheGame lg;

    /**
     * appelle constructeur parent
     *
     * @param game
     *            le jeutest a afficher
     */
    public LabyrinthePainter(LabyrintheGame game) {
        this.lg = game;
        this.font = new Font("Comic Sans MS", Font.BOLD, 20);
        this.font2 = new Font("Impact", Font.BOLD, 50);
        this.font3 = new Font("Comic Sans MS", Font.CENTER_BASELINE,14);
    }

    /**
     * methode  redefinie de Afficheur retourne une image du jeu
     */
    @Override
    public void draw(BufferedImage img) {
        camY = lg.getHeroY();
        camX = lg.getHeroX();
        Graphics2D crayon = (Graphics2D) img.getGraphics();
        crayon.setFont(this.font);
        Mur mur = lg.getMur();
        ArrayList<Sol> chemin = lg.getChemin();
        Arrive arrive = lg.getArrive();
        ArrayList<Case> caseSpeciales = lg.getCasesSpeciales();

        for (int i = 0; i < WIDTH*2; i+=32){
            for (int j = 0; j < HEIGHT*2; j+=32){
                int xCamera =i - camY + (32 / 2);
                int yCamera =j - camX + (32 / 2);
                crayon.drawImage(TextureFactory.getImgGrass(), null, xCamera, yCamera);
            }
        }
        // Dessiner le labyrinthe
        Rectangle rectangleArrive = arrive.getRectangleCamera(camY, camX, WIDTH, HEIGHT);
        crayon.drawImage(TextureFactory.getImgSol(), null, rectangleArrive.x, rectangleArrive.y);
        crayon.drawImage(arrive.getImg(), null, rectangleArrive.x, rectangleArrive.y);
        for (Brique b : mur) {
            // création du nouveau rectangle par rapport à la camera
            Rectangle r = b.getRectangleCamera(camY, camX, WIDTH, HEIGHT);
            crayon.drawImage(b.getImg(), null, r.x, r.y);
        }
        for (Sol s : chemin) {
            Rectangle r = s.getRectangleCamera(camY, camX, WIDTH, HEIGHT);
            crayon.drawImage(s.getImg(), null, r.x, r.y);
        }
        //Dessiner cases spéciales
        for (Case c : caseSpeciales) {
            if (c.isActive()) {
                Rectangle rectanglePiege = c.getRectangleCamera(camY, camX, WIDTH, HEIGHT);
                crayon.drawImage(c.getImg(), null, rectanglePiege.x, rectanglePiege.y);
            }
        }

        //Dessiner les Monstres
        for (Monstre m : this.lg.getMonstres()) {
            Rectangle rectanglemob = new Rectangle(m.y - camY + WIDTH / 2, m.x - camX + HEIGHT / 2, 20, 20);
            crayon.drawImage(m.getImgMonstre(), null, rectanglemob.x, rectanglemob.y);
        }
        // Dessiner le hero
        Rectangle rectangle1 = new Rectangle(lg.getHeroY() - camY + WIDTH / 2, lg.getHeroX() - camX + HEIGHT / 2, 20, 20);
        BufferedImage heroimg = lg.getHero().getImgHero();
        if (lg.getDammageProofHero() % 10 == 0){
            crayon.drawImage(heroimg, null, rectangle1.x + 2, rectangle1.y - 16);
        }

        //RECTANGLE INFO
        crayon.setColor(Color.WHITE);
        crayon.fillRect(10, 10, 145, 75);

        // Dessiner la condition de victoire et les étages:
        crayon.setColor(Color.black);
        crayon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        crayon.drawString("Etage n°" + lg.getFloor(), 20, 30);
        if (lg.isFinished() && lg.notInfinite()) {
            crayon.setColor(Color.gray);
            crayon.fillOval(this.getHeight() / 2 - this.getHeight() / 6, this.getWidth() / 2 - 65, 250, 100);
            crayon.setColor(Color.black);
            crayon.setFont(this.font2);
            crayon.drawString("Bravo !", this.getHeight() / 2 - this.getHeight() / 8, this.getWidth() / 2);
        }

        //STAMINA BAR
        int stamina = lg.getStamina();
        int stamina_percentage = (stamina * 100) / 200;
        int width_bar = (125 * stamina_percentage) / 200;
        Rectangle staminabar = new Rectangle(20, 50, width_bar * 2, 15);
        crayon.setColor(Color.green);
        crayon.fill(staminabar);
        Stroke oldstroke = crayon.getStroke();
        crayon.setStroke(new BasicStroke(2));
        crayon.setColor(Color.GRAY);
        crayon.drawRect(18, 48, 127, 17);
        crayon.setStroke(oldstroke);
        crayon.setColor(Color.black);
        crayon.setFont(font3);
        crayon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        crayon.drawString("Endurance", 40, 62);

        //Points de Vie BAR
        int pv = lg.getHero().getPv();
        int pv_percentage = (pv * 100) / lg.getHero().getPvMax();
        int pv_width_bar = (2 * pv_percentage) / lg.getHero().getPvMax();
        Rectangle pvbar = new Rectangle(19, 79, pv_width_bar * 2, 16);
        crayon.setColor(Color.RED);
        crayon.fill(pvbar);
        crayon.setStroke(new BasicStroke(2));
        crayon.setColor(Color.GRAY);
        crayon.drawRect(18, 78, 127, 17);
        crayon.setStroke(oldstroke);
        crayon.setColor(Color.black);
        crayon.setFont(font3);
        crayon.drawString("Points de vie", 30, 92);

        // Dessiner les vies
        for (int p = 0; p<pv; p++) {
            PointVie pvt = new PointVie(20+(p*32),100);
            crayon.drawImage(pvt.getImgPv(), null, pvt.getX(), pvt.getY());
        }
        for (int p = pv; p<lg.getHero().getPvMax(); p++) {
            PointVie pvt = new PointVie(20+(p*32),100);
            crayon.drawImage(pvt.getImgPvLost(), null, pvt.getX(), pvt.getY());
        }

        // Dessiner la fin du jeu (GAME OVER)
        if (lg.isOver()) {
            crayon.setColor(Color.gray);
            crayon.fillOval(this.getHeight() / 2 - this.getHeight() / 6, this.getWidth() / 2 - 65, 250, 100);
            crayon.setColor(Color.black);
            crayon.setFont(this.font2);
            crayon.drawString("Game Over !", this.getHeight() / 2 - this.getHeight() / 8, this.getWidth() / 2);
        }


    }


    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }
}
