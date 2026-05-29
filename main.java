import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// =========================================================================
// 1. KELAS UTAMA (DRIVER PROGRAM) 
// =========================================================================
public class Main {
    public static void main(String[] args) {
        Menu manajemenMenu = new Menu();
        manajemenMenu.muatMenuDariFile(); // Otomatis memuat data menu dari file jika ada
        
        Pesanan pesananAktif = new Pesanan();
        // Menggunakan Locale.US agar input desimal diskon menggunakan titik (.) tidak error
        Scanner input = new Scanner(System.in).useLocale(java.util.Locale.US);
        boolean berjalan = true;

        while (berjalan) {
            System.out.println("\n=== PROGRAM MANAJEMEN RESTORAN ===");
            System.out.println("1. Tambah Item Baru ke Menu");
            System.out.println("2. Tampilkan Menu Restoran");
            System.out.println("3. Terima/Tambah Pesanan Pelanggan (Bisa Banyak Menu)");
            System.out.println("4. Hitung Total & Cetak Struk");
            System.out.println("5. Keluar dari Program");
            System.out.print("Pilih Menu Opsi (1-5): ");
            
            int pilihan = input.nextInt();
            input.nextLine(); // Membersihkan buffer input

            switch (pilihan) {
                case 1:
                    System.out.println("\n--- TAMBAH ITEM MENU ---");
                    System.out.print("Pilih Kategori (1. Makanan, 2. Minuman, 3. Diskon): ");
                    int jenis = input.nextInt();
                    input.nextLine();
                    
                    System.out.print("Masukkan Nama Menu/Promo: ");
                    String nama = input.nextLine();

                    if (jenis == 1) {
                        System.out.print("Masukkan Harga Jual: ");
                        double harga = input.nextDouble();
                        input.nextLine();
                        System.out.print("Masukkan Jenis Makanan (contoh: Kuah/Goreng): ");
                        String jenisMak = input.nextLine();
                        manajemenMenu.tambahItem(new Makanan(nama, harga, jenisMak));
                    } else if (jenis == 2) {
                        System.out.print("Masukkan Harga Jual: ");
                        double harga = input.nextDouble();
                        input.nextLine();
                        System.out.print("Masukkan Jenis Minuman (contoh: Dingin/Panas): ");
                        String jenisMin = input.nextLine();
                        manajemenMenu.tambahItem(new Minuman(nama, harga, jenisMin));
                    } else if (jenis == 3) {
                        System.out.print("Masukkan Besar Diskon (Contoh 0.10 untuk 10%): ");
                        double diskon = input.nextDouble();
                        manajemenMenu.tambahItem(new Diskon(nama, diskon));
                    } else {
                        System.out.println("Pilihan kategori tidak valid.");
                    }
                    manajemenMenu.simpanMenuKeFile(); // Simpan database terbaru ke menu.txt
                    break;

                case 2:
                    manajemenMenu.tampilkanSemuaMenu();
                    break;

                case 3:
                    System.out.println("\n--- INPUT PESANAN RESTORAN ---");
                    boolean lanjutPesan = true;
                    
                    while (lanjutPesan) {
                        System.out.print("Ketikkan nama menu/diskon (atau ketik 'selesai' untuk kembali): ");
                        String namaPesanan = input.nextLine();
                        
                        // Jika user mengetik 'selesai', maka perulangan pesanan berhenti
                        if (namaPesanan.equalsIgnoreCase("selesai")) {
                            lanjutPesan = false;
                            System.out.println("Pencatatan pesanan selesai. Silakan pilih Opsi 4 untuk cetak struk.");
                            break;
                        }
                        
                        // Penerapan Exception Handling untuk mencari menu
                        try {
                            MenuItem itemDitemukan = manajemenMenu.cariItem(namaPesanan);
                            pesananAktif.tambahPesanan(itemDitemukan);
                        } catch (MenuNotFoundException e) {
                            System.out.println("[ERROR] " + e.getMessage()); 
                        }
                        System.out.println("----------------------------------------------");
                    }
                    break;

                case 4:
                    pesananAktif.cetakDanSimpanStruk();
                    pesananAktif = new Pesanan(); // Reset objek pesanan untuk transaksi berikutnya
                    break;

                case 5:
                    berjalan = false;
                    System.out.println("Keluar dari program. Terima kasih!");
                    break;

                default:
                    System.out.println("Opsi tidak tersedia. Silakan masukkan angka 1-5.");
            }
        }
        input.close();
    }
}

// =========================================================================
// 2. KELAS ABSTRAK INDUK & SUB-KELAS 
// =========================================================================
abstract class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nama;
    private double harga;
    private String kategori;

    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public abstract void tampilMenu();
}

class Makanan extends MenuItem {
    private String jenisMakanan; 

    public Makanan(String nama, double harga, String jenisMakanan) {
        super(nama, harga, "Makanan");
        this.jenisMakanan = jenisMakanan;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[%s] %-15s - Rp%,.2f (%s)\n", getKategori(), getNama(), getHarga(), jenisMakanan);
    }
}

