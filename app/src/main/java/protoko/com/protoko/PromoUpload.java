package protoko.com.protoko;

public class PromoUpload {

    private String id;
    private String zID;
    private String imageURL;
    private String imageURL2;
    private String imageURL3;
    private String imageURL4;
    private String judulPromo;
    private String jenisPromo;
    private String namaToko;
    private String lokasiToko;
    private int hargaLama;
    private int hargaBaru;
    private int jumlahBeli;
    private int jumlahGratis;
    private String deskripsi;
    private String kategori;
    private String timeStamp;
    private String durasi;
    private String zUpdateTime;
    private String latlong;
    private int meter;


    public PromoUpload() {

    }

    public PromoUpload(String id, String zID, String imageURL, String imageURL2, String imageURL3, String imageURL4, String judulPromo, String jenisPromo, String namaToko, String lokasiToko, int hargaLama, int hargaBaru, int jumlahBeli, int jumlahGratis, String deskripsi, String kategori, String timeStamp, String durasi, String zUpdateTime, String latlong, int meter) {
        this.id = id;
        this.zID = zID;
        this.imageURL = imageURL;
        this.imageURL2 = imageURL2;
        this.imageURL3 = imageURL3;
        this.imageURL4 = imageURL4;
        this.judulPromo = judulPromo;
        this.jenisPromo = jenisPromo;
        this.namaToko = namaToko;
        this.lokasiToko = lokasiToko;
        this.hargaLama = hargaLama;
        this.hargaBaru = hargaBaru;
        this.jumlahBeli = jumlahBeli;
        this.jumlahGratis = jumlahGratis;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.timeStamp = timeStamp;
        this.durasi = durasi;
        this.zUpdateTime = zUpdateTime;
        this.latlong = latlong;
        this.meter = meter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getzID() {
        return zID;
    }

    public void setzID(String zID) {
        this.zID = zID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL2() {
        return imageURL2;
    }

    public void setImageURL2(String imageURL2) {
        this.imageURL2 = imageURL2;
    }

    public String getImageURL3() {
        return imageURL3;
    }

    public void setImageURL3(String imageURL3) {
        this.imageURL3 = imageURL3;
    }

    public String getImageURL4() {
        return imageURL4;
    }

    public void setImageURL4(String imageURL4) {
        this.imageURL4 = imageURL4;
    }

    public String getJudulPromo() {
        return judulPromo;
    }

    public void setJudulPromo(String judulPromo) {
        this.judulPromo = judulPromo;
    }

    public String getJenisPromo() {
        return jenisPromo;
    }

    public void setJenisPromo(String jenisPromo) {
        this.jenisPromo = jenisPromo;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getLokasiToko() {
        return lokasiToko;
    }

    public void setLokasiToko(String lokasiToko) {
        this.lokasiToko = lokasiToko;
    }

    public int getHargaLama() {
        return hargaLama;
    }

    public void setHargaLama(int hargaLama) {
        this.hargaLama = hargaLama;
    }

    public int getHargaBaru() {
        return hargaBaru;
    }

    public void setHargaBaru(int hargaBaru) {
        this.hargaBaru = hargaBaru;
    }

    public int getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(int jumlahBeli) {
        this.jumlahBeli = jumlahBeli;
    }

    public int getJumlahGratis() {
        return jumlahGratis;
    }

    public void setJumlahGratis(int jumlahGratis) {
        this.jumlahGratis = jumlahGratis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getzUpdateTime() {
        return zUpdateTime;
    }

    public void setzUpdateTime(String zUpdateTime) {
        this.zUpdateTime = zUpdateTime;
    }

    public int getMeter() {
        return meter;
    }

    public void setMeter(int meter) {
        this.meter = meter;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }
}