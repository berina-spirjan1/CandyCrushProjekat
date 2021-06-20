package ba.unsa.pmf.candy.crush.logika;

import java.util.ArrayList;

/**
 * Element koji se koristi u polju igre
 */
public class Slatkis {
    private Tip tip;
    private Boja boja;

    public Slatkis(Tip tip, Boja boja) {
        this.tip = tip;
        this.boja = boja;
    }

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public Boja getBoja() {
        return boja;
    }

    public void setBoja(Boja boja) {
        this.boja = boja;
    }

    /**
     * Ova metoda vrsi provjeravanje okoline slatkisa i brise one slatkise koji formiraju lanac.
     * @param polja matrica polja slatkisa trenutne igre
     * @param red Red slatkisa koji se treba obraditi
     * @param kolona Kolona slatkisa koji se treba obraditi
     * @return dogadjaji koji su se desili nakon obrade polja
     */
    public ArrayList<Dogadjaj> obradi(Slatkis[][] polja, int red, int kolona) {
        ArrayList<Dogadjaj> dogadjaji = new ArrayList<>();
        int brojSlatkisa = 1;
        int topIndex = red, bottomIndex = red;
        for (int i = red + 1; i < CandyModel.VISINA; i++) {
            if (isMatching(polja[i][kolona], polja[red][kolona])) {
                brojSlatkisa++;
                bottomIndex = i;
            } else {
                break;
            }
        }
        for (int i = red - 1; i >= 0; i--) {
            if (isMatching(polja[i][kolona], polja[red][kolona])) {
                brojSlatkisa++;
                topIndex=i;
            } else {
                break;
            }
        }
        int brojSlatkisaURedu = 1, rightIndex = kolona, leftIndex = kolona;
        for (int i = kolona + 1; i < CandyModel.SIRINA; i++) {
            if (isMatching(polja[red][i],polja[red][kolona])) {
                brojSlatkisaURedu++;
                rightIndex = i;
            } else {
                break;
            }
        }
        for (int i = kolona - 1; i >= 0; i--) {
            if (isMatching(polja[red][i],polja[red][kolona])) {
                brojSlatkisaURedu++;
                leftIndex=i;
            } else {
                break;
            }
        }
        if (brojSlatkisa > 2) {
            for (int i = topIndex; i <= bottomIndex; i++) {
                polja[i][kolona] = null;
                dogadjaji.add(new Dogadjaj(TipDogadjaja.UNISTENO, i, kolona));
            }
        } else if (brojSlatkisaURedu > 2) {
            for (int i = leftIndex; i <= rightIndex; i++) {
                polja[red][i] = null;
                dogadjaji.add(new Dogadjaj(TipDogadjaja.UNISTENO, red, i));
            }
        }

        return dogadjaji;
    }

    /**
     * @param slatkis1
     * @param slatkis
     * @return Jesu li isti po boji i tipu, a da nisu null
     */
    private boolean isMatching(Slatkis slatkis1, Slatkis slatkis) {
        return slatkis1 != null && slatkis != null && slatkis1.getTip() == slatkis.getTip() &&
                slatkis1.getBoja() == slatkis.getBoja();
    }

    @Override
    public String toString() {
        return boja.ordinal() + "";
    }
}