class Minuman extends MenuItem {
    private String jenisMinuman; 

    public Minuman(String nama, double harga, String jenisMinuman) {
        super(nama, harga, "Minuman");
        this.jenisMinuman = jenisMinuman;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[%s] %-15s - Rp%,.2f (%s)\n", getKategori(), getNama(), getHarga(), jenisMinuman);
    }
}

class Diskon extends MenuItem {
    private double besarDiskon; 

    public Diskon(String nama, double besarDiskon) {
        super(nama, 0, "Diskon"); 
        this.besarDiskon = besarDiskon;
    }

    public double getBesarDiskon() { return besarDiskon; }

    @Override
    public void tampilMenu() {
        System.out.printf("[%s] %-15s - Potongan: %.0f%%\n", getKategori(), getNama(), (besarDiskon * 100));
    }
}

// =========================================================================
// 3. KELAS PENANGANAN ERROR KUSTOM 
// =========================================================================
class MenuNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public MenuNotFoundException(String pesan) {
        super(pesan);
    }
}

// =========================================================================
// 4. KELAS MANAJEMEN DATA MENU RESTORAN
// =========================================================================
class Menu {
    private ArrayList<MenuItem> daftarMenu;

    public Menu() {
        this.daftarMenu = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        daftarMenu.add(item);
        System.out.println("Item '" + item.getNama() + "' berhasil dimasukkan ke daftar menu.");
    }

    public void tampilkanSemuaMenu() {
        if (daftarMenu.isEmpty()) {
            System.out.println("Menu restoran kosong. Silakan tambah menu terlebih dahulu.");
            return;
        }
        System.out.println("\n=== DAFTAR MENU RESTORAN ===");
        for (MenuItem item : daftarMenu) {
            item.tampilMenu(); 
        }
    }

    public MenuItem cariItem(String nama) throws MenuNotFoundException {
        for (MenuItem item : daftarMenu) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        throw new MenuNotFoundException("Menu '" + nama + "' tidak ditemukan dalam daftar restoran!");
    }

    public void simpanMenuKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("menu.txt"))) {
            oos.writeObject(daftarMenu);
            System.out.println("[Sistem] Data menu terbaru berhasil dicadangkan ke 'menu.txt'.");
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data ke file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void muatMenuDariFile() {
        File file = new File("menu.txt");
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            daftarMenu = (ArrayList<MenuItem>) ois.readObject();
            System.out.println("[Sistem] Database menu lama berhasil dimuat dari 'menu.txt'.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Gagal memuat data dari file: " + e.getMessage());
        }
    }
}

// =========================================================================
// 5. KELAS TRANSAKSI PESANAN PELANGGAN
// =========================================================================
class Pesanan {
    private ArrayList<MenuItem> itemPesanan;

    public Pesanan() {
        this.itemPesanan = new ArrayList<>();
    }

    public void tambahPesanan(MenuItem item) {
        itemPesanan.add(item);
        System.out.println("'" + item.getNama() + "' dimasukkan ke keranjang belanja.");
    }

    public double hitungTotal() {
        double totalHarga = 0;
        double totalDiskon = 0;

        for (MenuItem item : itemPesanan) {
            if (item instanceof Diskon) {
                totalDiskon += ((Diskon) item).getBesarDiskon();
            } else {
                totalHarga += item.getHarga();
            }
        }
        return totalHarga - (totalHarga * totalDiskon);
    }

    public void cetakDanSimpanStruk() {
        if (itemPesanan.isEmpty()) {
            System.out.println("Keranjang pesanan masih kosong.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n==================================\n");
        sb.append("          STRUK RESTORAN          \n");
        sb.append("==================================\n");
        
        double totalMurni = 0;
        double diskonPersen = 0;

        for (MenuItem item : itemPesanan) {
            if (item instanceof Diskon) {
                diskonPersen += ((Diskon) item).getBesarDiskon();
                sb.append(String.format("%-20s : Potongan %.0f%%\n", item.getNama(), ((Diskon) item).getBesarDiskon() * 100));
            } else {
                totalMurni += item.getHarga();
                sb.append(String.format("%-20s : Rp%,.2f\n", item.getNama(), item.getHarga()));
            }
        }

        double totalAkhir = totalMurni - (totalMurni * diskonPersen);
        sb.append("----------------------------------\n");
        sb.append(String.format("%-20s : Rp%,.2f\n", "Total Bayar", totalAkhir));
        sb.append("==================================\n");

        System.out.print(sb.toString());

        try (PrintWriter writer = new PrintWriter(new FileWriter("struk.txt"))) {
            writer.print(sb.toString());
            System.out.println("[Sistem] Berkas cetak struk eksternal berhasil disimpan ke 'struk.txt'.");
        } catch (IOException e) {
            System.err.println("Gagal menyimpan file struk: " + e.getMessage());
        }
    }
}
