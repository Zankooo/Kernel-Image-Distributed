import mpi.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            // vsi procesi zacnejo tukaj. 
            // mpjrun.sh -np 4 -cp target/classes:lib/mpj.jar Main
            // ta ukaz pomeni da zacenemo 4 procese istega programa
            // inicializira se MPI okolje
            MPI.Init(args);
            // vsak proces dobi svoj rank
            int rank = MPI.COMM_WORLD.Rank();
            // prvi proces oziroma master ima GUI
            if (rank == 0) {
                Gui.ustvariGui();
                // Počakamo, da se GUI dejansko naloži in nato čakamo na njegovo zaprtje
                try {
                    Thread.sleep(1000); 
                    while (java.awt.Frame.getFrames().length > 0 && java.awt.Frame.getFrames()[0].isVisible()) {
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } 
            // ostali so pa workerji.
            else {
                KonvolucijaMPI.workerProcess(rank);
            }

            // Rank 0 needs to tell workers to terminate when GUI closes
            // However, in standard Java GUI apps, we might need a shutdown hook
            // or handle it when the window is closed.
            if (rank == 0) {
                // Simplified termination: when Gui.ustvariGui() returns, 
                // we tell workers to stop.
                int size = MPI.COMM_WORLD.Size();
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Send(new int[]{KonvolucijaMPI.CMD_TERMINATE}, 0, 1, MPI.INT, i, KonvolucijaMPI.TAG_COMMAND);
                }
            }

            MPI.Finalize();
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }
}