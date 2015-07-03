package ph.edu.speed.orbit.bukidutillity.PixelCam;

public class pixelSorter {
    int[] dColor = new int[100];

    public pixelSorter(int[] rgb, int frameWidth, int frameHeight) {
        for (int k = 0; k < 100; k++) {
            for (int i = (frameHeight / 2) - 2; i < (frameHeight / 2) + 2; i++) {
                for (int j = (frameWidth / 2) - 1; j < (frameWidth / 2) + 2; j++) {
                    dColor[k] = rgb[i * frameWidth + j];

                }
            }
        }

    }
}
