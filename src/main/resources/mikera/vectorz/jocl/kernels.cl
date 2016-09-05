__kernel void 
addCopy(__global double *a, __global const double *b,__global const double *c)
{
	int gid = get_global_id(0);
	a[gid] = b[gid] + c[gid];
}

__kernel void 
add(__global double *a, __global const double *b)
{
	int gid = get_global_id(0);
	a[gid] += b[gid];
}