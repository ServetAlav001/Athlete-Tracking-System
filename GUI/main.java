
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// Ana Uygulama Sınıfı (Main Class)
class SporcuTakipGUI extends JFrame {

    private final SporcuYonetimi yonetim;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    public SporcuTakipGUI() {
        // --- 1. Uygulama Başlangıcı ve DB Kontrolü ---
        this.yonetim = new SporcuYonetimi();
        try {
            yonetim.getDbYonetimi().baglantiKur();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Veritabanı bağlantısı kurulamadı. Lütfen ayarları kontrol edin. Hata: " + e.getMessage(),
                    "Kritik Hata",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Sporcu Takip Sistemi (GUI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 2. CardLayout İçerik Alanı ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // Panelleri oluştur ve CardLayout'a ekle
        contentPanel.add(new KayitFormuPanel(yonetim), "KAYIT");
        contentPanel.add(new SporcuListelePanel(yonetim), "LISTE");
        contentPanel.add(new YonetimPanel(yonetim), "YONETIM");
        contentPanel.add(new RaporPanel(yonetim), "RAPOR");

        // --- 3. Sol Menüyü Oluşturma ---
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout.show(contentPanel, "KAYIT");
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(187, 146, 146));

        JButton btnKayit = createMenuButton("➕ SPORCU EKLE", "KAYIT");
        JButton btnListe = createMenuButton("👥 SPORCU LİSTESİ", "LISTE");
        JButton btnYonetim = createMenuButton("🛠️ KAYIT YÖNETİMİ", "YONETIM");
        JButton btnFinans = createMenuButton("💰 FİNANS/RAPOR", "RAPOR");
        JButton btnCikis = new JButton("🚪 ÇIKIŞ (q)");
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setBackground(new Color(150, 0, 0));
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> System.exit(0));

        panel.add(btnKayit);
        panel.add(btnListe);
        panel.add(btnYonetim);
        panel.add(btnFinans);
        panel.add(new JLabel(""));
        panel.add(btnCikis);

        return panel;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setFocusPainted(false);
        button.addActionListener((ActionEvent e) -> cardLayout.show(contentPanel, cardName));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SporcuTakipGUI().setVisible(true);
        });
    }

    // =====================================================================
    // TEK DOSYADA BIRLEŞTİRİLMİŞ İÇ İÇE SINIFLAR (Nested Classes)
    // =====================================================================

    // A. SPORCU VERİ SINIFI (Sporcular.java)
    public static class Sporcular {
        private int id;
        private String isim;
        private String soyad;
        private long telefonNo;
        private int kayitUcreti;
        private int kayitSuresiAy;
        private LocalDateTime kayitTarihi;

        // Tam constructor (Veritabanından okumak için)
        public Sporcular(int id, String isim, String soyad, long telefonNo, int kayitUcreti, int kayitSuresiAy, LocalDateTime kayitTarihi) {
            this.id = id;
            this.isim = isim;
            this.soyad = soyad;
            this.telefonNo = telefonNo;
            this.kayitUcreti = kayitUcreti;
            this.kayitSuresiAy = kayitSuresiAy;
            this.kayitTarihi = kayitTarihi;
        }

        // Yeni sporcu kaydı için constructor
        public Sporcular(String isim, String soyad, long telefonNo, int kayitUcreti, int kayitSuresiAy, LocalDateTime kayitTarihi) {
            this(0, isim, soyad, telefonNo, kayitUcreti, kayitSuresiAy, kayitTarihi); // ID 0 olarak ayarlanır
        }

        // Getter ve Setter Metotları
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getIsim() { return isim; }
        public String getSoyad() { return soyad; }
        public long getTelefonNo() { return telefonNo; }
        public void setTelefonNo(long telefonNo) { this.telefonNo = telefonNo; }
        public int getKayitUcreti() { return kayitUcreti; }
        public void setKayitUcreti(int kayitUcreti) { this.kayitUcreti = kayitUcreti; }
        public int getKayitSuresiAy() { return kayitSuresiAy; }
        public void setKayitSuresiAy(int kayitSuresiAy) { this.kayitSuresiAy = kayitSuresiAy; }
        public LocalDateTime getKayitTarihi() { return kayitTarihi; }
        public void setKayitTarihi(LocalDateTime kayitTarihi) { this.kayitTarihi = kayitTarihi; }

        @Override
        public String toString() {
            // ComboBox'ta gösterilecek format
            return String.format("ID: %d - %s %s (Tel: %d)",
                    id, isim, soyad, telefonNo);
        }
    }

    // B. VERİTABANI YÖNETİMİ SINIFI (DByonetimi.java)
    public static class DByonetimi {
        private static final String URL = "jdbc:mysql://localhost:3306/SporcuVeritabani";
        private static final String USER = "root";
        private static final String PASSWORD = "Servet.alav2001";

        public Connection baglantiKur() {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Veritabanı bağlantısı başarılı.");
            } catch (SQLException e) {
                System.out.println("❌ Veritabanı bağlantısı sağlanamadı. Hata: " + e.getMessage());
                throw new RuntimeException("Veritabanı bağlantı hatası!", e);
            }
            return connection;
        }

        // CREATE
        public boolean sporcuEkle(Sporcular sporcu) {
            String sql = "INSERT INTO Sporcular (isim, soyad, telefon_no, kayit_ucreti, kayit_suresi, kayit_tarihi) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = baglantiKur(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, sporcu.getIsim());
                pstmt.setString(2, sporcu.getSoyad());
                pstmt.setLong(3, sporcu.getTelefonNo());
                pstmt.setInt(4, sporcu.getKayitUcreti());
                pstmt.setInt(5, sporcu.getKayitSuresiAy());
                pstmt.setTimestamp(6, Timestamp.valueOf(sporcu.getKayitTarihi()));
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("❌ Sporcu eklenirken SQL hatası oluştu: " + e.getMessage());
                return false;
            }
        }

        // READ ALL
        public List<Sporcular> tumSporculariGetir() {
            List<Sporcular> sporcular = new ArrayList<>();
            String sql = "SELECT * FROM Sporcular ORDER BY id";
            try (Connection conn = baglantiKur(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    sporcular.add(new Sporcular(
                            rs.getInt("id"), rs.getString("isim"), rs.getString("soyad"),
                            rs.getLong("telefon_no"), rs.getInt("kayit_ucreti"),
                            rs.getInt("kayit_suresi"), rs.getTimestamp("kayit_tarihi").toLocalDateTime()
                    ));
                }
            } catch (SQLException e) {
                System.out.println("❌ Sporcular listelenirken SQL hatası oluştu: " + e.getMessage());
            }
            return sporcular;
        }

        // READ by ISIM/SOYAD
        public List<Sporcular> sporcuKontrol(String isim, String soyad) {
            List<Sporcular> bulunanlar = new ArrayList<>();
            String sql = "SELECT * FROM Sporcular WHERE isim = ? AND soyad = ?";
            try (Connection conn = baglantiKur(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isim);
                pstmt.setString(2, soyad);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bulunanlar.add(new Sporcular(
                                rs.getInt("id"), rs.getString("isim"), rs.getString("soyad"),
                                rs.getLong("telefon_no"), rs.getInt("kayit_ucreti"),
                                rs.getInt("kayit_suresi"), rs.getTimestamp("kayit_tarihi").toLocalDateTime()
                        ));
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Sporcu kontrol edilirken SQL hatası oluştu: " + e.getMessage());
            }
            return bulunanlar;
        }

        // READ by ID
        public Sporcular sporcuGetirById(int id) {
            String sql = "SELECT * FROM Sporcular WHERE id = ?";
            try (Connection conn = baglantiKur(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Sporcular(
                                rs.getInt("id"), rs.getString("isim"), rs.getString("soyad"),
                                rs.getLong("telefon_no"), rs.getInt("kayit_ucreti"),
                                rs.getInt("kayit_suresi"), rs.getTimestamp("kayit_tarihi").toLocalDateTime()
                        );
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ ID ile sporcu getirilirken SQL hatası oluştu: " + e.getMessage());
            }
            return null;
        }

        // UPDATE
        public boolean sporcuGuncelle(Sporcular sporcu) {
            String sql = "UPDATE Sporcular SET isim = ?, soyad = ?, telefon_no = ?, kayit_ucreti = ?, kayit_suresi = ?, kayit_tarihi = ? WHERE id = ?";
            try (Connection conn = baglantiKur(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, sporcu.getIsim());
                pstmt.setString(2, sporcu.getSoyad());
                pstmt.setLong(3, sporcu.getTelefonNo());
                pstmt.setInt(4, sporcu.getKayitUcreti());
                pstmt.setInt(5, sporcu.getKayitSuresiAy());
                pstmt.setTimestamp(6, Timestamp.valueOf(sporcu.getKayitTarihi()));
                pstmt.setInt(7, sporcu.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("❌ Sporcu güncellenirken SQL hatası oluştu: " + e.getMessage());
                return false;
            }
        }

        // DELETE
        public boolean sporcuSil(String isim, String soyad) {
            String sql = "DELETE FROM Sporcular WHERE isim = ? AND soyad = ?";
            try (Connection conn = baglantiKur(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isim);
                pstmt.setString(2, soyad);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("❌ Sporcu silinirken SQL hatası oluştu: " + e.getMessage());
                return false;
            }
        }
    }

    // C. İŞ MANTIĞI YÖNETİM SINIFI (SporcuYonetimi.java)
    public static class SporcuYonetimi {
        private final DByonetimi dbYonetimi;

        public DByonetimi getDbYonetimi() { return dbYonetimi; }

        public SporcuYonetimi() { this.dbYonetimi = new DByonetimi(); }

        public boolean sporcuEkle(Sporcular sporcu) {
            if (!dbYonetimi.sporcuKontrol(sporcu.getIsim(), sporcu.getSoyad()).isEmpty()) {
                System.out.println("bu isim ve soyada sahip sporcu zaten kayitli...");
                return false;
            }
            return dbYonetimi.sporcuEkle(sporcu);
        }

        public boolean sporcuSil(String isim, String soyad) {
            return dbYonetimi.sporcuSil(isim, soyad);
        }

        public boolean sporcuGuncelle(Sporcular sporcu) {
            return dbYonetimi.sporcuGuncelle(sporcu);
        }

        public List<Sporcular> sporcuAra(int id, String isim, String soyad) {
            if (id > 0) {
                Sporcular s = dbYonetimi.sporcuGetirById(id);
                if (s != null) {
                    return List.of(s);
                }
                return new ArrayList<>();
            } else if (!isim.isEmpty() && !soyad.isEmpty()) {
                return dbYonetimi.sporcuKontrol(isim, soyad);
            }
            return new ArrayList<>();
        }
    }

    // D. KAYIT FORMU PANELİ (KayitFormuPanel.java)
    public class KayitFormuPanel extends JPanel {
        private final SporcuYonetimi yonetim;
        private JTextField txtIsim, txtSoyad, txtTelefonNo, txtKayitUcreti, txtKayitSuresi, txtYil, txtAy, txtGun;

        public KayitFormuPanel(SporcuYonetimi yonetim) {
            this.yonetim = yonetim;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Sporcu Kayıt Formu"));

            txtIsim = addField(formPanel, "İsim:");
            txtSoyad = addField(formPanel, "Soyad:");
            txtTelefonNo = addField(formPanel, "Telefon No:");
            txtKayitUcreti = addField(formPanel, "Kayıt Ücreti (TL):");
            txtKayitSuresi = addField(formPanel, "Kayıt Süresi (Ay):");

            JPanel tarihPanel = new JPanel(new GridLayout(1, 6, 5, 0));
            tarihPanel.add(new JLabel("Yıl:"));
            txtYil = new JTextField(4);
            tarihPanel.add(txtYil);
            tarihPanel.add(new JLabel("Ay:"));
            txtAy = new JTextField(2);
            tarihPanel.add(txtAy);
            tarihPanel.add(new JLabel("Gün:"));
            txtGun = new JTextField(2);
            tarihPanel.add(txtGun);

            formPanel.add(new JLabel("Kayıt Tarihi:"));
            formPanel.add(tarihPanel);

            JButton btnKaydet = new JButton("SPORCUYU KAYDET");
            btnKaydet.addActionListener(e -> sporcuKaydet());

            add(new JLabel("SPORCU KAYIT EKRANI", SwingConstants.CENTER), BorderLayout.NORTH);
            add(formPanel, BorderLayout.CENTER);
            add(btnKaydet, BorderLayout.SOUTH);
        }

        private JTextField addField(JPanel panel, String labelText) {
            panel.add(new JLabel(labelText));
            JTextField field = new JTextField(20);
            panel.add(field);
            return field;
        }

        private void sporcuKaydet() {
            try {
                String isim = txtIsim.getText().trim();
                String soyad = txtSoyad.getText().trim();
                long tlfNo = Long.parseLong(txtTelefonNo.getText().trim());
                int ucret = Integer.parseInt(txtKayitUcreti.getText().trim());
                int sure = Integer.parseInt(txtKayitSuresi.getText().trim());

                int yil = Integer.parseInt(txtYil.getText().trim());
                int ay = Integer.parseInt(txtAy.getText().trim());
                int gun = Integer.parseInt(txtGun.getText().trim());

                LocalDateTime tarih = LocalDateTime.of(yil, ay, gun, 0, 0);

                Sporcular yeniSporcu = new Sporcular(isim, soyad, tlfNo, ucret, sure, tarih);

                if (yonetim.sporcuEkle(yeniSporcu)) {
                    JOptionPane.showMessageDialog(this, "Sporcu başarıyla kaydedildi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Kayıt hatası veya bu isim/soyadda sporcu zaten mevcut!", "Hata", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen sayısal alanlara geçerli değerler girin.", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeException ex) {
                JOptionPane.showMessageDialog(this, "Tarih bilgileri (Yıl/Ay/Gün) geçerli değil.", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Beklenmeyen bir hata oluştu: " + ex.getMessage(), "Genel Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearForm() {
            txtIsim.setText("");
            txtSoyad.setText("");
            txtTelefonNo.setText("");
            txtKayitUcreti.setText("");
            txtKayitSuresi.setText("");
            txtYil.setText("");
            txtAy.setText("");
            txtGun.setText("");
        }
    }

    // E. LİSTELEME PANELİ (SporcuListelePanel.java)
    public class SporcuListelePanel extends JPanel {
        private final SporcuYonetimi yonetim;
        private JTable sporcuTable;
        private DefaultTableModel tableModel;

        public SporcuListelePanel(SporcuYonetimi yonetim) {
            this.yonetim = yonetim;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("👥 KAYITLI SPORCULAR LİSTESİ", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(titleLabel, BorderLayout.NORTH);

            String[] columnNames = {"ID", "İsim", "Soyad", "Telefon No", "Kayıt Ücreti", "Süre (Ay)", "Kayıt Tarihi"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            sporcuTable = new JTable(tableModel);
            sporcuTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(sporcuTable);
            add(scrollPane, BorderLayout.CENTER);

            JButton refreshButton = new JButton("🔄 Listeyi Yenile");
            refreshButton.addActionListener(e -> populateTable());

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controlPanel.add(refreshButton);
            add(controlPanel, BorderLayout.SOUTH);

            populateTable();
        }

        private void populateTable() {
            tableModel.setRowCount(0);
            List<Sporcular> sporcuList = yonetim.getDbYonetimi().tumSporculariGetir();

            for (Sporcular s : sporcuList) {
                Object[] rowData = {
                        s.getId(), s.getIsim(), s.getSoyad(), s.getTelefonNo(),
                        s.getKayitUcreti() + " TL", s.getKayitSuresiAy() + " Ay",
                        s.getKayitTarihi().toLocalDate().toString()
                };
                tableModel.addRow(rowData);
            }

            if (sporcuList.isEmpty()) {
                System.out.println("Kayıtlı sporcu yok.");
            }
        }
    }

    // F. RAPOR PANELİ (RaporPanel.java)
    public class RaporPanel extends JPanel {
        private final SporcuYonetimi yonetim;
        private JLabel lblToplamGelir;
        private JTextArea txtKontrolSonuclari;

        public RaporPanel(SporcuYonetimi yonetim) {
            this.yonetim = yonetim;
            setLayout(new BorderLayout(20, 20));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel gelirPanel = createGelirPanel();
            add(gelirPanel, BorderLayout.NORTH);

            JPanel kontrolPanel = createKontrolPanel();
            add(kontrolPanel, BorderLayout.CENTER);

            updateData();
        }

        private JPanel createGelirPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBorder(BorderFactory.createTitledBorder("TOPLAM FİNANSAL DURUM"));
            panel.setBackground(new Color(230, 255, 230));

            JLabel baslik = new JLabel("TOPLAM GELİR:");
            baslik.setFont(new Font("Arial", Font.BOLD, 20));

            lblToplamGelir = new JLabel("Hesaplanıyor...");
            lblToplamGelir.setFont(new Font("Arial", Font.BOLD, 36));
            lblToplamGelir.setForeground(new Color(0, 150, 0));

            panel.add(baslik);
            panel.add(lblToplamGelir);
            return panel;
        }

        private JPanel createKontrolPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("KAYIT SÜRESİ KONTROLÜ"));

            txtKontrolSonuclari = new JTextArea();
            txtKontrolSonuclari.setEditable(false);
            txtKontrolSonuclari.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scroll = new JScrollPane(txtKontrolSonuclari);
            panel.add(scroll, BorderLayout.CENTER);

            JButton refreshButton = new JButton("Yenile ve Tüm Süreleri Kontrol Et");
            refreshButton.addActionListener(e -> updateControlResults());
            panel.add(refreshButton, BorderLayout.SOUTH);

            return panel;
        }

        private void updateData() {
            updateGelir();
            updateControlResults();
        }

        private void updateGelir() {
            List<Sporcular> sporcular = yonetim.getDbYonetimi().tumSporculariGetir();
            int toplam = 0;
            for (Sporcular s : sporcular) {
                toplam += s.getKayitUcreti();
            }
            lblToplamGelir.setText(String.format("%,d TL", toplam));
        }

        private void updateControlResults() {
            txtKontrolSonuclari.setText("");
            List<Sporcular> sporcular = yonetim.getDbYonetimi().tumSporculariGetir();
            LocalDateTime bugun = LocalDateTime.now();
            StringBuilder sb = new StringBuilder();

            sb.append("--- TÜM SPORCULAR İÇİN KAYIT SÜRESİ RAPORU ---\n\n");

            if (sporcular.isEmpty()) {
                sb.append("Hiç kayıtlı sporcu bulunmamaktadır.\n");
            } else {
                for (Sporcular s : sporcular) {
                    LocalDateTime bitisTarihi = s.getKayitTarihi().plusMonths(s.getKayitSuresiAy());
                    Duration duration = Duration.between(bugun.toLocalDate().atStartOfDay(), bitisTarihi.toLocalDate().atStartOfDay());
                    long farkGun = duration.toDays();

                    String durum;
                    if (farkGun > 7) {
                        durum = "✅ Aktif. Kalan Gün: " + farkGun;
                    } else if (farkGun > 0) {
                        durum = "⚠️ BİTİŞ YAKIN! Kalan Gün: " + farkGun;
                    } else if (farkGun == 0) {
                        durum = "🛑 BUGÜN SON GÜN!";
                    } else {
                        durum = "❌ SÜRESİ DOLDU! (" + Math.abs(farkGun) + " gün önce)";
                    }

                    sb.append(String.format("ID: %-4d %-20s Bitiş: %s | Durum: %s\n",
                            s.getId(), s.getIsim() + " " + s.getSoyad(), bitisTarihi.toLocalDate(), durum));
                }
            }
            txtKontrolSonuclari.setText(sb.toString());
        }
    }

    // G. YÖNETİM PANELİ (YonetimPanel.java)
    public class YonetimPanel extends JPanel {
        private final SporcuYonetimi yonetim;
        private JTextField txtAramaId, txtAramaIsim, txtAramaSoyad;
        private JComboBox<Sporcular> cmbSonucListesi;
        private JTextField txtGuncelTelefonNo, txtGuncelUcret, txtGuncelSure, txtGuncelTarih;
        private JButton btnGuncelle, btnSil;

        public YonetimPanel(SporcuYonetimi yonetim) {
            this.yonetim = yonetim;
            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setResizeWeight(0.3);

            splitPane.setLeftComponent(createAramaVeSonucPanel());
            splitPane.setRightComponent(createGuncellemeFormuPanel());

            add(new JLabel("🛠️ KAYIT YÖNETİMİ VE GÜNCELLEME", SwingConstants.CENTER), BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);

            setFormEnabled(false);
        }

        private JPanel createAramaVeSonucPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Sporcu Ara ve Seç"));

            JPanel aramaForm = new JPanel(new GridLayout(4, 2, 5, 5));
            txtAramaId = new JTextField(5);
            txtAramaIsim = new JTextField(15);
            txtAramaSoyad = new JTextField(15);
            JButton btnAra = new JButton("Ara");
            btnAra.addActionListener(e -> sporcuAra());

            aramaForm.add(new JLabel("ID ile Ara:"));
            aramaForm.add(txtAramaId);
            aramaForm.add(new JLabel("İsim:"));
            aramaForm.add(txtAramaIsim);
            aramaForm.add(new JLabel("Soyad:"));
            aramaForm.add(txtAramaSoyad);
            aramaForm.add(new JLabel(""));
            aramaForm.add(btnAra);

            panel.add(aramaForm, BorderLayout.NORTH);

            cmbSonucListesi = new JComboBox<>();
            cmbSonucListesi.addActionListener(e -> sporcuSecildi());
            panel.add(new JScrollPane(cmbSonucListesi), BorderLayout.CENTER);

            btnSil = new JButton("🗑️ SEÇİLİ SPORCUYU SİL");
            btnSil.setForeground(Color.RED);
            btnSil.addActionListener(e -> sporcuSil());
            panel.add(btnSil, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createGuncellemeFormuPanel() {
            JPanel form = new JPanel(new BorderLayout(10, 10));
            JPanel grid = new JPanel(new GridLayout(6, 2, 10, 10));

            grid.setBorder(BorderFactory.createTitledBorder("Sporcu Bilgilerini Güncelle"));

            txtGuncelTelefonNo = addFormRow(grid, "Telefon No:", true);
            txtGuncelUcret = addFormRow(grid, "Kayıt Ücreti (TL):", true);
            txtGuncelSure = addFormRow(grid, "Kayıt Süresi (Ay):", true);
            txtGuncelTarih = addFormRow(grid, "Kayıt Tarihi (YYYY-MM-DDTHH:MM):", false);

            btnGuncelle = new JButton("💾 KAYDI GÜNCELLE");
            btnGuncelle.setFont(new Font("Arial", Font.BOLD, 14));
            btnGuncelle.addActionListener(e -> sporcuGuncelle());

            form.add(grid, BorderLayout.CENTER);
            form.add(btnGuncelle, BorderLayout.SOUTH);

            return form;
        }

        private JTextField addFormRow(JPanel panel, String labelText, boolean editable) {
            panel.add(new JLabel(labelText));
            JTextField field = new JTextField(20);
            field.setEditable(editable);
            panel.add(field);
            return field;
        }

        private void sporcuAra() {
            cmbSonucListesi.removeAllItems();
            setFormEnabled(false);

            int id = -1;
            try {
                if (!txtAramaId.getText().trim().isEmpty()) {
                    id = Integer.parseInt(txtAramaId.getText().trim());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen geçerli bir ID girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String isim = txtAramaIsim.getText().trim();
            String soyad = txtAramaSoyad.getText().trim();

            List<Sporcular> bulunanlar = yonetim.sporcuAra(id, isim, soyad);

            if (bulunanlar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kayıt bulunamadı.", "Sonuç Yok", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Sporcular s : bulunanlar) {
                    cmbSonucListesi.addItem(s);
                }
                cmbSonucListesi.setSelectedIndex(0);
            }
        }

        private void sporcuSecildi() {
            Sporcular seciliSporcu = (Sporcular) cmbSonucListesi.getSelectedItem();
            if (seciliSporcu != null) {
                setFormEnabled(true);
                txtGuncelTelefonNo.setText(String.valueOf(seciliSporcu.getTelefonNo()));
                txtGuncelUcret.setText(String.valueOf(seciliSporcu.getKayitUcreti()));
                txtGuncelSure.setText(String.valueOf(seciliSporcu.getKayitSuresiAy()));
                txtGuncelTarih.setText(seciliSporcu.getKayitTarihi().toString().substring(0, 16));
            } else {
                setFormEnabled(false);
            }
        }

        private void sporcuGuncelle() {
            Sporcular seciliSporcu = (Sporcular) cmbSonucListesi.getSelectedItem();
            if (seciliSporcu == null) return;

            try {
                // Sadece değiştirilebilen alanları al ve set et
                long yeniTlfNo = Long.parseLong(txtGuncelTelefonNo.getText().trim());
                int yeniUcret = Integer.parseInt(txtGuncelUcret.getText().trim());
                int yeniSure = Integer.parseInt(txtGuncelSure.getText().trim());

                seciliSporcu.setTelefonNo(yeniTlfNo);
                seciliSporcu.setKayitUcreti(yeniUcret);
                seciliSporcu.setKayitSuresiAy(yeniSure);

                if (yonetim.sporcuGuncelle(seciliSporcu)) {
                    JOptionPane.showMessageDialog(this, seciliSporcu.getIsim() + " başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    sporcuAra(); // Arama sonuçlarını yenile
                } else {
                    JOptionPane.showMessageDialog(this, "Güncelleme hatası oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen sayısal alanları doğru formatta girin.", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void sporcuSil() {
            Sporcular seciliSporcu = (Sporcular) cmbSonucListesi.getSelectedItem();
            if (seciliSporcu == null) return;

            int onay = JOptionPane.showConfirmDialog(this,
                    seciliSporcu.getIsim() + " " + seciliSporcu.getSoyad() + " adlı sporcuyu silmek istediğinizden emin misiniz?",
                    "Silme Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (onay == JOptionPane.YES_OPTION) {
                if (yonetim.sporcuSil(seciliSporcu.getIsim(), seciliSporcu.getSoyad())) {
                    JOptionPane.showMessageDialog(this, "Kayıt başarıyla silindi.", "Silme Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    sporcuAra();
                } else {
                    JOptionPane.showMessageDialog(this, "Silme işlemi başarısız oldu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void setFormEnabled(boolean enabled) {
            txtGuncelTelefonNo.setEditable(enabled);
            txtGuncelUcret.setEditable(enabled);
            txtGuncelSure.setEditable(enabled);
            btnGuncelle.setEnabled(enabled);
            btnSil.setEnabled(enabled);
        }
    }
}
