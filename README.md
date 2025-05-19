<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" class="logo" width="120"/>

# Rush Hour Solver

Rush Hour Solver adalah aplikasi berbasis JavaFX yang dirancang untuk memecahkan permainan puzzle Rush Hour menggunakan berbagai algoritma pencarian. Program ini memungkinkan pengguna untuk memasukkan konfigurasi papan permainan melalui file teks atau mendesain papan secara langsung melalui antarmuka grafis yang intuitif.

## Fitur Utama

- Input papan permainan melalui file atau pembuatan langsung
- Mendukung beberapa algoritma pencarian: UCS, GBFS, A*, IDA*, dan Genetic Algorithm (GA)
- Berbagai opsi heuristik: Manhattan Distance, Blocked Vehicles, Combined, Chebyshev, dan Evolved
- Visualisasi solusi dengan animasi
- Kemampuan menyimpan animasi solusi sebagai file GIF
- Antarmuka pengguna yang interaktif dan mudah digunakan


## Requirements

- Java Development Kit (JDK) 11 atau lebih baru
- Apache Maven
- JavaFX
- Animated GIF library for Java


## Instalasi

### 1. Instalasi JDK

Pastikan JDK telah terinstal di komputer Anda. Untuk memeriksa versi Java:

```bash
java -version
```

Jika belum terinstal, Anda dapat menginstalnya dengan:

```bash
sudo apt install openjdk-11-jdk
```


### 2. Instalasi JavaFX

Untuk menginstal JavaFX di Ubuntu, gunakan perintah:

```bash
sudo apt install openjfx
```

Atau, Anda dapat menggunakan Snap:

```bash
sudo snap install openjfx
```


### 3. Instalasi Maven

Maven diperlukan untuk mengelola dependensi dan membangun proyek. Install dengan:

```bash
sudo apt install maven
```


## Kompilasi Program

1. Pertama, clone repositori atau ekstrak sumber kode ke direktori lokal
2. Masuk ke direktori proyek:

```bash
cd [direktori-proyek]
```

3. Download semua dependensi yang diperlukan:

```bash
mvn clean install
```

4. Kompilasi program:

```bash
mvn compile
```


## Cara Menjalankan Program

Setelah kompilasi berhasil, program dapat dijalankan dengan perintah:

```bash
mvn javafx:run
```

Atau, Anda dapat mengompilasi dan menjalankan program sekaligus dengan:

```bash
mvn compile && mvn javafx:run
```


## Penggunaan Program

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

### Animasi

- Gunakan slider untuk melihat langkah-langkah solusi
- Tombol "Pause/Resume" untuk menghentikan atau melanjutkan animasi
- Klik "Save as GIF" untuk menyimpan animasi sebagai file GIF


## Format File Input

File input berisi konfigurasi papan permainan dalam format teks sederhana:

- Baris pertama: jumlah baris dan kolom papan
- Baris kedua: jumlah kendaraan (tidak termasuk kendaraan utama)
- Baris-baris berikutnya: representasi papan permainan

Contoh:

```
6 6
5
......
.AA...
.P.BBB
.P.C..
.P.C..
.....K
```

Keterangan:

- `.` : sel kosong
- `P` : kendaraan utama (yang harus mencapai pintu keluar)
- `K` : pintu keluar
- `A-Z` : kendaraan lain


## Author

| NIM         | Nama Lengkap            |
|-------------|-------------------------|
| 13523042  | Farhan Abdullah  |
| 13523052  | Adhimas Aryo Bimo    |

---

*Program ini dibuat sebagai bagian dari Tugas Kuliah Teknik Informatika.*

<div style="text-align: center">⁂</div>
