#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double calculateMSE(unsigned char *img1, unsigned char *img2, int width, int height, int channels) {
    double mse = 0.0;
    long size = (long)width * height * channels;
    for (long i = 0; i < size; i++) {
        double diff = (double)img1[i] - (double)img2[i];
        mse += diff * diff;
    }
    return mse / size;
}

double calculatePSNR(double mse) {
    if (mse == 0) return INFINITY;  // same image
    double max_pixel = 255.0;
    return 10.0 * log10((max_pixel * max_pixel) / mse);
}

int main() {
    int width = 1920, height = 1080, channels = 3;
    long size = (long)width * height * channels;

    // Allocate memory on the heap
    unsigned char *img1 = malloc(size);
    unsigned char *img2 = malloc(size);

    if (!img1 || !img2) {
        printf("Memory allocation failed!\n");
        return 1;
    }

    // Simulate some data
    for (long i = 0; i < size; i++) {
        img1[i] = 100;
        img2[i] = 100 + (i % 10 == 0 ? 2 : 0);
    }

    double mse = calculateMSE(img1, img2, width, height, channels);
    double psnr = calculatePSNR(mse);

    printf("MSE: %.6f\n", mse);
    printf("PSNR: %.2f dB\n", psnr);

    // Free allocated memory
    free(img1);
    free(img2);

    return 0;
}