import mpi.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JCheckBox;

public class KonvolucijaMPI {

    // Konstante za MPI oznake (Tags)
    public static final int TAG_COMMAND = 0;
    public static final int TAG_METADATA = 1;
    public static final int TAG_KERNEL = 2;
    public static final int TAG_IMAGE_DATA = 3;
    public static final int TAG_RESULT = 4;

    // Ukazi za delavce
    public static final int CMD_PROCESS = 1;
    public static final int CMD_TERMINATE = 2;

    /**
     * Glavna funkcija za porazdeljeno obdelavo slik.
     */
    public static ArrayList<BufferedImage> izvediOperacije(ArrayList<BufferedImage> slike, ArrayList<float[][]> kerneli, ArrayList<String> imenaKernelov, JCheckBox cbMirror) throws MPIException {
        
        // čas merimo izključno za izvedbo konvolucije
        // tukaj ne vključimo notri čas branja slika čas write na disk..
        long zacetniCas = System.currentTimeMillis();

        // tukaj je glavni prehod iz navadne aplikacije v distributed
        // stevilo vseh 
        int size = MPI.COMM_WORLD.Size();
        // ce imamo 4, je v bistvu 3 ker je prvi za gui
        int numWorkers = Math.max(1, size - 1); // Vsaj 1 delavec, da ne pride do / 0

        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();
        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            System.out.println("Obdelava slike " + (i + 1) + " od " + slike.size());

            for (int j = 0; j < kerneli.size(); j++) {
                float[][] kernel = kerneli.get(j);
                System.out.println("Kernel: " + imenaKernelov.get(j));
                
                trenutnaSlika = konvolucijaRGBDistributed(trenutnaSlika, kernel, numWorkers);
            }

            if (cbMirror.isSelected()) {
                trenutnaSlika = mirrorFunkcija(trenutnaSlika);
                System.out.println("Mirror koncan");
            }

            rezultatiSlik.add(trenutnaSlika);
        }
        long koncaniCas = System.currentTimeMillis();
        double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;

        System.out.println();
        System.out.println("Cas trajanja " + kolikoCasaJeTrajaloSek + " sekund");

