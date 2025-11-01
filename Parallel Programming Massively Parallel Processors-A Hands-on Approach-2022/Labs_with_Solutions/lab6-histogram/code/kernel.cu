/******************************************************************************
 *cr
 *cr            (C) Copyright 2010 The Board of Trustees of the
 *cr                        University of Illinois
 *cr                         All Rights Reserved
 *cr
 ******************************************************************************/

// Define your kernels in this file you may use more than one kernel if you
// need to

// INSERT KERNEL(S) HERE

__global__ void histogram_kernel(unsigned int* input, unsigned int* bins,
    unsigned int num_elements, unsigned int num_bins) {

    unsigned int tid = blockIdx.x*blockDim.x + threadIdx.x;

    // Privatized bins
    extern __shared__ unsigned int bins_s[];
    for(unsigned int binIdx = threadIdx.x; binIdx < num_bins; binIdx +=blockDim.x) {
        bins_s[binIdx] = 0u;
    }
    __syncthreads();

    // Histogram
    for(unsigned int i = tid; i < num_elements; i += blockDim.x*gridDim.x) {
        atomicAdd(&(bins_s[input[i]]), 1);
    }
    __syncthreads();

    // Commit to global memory
    for(unsigned int binIdx = threadIdx.x; binIdx < num_bins; binIdx += blockDim.x) {
        atomicAdd(&(bins[binIdx]), bins_s[binIdx]);
    }

}

__global__ void convert_kernel(unsigned int *bins32, uint8_t *bins8,
    unsigned int num_bins) {

    unsigned int tid = blockIdx.x*blockDim.x + threadIdx.x;

    if(tid < num_bins) {
        unsigned int count = bins32[tid];
        if (count < 256) {
            bins8[tid] = (uint8_t) count;
        } else {
            bins8[tid] = 255u;
        }
    }


}

/******************************************************************************
Setup and invoke your kernel(s) in this function. You may also allocate more
GPU memory if you need to
*******************************************************************************/
void histogram(unsigned int* input, uint8_t* bins, unsigned int num_elements,
        unsigned int num_bins) {

    // INSERT CODE HERE

    // Create 32 bit bins
    unsigned int *bins32;
    cudaMalloc((void**)&bins32, num_bins * sizeof(unsigned int));
    cudaMemset(bins32, 0, num_bins * sizeof(unsigned int));

    // Launch histogram kernel using 32-bit bins
    dim3 dim_grid, dim_block;
    dim_block.x = 512; dim_block.y = dim_block.z = 1;
    dim_grid.x = 30; dim_grid.y = dim_grid.z = 1;
    histogram_kernel<<<dim_grid, dim_block, num_bins*sizeof(unsigned int)>>>
        (input, bins32, num_elements, num_bins);

    // Convert 32-bit bins into 8-bit bins
    dim_block.x = 512;
    dim_grid.x = (num_bins - 1)/dim_block.x + 1;
    convert_kernel<<<dim_grid, dim_block>>>(bins32, bins, num_bins);

    // Free allocated device memory
    cudaFree(bins32);

}


