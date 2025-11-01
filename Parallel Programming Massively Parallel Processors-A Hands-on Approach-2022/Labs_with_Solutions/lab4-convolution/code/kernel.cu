/******************************************************************************
 *cr
 *cr            (C) Copyright 2010 The Board of Trustees of the
 *cr                        University of Illinois
 *cr                         All Rights Reserved
 *cr
 ******************************************************************************/

__constant__ float M_c[FILTER_SIZE][FILTER_SIZE];

__global__ void convolution(Matrix N, Matrix P)
{
	/********************************************************************
	Determine input and output indexes of each thread
	Load a tile of the input image to shared memory
	Apply the filter on the input image tile
	Write the compute values to the output image at the correct indexes
	********************************************************************/

    //INSERT KERNEL CODE HERE

	__shared__ float N_s[BLOCK_SIZE][BLOCK_SIZE];

	int outRow = blockIdx.y*TILE_SIZE+threadIdx.y;
	int outCol = blockIdx.x*TILE_SIZE+threadIdx.x;

	int inRow = outRow-2;
	int inCol = outCol-2;

	float output = 0.0f;

	if(inRow >= 0 && inRow < N.height && inCol >= 0 && inCol < N.width)
		N_s[threadIdx.y][threadIdx.x] = N.elements[inRow*N.width+inCol];
	else
		N_s[threadIdx.y][threadIdx.x] = 0.0f;

	__syncthreads();

	if(threadIdx.x < TILE_SIZE && threadIdx.y < TILE_SIZE) {
		for(int i=0; i<FILTER_SIZE; i++)
			for(int j=0; j<FILTER_SIZE; j++)
				output += M_c[i][j]*N_s[i+threadIdx.y][j+threadIdx.x];

		if(outRow < P.height && outCol < P.width)
			P.elements[outRow*P.width+outCol] = output;
	}

}
