package at.jku.se.prse.team3;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fahrtenbuch {

    private List<Kategorie> kategorien;
    private List<Fahrt> fahrten;

    /**
     * Filled Constructor
     *
     * @param kategorien List of available Categories
     * @param fahrten    List of available Driving records
     */
    public Fahrtenbuch(List<Kategorie> kategorien, List<Fahrt> fahrten) {
        this.kategorien = new ArrayList<>(kategorien);
        this.fahrten = new ArrayList<>(fahrten);
    }

    /**
     * empty Constructor in Case first start
     */
    public Fahrtenbuch() {
        kategorien = new ArrayList<>();
        fahrten = new ArrayList<>();
    }

    /**
     * Method to crete a new Fahrt Issue #1
     *
     * @param kfzKennzeichen         kfzKennzeichen
     * @param datum                  datum
     * @param abfahrtszeit           abfahrtszeit
     * @param neueAnkunftszeit       neueAnkunftszeit
     * @param neueGefahreneKilometer neueGefahreneKilometer
     * @param neueAktiveFahrzeit     neueAktiveFahrzeit
     * @param fahrtStatus            fahrtStatus
     * @param kategorien             kategorien
     * @throws IOException
     */

    public void neueFahrt(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit,
                          LocalTime neueAnkunftszeit, Double neueGefahreneKilometer,
                          LocalTime neueAktiveFahrzeit, FahrtStatus fahrtStatus, List<Kategorie> kategorien) throws IOException {
        fahrten.add(new Fahrt(kfzKennzeichen, datum, abfahrtszeit, neueAnkunftszeit, neueGefahreneKilometer, neueAktiveFahrzeit, kategorien, fahrtStatus));

    }


    /**
     * Bearbeitungsvorgang einer Fahrt Issue #3
     *
     * @param kfzKennzeichen
     * @param datum
     * @param abfahrtszeit
     * @param neueAnkunftszeit
     * @param neueGefahreneKilometer
     * @param neueAktiveFahrzeit
     */
    public void bearbeiteFahrt(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit,
                               LocalTime neueAnkunftszeit, Double neueGefahreneKilometer,
                               LocalTime neueAktiveFahrzeit) {
        for (Fahrt fahrt : fahrten) {
            // Finde die spezifische Fahrt basierend auf dem Schlüssel
            if (fahrt.getKfzKennzeichen().equals(kfzKennzeichen) &&
                    fahrt.getDatum().equals(datum) &&
                    fahrt.getAbfahrtszeit().equals(abfahrtszeit)) {

                // Update der Eigenschaften der gefundenen Fahrt
                fahrt.setAnkunftszeit(neueAnkunftszeit);
                fahrt.setGefahreneKilometer(neueGefahreneKilometer);
                fahrt.setAktiveFahrzeit(neueAktiveFahrzeit);

                // Breche die Schleife ab, da die Fahrt gefunden und bearbeitet wurde
                break;
            }
        }
    }

    /**
     * Hilffunktion zum Hionzufügen einer Kategorie
     *
     * @param kategorie
     */
    public void addKategorie(String kategorie) {
        if(this.kategorien.stream().anyMatch(k -> k.getName().equals(kategorie))){
            System.out.println("Kategorie "+ kategorie+" existiert bereits!");
            return;
        }
        kategorien.add(new Kategorie(kategorie));
    }

    public void updateKategorie(Kategorie kategorie) {
        Kategorie kat = this.kategorien.stream().filter(k -> k.getName().equals(kategorie.getName())).findFirst().orElse(null);
        if(kat != null){
            kat.setName(kategorie.getName());
        }
        System.out.println("Kategorien aktualisiert:"+this.kategorien.toString());
    }

    /**
     * Fahrt löschen
     *
     * @param kfzKennzeichen
     * @param datum
     * @param abfahrtszeit
     */

    public void loescheFahrten(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit) {
        fahrten.removeIf(fahrt ->
                fahrt.getKfzKennzeichen().equals(kfzKennzeichen) &&
                        fahrt.getDatum().equals(datum) &&
                        fahrt.getAbfahrtszeit().equals(abfahrtszeit));

    }

    /**
     * Issue #5 wiederkehrende Fahrten
     *
     * @param reoccurances
     * @param kfzKennzeichen
     * @param abfahrtszeit
     * @param fahrtKategorie
     * @throws IOException
     */
    public void planeZukuenftigeFahrten(List<LocalDate> reoccurances,
                                        String kfzKennzeichen,
                                        LocalTime abfahrtszeit,
                                        List<Kategorie> fahrtKategorie) throws IOException {

        for (LocalDate d : reoccurances
        ) {
            LocalTime neueAnkunftszeit = LocalTime.of(0, 0, 0);
            double neueGefahreneKilometer = 0;
            LocalTime neueAktiveFahrzeit = LocalTime.of(0, 0, 0);

            fahrten.add(new Fahrt(
                    kfzKennzeichen,
                    d,
                    abfahrtszeit,
                    neueAnkunftszeit,
                    neueGefahreneKilometer,
                    neueAktiveFahrzeit,
                    fahrtKategorie,
                    FahrtStatus.ZUKUENFTIG));
        }

        exportFahrt();


    }

    /**
     * returns all entered Fahrten
     *
     * @return
     */
    public List<Fahrt> listeFahrten() {


        return fahrten;
    }

    /**
     * Filtert Fahrten anhand spezifischer Kriterien.
     *
     * @return Eine Liste gefilterter Fahrten.
     */
    public List<Fahrt> filtereFahrten() {

        //List<Fahrt> fahrten = new ArrayList<Fahrt>();

        return fahrten;
    }

    /**
     * Sortiert die Fahrten im Fahrtenbuch.
     */
    public void sortiereFahrten() {
        fahrten.sort(Comparator.comparing(Fahrt::getDatum)
                .thenComparing(Fahrt::getAbfahrtszeit));
    }

    /**
     * Ermittelt den Status einer spezifischen Fahrt.
     *
     * @param fahrt Die Fahrt, für die der Status ermittelt werden soll.
     * @return Der Status der Fahrt.
     */
    public FahrtStatus getFahrtstatus(Fahrt fahrt) {
        return fahrt.getFahrtstatus();
    }

    /**
     * Exportiert die Fahrtendaten in eine CSV-Datei.
     *
     * @throws IOException Wenn beim Schreiben der Datei ein Fehler auftritt.
     */
    public void exportFahrt() throws IOException {
        //export Fahrten&Kategorien as CSV.
        Path path = Paths.get(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Fahrtenbuch 0.0.3");
        String realExport = path.toString();

        String exportFahrten = realExport + File.separator + "fahrten.csv";
        String exportKategorien = realExport + File.separator + "Kategorien.csv";
        CSVWriter csvWriter = new CSVWriter(new FileWriter(exportFahrten));


        for (Fahrt f : fahrten
        ) {
            String[] data = {f.getKfzKennzeichen(),
                    String.valueOf(f.getDatum()),
                    String.valueOf(f.getAbfahrtszeit()),
                    String.valueOf(f.getAnkunftszeit()),
                    String.valueOf(f.getGefahreneKilometer()),
                    String.valueOf(f.getAktiveFahrzeit()),
                    String.valueOf(f.getFahrtstatus()),
                    f.getKategorieNames().toString().replace("[", "").replace("]", "")};

            csvWriter.writeNext(data);
        }
        csvWriter.close();
        CSVWriter csvWriter2 = new CSVWriter(new FileWriter(exportKategorien));
        for (Kategorie k : kategorien
        ) {
            String[] data = {k.getName()};
            csvWriter2.writeNext(data);
        }

        csvWriter2.close();

    }

    /**
     * Importiert Fahrtendaten aus einer CSV-Datei.
     *
     * @throws IOException Wenn beim Lesen der Datei ein Fehler auftritt.
     * @throws CsvValidationException Wenn die CSV-Daten ungültig sind.
     */
    public void importFahrt() throws IOException, CsvValidationException {
        //export Fahrten&Kategorien as CSV.

        Path path = Paths.get(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Fahrtenbuch 0.0.3");
        Path realImport = path;
        Path importFahrten = Paths.get(realImport + File.separator + "fahrten.csv");

        Path importKategorien = Paths.get(realImport + File.separator + "Kategorien.csv");
        try {

            Files.createDirectory(realImport);
            System.out.println("Neues System...");
            try (CSVWriter writer = new CSVWriter(new FileWriter(importFahrten.toFile()))) {
            }
            try (CSVWriter writer2 = new CSVWriter(new FileWriter(importKategorien.toFile()))) {
            }
            System.out.println("Initialisiere Datensätze...");
        } catch (FileAlreadyExistsException f) {
            System.out.println("Willkommen zurück!");

            String[] data;
            String kFZKennzeichen;
            LocalDate datum;
            LocalTime abfahrtszeit;
            LocalTime ankunftszeit;
            Double gefahreneKilometer;
            LocalTime aktiveFahrzeit;
            List<Kategorie> kategorien;
            FahrtStatus fahrtstatus;


            try (CSVReader reader = new CSVReader(new FileReader(importFahrten.toFile()))) {

                while ((data = reader.readNext()) != null) {


                    kFZKennzeichen = data[0];


                    datum = LocalDate.parse(data[1]);


                    abfahrtszeit = LocalTime.parse(data[2]);


                    ankunftszeit = LocalTime.parse(data[3]);
                    gefahreneKilometer = Double.valueOf(data[4]);
                    aktiveFahrzeit = LocalTime.parse(data[5]);
                    if (FahrtStatus.ZUKUENFTIG.toString().equals(data[6])) fahrtstatus = FahrtStatus.ZUKUENFTIG;
                    else if (FahrtStatus.ABSOLVIERT.toString().equals(data[6])) fahrtstatus = FahrtStatus.ABSOLVIERT;
                    else fahrtstatus = FahrtStatus.AUF_FAHRT;
                    kategorien = Stream.of(data[7]).map(s -> new Kategorie(s)).toList();
                    neueFahrt(kFZKennzeichen, datum, abfahrtszeit, ankunftszeit, gefahreneKilometer, aktiveFahrzeit, fahrtstatus, kategorien);

                }

            } catch (NullPointerException nullPointerException) {


            }

            try (CSVReader reader2 = new CSVReader(new FileReader(importKategorien.toFile()))) {
                List<String[]> allKat = reader2.readAll();
                for (String[] d : allKat) {
                    for (String cat : d) {
                        this.kategorien.add(new Kategorie(cat.trim()));
                    }
                }
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }


        }

    }

    /**
     * Berechnet die insgesamt gefahrenen Kilometer pro Monat.
     *
     * @return Eine Map, die jedem Monat (YearMonth) die insgesamt gefahrenen Kilometer zuordnet.
     */
    public Map<YearMonth, Double> berechneKilometerProMonat() {
        Map<YearMonth, Double> kilometerProMonat = new TreeMap<>();
        for (Fahrt fahrt : fahrten) {
            YearMonth yearMonth = YearMonth.from(fahrt.getDatum());
            kilometerProMonat.merge(yearMonth, fahrt.getGefahreneKilometer(), Double::sum);
        }
        return kilometerProMonat;
    }


    /**
     * Berechnet die gefahrenen Kilometer pro Monat und Kategorie.
     *
     * @return Eine Map, die jedem Monat (YearMonth) eine Map von Kategorien und den jeweiligen Kilometern zuordnet.
     */
    public Map<YearMonth, Map<String, Double>> berechneKilometerProMonatUndKategorie() {
        Map<YearMonth, Map<String, Double>> kilometerProMonatUndKategorie = new TreeMap<>();

        for (Fahrt fahrt : fahrten) {
            YearMonth yearMonth = YearMonth.from(fahrt.getDatum());
            List<String> fahrtKategorien = fahrt.getKategorieNames();

            fahrtKategorien.forEach(kategorie -> {
                kilometerProMonatUndKategorie
                        .computeIfAbsent(yearMonth, k -> new HashMap<>())
                        .merge(kategorie, fahrt.getGefahreneKilometer(), Double::sum);
            });
        }
        return kilometerProMonatUndKategorie;
    }


    /**
     * Berechnet die gefahrenen Kilometer pro Jahr und Kategorie.
     *
     * @return Eine Map, die jedem Jahr eine Map von Kategorien und den jeweiligen Kilometern zuordnet.
     */
    public Map<Integer, Map<String, Double>> berechneKilometerProJahrUndKategorie() {
        Map<Integer, Map<String, Double>> kilometerProJahrUndKategorie = new TreeMap<>();

        for (Fahrt fahrt : fahrten) {
            int jahr = fahrt.getDatum().getYear();
            List<String> fahrtKategorien = fahrt.getKategorieNames();

            fahrtKategorien.forEach(kategorie -> {
                kilometerProJahrUndKategorie
                        .computeIfAbsent(jahr, k -> new HashMap<>())
                        .merge(kategorie, fahrt.getGefahreneKilometer(), Double::sum);
            });
        }
        return kilometerProJahrUndKategorie;
    }

    /**
     * Gibt eine Liste aller einzigartigen Kategorien zurück.
     *
     * @return Ein Set mit einzigartigen Kategorien.
     */
    public Set<String> getKategorien() {
        Set<String> uniqueKategorien = new HashSet<>();
        for (Fahrt fahrt : fahrten) {
            uniqueKategorien.addAll(fahrt.getKategorieNames());
        }
        return uniqueKategorien;
    }

    /**
     * Gibt alle Kategorien als ObservableList zurück.
     * Diese Methode bietet die gleiche Funktionalität wie getKategorien(), gibt jedoch eine ObservableList zurück.
     *
     * @param x Aktiviert die Methode, wenn true. Kann true oder false sein.
     * @return Eine ObservableList aller Kategorien.
     */
    public ObservableList<Kategorie> getKategorien(Boolean x) {
        ObservableList<Kategorie> uniqueKategorien = FXCollections.observableArrayList();
        uniqueKategorien.addAll(kategorien);
        return uniqueKategorien;
    }

    public ObservableList<String> getKategorieNames(Boolean x) {
        ObservableList<String> uniqueKategorien = FXCollections.observableArrayList();
        uniqueKategorien.addAll(kategorien.stream().map(k -> k.getName()).toList());
        return uniqueKategorien;
    }

    /**
     * Fügt eine Sammlung von Kategorien zur Liste der Kategorien in der Logikklasse hinzu.
     *
     * @param kategorienNeu Eine Sammlung von Kategorien.
     */
    public void addKategories(Collection kategorienNeu) {
        kategorienNeu.forEach(k -> this.addKategorie((String) k));
        //kategorien.addAll(kategorieList);
    }

    public boolean deleteKategorie(Kategorie kategorie) {
        boolean kategorieIstInEinerFahrtVorhanden = false;
        for(Fahrt f:fahrten){
            if(f.hasKategorie(kategorie.getName())){
                System.out.println("Kann Kategorie "+kategorie.getName() +" nicht löschen, da sie in der Fahrt "+ f+" verwendet wird.");
                return false;
            }
        }
        //var kat = this.kategorien.stream().filter(k -> k.getName().equals(kategorie.getName())).findAny().orElse(null);
        return this.kategorien.remove(kategorie);
    }

    /**
     * Filtert Fahrten nach einem spezifischen Datum.
     *
     * @param date Das Datum, nach dem gefiltert werden soll.
     * @return Eine Liste von Fahrten, die am angegebenen Datum stattgefunden haben.
     */
    public List<Fahrt> filterByDate(LocalDate date) {
        if (date == null) {
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = fahrten.stream().filter(f ->
                f.getDatum().getYear()==
                date.getYear() && f.getDatum().getMonth() ==date.getMonth() && f.getDatum().getDayOfMonth() == date.getDayOfMonth()).collect(Collectors.toList());
        return gefilterteFahrten;
    }


    /**
     * Filtert Fahrten, deren Durchschnittsgeschwindigkeit unter oder gleich dem angegebenen Wert ist.
     *
     * @param avg Die Vergleichsvariable für die Durchschnittsgeschwindigkeit.
     * @return Eine Liste von Fahrten, deren Durchschnittsgeschwindigkeit unter oder gleich dem angegebenen Wert ist.
     */
    public List<Fahrt> filterByAvgVUnder(double avg){
        if (avg<0.00){
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();

        for(Fahrt fahrt: fahrten){
            double gefahreneKilometer = fahrt.getGefahreneKilometer();
            LocalTime aktiveFahrzeit = fahrt.getAktiveFahrzeit();
            double time = aktiveFahrzeit.getHour() + (aktiveFahrzeit.getMinute()/60.0);
            double instanceAvg = gefahreneKilometer/time;
            if(instanceAvg<=avg){
                gefilterteFahrten.add(fahrt);
            }
        }
        return gefilterteFahrten;
    }

    /**
     * Filtert Fahrten, deren Durchschnittsgeschwindigkeit über oder gleich dem angegebenen Wert ist.
     *
     * @param avg Die Vergleichsvariable für die Durchschnittsgeschwindigkeit.
     * @return Eine Liste von Fahrten, deren Durchschnittsgeschwindigkeit über oder gleich dem angegebenen Wert ist.
     */
    public List<Fahrt> filterByAvgVOver(double avg){
        if (avg<0.00){
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();

        for(Fahrt fahrt: fahrten){
            double gefahreneKilometer = fahrt.getGefahreneKilometer();
            LocalTime aktiveFahrzeit = fahrt.getAktiveFahrzeit();
            double time = aktiveFahrzeit.getHour() + (aktiveFahrzeit.getMinute()/60.0);
            double instanceAvg = gefahreneKilometer/time;
            if(instanceAvg>=avg){
                gefilterteFahrten.add(fahrt);
            }
        }
        return gefilterteFahrten;
    }

    /**
     * Filtert Fahrten nach bestimmten Kategorien.
     *
     * @param filterKategorien Eine Liste von Kategorien, nach denen gefiltert werden soll.
     * @return Eine Liste von Fahrten, die mindestens eine der angegebenen Kategorien enthalten.
     */
    public List<Fahrt> filterByKategorie(List<String> filterKategorien) {
        if (kategorien == null || filterKategorien.isEmpty()) {
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();
        for (String filterKategorie : filterKategorien) {
            gefilterteFahrten.addAll(this.fahrten.stream()
                    .filter(f -> f.getKategorieNames().contains(filterKategorie))
                    .collect(Collectors.toList()));
        }
        return gefilterteFahrten;
    }
}
