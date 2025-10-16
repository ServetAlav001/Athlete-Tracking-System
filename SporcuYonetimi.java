import java.io.*;
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

    public void  SporcuEkle(Sporcular Sporcu){
        if(SporcuListesi.containsValue(Sporcu)){
            System.out.println("bu sporcu zaten kayitli...");
        }else {
            SporcuListesi.put(SporcuListesi.size()+1,Sporcu);
        }

    }
    public boolean SporcuKontrol(Sporcular sporcu){
        for (Sporcular s: SporcuListesi.values()) {
            if(s.getIsim().equals(sporcu.getIsim())&&s.getSoyad().equals(sporcu.getSoyad())){
                System.out.println("Kayit mevcut");
                return true;
            }
        }
            return false;
    }
    public void SporcuListele(){
            if(SporcuListesi.isEmpty()){
                System.out.println("Sporcu listesi su anda boş...");
            }else{
                for (Sporcular s:SporcuListesi.values()) {
                    System.out.println(s);
                }
            }
    }
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
    public void GelirHesapla(){
       int toplam=0;
           for (Sporcular s: SporcuListesi.values() )
           {
               toplam+=s.getKayit_ucreti();
           }
           System.out.println("toplam gelir: " + toplam);

    }

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
        } else {
            System.out.println("❌ Kayıt süresi " + Math.abs(farkGun) + " gün önce dolmuş.");
        }

        System.out.println("-------------------------------------------\n");

    }
    public void TumKayitSureleriniKontrolEt() {
        if (SporcuListesi.isEmpty()) {
            System.out.println("Hiç kayıtlı sporcu yok.");
            return;
        }

        for (Sporcular s : SporcuListesi.values()) {
            KayitSuresi_Kontrol(s);
        }
    }
    public void DosyayaYaz(String DosyaAdi){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DosyaAdi,true))) {
            for (Sporcular s: SporcuListesi.values()) {
                writer.write(s.toString()+"\n");
            }
            System.out.println("veriler basarili bir sekilde kaydedildi...");
        } catch (IOException e) {
            System.out.println("dosyaya yazdirilirken bir hata olustu...");
        }
    }
    public void DosyadanOku(String DosyaAdi){
        try(BufferedReader reader = new BufferedReader(new FileReader(DosyaAdi))) {
            String satir;
            int id = 1;
            while ((satir = reader.readLine()) != null) {
                String[] veriler = satir.split(";");
                if (veriler.length == 6) {
                    Sporcular s = new Sporcular(
                            veriler[0], // isim
                            veriler[1], // soyad
                            Long.parseLong(veriler[2]), // TL_No
                            Integer.parseInt(veriler[3]), // ücret
                            Integer.parseInt(veriler[4]), // süre
                            LocalDateTime.parse(veriler[5]) // kayıt tarihi
                    );
                    SporcuListesi.put(id++, s);
                }
            }
            System.out.println("✅ Veriler dosyadan yüklendi: " + DosyaAdi);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("❌ Dosya okuma hatası: " + e.getMessage());
        }
    }

}




