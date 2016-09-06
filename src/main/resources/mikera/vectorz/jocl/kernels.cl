__kernel void 
addCopy(__global double *a, __global const double *b,__global const double *c)
{
	int gid = get_global_id(0);
	a[gid] = b[gid] + c[gid];
}

__kernel void 
add(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] += b[gid+boffset];
}

__kernel void 
mul(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] *= b[gid+boffset];
}

__kernel void 
div(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] /= b[gid+boffset];
}

__kernel void 
sub(__global double *a, __global const double *b, const int aoffset, const int boffset)
{
	int gid = get_global_id(0);
	a[gid+aoffset] -= b[gid+boffset];
}


__kernel void 
scaleAdd_scalar(__global double *a, const int aoffset, const double factor, const double c)
{
	int i = get_global_id(0) + aoffset;
	a[i] = a[i] * factor + c;
}

__kernel void 
addAt(__global double *a, const int offset, const double v)
{
	a[offset] += v;
}