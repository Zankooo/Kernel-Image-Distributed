import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;

import java.awt.image.BufferedImage;

public class Konvolucija {
    
    /**
     * Funkcija, ki izvede konvolucijo/e nad sliko,slikami zaporedno.
    
     * Za vsako vhodno sliko se izvede zaporedje vseh podanih kernelov.
     * Rezultat ene konvolucije se uporabi kot vhod v naslednjo,
     * zato se na vsaki sliki izvede celotna sekvenca operacij.
     
     * Na koncu funkcija vrne seznam končnih slik, kjer je
     * za vsako vhodno sliko ustvarjena natanko ena izhodna slika.
     
     * @param slike Seznam vhodnih slik (BufferedImage), nad katerimi se izvede obdelava.
     * @param kerneli Seznam kernelov (float[][]), ki se izvedejo zaporedno na vsaki sliki.
     * @return Seznam BufferedImage objektov, ki predstavljajo končne rezultate obdelave.
     */

    public static ArrayList<BufferedImage> izvediOperacije(ArrayList<BufferedImage> slike, ArrayList<float[][]> kerneli, JCheckBox cbMirror) {
        
        // kamor bomo shranjevali rezultate slik po konvoluciji
        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();
        // vzamemo vsako sliko posebej
        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            // in na njen naredimo sekvenco vseh izbranih kernelov
            for (int j = 0; j < kerneli.size(); j++) {
                float[][] kernel = kerneli.get(j);
                // kličemo logično funkcijo
                trenutnaSlika = konvolucijaRGBParallel(trenutnaSlika, kernel);
                System.out.println("Za " + (i + 1) + ". sliko je končana operacija: " + Arrays.deepToString(kerneli.get(j)) );
            }
            // OBRAT SLIKE OZ MIRROR SE NAREDI VEDNO NA KONCU!
            if (cbMirror.isSelected()) {
                trenutnaSlika = mirrorFunkcija(trenutnaSlika);
                System.out.println("Operacija Mirror je bila narejena!");
}
            System.out.println();
            rezultatiSlik.add(trenutnaSlika);
        }

        return rezultatiSlik;
    }


    public static BufferedImage mirrorFunkcija(BufferedImage slika) {
        int sirina = slika.getWidth();
        int visina = slika.getHeight();

        BufferedImage out = new BufferedImage(sirina, visina, slika.getType());

        for (int y = 0; y < visina; y++) {
            for (int x = 0; x < sirina; x++) {
                int rgb = slika.getRGB(x, y);
                out.setRGB(sirina - 1 - x, y, rgb);
            }
        }

        return out;
    }


    /**
     * Funkcija izvede 2D konvolucijo nad barvno sliko (RGB) z uporabo podanega kernela.
     *
     * Za vsak piksel vhodne slike izračuna novo vrednost barvnih komponent (R, G, B)
     * tako, da uporabi uteži iz kernela nad sosednjimi piksli.
     * Robovi slike so obravnavani s pomočjo omejevanja indeksov (clamp),
     * kar prepreči dostop izven meja slike.
     *
     * Alpha kanal (prosojnost) se ohrani iz izvornega piksla.
     * Rezultat konvolucije je nova slika enakih dimenzij kot vhodna slika.
     *
     * @param slika Vhodna slika tipa BufferedImage, nad katero se izvede konvolucija.
     * @param kernel 2D matrika uteži (float[][]), ki predstavlja konvolucijski kernel.
     *               Dimenzije kernela morajo biti lihe (npr. 3x3, 5x5).
     * @return Nova BufferedImage slika, ki predstavlja rezultat konvolucije.
     * @throws IllegalArgumentException Če ima kernel sodo širino ali višino.
     */

    public static BufferedImage konvolucijaRGBParallel(BufferedImage slika, float[][] kernel) {
        
        // vzamemo max stevilo niti ki so na voljo
        int steviloNiti = Runtime.getRuntime().availableProcessors() - 1;
        
        int kernelSirina = kernel[0].length;
        int kernelDolzina = kernel.length;


        int kernelPolmerXos = kernelSirina / 2;
        int kernelPolmerYos = kernelDolzina / 2;

        int sirinaSlike = slika.getWidth();
        int visinaSlike = slika.getHeight();

        BufferedImage novaSlika = new BufferedImage(sirinaSlike, visinaSlike, BufferedImage.TYPE_INT_ARGB);

        Thread[] niti = new Thread[steviloNiti];

        int vrsticeNaEnoNit = visinaSlike / steviloNiti;

        for (int i = 0; i < steviloNiti; i++) {
            final int startY = i * vrsticeNaEnoNit;
            final int endY = (i == steviloNiti - 1) ? visinaSlike : startY + vrsticeNaEnoNit;

            niti[i] = new Thread(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < sirinaSlike; x++) {

                        float vsotaRed = 0;
                        float vsotaGreen = 0;
                        float vsotaBlue = 0;

                        int centerARGB = slika.getRGB(x, y);
                        int a = (centerARGB >>> 24) & 0xFF;

                        for (int kernelY = -kernelPolmerYos; kernelY <= kernelPolmerYos; kernelY++) {
                            for (int kernelX = -kernelPolmerXos; kernelX <= kernelPolmerXos; kernelX++) {

                                int px = clamp(x + kernelX, 0, sirinaSlike - 1);
                                int py = clamp(y + kernelY, 0, visinaSlike - 1);

                                int argb = slika.getRGB(px, py);

                                int r = (argb >>> 16) & 0xFF;
                                int g = (argb >>> 8) & 0xFF;
                                int b = argb & 0xFF;

                                float weight = kernel[kernelY + kernelPolmerYos][kernelX + kernelPolmerXos];

                                vsotaRed += r * weight;
                                vsotaGreen += g * weight;
                                vsotaBlue += b * weight;
                            }
                        }

                        int outRed = clamp(Math.round(vsotaRed), 0, 255);
                        int outGreen = clamp(Math.round(vsotaGreen), 0, 255);
                        int outBlue = clamp(Math.round(vsotaBlue), 0, 255);

                        int outARGB = (a << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
                        novaSlika.setRGB(x, y, outARGB);
                    }
                }
            });

            niti[i].start();
        }

        for (int i = 0; i < steviloNiti; i++) {
            try {
                niti[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Nit je bila prekinjena.", e);
            }
        }
        System.out.println();
        System.out.println("Uporabljenih je bilo: " + steviloNiti + " niti!");

        return novaSlika;
    }




    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

}
