package ba.unsa.pmf.candy.crush.grafika;

import ba.unsa.pmf.candy.crush.logika.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Ovdje se definisu sve animacije koje se trebaju prikazati na grafickom interfejsu
 */
public class PrikazivacGUIa implements Initializable {

    public GridPane board;
    public Label score;
    public Label brojPoteza;
    public Label vrijeme;

    /**
     * U ovoj metodi se vrsi poziv inicijalizacije igre.
     *
     * @param url Parametar koji ne koristimo
     * @param resourceBundle Parametar koji ne koristimo
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        zapocniNovuIgru();
    }

    /**
     * Metoda uklanja slike slatkisa,
     */
    private void zavrsiIgru() {
        board.getChildren().clear();
    }

    /**
     * Inicijalizira GUI i kreira CandyModel koji se koristi za igru
     */
    private void zapocniNovuIgru() {
        CandyModel candyModel = new CandyModel(25,60,this::prikaziVrijeme,this::zaustaviIgru);
        int rowCount = candyModel.getSIRINA(), columnCount = candyModel.getSIRINA();
        score.setText("0");
        brojPoteza.setText(candyModel.getBrojPreostalihPoteza() + "");

        board.getChildren().clear();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                ImageView imageView = getImageView(candyModel, col, row);
                board.add(imageView, col, row);
            }
        }
    }

    /**
     * Prikazuje vrijeme preostalo koje se iz CandyModel moze dobiti
     * @param preostaloVremena
     */
    void prikaziVrijeme(Integer preostaloVremena) {
        Platform.runLater(() -> {
            vrijeme.setText(preostaloVremena.toString());
        });
    }

    /**
     * Zavrsava igru
     * @return
     */
    Integer zaustaviIgru() {
        Platform.runLater(this::zavrsiIgru);
        return 0;
    }

    /**
     * U ovoj metodi se kreira komponenta sa slikom slatkisa koja ce se ubaciti u polje za igru.
     * Ovdje se, takodjer, definise oko animacija koje se desavaju prilikom premjestanja slatkisa sa jedne
     * pozicije na drugu.
     * @param candyModel model gdje se nalaze informacije o slatkisima
     * @param col kolona slatkisa za koji treba kreirati sliku
     * @param row red slatkisa za koji treba kreirati sliku
     * @return Slika slatkisa
     */
    private ImageView getImageView(CandyModel candyModel, int col, int row) {
        ImageView imageView = new ImageView(loadCandyImage(candyModel.getPolja()[row][col]));
        imageView.setOnDragDetected(mouseEvent -> {
            // Animacija kada se krene vuci slika slatkisa
            int computedRow = GridPane.getRowIndex(imageView);
            int computedCol = GridPane.getColumnIndex(imageView);
            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(computedRow + "," + computedCol);
            db.setContent(content);
            mouseEvent.consume();
        });
        imageView.setOnDragOver(event -> {
            // Animacija kada prevlacimo jedan slatkis preko drugog
            int computedRow = GridPane.getRowIndex(imageView);
            int computedCol = GridPane.getColumnIndex(imageView);
            // Ovo omogucava nastavak animacije
            if (event.getGestureSource() != imageView &&
                    event.getDragboard().hasString()) {
                String[] coordinates = event.getDragboard().getString().split(",");
                int prevRow = Integer.parseInt(coordinates[0]);
                int prevCol = Integer.parseInt(coordinates[1]);
                if (Math.abs(computedCol - prevCol) == 1 ^ Math.abs(computedRow - prevRow) == 1) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });
        imageView.setOnDragDropped(mouseEvent -> {
            // Animacija kada pustimo prvi slatkis na drugo mjesto
            int computedRow = GridPane.getRowIndex(imageView);
            int computedCol = GridPane.getColumnIndex(imageView);
            Dragboard dragboard = mouseEvent.getDragboard();
            // treba li izvrsiti animaciju
            if (dragboard.hasString()) {
                String[] coordinates = mouseEvent.getDragboard().getString().split(",");
                int prevRow = Integer.parseInt(coordinates[0]);
                int prevCol = Integer.parseInt(coordinates[1]);
                Node prevNode = null, nextNode = null;
                // pronadji koje slike treba zamijeniti
                for (Node child : board.getChildren()) {
                    if (GridPane.getRowIndex(child) == prevRow && GridPane.getColumnIndex(child) == prevCol) {
                        prevNode = child;
                    }
                    if (GridPane.getRowIndex(child) == computedRow && GridPane.getColumnIndex(child) == computedCol) {
                        nextNode = child;
                    }
                }
                // Pravljenje i pokretanje animacija za zamjenu dvije slike
                if (prevNode != null && nextNode != null) {
                    napraviAnimacijeIPokreni(candyModel, computedRow, computedCol, prevRow, prevCol, prevNode, nextNode);
                }
            }
            mouseEvent.setDropCompleted(true);
            mouseEvent.consume();
        });
        return imageView;
    }

    /**
     * Kreira animacije koje zamijene slike slatkisa
     * @param candyModel
     * @param computedRow
     * @param computedCol
     * @param prevRow
     * @param prevCol
     * @param prevNode
     * @param nextNode
     */
    private void napraviAnimacijeIPokreni(CandyModel candyModel, int computedRow, int computedCol, int prevRow, int prevCol, Node prevNode, Node nextNode) {
        Node finalPrevNode = prevNode;
        Node finalNextNode = nextNode;
        double hOffset = prevNode.getBoundsInParent().getWidth() + board.getHgap();
        double vOffset = prevNode.getBoundsInParent().getHeight() + board.getVgap();
        int hSign = Integer.signum(computedCol - prevCol);
        int vSign = Integer.signum(computedRow - prevRow);
        // kreiranje animacije
        TranslateTransition translateTransition = new TranslateTransition();
        // postavljanje parametara animacije
        translateTransition.setDuration(Duration.millis(250));
        translateTransition.setByX(hOffset * hSign);
        translateTransition.setByY(vOffset * vSign);
        translateTransition.setNode(prevNode);
        translateTransition.setOnFinished(actionEvent -> {
            GridPane.setRowIndex(finalPrevNode, computedRow);
            GridPane.setColumnIndex(finalPrevNode, computedCol);
            finalPrevNode.setTranslateX(0);
            finalPrevNode.setTranslateY(0);
        });
        // kreiranje animacije
        TranslateTransition translateTransition2 = new TranslateTransition();
        // postavljenja parametara animacije
        translateTransition2.setDuration(Duration.millis(256));
        translateTransition2.setByX(-hOffset * hSign);
        translateTransition2.setByY(-vOffset * vSign);
        translateTransition2.setNode(nextNode);
        translateTransition2.setOnFinished(actionEvent -> {
            GridPane.setRowIndex(finalNextNode, prevRow);
            GridPane.setColumnIndex(finalNextNode, prevCol);
            finalNextNode.setTranslateX(0);
            finalNextNode.setTranslateY(0);
            // Ovo je igranje koje se pojavljuje i u konzolnoj aplikaciji
            ArrayList<Dogadjaj> dogadjaji = candyModel.odigraj(prevRow, prevCol, computedRow, computedCol);
            // ----------------------------------------------------------
            if (dogadjaji.isEmpty()) {
                GridPane.setRowIndex(finalNextNode, computedRow);
                GridPane.setColumnIndex(finalNextNode, computedCol);
                GridPane.setRowIndex(finalPrevNode, prevRow);
                GridPane.setColumnIndex(finalPrevNode, prevCol);
            } else {
                // prezentacija vrijednosti korisniku
                score.setText(candyModel.getPoeni() + "");
                brojPoteza.setText(candyModel.getBrojPreostalihPoteza()+ "");
                azurirajSveSlike(candyModel);
            }
        });
        // Pokretanje animacije na GUI-u
        translateTransition.play();
        translateTransition2.play();
    }

    /**
     * Azuriranje slika na GUI-u na osnovu modela.
     * @param candyModel model u kojem se nalaze informacije o slatkisima
     */
    private void azurirajSveSlike(CandyModel candyModel) {
        Slatkis[][] polje = candyModel.getPolja();
        board.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;
            Slatkis slatkis = polje[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)];
            imageView.setImage(loadCandyImage(slatkis));
        });
    }

    /**
     * @param slatkis podaci o slatkisu
     * @return slika slatkisa na osnovu njegove boje
     */
    private Image loadCandyImage(Slatkis slatkis) {
        String path = "/img/" + slatkis.getBoja().toString().toLowerCase() + ".png";
        return new Image(UcitavacGUIa.class.getResource(path).toString(), 64, 64, true, true);
    }
}
