# Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

Rush Hour Solver adalah aplikasi berbasis JavaFX yang dirancang untuk memecahkan permainan puzzle Rush Hour menggunakan berbagai algoritma pencarian. Program ini memungkinkan pengguna untuk memasukkan konfigurasi papan permainan melalui file teks atau mendesain papan secara langsung melalui antarmuka grafis yang intuitif.

Program ini digunakan untuk menyelesaikan puzzle Rush Hour menggunakan algoritma pathfinding seperti UCS, GBFS, A*, IDA*, dan Genetic Algorithm dengan berbagai pilihan heuristik. Cocok untuk mahasiswa, peneliti, dan pecinta puzzle dalam konteks pembelajaran algoritma pencarian dan pemecahan masalah.

---

## 👥 Kontributor

| NIM         | Nama Lengkap            |
|-------------|-------------------------|
| 13523042    | Farhan Abdullah         |
| 13523052    | Adhimas Aryo Bimo       |

---

## ⚙️ Requirement

Tuliskan requirement atau dependensi yang dibutuhkan untuk menjalankan program:

- **Bahasa pemrograman**: Java 11 atau lebih baru
- **Build Tool**: Apache Maven
- **Library eksternal**: 
  - JavaFX
  - Animated GIF library for Java
- **Sistem Operasi**: Linux/Windows/macOS
- **IDE (opsional)**: IntelliJ IDEA, Eclipse, VSCode

---

## 🔧 Instalasi

Langkah-langkah untuk mengunduh dan menyiapkan proyek:

### 1. Clone Repository
```bash
git clone git@github.com:ryonlunar/Tucil3_13523042_13523052.git
cd Tucil3_13523042_13523052
```

### 2. Instalasi JDK (jika belum ada)
```bash
# Ubuntu/Debian
sudo apt install openjdk-11-jdk

# Verifikasi instalasi
java -version
```

### 3. Instalasi JavaFX
```bash
# Ubuntu/Debian
sudo apt install openjfx

# Atau menggunakan Snap
sudo snap install openjfx
```

### 4. Instalasi Maven
```bash
# Ubuntu/Debian
sudo apt install maven
```

### 5. Download Dependencies
```bash
mvn clean install
```

---

## 🚀 Cara Menjalankan Program

### Kompilasi Program
```bash
mvn compile
```

### Menjalankan Aplikasi
```bash
mvn javafx:run
```

### Kompilasi dan Jalankan Sekaligus
```bash
mvn compile && mvn javafx:run
```

---

## 📋 Penggunaan Program

### Input File
1. Pilih opsi "File Input" pada aplikasi
2. Klik "Browse" untuk memilih file input (format .txt)
3. Pilih algoritma pencarian yang diinginkan
4. Jika menggunakan GBFS, A*, IDA*, atau GA, pilih juga heuristik yang diinginkan
5. Klik "Run Algorithm" untuk mencari solusi
6. Hasil solusi akan ditampilkan sebagai animasi pada aplikasi

### Input Langsung
1. Pilih opsi "Direct Input" pada aplikasi
2. Masukkan ukuran papan (baris dan kolom)
3. Klik "Create Board" untuk membuat papan kosong
4. Pilih orientasi (Horizontal/Vertical), panjang, dan ID untuk kendaraan
5. Klik "Place Vehicle" lalu klik pada sel papan untuk menempatkan kendaraan
6. Klik "Place Exit" lalu klik pada tepi papan untuk menempatkan pintu keluar
7. Setelah selesai, klik "Generate Board" untuk menghasilkan dan menyimpan konfigurasi papan
8. Kembali ke mode "File Input" dan jalankan algoritma seperti biasa

---

## 🎯 Fitur Utama

- **Input Fleksibel**: File input atau pembuat papan langsung
- **Multi-Algoritma**: UCS, GBFS, A*, IDA*, dan Genetic Algorithm (GA)
- **Pilihan Heuristik**: Manhattan Distance, Blocked Vehicles, Combined, Chebyshev, dan Evolved
- **Visualisasi**: Animasi solusi yang interaktif
- **Export GIF**: Kemampuan menyimpan animasi sebagai file GIF
- **UI Intuitif**: Antarmuka pengguna yang mudah digunakan

---

## 📄 Format File Input

File input berisi konfigurasi papan permainan dalam format teks sederhana:

```
6 6
12
 ..AAAB
 .CDD.B
KECFPP.
 ECFGHH
 EIIGJ.
 LLMMJ.
```

**Keterangan:**
- Baris pertama: jumlah baris dan kolom papan
- Baris kedua: jumlah kendaraan (tidak termasuk kendaraan utama)
- `.` : sel kosong
- `P` : kendaraan utama (yang harus mencapai pintu keluar)
- `K` : pintu keluar
- `A-Z` : kendaraan lain

---

## 🎮 Kontrol Animasi

- **Slider**: Navigasi manual langkah-langkah solusi
- **Pause/Resume**: Menghentikan atau melanjutkan animasi
- **Save as GIF**: Menyimpan animasi sebagai file GIF

---

## Tutorial Cara Menggunakan program

[![How to Use Program](https://github.com/ryonlunar/Tucil3_13523042_13523052/blob/main/tutorial.mp4)]


*Program ini dibuat sebagai bagian dari Tugas Kuliah Teknik Informatika.*
