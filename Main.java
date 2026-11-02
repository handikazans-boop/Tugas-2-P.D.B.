import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static NumberFormat nf =
            NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));

    static final String[] KATEGORI = {"Makanan", "Minuman"};
    static final int BIAYA_PELAYANAN = 20000;
    static final int PAJAK_PERSEN = 10;
    static final int DISKON_PERSEN = 10;
    static final int BATAS_DISKON = 100000;
    static final int BATAS_BOGO = 50000;

    // Data menu
    static Menu[] daftarMenu = new Menu[10];
    static int jumlahMenu = 0;

    // PROGRAM UTAMA 

    public static void main(String[] args) {
        isiMenuAwal();

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n========= APLIKASI RESTORAN =========");
            System.out.println("1. Menu Pelanggan (pesan makanan/minuman)");
            System.out.println("2. Menu Pemilik (kelola menu)");
            System.out.println("0. Keluar");
            int pilih = bacaPilihan("Pilih menu (0-2): ", 0, 2);

            switch (pilih) {
                case 1:
                    menuPelanggan();
                    break;
                case 2:
                    menuPemilik();
                    break;
                case 0:
                    System.out.println("Terima kasih, sampai jumpa!");
                    jalan = false;
                    break;
            }
        }
        scanner.close();
    }

    static void isiMenuAwal() {
        simpanMenu(new Menu("Nasi Katsu", 25000, "Makanan"));
        simpanMenu(new Menu("Nasi Goreng", 20000, "Makanan"));
        simpanMenu(new Menu("Ayam Bakar", 30000, "Makanan"));
        simpanMenu(new Menu("Rawon", 40000, "Makanan"));
        simpanMenu(new Menu("Lychee Tea", 12000, "Minuman"));
        simpanMenu(new Menu("Americano", 18000, "Minuman"));
        simpanMenu(new Menu("Matcha Latte", 18000, "Minuman"));
        simpanMenu(new Menu("Aren Latte", 22000, "Minuman"));
    }


    static int keAngka(String teks) {
        teks = teks.trim();
        if (teks.length() == 0 || teks.length() > 9) {
            return -1;
        }
        for (int i = 0; i < teks.length(); i++) {
            char c = teks.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
        }
        return Integer.parseInt(teks);
    }

    static int bacaPilihan(String prompt, int min, int max) {
        int angka;
        do {
            System.out.print(prompt);
            angka = keAngka(scanner.nextLine());
            if (angka < min || angka > max) {
                System.out.println("Input tidak valid. Masukkan angka "
                        + min + " sampai " + max + ".");
            }
        } while (angka < min || angka > max);
        return angka;
    }

    static boolean konfirmasi(String pertanyaan) {
        String jawab;
        do {
            System.out.print(pertanyaan + " (Ya/Tidak): ");
            jawab = scanner.nextLine().trim();
            if (!jawab.equalsIgnoreCase("Ya")
                    && !jawab.equalsIgnoreCase("Tidak")) {
                System.out.println("Input tidak valid. Ketik 'Ya' atau 'Tidak'.");
            }
        } while (!jawab.equalsIgnoreCase("Ya")
                && !jawab.equalsIgnoreCase("Tidak"));
        return jawab.equalsIgnoreCase("Ya");
    }

    static String rp(int nilai) {
        return "Rp " + nf.format(nilai);
    }


    static void simpanMenu(Menu menu) {
        if (jumlahMenu == daftarMenu.length) {
            Menu[] baru = new Menu[daftarMenu.length * 2];
            for (int i = 0; i < jumlahMenu; i++) {
                baru[i] = daftarMenu[i];
            }
            daftarMenu = baru;
        }
        daftarMenu[jumlahMenu] = menu;
        jumlahMenu++;
    }

    static int cariMenu(String nama) {
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].getNama().equalsIgnoreCase(nama)) {
                return i;
            }
        }
        return -1;
    }

    static void tampilkanMenu() {
        for (String kategori : KATEGORI) {
            System.out.println("\n___ MENU " + kategori.toUpperCase() + " ___");
            boolean ada = false;
            for (int i = 0; i < jumlahMenu; i++) {
                if (daftarMenu[i].getKategori().equals(kategori)) {
                    System.out.println((i + 1) + ". " + daftarMenu[i].getNama()
                            + " - " + rp(daftarMenu[i].getHarga()));
                    ada = true;
                }
            }
            if (!ada) {
                System.out.println("(belum ada menu)");
            }
        }
    }


    static void menuPelanggan() {
        if (jumlahMenu == 0) {
            System.out.println("\nMaaf, menu belum tersedia.");
            return;
        }

        Menu[] pesanan = new Menu[jumlahMenu];
        int[] jumlah = new int[jumlahMenu];
        int jumlahItem = 0;

        tampilkanMenu();
        System.out.println("\nKetik nomor menu untuk memesan.");
        System.out.println("Perintah lain: 'hapus' (hapus item pesanan), "
                + "'selesai' (bayar), 'kembali' (batal).");

        boolean selesai = false;
        while (!selesai) {
            System.out.print("\nPilih menu: ");
            String input = scanner.nextLine().trim();
            int nomor = keAngka(input);

            if (input.equalsIgnoreCase("selesai")) {
                if (jumlahItem == 0) {
                    System.out.println("Belum ada pesanan. Pilih menu dulu "
                            + "atau ketik 'kembali'.");
                } else {
                    selesai = true;
                }
            } else if (input.equalsIgnoreCase("kembali")) {
                System.out.println("Pesanan dibatalkan.");
                return;
            } else if (input.equalsIgnoreCase("hapus")) {
                jumlahItem = hapusItemPesanan(pesanan, jumlah, jumlahItem);
            } else if (nomor >= 1 && nomor <= jumlahMenu) {
                Menu dipilih = daftarMenu[nomor - 1];
                int qty = bacaPilihan("Jumlah " + dipilih.getNama() + ": ", 1, 99);

                int posisi = cariDiPesanan(dipilih, pesanan, jumlahItem);
                if (posisi >= 0) {
                    jumlah[posisi] += qty;
                } else {
                    pesanan[jumlahItem] = dipilih;
                    jumlah[jumlahItem] = qty;
                    jumlahItem++;
                }
                System.out.println("Ditambahkan: " + qty + " x " + dipilih.getNama());
            } else {
                System.out.println("Input tidak valid. Masukkan nomor menu "
                        + "(1-" + jumlahMenu + "), 'hapus', 'selesai', atau 'kembali'.");
            }
        }

        prosesPembayaran(pesanan, jumlah, jumlahItem);
    }

    static int cariDiPesanan(Menu menu, Menu[] pesanan, int jumlahItem) {
        for (int i = 0; i < jumlahItem; i++) {
            if (pesanan[i] == menu) {
                return i;
            }
        }
        return -1;
    }

    static int hapusItemPesanan(Menu[] pesanan, int[] jumlah, int jumlahItem) {
        if (jumlahItem == 0) {
            System.out.println("Belum ada pesanan yang bisa dihapus.");
            return jumlahItem;
        }

        System.out.println("\n--- Pesanan saat ini ---");
        for (int i = 0; i < jumlahItem; i++) {
            System.out.println((i + 1) + ". " + pesanan[i].getNama()
                    + " x " + jumlah[i]);
        }

        int nomor = bacaPilihan(
                "Nomor item yang dihapus (0 untuk batal): ", 0, jumlahItem);
        if (nomor == 0) {
            return jumlahItem;
        }

        String namaDihapus = pesanan[nomor - 1].getNama();
        for (int i = nomor - 1; i < jumlahItem - 1; i++) {
            pesanan[i] = pesanan[i + 1];
            jumlah[i] = jumlah[i + 1];
        }
        pesanan[jumlahItem - 1] = null;
        jumlah[jumlahItem - 1] = 0;

        System.out.println(namaDihapus + " dihapus dari pesanan.");
        return jumlahItem - 1;
    }


    static int hitungSubtotal(Menu[] pesanan, int[] jumlah, int jumlahItem) {
        int subtotal = 0;
        for (int i = 0; i < jumlahItem; i++) {
            subtotal += pesanan[i].getHarga() * jumlah[i];
        }
        return subtotal;
    }

    static int hitungDiskonBOGO(Menu[] pesanan, int[] jumlah, int jumlahItem) {
        int diskon = 0;
        for (int i = 0; i < jumlahItem; i++) {
            if (pesanan[i].getKategori().equals("Minuman")) {
                diskon += (jumlah[i] / 2) * pesanan[i].getHarga();
            }
        }
        return diskon;
    }

    static void prosesPembayaran(Menu[] pesanan, int[] jumlah, int jumlahItem) {
        int subtotal = hitungSubtotal(pesanan, jumlah, jumlahItem);

        int diskonBOGO = 0;
        if (subtotal > BATAS_BOGO) {
            diskonBOGO = hitungDiskonBOGO(pesanan, jumlah, jumlahItem);
        }
        int setelahBOGO = subtotal - diskonBOGO;

        int diskonPersen = 0;
        if (subtotal > BATAS_DISKON) {
            diskonPersen = setelahBOGO * DISKON_PERSEN / 100;
        }

        int setelahDiskon = setelahBOGO - diskonPersen;
        int pajak = setelahDiskon * PAJAK_PERSEN / 100;
        int totalAkhir = setelahDiskon + pajak + BIAYA_PELAYANAN;

        cetakStruk(pesanan, jumlah, jumlahItem, subtotal, diskonBOGO,
                diskonPersen, setelahDiskon, pajak, totalAkhir);
    }

    static void baris(String label, String nilai) {
        System.out.println(String.format("%-26s: %16s", label, nilai));
    }

    static void cetakStruk(Menu[] pesanan, int[] jumlah, int jumlahItem,
                           int subtotal, int diskonBOGO, int diskonPersen,
                           int setelahDiskon, int pajak, int totalAkhir) {

        System.out.println("\n============ STRUK PESANAN ============");

        for (int i = 0; i < jumlahItem; i++) {
            Menu m = pesanan[i];
            System.out.println((i + 1) + ". " + m.getNama()
                    + " (" + m.getKategori() + ")");
            System.out.println("   " + jumlah[i] + " x " + rp(m.getHarga())
                    + " = " + rp(m.getHarga() * jumlah[i]));
        }

        System.out.println("---------------------------------------");
        baris("Subtotal", rp(subtotal));

        if (subtotal > BATAS_BOGO) {
            if (diskonBOGO > 0) {
                baris("Promo BOGO minuman", "- " + rp(diskonBOGO));
                for (int i = 0; i < jumlahItem; i++) {
                    if (pesanan[i].getKategori().equals("Minuman")
                            && jumlah[i] / 2 > 0) {
                        System.out.println("   gratis " + (jumlah[i] / 2)
                                + " x " + pesanan[i].getNama());
                    }
                }
            } else {
                System.out.println("Promo BOGO aktif, tapi tidak ada minuman "
                        + "sejenis yang berpasangan.");
            }
        }

        if (diskonPersen > 0) {
            baris("Diskon " + DISKON_PERSEN + "% (> " + rp(BATAS_DISKON) + ")",
                    "- " + rp(diskonPersen));
        }

        if (diskonBOGO > 0 || diskonPersen > 0) {
            baris("Harga setelah diskon", rp(setelahDiskon));
        }

        baris("Pajak " + PAJAK_PERSEN + "%", rp(pajak));
        baris("Biaya pelayanan", rp(BIAYA_PELAYANAN));
        System.out.println("---------------------------------------");
        baris("TOTAL BAYAR", rp(totalAkhir));
        System.out.println("=======================================");
    }


    static void menuPemilik() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n===== PENGELOLAAN MENU (PEMILIK) =====");
            System.out.println("1. Lihat daftar menu");
            System.out.println("2. Tambah menu baru");
            System.out.println("3. Ubah harga menu");
            System.out.println("4. Hapus menu");
            System.out.println("0. Kembali");
            int pilih = bacaPilihan("Pilih menu (0-4): ", 0, 4);

            switch (pilih) {
                case 1:
                    tampilkanMenu();
                    break;
                case 2:
                    tambahMenuBaru();
                    break;
                case 3:
                    ubahHargaMenu();
                    break;
                case 4:
                    hapusMenu();
                    break;
                case 0:
                    kembali = true;
                    break;
            }
        }
    }

    static void tambahMenuBaru() {
        int banyak = bacaPilihan(
                "\nBerapa menu baru yang ingin ditambahkan (0 untuk batal)? ", 0, 20);

        for (int n = 1; n <= banyak; n++) {
            System.out.println("\n--- Menu baru ke-" + n + " dari " + banyak + " ---");

            String nama;
            boolean valid;
            do {
                System.out.print("Nama menu: ");
                nama = scanner.nextLine().trim();
                valid = nama.length() > 0 && cariMenu(nama) == -1;
                if (!valid) {
                    System.out.println("Nama tidak boleh kosong atau sama "
                            + "dengan menu yang sudah ada.");
                }
            } while (!valid);

            int k = bacaPilihan("Kategori (1. Makanan, 2. Minuman): ", 1, 2);
            int harga = bacaPilihan("Harga (Rp): ", 1, 1000000);

            simpanMenu(new Menu(nama, harga, KATEGORI[k - 1]));
            System.out.println("Menu '" + nama + "' berhasil ditambahkan.");
        }
    }

    static void ubahHargaMenu() {
        if (jumlahMenu == 0) {
            System.out.println("\nBelum ada menu.");
            return;
        }

        tampilkanMenu();
        int nomor = bacaPilihan(
                "\nNomor menu yang diubah harganya (0 untuk batal): ", 0, jumlahMenu);
        if (nomor == 0) {
            return;
        }

        Menu menu = daftarMenu[nomor - 1];
        int hargaBaru = bacaPilihan(
                "Harga baru untuk " + menu.getNama() + " (Rp): ", 1, 1000000);

        System.out.println("\nUbah harga " + menu.getNama() + " dari "
                + rp(menu.getHarga()) + " menjadi " + rp(hargaBaru) + "?");
        if (konfirmasi("Yakin")) {
            menu.setHarga(hargaBaru);
            System.out.println("Harga berhasil diubah.");
        } else {
            System.out.println("Perubahan dibatalkan.");
        }
    }

    static void hapusMenu() {
        if (jumlahMenu == 0) {
            System.out.println("\nBelum ada menu.");
            return;
        }

        tampilkanMenu();
        int nomor = bacaPilihan(
                "\nNomor menu yang dihapus (0 untuk batal): ", 0, jumlahMenu);
        if (nomor == 0) {
            return;
        }

        Menu menu = daftarMenu[nomor - 1];
        System.out.println("\nHapus menu " + menu.getNama()
                + " (" + rp(menu.getHarga()) + ")?");
        if (konfirmasi("Yakin")) {
            for (int i = nomor - 1; i < jumlahMenu - 1; i++) {
                daftarMenu[i] = daftarMenu[i + 1];
            }
            daftarMenu[jumlahMenu - 1] = null;
            jumlahMenu--;
            System.out.println("Menu berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}
