package tugas_sda;

import java.util.ArrayList;
import java.util.List;

public class LinearSearch {

    // Metode pencarian linier
    public static List<PatientData> searchPatientsByName(List<PatientData> patients, String keyword) {
        List<PatientData> searchResults = new ArrayList<>();

        for (PatientData patient : patients) {
            if (patient.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(patient);
            }
        }

        return searchResults;
    }

    public static void main(String[] args) {
        // Contoh penggunaan
        List<PatientData> patients = new ArrayList<>();
        patients.add(new PatientData("John Doe", "30", "Fever", "Paracetamol", false));
        patients.add(new PatientData("Alice Smith", "25", "Cough", "Cough Syrup", true));
        patients.add(new PatientData("Bob Johnson", "40", "Headache", "Ibuprofen", false));

        String keyword = "Alice";

        List<PatientData> searchResults = searchPatientsByName(patients, keyword);

        // Tampilkan hasil pencarian
        System.out.println("Search results for '" + keyword + "':");
        for (PatientData result : searchResults) {
            System.out.println("Nama: " + result.getNama() +
                    ", Usia: " + result.getUsia() +
                    ", Konsumsi: " + result.getKonsumsi() +
                    ", Obat: " + result.getObat() +
                    ", Pasien Darurat: " + result.isPasienDarurat());
        }
    }
}
