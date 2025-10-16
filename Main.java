import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        SporcuYonetimi Yonetim = new SporcuYonetimi();

        System.out.println("sporcu takip sistemine hosgeldiniz...");

        String islemler= "1.islem: islemler\n" +
                "2.islem: Sporcu Kayit\n" +
                "3.islem: Sporcu Sil\n" +
                "4.islem: Sporcu Listesi\n" +
                "5.islem: Aylik Ortalama Gelir\n" +
                "6.islem: Sporcu kayit suresi\n" +
                "7.islem: Sporcu kayit Kontrol\n" +
                "8.islem: Sporcu kayit Kontrol\n" +
                "q.islem: cikis\n";

        System.out.println(islemler);
        System.out.println("*******************");

        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("lutfen yapmak istediginiz islemi giriniz:");
            String islem = scanner.nextLine();

            if(islem.equals("q")){
                System.out.println("sistemden cikiliyor...");
                break;
            }
            else if(islem.equals("1")){
                System.out.println(islemler);
            }
            else if(islem.equals("2")){
                try {
                        System.out.print("sporcunun ismini giriniz:");
                        String isim=scanner.nextLine();
                        System.out.print("sporcunun soyadini giriniz:");
                        String soyad=scanner.nextLine();
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
                        scanner.nextLine();
                        LocalDateTime tarih = LocalDateTime.of(yil,ay,gun,0,0);
                        Yonetim.SporcuEkle(new Sporcular(isim,soyad,tlf_no,Kayitucreti,kayitsuresi,tarih));
                        }catch (Exception e){
                        System.out.println("kayit yapilirken bir hata olustu..." + e.getMessage());
                }
            }
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
            else if(islem.equals("4")){
                System.out.println(Yonetim.getSporcuListesi());
            }
            else if(islem.equals("5")){
                Yonetim.GelirHesapla();
            }
            else if(islem.equals("6")){
                System.out.print("sporcunun ismini giriniz:");
                String isim=scanner.nextLine();
                System.out.print("sporcunun soyadini giriniz:");
                String soyad=scanner.nextLine();
                Sporcular sporcu = new Sporcular(isim,soyad);
                Yonetim.KayitSuresi_Kontrol(sporcu);
            }
            else if(islem.equals("7")){
                System.out.print("sporcunun ismini giriniz:");
                String isim=scanner.nextLine();
                System.out.print("sporcunun soyadini giriniz:");
                String soyad=scanner.nextLine();
                Sporcular sporcu = new Sporcular(isim,soyad);
                Yonetim.SporcuKontrol(sporcu);
            }
            else if(islem.equals("8")){
                Yonetim.TumKayitSureleriniKontrolEt();
            }
            else {
                System.out.println("yanlis bir islem girdiniz...");

            }

        }
        Yonetim.DosyayaYaz("Sporcu Bilgileri.txt");
        Yonetim.DosyadanOku("Sporcu Bilgileri.txt");

    }


}