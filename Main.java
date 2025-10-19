import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {

        String DosyaAdi = "Sporcu Bilgileri.txt";

        SporcuYonetimi Yonetim = new SporcuYonetimi();

        Yonetim.DosyadanOku(DosyaAdi);
        System.out.println("sporcu takip sistemine hosgeldiniz...");

        String islemler= "1.islem: islemler\n" +
                "2.islem: Sporcu Kayit\n" +
                "3.islem: Sporcu Sil\n" +
                "4.islem: Sporcu Listesi\n" +
                "5.islem: Aylik Ortalama Gelir\n" +
                "6.islem: Sporcu kayit suresi\n" +
                "7.islem: Sporcu kayit Kontrol\n" +
                "8.islem: tum Sporcularin kayit sureleri\n" +
                "q.islem: cikis\n";

        System.out.println(islemler);
        System.out.println("*******************");

        Scanner scanner = new Scanner(System.in);

        while (true){

            System.out.print("lutfen yapmak istediginiz islemi giriniz:");
            String islem = scanner.nextLine();
            //sistemden cikar.
            if(islem.equals("q")){
                System.out.println("sistemden cikiliyor...");
                break;
            }
            //işlemleri bastırır.
            else if(islem.equals("1")){
                System.out.println(islemler);
            }
            //sporcu kaydını ekler
            else if(islem.equals("2")){
                try {
                        System.out.print("sporcunun ismini giriniz:");
                        String isim=scanner.nextLine();
                        System.out.print("sporcunun soyadini giriniz:");
                        String soyad=scanner.nextLine();
                        if(Yonetim.SporcuKontrol(new Sporcular(isim,soyad))){
                            System.out.print("ayni isme ait birden fazla kayit yaptiramazsiniz...");
                        }else {
                            System.out.print("telefon numarasini giriniz:");
                            long tlf_no=scanner.nextLong();
                            System.out.print("Kayit ucretini giriniz:");
                            int Kayitucreti=scanner.nextInt();
                            System.out.print("kac aylik kayit yaptirdi:");
                            int kayitsuresi=scanner.nextInt();
                            System.out.println("tarih giriniz(yil):");
                            int yil =scanner.nextInt();
                            System.out.println("tarih giriniz(ay):");
                            int ay = scanner.nextInt();
                            System.out.println("tarih giriniz(gun):");
                            int gun = scanner.nextInt();
                            LocalDateTime tarih = LocalDateTime.of(yil,ay,gun,0,0);
                            Yonetim.SporcuEkle(new Sporcular(isim,soyad,tlf_no,Kayitucreti,kayitsuresi,tarih));
                            System.out.println("kayit yapildi..");
                        }
                }
                catch (DateTimeException e){
                    System.out.println("lutfen tarih degerlerini dogru bir sekilde giriniz..."+e.getMessage());
                }
                catch (InputMismatchException e){
                    System.out.println("lutfen bir sayi giriniz..." + e.getMessage());
                }
                catch (Exception e){
                        System.out.println("kayit yapilirken bir hata olustu..." + e.getMessage());
                }

            }
            //var olan sporcuyu siler.
            else if(islem.equals("3")){
                try {
                    System.out.print("sporcunun ismini giriniz:");
                    String isim=scanner.nextLine();
                    System.out.print("sporcunun soyadini giriniz:");
                    String soyad=scanner.nextLine();
                    Sporcular sporcu = new Sporcular(isim,soyad);
                    Yonetim.SporcuSil(sporcu);


                }catch (Exception e){
                    e.getMessage();
                }
            }
            //sporcuların bilgilerini listeler.
            else if(islem.equals("4")){

                Yonetim.SporcuListele();

            }
            //gelir hesaplamasını yapar.
            else if(islem.equals("5")){
                Yonetim.GelirHesapla();
            }
            //sporcunun kalan kayıt suresini hesaplar.
            else if(islem.equals("6")){
                System.out.print("sporcunun ismini giriniz:");
                String isim=scanner.nextLine();
                System.out.print("sporcunun soyadini giriniz:");
                String soyad=scanner.nextLine();

                for (Sporcular s : Yonetim.getSporcuListesi().values()){
                    if (s.getIsim().equals(isim) && s.getSoyad().equals(soyad)){
                        try {
                            Yonetim.KayitSuresi_Kontrol(s);
                        }catch (NullPointerException e){
                            System.out.println(e.getMessage());
                        }
                    }else {
                        System.out.println("kayit mevcut degil..");
                        break;
                    }
                }

            }
            //sporcunu var olup olmadıgını kontrol eder
            else if(islem.equals("7")) {
                System.out.print("sporcunun ismini giriniz:");
                String isim = scanner.nextLine();
                System.out.print("sporcunun soyadini giriniz:");
                String soyad = scanner.nextLine();
                Sporcular sporcu = new Sporcular(isim,soyad);
                Yonetim.SporcuKontrol(sporcu);
            }
            //tum sporcuların kayit surelerini listeler.
            else if (islem.equals("8")) {
                try {
                Yonetim.TumKayitSureleriniKontrolEt();
            }catch (RuntimeException e){

            }

            }else {
                System.out.println("yanlis bir islem girdiniz..");
            }
            scanner.nextLine();
        }

        //var olan dosyadaki verileri siler.
        Yonetim.DosyadanSil(DosyaAdi);
        //yapılan kayıtları dosyaya ekler.
        Yonetim.DosyayaYaz(DosyaAdi);
    }
}
