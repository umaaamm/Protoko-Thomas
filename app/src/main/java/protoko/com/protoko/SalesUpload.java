package protoko.com.protoko;


class SalesUpload {

    private String idSales;
    private String namaSales;
    private String namaPerusahaan;
    private String emailSales;
    private String telpSales;
    private String ktpURL;
    private String fotoURL;
    private String zID;
    private String verified;

    public SalesUpload(){

    }

    public SalesUpload(String idSales, String namaSales, String namaPerusahaan, String emailSales, String telpSales, String ktpURL, String fotoURL, String zID, String verified) {
        this.idSales = idSales;
        this.namaSales = namaSales;
        this.namaPerusahaan = namaPerusahaan;
        this.emailSales = emailSales;
        this.telpSales = telpSales;
        this.ktpURL = ktpURL;
        this.fotoURL = fotoURL;
        this.zID = zID;
        this.verified = verified;
    }

    public String getIdSales() {
        return idSales;
    }

    public void setIdSales(String idSales) {
        this.idSales = idSales;
    }

    public String getNamaSales() {
        return namaSales;
    }

    public void setNamaSales(String namaSales) {
        this.namaSales = namaSales;
    }

    public String getEmailSales() {
        return emailSales;
    }

    public void setEmailSales(String emailSales) {
        this.emailSales = emailSales;
    }

    public String getTelpSales() {
        return telpSales;
    }

    public void setTelpSales(String telpSales) {
        this.telpSales = telpSales;
    }

    public String getKtpURL() {
        return ktpURL;
    }

    public void setKtpURL(String ktpURL) {
        this.ktpURL = ktpURL;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getzID() {
        return zID;
    }

    public void setzID(String zID) {
        this.zID = zID;
    }

    public String getNamaPerusahaan() {
        return namaPerusahaan;
    }

    public void setNamaPerusahaan(String namaPerusahaan) {
        this.namaPerusahaan = namaPerusahaan;
    }
}
