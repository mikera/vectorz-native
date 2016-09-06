__kernel void 
op_abs(__global double *a)
{
	int gid = get_global_id(0);
	a[gid] = fabs(a[gid]);
}

