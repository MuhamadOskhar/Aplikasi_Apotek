package tugas_sda;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PriorityQueue;

public class UnitTestGUI extends JFrame {

    private PriorityQueue<PatientData> patientQueue;

    public UnitTestGUI() {
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

        JTextField searchTextField = new JTextField("Cari Data");
        headerPanel.add(searchTextField);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void retrieveDataFromDatabase() {
        patientQueue = new PriorityQueue<>();

        try (Connection connection = koneksi.getConnection()) {
            String query = "SELECT * FROM apotek";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nama = resultSet.getString("nama");
                        String usia = resultSet.getString("usia");
                        String diagnosis = resultSet.getString("diagnosis");
                        String obat = resultSet.getString("obat");
                        boolean pasienDarurat = resultSet.getBoolean("pasien_darurat");

                        PatientData patientData = new PatientData(nama, usia, diagnosis, obat, pasienDarurat);
                        patientQueue.offer(patientData);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayQueueData() {
        JPanel queuePanel = new JPanel(new BorderLayout());

        JPanel dataPanel = new JPanel();

        GroupLayout dataPanelLayout = new GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);

        GroupLayout.SequentialGroup horizontalGroup = dataPanelLayout.createSequentialGroup();
        GroupLayout.SequentialGroup verticalGroup = dataPanelLayout.createSequentialGroup();

        while (!patientQueue.isEmpty()) {
            PatientData patientData = patientQueue.poll();
            String patientInfo = "Nama: " + patientData.getNama() +
                    ", Usia: " + patientData.getUsia() +
                    ", Konsumsi: " + patientData.getKonsumsi() +
                    ", Obat: " + patientData.getObat() +
                    ", Pasien Darurat: " + patientData.isPasienDarurat();

            JLabel patientLabel = new JLabel(patientInfo);

            JButton selesaiButton = new JButton("Selesai");
            selesaiButton.addActionListener(e -> selesaiButtonActionPerformed(patientData)); // Panggil metode saat
                                                                                             // tombol "Selesai" ditekan

            GroupLayout.Group entryHorizontalGroup = dataPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(patientLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selesaiButton, GroupLayout.Alignment.TRAILING);

            GroupLayout.Group entryVerticalGroup = dataPanelLayout.createSequentialGroup()
                    .addComponent(patientLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(selesaiButton);

            horizontalGroup.addGroup(entryHorizontalGroup);
            verticalGroup.addGroup(entryVerticalGroup);
        }

        dataPanelLayout.setHorizontalGroup(
                dataPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(horizontalGroup));

        dataPanelLayout.setVerticalGroup(
                dataPanelLayout.createSequentialGroup()
                        .addGroup(verticalGroup));

        JScrollPane scrollPane = new JScrollPane(dataPanel);
        queuePanel.add(scrollPane, BorderLayout.CENTER);

        add(queuePanel, BorderLayout.CENTER);
    }

    private void selesaiButtonActionPerformed(PatientData patientData) {
        // Implementasi untuk menghapus data dari database
        try (Connection connection = koneksi.getConnection()) {
            String deleteQuery = "DELETE FROM apotek WHERE nama = ? AND usia = ? AND diagnosis = ? AND obat = ? AND pasien_darurat = ?";
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
            UnitTestGUI frame = new UnitTestGUI();
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
