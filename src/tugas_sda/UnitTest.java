package tugas_sda;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PriorityQueue;

public class UnitTest {
    private static PatientData linearSearchByName(String searchName, PriorityQueue<PatientData> patientQueue) {
        for (PatientData patientData : patientQueue) {
            if (patientData.getNama().equalsIgnoreCase(searchName)) {
                return patientData;
            }
        }
        return null; // Mengembalikan null jika tidak ditemukan
    }
    public static void main(String[] args) {
        // Buat PriorityQueue untuk menyimpan data dengan prioritas
        PriorityQueue<PatientData> patientQueue = new PriorityQueue<>();

        try (Connection connection = koneksi.getConnection()) {
            String query = "SELECT * FROM apotek ORDER BY created_at DESC";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Ambil data dari setiap kolom
                        String nama = resultSet.getString("nama");
                        String usia = resultSet.getString("usia");
                        String konsumsi = resultSet.getString("konsumsi");
                        String obat = resultSet.getString("obat");
                        boolean pasienDarurat = resultSet.getBoolean("pasien_darurat");

                        // Buat objek PatientData dan tambahkan ke PriorityQueue
                        PatientData patientData = new PatientData(nama, usia, konsumsi, obat, pasienDarurat);
                        patientQueue.offer(patientData);
                    }
                }
            }
            String searchName = "usman";
            PatientData foundPatient = linearSearchByName(searchName, patientQueue);

            if (foundPatient != null) {
                System.out.println("Pasien ditemukan:");
                System.out.println("Nama: " + foundPatient.getNama() + ", Usia: " + foundPatient.getUsia() +
                        ", Konsumsi: " + foundPatient.getKonsumsi() + ", Obat: " + foundPatient.getObat() +
                        ", Pasien Darurat: " + foundPatient.isPasienDarurat());
            } else {
                System.out.println("Pasien dengan nama '" + searchName + "' tidak ditemukan.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Kelas untuk menyimpan data pasien
class PatientData implements Comparable<PatientData> {
    private String nama;
    private String usia;
    private String konsumsi;
    private String obat;
    private boolean pasienDarurat;

    public PatientData(String nama, String usia, String konsumsi, String obat, boolean pasienDarurat) {
        this.nama = nama;
        this.usia = usia;
        this.konsumsi = konsumsi;
        this.obat = obat;
        this.pasienDarurat = pasienDarurat;
    }

    // Mendefinisikan kriteria prioritas (misalnya, usia atau status pasien darurat)
    @Override
    public int compareTo(PatientData other) {
        // Contoh: Prioritaskan pasien darurat lebih tinggi
        if (this.pasienDarurat && !other.pasienDarurat) {
            return -1;
        } else if (!this.pasienDarurat && other.pasienDarurat) {
            return 1;
        } else {
            // Jika sama atau tidak ada kriteria prioritas, urutkan berdasarkan nama
            return this.nama.compareTo(other.nama);
        }
    }

    // Getter untuk mendapatkan nilai properti
    public String getNama() {
        return nama;
    }

    public String getUsia() {
        return usia;
    }

    public String getKonsumsi() {
        return konsumsi;
    }

    public String getObat() {
        return obat;
    }

    public boolean isPasienDarurat() {
        return pasienDarurat;
    }
}
