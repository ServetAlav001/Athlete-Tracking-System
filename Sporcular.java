import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

public class Sporcular {

    private String isim;
    private String soyad;
    private long TL_No;
    private int kayit_ucreti;
    private int KayitSuresi;
   private LocalDateTime KayitTarihi;

    public Sporcular(String isim, String soyad, long TL_No,int kayit_ucreti,int KayitSuresi,LocalDateTime KayitTarihi) {
        this.isim = isim;
        this.soyad = soyad;
        this.TL_No = TL_No;
        this.kayit_ucreti=kayit_ucreti;
        this.KayitSuresi=KayitSuresi;
        this.KayitTarihi = KayitTarihi;
    }
    public Sporcular(String isim, String soyad) {
        this.isim = isim;
        this.soyad = soyad;

    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public long getTL_No() {
        return TL_No;
    }

    public void setTL_No(long TL_No) {
        this.TL_No = TL_No;
    }

    public int getKayit_ucreti() {
        return kayit_ucreti;
    }

    public void setKayit_ucreti(int kayit_ucreti) {
        this.kayit_ucreti = kayit_ucreti;
    }

    public int getKayitSuresi() {
        return KayitSuresi;
    }

    public void setKayitSuresi(int kayitSuresi) {
        KayitSuresi = kayitSuresi;
    }

    public LocalDateTime getKayitTarihi() {
        return KayitTarihi;
    }

    public void setKayitTarihi(LocalDateTime kayitTarihi) {
        KayitTarihi = kayitTarihi;
    }

    @Override
    public String toString() {
        return "Sporcular{" +
                "isim='" + isim + '\'' +
                ", soyad='" + soyad + '\'' +
                ", TL_No=" + TL_No +

                '}'+"\n";
    }
}
