/******************************************************************************
 *cr
 *cr            (C) Copyright 2010 The Board of Trustees of the
 *cr                        University of Illinois
 *cr                         All Rights Reserved
 *cr
 ******************************************************************************/

#define BLOCK_SIZE 512

// Define your kernels in this file you may use more than one kernel if you
// need to

// INSERT KERNEL(S) HERE

__global__ void preScanKernel(float *out, float *in, unsigned size, float *sum)
{
	__shared__ float in_s[BLOCK_SIZE*2];
	int i, idx, offset;

	idx = 2*blockIdx.x*BLOCK_SIZE+threadIdx.x;

	if(idx < size)
		in_s[threadIdx.x] = in[idx];
	else
		in_s[threadIdx.x] = 0.0f;

	if(idx+BLOCK_SIZE < size)
		in_s[threadIdx.x+BLOCK_SIZE] = in[idx+BLOCK_SIZE];
	else
		in_s[threadIdx.x+BLOCK_SIZE] = 0.0f;

	for(i=BLOCK_SIZE, offset=1; i>0; i>>=1, offset<<=1) {
		__syncthreads();

		if(threadIdx.x < i)
			in_s[offset*(2*threadIdx.x+2)-1] +=
				in_s[offset*(2*threadIdx.x+1)-1];
	}

	if(threadIdx.x == 0) {
		if(sum != NULL) sum[blockIdx.x] = in_s[2*BLOCK_SIZE-1];
		in_s[2*BLOCK_SIZE-1] = 0.0f;
	}

	for(i=1, offset=BLOCK_SIZE ; i<=BLOCK_SIZE; i<<=1, offset>>=1) {
		__syncthreads();

		if(threadIdx.x < i) {
			float t = in_s[offset*(2*threadIdx.x+1)-1];
			in_s[offset*(2*threadIdx.x+1)-1] =
				in_s[offset*(2*threadIdx.x+2)-1];
			in_s[offset*(2*threadIdx.x+2)-1] += t;
		}
	}

	__syncthreads();

	out[idx] = in_s[threadIdx.x];
	out[idx+BLOCK_SIZE] = in_s[threadIdx.x+BLOCK_SIZE];
}


__global__ void addKernel(float *out, float *sum, unsigned size)
{
	int idx = 2*blockIdx.x*BLOCK_SIZE+threadIdx.x;
	if(idx < size)
		out[idx] += sum[blockIdx.x];
	if(idx+BLOCK_SIZE < size)
		out[idx+BLOCK_SIZE] += sum[blockIdx.x];
}

/******************************************************************************
Setup and invoke your kernel(s) in this function. You may also allocate more
GPU memory if you need to
*******************************************************************************/
void preScan(float *out, float *in, unsigned in_size)
{
    // INSERT CODE HERE
	float *sum;
	unsigned num_blocks;
	cudaError_t cuda_ret;
	dim3 dim_grid, dim_block;

	num_blocks = in_size/(BLOCK_SIZE*2);
	if(in_size%(BLOCK_SIZE*2) !=0) num_blocks++;

	dim_block.x = BLOCK_SIZE; dim_block.y = 1; dim_block.z = 1;
	dim_grid.x = num_blocks; dim_grid.y = 1; dim_grid.z = 1;

	if(num_blocks > 1) {
		cuda_ret = cudaMalloc((void**)&sum, num_blocks*sizeof(float));
		if(cuda_ret != cudaSuccess) FATAL("Unable to allocate device memory");

		preScanKernel<<<dim_grid, dim_block>>>(out, in, in_size, sum);
		preScan(sum, sum, num_blocks);
		addKernel<<<dim_grid, dim_block>>>(out, sum, in_size);

		cudaFree(sum);
	}
	else
		preScanKernel<<<dim_grid, dim_block>>>(out, in, in_size, NULL);
}