        return rezultatiSlik;
    }

    /**
     * Master logika za razpošiljanje dela.
     * Ime usklajeno s konvolucijaRGBParallel
     */
    private static BufferedImage konvolucijaRGBDistributed(BufferedImage slika, float[][] kernel, int numWorkers) throws MPIException {
        // master razdeli sliko po vrsticah
        int sirina = slika.getWidth();
        int visina = slika.getHeight();
        // najprej se slika pretvori v en velik int[]
        int[] piksli = slika.getRGB(0, 0, sirina, visina, null, 0, sirina);
        // potem se visina razdeli med workerje
        int stVrsticNaWorkerja = visina / numWorkers;
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        int[] procesiraniPiksli = new int[piksli.length];
                            // 3
        for (int i = 1; i <= numWorkers; i++) {
            // Tukaj se razdeli slika med workerje
            int startRow = (i - 1) * stVrsticNaWorkerja;
            // zadnji dobi ostanek ce ni popolnoma mogoce razdelit
            int endRow = (i == numWorkers) ? visina : i * stVrsticNaWorkerja;
            int numRows = endRow - startRow;

            System.out.println("Master: Posiljanje dela workerju " + i + " (" + numRows + " vrstic)");
            // master poslje vsakemu workerju stiri stvari:
            // 1. to pomeni zacni obdelavo
            MPI.COMM_WORLD.Send(new int[]{CMD_PROCESS}, 0, 1, MPI.INT, i, TAG_COMMAND);

            
            // 2. Meta podatke= sirina slike, visina, od katere vrstice zacnes 
            // koliko vrstic obdelaj, visina kernela, sirina kernela
            int[] metadata = {sirina, visina, startRow, numRows, kernelHeight, kernelWidth};
            MPI.COMM_WORLD.Send(metadata, 0, metadata.length, MPI.INT, i, TAG_METADATA);
            
            // 3. poslje kernel
            float[] kernelKotTabela = pretvoriKernelV1D(kernel);
            MPI.COMM_WORLD.Send(kernelKotTabela, 0, kernelKotTabela.length, MPI.FLOAT, i, TAG_KERNEL);
            
            // 4. Se vse piksle slike
            MPI.COMM_WORLD.Send(piksli, 0, piksli.length, MPI.INT, i, TAG_IMAGE_DATA);
            
            // pomembno: vsak worker dobi celo sliko obdela pa samo svoj del.
            // Celo pa zato, ker rabi sosede!
        
        }
        
        // to se izvede ko workerji ze naredijo svoj delo. 
        // master mora dobiti nazaj in sestaviti v sliko
        for (int i = 1; i <= numWorkers; i++) {
            int startRow = (i - 1) * stVrsticNaWorkerja;
            int endRow = (i == numWorkers) ? visina : i * stVrsticNaWorkerja;
            int numRows = endRow - startRow;

            int[] receivedPixels = new int[sirina * numRows];
            MPI.COMM_WORLD.Recv(receivedPixels, 0, receivedPixels.length, MPI.INT, i, TAG_RESULT);
            
            System.out.println("Master: Prejel nazaj rezultat od workerja " + i);
            System.arraycopy(receivedPixels, 0, procesiraniPiksli, startRow * sirina, receivedPixels.length);
        }

        BufferedImage novaSlika = new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_ARGB);
        novaSlika.setRGB(0, 0, sirina, visina, procesiraniPiksli, 0, sirina);
        return novaSlika;
    }

    public static void workerProcess(int rank) throws MPIException {
        // worker stoji in caka da mu master poslje
        while (true) {
            int[] command = new int[1];
            MPI.COMM_WORLD.Recv(command, 0, 1, MPI.INT, 0, TAG_COMMAND);
            // ce dobi CMD_TERMINATE se ustvari
            if (command[0] == CMD_TERMINATE) {
                break;
            }
            // ce dobi CMD_PROCESS potem sprejme parametre. 
            if (command[0] == CMD_PROCESS) {
                int[] metadata = new int[6];
                MPI.COMM_WORLD.Recv(metadata, 0, 6, MPI.INT, 0, TAG_METADATA);
                int sirina = metadata[0];
                int visina = metadata[1];
                int startRow = metadata[2];
                int numRows = metadata[3];
                int visinaKernela = metadata[4];
                int sirinaKernela = metadata[5];

                System.out.println("Worker " + rank + ": Dobil sem za delati: (" + numRows + " vrstic)");

                // dobi kernel, ki ga more narediti da ni vec flat.
                // 1d array je veliko bolj prakticno posiljat kot 2d
                float[] kernelKotTabela = new float[visinaKernela * sirinaKernela];
                MPI.COMM_WORLD.Recv(kernelKotTabela, 0, kernelKotTabela.length, MPI.FLOAT, 0, TAG_KERNEL);
                float[][] kernel = pretvori1DvKernel(kernelKotTabela, visinaKernela, sirinaKernela);

                // dobi piksle slike
                int[] piksli = new int[sirina * visina];
                MPI.COMM_WORLD.Recv(piksli, 0, piksli.length, MPI.INT, 0, TAG_IMAGE_DATA);

                // tukaj se zacne oz se klice konvolucija
                System.out.println("Worker " + rank + ": Računam...");
                // izracuna svoj del
                int[] result = narediKonvolucijoZaSvojDel(piksli, sirina, visina, startRow, numRows, kernel);

                // poslje rezultat nazaj
                System.out.println("Worker " + rank + ": Pošiljam rezultat nazaj.");
                // poslje nazaj rezultat masterju
                MPI.COMM_WORLD.Send(result, 0, result.length, MPI.INT, 0, TAG_RESULT);
            }
        }
    }

    private static int[] narediKonvolucijoZaSvojDel(int[] piksli, int sirinaSlike, int visinaSlike, int startRow, int numRows, float[][] kernel) {
        int kernelSirina = kernel[0].length;
        int kernelDolzina = kernel.length;

        int kernelPolmerXos = kernelSirina / 2;
        int kernelPolmerYos = kernelDolzina / 2;

        int[] novaSlika = new int[sirinaSlike * numRows];

        for (int poStolpcuDol = 0; poStolpcuDol < numRows; poStolpcuDol++) {
            int dejanskaVrsticaSlike = startRow + poStolpcuDol;
            for (int poVrsticiDesno = 0; poVrsticiDesno < sirinaSlike; poVrsticiDesno++) {
                float vsotaRed = 0;
                float vsotaGreen = 0;
                float vsotaBlue = 0;
                
                int centerARGB = piksli[dejanskaVrsticaSlike * sirinaSlike + poVrsticiDesno];
                int prosojnost = (centerARGB >>> 24) & 0xFF;

                for (int kernelY = -kernelPolmerYos; kernelY <= kernelPolmerYos; kernelY++) {
                    for (int kernelX = -kernelPolmerXos; kernelX <= kernelPolmerXos; kernelX++) {
                        int px = omejimoRobnePiksle(poVrsticiDesno + kernelX, 0, sirinaSlike - 1);
                        int py = omejimoRobnePiksle(dejanskaVrsticaSlike + kernelY, 0, visinaSlike - 1);
                        int argb = piksli[py * sirinaSlike + px];
                        
                        int rdeca = (argb >>> 16) & 0xFF;
                        int zelena = (argb >>> 8) & 0xFF;
                        int modra = argb & 0xFF;

                        float weight = kernel[kernelY + kernelPolmerYos][kernelX + kernelPolmerXos];

                        vsotaRed += rdeca * weight;
                        vsotaGreen += zelena * weight;
                        vsotaBlue += modra * weight;
                    }
                }

                int outRed = omejimoRobnePiksle(Math.round(vsotaRed), 0, 255);
                int outGreen = omejimoRobnePiksle(Math.round(vsotaGreen), 0, 255);
                int outBlue = omejimoRobnePiksle(Math.round(vsotaBlue), 0, 255);

                int outARGB = (prosojnost << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
                novaSlika[poStolpcuDol * sirinaSlike + poVrsticiDesno] = outARGB;
            }
        }
        return novaSlika;
    }

    // mirror ni porazdeljen
    public static BufferedImage mirrorFunkcija(BufferedImage slika) {
        int sirina = slika.getWidth();
        int visina = slika.getHeight();
        BufferedImage out = new BufferedImage(sirina, visina, slika.getType());
        for (int y = 0; y < visina; y++) {
            for (int x = 0; x < sirina; x++) {
                out.setRGB(sirina - 1 - x, y, slika.getRGB(x, y));
            }
        }
        return out;
    }

    private static int omejimoRobnePiksle(int stevilkaKiJoOmejimo, int minimalno, int maksimalno) {
        return Math.max(minimalno, Math.min(maksimalno, stevilkaKiJoOmejimo));
    }

    
    // iz { {1, 2, 3},
    //      {4, 5, 6},
    //      {7, 8, 9} } v
    // v [1, 2, 3, 4, 5, 6, 7, 8, 9] v:
    private static float[] pretvoriKernelV1D(float[][] kernel) {
        int stVrsticKernela = kernel.length;
        int stStolpcevKernela = kernel[0].length;

        float[] kernelKotTabela = new float[stVrsticKernela * stStolpcevKernela];

        for (int vrstica = 0; vrstica < stVrsticKernela; vrstica++) {
            System.arraycopy(
                    kernel[vrstica],
                    0,
                    kernelKotTabela,
                    vrstica * stStolpcevKernela,
                    stStolpcevKernela
            );
        }

        return kernelKotTabela;
    }

    // iz [1, 2, 3, 4, 5, 6, 7, 8, 9] v:
    // { {1, 2, 3},
    //   {4, 5, 6},
    //   {7, 8, 9} }
    private static float[][] pretvori1DvKernel(float[] kernelKotTabela, int stVrsticKernela, int stStolpcevKernela) {
        float[][] kernel = new float[stVrsticKernela][stStolpcevKernela];

        for (int vrstica = 0; vrstica < stVrsticKernela; vrstica++) {
            System.arraycopy(
                    kernelKotTabela,
                    vrstica * stStolpcevKernela,
                    kernel[vrstica],
                    0,
                    stStolpcevKernela
            );
        }

        return kernel;
    }
}
