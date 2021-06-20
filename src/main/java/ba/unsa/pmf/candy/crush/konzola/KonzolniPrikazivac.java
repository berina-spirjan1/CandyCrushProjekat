package ba.unsa.pmf.candy.crush.konzola;

import ba.unsa.pmf.candy.crush.logika.CandyModel;

import java.util.Scanner;

public class KonzolniPrikazivac {
    static boolean play = true;

    /**
     * Kreira Model slatkisa i trazi od korisnika unos i ispisuje rezultate na konzolu
     */
    public static void pokreni() {
        CandyModel candyModel = new CandyModel(30,180,v->{},KonzolniPrikazivac::zaustaviIgru);
        prikaziPolja(candyModel);
        Scanner scanner = new Scanner(System.in);
        while(play) {
            int stariRed, staraKolona, noviRed, novaKolona;
            System.out.print("Unesi koordinate prvog slatkisa kao \"red kolona\" (-1 za kraj): ");
            stariRed = scanner.nextInt();
            if (stariRed == -1) {
                break;
            }
            staraKolona = scanner.nextInt();
            System.out.print("Unesi koordinate drugog slatkisa kao \"red kolona\": ");
            noviRed = scanner.nextInt();
            novaKolona = scanner.nextInt();
            candyModel.odigraj(stariRed, staraKolona, noviRed, novaKolona);
            prikaziPolja(candyModel);
            prikaziPoene(candyModel);
            prikaziVrijeme(candyModel.getPreostaloVremena());
        }
        candyModel.prekiniIgru();
        System.out.println("Igra je zavrsena.");
    }

    /**
     * Metoda prekida izvrsavanje konzolne aplikacije
     * @return
     */
    private static Integer zaustaviIgru() {
        play = false;
        return 0;
    }

    /**
     * Metoda ispisuje vrijeme koje je preostalo za igranje
     * @param vrijeme
     */
    private static void prikaziVrijeme(Integer vrijeme) {
        System.out.println("Vrijeme: " + vrijeme + " s");
    }

    /**
     * Ispisuje broj poena u trenutnoj igri
     * @param candyModel Logicki model gdje se nalaze informacije o slatkisima
     */
    private static void prikaziPoene(CandyModel candyModel) {
        System.out.printf("Poeni: %d\n",candyModel.getPoeni());
    }

    /**
     * Ispisuje sva polja slatkisa u formatu matrice
     * @param candyModel Logicki model gdje se nalaze informacije o slatkisima
     */
    private static void prikaziPolja(CandyModel candyModel) {
        System.out.printf("  |");
        for (int i = 0; i < candyModel.getSIRINA(); i++) {
            System.out.printf("%3d",i);
        }
        System.out.print("\n--|");
        for (int i = 0; i < candyModel.getSIRINA(); i++) {
            System.out.print("---");
        }
        System.out.println();
        for (int i = 0; i < candyModel.getSIRINA(); i++) {
            System.out.printf("%2d|",i);
            for (int j = 0; j < candyModel.getSIRINA(); j++) {
                System.out.printf(" %2s",candyModel.getPolja()[i][j]);
            }
            System.out.println();
        }
    }
}
