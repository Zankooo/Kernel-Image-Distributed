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


        int size = MPI.COMM_WORLD.Size();
        int numWorkers = Math.max(1, size - 1); // Vsaj 1 delavec, da ne pride do / 0

        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();

        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            System.out.println("\n🖼️ OBDELAVA SLIKE " + (i + 1) + " od " + slike.size());

            for (int j = 0; j < kerneli.size(); j++) {
                float[][] kernel = kerneli.get(j);
                System.out.println("   ⚙️ Uporabljam kernel: " + imenaKernelov.get(j));
                
                trenutnaSlika = konvolucijaRGBDistributed(trenutnaSlika, kernel, numWorkers);
            }

            if (cbMirror.isSelected()) {
                trenutnaSlika = mirrorFunkcija(trenutnaSlika);
                System.out.println("   🔄 Operacija Mirror končana.");
            }

            rezultatiSlik.add(trenutnaSlika);
        }
        long koncaniCas = System.currentTimeMillis();
        double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;

        System.out.println();
        System.out.println("⏱️ Čas IZRAČUNA " + kolikoCasaJeTrajaloSek + " sekund");

        return rezultatiSlik;
    }

    /**
     * Master logika za razpošiljanje dela.
     * Ime usklajeno s konvolucijaRGBParallel
     */
    private static BufferedImage konvolucijaRGBDistributed(BufferedImage slika, float[][] kernel, int numWorkers) throws MPIException {
        int sirina = slika.getWidth();
        int visina = slika.getHeight();

        int[] piksli = slika.getRGB(0, 0, sirina, visina, null, 0, sirina);
        int rowsPerWorker = visina / numWorkers;
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        int[] procesiraniPiksli = new int[piksli.length];

        for (int i = 1; i <= numWorkers; i++) {
            int startRow = (i - 1) * rowsPerWorker;
            int endRow = (i == numWorkers) ? visina : i * rowsPerWorker;
            int numRows = endRow - startRow;

            System.out.println("   📤 Master: Pošiljam delo delavcu " + i + " (" + numRows + " vrstic)");

            MPI.COMM_WORLD.Send(new int[]{CMD_PROCESS}, 0, 1, MPI.INT, i, TAG_COMMAND);

            int[] metadata = {sirina, visina, startRow, numRows, kernelHeight, kernelWidth};
            MPI.COMM_WORLD.Send(metadata, 0, metadata.length, MPI.INT, i, TAG_METADATA);

            float[] flatKernel = flattenKernel(kernel);
            MPI.COMM_WORLD.Send(flatKernel, 0, flatKernel.length, MPI.FLOAT, i, TAG_KERNEL);

            MPI.COMM_WORLD.Send(piksli, 0, piksli.length, MPI.INT, i, TAG_IMAGE_DATA);
        }

        for (int i = 1; i <= numWorkers; i++) {
            int startRow = (i - 1) * rowsPerWorker;
            int endRow = (i == numWorkers) ? visina : i * rowsPerWorker;
            int numRows = endRow - startRow;

            int[] receivedPixels = new int[sirina * numRows];
            MPI.COMM_WORLD.Recv(receivedPixels, 0, receivedPixels.length, MPI.INT, i, TAG_RESULT);
            
            System.out.println("   📥 Master: Prejel rezultat od delavca " + i);
            System.arraycopy(receivedPixels, 0, procesiraniPiksli, startRow * sirina, receivedPixels.length);
        }

        BufferedImage novaSlika = new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_ARGB);
        novaSlika.setRGB(0, 0, sirina, visina, procesiraniPiksli, 0, sirina);
        return novaSlika;
    }

    public static void workerProcess(int rank) throws MPIException {
        while (true) {
            int[] command = new int[1];
            MPI.COMM_WORLD.Recv(command, 0, 1, MPI.INT, 0, TAG_COMMAND);

            if (command[0] == CMD_TERMINATE) {
                break;
            }

            if (command[0] == CMD_PROCESS) {
                int[] metadata = new int[6];
                MPI.COMM_WORLD.Recv(metadata, 0, 6, MPI.INT, 0, TAG_METADATA);
                int sirina = metadata[0];
                int visina = metadata[1];
                int startRow = metadata[2];
                int numRows = metadata[3];
                int kH = metadata[4];
                int kW = metadata[5];

                System.out.println("   👷 Worker " + rank + ": Prejel nalogo (" + numRows + " vrstic)");

                float[] flatKernel = new float[kH * kW];
                MPI.COMM_WORLD.Recv(flatKernel, 0, flatKernel.length, MPI.FLOAT, 0, TAG_KERNEL);
                float[][] kernel = unflattenKernel(flatKernel, kH, kW);

                int[] piksli = new int[sirina * visina];
                MPI.COMM_WORLD.Recv(piksli, 0, piksli.length, MPI.INT, 0, TAG_IMAGE_DATA);

                System.out.println("   ⚙️ Worker " + rank + ": Računam...");
                int[] result = processChunk(piksli, sirina, visina, startRow, numRows, kernel);

                System.out.println("   📤 Worker " + rank + ": Pošiljam rezultat.");
                MPI.COMM_WORLD.Send(result, 0, result.length, MPI.INT, 0, TAG_RESULT);
            }
        }
    }

    private static int[] processChunk(int[] piksli, int sirina, int visina, int startRow, int numRows, float[][] kernel) {
        int[] out = new int[sirina * numRows];
        int kRadiusY = kernel.length / 2;
        int kRadiusX = kernel[0].length / 2;

        for (int y = 0; y < numRows; y++) {
            int actualY = startRow + y;
            for (int x = 0; x < sirina; x++) {
                float rSum = 0, gSum = 0, bSum = 0;
                
                int centerPixel = piksli[actualY * sirina + x];
                int a = (centerPixel >>> 24) & 0xFF;

                for (int ky = -kRadiusY; ky <= kRadiusY; ky++) {
                    for (int kx = -kRadiusX; kx <= kRadiusX; kx++) {
                        int px = clamp(x + kx, 0, sirina - 1);
                        int py = clamp(actualY + ky, 0, visina - 1);

                        int rgb = piksli[py * sirina + px];
                        float weight = kernel[ky + kRadiusY][kx + kRadiusX];

                        rSum += ((rgb >>> 16) & 0xFF) * weight;
                        gSum += ((rgb >>> 8) & 0xFF) * weight;
                        bSum += (rgb & 0xFF) * weight;
                    }
                }

                int r = clamp(Math.round(rSum), 0, 255);
                int g = clamp(Math.round(gSum), 0, 255);
                int b = clamp(Math.round(bSum), 0, 255);

                out[y * sirina + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return out;
    }

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

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static float[] flattenKernel(float[][] kernel) {
        int rows = kernel.length, cols = kernel[0].length;
        float[] flat = new float[rows * cols];
        for (int i = 0; i < rows; i++) System.arraycopy(kernel[i], 0, flat, i * cols, cols);
        return flat;
    }

    private static float[][] unflattenKernel(float[] flat, int rows, int cols) {
        float[][] kernel = new float[rows][cols];
        for (int i = 0; i < rows; i++) System.arraycopy(flat, i * cols, kernel[i], 0, cols);
        return kernel;
    }
}
