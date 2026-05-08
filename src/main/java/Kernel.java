import java.util.ArrayList;

public class Kernel {
    /**
     * Funkcija ki vrne izbran kernel, s katerim bomo manipulirali sliko
     * @return izbran kernel
     */
    public static ArrayList<float[][]> izbiraKernelov(ArrayList<String> izbraniKerneli) {

        ArrayList<float[][]> seznamKernelov = new ArrayList<>();

        float[][] Blur = {
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25}
        };

        float[][] Sharpen = {
                { 0, -1,  0},
                {-1,  5, -1},
                { 0, -1,  0}
        };

        float[][] SobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        float[][] Gaussian = {
                {1f/16, 2f/16, 1f/16},
                {2f/16, 4f/16, 2f/16},
                {1f/16, 2f/16, 1f/16}
        };

        float[][] EdgeDetection = {
                { 0, -1,  0},
                {-1,  4, -1},
                { 0, -1,  0}
        };

        // pac kr neki sm dou tuki samo da pac je lahko vse v enem arraylistu
        

        for (String imeKernela : izbraniKerneli) {
            switch (imeKernela) {
                case "Blur" -> seznamKernelov.add(Blur);
                case "Sharpen" -> seznamKernelov.add(Sharpen);
                case "SobelX" -> seznamKernelov.add(SobelX);
                case "Gaussian" -> seznamKernelov.add(Gaussian);
                case "EdgeDetection" -> seznamKernelov.add(EdgeDetection);
    
                default -> {
                    System.out.println("Neznan kernel: " + imeKernela + " – uporabljen bo Blur.");
                    seznamKernelov.add(Blur);
                }
            }
        }

    return seznamKernelov;
}

    
}
