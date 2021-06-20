package ba.unsa.pmf.candy.crush.logika;

/**
 * Klasa koja govori o tome sta se desilo sa nekom pozicijom u polju slatkisa
 */
public class Dogadjaj {
    TipDogadjaja tipDogadjaja;
    int x, y;

    /**
     * @param tipDogadjaja Sta se, ustvari, desilo
     * @param x U kojem redu se desilo
     * @param y U kojoj koloni se desilo
     */
    public Dogadjaj(TipDogadjaja tipDogadjaja, int x, int y) {
        this.tipDogadjaja = tipDogadjaja;
        this.x = x;
        this.y = y;
    }

    /**
     * @return
     */
    public TipDogadjaja getTipDogadjaja() {
        return tipDogadjaja;
    }

    /**
     * @param tipDogadjaja
     */
    public void setTipDogadjaja(TipDogadjaja tipDogadjaja) {
        this.tipDogadjaja = tipDogadjaja;
    }

    /**
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Metoda koja pomaze prilikom raspoznavanja dogadjaja
     * @return Tekstualna reprezentacija dogadjaja
     */
    @Override
    public String toString() {
        return "Dogadjaj{" +
                "tipDogadjaja=" + tipDogadjaja +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
