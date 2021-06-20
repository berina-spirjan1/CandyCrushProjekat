package ba.unsa.pmf.candy.crush.logika;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Klasa u kojoj cuvamo logiku igre i podatke o trenutnoj igri
 */
public class CandyModel {
    private final Slatkis[][] polja;
    static final int SIRINA = 8;
    static final int VISINA = SIRINA;
    private int poeni;
    private int preostaloPoteza;
    private final Timer timer;
    private int preostaloVremena;

    public void prekiniIgru() {
        timer.cancel();
    }

    public int getPreostaloVremena() {
        return preostaloVremena;
    }

    public int getBrojPreostalihPoteza() {
        return preostaloPoteza;
    }

    /**
     * @return visina polja
     */
    public static int getVISINA() {
        return VISINA;
    }

    /**
     * @return broj poena u trenutnoj igri
     */
    public int getPoeni() {
        return poeni;
    }

    /**
     * Kreira model sa nasumicno popunjenim poljima i poenima postavljenim na 0
     */
    public CandyModel(int preostaloPoteza, int brojSekundi , Consumer<Integer> promjenaSata, Supplier<Integer> krajSata) {
        polja = new Slatkis[SIRINA][SIRINA];
        popuniPolja(polja);
        poeni = 0;
        this.preostaloPoteza = preostaloPoteza;
        timer = new Timer();
        preostaloVremena = brojSekundi;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (preostaloVremena > 0 && preostaloPoteza > 0) {
                    promjenaSata.accept(preostaloVremena--);
                } else {
                    krajSata.get();
                    timer.cancel();
                }
            }
        }, 0, 1000L);
    }

    /**
     * Metoda vrsi popunjavanje polja nasumicno,
     * a nakon toga izbaci nevalidne slatkise
     * @param polja Matrica svih slatkisa koji se nalaze u igri
     */
    private void popuniPolja(Slatkis[][] polja) {
        for (int i = 0; i < VISINA; i++) {
            for (int j = 0; j < SIRINA; j++) {
                polja[i][j] = dajRandomSlatkis();
            }
        }
        Boja boja = polja[0][0].getBoja();
        int broj = 0;
        for (int i = 0; i < VISINA; i++) {
            for (int j = 0; j < SIRINA; j++) {
                if (polja[i][j].getBoja() == boja) {
                    broj++;
                    if (broj == 3) {
                        polja[i][j].setBoja(Boja.values()[(boja.ordinal()+1)%Boja.values().length]);
                        broj = 0;
                    }
                } else {
                    boja = polja[i][j].getBoja();
                    broj = 1;
                }
            }
        }
        for (int j = 0; j < VISINA; j++) {
            for (int i = 0; i < SIRINA; i++) {
                if (polja[i][j].getBoja() == boja) {
                    broj++;
                    if (broj == 3) {
                        polja[i][j].setBoja(Boja.values()[(boja.ordinal()+1)%Boja.values().length]);
                        broj = 0;
                    }
                } else {
                    boja = polja[i][j].getBoja();
                    broj = 1;
                }
            }
        }
    }

    /**
     * Metoda vrsi obradu polja nakon sto se zamijene dva slatkisa sa koordinatama poslanim u parametrima.
     * Nakon obrade, azurira se broj poena, ili se vrate slatkisi na prvobitna mjesta. Ovo zavisi od toga da li je
     * potez bio validan ili ne.
     * @param stariRed Red prvog slatkisa
     * @param staraKolona Kolona prvog slatkisa
     * @param noviRed Red drugog slatkisa
     * @param novaKolona Kolona drugog slatkisa
     * @return lista dogadjaja koji su se desili na polju slatkisa
     */
    public ArrayList<Dogadjaj> odigraj(int stariRed, int staraKolona, int noviRed, int novaKolona) {
        if (polja[stariRed][staraKolona].getBoja() == polja[noviRed][novaKolona].getBoja()) {
            return new ArrayList<>();
        }
        zamijeni(stariRed, staraKolona, noviRed, novaKolona);
        ArrayList<Dogadjaj> dogadjaji = obradiPolje(noviRed, novaKolona);
        ArrayList<Dogadjaj> dogadjaji2 = obradiPolje(stariRed, staraKolona);
        dogadjaji.addAll(dogadjaji2);
        if (dogadjaji.isEmpty()) {
            zamijeni(stariRed, staraKolona, noviRed, novaKolona);
        } else {
            int rezultat = dogadjaji.stream().mapToInt(dogadjaj -> (dogadjaj.getTipDogadjaja()==TipDogadjaja.UNISTENO)?100:0).sum();
            poeni += rezultat;
            spusti();
            preostaloPoteza--;
            for (int i = 0; i < VISINA; i++) {
                for (int j = 0; j < SIRINA; j++) {
                    ArrayList<Dogadjaj> d = obradiPolje(i, j);
                    if (!d.isEmpty()) {
                        int rez = d.stream().mapToInt(dogadjaj -> (dogadjaj.getTipDogadjaja() == TipDogadjaja.UNISTENO) ? 100 : 0).sum();
                        poeni += rez;
                        spusti();
                    }
                }
            }
        }
        return dogadjaji;
    }

    /**
     * Metoda ce zamijeniti dva slatkisa u matrici slatkisa
     * @param stariRed Red prvog slatkisa
     * @param staraKolona Kolona prvog slatkisa
     * @param noviRed Red drugog slatkisa
     * @param novaKolona Kolona drugog slatkisa
     */
    private void zamijeni(int stariRed, int staraKolona, int noviRed, int novaKolona) {
        Slatkis temp = polja[stariRed][staraKolona];
        polja[stariRed][staraKolona] = polja[noviRed][novaKolona];
        polja[noviRed][novaKolona] = temp;
    }

    /**
     * Obrada jednog slatkisa u polju slatkisa
     * @param red Red slatkisa kojeg treba obraditi
     * @param kolona Kolona slatkisa kojeg treba obraditi
     * @return dogadjaji koji su se desili nakon obrade
     */
    private ArrayList<Dogadjaj> obradiPolje(int red, int kolona) {
        return polja[red][kolona].obradi(polja, red, kolona);
    }

    /**
     * Pomjeranje svih slatkisa prema dolje. Ovo simulira padanje slatkisa da se popune prazna mjesta
     * Nakon pomjeranja, prazna mjesta koja budu na vrhu bivaju popunjena nasumicno generisanim slatkisima
     */
    private void spusti() {
        for (int i = 0; i < SIRINA; i++) {
            for (int k = 0; k < VISINA; k++) {
                for (int j = VISINA - 1; j > 0; j--) {
                    if (polja[j][i] == null) {
                        polja[j][i] = polja[j - 1][i];
                        polja[j - 1][i] = null;
                    }
                }
            }
            for (int j = 0; j < VISINA; j++) {
                if (polja[j][i] == null) {
                    polja[j][i] = dajRandomSlatkis();
                }
            }
        }
    }

    /**
     * @return Nasumicno generisani slatkis
     */
    private Slatkis dajRandomSlatkis() {
        int indexBoje = ((int) Math.round(Math.random() * 100))%Boja.values().length;
        return new Slatkis(Tip.Obicni, Boja.values()[indexBoje]);
    }

    /**
     * @return polja slatkisa
     */
    public Slatkis[][] getPolja() {
        return polja;
    }

    /**
     * @return sirina polja
     */
    public int getSIRINA() {
        return SIRINA;
    }
}
