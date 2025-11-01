/******************************************************************************
 *cr
 *cr            (C) Copyright 2010 The Board of Trustees of the
 *cr                        University of Illinois
 *cr                         All Rights Reserved
 *cr
 ******************************************************************************/

#include <stdio.h>

#define TILE_SIZE 16

__global__ void mysgemm(int m, int n, int k, const float *A, const float *B, float* C) {

    /********************************************************************
     *
     * Compute C = A x B
     *   where A is a (m x k) matrix
     *   where B is a (k x n) matrix
     *   where C is a (m x n) matrix
     *
     * Use shared memory for tiling
     *
     ********************************************************************/

    // INSERT KERNEL CODE HERE

    unsigned int row = blockIdx.y*blockDim.y + threadIdx.y;
    unsigned int col = blockIdx.x*blockDim.x + threadIdx.x;

    __shared__ float A_s[TILE_SIZE][TILE_SIZE];
    __shared__ float B_s[TILE_SIZE][TILE_SIZE];

    float sum = 0;
    for(unsigned int tileIdx = 0; tileIdx < (k - 1)/TILE_SIZE + 1; ++tileIdx) {
        if(row < m && + tileIdx*TILE_SIZE + threadIdx.x < k) {
            A_s[threadIdx.y][threadIdx.x] =
                A[row*k + tileIdx*TILE_SIZE + threadIdx.x];
        } else {
            A_s[threadIdx.y][threadIdx.x] = 0;
        }
        if(tileIdx*TILE_SIZE + threadIdx.y < k && col < n) {
            B_s[threadIdx.y][threadIdx.x] =
                B[(tileIdx*TILE_SIZE + threadIdx.y)*n + col];
        } else {
            B_s[threadIdx.y][threadIdx.x] = 0;
        }
        __syncthreads();
        if(row < m && col < n) {
            for(unsigned int i = 0; i < TILE_SIZE; ++i) {
                sum += A_s[threadIdx.y][i]*B_s[i][threadIdx.x];
            }
        }
        __syncthreads();
    }

    if(row < m && col < n) {
        C[row*n + col] = sum;
    }

}

void basicSgemm(char transa, char transb, int m, int n, int k, float alpha, const float *A, int lda, const float *B, int ldb, float beta, float *C, int ldc)
{
    if ((transa != 'N') && (transa != 'n')) {
	printf("unsupported value of 'transa'\n");
    	return;
    }

    if ((transb != 'N') && (transb != 'n')) {
	printf("unsupported value of 'transb'\n");
	return;
    }

    if ((alpha - 1.0f > 1e-10) || (alpha - 1.0f < -1e-10)) {
	printf("unsupported value of alpha\n");
	return;
    }

    if ((beta - 0.0f > 1e-10) || (beta - 0.0f < -1e-10)) {
	printf("unsupported value of beta\n");
	return;
    }

    // Initialize thread block and kernel grid dimensions ---------------------

    const unsigned int BLOCK_SIZE = TILE_SIZE;

    //INSERT CODE HERE
    dim3 gridDim((n - 1)/BLOCK_SIZE + 1, (m - 1)/BLOCK_SIZE + 1, 1);
    dim3 blockDim(BLOCK_SIZE, BLOCK_SIZE, 1);

    // Invoke CUDA kernel -----------------------------------------------------

    //INSERT CODE HERE

    mysgemm<<< gridDim, blockDim >>> (m, n, k, A, B, C);


}


