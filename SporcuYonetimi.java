import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;


public class SporcuYonetimi {


    private Map<Integer,Sporcular> SporcuListesi = new LinkedHashMap<>();
    public Map<Integer, Sporcular> getSporcuListesi() {
        return SporcuListesi;
    }

    public void setSporcuListesi(Map<Integer, Sporcular> sporcuListesi) {
        SporcuListesi = sporcuListesi;
    }

    //sporcu ekleme fonksiyonu
    public void  SporcuEkle(Sporcular Sporcu){
        if(SporcuListesi.containsValue(Sporcu)){
            System.out.println("bu sporcu zaten kayitli...");
        }else {
            SporcuListesi.put(SporcuListesi.size()+1,Sporcu);
        }

    }
    //sporcu kontrol etme fonksiyonu
    public boolean SporcuKontrol(Sporcular sporcu){
        for (Sporcular s: SporcuListesi.values()) {
            if(s.getIsim().equals(sporcu.getIsim())&&s.getSoyad().equals(sporcu.getSoyad())){

                return true;
            }
        }

            return false;
    }
    //sporcu listeleme fonksiyonu
    public void SporcuListele(){
            if(SporcuListesi.isEmpty()){
                System.out.println("Sporcu listesi su anda boş...");
            }else{
                for (Sporcular s:SporcuListesi.values()) {
                    System.out.println(s);
                }
            }
    }
    //sporcu silme fonksiyonu
    public boolean SporcuSil(Sporcular sporcu){
        Iterator<Map.Entry<Integer, Sporcular>> it = SporcuListesi.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, Sporcular> entry = it.next();
            Sporcular s = entry.getValue();

            if (s.getIsim().equals(sporcu.getIsim()) && s.getSoyad().equals(sporcu.getSoyad())) {
                it.remove(); // güvenli silme
                System.out.println(sporcu.getIsim() + " " + sporcu.getSoyad() + " kayıttan silindi.");
                return true;
            }
        }
        System.out.println("Kayıt bulunamadı...");
        return false;
    }
    //gelir hesaplama fonksiyonu
    public void GelirHesapla(){
       int toplam=0;
           for (Sporcular s: SporcuListesi.values() )
           {
               toplam+=s.getKayit_ucreti();
           }
           System.out.println("toplam gelir: " + toplam);
    }
    //sporcunun kayıt suresini hesaplama fonksiyonu
    public void KayitSuresi_Kontrol(Sporcular sporcu){

        LocalDateTime bugun = LocalDateTime.now();
        LocalDateTime bitisTarihi = sporcu.getKayitTarihi().plusMonths(sporcu.getKayitSuresi());

        // Süre farkını gün cinsinden hesapla
        long farkGun = java.time.Duration.between(bugun, bitisTarihi).toDays();

        System.out.println("-------------------------------------------");
        System.out.println(sporcu.getIsim() + " " + sporcu.getSoyad() + " için kayıt süresi kontrol ediliyor...");
        System.out.println("Kayıt tarihi: " + sporcu.getKayitTarihi());
        System.out.println("Bitiş tarihi: " + bitisTarihi);

        if (farkGun > 0) {
            if (farkGun <= 7) {
                System.out.println("⚠️ Kayıt " + farkGun + " gün sonra bitecek. Yenileme zamanı yaklaşıyor!");
            } else {
                System.out.println("✅ Kayıt aktif. Kalan süre: " + farkGun + " gün.");
            }
        } else if (farkGun == 0) {
            System.out.println("⚠️ Bugün son gün! Kayıt süresi bugün doluyor.");
        } else if(farkGun<0) {
            System.out.println("❌ Kayıt süresi " + Math.abs(farkGun) + " gün önce dolmuş.");


        }
        System.out.println("-------------------------------------------\n");
    }
    //sporcuların kayıt surelerini kontrol etme fonksiyonu
    public void TumKayitSureleriniKontrolEt() {
        if (SporcuListesi.isEmpty()) {
            System.out.println("Hiç kayıtlı sporcu yok.");
            return;
        }
        for (Sporcular s : SporcuListesi.values()) {
            KayitSuresi_Kontrol(s);
        }
    }
    //verileri dosyaya yazdırma fonksiyonu
    public void DosyayaYaz(String DosyaAdi){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DosyaAdi,true))) {

            for (Sporcular s: SporcuListesi.values()){
                writer.write(s.getIsim()+";"+s.getSoyad()+";"+s.getTL_No()+";"
                        +s.getKayit_ucreti()+";"+s.getKayitSuresi()+";"+s.getKayitTarihi()+"\n");
            }

            System.out.println("✅ Veriler dosyaya kaydedildi: " + DosyaAdi);
        } catch (IOException e) {
            System.out.println("❌ Dosya yazma hatası: " + e.getMessage());
        }
    }
    //var olan dosya verileri okuma fonksiyonu
    public void DosyadanOku(String DosyaAdi){
        try (BufferedReader reader = new BufferedReader(new FileReader(DosyaAdi))){

            String satir;

            while ((satir=reader.readLine())!=null){
                String[] veri = satir.split(";");
                if (veri.length==6){
                    String isim = veri[0];
                    String soyisim = veri[1];
                    Long telefon =Long.parseLong(veri[2]);
                    int kayitucreti = Integer.parseInt(veri[3]);
                    int kayitsuresi = Integer.parseInt(veri[4]);
                    LocalDateTime tarih = LocalDateTime.parse(veri[5].replace("}","").trim());

                    Sporcular sporcu = new Sporcular(isim,soyisim,telefon,kayitucreti,kayitsuresi,tarih);
                    SporcuListesi.put(SporcuListesi.size()+1,sporcu);
                }
            }
            System.out.println("📂 Dosyadan " + SporcuListesi.size() + " sporcu yüklendi.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //dosyadan verileri silme fonksiyonu
    public void DosyadanSil(String File){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(File,false))){

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}




