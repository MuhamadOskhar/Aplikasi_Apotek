package tugas_sda;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PriorityQueue;

public class Halaman_Lihat extends JFrame {

    private List<PatientData> linearSearchByName(String searchName, List<PatientData> patientList) {
        List<PatientData> foundPatients = new ArrayList<>();

        for (PatientData patientData : patientList) {
            if (patientData.getNama().toLowerCase().contains(searchName)) {
                foundPatients.add(patientData);
            }
        }

        return foundPatients.isEmpty() ? null : foundPatients;
    }

    private PriorityQueue<PatientData> patientQueue;
    private JPanel queuePanel;
    private JPanel dataPanel;
    private JPanel searchPanel;

    public Halaman_Lihat() {
        initComponents();
        retrieveDataFromDatabase();
        displayQueueData();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Med Shop");
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Kembali");
        backButton.addActionListener(e -> backButtonActionPerformed());
        headerPanel.add(backButton);

        ImageIcon medShopIcon = new ImageIcon("/home/oskhar/server/myApp/Aplikasi_Apotek/img/netbeans/medshop.png");
        JLabel medShopLabel = new JLabel(medShopIcon);
        headerPanel.add(medShopLabel);

        JLabel titleLabel = new JLabel("Med Shop");
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
        headerPanel.add(titleLabel);

        searchPanel = new JPanel();
        JTextField searchTextField = new JTextField("", 20);
        searchPanel.add(searchTextField);

        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchButtonActionPerformed(searchTextField.getText()));
        searchPanel.add(searchButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshButtonActionPerformed());
        searchPanel.add(refreshButton);

        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.SOUTH);
    }

    private void refreshButtonActionPerformed() {
        // Perbarui tampilan dengan data terbaru dari database
        // Refresh tampilan setelah menghapus data
        getContentPane().removeAll();
        initComponents();
        retrieveDataFromDatabase();
        displayQueueData();
        revalidate();
        repaint();
    }

    private void searchButtonActionPerformed(String searchText) {
        retrieveDataFromDatabase();
        String searchName = searchText.toLowerCase();

        List<PatientData> patientList = new ArrayList<>(patientQueue);
        List<PatientData> foundPatients = linearSearchByName(searchName, patientList);

        if (!foundPatients.isEmpty()) {
            System.out.println("Pasien ditemukan:");

            for (PatientData foundPatient : foundPatients) {
                System.out.println("Nama: " + foundPatient.getNama() +
                        ", Usia: " + foundPatient.getUsia() +
                        ", Konsumsi: " + foundPatient.getKonsumsi() +
                        ", Obat: " + foundPatient.getObat() +
                        ", Pasien Darurat: " + foundPatient.isPasienDarurat());
            }
        } else {
            System.out.println("Pasien dengan nama '" + searchName + "' tidak ditemukan.");
        }

        // Update the view with the search result
        updateQueuePanel(foundPatients);
    }

    private void updateQueuePanel(List<PatientData> foundPatients) {
        // Hapus semua komponen dari dataPanel
        dataPanel.removeAll();

        if (!foundPatients.isEmpty()) {
            // Tambahkan kembali komponen sesuai dengan foundPatients
            for (PatientData foundPatient : foundPatients) {
                String patientInfo = "Nama: " + foundPatient.getNama() +
                        ", Usia: " + foundPatient.getUsia() +
                        ", Konsumsi: " + foundPatient.getKonsumsi() +
                        ", Obat: " + foundPatient.getObat() +
                        ", Pasien Darurat: " + foundPatient.isPasienDarurat();

                JLabel patientLabel = new JLabel(patientInfo);

                JButton selesaiButton = new JButton("Selesai");
                selesaiButton.addActionListener(e -> selesaiButtonActionPerformed(foundPatient));

                // Menambahkan komponen ke dalam dataPanel
                dataPanel.add(patientLabel);
                dataPanel.add(selesaiButton);
            }
        } else {
            JLabel notFoundLabel = new JLabel("Pasien tidak ditemukan.");
            dataPanel.add(notFoundLabel);
        }

        // Validate and repaint
        revalidate();
        repaint();
    }

    private void retrieveDataFromDatabase() {
        patientQueue = new PriorityQueue<>();

        try (Connection connection = koneksi.getConnection()) {
            String query = "SELECT * FROM apotek ORDER BY created_at DESC";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nama = resultSet.getString("nama");
                        String usia = resultSet.getString("usia");
                        String konsumsi = resultSet.getString("konsumsi");
                        String obat = resultSet.getString("obat");
                        boolean pasienDarurat = resultSet.getBoolean("pasien_darurat");

                        PatientData patientData = new PatientData(nama, usia, konsumsi, obat, pasienDarurat);
                        patientQueue.offer(patientData);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayQueueData() {
        queuePanel = new JPanel(new BorderLayout());

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));

        while (!patientQueue.isEmpty()) {
            PatientData patientData = patientQueue.poll();
            String patientInfo = "Nama: " + patientData.getNama() +
                    ", Usia: " + patientData.getUsia() +
                    ", Konsumsi: " + patientData.getKonsumsi() +
                    ", Obat: " + patientData.getObat() +
                    ", Pasien Darurat: " + patientData.isPasienDarurat();

            JLabel patientLabel = new JLabel(patientInfo);

            JButton selesaiButton = new JButton("Selesai");
            selesaiButton.addActionListener(e -> selesaiButtonActionPerformed(patientData));

            // Menambahkan komponen ke dalam dataPanel
            dataPanel.add(patientLabel);
            dataPanel.add(selesaiButton);
        }

        // Menggunakan JScrollPane agar dapat di-scroll jika kontennya lebih besar dari
        // panelnya
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        queuePanel.add(scrollPane, BorderLayout.CENTER);

        add(queuePanel, BorderLayout.CENTER);
    }

    private void selesaiButtonActionPerformed(PatientData patientData) {
        // Implementasi untuk menghapus data dari database
        try (Connection connection = koneksi.getConnection()) {
            String deleteQuery = "DELETE FROM apotek WHERE nama = ? AND usia = ? AND konsumsi = ? AND obat = ? AND pasien_darurat = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, patientData.getNama());
                preparedStatement.setString(2, patientData.getUsia());
                preparedStatement.setString(3, patientData.getKonsumsi());
                preparedStatement.setString(4, patientData.getObat());
                preparedStatement.setBoolean(5, patientData.isPasienDarurat());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari database");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data", "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data dari database", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Refresh tampilan setelah menghapus data
        getContentPane().removeAll();
        initComponents();
        retrieveDataFromDatabase();
        displayQueueData();
        revalidate();
        repaint();
    }

    private void backButtonActionPerformed() {
        this.dispose();
        NewJDialog frame1 = new NewJDialog(null, rootPaneCheckingEnabled);
        frame1.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Halaman_Lihat frame = new Halaman_Lihat();
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
