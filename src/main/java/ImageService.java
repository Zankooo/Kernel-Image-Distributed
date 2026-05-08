import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;

public class ImageService {

    /**
     * Funkcija ki naloži sliko iz določene poti in jo vrne kot BufferedImage.
     * BufferedImage je klass za delo s slikami v RAMU
     * omogoča branje, risanje, spreminjanje in shranjevanje slik na nivoju posameznih pikslov.
     * @param potDoSlike Pot do slikovne datoteke.
     * @return BufferedImage ali null, če pride do napake.
     */

    public static BufferedImage naloziSliko(String potDoSlike) {
        try {
            File file = new File(potDoSlike);

            if (!file.exists()) {
                System.err.println("Napaka: Datoteka na poti " + potDoSlike + " ne obstaja.");
                return null;
            }
            // ImageIO.read(File) poskuša dešifrirati sliko in jo naložiti
            BufferedImage image = ImageIO.read(file);
            // če je poškodovana datoteka
            if (image == null) {
                System.err.println("Napaka: Datoteka je morda poškodovana ali pa ImageIO formata ne podpira.");
            }

            // vrnemo sliko
            return image;

            // če je i/o napaka
        } catch (IOException e) {
            System.err.println("Napaka pri I/O operaciji: " + e.getMessage());
            return null;
        }
    }

    
    public static ArrayList<BufferedImage> naloziSlikeIzMape(File[] datoteke) {
        ArrayList<BufferedImage> slikeSeznam = new ArrayList<>();
        System.out.println("📂 Nalagam slike v pomnilnik...");
        if (datoteke != null) {
            int count = 0;
            for (File datoteka : datoteke) {
                try {
                    String ime = datoteka.getName().toLowerCase();
                    if (ime.endsWith(".png") || ime.endsWith(".jpg") || ime.endsWith(".jpeg")) {
                        slikeSeznam.add(ImageIO.read(datoteka));
                        count++;
                    }
                } catch (IOException e) {
                    System.out.println("Napaka pri datoteki: " + datoteka.getName());
                }
            }
            System.out.println("✅ Naloženih " + count + " slik.");
        }

        return slikeSeznam;
    }


    public static void izvediOperacijeSlikam(ArrayList<BufferedImage> slike, ArrayList<String> imenaKernelov, JCheckBox cbmirror) throws IOException {

        if (slike == null || slike.isEmpty()) {
            System.out.println("Nalaganje slik ni uspelo. Poskusite z drugo izbiro.");
            return;
        }

        System.out.println("---------------------------------------------");
        System.out.println("REZULTATI:");

        ArrayList<float[][]> kerneli = Kernel.izbiraKernelov(imenaKernelov);
        
        // čas merimo izključno za izvedbo konvolucije
        // tukaj ne vključimo notri čas branja slika čas write na disk..
        long zacetniCas = System.currentTimeMillis();

        ArrayList<BufferedImage> rezultati;
        try {
            if (mpi.MPI.COMM_WORLD.Size() > 1) {
                rezultati = KonvolucijaMPI.izvediOperacijeDistributed(slike, kerneli, imenaKernelov, cbmirror);
            } else {
                rezultati = Konvolucija.izvediOperacije(slike, kerneli, cbmirror);
            }
        } catch (mpi.MPIException e) {
            System.err.println("MPI Napaka: " + e.getMessage());
            rezultati = Konvolucija.izvediOperacije(slike, kerneli, cbmirror);
        }

        long koncaniCas = System.currentTimeMillis();
        double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;

        System.out.println();
        System.out.println("⏱️ Čas IZRAČUNA (brez branja/shranjevanja): " + kolikoCasaJeTrajaloSek + " sekund");
        
        //shranimo v mapo slike
        shraniNoveSlikeVmapo(rezultati);
        
    }

    private static void shraniNoveSlikeVmapo(ArrayList<BufferedImage> rezultati) throws IOException {
        File mapa = new File("ustvarjeneSlike");

        // pobrisemo prejsne slike ali ustvarimo direktorij ce ga se ni ustvarjega
        if (mapa.exists() && mapa.isDirectory()) {
            File[] datoteke = mapa.listFiles();
            if (datoteke != null) {
                for (File f : datoteke) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        } else {
            mapa.mkdirs();
        }

        System.out.println("💾 Shranjevanje obdelanih slik (ustvarjeneSlike/)...");
        // shranimo slike notri
        for (int i = 0; i < rezultati.size(); i++) {
            BufferedImage rezultat = rezultati.get(i);
            ImageIO.write(
                    rezultat,
                    "png",
                    new File(mapa, "slika_" + (i + 1) + ".png")
            );
            System.out.print("."); // Pika za vsako shranjeno sliko
        }
        System.out.println("\n✅ Vse slike so shranjene.");

        System.out.println();
        System.out.println("Ustvarjene slike so na voljo v mapi: ustvarjeneSlike");
    }




}
